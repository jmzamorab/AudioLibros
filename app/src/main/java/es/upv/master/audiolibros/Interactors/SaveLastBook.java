package es.upv.master.audiolibros.Interactors;


import es.upv.master.audiolibros.LibroStorage;
import es.upv.master.audiolibros.Repositories.BooksRepository;

public class SaveLastBook {

    private final BooksRepository booksRepository;

    public SaveLastBook(BooksRepository booksRepository) {

        //this.librosStorage = librosStorage;
        this.booksRepository = booksRepository;
    }


    public void execute(int id) {
        booksRepository.saveLastBook(id);
    }
}
