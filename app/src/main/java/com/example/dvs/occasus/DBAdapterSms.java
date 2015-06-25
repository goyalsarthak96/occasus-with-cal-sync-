package com.example.dvs.occasus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapterSms {

    public static final String DATABASE_NAME = "SMS_contacts";
    public static final String DATABASE_TABLE = "SmsContacts";

    public static final int DATABASE_VERSION = 1;


    private DbHelper1 Helper;
    private static Context Context;
    private static SQLiteDatabase db;
    private static String values="id";

    //---create table---
    static final String DATABASE_CREATE =
            "create table SmsContacts (id text primary key, "
                    + "Name text not null, PhoneNo text not null);";

    public DBAdapterSms(Context ctx) {
        Context = ctx;
        Helper = new DbHelper1(Context);
    }

    private static class DbHelper1 extends SQLiteOpenHelper {

        public DbHelper1(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                throw e;
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DbAdapter", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS SmsContacts");
            onCreate(db);
        }
    }

    public DBAdapterSms open() throws SQLException {
        db = Helper.getWritableDatabase();
        return this;
    }

    public void close() {
        Helper.close();
    }

    //---inserts a particular contact---
    public long insertContact(String name,String id, String number)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", id);
        initialValues.put("Name", name);
        initialValues.put("PhoneNo", number);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact---
    public void deleteContact(String rowId)
    {
        open();
        Cursor c = db.query(DATABASE_TABLE, new String[] {"id", "Name",
                "PhoneNo"}, null, null, null, null, null);
        db.delete(DATABASE_TABLE,values + "= ?",new String[]{rowId});
        if (c.moveToFirst())
        {
            do {
                String id = c.getString(c.getColumnIndex("id"));
                if(id.equals((rowId.substring(0,rowId.length()-1) + 1)) || id.equals(rowId.substring(0,rowId.length()-1) + 2)) {
                    db.delete(DATABASE_TABLE, values + "= ?", new String[]{id});
                }
            } while (c.moveToNext());
        }
        c.close();
        close();
    }

    //---retrieves all the contacts---
    public Cursor getAllContacts()
    {
        return db.query(DATABASE_TABLE, new String[] {"id", "Name",
                "PhoneNo"}, null, null, null, null, null);
    }

}