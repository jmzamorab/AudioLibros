package es.upv.master.audiolibros.Interactors;


import es.upv.master.audiolibros.LibroStorage;
import es.upv.master.audiolibros.Repositories.BooksRepository;

import static android.R.attr.id;

public class GetLastBook {
    //private final LibroStorage librosStorage;
    private final BooksRepository booksRepository;

    public GetLastBook(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public int execute() {
        return booksRepository.getLastBook();
    }
}
