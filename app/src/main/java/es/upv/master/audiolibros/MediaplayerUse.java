package es.upv.master.audiolibros;

/**
 * Created by padres on 30/01/2017.
 */

public interface MediaplayerUse {
    boolean initialize();
    int stop();
    void setLastBook(int lastBook);
    void saveLastBook(int lastBook);
}
