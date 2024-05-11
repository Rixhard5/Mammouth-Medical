package com.example.mammouthmedicalpharmacyapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mammouthmedicalpharmacyapp.Adapters.CartItemAdapter;
import com.example.mammouthmedicalpharmacyapp.Model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Objects;

public class ShoppingCart extends AppCompatActivity {
    private static final String LOG_TAG = ShoppingCart.class.getName();

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestoreDb;
    CollectionReference cartRef;

    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private ArrayList<CartItem> cartItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shoppingCartActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestoreDb = FirebaseFirestore.getInstance();
        cartRef = firebaseFirestoreDb.collection("Cart");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mammouth Medical");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();

        cartItemAdapter = new CartItemAdapter(this, cartItems);
        recyclerView.setAdapter(cartItemAdapter);

        loadCartItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear_cart, menu);
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
                R.id.shopping_cart
        };

        for (int id : itemIds) {
            subMenu.removeItem(id);
        }
        menu.removeItem(R.id.search_bar);
        menu.removeItem(R.id.view_selector);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.clear_cart) {
            clearCart();
        } else if (itemId == R.id.shopping_page) {
            startNewActivity(MainShopList.class);
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

    private void loadCartItems() {
        cartRef.whereEqualTo("userId", firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String itemId = document.getString("itemId");
                            String userId = document.getString("userId");
                            Integer itemCount = Math.toIntExact(document.getLong("itemCount"));

                            firebaseFirestoreDb.collection("PharmacyItems")
                                    .whereEqualTo("itemId", itemId)
                                    .get()
                                    .addOnCompleteListener(innerTask -> {
                                        if (innerTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot itemDocument : innerTask.getResult()) {
                                                String itemName = itemDocument.getString("name");
                                                String itemImage = itemDocument.getString("imageResource");

                                                if (itemName != null && itemImage != null) {
                                                    CartItem cartItem = new CartItem(userId, itemId, itemName, itemImage, itemCount);
                                                    cartItems.add(cartItem);

                                                    cartItemAdapter.notifyDataSetChanged();
                                                    Log.w(LOG_TAG, "Document found successfully: " + cartItem.getName());
                                                } else {
                                                    Log.w(LOG_TAG, "Document exists but missing expected fields.");
                                                }
                                            }
                                        } else {
                                            Log.w(LOG_TAG, "No document found for itemId: " + itemId);
                                        }
                                    });
                        }
                    } else {
                        Log.w(LOG_TAG, "Error getting documents.", task.getException());
                    }
                });

    }

    private void startNewActivity(Class<?> destinationClass) {
        Intent intent = new Intent(getApplicationContext(), destinationClass);
        startActivity(intent);
        finish();
    }

    public void deleteCartItem(CartItem currentItem) {
        cartRef.whereEqualTo("itemId", currentItem.getItemId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete().addOnSuccessListener(aVoid -> {
                                cartItems.remove(currentItem);
                                cartItemAdapter.notifyDataSetChanged();
                            });
                        }
                        Log.d(LOG_TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(this, "Item removed successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Log.w(LOG_TAG, "Error deleting document: " + task.getException());
                        Toast.makeText(this, "Error removing item: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void deleteOneItem(CartItem currentItem) {
        Log.w(LOG_TAG, "CURRENT ITEM COUNT: " + currentItem.getItemCount());
        if (currentItem.getItemCount() > 1) {
            cartRef.whereEqualTo("itemId", currentItem.getItemId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update("itemCount", FieldValue.increment(-1)).addOnSuccessListener(aVoid -> {
                                    currentItem.setItemCount(currentItem.getItemCount() - 1);
                                    cartItemAdapter.notifyDataSetChanged();
                                });
                            }
                            Log.d(LOG_TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(this, "1 item removed successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(LOG_TAG, "Error updating document: " + task.getException());
                            Toast.makeText(this, "Error removing 1 item: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            deleteCartItem(currentItem);
        }
    }

    private void clearCart() {
        cartRef.whereEqualTo("userId", firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch writeBatch = firebaseFirestoreDb.batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            writeBatch.delete(document.getReference());
                        }
                        writeBatch.commit().addOnCompleteListener(writeTask -> {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "Documents successfully cleared!");
                                Toast.makeText(this, "Cart cleared successfully!", Toast.LENGTH_LONG).show();
                                cartItems.clear();
                                cartItemAdapter.notifyDataSetChanged();
                            } else {
                                Log.w(LOG_TAG, "Error clearing the document: " + task.getException());
                                Toast.makeText(this, "Error clearing the cart: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.w(LOG_TAG, "Error getting the document: " + task.getException());
                    }
                });
    }
}
