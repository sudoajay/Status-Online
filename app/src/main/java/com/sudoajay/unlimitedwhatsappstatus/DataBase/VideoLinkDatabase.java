package com.sudoajay.unlimitedwhatsappstatus.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sudoajay on 2/6/18.
 */

public class VideoLinkDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VideoLinkDatabase.db";
    public static final String DATABASE_TABLE_NAME = "DATABASE_TABLE_NAME";
    public static final String col_1 = "ID";
    public static final String col_2 = "Link";
    public static final String col_3 = "Done";

    public VideoLinkDatabase(Context context  )
    {
        super(context, DATABASE_NAME, null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DATABASE_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "Link TEXT , Done INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }
    public void Fill_It(final String link, final int done){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2,link);
        contentValues.put(col_3,done);
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

    public Cursor getLink(final long getCount){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select ID , Link from DATABASE_TABLE_NAME WHERE Done = 0 "+" Order By Random() limit "+getCount+"",null);
    }
    public long getProfilesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_NAME);
        db.close();
        return count;
    }

    public void UpdateTheDoneColumn(final String id , final int Done){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,id);
        contentValues.put(col_3,Done);
        sqLiteDatabase.update(DATABASE_TABLE_NAME,contentValues,"ID = ?",new String[] { id });
        sqLiteDatabase.close();
    }

}
