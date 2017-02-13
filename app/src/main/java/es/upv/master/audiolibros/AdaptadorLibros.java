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

import es.upv.master.audiolibros.singletons.LibrosSingleton;
import es.upv.master.audiolibros.singletons.VolleySingleton;

import static es.upv.master.audiolibros.R.id.titulo;

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder> {
    private LayoutInflater inflador; //Crea Layouts a partir del XML
    private Context contexto;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private ClickAction clickAction = new EmptyClickAction();
    private LongClickAction longClickAction = new EmptyLongClickAction();
    LibrosSingleton libroSingleton;
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

    public AdaptadorLibros(Context contexto){
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contexto = contexto;
        libroSingleton = LibrosSingleton.getInstance(contexto);
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
        //v.setOnLongClickListener(onLongClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int posicion) {
        final Libro libroItem = libroSingleton.getVectorLibros().get(posicion);
        holder.titulo.setText(libroItem.getTitulo());
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
        volleySingleton.getLectorImagenes().get(libroItem.getUrlImagen(), new ImageLoader.ImageListener() {


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
                           if (libroItem.getColorVibrante() == -1){
                               libroItem.setColorVibrante(palette.getLightMutedColor(0));
                           }
                           if (libroItem.getColorApagado() == -1){
                               libroItem.setColorApagado(palette.getLightVibrantColor(0));
                           }
                           holder.itemView.setBackgroundColor(libroItem.getColorVibrante());
                           holder.titulo.setBackgroundColor(libroItem.getColorApagado());
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

    // Indicamos el número de elementos de la lista
    @Override
    public int getItemCount() {
        return libroSingleton.getVectorLibros().size();
    }
}
