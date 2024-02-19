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
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.java.User;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditTxt, pwdEditTxt;
    private Button loginBtn, signupBtn;
    private BBDDHelper bdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        bdHelper = new BBDDHelper(getApplicationContext());

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
                Integer userId = bdHelper.login_user(new String[]{name, name, pwd});
                if(userId != null){
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("userId", userId);
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