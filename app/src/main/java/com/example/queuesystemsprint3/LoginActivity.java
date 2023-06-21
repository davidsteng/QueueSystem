package com.example.queuesystemsprint3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button sign_in;
    EditText emailEntry;
    EditText passwordEntry;
    String email;
    String password;
    private FirebaseAuth mAuth;

    EditText firstName;
    EditText lastName;
    TextView firstNameLabel;
    TextView lastNameLabel;

    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        sign_in = (Button) findViewById(R.id.button2);
        emailEntry = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passwordEntry = (EditText) findViewById(R.id.editTextTextPassword);
        db = FirebaseFirestore.getInstance();


        // Enabling extra text boxes
        firstNameLabel = (TextView) findViewById(R.id.firststname);
        lastNameLabel = (TextView) findViewById(R.id.lastname);
        firstName = (EditText) findViewById(R.id.editTextFirstName);
        lastName = (EditText) findViewById(R.id.editTextLastName);
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

                            loadQueue(email);
                        } else {
                            System.out.println("Login failed");
                            firstName.setVisibility(View.VISIBLE);
                            lastName.setVisibility(View.VISIBLE);
                            firstNameLabel.setVisibility(View.VISIBLE);
                            lastNameLabel.setVisibility(View.VISIBLE);
                            sign_in.setOnClickListener(null);
                            sign_in.setText("Register");
                            sign_in.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String firstNameText = firstName.getText().toString();
                                    String lastNameText = lastName.getText().toString();

                                    createNewUser(email, password, firstNameText, lastNameText);
                                }
                            });

                        }
                    }
                });
    }

    public void createNewUser(String email, String password, String firstName, String lastName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            CollectionReference ref = db.collection("Students");
                            Map<String, Object> entry = new HashMap<>();
                            entry.put("CourseList", new ArrayList<String>());
                            entry.put("Name", firstName + " " + lastName);
                            entry.put("TACurrentHelping", "");
                            entry.put("TimeQueueEnter", "");
                            entry.put("inQueue", Boolean.FALSE);
                            entry.put("inQueueFor", "");
                            entry.put("isTA", Boolean.FALSE);
                            entry.put("isTAFor", "");
                            entry.put("reason", "");

                            ref.document(email).set(entry);
                            loadQueue(email);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            System.out.println("The login failed!");
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

    public void loadQueue(String passedInEmail) {
        System.out.println("Change the activity");
        Intent changeActivity = new Intent(this, MainActivity.class);
        changeActivity.putExtra("email", passedInEmail);
        startActivity(changeActivity);
    }
}