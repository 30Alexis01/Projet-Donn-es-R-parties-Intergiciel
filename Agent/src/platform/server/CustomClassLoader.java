package platform.server;

import java.util.Map;

/**
 * Chargeur de classe personnalisé pour les Agents Mobiles.
 * Permet d'isoler chaque agent dans son propre espace de noms et de charger
 * des classes depuis les octets reçus (au lieu du disque local).
 */
public class CustomClassLoader extends ClassLoader {

    /**
     * Cache contenant le bytecode des classes reçues.
     * Clé : Nom de la classe (ex: "agents.PingPongAgent")
     * Valeur : Le contenu du fichier .class en tableau d'octets.
     */
    private final Map<String, byte[]> classCache;

    /**
     * Constructeur.
     * @param parent Le ClassLoader parent (généralement le système, pour charger String, System, etc.)
     * @param classData La map extraite du JAR contenant le bytecode de l'agent.
     */
    public CustomClassLoader(ClassLoader parent, Map<String, byte[]> classData) {
        super(parent);
        this.classCache = classData;
    }

    /**
     * Méthode appelée automatiquement par la JVM quand elle cherche une classe
     * qu'elle ne connait pas encore.
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 1. On cherche le bytecode dans notre cache (reçu du réseau)
        byte[] b = classCache.get(name);

        if (b == null) {
            // Si on ne l'a pas, c'est une classe inconnue -> Erreur
            throw new ClassNotFoundException(name);
        }

        // 2. On transforme les octets en "vraie" classe Java
        // defineClass est une méthode native de ClassLoader qui fait la magie.
        return defineClass(name, b, 0, b.length);
    }
}