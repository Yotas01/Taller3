package com.taller3.Activities;

import androidx.annotation.NonNull;
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
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taller3.Module.User;
import com.taller3.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

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
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference ref;
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
                if (!check.equals("All Good")) {
                    Toast.makeText(getApplicationContext(), check, Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("REGISTER","los campos están bien");
                //Create user
                String mail = email.getText().toString();
                String psw = password.getText().toString();
                Log.d("REGISTER","Se va a registrar");
                mAuth.createUserWithEmailAndPassword(mail, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("REGISTER","Entró al on Complete");
                                // Sign in success, update UI with the signed-in user's information
                                if(task.isSuccessful()) {
                                    FirebaseUser usr = mAuth.getCurrentUser();
                                    Log.d("REGISTER","isSuccesful");
                                    Toast.makeText(SignUpActivity.this, "Si se creo la cuenta",
                                            Toast.LENGTH_SHORT).show();
                                    User user = new User();
                                    user.setName(name.getText().toString());
                                    user.setLastName(lastName.getText().toString());
                                    user.setEmail(email.getText().toString());
                                    user.setPhoto(uploadPhoto());
                                    user.setState("away");
                                    ref.child(mAuth.getCurrentUser().getUid()).setValue(user);
                                }
                                else {
                                    Log.d("REGISTER","ELSE");
                                    Toast.makeText(SignUpActivity.this,
                                            "No se ha creado la cuenta" + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //Ask the user to log in
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            }
        });
    }

    private String uploadPhoto() {
        profilePicture.setDrawingCacheEnabled(true);
        profilePicture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        String uri = "images/" + UUID.randomUUID().toString();
        UploadTask uploadTask = storageReference.child(uri).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Hubo un error subiendo la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignUpActivity.this, "Se subió la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return uri;
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
        if(ContextCompat.checkSelfPermission(this, permGallery) != PackageManager.PERMISSION_GRANTED){
            requestPermission(this,permGallery,"We need permission to access storage",GALLERY_REQUEST);
        }
        else{
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage,GALLERY_REQUEST);
        }
    }

    public void launchCamera(View v){
        if(ContextCompat.checkSelfPermission(this, permCamera) != PackageManager.PERMISSION_GRANTED){
            requestPermission(this,permCamera,"We need permission to access camera",CAMERA_REQUEST);
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
    }

}