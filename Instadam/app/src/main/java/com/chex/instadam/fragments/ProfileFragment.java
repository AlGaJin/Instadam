package com.chex.instadam.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chex.instadam.GridSpacingItemDecoration;
import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.LoginActivity;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.enums.PostTypes;
import com.chex.instadam.java.Post;
import com.chex.instadam.java.User;
import com.chex.instadam.rv_adapter.ProfileFeedAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Da funcionalidad a la vista que muestra datos de los usuarios
 */
public class ProfileFragment extends Fragment {
    private User user; //Usuario del que se van a obtener los datos
    private FloatingActionButton aniadirFab, animaliaFab, plantaeFab, fungiFab; //Permite subir una publciación
    private View shadowBg;
    private boolean clicado;
    private RecyclerView rv;
    private List<Post> posts;
    private TextView usernameTxtV, followedTxtV, followingTxtV, compendioTxtV, dscpTxtV;
    private ImageView profilePicImgV;
    private Button leftBtn, rightBtn;
    private BBDDHelper bdHelper;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

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

        ((MainActivity) getActivity()).desactivarBtnNav();//Oculta el Bottom Nav View

        bdHelper = new BBDDHelper(getContext()); //Conexión con la base de datos local

        //Recuperación de los elementos que hay en la vista
        usernameTxtV = v.findViewById(R.id.fgt_profile_userameTxtView);
        followedTxtV = v.findViewById(R.id.seguidosTxtV);
        followingTxtV = v.findViewById(R.id.seguidoresTxtV);
        profilePicImgV = v.findViewById(R.id.fgt_profile_userImgV);
        compendioTxtV = v.findViewById(R.id.compendioTxtV);
        dscpTxtV = v.findViewById(R.id.dscpTxtV);
        leftBtn = v.findViewById(R.id.leftBtn);
        rightBtn = v.findViewById(R.id.rigthBtn);
        aniadirFab = v.findViewById(R.id.aniadirFab);

        //Si el perfil que se va a mostrar no es el de la persona que ha iniciado sesión,
        //se pasará por argumentos el id del usuario
        Bundle bundle = getArguments();
        if(bundle != null){
            user = bdHelper.getUserById(bundle.getString("userId")); //Se recupera el usuario con ayuda de la base de datos
        }else{
            user = MainActivity.logedUser;//Si no hay argumentos, se entiende que se cargará la vista del usuario que ha iniciado sesión
        }

        isLogedUser();//Comprueba qué usuario es
        cargarDatosPersonales();//Recupera los datos del usuario y los setea para mostrarlos

        posts = bdHelper.getUserPosts(user); //Recuepra las publicaciones del usuario

        //Configuración del Recycler View
        rv = v.findViewById(R.id.fgt_profile_rv);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv.addItemDecoration(new GridSpacingItemDecoration(3, 15, true)); //Decoración que genera un espacio concreto entre las imágenes
        rv.setAdapter(new ProfileFeedAdapter(posts));

        shadowBg = v.findViewById(R.id.shadow_bg);
        //Si se pulsa la pantalla cuando está el fondo se quita, como si se pulsara para cancelar en el botón flotante
        shadowBg.setOnClickListener(view -> aniadirFabClicked());


        clicado = false;
        aniadirFab.setOnClickListener(view -> aniadirFabClicked());

        animaliaFab = v.findViewById(R.id.animaliaFab);
        animaliaFab.setOnClickListener(view -> ((MainActivity)getActivity()).crearPublicacion(PostTypes.ANM));

        plantaeFab = v.findViewById(R.id.plantaeFab);
        plantaeFab.setOnClickListener(view -> ((MainActivity)getActivity()).crearPublicacion(PostTypes.PLT));

        fungiFab = v.findViewById(R.id.fungiFab);
        fungiFab.setOnClickListener(view -> ((MainActivity)getActivity()).crearPublicacion(PostTypes.FNG));

        return v;
    }

    /**
     * Modifica la vista dependiendo de qué usuario sea:
     *  -Si es el usuario que ha iniciado sesión, se mostrará:
     *      +Botón flotante para hacer una publicación
     *      +Botón para editar el perfil
     *      +Botón para cerrar sesión
     *  -Si es otro usuario registrado, se mostrará:
     *      +Botón para seguir
     *      +Botón para enviar mensaje
     */
    public void isLogedUser(){
        if(user.getId() == MainActivity.logedUser.getId()){
            //Acción para el botón que permite editar los datos del perfil
            leftBtn.setOnClickListener(view -> {
                ((MainActivity) getActivity()).editarPerfil();
            });

            //Acción para el botón que cierra la sesión
            rightBtn.setOnClickListener(view -> logOut());
        }else{
            aniadirFab.setVisibility(View.GONE);
            isFollowing();
            //Acción para el botón que permite seguir o dejar de seguir al usuario
            leftBtn.setOnClickListener(view -> {
                bdHelper.changeFollow(MainActivity.logedUser, user);
                isFollowing();
            });

            rightBtn.setText(R.string.enviar_mensaje);
            rightBtn.setBackgroundColor(getResources().getColor(R.color.primarioClaro, getActivity().getTheme()));
            //Acción para el botón que cierra la sesión
            rightBtn.setOnClickListener(view -> {
                ((MainActivity)getActivity()).enviarMensaje(user);
            });
        }
    }

    public void isFollowing(){
        if(bdHelper.isFollowing(MainActivity.logedUser, user)){
            leftBtn.setText(getResources().getString(R.string.siguiendo));
        }else{
            leftBtn.setText(getResources().getString(R.string.seguir));
        }
        followingTxtV.setText(bdHelper.getNumberUserFollowing(user.getId()));
    }

    /**
     * Cierra la sesión que está iniciada
     */
    public void logOut(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", "");
        editor.apply();

        startActivity(new Intent(this.getContext(), LoginActivity.class));
        requireActivity().finish();
    }

    /**
     * Carga los datos del usuario en la vista
     */
    private void cargarDatosPersonales() {
        String username = user.getUsername();
        String imgUrl = user.getProfilePic();
        String followed = bdHelper.getNumberUserFollowed(user.getId());
        String following = bdHelper.getNumberUserFollowing(user.getId());
        String compendium = bdHelper.getNumberUserCompendium(user.getId());
        String dsc = user.getDscp();

        usernameTxtV.setText(username);
        ((MainActivity)getActivity()).cargarImagenFireBase(imgUrl, profilePicImgV);
        followedTxtV.setText(followed);
        followingTxtV.setText(following);
        compendioTxtV.setText(compendium);
        if(dsc != null) dscpTxtV.setText(dsc);
    }

    /**
     * Anima el botón flotante
     */
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

    /**
     * Desactiva el Bottom Navigation Bar si se continuara la aplicación en este fragmento
     */
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).desactivarBtnNav();
    }
}