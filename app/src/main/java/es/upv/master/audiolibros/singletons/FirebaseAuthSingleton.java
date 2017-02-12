package es.upv.master.audiolibros.singletons;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by padres on 06/02/2017.
 */
public class FirebaseAuthSingleton {

    private static FirebaseAuthSingleton instance;
    private static FirebaseAuth auth;

    private FirebaseAuthSingleton() {
        auth = FirebaseAuth.getInstance();
    }


    public static FirebaseAuthSingleton getInstance() {
        if (instance == null)
        {
          instance = new FirebaseAuthSingleton();
        }
        return instance;
    }

    public FirebaseAuth getAuth() { return auth; }
}
