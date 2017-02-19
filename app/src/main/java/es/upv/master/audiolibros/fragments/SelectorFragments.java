package es.upv.master.audiolibros.fragments;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
//import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.google.firebase.database.DatabaseReference;

import java.util.List;
import es.upv.master.audiolibros.AdaptadorLibrosFiltro;
import es.upv.master.audiolibros.Libro;
import es.upv.master.audiolibros.singletons.FirebaseDBSingleton;
import es.upv.master.audiolibros.singletons.LecturasSingleton;
import es.upv.master.audiolibros.singletons.LibrosSingleton;
import es.upv.master.audiolibros.MainActivity;
import es.upv.master.audiolibros.OpenContextualMenuLongClickAction;
import es.upv.master.audiolibros.OpenDetailClickAction;
import es.upv.master.audiolibros.R;
import es.upv.master.audiolibros.SearchObservable;

import static android.R.attr.animation;

public class SelectorFragments extends Fragment implements Animation.AnimationListener{
    private Activity actividad;
    private RecyclerView recyclerView;
    private AdaptadorLibrosFiltro adaptador;
    LibrosSingleton librosSingleton;
    LecturasSingleton lecturasSingleton;

    @Override
    public void onAttach(Context contexto) {
        super.onAttach(contexto);
        if (contexto instanceof Activity) {
            this.actividad = (Activity) contexto;
            librosSingleton = LibrosSingleton.getInstance(contexto);
            adaptador = (AdaptadorLibrosFiltro) librosSingleton.getAdaptador();
            lecturasSingleton = LecturasSingleton.getInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector, contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(actividad));
        recyclerView.setAdapter(adaptador);
        setHasOptionsMenu(true);

        adaptador.setClickAction(new OpenDetailClickAction((MainActivity) getActivity()));
        adaptador.setLongClickAction(new OpenContextualMenuLongClickAction((MainActivity) getActivity(), adaptador, vista, SelectorFragments.this));

        return vista;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_buscar);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        SearchObservable seacrhObservable = new SearchObservable();
        seacrhObservable.addObserver(adaptador);
        searchView.setOnQueryTextListener(seacrhObservable);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adaptador.setBusqueda("");
                adaptador.notifyDataSetChanged();
                return true; // Para permitir cierre
            }
        });
          super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onNewIntent(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
                adaptador.setBusqueda(intent.getStringExtra(SearchManager.QUERY));
                adaptador.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        ((MainActivity) getActivity()).mostrarElementos(true);
        adaptador.activaEscuchadorLibros();
        lecturasSingleton.activeListenerLecturas();
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        lecturasSingleton.desactivaEscuchadorLecturas();
        adaptador.desactivaEscuchadorLibros();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

 }

