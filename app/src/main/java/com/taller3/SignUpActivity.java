package com.taller3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    EditText name;
    EditText lastName;
    EditText email;
    EditText password;
    ImageView profilePicture;
    ImageButton camera;
    ImageButton gallery;
    Button signUp;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inflate();

        //Create an account
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check all the fields
                String check = validateAll();
                if(!check.equals("All Good")) {
                    Toast.makeText(getApplicationContext(),check,Toast.LENGTH_LONG).show();
                    return;
                }
                //Create user
                String mail = email.getText().toString();
                String psw = password.getText().toString();
                mAuth.createUserWithEmailAndPassword(mail, psw)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignUpActivity.this, "Se ha creado la cuenta",
                                            Toast.LENGTH_SHORT).show();
                                    user = mAuth.getCurrentUser();
                                    String displayName = name.getText().toString() + "," + lastName.getText().toString();
                                    Log.d("UPDATE",displayName);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(displayName)
                                            //.setPhotoUri(Uri.parse("https://example.com/algo"))
                                            .build();
                                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                                Log.d("UPDATE","account updated");
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //Ask the user to log in
                startActivity(new Intent(getApplicationContext(),LogInActivity.class));
            }
        });
    }

    private String validateAll() {
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
        name.getText().toString().isEmpty() || lastName.getText().toString().isEmpty())
            return "Los campos no están completos";
        String psw = password.getText().toString();
        if(psw.length() < 6)
            return "La contraseña debe tener por lo menos 6 caracteres";
        //TODO: Get picture
        return "All Good";
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
        mAuth = FirebaseAuth.getInstance();
    }

}