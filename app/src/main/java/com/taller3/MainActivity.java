package com.taller3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView welcome;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflate();

        //Display user name
        welcome.setText("Welcome "+user.getUid());
    }

    private void inflate() {
        welcome = findViewById(R.id.textViewWelcome);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }
}