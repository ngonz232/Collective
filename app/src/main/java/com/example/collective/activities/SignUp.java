package com.example.collective.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.collective.R;
import com.example.collective.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    // Variable to initialize binding
    private ActivitySignUpBinding binding;
    Button register2;
    EditText username;
    EditText password;
    EditText address;
    EditText email;
    EditText fullName;
    EditText phoneNumber;
    EditText dateOfBirth;
    RadioButton maleCheck;
    RadioButton femaleCheck;
    // String to hold gender value from checkbox
    String gender = new String("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Binding all items
        register2 = binding.register2;
        username = binding.usernameR;
        password = binding.passwordR;
        address = binding.Address;
        email = binding.email;
        fullName = binding.fullName;
        phoneNumber = binding.phoneNumber;
        dateOfBirth = binding.dateOfBirth;
        maleCheck = binding.maleCheck;
        femaleCheck = binding.femaleCheck;

        maleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set gender string and store it in Parse
                if (maleCheck.isChecked()) {
                    gender = "male";
                } else if (femaleCheck.isChecked()) {
                    gender = "female";
                }
            }
        });

        register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
// Set core properties
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                user.setEmail(email.getText().toString());
// Set custom properties
                user.put("phoneNumber", phoneNumber.getText().toString());
                user.put("fullName", fullName.getText().toString());
                user.put("Address", address.getText().toString());
                user.put("dateOfBirth", dateOfBirth.getText().toString());
                user.put("gender",gender);

// Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(),"Sign Up Successful!",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Sign Up Error. Check ParseException Log",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                finish();
            }
        });

    }
}