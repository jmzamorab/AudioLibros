package es.upv.master.audiolibros;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.upv.master.audiolibros.singletons.LibrosSingleton;
import es.upv.master.audiolibros.singletons.VolleySingleton;


import static es.upv.master.audiolibros.R.drawable.libro;
import static es.upv.master.audiolibros.R.id.titulo;

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder>
        implements ChildEventListener {
    private LayoutInflater inflador; //Crea Layouts a partir del XML
    private Context contexto;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private ClickAction clickAction = new EmptyClickAction();
    private LongClickAction longClickAction = new EmptyLongClickAction();
    VolleySingleton volleySingleton;
    // Debido al uso de FireBase SDK
    private ArrayList<String> keys;
    private ArrayList<DataSnapshot> items;
    protected DatabaseReference booksReference;


    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public void setLongClickAction(LongClickAction longClickAction) {
        this.longClickAction = longClickAction;
    }


    public void setOnItemLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public AdaptadorLibros(Context contexto, DatabaseReference reference) {
        this.contexto = contexto;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        volleySingleton = VolleySingleton.getInstance(contexto);
        // creo nuevamente la estructura, sino añadiría las mismas siempre
        keys = new ArrayList<String>();
        items = new ArrayList<DataSnapshot>();
        // obtengo la referencia a BBDD y asigno el Listener
        booksReference = reference;
        booksReference.addChildEventListener(this);
    }

    //SI añaden nuevo libro, inserto en ambas estructuras locales items y las keys
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        items.add(dataSnapshot);
        keys.add(dataSnapshot.getKey());
        notifyItemInserted(getItemCount() - 1);
    }

    //SI modifican libro, actaulizo en estructura local items
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        int index = keys.indexOf(key);
        if (index != -1) {
            items.set(index, dataSnapshot);
            notifyItemChanged(index, dataSnapshot.getValue(Libro.class));
        }
    }

    // SI borran libro, borro en ambas estructuras
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        int index = keys.indexOf(key);
        if (index != -1) {
            keys.remove(index);
            items.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView portada;
        public TextView titulo;

        public ViewHolder(View itemView) {
            super(itemView);
            portada = (ImageView) itemView.findViewById(R.id.portada);
            portada.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.elemento_selector, null);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public DatabaseReference getRef(int pos) {
        return items.get(pos).getRef();
    }

    public Libro getItem(int pos) {
        return items.get(pos).getValue(Libro.class);
    }

    public String getItemKey(int pos) {
        return keys.get(pos);
    }

    public int getPosByKey(String key){
       return  keys.indexOf(key);
    }

    public Libro getItemByKey(String key) {
        int index = keys.indexOf(key);
        if (index != -1) {
            return getItem(index);//return items.get(index).getValue(Libro.class);
        } else {
            return null;
        }
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int posicion) {
        Libro libro = getItem(posicion);
        final String key = this.getItemKey(posicion);
        holder.titulo.setText(libro.getTitulo());
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   clickAction.execute(key);
                                               }
                                           }
        );
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                   @Override
                                                   public boolean onLongClick(View v) {
                                                       longClickAction.execute(key);
                                                       return true;
                                                   }
                                               }

        );
        volleySingleton.getLectorImagenes().get(libro.getUrlImagen(), new ImageLoader.ImageListener() {


            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                final Libro libro = getItem(posicion);
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    holder.portada.setImageBitmap(bitmap);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            if (libro.getColorVibrante() == -1) {
                                libro.setColorVibrante(palette.getLightMutedColor(0));
                            }
                            if (libro.getColorApagado() == -1) {
                                libro.setColorApagado(palette.getLightVibrantColor(0));
                            }
                            holder.itemView.setBackgroundColor(libro.getColorVibrante());
                            holder.titulo.setBackgroundColor(libro.getColorApagado());
                            holder.portada.invalidate();
                        }
                    });
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                holder.portada.setImageResource(R.drawable.books);
            }
        });

    }

    public void activaEscuchadorLibros() {
        //keys = new ArrayList<String>();
        //items = new ArrayList<DataSnapshot>();
        //booksReference.addChildEventListener(this);
        FirebaseDatabase.getInstance().goOnline();
    }

    public void desactivaEscuchadorLibros() {
        //  booksReference.removeEventListener(this);
        FirebaseDatabase.getInstance().goOffline();
    }

}
