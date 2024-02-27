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

import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Post;
import com.chex.instadam.R;
import com.chex.instadam.java.User;
import com.chex.instadam.rv_adapter.HomeFeedAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private BBDDHelper bdHelper;
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

        bdHelper = new BBDDHelper(getContext());

        //Crea la lista de publicaciones de los usuarios a los que sigue el usuario que ha iniciado sesión
        List<Post> posts = bdHelper.getFollowedPosts(logedUser);
        posts.sort((post1, post2) -> {
            if(post1.getPublish_date().before(post2.getPublish_date())){
                return 1;
            }else if(post1.getPublish_date().after(post2.getPublish_date())){
                return -1;
            }else{
                return 0;
            }
        });

        //Recupera el Recycler View y lo carga con los posts que se han obtenido y ordenado anteriormente
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