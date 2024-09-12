package com.example.findpet.dp;

import static com.example.findpet.utils.Constants.COL_ID;
import static com.example.findpet.utils.Constants.COL_KEY;
import static com.example.findpet.utils.Constants.DB_NAME;
import static com.example.findpet.utils.Constants.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHelper";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_KEY + " TEXT UNIQUE " + ")";
        Log.d(TAG, "Creating table with query: " + query);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        Log.d(TAG, "Upgrading database with query: " + query);
        db.execSQL(query);
        onCreate(db);
    }

    public long insertPost(String key) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_KEY, key);

        long result = database.insert(TABLE_NAME, null, contentValues);
        Log.d(TAG, "Inserted post with key: " + key + " result: " + result);
        database.close();
        return result;
    }

    public String readPost(String key) {
        SQLiteDatabase database = this.getReadableDatabase();
        String result = null;
        Cursor cursor = null;

        try {
            // Constructing the query
            String query = "SELECT " + COL_KEY + " FROM " + TABLE_NAME + " WHERE " + COL_KEY + "=?";
            Log.d(TAG, "Query: " + query); // Logging the query for debugging

            // Executing the query
            cursor = database.rawQuery(query, new String[]{key});

            // Checking if cursor has results
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(COL_KEY));
                Log.d(TAG, "Read result: " + result); // Logging the result for debugging
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading post: " + e.getMessage());
        } finally {
            // Closing cursor and database
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }

        return result;
    }

    public void removedFavourite(String key) {
        SQLiteDatabase sq = this.getWritableDatabase();
        sq.delete(TABLE_NAME, COL_KEY + " =?", new String[]{key});
        sq.close();
    }

}
