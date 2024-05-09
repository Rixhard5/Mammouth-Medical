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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mammouthmedicalpharmacyapp.Adapters.ItemAdapter;
import com.example.mammouthmedicalpharmacyapp.Model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainShopList extends AppCompatActivity {
    private static final String LOG_TAG = MainShopList.class.getName();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestoreDb;
    CollectionReference pharmacyItemsRef;

    private RecyclerView recyclerView;
    private ArrayList<Item> itemList;
    private ItemAdapter itemAdapter;

    private int gridNumber = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestoreDb = FirebaseFirestore.getInstance();
        pharmacyItemsRef = firebaseFirestoreDb.collection("PharmacyItems");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mammouth Medical");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        itemList = new ArrayList<>();

        itemAdapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(itemAdapter);

        initilizeData();
    }

    private void initilizeData() {
        pharmacyItemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> idList = new ArrayList<>();
                List<String> itemsTitle = new ArrayList<>();
                List<String> itemsDescription = new ArrayList<>();
                List<String> itemsPrice = new ArrayList<>();
                List<String> itemsCategory = new ArrayList<>();
                List<String> itemsImageResource = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    idList.add(document.getString("itemId"));
                    itemsTitle.add(document.getString("name"));
                    itemsDescription.add(document.getString("details"));
                    itemsPrice.add(document.getString("price"));
                    itemsCategory.add(document.getString("category"));
                    itemsImageResource.add(document.getString("imageResource"));
                }

                itemList.clear();

                String[] itemsIdArray = idList.toArray(new String[0]);
                String[] itemsTitleArray = itemsTitle.toArray(new String[0]);
                String[] itemsDescriptionArray = itemsDescription.toArray(new String[0]);
                String[] itemsPriceArray = itemsPrice.toArray(new String[0]);
                String[] itemsCategoryArray = itemsCategory.toArray(new String[0]);
                String[] itemsImageResourceArray = itemsImageResource.toArray(new String[0]);

                for (int i = 0; i < itemsTitleArray.length; i++) {
                    itemList.add(
                            new Item(
                                    itemsIdArray[i],
                                    itemsTitleArray[i],
                                    itemsDescriptionArray[i],
                                    itemsCategoryArray[i],
                                    itemsPriceArray[i],
                                    itemsImageResourceArray[i]
                            )
                    );
                }

                itemAdapter.notifyDataSetChanged();
            } else {
                Log.d(LOG_TAG, "Error getting documents: ", task.getException());
            }
        });


    }

    private void startNewActivity(Class<?> destinationClass) {
        Intent intent = new Intent(getApplicationContext(), destinationClass);
        startActivity(intent);
        finish();
    }
}