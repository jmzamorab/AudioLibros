package es.upv.master.audiolibros;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import es.upv.master.audiolibros.singletons.LibrosSingleton;
import es.upv.master.audiolibros.fragments.SelectorFragments;

import static android.media.CamcorderProfile.get;

public class OpenContextualMenuLongClickAction implements LongClickAction {

    private final MainActivity actividad;
    //private final List<Libro> vectorLibros;
    private final AdaptadorLibrosFiltro adaptador;
    private final View vista;
    private final SelectorFragments selectorFragment;
    LibrosSingleton librosSingleton;// = LibrosSingleton.getInstance();


    public OpenContextualMenuLongClickAction(MainActivity actividad, AdaptadorLibros adaptador, View vista, SelectorFragments selectorFragment) {
        this.actividad = actividad;

        librosSingleton = LibrosSingleton.getInstance(actividad);
        this.adaptador = (AdaptadorLibrosFiltro) librosSingleton.getAdaptador();


        this.vista = vista;
        this.selectorFragment = selectorFragment;
    }


    @Override
    public void execute(final int position) {
            AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
            CharSequence[] opciones = {"Compartir", "Borrar ", "Insertar"};
            menu.setItems(opciones, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int opcion) {
                    switch (opcion) {
                        case 0: //Compartir Libro
                            //Libro libro = vectorLibros.get(position);
                            Libro libroItem = librosSingleton.getVectorLibros().get(position);
                            ((MainActivity) actividad).shareBook(libroItem);
                            break;
                        case 1: //Borrar
                            Snackbar.make(vista, "¿Estás seguro?", Snackbar.LENGTH_LONG).setAction("SI", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //vectorLibros.remove(position);
                                    librosSingleton.getVectorLibros().remove(position);
                                    Animation anim = AnimationUtils.loadAnimation(actividad, R.anim.menguar);
                                    anim.setAnimationListener((Animation.AnimationListener) selectorFragment);
                                    vista.startAnimation(anim);
                                    adaptador.borrar(position);
                                    adaptador.notifyDataSetChanged();
                                }
                            }).show();
                            break;
                        case 2: //Insertar
                            adaptador.insertar((Libro) adaptador.getItem(position));
                            //adaptador.notifyDataSetChanged();
                            adaptador.notifyItemRangeChanged(0,adaptador.getItemCount());
                            Snackbar.make(vista, "Libro insertado", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            }).show();


                            break;
                    }
                }
            });
        menu.create().show();

    }
}
