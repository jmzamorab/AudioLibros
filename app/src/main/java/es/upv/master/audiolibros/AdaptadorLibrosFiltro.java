package es.upv.master.audiolibros;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import es.upv.master.audiolibros.singletons.LibrosSingleton;


public class AdaptadorLibrosFiltro extends AdaptadorLibros implements Observer{
    private List<Libro> vectorSinFiltro;// Vector con todos los libros
    private List<Integer> indiceFiltro; // Índice en vectorSinFiltro de
    // Cada elemento de vectorLibros
    private String busqueda = ""; // Búsqueda sobre autor o título
    private String genero = ""; // Género seleccionado
    private boolean novedad = false; // Si queremos ver solo novedades
    private boolean leido = false; // Si queremos ver solo leidos
    //Aplicacion app;
    LibrosSingleton librosSingleton;

    //public AdaptadorLibrosFiltro(Context contexto, List<Libro> vectorLibros) {
    public AdaptadorLibrosFiltro(Context contexto) {
        super(contexto);
        librosSingleton = LibrosSingleton.getInstance(contexto);

        vectorSinFiltro = librosSingleton.getVectorLibros();
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
        librosSingleton.setVectorLibros(new ArrayList<Libro>());
        indiceFiltro = new ArrayList<Integer>();
        for (int i = 0; i < vectorSinFiltro.size(); i++) {
            Libro libroItem = vectorSinFiltro.get(i);//.elementAt(i);
            if ((libroItem.titulo.toLowerCase().contains(busqueda) || libroItem.autor.toLowerCase().contains(busqueda))
                    && (libroItem.genero.startsWith(genero))
                    && (!novedad || (novedad && libroItem.novedad))
                    && (!leido || (leido && libroItem.leido))) {
                librosSingleton.getVectorLibros().add(libroItem);

                indiceFiltro.add(i);
            }
        }
    }

    public Libro getItem(int posicion) {
        return vectorSinFiltro.get(indiceFiltro.get(posicion));
    }

    public long getItemId(int posicion) {
        return indiceFiltro.get(posicion);
    }

    public void borrar(int posicion) {
        vectorSinFiltro.remove((int) getItemId(posicion));
        recalculaFiltro();
    }

    public void insertar(Libro libro) {
        vectorSinFiltro.add(0,libro);
        recalculaFiltro();
    }

    @Override
    public void update(Observable observable, Object data) {
        setBusqueda((String) data);
        notifyDataSetChanged();
    }
}