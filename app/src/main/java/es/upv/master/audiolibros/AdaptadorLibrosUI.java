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
import com.google.firebase.database.DatabaseReference;

import es.upv.master.audiolibros.singletons.VolleySingleton;

public class AdaptadorLibrosUI extends FirebaseRecyclerAdapter<Libro, AdaptadorLibrosUI.ViewHolder> {
    private LayoutInflater inflador; //Crea Layouts a partir del XML
    private Context contexto;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private ClickAction clickAction = new EmptyClickAction();
    private LongClickAction longClickAction = new EmptyLongClickAction();
   protected DatabaseReference booksReference;
    VolleySingleton volleySingleton;

    public void setClickAction(ClickAction clickAction)
    {
        this.clickAction = clickAction;
    }

    public void setLongClickAction(LongClickAction longClickAction)
    {
        this.longClickAction = longClickAction;
    }


    public void setOnItemLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public AdaptadorLibrosUI(Context contexto, DatabaseReference reference){
        super(Libro.class, R.layout.elemento_selector, AdaptadorLibrosUI.ViewHolder.class, reference);
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contexto = contexto;
        this.booksReference = reference;
        volleySingleton = VolleySingleton.getInstance(contexto);
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
    // Creamos el ViewHolder con las vista de un elemento sin personalizar @Override

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.elemento_selector, null);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void populateViewHolder(final ViewHolder holder, final Libro libro, final int posicion) {
        holder.titulo.setText(libro.getTitulo());
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickAction.execute(posicion);
            }
        }
        );
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
          @Override
          public boolean onLongClick(View v)
          {
              longClickAction.execute(posicion);
              return true;
          }
        }

        );
        volleySingleton.getLectorImagenes().get(libro.getUrlImagen(), new ImageLoader.ImageListener() {


            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    holder.portada.setImageBitmap(bitmap);
                    //Palette palette = Palette.from(bitmap).generate();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener()
                    {
                       public void onGenerated(Palette palette)
                       {
                           if (libro.getColorVibrante() == -1){
                               libro.setColorVibrante(palette.getLightMutedColor(0));
                           }
                           if (libro.getColorApagado() == -1){
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

}
