package com.example.mammouthmedicalpharmacyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 55;

    EditText usernameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    EditText phoneEditText;
    EditText addressEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 55) {
            finish();
        }

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);

        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");

        usernameEditText.setText(username);
        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Passwords do not match!");
            return;
        }

        String phone = phoneEditText.getText().toString();
        // TODO: store other data

        Log.i(LOG_TAG, "Registered user: " + username + ", email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User created successfully in Firebase Database.");
                gotoShopPage();
            } else {
                Log.d(LOG_TAG, "Error while creating user!");
                Toast.makeText(RegisterActivity.this, "Error while creating user: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void gotoShopPage() {
        // TODO:
        // Intent intent = new Intent(this, ShopListActivity.class);
        // startActivity(intent);
    }
}