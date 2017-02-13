package es.upv.master.audiolibros.Presenter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

import java.io.IOException;

import es.upv.master.audiolibros.Libro;
import es.upv.master.audiolibros.OnValueListener;
import es.upv.master.audiolibros.ZoomSeekBar;
import es.upv.master.audiolibros.fragments.DetalleFragment;
import es.upv.master.audiolibros.singletons.LibrosSingleton;
import es.upv.master.audiolibros.singletons.VolleySingleton;

/**
 * Created by JoseMaria.Zamora on 02/02/2017.
 */

public class DetalleFragmentPresenter implements OnValueListener, MediaPlayer.OnPreparedListener {
    private final static int SECOND = 1000;
    private final static int MINUTE = SECOND * 60;
    private MediaPlayer mediaPlayer = null;
    private ZoomSeekBar zmSk;
    private final View view;
    private Activity activity;
    LibrosSingleton librosSingleton;
    VolleySingleton volleySingleton;


    public DetalleFragmentPresenter(DetalleFragmentPresenter.View vista) {
        this.zmSk = ((DetalleFragment) vista).getZmSk();
        this.activity = ((DetalleFragment) vista).getActivity();
        view = vista;
        librosSingleton = LibrosSingleton.getInstance(this.activity);
        volleySingleton = VolleySingleton.getInstance(this.activity);
    }


    public void ponInfoLibro(int id) {
        final Libro libro = librosSingleton.getVectorLibros().get(id);
        ImageLoader imgLoader = volleySingleton.getLectorImagenes();
        view.showLibro(libro, imgLoader);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(this);
        Uri audio = Uri.parse(libro.getUrlAudio());
        try {
            mediaPlayer.setDataSource(this.activity, audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir " + audio, e);
        }
    }

    public void stopMediaPlayer() {
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }
    }

    private void initializeZmSkBar(int duration) {
        if (zmSk != null) {
            zmSk.setValMin(1);
            zmSk.setValMax(duration);
            zmSk.setVal(1);
            zmSk.setEscalaMin(1);
            zmSk.setEscalaMax(duration);
            zmSk.setEscalaIni(1);
            if (zmSk.getEscalaMax() <= 5) {
                zmSk.setEscalaRaya(1);
                zmSk.setEscalaRayaLarga(2);
            } else {
                if (zmSk.getEscalaMax() <= 200) {
                    zmSk.setEscalaRaya(2);
                    zmSk.setEscalaRayaLarga(5);
                } else if (zmSk.getEscalaMax() <= 400) {
                    zmSk.setEscalaRaya(3);
                    zmSk.setEscalaRayaLarga(7);
                } else {
                    zmSk.setEscalaRaya(5);
                    zmSk.setEscalaRayaLarga(10);
                }
            }
            zmSk.setOnValueListener((OnValueListener) this);
        }
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        initializeZmSkBar((mediaPlayer.getDuration() / SECOND / 60));
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(this.activity);

        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onChangeVal(Integer value) {
        mediaPlayer.seekTo(value * SECOND);
    }



    public interface View {
        void showLibro(Libro libro, ImageLoader imgLoader);
    }

}
