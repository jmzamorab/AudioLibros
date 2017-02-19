package es.upv.master.audiolibros.singletons;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.upv.master.audiolibros.AdaptadorLibros;
import es.upv.master.audiolibros.AdaptadorLibrosFiltro;

import static android.R.attr.key;

/**
 * Created by padres on 30/01/2017.
 */
public class LecturasSingleton implements ChildEventListener {
    private static LecturasSingleton instance;
    private String UIDUser;

    private DatabaseReference lecturasReferences;
    private DatabaseReference usuarioLecturas;

    final List<String> librosLeidos;
    DatabaseReference libroLectura;


    public static LecturasSingleton getInstance() {
        if (instance == null)
        {
            instance = new LecturasSingleton();
        }
        return instance;
    }

    private LecturasSingleton()
    {
        librosLeidos = new ArrayList <String>();//Collections.synchronizedSet(new LinkedHashSet<String>(10));
        UIDUser = FirebaseAuthSingleton.getInstance().getAuth().getCurrentUser().getUid();
        lecturasReferences =FirebaseDBSingleton.getInstance().getLecturasReference();
        usuarioLecturas = lecturasReferences.child(UIDUser);

        lecturasReferences.addChildEventListener(this);
    }

    public void activeListenerLecturas(){
        lecturasReferences.addChildEventListener(this);
           //FirebaseDatabase.getInstance().goOnline();
    }

    public void desactivaEscuchadorLecturas() {
        lecturasReferences.removeEventListener(this);
    //    FirebaseDatabase.getInstance().goOffline();
    }


    public void libroLeidoPorUsuario(String libroId, boolean leido){
        Log.d("TRAZA", "LecturasSingleton quiero actualizar BBDD ");
         if (leido)
         {
             Log.d("TRAZA", "leido SI y libroID = " + libroId);
             DatabaseReference newBookId = lecturasReferences.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
//             newBookId.setValue(leido);

             newBookId.child(libroId).setValue(leido);

             librosLeidos.add(libroId);
             Log.d("TRAZA - BBDD", "Añado a ArrayList interno el libroId " + libroId );
         }
        else
         {
             DatabaseReference miRef = lecturasReferences.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef();
             miRef.child(libroId).removeValue();
         }
    }

    public boolean libroLeido(String key){
        Boolean lRes;
        Log.d("TRAZA", "LecturasSingleton, veo si " + key + " está en librosLeidos " + librosLeidos.contains(key));
        if (librosLeidos.size() > 0 )
            lRes = librosLeidos.contains(key);
        else{
            lRes = false;
        }
        return lRes;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("TRAZA - BBDD", "LecturasSingleton - onChildAdded llega dataSnapshot.getKey() = " +dataSnapshot.getKey() + " y String = " + s);
        Log.d("TRAZA - BBDD", "LecturasSingleton - onChildAdded dataSnapshot.getValue() = " + dataSnapshot.getValue());
        Log.d("TRAZA - BBDD", "LecturasSingleton - onChildAdded dataSnapshot.getChildrenCount()= " + dataSnapshot.getChildrenCount());
        librosLeidos.add(dataSnapshot.getKey());


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d("TRAZA - BBDD", "LecturasSingleton - onChildChanged llega dataSnapshot.getKey() = " +dataSnapshot.getKey() + " y String = " + s);
       }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d("TRAZA - BBDD", "LecturasSingleton - onChildRemoved llega SOLO dataSnapshot.getKey() = " +dataSnapshot.getKey() );
        if (librosLeidos.contains(dataSnapshot.getKey()))
        {
            librosLeidos.remove(librosLeidos.indexOf(dataSnapshot.getKey()));
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
       Log.w("TRAZA", "Fallo al leer valores de BBDD", databaseError.toException());
    }
}
