package com.taller3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
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
    static final int CAMERA_REQUEST = 1;
    static final int GALLERY_REQUEST = 2;
    static final String permCamera = Manifest.permission.CAMERA;
    static final String permGallery = Manifest.permission.READ_EXTERNAL_STORAGE;
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
                                    .setDisplayName(displayName).build();
                            //.setPhotoUri(Uri.parse("https://example.com/algo"))
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        Log.d("UPDATE","account updated: " + user.getDisplayName());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profilePicture.setImageBitmap(imageBitmap);
                }
                break;
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profilePicture.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void launchGallery(View v){
        if(ContextCompat.checkSelfPermission(this, permCamera) != PackageManager.PERMISSION_GRANTED){
            requestPermission(this,permCamera,"We need permission to access storage",CAMERA_REQUEST);
        }
        else{
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage,GALLERY_REQUEST);
        }
    }

    public void launchCamera(View v){
        if(ContextCompat.checkSelfPermission(this, permCamera) != PackageManager.PERMISSION_GRANTED){
            requestPermission(this,permCamera,"We need permission to access storage",CAMERA_REQUEST);
        }
        else{
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try{
                startActivityForResult(takePicture,CAMERA_REQUEST);
            }catch (ActivityNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try{
                startActivityForResult(takePicture,CAMERA_REQUEST);
            }catch (ActivityNotFoundException e){
                e.printStackTrace();
            }
        }
        else if(requestCode == GALLERY_REQUEST){
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage,GALLERY_REQUEST);
        }
    }

    private String validateAll() {
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
        name.getText().toString().isEmpty() || lastName.getText().toString().isEmpty())
            return "Los campos no están completos";
        String psw = password.getText().toString();
        if(psw.length() < 6)
            return "La contraseña debe tener por lo menos 6 caracteres";
        if(profilePicture.getDrawable() == null)
            return "Seleccione una foto de perfil";
        return "All Good";
    }

    private void requestPermission(Activity context, String permission, String justification, int id){
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            // request the permission.
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
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