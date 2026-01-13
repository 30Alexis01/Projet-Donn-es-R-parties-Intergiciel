package platform.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

import platform.agent.Agent;
import platform.common.MigrationHeader;
import platform.common.ProtocolException;
import platform.transport.JarUtils;

public class AgentServer {

    private final String host;
    private final int port;
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private volatile boolean running;

    // Service de nommage local
    private final Hashtable<String, Object> nameServer;

    public AgentServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.nameServer = new Hashtable<>();
    }

    public Hashtable<String, Object> getNameServer() {
        return nameServer;
    }

    

    public void start() throws IOException {
        if (running) {
            throw new IllegalStateException("Server already started");
        }
        serverSocket = new ServerSocket(port);
        running = true;

        acceptThread = new Thread(this::acceptLoop);
        acceptThread.start();

        System.out.println("AgentServer started on " + host + ":" + port);
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
        System.out.println("AgentServer stopped");
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                handleConnection(socket);
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Gestion Connexion (Réseau) 

    // lit le header, puis le JAR et les données, et délègue le traitement à processAgent.
    private void handleConnection(Socket socket) {
        try (
            Socket s = socket;
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()))
        ) {
            // Header
            MigrationHeader header = readHeader(in);
           
            

            //  Jar
            byte[] jarBytes = readFully(in, header.jarSize);
            
            //Data
            byte[] dataBytes = readFully(in, header.dataSize);
          

            processAgent(header, jarBytes, dataBytes);

        } catch (Exception e) {
            System.err.println("Erreur durant la réception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    // Lit l’en-tête du message de migration, vérifie que le protocole est valide (magic) et récupère les tailles du JAR et des données.
    private void processAgent(MigrationHeader header, byte[] jarBytes, byte[] dataBytes) {
        try {
           
            // Extraction des classes
            Map<String, byte[]> classes = JarUtils.extractClasses(jarBytes);

            // ClassLoader dédié
            CustomClassLoader loader = new CustomClassLoader(ClassLoader.getSystemClassLoader(), classes);

            //  Désérialisation avec le bon ClassLoader
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(dataBytes);
            CustomObjectInputStream ois = new CustomObjectInputStream(bis, loader);
            
            Agent agent = (Agent) ois.readObject();
            ois.close();

            //  Injection dépendances
            agent.setNameServer(this.nameServer);
            
            // On redonne son JAR à l'agent pour qu'il puisse repartir
            if (agent instanceof platform.agent.AgentImpl) {
                ((platform.agent.AgentImpl) agent).setJarBytes(jarBytes);
            }

            new Thread(() -> {
                try {
                    agent.main();
                } catch (Exception e) {
                    System.err.println("Erreur dans l'agent " + header.mainClassName);
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("Erreur au déploiement de l'agent :");
            e.printStackTrace();
        }
    }

    //Reconstruit l’agent reçu :
    // charge les classes depuis le JAR,
    // désérialise l’agent avec le bon ClassLoader,
    // injecte les services locaux et lance son exécution.
    private MigrationHeader readHeader(DataInputStream in) throws IOException, ProtocolException {
        int magic = in.readInt();
        int jarSize = in.readInt();
        int dataSize = in.readInt();
        String mainClassName = in.readUTF();

        if (magic != platform.transport.AgentSender.PROTOCOL_MAGIC) {
            throw new ProtocolException("Bad magic: " + magic);
        }
        
        return new MigrationHeader(magic, jarSize, dataSize, mainClassName);
    }

    private byte[] readFully(DataInputStream in, int length) throws IOException {
        byte[] buffer = new byte[length];
        in.readFully(buffer);
        return buffer;
    }
}