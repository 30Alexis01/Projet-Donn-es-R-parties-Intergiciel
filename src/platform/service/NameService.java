package platform.service;

public interface NameService {
    int getCount(int year, String name);
    
    // NOUVELLE MÉTHODE
    int getCountByLine(int lineNumber);
}