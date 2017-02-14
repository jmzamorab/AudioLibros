package es.upv.master.audiolibros.Presenter;

import es.upv.master.audiolibros.Interactors.GetLastBook;
import es.upv.master.audiolibros.Interactors.HasLastBook;
import es.upv.master.audiolibros.Interactors.SaveLastBook;

import static android.R.attr.id;

/**
 * Created by padres on 01/02/2017.
 */

public class MainPresenter {
    private final View view;
    private SaveLastBook saveLastBook;
    private HasLastBook  hasLastBook;
    private GetLastBook getLastBook;

    public MainPresenter(SaveLastBook saveLastBook, HasLastBook hasLastBook, GetLastBook getLastBook, MainPresenter.View view) {
        this.saveLastBook = saveLastBook;
        this.hasLastBook = hasLastBook;
        this.getLastBook = getLastBook;
        this.view = view;
            }

    public void clickFavoriteButton() {
        if (hasLastBook.execute()) {
            view.mostrarFragmentDetalle(getLastBook.execute());
        } else {
            view.mostrarNoUltimaVisita();
        }
    }

    //public void openDetalle(int id) {
    public void openDetalle(String key) {
        //saveLastBook.execute(id);
        //view.mostrarFragmentDetalle(id);
        saveLastBook.execute(key);
        view.mostrarFragmentDetalle(key);
      }

    public interface View {
//        void mostrarFragmentDetalle(int lastBook);
        void mostrarFragmentDetalle(String key);
        void mostrarNoUltimaVisita();
    }
}
