package es.upv.master.audiolibros;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import es.upv.master.audiolibros.singletons.LibrosSingleton;


public class AdaptadorLibrosFiltro extends AdaptadorLibros implements Observer{
    private List<Integer> indiceFiltro; // Índice en vectorSinFiltro de
    // Cada elemento de vectorLibros
    private String busqueda = ""; // Búsqueda sobre autor o título
    private String genero = ""; // Género seleccionado
    private boolean novedad = false; // Si queremos ver solo novedades
    private boolean leido = false; // Si queremos ver solo leidos
    private int librosUltimoFiltro;

    public AdaptadorLibrosFiltro(Context contexto, DatabaseReference reference) {
        super(contexto, reference);
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
            if ((libroItem.getTitulo().toLowerCase().contains(busqueda) || libroItem.getAutor().toLowerCase().contains(busqueda))
                    && (libroItem.getGenero().startsWith(genero))
                    && (!novedad || (novedad && libroItem.getNovedad()))
                    && (!leido || (leido /*&& libro.leido*/))) {
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
        //vectorSinFiltro.add(0,libro);
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
}