package com.example.finalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private Button loginButton, accountButton;
    private String email, password;

    private StorageReference mStorageRef;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        accountButton = findViewById(R.id.account);

        mUser = null;
    }

    public void createAccountClick(View view){
        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                (MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),
                                    "New Account Created.",Toast.LENGTH_SHORT).show();
                            mUser = mAuth.getCurrentUser();
                            Intent pull = new Intent(MainActivity.this,
                                    TextCapture.class);
                            startActivity(pull);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Account Creation Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.raw.icons8training96)
                .setContentTitle("Welcome!")
                .setContentText(email + " is the current user.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1234, builder.build());
    }

    public void loginClick(View view) {
        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener
                (MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),
                                    "Login Successful.",Toast.LENGTH_SHORT).show();
                            mUser = mAuth.getCurrentUser();
                            Intent pull = new Intent(MainActivity.this,
                                    TextCapture.class);
                            startActivity(pull);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Incorrect Email/Password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.raw.icons8training96)
                .setContentTitle("Welcome!")
                .setContentText(email + " is the current user.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1234, builder.build());
    }

}
