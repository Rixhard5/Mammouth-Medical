package com.example.mammouthmedicalpharmacyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainShopList extends AppCompatActivity {
    private static final String LOG_TAG = MainShopList.class.getName();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestoreDb;
    CollectionReference pharmacyItemsRef;
    CollectionReference cartRef;

    private RecyclerView recyclerView;
    private ArrayList<Item> itemList;
    private ItemAdapter itemAdapter;

    private FrameLayout redCircle;
    private TextView contextTextView;

    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean viewRow = true;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        SubMenu subMenu = Objects.requireNonNull(menu.findItem(R.id.other_items).getSubMenu());

        int[] menuItemsInUse = {
                R.id.category_firstAid,
                R.id.category_hayFever,
                R.id.category_coldEtc,
                R.id.category_thrushTre,
                R.id.category_travelMed,
                R.id.shopping_cart,
                R.id.separator,
                R.id.profile,
                R.id.logout,
                R.id.home_page
        };

        subMenu.removeItem(R.id.shopping_page);
        subMenu.removeItem(R.id.clear_cart);
        if (firebaseUser == null) {
            subMenu.removeItem(R.id.profile);
            subMenu.removeItem(R.id.logout);
            subMenu.removeItem(R.id.shopping_cart);
        } else {
            subMenu.removeItem(R.id.home_page);
        }

        for (int id : menuItemsInUse) {
            if (menu.findItem(id) != null) {
                MainActivity.setMenuIconColor(menu.findItem(id), this);
            }
        }

        MenuItem searchItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchText.setTextColor(Color.WHITE);
        searchText.setHintTextColor(Color.WHITE);
        closeButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    private void updateItemList(String category) {
        Query query = pharmacyItemsRef.whereEqualTo("category", category)
                .orderBy("name")
                .limit(5);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemList.clear();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    itemList.add(
                            new Item(
                                    Objects.requireNonNull(documentSnapshot.getData().get("itemId")).toString(),
                                    Objects.requireNonNull(documentSnapshot.getData().get("name")).toString(),
                                    Objects.requireNonNull(documentSnapshot.getData().get("details")).toString(),
                                    Objects.requireNonNull(documentSnapshot.getData().get("category")).toString(),
                                    Objects.requireNonNull(documentSnapshot.getData().get("price")).toString(),
                                    Objects.requireNonNull(documentSnapshot.getData().get("imageResource")).toString()
                            )
                    );
                }
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.category_firstAid) {
            updateItemList("first_aid");
        } else if (itemId == R.id.category_coldEtc) {
            updateItemList("cold_cough_flu");
        } else if (itemId == R.id.category_hayFever) {
            updateItemList("hay_fever");
        } else if (itemId == R.id.category_thrushTre) {
            updateItemList("thrush_treatment");
        } else if (itemId == R.id.category_travelMed) {
            updateItemList("travel_medicine");
        } else if (itemId == R.id.home_page) {
            startNewActivity(MainActivity.class);
        } else if (itemId == R.id.shopping_cart) {
            startNewActivity(ShoppingCart.class);
        } else if (itemId == R.id.profile) {
            startNewActivity(Profile.class);
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_LONG).show();
            startNewActivity(MainActivity.class);
        } else if (itemId == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.baseline_view_module_24, 1);
            } else {
                changeSpanCount(item, R.drawable.baseline_view_agenda_24, 2);
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.other_items).getSubMenu().findItem(R.id.shopping_cart);
        if (alertMenuItem != null) {
            FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

            assert rootView != null;
            redCircle = (FrameLayout) rootView.findViewById(R.id.alert_circle);
            contextTextView = (TextView) rootView.findViewById(R.id.alert_text);

            rootView.setOnClickListener(v -> onOptionsItemSelected(alertMenuItem));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            contextTextView.setText(String.valueOf(cartItems));
        } else {
            contextTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? View.VISIBLE : View.GONE);
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

    public void addToCart(Item currentItem) {
        cartRef = firebaseFirestoreDb.collection("Cart");

        cartRef.whereEqualTo("itemId", currentItem.getItemId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cartRef.document(document.getId())
                                        .update("itemCount", FieldValue.increment(1));
                            }
                        } else {
                            Map<String, Object> cartItem = new HashMap<>();
                            cartItem.put("itemId", currentItem.getItemId());
                            cartItem.put("itemCount", 1);
                            cartItem.put("userId", firebaseUser.getUid());

                            cartRef.add(cartItem);
                        }
                        Toast.makeText(MainShopList.this, "Item added successfully to cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(LOG_TAG, "Error getting documents.", task.getException());
                    }
                });

    }
}