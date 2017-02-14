package es.upv.master.audiolibros;

/**
 * Created by padres on 30/01/2017.
 */

public interface LibroStorage {
    boolean hasLastBook();
    String getLastBook();
    void setLastBook(String  key);
    void saveLastBook(String key);
}
