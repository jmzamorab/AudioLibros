package es.upv.master.audiolibros;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Vector;

import static es.upv.master.audiolibros.R.drawable.libro;

/**
 * Created by padres on 08/01/2017.
 */

public class Aplicacion extends Application {
    //private static RequestQueue colaPeticiones;
    //private static ImageLoader lectorImagenes;

    //public LibrosSingleton getLibro() {
     //   return libro;
    //}

    //public void setLibro(LibrosSingleton libro) {
      //  this.libro = libro;
    //}

     //LibrosSingleton libro;
    //private List<Libro> vectorLibros;
    //private AdaptadorLibros adaptador;
    //private AdaptadorLibrosFiltro adaptador;

    @Override
    public void onCreate() {
    /*    libro = LibrosSingleton.getInstance();
        libro.setVectorLibros(Libro.ejemploLibros());

        //vectorLibros = Libro.ejemploLibros();
        //adaptador = new AdaptadorLibrosFiltro(this);//, vectorLibros);
        libro.setAdaptador( new AdaptadorLibrosFiltro(this));
        colaPeticiones = Volley.newRequestQueue(this);
        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });*/
    }

    //public AdaptadorLibros getAdaptador() {
  /*  public AdaptadorLibrosFiltro getAdaptador() {
        return (AdaptadorLibrosFiltro) libro.getAdaptador();
    }

/*    public List<Libro> getVectorLibros() {
        return libro.getVectorLibros();

    }*/

    /*public static RequestQueue getColaPeticiones() {
     //   return colaPeticiones;
    }

    public static ImageLoader getLectorImagenes() {
        return lectorImagenes;
    }*/
}
