package com.taller3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SignUpActivity extends AppCompatActivity {

    EditText name;
    EditText lastName;
    EditText email;
    EditText password;
    ImageView profilePicture;
    ImageButton camera;
    ImageButton gallery;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inflate();

        //Crear cuenta
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Verificar campos y crear cuenta
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void inflate() {
        name = findViewById(R.id.eTNameSigUp);
        lastName = findViewById(R.id.eTLastNameSignUp);
        email = findViewById(R.id.eTEmailSignUp);
        password = findViewById(R.id.eTPswSignUp);
        profilePicture = findViewById(R.id.imageViewProfileSignUp);
        camera = findViewById(R.id.imageButtonCameraSignUp);
        gallery = findViewById(R.id.imageButtonGallerySignUp);
        signUp = findViewById(R.id.buttonSignUp);
    }

}