package es.upv.master.audiolibros;

import android.app.Activity;
import android.os.Bundle;

import es.upv.master.audiolibros.fragments.PreferenciasFragment;

/**
 * Created by padres on 17/01/2017.
 */

public class PreferenciasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id. content, new PreferenciasFragment()).commit();
    }
}
