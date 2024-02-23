package com.chex.instadam.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.rv_adapter.SearchFeedAdapter;

public class SearchFragment extends Fragment {
    private RecyclerView rv;
    private BBDDHelper bdHelper;
    private EditText searchEditTxt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.app_name));
        ab.setDisplayHomeAsUpEnabled(false);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.home_tb_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        //Instanciación del ayudante de la base de datos local
        bdHelper = new BBDDHelper(getContext());

        //Instanciación e lanzamiento del recycler view que mostará a todos los usuarios registrados en la aplicación
        rv = v.findViewById(R.id.search_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new SearchFeedAdapter(bdHelper.getAllUsers(MainActivity.logedUser)));

        //Recuperación del Edit Text que permitirá hacer búsquedas
        searchEditTxt = v.findViewById(R.id.searchEditTxt);
        //Se le añade un listener que espera a que se cambie el texto, permite la busqueda dinámica (sin pulser enter)
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            //Estos dos métodos son obligatorios de implementar, sin embargo no son necsarios
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            //Con el uso del dbHelper, se hacen busquedas dinámicas
            @Override
            public void afterTextChanged(Editable editable) {
                String filter = searchEditTxt.getText().toString().trim();
                if(filter.isEmpty()) { //Si no hay nada en el editText se muestran todos los usuarios
                    rv.setAdapter(new SearchFeedAdapter(bdHelper.getAllUsers(MainActivity.logedUser)));
                }else{ //Si hay algo escrito se llama a la base de datos con el filtro
                    rv.setAdapter(new SearchFeedAdapter(bdHelper.getFilteredUsers(MainActivity.logedUser, filter)));
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).activarBtnNav();
    }
}