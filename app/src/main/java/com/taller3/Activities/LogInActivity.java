package com.taller3.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.taller3.R;

public class LogInActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    TextView signup;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inflate();

        if (mAuth.getCurrentUser() != null)
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

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
                String check = validateAll();
                if(!check.equals("All Good")) {
                    Toast.makeText(LogInActivity.this,check,Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this,"Se inició sesión",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else
                            Toast.makeText(LogInActivity.this,"Hubo un error iniciando sesión",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private String validateAll() {
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty())
            return "Los campos no están completos";
        String psw = password.getText().toString();
        if(psw.length() < 6)
            return "La contraseña debe tener por lo menos 6 caracteres";
        return "All Good";
    }
    private void inflate() {
        email = findViewById(R.id.eTEmailLogIn);
        password = findViewById(R.id.eTPswLogin);
        login = findViewById(R.id.buttonLogIn);
        signup = findViewById(R.id.tVRegister);
        mAuth = FirebaseAuth.getInstance();
    }
}