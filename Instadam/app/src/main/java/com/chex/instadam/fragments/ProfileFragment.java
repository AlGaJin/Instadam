package com.chex.instadam.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import jp.wasabeef.glide.transformations.BlurTransformation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chex.instadam.GridSpacingItemDecoration;
import com.chex.instadam.R;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.enums.Type;
import com.chex.instadam.java.Post;
import com.chex.instadam.rv_adapter.ProfileFeedAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FloatingActionButton aniadirFab, animaliaFab, plantaeFab, fungiFab;
    private ImageButton fungiCatalogBtn, plantaeCatalogBtn, animaliaCatalogBtn;
    private View shadowBg;
    private boolean clicado;
    private RecyclerView rv;
    private List<Post> posts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.profile_tb_menu, menu);
        ab.setTitle("");
        ab.setDisplayHomeAsUpEnabled(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ((MainActivity) getActivity()).desactivarBtnNav();

        posts = new ArrayList<>();
        posts.add(new Post(R.drawable.user_img_1, R.drawable.tyto_alba,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.hongo,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.animalia_bg,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.fungi_bg,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.plantae_bg,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.tyto_alba,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.hongo,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.animalia_bg,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.fungi_bg,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));
        posts.add(new Post(R.drawable.user_img_1, R.drawable.plantae_bg,"Vicioso","Tyto alba", "Lechuza común", "Vuela", "14-05-23", Type.ANIMALIA));

        rv = v.findViewById(R.id.fgt_profile_rv);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv.addItemDecoration(new GridSpacingItemDecoration(3, 15, true));
        rv.setAdapter(new ProfileFeedAdapter(posts));


        shadowBg = v.findViewById(R.id.shadow_bg);
        shadowBg.setOnClickListener(view -> aniadirFabClicked());

        aniadirFab = v.findViewById(R.id.aniadirFab);
        clicado = false;
        aniadirFab.setOnClickListener(view -> aniadirFabClicked());

        animaliaFab = v.findViewById(R.id.animaliaFab);
        animaliaFab.setOnClickListener(view -> Toast.makeText(this.getContext(), "Añadir animal", Toast.LENGTH_SHORT).show());

        plantaeFab = v.findViewById(R.id.plantaeFab);
        plantaeFab.setOnClickListener(view -> Toast.makeText(this.getContext(), "Añadir planta", Toast.LENGTH_SHORT).show());

        fungiFab = v.findViewById(R.id.fungiFab);
        fungiFab.setOnClickListener(view -> Toast.makeText(this.getContext(), "Añadir hongo", Toast.LENGTH_SHORT).show());

        //Cambiar la función del botón Back en el móvil
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                salir();
            }
        });

        return v;
    }

    //Método que le da animación al botón flotante
    private void aniadirFabClicked() {
        if(!clicado){ //Cambia la visibilidad de los otros botones flotantes según si está o no clicado el fab principal
            animaliaFab.setVisibility(View.VISIBLE);
            plantaeFab.setVisibility(View.VISIBLE);
            fungiFab.setVisibility(View.VISIBLE);
        }else{
            animaliaFab.setVisibility(View.INVISIBLE);
            plantaeFab.setVisibility(View.INVISIBLE);
            fungiFab.setVisibility(View.INVISIBLE);
        }

        if(!clicado){ //Activa una acción según si está o no clicado el fab principal
            animaliaFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom));
            plantaeFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom));
            fungiFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom));
            aniadirFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_open));
            shadowBg.setVisibility(View.VISIBLE);
        }else{
            animaliaFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom));
            plantaeFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom));
            fungiFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom));
            aniadirFab.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_close));
            shadowBg.setVisibility(View.GONE);
        }

        clicado = !clicado; //Cambia el booleano utilizado para saber si está clicado o no el fab principal
    }

    //Método que permite salir del fragmento al MainActivity y mostrar de nuevo el Bottom Navigarion View
    private void salir(){
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.frLyt, new HomeFragment()).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).activarBtnNav();
    }
}