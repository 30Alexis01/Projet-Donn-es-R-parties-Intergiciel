package platform.server;

import java.util.Map;

//Chargeur de classe personnalisé pour les Agents Mobiles.Permet d'isoler chaque agent dans son propre espace de noms et de charger des classes depuis les octets reçus (au lieu du disque local).
 
public class CustomClassLoader extends ClassLoader {

    //nom de la classe, .class
    private final Map<String, byte[]> classCache;


    public CustomClassLoader(ClassLoader parent, Map<String, byte[]> classData) {
        super(parent);
        this.classCache = classData;
    }

    
     //Méthode appelée automatiquement par la JVM quand elle cherche une classe qu'elle ne connait pas encore.

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // On cherche le bytecode dans notre cache (reçu du réseau)
        byte[] b = classCache.get(name);

        if (b == null) {
            throw new ClassNotFoundException(name);
        }

        // On transforme les octets en classe Java
        return defineClass(name, b, 0, b.length);
    }
}