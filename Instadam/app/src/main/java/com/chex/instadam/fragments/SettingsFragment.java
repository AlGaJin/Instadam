package com.chex.instadam.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.chex.instadam.R;
import com.chex.instadam.activities.MainActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

/**
 * Da funcionalidad a la vista de ajustes
 */
public class SettingsFragment extends Fragment {

    private SwitchMaterial darkModeSwitch;

    //Permite modificar el toolBar del MainActivity
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    //Modificaciones del toolbar del MainActivity. Así se consigue un solo toolbar para todos los fragmentos
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.ajustes);
        ab.setDisplayHomeAsUpEnabled(true);
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ((MainActivity) getActivity()).desactivarBtnNav();

        //Recuperación de los elementos de la vista
        Spinner langSpinner = v.findViewById(R.id.langSpinner);
        darkModeSwitch = v.findViewById(R.id.darkModeSwtich);

        //Aplicación del adapter al spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.Idioma,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
        if(Locale.getDefault().getLanguage().equals("en")){ //Si el idioma del dispositivo es el inglés se cambia la selección del spinner
            langSpinner.setSelection(1);
        }
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //Dependiendo del item que se seleccione se pondrá uno u otro idioma
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String lang = "es";
                if(pos == 1){
                    lang = "en";
                }
                cambiarIdioma(lang);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Comprueba si ya está activo el modo noche, gestionando el estado switch
        darkModeSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO);

        darkModeSwitch.setOnClickListener(view -> {
            if(darkModeSwitch.isChecked()){ //Activa o desactiva el modo noche
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        return v;
    }

    /**
     * Cambia el idioma que muestra la aplicación
     * @param lang el idioma al que se va a cambiar la aplicación
     */
    private void cambiarIdioma(String lang) {
        if(!Locale.getDefault().getLanguage().equals(lang)){//Si se selecciona el mismo que ya se tiene no hace nada

            Locale myLocale = new Locale(lang);//Se recupera el locale según el idioma recibido

            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration conf = getResources().getConfiguration();
            conf.setLocale(myLocale);
            getResources().updateConfiguration(conf, dm);
            getActivity().recreate();//Reinicia el activity para que se apliquen los cambios
        }
    }
}