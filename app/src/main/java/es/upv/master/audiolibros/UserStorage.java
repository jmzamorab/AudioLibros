package es.upv.master.audiolibros;

/**
 * Created by padres on 30/01/2017.
 */

public interface UserStorage {
    boolean hasEMail();
    String getProvider();
    void setProvider(String provider);
    String getName();
    void setName(String name);
    void setEMail(String eMail);
    String getEmail();
    void remove(String key);
}
