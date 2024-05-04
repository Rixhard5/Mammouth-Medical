package com.example.mammouthmedicalpharmacyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainShopList extends AppCompatActivity {
    private static final String LOG_TAG = MainShopList.class.getName();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Log.d(LOG_TAG, "FIREBASE USER: " + firebaseUser);
        if (firebaseUser == null) {
            menu.removeItem(R.id.profile);
            menu.removeItem(R.id.shopping_cart);
            menu.removeItem(R.id.logout);
        } else {
            menu.removeItem(R.id.home_page);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent;
        if (itemId == R.id.home_page) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.shopping_page) {
            intent = new Intent(this, MainShopList.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.shopping_cart) {
            intent = new Intent(this, ShoppingCart.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.profile) {
            intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_shop_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mammouth Medical");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOverflowIcon(getDrawable(R.drawable.baseline_menu_24));
        setSupportActionBar(toolbar);
    }
}