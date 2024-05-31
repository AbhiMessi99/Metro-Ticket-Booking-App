package com.example.metroTicketBooking;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DBhelperClass extends SQLiteOpenHelper {

    public static final int DatabaseVersion = 1;
    public static final String DatabaseBase = "allusers.db";
    public static final String Table_Name = "users";
    public static final String COL_Username = "username";
    public static final String COL_Password = "password";
    public static final String COL_Email = "email";
    public static final String COL_Mobile = "mobile";
    public static final String COL_Gender = "gender";
    public static final String COL_DOB = "dob";

    public DBhelperClass(@Nullable Context context) {
        super(context, DatabaseBase, null, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("CREATE TABLE " +
                Table_Name + " (" +
                COL_Username + " TEXT, " +
                COL_Password + " TEXT, " +
                COL_Email + " TEXT, " +
                COL_Mobile + " TEXT, " +
                COL_Gender + " TEXT, " +
                COL_DOB + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists "+Table_Name);
    }

    public Boolean insertQuery(String username, String password, String email, String mobile, String gender,String dob){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_Username, username);
        cv.put(COL_Password, password);
        cv.put(COL_Email, email);
        cv.put(COL_Mobile, mobile);
        cv.put(COL_Gender, gender);
        cv.put(COL_DOB, dob);

        long result = MyDB.insert(Table_Name,null,cv);
        return result != -1;

    }
    public int checkValidity(String username, String email, String mno){
        SQLiteDatabase MyDb = this.getWritableDatabase();
        Cursor cur = MyDb.rawQuery("Select * from "+Table_Name+" where "+COL_Username+" = ?", new String[]{username});
        if(cur.getCount()>0)
        {
            return 1;
        }
        Cursor cur1 = MyDb.rawQuery("Select * from "+Table_Name+" where "+COL_Email+" = ?", new String[]{email});
        if(cur1.getCount()>0)
        {
            return 2;
        }
        Cursor cur2 = MyDb.rawQuery("Select * from "+Table_Name+" where "+COL_Mobile+" = ?", new String[]{mno});
        if(cur2.getCount()>0)
        {
            return 3;
        }
        return 0;
    }
    public boolean authenticate(String username, String password){
        SQLiteDatabase MyDb = this.getWritableDatabase();
        Cursor cur = MyDb.rawQuery("select * from "+ Table_Name +" where "+COL_Username+" = ? and "+COL_Password+" = ?", new String[]{username,password});
        return cur.getCount() > 0;
    }

    public boolean checkForForgetting(String emailmob){
        SQLiteDatabase MyDb = this.getWritableDatabase();
        Cursor cur = MyDb.rawQuery("select * from "+Table_Name+" where "+COL_Mobile+"=? or "+COL_Email+"=?", new String[]{emailmob, emailmob});
        return cur.getCount() > 0;
    }

    public int updatePassword(String password, String username){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(COL_Password, password);
        int result = MyDB.update(Table_Name, cv, COL_Username+ " =?",new String[]{username});
        return result;
    }

    public int deleteAcc(String uid){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        int result = MyDB.delete(Table_Name, COL_Username+" =?", new String[]{uid});
        return result;
    }

    @SuppressLint("Range")
    public String getEmail(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = null;

        Cursor cursor = db.query(Table_Name, new String[]{COL_Email}, COL_Username + "=?",
                new String[]{username}, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                email = cursor.getString(cursor.getColumnIndex(COL_Email));
            }
            cursor.close();
        }

        db.close();
        return email;
    }

    public HashMap<String, String> getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> userDetails = new HashMap<>();

        Cursor cursor = db.query(Table_Name, new String[]{COL_Mobile, COL_Gender, COL_DOB}, COL_Username + "=?",
                new String[]{username}, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userDetails.put("mobile", cursor.getString(cursor.getColumnIndex(COL_Mobile)));
                userDetails.put("gender", cursor.getString(cursor.getColumnIndex(COL_Gender)));
                userDetails.put("birth", cursor.getString(cursor.getColumnIndex(COL_DOB)));
            }
            cursor.close();
        }

        db.close();
        return userDetails;
    }
}
