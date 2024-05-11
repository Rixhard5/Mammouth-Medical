package com.example.mammouthmedicalpharmacyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Objects;

public class Profile extends AppCompatActivity {
    private static final String LOG_TAG = Profile.class.getName();

    EditText usernameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText addressEditText;
    String currentUserId;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    CollectionReference cartRef;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mammouth Medical");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        cartRef = firestoreDb.collection("Cart");

        firestoreDb.collection("Users").whereEqualTo("email", firebaseUser.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            currentUserId = doc.getId();
                            usernameEditText.setText(Objects.requireNonNull(doc.get("username")).toString());
                            emailEditText.setText(Objects.requireNonNull(doc.get("email")).toString());
                            phoneEditText.setText(Objects.requireNonNull(doc.get("phone")).toString());
                            addressEditText.setText(Objects.requireNonNull(doc.get("address")).toString());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        SubMenu subMenu = Objects.requireNonNull(menu.findItem(R.id.other_items).getSubMenu());
        int[] itemIds = {
                R.id.category_firstAid,
                R.id.category_coldEtc,
                R.id.category_hayFever,
                R.id.category_thrushTre,
                R.id.category_travelMed,
                R.id.separator,
                R.id.home_page,
                R.id.profile,
                R.id.clear_cart
        };

        int[] menuItemsInUse = {
                R.id.shopping_page,
                R.id.shopping_cart,
                R.id.logout
        };

        for (int id : itemIds) {
            subMenu.removeItem(id);
        }
        if (firebaseUser == null) {
            subMenu.removeItem(R.id.shopping_cart);
        }
        menu.removeItem(R.id.search_bar);
        menu.removeItem(R.id.view_selector);

        for (int id : menuItemsInUse) {
            if (menu.findItem(id) != null) {
                MainActivity.setMenuIconColor(menu.findItem(id), this);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.shopping_page) {
            startNewActivity(MainShopList.class);
        } else if (itemId == R.id.shopping_cart) {
            startNewActivity(ShoppingCart.class);
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_LONG).show();
            startNewActivity(MainActivity.class);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void remove(View view) {
        deleteFirebaseUser();
    }

    private void deleteFirebaseUser() {
        firebaseUser.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "User account deleted.");
                        clearCart();
                        deleteFirestoreUserDocument();
                    }
                });
    }

    private void clearCart() {
        cartRef.whereEqualTo("userId", firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch writeBatch = firestoreDb.batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            writeBatch.delete(document.getReference());
                        }
                        writeBatch.commit();
                    } else {
                        Log.w(LOG_TAG, "Error getting the document: " + task.getException());
                    }
                });
    }

    private void deleteFirestoreUserDocument() {
        firestoreDb.collection("Users").document(currentUserId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOG_TAG, "DocumentSnapshot successfully deleted!");
                    navigateToMainActivityWithMessage();
                })
                .addOnFailureListener(e -> Log.w(LOG_TAG, "Error deleting document", e));
    }

    private void navigateToMainActivityWithMessage() {
        Toast.makeText(Profile.this, "User deleted successfully!", Toast.LENGTH_LONG).show();
        startNewActivity(MainShopList.class);
        finish();
    }

    public void update(View view) {
        firestoreDb.collection("Users").document(currentUserId)
                .update("username", usernameEditText.getText().toString(),
                        "phone", phoneEditText.getText().toString(),
                        "address", addressEditText.getText().toString())
                .addOnSuccessListener(listener -> {
                    Log.d(LOG_TAG, "DocumentSnapshot successfully updated!");
                    Toast.makeText(Profile.this, "User updated successfully!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(LOG_TAG, "Error updating document", e);
                    Toast.makeText(Profile.this, "Error while updating the user:" + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void cancel(View view) {
        startNewActivity(MainShopList.class);
    }

    private void startNewActivity(Class<?> destinationClass) {
        Intent intent = new Intent(getApplicationContext(), destinationClass);
        startActivity(intent);
        finish();
    }
}
