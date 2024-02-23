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

        bdHelper = new BBDDHelper(getContext());

        rv = v.findViewById(R.id.search_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new SearchFeedAdapter(bdHelper.getAllUsers(MainActivity.logedUser)));

        searchEditTxt = v.findViewById(R.id.searchEditTxt);
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(searchEditTxt.getText().toString().isEmpty()) {
                    rv.setAdapter(new SearchFeedAdapter(bdHelper.getAllUsers(MainActivity.logedUser)));
                }else{
                    rv.setAdapter(new SearchFeedAdapter(bdHelper.getFilteredUsers(MainActivity.logedUser, searchEditTxt.getText().toString().trim())));
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