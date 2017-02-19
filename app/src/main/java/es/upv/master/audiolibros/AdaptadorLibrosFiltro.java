package es.upv.master.audiolibros;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import es.upv.master.audiolibros.singletons.FirebaseAuthSingleton;
import es.upv.master.audiolibros.singletons.LecturasSingleton;
import es.upv.master.audiolibros.singletons.LibrosSingleton;

import static android.R.attr.id;


public class AdaptadorLibrosFiltro extends AdaptadorLibros implements Observer{
    private List<Integer> indiceFiltro; // Índice en vectorSinFiltro de
    // Cada elemento de vectorLibros
    private String busqueda = ""; // Búsqueda sobre autor o título
    private String genero = ""; // Género seleccionado
    private boolean novedad = false; // Si queremos ver solo novedades
    private boolean leido = false; // Si queremos ver solo leidos
    private int librosUltimoFiltro;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private LecturasSingleton lecturasSingleton;

    public AdaptadorLibrosFiltro(Context contexto, DatabaseReference reference) {
        super(contexto, reference);
        auth = FirebaseAuthSingleton.getInstance().getAuth();
        currentUser = auth.getCurrentUser();
        lecturasSingleton = LecturasSingleton.getInstance();
        recalculaFiltro();
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda.toLowerCase();
        recalculaFiltro();
    }

    public void setGenero(String genero) {
        this.genero = genero;
        recalculaFiltro();
    }

    public void setNovedad(boolean novedad) {
        this.novedad = novedad;
        recalculaFiltro();
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
        recalculaFiltro();
    }

    public void recalculaFiltro() {
        indiceFiltro = new ArrayList<Integer>();
        librosUltimoFiltro = super.getItemCount();

        for (int i = 0; i < librosUltimoFiltro; i++) {
            Libro libroItem = super.getItem(i);
            //Log.d("TRAZA", "recalculaFiltro item " + i + " getItemKey(i) " + getItemKey(i) + " ");
         //   Log.d("TRAZA", "Reviso Leido; leido = " + leido + " libroLeido (lecturas)  " + lecturasSingleton.libroLeido(getItemKey(i)));
            Log.d("TRAZA", "y finalmente libro leidoPormi = " + libroItem.leidoPorMi(currentUser.getUid()));
            if ((libroItem.getTitulo().toLowerCase().contains(busqueda) || libroItem.getAutor().toLowerCase().contains(busqueda))
                    && (libroItem.getGenero().startsWith(genero))
                    && (!novedad || (novedad && libroItem.getNovedad()))
                    && (!leido || (leido && lecturasSingleton.libroLeido(getItemKey(i))))){
                   // && (!leido || (leido && libroItem.leidoPorMi(currentUser.getUid())))){
                indiceFiltro.add(i);
        }

    }
}

    public Libro getItem(int posicion) {
        if (librosUltimoFiltro != super.getItemCount()) {
            recalculaFiltro();
        }
        return super.getItem(indiceFiltro.get(posicion));
    }

    public long getItemId(int posicion) {
        return indiceFiltro.get(posicion);
    }

    public void borrar(int posicion) {
        DatabaseReference referencia=getRef(indiceFiltro.get(posicion));
        referencia.removeValue();
        recalculaFiltro();
    }

    public void insertar(Libro libro) {
        booksReference.push().setValue(libro);
        recalculaFiltro();
    }

    public Libro getItemById(int id) {
        return super.getItem(id);
    }

    @Override
    public void update(Observable observable, Object data) {
        setBusqueda((String) data);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        if (librosUltimoFiltro != super.getItemCount()) {
            recalculaFiltro();
        }
        return indiceFiltro.size();
    }

    public String getItemKey(int posicion) {
        String sRes = "";
        if (librosUltimoFiltro != super.getItemCount()) {
            recalculaFiltro();
        }
        if (indiceFiltro.size() > 0) {
            int id = indiceFiltro.get(posicion);
            sRes = super.getItemKey(id);
        }
        return sRes;
    }

}