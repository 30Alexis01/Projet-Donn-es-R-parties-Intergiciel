package platform.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

//ObjectInputStream personnalisé.Permet de désérialiser un agent en utilisant un ClassLoader spécifique.
public class CustomObjectInputStream extends ObjectInputStream {

    // ClassLoader qui connaît les classes de l'agent
    private final ClassLoader customLoader;

    // Crée un flux de désérialisation avec un ClassLoader personnalisé
    public CustomObjectInputStream(InputStream in, ClassLoader customLoader) throws IOException {
        super(in);
        this.customLoader = customLoader;
    }

    // Charge les classes en utilisant le CustomClassLoader lors de la désérialisation
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {

        try {
            // Tentative de chargement avec le ClassLoader de l'agent
            return Class.forName(desc.getName(), false, customLoader);
        } catch (ClassNotFoundException e) {
            // Sinon, on utilise le mécanisme standard de Java
            return super.resolveClass(desc);
        }
    }
}
