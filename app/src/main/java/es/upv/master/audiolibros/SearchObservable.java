package es.upv.master.audiolibros;


import android.widget.SearchView;

import java.util.Observable;




public class SearchObservable extends Observable implements SearchView.OnQueryTextListener {

    @Override
    public boolean onQueryTextSubmit(String query) {
        setChanged();
        notifyObservers(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
