package es.upv.master.audiolibros;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by padres on 08/01/2017.
 */

public class Libro {
    private int colorVibrante = -1;
    private int colorApagado = -1;

    public int getColorVibrante() {
        return colorVibrante;
    }

    public void setColorVibrante(int colorVibrante) {
        this.colorVibrante = colorVibrante;
    }

    public int getColorApagado() {
        return colorApagado;
    }

    public void setColorApagado(int colorApagado) {
        this.colorApagado = colorApagado;
    }

    public String titulo;

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String autor;
    //public int recursoImagen;
    public String urlImagen;
    public String urlAudio;
    public String genero; // Género literario
    public Boolean novedad; // Es una novedad
    public Boolean leido; // Leído por el usuario
    public final static String G_TODOS = "Todos los géneros";
    public final static String G_EPICO = "Poema épico";
    public final static String G_S_XIX = "Literatura siglo XIX";
    public final static String G_SUSPENSE = "Suspense";
    public final static String[] G_ARRAY = new String[]{G_TODOS, G_EPICO,
            G_S_XIX, G_SUSPENSE};
    private final static String SERVIDOR = "http://www.dcomg.upv.es/~jtomas/android/audiolibros/";

    // Patrón Null Object
    public final static Libro LIBRO_EMPTY = new LibroBuilder().withTitulo("anonimo").withUrlImagen(SERVIDOR +"sin_portada.jpg").withGenero(G_TODOS).withLeido(false).withNovedad(true).build();

    private  Libro(String titulo, String autor, String urlImagen,
                 String urlAudio, String genero, Boolean novedad, Boolean leido) {
        this.titulo = titulo;
        this.autor = autor;
        this.urlImagen = urlImagen;
        this.urlAudio = urlAudio;
        this.genero = genero;
        this.novedad = novedad;
        this.leido = leido;
    }


    public static List<Libro> ejemploLibros() {
        List<Libro> libros = new ArrayList<Libro>();
        libros.add(new LibroBuilder().withTitulo("Kappa").withAutor("Akutagawa").withUrlImagen(SERVIDOR + "kappa.jpg").withUrlAudio(SERVIDOR + "kappa.mp3").withGenero(Libro.G_S_XIX).withLeido(false).withNovedad(false).build());
        libros.add(new LibroBuilder().withTitulo("Avecilla").withAutor("Alas Clarín, Leopoldo").
                withUrlImagen(SERVIDOR + "avecilla.jpg").withUrlAudio(SERVIDOR + "avecilla.mp3").withGenero(Libro.G_S_XIX).withLeido(true).withNovedad(false).build());
        libros.add(new LibroBuilder().withTitulo("Divina Comedia").withAutor("Dante").withUrlImagen(SERVIDOR + "divina_comedia.jpg").withUrlAudio(SERVIDOR + "divina_comedia.mp3").withGenero(Libro.G_EPICO).withLeido(true).withNovedad(false).build());
        libros.add(new LibroBuilder().withTitulo("Viejo Pancho, El").withAutor("Alonso y Trelles, José").withUrlImagen(SERVIDOR + "viejo_pancho.jpg").withUrlAudio(SERVIDOR + "viejo_pancho.mp3").withGenero(Libro.G_S_XIX).withLeido(true).withNovedad(true).build());
        libros.add(new LibroBuilder().withTitulo("Canción de Rolando").withAutor("Anónimo").withUrlImagen(SERVIDOR + "cancion_rolando.jpg").withUrlAudio(SERVIDOR + "cancion_rolando.mp3").withGenero(Libro.G_EPICO).withLeido(false).withNovedad(true).build());
        libros.add(new LibroBuilder().withTitulo("Matrimonio de sabuesos").withAutor("Agata Christie").withUrlImagen(SERVIDOR + "matrim_sabuesos.jpg").withUrlAudio(SERVIDOR + "matrim_sabuesos.mp3").withGenero(Libro.G_SUSPENSE).withLeido(false).withNovedad(true).build());
        libros.add(new LibroBuilder().withTitulo("La iliada").withAutor("Homero").withUrlImagen(SERVIDOR + "la_iliada.jpg").withUrlAudio(SERVIDOR + "la_iliada.mp3").withGenero(Libro.G_EPICO).withLeido(true).withNovedad(false).build());
        return libros;
    }

    public static class LibroBuilder{
        private String titulo = "";
        private String autor = "";
        private  String urlImagen = SERVIDOR +"sin_portada.jpg";
        private  String urlAudio = "";
        private  String genero = G_TODOS; // Género literario
        private  Boolean novedad = false; // Es una novedad
        private  Boolean leido = false; // Leído por el usuario

        public LibroBuilder withTitulo(String titulo)
        {
            this.titulo = titulo;
            return this;
        }
        public LibroBuilder withAutor(String autor)
        {
            this.autor = autor;
            return this;
        }
        public LibroBuilder withUrlImagen(String urlImagen)
        {
            this.urlImagen = urlImagen;
            return this;
        }
        public LibroBuilder withUrlAudio(String urlAudio)
        {
            this.urlAudio = urlAudio;
            return this;
        }
        public LibroBuilder withGenero(String genero)
        {
            this.genero = genero;
            return this;
        }
        public LibroBuilder withNovedad(Boolean novedad)
        {
            this.novedad = novedad;
            return this;
        }
        public LibroBuilder withLeido(Boolean leido)
        {
            this.leido = leido;
            return this;
        }

        public Libro build()
        {
            return new Libro(titulo, autor, urlImagen, urlAudio, genero, novedad, leido);
        }

    }
}
