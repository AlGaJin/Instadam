package com.chex.instadam.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.enums.Type;
import com.chex.instadam.java.Post;
import com.chex.instadam.R;
import com.chex.instadam.java.User;
import com.chex.instadam.rv_adapter.HomeFeedAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private List<Post> posts;
    private final User logedUser = MainActivity.logedUser;

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
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Datos de prueba
        posts = new ArrayList<>();
        posts.add(new Post(R.drawable.user_img_1, R.drawable.tyto_alba,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_2, R.drawable.hongo,"Luisito","Amanita muscaria", "Champi", "Esto es una descripción de prueba", "02-03-23", Type.FUNGI));
        posts.add(new Post(R.drawable.user_img_3, R.drawable.fungi_bg, "Carlos", "Arfali calidonie", "Peluca falsa", "", "04-02-23", Type.FUNGI));
        rv = v.findViewById(R.id.fgt_home_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new HomeFeedAdapter(posts));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).activarBtnNav();
    }
}