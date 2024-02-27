package com.chex.instadam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditTxt, pwdEditTxt;
    private CheckBox rememberMe;
    private SharedPreferences preferences;
    private BBDDHelper bdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide(); // Oculta el ActionBar

        //Instanciación de la base de datos para comprobar el inicio de sesión
        bdHelper = new BBDDHelper(getApplicationContext());

        //Instanciación del SharedPreferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String userId = preferences.getString("userId", ""); //Recupero el id del usuario que se almacena en la clave userId
        //Compruebo que no esté vacío y que el usuario existe
        // para así cargar en el Main el id del usuario
        if(!userId.trim().isEmpty() && bdHelper.getUserById(userId) != null){
            toMainActivity(userId);
        }

        //Intanciación del CheckBox que se usará al iniciar sesión para saber si se quiere mantener la sesión iniciada
        rememberMe = findViewById(R.id.rememberCheckBox);

        //Instanciación de los EditText nombre y contraseña
        nameEditTxt = findViewById(R.id.loginNameEditTxt);
        pwdEditTxt = findViewById(R.id.loginPasswordEditTxt);

        //Si se viene del registro, se introducirá el nombre de usuario automáticamente
        String bundleUsername = getIntent().getStringExtra("username");
        if( bundleUsername != null){
            nameEditTxt.setText(bundleUsername);
        }

        //Acción para iniciar sesión con el botón de login
        findViewById(R.id.loginBtn).setOnClickListener(view -> login());

        //Acción para cambiar al Activity de registro con el botón SignUp
        findViewById(R.id.signUpBtn).setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

    }

    /**
     * Lanza el MainActivity y cierra este
     * @param userId Id del usuario que inicia sesión
     */
    public void toMainActivity(String userId){
        Intent intent = new Intent(this, MainActivity.class); //Se inicia la actividad
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    /**
     * Comprueba los EditText y si están rellenos correctamente inicia sesión,
     * lanzando el MainActivity
     */
    public void login(){
        //Recolección de lo escrito en los campos
        String name = nameEditTxt.getText().toString();
        String pwd = pwdEditTxt.getText().toString();

        if(name.isEmpty() || pwd.isEmpty()){
            Toast.makeText(this, getString(R.string.rellena_todos_los_campos), Toast.LENGTH_SHORT).show();
        }else{
            Integer userId = bdHelper.login_user(new String[]{name, name, pwd}); //Con el nombre de usuario/email y contraseñas correctos, se obtiene el id del usuario
            if(userId != null){
                if(rememberMe.isChecked()){ //Si se ha activado el CheckBox
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userId", userId+""); //Se almacena el id del usuario en el SharedPreferences
                    editor.apply();
                }

                toMainActivity(userId.toString());
            }else{
                pwdEditTxt.setText("");
                pwdEditTxt.setError(getString(R.string.login_error));
            }
        }
    }
}