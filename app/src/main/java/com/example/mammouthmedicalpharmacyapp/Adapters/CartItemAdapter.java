package com.example.mammouthmedicalpharmacyapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mammouthmedicalpharmacyapp.MainShopList;
import com.example.mammouthmedicalpharmacyapp.Model.CartItem;
import com.example.mammouthmedicalpharmacyapp.R;
import com.example.mammouthmedicalpharmacyapp.ShoppingCart;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private ArrayList<CartItem> cartItems;
    private int lastPosition = -1;
    private final Context context;

    public CartItemAdapter(Context context, ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemAdapter.CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemViewHolder(LayoutInflater.from(context).inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem currentItem = cartItems.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView itemCount;

        public CartItemViewHolder(View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemCount = itemView.findViewById(R.id.itemCount);
        }

        public void bindTo(CartItem currentItem) {
            Log.w(ShoppingCart.class.getName(), "CURRENT ITEM NAME: " + itemName);
            itemName.setText(currentItem.getName());
            itemCount.setText(String.valueOf(currentItem.getItemCount()));

            StorageReference storageReference = FirebaseStorage.getInstance().getReference((currentItem.getImageResource()));
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri.toString())
                        .error(R.mipmap.image_not_found)
                        .into(itemImage);
            }).addOnFailureListener(e -> {
                Log.d(ShoppingCart.class.getName(), "Error while loading the image: " + e.getMessage());
            });

            itemView.findViewById(R.id.deleteItemButton).setOnClickListener(view -> {
                ((ShoppingCart) context).deleteCartItem(currentItem);
            });

            itemView.findViewById(R.id.deleteOneButton).setOnClickListener(view -> {
                ((ShoppingCart) context).deleteOneItem(currentItem);
            });
        }
    }
}

