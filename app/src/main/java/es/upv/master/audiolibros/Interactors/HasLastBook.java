package es.upv.master.audiolibros.Interactors;


import es.upv.master.audiolibros.LibroStorage;
import es.upv.master.audiolibros.Repositories.BooksRepository;

public class HasLastBook {
  //  private final LibroStorage librosStorage;
    private final BooksRepository booksRepository;

    public HasLastBook(BooksRepository booksRepository) {

        //this.librosStorage = librosStorage;
        this.booksRepository = booksRepository;
    }

    public boolean execute() {
        return booksRepository.hasLastBook();
    }
}
