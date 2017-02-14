package es.upv.master.audiolibros.Repositories;

import es.upv.master.audiolibros.LibroStorage;

/**
 * Created by padres on 04/02/2017.
 */

public class BooksRepository {
    private final LibroStorage librosStorage;

    public BooksRepository(LibroStorage librosStorage) {
        this.librosStorage = librosStorage;
    }

    //public int getLastBook() {
    public String getLastBook() {

        return librosStorage.getLastBook();
    }

    public boolean hasLastBook(){
        return  librosStorage.hasLastBook();
    }

    //public void saveLastBook(int lastBook){
    public void saveLastBook(String key){
        //librosStorage.saveLastBook(lastBook);
        librosStorage.saveLastBook(key);
    }
}
