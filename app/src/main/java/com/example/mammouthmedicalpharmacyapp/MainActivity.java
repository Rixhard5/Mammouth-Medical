package com.example.mammouthmedicalpharmacyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import com.example.mammouthmedicalpharmacyapp.ui.login.LoginFragment;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 55;
    private FirebaseAuth firebaseAuthInstance;
    private GoogleSignInClient googleSignInClientInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuthInstance = FirebaseAuth.getInstance();

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            LoginFragment loginFragment = new LoginFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim01, 0)
                    .add(R.id.fragmentContainer, loginFragment)
                    .addToBackStack(null)
                    .commit();
            FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
            fragmentContainer.bringToFront();
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("254412408509-emcn8s9uf77v94b0rmd9v3bf00ekeeua.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClientInstance = GoogleSignIn.getClient(this, gso);

    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(LOG_TAG, "Account id with google login:" + account.getId());
            Toast.makeText(MainActivity.this, "Google login successful: " + account.getEmail(), Toast.LENGTH_LONG).show();
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(LOG_TAG, "Google sign in failed: ", e);
            Toast.makeText(MainActivity.this, "Google login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuthInstance.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(LOG_TAG, "signInWithCredential:success");
                gotoShopPage();
            } else {
                // If sign in fails, display a message to the user.
                Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

    public void gotoShopPage() {
        // TODO: create ShopPage component
        // Intent intent = new Intent(this, ShopListActivity.class);
        // startActivity(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                } else {
                    Toast.makeText(MainActivity.this, "Internal program error: " + result.getResultCode(), Toast.LENGTH_LONG).show();
                }
            }
    );

    public void loginWithGoogle(View view) {
        Intent signInIntent = googleSignInClientInstance.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    public void loginAnonymously(View view) {
        firebaseAuthInstance.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User login anonymously!");
                gotoShopPage();
            } else {
                Log.d(LOG_TAG, "User login anonymously failed!");
                Toast.makeText(MainActivity.this, "User login anonymously failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}