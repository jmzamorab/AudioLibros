package es.upv.master.audiolibros.singletons;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by padres on 06/02/2017.
 */
public class FirebaseDBSingleton {

    private static FirebaseDBSingleton instance;
    private static FirebaseDatabase dbFirebase;
    private final static String BOOKS_CHILD = "libros";
    private final static String USERS_CHILD = "usuarios";
    private DatabaseReference usersReference;
    private DatabaseReference booksReference;

    private FirebaseDBSingleton() {

        dbFirebase = FirebaseDatabase.getInstance();
        dbFirebase.setPersistenceEnabled(true);
        booksReference = dbFirebase.getReference().child(BOOKS_CHILD);
        usersReference = dbFirebase.getReference().child(USERS_CHILD);
    }


    public static FirebaseDBSingleton getInstance() {
        if (instance == null) {
            instance = new FirebaseDBSingleton();
        }
        return instance;
    }

    public FirebaseDatabase getDB() {
        return dbFirebase;
    }

    public DatabaseReference getUsersReference() {
        return usersReference;
    }

    public DatabaseReference getBooksReference() {
        return booksReference;
    }
}

