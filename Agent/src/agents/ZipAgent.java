package agents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.service.FileService;
import apps.client.ClientMain; // Import pour accéder à la variable statique

public class ZipAgent extends AgentImpl {

    private List<String> fileNames;
    private byte[] compressedData;
    private boolean hasFinishedWork = false;

    public ZipAgent(List<String> files) {
        this.fileNames = files;
    }

    @Override
    public void main() throws MoveException {
        if (!hasFinishedWork) {
            // === CAS 1 : SUR LE SERVEUR DISTANT ===
            try {
                FileService fs = (FileService) getNameServer().get("FileService");
                if (fs == null) {
                    System.err.println("Erreur: FileService absent sur le serveur !");
                    return; 
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    for (String fileName : fileNames) {
                        try {
                            byte[] content = fs.getFileContent(fileName);
                            ZipEntry entry = new ZipEntry(fileName);
                            zos.putNextEntry(entry);
                            zos.write(content);
                            zos.closeEntry();
                        } catch (Exception e) {
                            // On ignore juste le fichier manquant
                        }
                    }
                }
                
                // On stocke le ZIP dans l'objet (pour le voyage retour)
                this.compressedData = baos.toByteArray();
                this.hasFinishedWork = true;
                
                back(); // Retour à l'envoyeur

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // === CAS 2 : RETOUR CHEZ LE CLIENT ===
            
            // 1. Livraison du colis
            // On dépose les données dans la variable statique du Client
            ClientMain.receivedData = this.compressedData;

            // 2. Notification
            // On réveille le ClientMain qui dort sur le lock
            synchronized (ClientMain.lock) {
                ClientMain.lock.notify();
            }
        }
    }

    public byte[] getCompressedData() {
        return compressedData;
    }
}