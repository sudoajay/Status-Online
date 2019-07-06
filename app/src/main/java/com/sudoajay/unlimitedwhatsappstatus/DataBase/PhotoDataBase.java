package com.sudoajay.unlimitedwhatsappstatus.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sudoajay on 2/6/18.
 */

public class PhotoDataBase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PhotoDatabase.db";
    public static final String DATABASE_TABLE_NAME = "DATABASE_TABLE_NAME";
    public static final String col_1 = "ID";
    public static final String col_2 = "Link";
    public static final String col_3 = "Name";

    public PhotoDataBase(Context context  )
    {
        super(context, DATABASE_NAME, null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DATABASE_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "Link TEXT , Name TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }
    public void FillIt(final String link, final String name){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2,link);
        contentValues.put(col_3,name);
        sqLiteDatabase.insert(DATABASE_TABLE_NAME,null,contentValues);
    }
    public boolean isEmpty(){
        int count = 0;
        try {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from "+DATABASE_TABLE_NAME,null);
        cursor.moveToFirst();
         count = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        } catch (Exception s) {
            return count <= 0;
        }
        return count <= 0;
    }

    public Cursor RandomData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from DATABASE_TABLE_NAME ORDER BY Random() limit 20 " ,null);

    }

    public Cursor FilterName(final String searchString, final int arraySize) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from DATABASE_TABLE_NAME  WHERE Name Like '%" + searchString + "%'" +
                "limit " + arraySize + " , " + arraySize + "+20", null);
    }
}
