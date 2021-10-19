package com.taller3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inflate();

        //"Boton" para crear cuenta
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
        //Boton iniciar sesión
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Autenticar
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void inflate() {
        email = findViewById(R.id.eTEmailLogIn);
        password = findViewById(R.id.eTPswLogin);
        login = findViewById(R.id.buttonLogIn);
        signup = findViewById(R.id.tVRegister);
    }
}