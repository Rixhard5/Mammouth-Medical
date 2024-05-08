package com.example.mammouthmedicalpharmacyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import java.util.Objects;

public class MainShopList extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Objects.requireNonNull(menu.getItem(0).getSubMenu()).removeItem(R.id.shopping_page);
        if (firebaseUser == null) {
            Objects.requireNonNull(menu.getItem(0).getSubMenu()).removeItem(R.id.profile);
            Objects.requireNonNull(menu.getItem(0).getSubMenu()).removeItem(R.id.shopping_cart);
            Objects.requireNonNull(menu.getItem(0).getSubMenu()).removeItem(R.id.logout);
        } else {
            Objects.requireNonNull(menu.getItem(0).getSubMenu()).removeItem(R.id.home_page);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home_page) {
            startNewActivity(MainActivity.class);
        } else if (itemId == R.id.shopping_cart) {
            startNewActivity(ShoppingCart.class);
        } else if (itemId == R.id.profile) {
            startNewActivity(Profile.class);
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_LONG).show();
            startNewActivity(MainActivity.class);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_shop_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shopListActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mammouth Medical");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    private void startNewActivity(Class<?> destinationClass) {
        Intent intent = new Intent(getApplicationContext(), destinationClass);
        startActivity(intent);
        finish();
    }
}