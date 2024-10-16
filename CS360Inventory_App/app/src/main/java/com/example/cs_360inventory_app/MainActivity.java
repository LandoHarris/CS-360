package com.example.cs_360inventory_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private DatabaseHelper databaseHelper;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton profileButton = findViewById(R.id.profile);
        ImageButton logoutButton = findViewById(R.id.Logout);

        profileButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(MainActivity.this, SMSNotification.class);
            startActivity(profileIntent);
        });

        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        itemList = new ArrayList<>(databaseHelper.getAllItems());

        sortItemsByQuantity();

        itemAdapter = new ItemAdapter(this, itemList, databaseHelper);
        recyclerView.setAdapter(itemAdapter);
        userDatabaseHelper = new UserDatabaseHelper(this);

        // Add item to database
        Button addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(v -> addItemDialog());

        // List all of the items in the database
        Button listButton = findViewById(R.id.button_list);
        listButton.setOnClickListener(v -> showItemListDialog());

        // List all of the users in the user database
        Button userListButton = findViewById(R.id.button_user_list);
        userListButton.setOnClickListener(v -> showUserListDialog());

        Log.d("MainActivity", "RecyclerView and Adapter set");

        // If there are no items in the database, then we add one.
        if (itemList.isEmpty()) {
            Log.d("MainActivity", "Item list is empty, adding placeholder item");
            addItem("Placeholder Item", 0);
        }
    }

    private void sortItemsByQuantity() {
        itemList.sort(Comparator.comparingInt(Item::getQuantity));
    }

    private void addItem(String name, int quantity) {
        Log.d("MainActivity", "Adding item: " + name + ", Quantity: " + quantity);
        Item item = new Item(0, name, quantity);
        databaseHelper.addItem(item);
        itemList.add(item);
        sortItemsByQuantity(); // Sort after adding

        int position = itemList.indexOf(item);  // Get the new position after sorting
        itemAdapter.notifyItemInserted(position);  // Notify the adapter that an item was added at 'position'

        // Check if the quantity is zero and show a Toast message if true
        if (quantity == 0) {
            Toast.makeText(MainActivity.this, "Item '" + name + "' has reached zero!", Toast.LENGTH_SHORT).show();
        }
    }


    private void addItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, (ViewGroup) getWindow().getDecorView(), false);
        final EditText inputName = viewInflated.findViewById(R.id.input_name);
        final EditText inputQuantity = viewInflated.findViewById(R.id.input_quantity);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, null); // Set to null, so we can control the click action
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the click listener for the OK button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String quantityStr = inputQuantity.getText().toString().trim();

            // Check if name and quantity are not empty
            if (name.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in both the name and quantity.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int quantity = Integer.parseInt(quantityStr);  // Ensure quantity is a valid number
                    if (quantity < 0) {
                        Toast.makeText(MainActivity.this, "Quantity cannot be negative.", Toast.LENGTH_SHORT).show();
                    } else {
                        addItem(name, quantity);  // Add the item if validation passes
                        dialog.dismiss();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showItemListDialog() {
        // Fetch all items from the database
        List<Item> items = databaseHelper.getAllItems();

        // Create a String array to hold item names and quantities
        List<String> itemDetails = new ArrayList<>();
        for (Item item : items) {
            itemDetails.add(item.getName() + " - Quantity: " + item.getQuantity());
        }

        // Create an AlertDialog to show the items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Items in Inventory");

        // Inflate a ListView to display the items
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_item_list, (ViewGroup) getWindow().getDecorView(), false);
        ListView listView = viewInflated.findViewById(R.id.listView_items);

        // Set the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemDetails);
        listView.setAdapter(adapter);

        builder.setView(viewInflated);
        builder.setNegativeButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showUserListDialog() {
        // Fetch all users from the database
        Cursor cursor = userDatabaseHelper.getAllUsers();

        // Create a String array to hold user names and their details
        List<String> userDetails = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                userDetails.add(username + " - Password: " + password);
            } while (cursor.moveToNext());
        }
        cursor.close(); // Close the cursor after usage

        // Create an AlertDialog to show the users
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registered Users");

        // Inflate a ListView to display the users
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_user_list, (ViewGroup) getWindow().getDecorView(), false);
        ListView listView = viewInflated.findViewById(R.id.listView_users);

        // Set the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userDetails);
        listView.setAdapter(adapter);

        builder.setView(viewInflated);
        builder.setNegativeButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
