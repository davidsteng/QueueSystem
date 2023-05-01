package com.example.queuesystemsprint3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button sign_in;
    EditText emailEntry;
    EditText passwordEntry;
    String email;
    String password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        sign_in = (Button) findViewById(R.id.button2);
        emailEntry = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passwordEntry = (EditText) findViewById(R.id.editTextTextPassword);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEntry.getText().toString();
                password = passwordEntry.getText().toString();
                signInUser(email, password);
            }
        });
    }

    public void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User signed in
                            System.out.println("User signed in!!");

                            loadQueue();
                        } else {
                            System.out.println("Login failed");
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            // Open another activity with the current user
//        }
    }

    public void loadQueue() {
        System.out.println("Change the activity");
        Intent changeActivity = new Intent(this, MainActivity.class);
        changeActivity.putExtra("email", email);
        startActivity(changeActivity);
    }
}