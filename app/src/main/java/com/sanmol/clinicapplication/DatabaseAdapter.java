package com.sanmol.clinicapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {
    static final String DATABASE_NAME = "login.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;
    static final String DATABASE_CREATE = "create table " + "Patient" + "( "
            + "ID" + " integer primary key autoincrement,"
            + "fname  text,lname text,dob text,department text,doctor text,reason text,contact text,email text); ";//,password text,confirmpassword text); ";
    public SQLiteDatabase db;
    private final Context context;
    private DataBaseHelper dbHelper;

    public DatabaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    public DatabaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(String fname, String lname, String dob, String department, String doctor, String reason, String contact, String email){//, String password, String confirmPassword) {
        ContentValues newValues = new ContentValues();
        newValues.put("fname", fname);
        newValues.put("lname", lname);
        newValues.put("dob", dob);
        newValues.put("department", department);
        newValues.put("doctor", doctor);
        newValues.put("reason", reason);
        newValues.put("contact", contact);
        newValues.put("email", email);
        /*newValues.put("password", password);
        newValues.put("confirmpassword", confirmPassword);
*/
        db.insert("Patient", null, newValues);

    }

    public int deleteEntry(String UserName) {

        String where = "USERNAME=?";
        int numberOFEntriesDeleted = db.delete("LOGIN", where,
                new String[]{UserName});
        return numberOFEntriesDeleted;
    }

    public String getSinlgeEntry(String contact) {
        Cursor cursor = db.query("Patient", null, " contact=?",
                new String[]{contact}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("password"));
        cursor.close();
        return password;
    }

    public boolean contactExist(String contact) {
        Cursor cursor = db.query("Patient", null, " contact=?",
                new String[]{contact}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        else
        return true;
    }

    public boolean refnoExist(String ID) {
        Cursor cursor = db.query("Patient", null, " ID=?",
                new String[]{ID}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        else
            return true;
    }
    public boolean emailExist(String email) {
        Cursor cursor = db.query("Patient", null, " email=?",
                new String[]{email}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        else
            return true;
    }

    public int getID() {
        int lastId = 0;
       /* Cursor cursor = db.query("Patient", null, " contact=?",
                new String[] { contact }, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("password"));
        cursor.close();
        return password;*/

        String query = "SELECT ID from Patient order by ID DESC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return lastId;
    }

    public boolean authenticateEntry(String contact) {
        Cursor cursor = db.query("Patient", null, " contact=?",
                new String[]{contact}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public void updateEntry(String userName, String password) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("USERNAME", userName);
        updatedValues.put("PASSWORD", password);

        String where = "USERNAME = ?";
        db.update("LOGIN", updatedValues, where, new String[]{userName});
    }
}