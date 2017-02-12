package es.upv.master.audiolibros.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import es.upv.master.audiolibros.R;

/**
 * Created by padres on 17/01/2017.
 */

public class PreferenciasFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
