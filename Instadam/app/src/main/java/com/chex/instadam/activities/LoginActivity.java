package com.chex.instadam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chex.instadam.R;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditTxt, pwdEditTxt;
    private Button loginBtn, signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditTxt = findViewById(R.id.loginNameEditTxt);
        String bundleUsername = getIntent().getStringExtra("username");
        if( bundleUsername != null){
            nameEditTxt.setText(bundleUsername);
        }

        pwdEditTxt = findViewById(R.id.loginPasswordEditTxt);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(view -> {
            String name = nameEditTxt.getText().toString();
            String pwd = pwdEditTxt.getText().toString();

            if(name.isEmpty() || pwd.isEmpty()){
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }else{
                if(name.equals("admin") && pwd.equals("admin")){
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }
        });

        signupBtn = findViewById(R.id.signUpBtn);
        signupBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

    }
}