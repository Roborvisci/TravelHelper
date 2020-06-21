package com.mtullier.travelapp_v2.ui.budget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BudgetDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Budget.db";
    public static final String TABLE_NAME = "budget_table";
    public static final String COLUMN_TWO = "TYPE";
    public final String COLUMN_THREE = "AMOUNT";
    public static final String COLUMN_FOUR = "DATE";


    public BudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.getWritableDatabase();
    }

    //creates the table with an autoincrementing ID and three appropriate columns
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " TYPE TEXT, AMOUNT REAL, DATE TEXT)");
    }

    //drops the current table and creates a new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    //inserts data into the table at the appropriate columns
    public boolean insertData(String type, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TWO, type);
        contentValues.put(COLUMN_THREE, amount);
        contentValues.put(COLUMN_FOUR, date);

        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    //retrieves data from the table
    public Cursor retrieveData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from " + TABLE_NAME, null);
    }

    //deletes the contents of the table
    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null, null);
    }

    //get the sum of the AMOUNT column
    public double getTotalSpent() {
        double sum = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();

        String sumQuery = String.format("SELECT SUM(%s) as Total FROM %s",
                COLUMN_THREE, TABLE_NAME);
        Cursor res = db.rawQuery(sumQuery, null);

        if(res.moveToFirst()) sum = res.getDouble(res.getColumnIndex("Total"));

        res.close();

        return sum;
    }

}
