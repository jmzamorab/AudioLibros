package es.upv.master.audiolibros.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import es.upv.master.audiolibros.Presenter.DetalleFragmentPresenter;
import es.upv.master.audiolibros.Libro;
import es.upv.master.audiolibros.MainActivity;
import es.upv.master.audiolibros.R;
import es.upv.master.audiolibros.ZoomSeekBar;



public class DetalleFragment extends Fragment implements DetalleFragmentPresenter.View, View.OnTouchListener{
    public static String ARG_ID_LIBRO = "id_libro";

    public ZoomSeekBar getZmSk() {
        return zmSk;
    }
    private ZoomSeekBar zmSk;
    DetalleFragmentPresenter detalleFragmentPresenter;
    private View vista;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        super.onCreateView(inflador, contenedor, savedInstanceState);
        vista = inflador.inflate(R.layout.fragment_detalle, contenedor, false);
        zmSk = (ZoomSeekBar) vista.findViewById(R.id.zsbModifica);
        //MediaplayerRepository mediaplayerRepository = new MediaplayerRepository(new MediaPlayer());
        detalleFragmentPresenter = new DetalleFragmentPresenter (this);

        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            detalleFragmentPresenter.ponInfoLibro(position);
        } else {
            detalleFragmentPresenter.ponInfoLibro(0);
        }
        return vista;
    }

    public void ponInfoLibro(int id) {
        detalleFragmentPresenter.ponInfoLibro(id);
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
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((NetworkImageView) vista.findViewById(R.id.portada)).setImageUrl(libro.urlImagen, imgLoader);
        vista.setOnTouchListener(this);
    }



}
