package com.chex.instadam.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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

public class SettingsFragment extends Fragment {

    private SwitchMaterial darkModeSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        Spinner langSpinner = v.findViewById(R.id.langSpinner);
        darkModeSwitch = v.findViewById(R.id.darkModeSwtich);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.Idioma,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
        if(Locale.getDefault().getLanguage().equals("en")){
            langSpinner.setSelection(1);
        }
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        darkModeSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO);

        darkModeSwitch.setOnClickListener(view -> {
            if(darkModeSwitch.isChecked()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        return v;
    }

    private void cambiarIdioma(String lang) {
        if(!Locale.getDefault().getLanguage().equals(lang)){
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(myLocale);
            res.updateConfiguration(conf, dm);
            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).desactivarBtnNav();
    }
}