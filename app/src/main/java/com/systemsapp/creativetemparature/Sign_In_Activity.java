package com.systemsapp.creativetemparature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Sign_In_Activity extends AppCompatActivity {

    EditText emaileText, passText;
    Button btn_submite;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        emaileText = (EditText) findViewById(R.id.edit_email);
        passText = (EditText) findViewById(R.id.editPassowrd);
        btn_submite = (Button) findViewById(R.id.btn_sent);

        btn_submite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Registration();

            }
        });
    }

    private void Registration() {

        String email = emaileText.getText().toString().trim();
        String pass = passText.getText().toString().trim();

        if (email.isEmpty()) {

            emaileText.setError("Please enter your email,,,!");
            emaileText.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emaileText.setError("Enter the valid email,,,!");
            emaileText.requestFocus();
            return;
        }


        if (pass.length() <= 6) {
            passText.setError("Enter the password minimum 6 number,,,!");
            passText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Sign_In_Activity.this, "You Are Verified", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Sign_In_Activity.this, "Some Eror", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
