package com.example.collective.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.collective.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    // Variable to initialize binding
    private ActivitySignUpBinding binding;
    // Declaring all items
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
                // Set gender string and store it in Parse based on user selected checkbox
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
// Username converted to lowercase for case insensitivity
                user.setUsername(username.getText().toString().toLowerCase());
                user.setPassword(password.getText().toString());
                user.setEmail(email.getText().toString());
// Set custom properties
                user.put("phoneNumber", phoneNumber.getText().toString());
                user.put("fullName", fullName.getText().toString());
                user.put("Address", address.getText().toString());
                user.put("dateOfBirth", dateOfBirth.getText().toString());
                user.put("gender", gender);

               /* "If" statement checks if our earlier field validation method returns true before saving
               new user to Parse
               */
                if (checkValidation()) {
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "Sign Up Successful!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Sign Up Error. Check ParseException Log", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    finish();
                }
            }
        });

    }

    public boolean checkValidation() {

        // Nested "if" statements checking if each field is filled by the user before proceeding
        if (username.length() <= 0) {
            username.requestFocus();
            username.setError("Enter Username");
            return false;

        } else if (password.length() <= 0) {
            password.requestFocus();
            password.setError("Enter Password");
            return false;

        } else if (email.length() <= 0) {
            email.requestFocus();
            email.setError("Email");
            return false;

        } else if (phoneNumber.length() <= 0) {
            phoneNumber.requestFocus();
            phoneNumber.setError("Enter Phone Number");
            return false;

        } else if (address.length() <= 0) {
            address.requestFocus();
            address.setError("Enter Full Address");
            return false;

        } else if (dateOfBirth.length() <= 0) {
            dateOfBirth.requestFocus();
            dateOfBirth.setError("Enter Description");
            return false;

        } else if (!(maleCheck.isChecked() || femaleCheck.isChecked())) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_LONG).show();
            return false;

        } else {
            return true;
        }
    }
}
