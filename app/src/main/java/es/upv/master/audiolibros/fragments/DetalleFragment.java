package es.upv.master.audiolibros.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upv.master.audiolibros.Presenter.DetalleFragmentPresenter;
import es.upv.master.audiolibros.Libro;
import es.upv.master.audiolibros.MainActivity;
import es.upv.master.audiolibros.R;
import es.upv.master.audiolibros.ZoomSeekBar;
import es.upv.master.audiolibros.singletons.LecturasSingleton;

import static android.R.attr.id;
import static android.R.attr.key;
import static es.upv.master.audiolibros.R.id.autor;


public class DetalleFragment extends Fragment implements DetalleFragmentPresenter.View, View.OnTouchListener{
    public static String ARG_ID_LIBRO = "id_libro";

    public ZoomSeekBar getZmSk() {
        return zmSk;
    }
    private ZoomSeekBar zmSk;
    private DetalleFragmentPresenter detalleFragmentPresenter;
    private View vista;
    private LecturasSingleton lecturasSingleton;
    private FirebaseUser currentUser;
    private ToggleButton tglButton;
    private String key;


    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        super.onCreateView(inflador, contenedor, savedInstanceState);
        vista = inflador.inflate(R.layout.fragment_detalle, contenedor, false);
        zmSk = (ZoomSeekBar) vista.findViewById(R.id.zsbModifica);

        tglButton = (ToggleButton) vista.findViewById(R.id.libroLeido);
        tglButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("TRAZA", "DetalleFragment, pulsado botón Leido con key " + key + " y checked " + isChecked);
                lecturasSingleton.libroLeidoPorUsuario(key, isChecked);
            }
        });

        lecturasSingleton = LecturasSingleton.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        detalleFragmentPresenter = new DetalleFragmentPresenter (this);

        Bundle args = getArguments();
        if (args != null) {
            key = args.getString(ARG_ID_LIBRO);
            detalleFragmentPresenter.ponInfoLibro(key);
        }
        return vista;
    }

    public void ponInfoLibro(String key) {
        detalleFragmentPresenter.ponInfoLibro(key);
    }

    @Override
    public void onResume() {
        DetalleFragment detalleFragment = (DetalleFragment) getFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment == null) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }

    @Override
    public boolean onTouch(View vista, MotionEvent evento) {
        return false;
    }

    //@Override
    public void onStop() {
        detalleFragmentPresenter.stopMediaPlayer();
        super.onStop();
    }

    @Override
    public void showLibro(Libro libro, ImageLoader imgLoader) {
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.getTitulo());
        ((TextView) vista.findViewById(autor)).setText(libro.getAutor());
        ((NetworkImageView) vista.findViewById(R.id.portada)).setImageUrl(libro.getUrlImagen(), imgLoader);
        //((ToggleButton) vista.findViewById(R.id.libroLeido)).setActivated(libro.leidoPor(currentUser.getUid()));
        boolean leidoFB = lecturasSingleton.libroLeido(key);
        Log.d("TRAZA", "Está leído en BBDD => " + leidoFB);
        ((ToggleButton) vista.findViewById(R.id.libroLeido)).setChecked(leidoFB);
        vista.setOnTouchListener(this);
    }



}
