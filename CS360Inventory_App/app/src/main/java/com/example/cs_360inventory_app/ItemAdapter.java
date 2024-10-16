package com.example.cs_360inventory_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private final List<Item> itemList;
    private final DatabaseHelper databaseHelper;

    public ItemAdapter(Context context, List<Item> itemList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.itemList = itemList;
        this.databaseHelper = databaseHelper;
        sortItemsByQuantity(); // Initial sort
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Change text color for items with zero quantity
        if (item.getQuantity() == 0) {
            holder.quantityTextView.setTextColor(Color.RED);
        } else {
            holder.quantityTextView.setTextColor(Color.BLACK);
        }

        holder.buttonPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            databaseHelper.updateItem(item); // Update DB
            sortItemsByQuantity(); // Sort after update
            notifyItemChanged(position); // Update the specific item
        });

        holder.buttonMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                databaseHelper.updateItem(item); // Update DB
                sortItemsByQuantity(); // Sort after update
                notifyItemChanged(position); // Update the specific item

                if (item.getQuantity() == 0) {
                    Toast.makeText(context, "Item '" + item.getName() + "' has reached zero!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            databaseHelper.deleteItem(item.getId()); // Remove from DB
            itemList.remove(position); // Update list
            notifyItemRemoved(position); // Notify removal
            notifyItemRangeChanged(position, itemList.size()); // Update remaining items
        });
    }

        @Override
    public int getItemCount() {
        return itemList.size();
    }



    private void sortItemsByQuantity() {
        itemList.sort(Comparator.comparingInt(Item::getQuantity));
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;
        Button buttonPlus;
        Button buttonMinus;
        Button buttonDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            buttonPlus = itemView.findViewById(R.id.button_plus);
            buttonMinus = itemView.findViewById(R.id.button_minus);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
