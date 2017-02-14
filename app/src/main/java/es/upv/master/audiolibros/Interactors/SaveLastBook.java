package es.upv.master.audiolibros.Interactors;


import es.upv.master.audiolibros.LibroStorage;
import es.upv.master.audiolibros.Repositories.BooksRepository;

import static android.R.attr.id;

public class SaveLastBook {

    private final BooksRepository booksRepository;

    public SaveLastBook(BooksRepository booksRepository) {

        //this.librosStorage = librosStorage;
        this.booksRepository = booksRepository;
    }

     public void execute(String key) {
        booksRepository.saveLastBook(key);
     }
    //public void execute(int id) {
    //    booksRepository.saveLastBook(id);
   // }
}
