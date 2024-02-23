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

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditTxt, usernameEditTxt, pwdEditTxt, confirmPwdEditTxt;
    private BBDDHelper bdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        bdHelper = new BBDDHelper(getApplicationContext());

        emailEditTxt = findViewById(R.id.regEmailEditTxt);
        usernameEditTxt = findViewById(R.id.regUsernameEditTxt);
        pwdEditTxt = findViewById(R.id.regPwdEditTxt);
        confirmPwdEditTxt = findViewById(R.id.confirmPwdEditTxt);

        Button loginBtn = findViewById(R.id.regLoginBtn);
        loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        Button signUpBtn = findViewById(R.id.regSignUpBtn);
        signUpBtn.setOnClickListener(view -> {
            String username = usernameEditTxt.getText().toString();
            String email = emailEditTxt.getText().toString();
            String pwd = pwdEditTxt.getText().toString();
            String confPwd = confirmPwdEditTxt.getText().toString();

            if(username.isEmpty() || pwd.isEmpty() || confPwd.isEmpty() || email.isEmpty()){
                Toast.makeText(this, R.string.rellena_campos, Toast.LENGTH_SHORT).show();
            }else{
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(pwd.equals(confPwd)){
                        switch (bdHelper.insertUser(username, pwd, email, new java.sql.Date(System.currentTimeMillis()))){
                            case 0:
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                usernameEditTxt.setError(getString(R.string.username_error));
                                break;
                            case 2:
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