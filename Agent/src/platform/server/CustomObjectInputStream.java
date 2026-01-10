package platform.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Version personnalisée de ObjectInputStream.
 * Son seul but est de surcharger resolveClass() pour utiliser notre CustomClassLoader
 * lors de la désérialisation de l'agent.
 */
public class CustomObjectInputStream extends ObjectInputStream {

    /** Le classLoader qui connait les classes de l'agent */
    private final ClassLoader customLoader;

    public CustomObjectInputStream(InputStream in, ClassLoader customLoader) throws IOException {
        super(in);
        this.customLoader = customLoader;
    }

    /**
     * Méthode appelée juste avant de charger une classe depuis le flux d'objets.
     * Par défaut, Java utilise le "latest user defined Loader".
     * Nous forçons ici l'utilisation de notre loader.
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            // On essaie de charger la classe avec notre CustomClassLoader
            return Class.forName(desc.getName(), false, customLoader);
        } catch (ClassNotFoundException e) {
            // Si ça échoue, on laisse le comportement par défaut (pour String, etc.)
            return super.resolveClass(desc);
        }
    }
}