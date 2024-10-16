package com.example.cs_360inventory_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";

    private static final String TABLE_CREATE_ITEMS =
            "CREATE TABLE " + TABLE_ITEMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables...");
        db.execSQL(TABLE_CREATE_ITEMS);
        Log.d("DatabaseHelper", "Tables created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database...");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
        Log.d("DatabaseHelper", "Database upgraded successfully.");
    }

    // Create/Add Item to the grid
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    // Read items from grid
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                if (idIndex == -1 || nameIndex == -1 || quantityIndex == -1) {
                    Log.e("Database Error", "Column index not found");
                } else {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    itemList.add(new Item(id, name, quantity));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // Update Item in the grid
    public void updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    // Delete Item from the grid
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
