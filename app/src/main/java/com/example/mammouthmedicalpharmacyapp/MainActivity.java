package com.example.mammouthmedicalpharmacyapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private static final int RC_SIGN_IN = 44;
    private static final int SECRET_KEY = 55;
    private SharedPreferences preferences;
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

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        firebaseAuthInstance = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("254412408509-rkjau79gfjrsutdi65t58mika6cvqntt.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClientInstance = GoogleSignIn.getClient(this, gso);

    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        // Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(LOG_TAG, "Account id with google login:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(LOG_TAG, "Google sign in failed: ", e);
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

    public void login(View view) {
        // TODO:
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            }
    );

    public void loginWithGoogle(View view) {
        Intent signInIntent = googleSignInClientInstance.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO:
        SharedPreferences.Editor editor = preferences.edit();
        // editor.putString("userName", userNameET.getText().toString());
        // editor.putString("password", passwordET.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
}