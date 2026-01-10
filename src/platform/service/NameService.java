package platform.service;

public interface NameService {
    /**
     * Retourne le nombre de naissances pour un prénom et une année donnés.
     * (Additionne les garçons et les filles si le prénom est mixte).
     */
    int getCount(int year, String name);
}