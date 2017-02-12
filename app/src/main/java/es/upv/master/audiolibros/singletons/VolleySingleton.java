package es.upv.master.audiolibros.singletons;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton ourInstance;
    private RequestQueue colaPeticiones;
    private ImageLoader lectorImagenes;
    private Context context;

    public static VolleySingleton getInstance(Context contexto) {
        if (ourInstance == null) {
            ourInstance = new VolleySingleton(contexto);
            ourInstance.initialize();
        }
        return ourInstance;
    }

    private VolleySingleton(Context context) {
        this.context = context;
    }

    public RequestQueue getColaPeticiones() {
        return colaPeticiones;
    }

    public void setColaPeticiones(RequestQueue colaPeticiones) {
        this.colaPeticiones = colaPeticiones;
    }

    public ImageLoader getLectorImagenes() {
        return lectorImagenes;
    }

    public void setLectorImagenes(ImageLoader lectorImagenes) {
        this.lectorImagenes = lectorImagenes;
    }

    private void initialize() {
        colaPeticiones = Volley.newRequestQueue(context);
        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });
    }
}



