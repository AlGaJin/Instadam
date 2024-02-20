package com.chex.instadam.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.fragments.ChatFragment;
import com.chex.instadam.fragments.HomeFragment;
import com.chex.instadam.R;
import com.chex.instadam.fragments.NotificationFragment;
import com.chex.instadam.fragments.ProfileFragment;
import com.chex.instadam.fragments.SearchFragment;
import com.chex.instadam.fragments.SettingsFragment;
import com.chex.instadam.java.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Declaración de variables que se van a usar en diferentes puntos del programa
    private BottomNavigationView bttmNav;
    private Deque<Integer> idDeque; // Lista "estática" que tiene funciones de listas dinámicas (útil para Back Stack casero)
    private boolean flag; // Boleano necesario para Back Stack casero
    public static User logedUser;
    private BBDDHelper bdHelper;

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
            return new ChatFragment();
        }else if(id == R.id.settings_menu){
            return new SettingsFragment();
        }else if(id == R.id.profile_menu){
            return new ProfileFragment();
        }else if(id == R.id.notificacion_menu){
            return new NotificationFragment();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fr = null;

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
                    .addToBackStack(null)
                    .replace(R.id.frLyt, fr).commit();
        }else{
            accionBack();
        }

        return super.onOptionsItemSelected(item);
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
}