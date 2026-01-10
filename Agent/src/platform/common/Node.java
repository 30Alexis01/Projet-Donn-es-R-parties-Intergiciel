package platform.common;

import java.io.Serializable;

/**
 * Représente un nœud du réseau (une machine + un port) où un AgentServer écoute.
 * On le rend Serializable car on pourra le stocker dans l'agent (origin, destinations, etc.).
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nom d'hôte ou IP (ex: "localhost", "192.168.1.20") */
    public final String host;

    /** Port TCP (ex: 2001) */
    public final int port;

    /**
     * Constructeur.
     */
    public Node(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Affichage pratique pour debug.
     */
    @Override
    public String toString() {
        return host + ":" + port;
    }

    /**
     * Pour comparer deux Node (utile si tu fais place == nodeA etc.)
     * Ici on compare host + port.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return this.port == other.port && this.host.equals(other.host);
    }

    @Override
    public int hashCode() {
        // Simple: combinaison host/port
        return 31 * host.hashCode() + port;
    }
}
