package com.stepup.counter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String dbName = "Signup.db";

    public DBHelper(@Nullable Context context) {
        super(context, "Signup.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table allusers(username TEXT primary key, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists allusers");
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = db.insert("allusers", null, contentValues);

        if(result==-1) return false;
        else return true;

    }
    public Boolean checkUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from allusers where username = ?", new String[]{username});

        if(cursor.getCount() > 0) return true;
        else return false;
    }
    public Boolean checkUsernamePassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from allusers where username = ? and password = ?", new String[]{username, password});
        if(cursor.getCount()>0) return true;
        else return false;
    }
}
