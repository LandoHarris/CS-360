package com.example.cs_360inventory_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        usernameEditText = findViewById(R.id.editText);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.LoginButton);
        Button createUserButton = findViewById(R.id.CreateUserButton);

        userDatabaseHelper = new UserDatabaseHelper(this);

        loginButton.setOnClickListener(v -> loginUser());
        createUserButton.setOnClickListener(v -> createUser());
        }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userDatabaseHelper.isUserExist(username)) {
            Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
        } else if (!userDatabaseHelper.isPasswordCorrect(username, password)) {
            Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show();
        } else {
            // Close the keyboard before starting MainActivity
            hideKeyboard();

            // Login successful, navigate to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void createUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDatabaseHelper.isUserExist(username)) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
        } else {
            userDatabaseHelper.addUser(username, password);
            Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
