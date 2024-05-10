package com.example.mammouthmedicalpharmacyapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mammouthmedicalpharmacyapp.MainShopList;
import com.example.mammouthmedicalpharmacyapp.Model.Item;
import com.example.mammouthmedicalpharmacyapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<Item> itemsData;
    private final ArrayList<Item> itemsDataAll;
    private final Context context;
    private int lastPosition = -1;

    public ItemAdapter(Context context, ArrayList<Item> itemsData) {
        this.itemsData = itemsData;
        this.itemsDataAll = itemsData;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        Item currentItem = itemsData.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Item> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                filterResults.count = itemsDataAll.size();
                filterResults.values = itemsDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Item item : itemsDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            itemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView detailText;
        private final TextView priceText;
        private final ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.itemTitle);
            detailText = itemView.findViewById(R.id.itemDetails);
            priceText = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.addToCart).setOnClickListener(v -> {
                ((MainShopList) context).updateAlertIcon();
            });
        }

        public void bindTo(Item currentItem) {
            titleText.setText(currentItem.getName());
            detailText.setText(currentItem.getDetails());
            priceText.setText(currentItem.getPrice());

            StorageReference storageReference = FirebaseStorage.getInstance().getReference((currentItem.getImageResource()));
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri.toString())
                        .error(R.mipmap.image_not_found)
                        .into(itemImage);
            }).addOnFailureListener(e -> {
                Log.d(MainShopList.class.getName(), "Error while loading the image: " + e.getMessage());
            });

        }
    }
}
