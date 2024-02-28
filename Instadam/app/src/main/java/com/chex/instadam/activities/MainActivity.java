package com.chex.instadam.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.enums.PostTypes;
import com.chex.instadam.fragments.ChatFragment;
import com.chex.instadam.fragments.ChatsFragment;
import com.chex.instadam.fragments.EditProfileFragment;
import com.chex.instadam.fragments.HomeFragment;
import com.chex.instadam.R;
import com.chex.instadam.fragments.NotificationFragment;
import com.chex.instadam.fragments.ProfileFragment;
import com.chex.instadam.fragments.PostFragment;
import com.chex.instadam.fragments.SearchFragment;
import com.chex.instadam.fragments.SettingsFragment;
import com.chex.instadam.java.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Declaración de variables que se van a usar en diferentes puntos del programa
    private BottomNavigationView bttmNav;
    private Deque<Integer> idDeque; // Lista "estática" que tiene funciones de listas dinámicas (útil para Back Stack casero)
    private boolean flag; // Boleano necesario para Back Stack casero
    public static User logedUser; //Usuario que ha iniciado sesión, se necesitará en más zonas del código
    private BBDDHelper bdHelper;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicialización del gestor de la Base de datos local de SQLite
        bdHelper = new BBDDHelper(getApplicationContext());

        //Recuperar el usuario que ha iniciado sesión
        logedUser = bdHelper.getUserById(getIntent().getStringExtra("userId"));

        // Inicialización y recuperación de las variables y vistas
        bttmNav = findViewById(R.id.bttmNavView);
        idDeque = new ArrayDeque<>(3); // Lista con un número límite de almacenaje (permite que solo se puedan guardar X fragmentos en "memoria")
        flag = true;

        // Aplicación de funcionalidad a los items del Bottom Navigation Bar
        bttmNav.setOnItemSelectedListener(item -> {
            cargarFragmento(getFragment(addDeque(item.getItemId())));
            return true;
        });

        //Modifico la función del botón de Back del móvil
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                accionBack();
            }
        });

        //Dos lineas para comenzar el programa con el menú de Inicio
        getSupportFragmentManager().beginTransaction().replace(R.id.frLyt, new HomeFragment()).commit();
        idDeque.addLast(R.id.home_menu);
    }

    /**
     * Añade un id a una cola de un tamaño máximo para crear un Back Stack de tamaño limitado
     * @param id Id del item que se acciona
     * @return El id recibido
     */
    public int addDeque(int id){
        if(idDeque.contains(id)){
            if(id == R.id.home_menu && idDeque.size() != 1 && flag){ //Resumidamente: obliga a que el último fragmento que se muestra sea siempre el de inicio
                idDeque.addFirst(R.id.home_menu);
                flag = false;
            }
            idDeque.remove(id); //Elimina una antigua selección para no repetir (Back Stack no lo hace)
        }
        idDeque.push(id);
        return id;
    }

    /**
     * Elimina el fragmento que se estaba mostrando y muestra el anterior que se había añadido
     */
    public void accionBack(){
        idDeque.pop(); //Elimino el id del fragmento que se está mostrando
        if(!idDeque.isEmpty()){
            cargarFragmento(getFragment(idDeque.getFirst())); //Muestro el fragmento que se estaba mostrando anteriormente
        }else{
            finish(); // Si la lista está vacia se cierra el programa
        }
    }

    /**
     * Función que en función del id activará un item del Bottom Navigation View
     * @param id el id del item
     * @return fragmento correspondiente al item seleccionado; sino, el fragmento del menú de inicio
     */
    public Fragment getFragment(int id){
        if(id == R.id.home_menu){
            bttmNav.getMenu().getItem(0).setChecked(true);
            return new HomeFragment();
        }else if(id == R.id.search_menu){
            bttmNav.getMenu().getItem(1).setChecked(true);
            return new SearchFragment();
        }else if(id == R.id.message_menu){
            bttmNav.getMenu().getItem(2).setChecked(true);
            return new ChatsFragment();
        }else if(id == R.id.settings_menu){
            return new SettingsFragment();
        }else if(id == R.id.profile_menu){
            return new ProfileFragment();
        }else if(id == R.id.notificacion_menu){
            return new NotificationFragment();
        }else if(id == R.id.leftBtn){
            return new EditProfileFragment();
        }else if(id == R.id.aniadirFab){
            return new PostFragment();
        }else if(id == R.id.sendBtn){
            return new ChatFragment();
        }

        bttmNav.getMenu().getItem(0).setChecked(true);
        return new HomeFragment();
    }

    /**
     * Función que busca al fragmento que está visible
     * @return el fragmento visible; si no hay un null
     */
    private Fragment getVisibleFragment(){
        List<Fragment> fragmentos = getSupportFragmentManager().getFragments();
        for (Fragment f:fragmentos)
            if(f.isVisible())
                return f;

        return null;
    }

    /**
     * Método que cambia el fragmento que se está mostrando en el MainActivity
     * (no lo cambiará si ya se está mostrando)
     * @param f El fragmento que se quiere mostrar
     */
    public void cargarFragmento(Fragment f) {
        // Llama a un método que devuelve el fragmento que se está mostrando actualmente
        // y lo compara con el que se recibe por parámetro, si son de la misma clase no hace nada;
        // sino, cambia el fragmento que se muestra
        if(!getVisibleFragment().getClass().equals(f.getClass()))
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out) //Le da animación al cambio de fragmentos
                    .replace(R.id.frLyt, f).commit();

    }

    //Para indicarle al Bottom Navigation View qué menú usar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_tb_menu, menu);
        return true;
    }

    //Acción para el ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); //Recupera el id del MenuItem
        Fragment fr = null;

        //Dependiendo del id se cargará un fragmento u otro
        if(id == R.id.notificacion_menu){
            fr = new NotificationFragment();
        }else if(id == R.id.profile_menu){
            fr = new ProfileFragment();
        }else if(id == R.id.settings_menu){
            fr = new SettingsFragment();
        }

        if(fr != null){
            addDeque(id);
            getSupportFragmentManager()
                    .beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    .replace(R.id.frLyt, fr).commit();
        }else{
            accionBack(); //Si no es ninguno de ellos, se entiende que es el item de retroceso
        }

        return super.onOptionsItemSelected(item);
    }

    public void verPerfil(int userId){
        ProfileFragment fgt = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId+"");
        fgt.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.frLyt, fgt).commit();
    }

    /**
     * Muestra el fragmento de editar el perfil
     */
    public void editarPerfil(){
        addDeque(R.id.leftBtn);
        getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.frLyt, new EditProfileFragment()).commit();
    }

    /**
     * Muestra el fragmento para crear una publicación
     * @param type dependiendo del botón que haya accionado el método se pasará por bundle
     *             un tipo u otro de post
     */
    public void crearPublicacion(PostTypes type){
        addDeque(R.id.aniadirFab);
        Bundle bundle = new Bundle();
        bundle.putString("type", type.toString());

        PostFragment fgt = new PostFragment();
        fgt.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.frLyt, fgt).commit();
    }

    /**
     * Oculta el Bottom Navigation y actualiza el ConstraintLayout del Frame
     * para ocupar toda la pantalla.
     */
    public void desactivarBtnNav(){
        ConstraintLayout cLyt = findViewById(R.id.constraintLyt);
        ConstraintSet cSet = new ConstraintSet();
        cSet.clone(cLyt);

        cSet.connect(R.id.frLyt, ConstraintSet.BOTTOM, R.id.constraintLyt,ConstraintSet.BOTTOM);
        cSet.applyTo(cLyt);
        bttmNav.setVisibility(View.INVISIBLE);
    }
    /**
     * Muestra el Bottom Navigation y actualiza el ConstraintLayout del Frame
     * para ocupar hasta el inicio del Bottom Navigation Menu.
     */
    public void activarBtnNav(){
        ConstraintLayout cLyt = findViewById(R.id.constraintLyt);
        ConstraintSet cSet = new ConstraintSet();
        cSet.clone(cLyt);

        cSet.connect(R.id.frLyt, ConstraintSet.BOTTOM, R.id.bttmNavView,ConstraintSet.TOP);
        cSet.applyTo(cLyt);
        bttmNav.setVisibility(View.VISIBLE);
    }

    /**
     * Recupera de FireBase una imagen de perfil publicado, sino se carga una imagen predeterminada
     * @param imgPath Ruta donde se almacena la imagen en FireBase
     * @param imgV El Imagen View sobre el que se va a cargar la imagen de FireBase
     */
    public void cargarProfilePic(String imgPath, ImageView imgV) {
        StorageReference imgRef = stRef.child(imgPath);
        final long EIGHT_MEGABYTE = 1024*1024*8;
        imgRef.getBytes(EIGHT_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            imgV.setImageBitmap(bitmap);
        });
    }

    public void enviarMensaje(User user) {
        addDeque(R.id.sendBtn);
        int chatId = bdHelper.getChatId(logedUser, user);
        ChatFragment fgt = new ChatFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("userId", user.getId());
        bundle.putInt("chatId", chatId);

        fgt.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.frLyt, fgt).commit();
    }
}