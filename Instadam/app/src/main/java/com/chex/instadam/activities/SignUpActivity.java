package com.chex.instadam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chex.instadam.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditTxt, usernameEditTxt, pwdEditTxt, confirmPwdEditTxt;
    private Button signUpBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditTxt = findViewById(R.id.regEmailEditTxt);
        usernameEditTxt = findViewById(R.id.regUsernameEditTxt);
        pwdEditTxt = findViewById(R.id.regPwdEditTxt);
        confirmPwdEditTxt = findViewById(R.id.confirmPwdEditTxt);

        loginBtn = findViewById(R.id.regLoginBtn);
        loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        signUpBtn = findViewById(R.id.regSignUpBtn);
        signUpBtn.setOnClickListener(view -> {
            String username = usernameEditTxt.getText().toString();
            String email = emailEditTxt.getText().toString();
            String pwd = pwdEditTxt.getText().toString();
            String confPwd = confirmPwdEditTxt.getText().toString();

            if(username.isEmpty() || pwd.isEmpty() || confPwd.isEmpty() || email.isEmpty()){
                Toast.makeText(this, R.string.rellena_campos, Toast.LENGTH_SHORT).show();
            }else{
                if(pwd.equals(confPwd)){
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }else{
                    pwdEditTxt.setText("");
                    confirmPwdEditTxt.setText("");
                    pwdEditTxt.setError(getString(R.string.error_pwd));
                }
            }
        });
    }
}