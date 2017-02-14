package es.upv.master.audiolibros.singletons;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import es.upv.master.audiolibros.AdaptadorLibros;
import es.upv.master.audiolibros.AdaptadorLibrosFiltro;
import es.upv.master.audiolibros.Libro;

/**
 * Created by padres on 30/01/2017.
 */
public class LibrosSingleton {
    private static LibrosSingleton instance;
    private Context context;
    private AdaptadorLibros adaptador ;


    private void inicializa() {
        adaptador = new AdaptadorLibrosFiltro(context,  FirebaseDBSingleton.getInstance().getBooksReference());
    }

    public AdaptadorLibros getAdaptador() {
        return adaptador;
    }

    public void setAdaptador(AdaptadorLibros adaptador) {
        this.adaptador = adaptador;
    }

    public static LibrosSingleton getInstance(Context context) {
        if (instance == null)
        {
            instance = new LibrosSingleton(context);
            instance.inicializa();
        }
        return instance;
    }

    private LibrosSingleton(Context context) {
        this.context = context;
    }
}
