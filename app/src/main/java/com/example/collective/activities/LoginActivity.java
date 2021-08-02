package com.example.collective.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.collective.databinding.ActivityLoginBinding;
import com.parse.ParseUser;


// Login Activity file
public class LoginActivity extends AppCompatActivity {

    // Variable to initialize binding
    private ActivityLoginBinding binding;
    // Register button
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Checks if user has already logged in to skip login process/persist login
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Binding the register button
        register = binding.register;

        // OnClickListener for register button to be taken to the sign up activity
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUp.class);
                startActivity(i);
            }
        });

    }

//    // Sets click listener to login button. This is referenced in the LoginActivity.xml
    public void loginOnClick(View v) {
        // Gets user inputs for both username and password passing it to the login method
        // Username converted to lowercase for case insensitivity
        String username = binding.username.getText().toString().toLowerCase();
        String password = binding.password.getText().toString();
        loginUser(username, password);
    }

    // Login method for parse
    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                // Toast to warn us if there is an issue with the entered credentials
                Toast.makeText(this, "Username or password is incorrect.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Calls intent method upon successful login to take the user into the app
            goMainActivity();
        });
    }

    // Intent method to take user into the app upon successful login
    private void goMainActivity() {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


}

