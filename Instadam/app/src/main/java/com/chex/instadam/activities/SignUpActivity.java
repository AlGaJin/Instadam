package com.chex.instadam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;

import java.sql.Date;
import java.util.Calendar;

/**
 * Da funcionalidad a la vista de Registro
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditTxt, usernameEditTxt, pwdEditTxt, confirmPwdEditTxt; //Campos para escribir los datos necesarios para registrarse
    private BBDDHelper bdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide(); //Oculta el toolbar porque no interesa ser mostrado en esta actividad

        bdHelper = new BBDDHelper(getApplicationContext());

        //Recuperación de los elementos de la vista
        emailEditTxt = findViewById(R.id.regEmailEditTxt);
        usernameEditTxt = findViewById(R.id.regUsernameEditTxt);
        pwdEditTxt = findViewById(R.id.regPwdEditTxt);
        confirmPwdEditTxt = findViewById(R.id.confirmPwdEditTxt);

        Button loginBtn = findViewById(R.id.regLoginBtn);
        loginBtn.setOnClickListener(view -> { //Acción para el botón de inicio de sesión, cambia la actividad
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        //Recuperación y acción para el botón de registro
        Button signUpBtn = findViewById(R.id.regSignUpBtn);
        signUpBtn.setOnClickListener(view -> {
            //Recuperación de los datos escritos en los diferentes campos
            String username = usernameEditTxt.getText().toString();
            String email = emailEditTxt.getText().toString();
            String pwd = pwdEditTxt.getText().toString();
            String confPwd = confirmPwdEditTxt.getText().toString();

            if(username.isEmpty() || pwd.isEmpty() || confPwd.isEmpty() || email.isEmpty()){
                Toast.makeText(this, R.string.rellena_campos, Toast.LENGTH_SHORT).show(); //Si no están todos los campos rellenos se le notifica al usuario
            }else{
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //Comprueba si el email tiene un formato correcto ex@amp.le
                    if(pwd.equals(confPwd)){
                        switch (bdHelper.insertUser(username, pwd, email)){ //Se usa la función de insertUser que devuelve un número según si:
                            case 0://Se ha creado el usuario correctamente
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent); //Se cambia a la actividad de inicio de sesión con el nombre de usuario como extra
                                finish();
                                break;
                            case 1: //El nombre de usuario no está disponible
                                usernameEditTxt.setError(getString(R.string.username_error));
                                break;
                            case 2: //El emial ya ha sido registrado
                                emailEditTxt.setError(getString(R.string.email_error));
                                break;
                        }
                    }else{
                        pwdEditTxt.setText("");
                        confirmPwdEditTxt.setText("");
                        pwdEditTxt.setError(getString(R.string.error_pwd));
                    }
                }else{
                    emailEditTxt.setError(getString(R.string.email_regex));
                }
            }
        });
    }
}