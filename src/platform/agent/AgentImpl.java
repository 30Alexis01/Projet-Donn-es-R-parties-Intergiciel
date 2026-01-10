package platform.agent;

import java.util.Hashtable;
import java.io.IOException;

import platform.common.MoveException;
import platform.common.Node;
import platform.transport.AgentSender;
import platform.transport.SerializerUtils;

public abstract class AgentImpl implements Agent {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected Node origin;
    protected transient Hashtable<String, Object> nameServer;
    
    // Le code (JAR) pour la migration
    protected transient byte[] jarBytes;

    public void setJarBytes(byte[] jarBytes) {
        this.jarBytes = jarBytes;
    }

    @Override
    public void init(String name, Node origin) {
        this.name = name;
        this.origin = origin;
    }

    @Override
    public void setNameServer(Hashtable<String, Object> ns) {
        this.nameServer = ns;
    }

    @Override
    public Hashtable<String, Object> getNameServer() {
        return nameServer;
    }

    @Override
    public void back() throws MoveException {
        if (origin == null) throw new MoveException("origin is null");
        move(origin);
    }

    @Override
    public void move(Node target) throws MoveException {
        if (jarBytes == null) {
            throw new MoveException("Impossible de migrer : je n'ai pas mon code (jarBytes) !");
        }
        try {
            System.out.println("Migration vers " + target + "...");
            byte[] dataBytes = SerializerUtils.serialize(this);
            AgentSender.send(target, this.jarBytes, dataBytes, this.getClass().getName());
        } catch (IOException e) {
            throw new MoveException("Echec de la migration vers " + target, e);
        }
    }

    @Override
    public abstract void main() throws MoveException;

    // --- CES GETTERS SONT OBLIGATOIRES POUR PINGPONGAGENT ---
    public String getName() {
        return name;
    }

    public Node getOrigin() {
        return origin;
    }
}