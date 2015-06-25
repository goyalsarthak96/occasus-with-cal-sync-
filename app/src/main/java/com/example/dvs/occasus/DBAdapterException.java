package com.example.dvs.occasus;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapterException {

    public static final String DATABASE_NAME = "Exception_Contacts";
    public static final String DATABASE_TABLE = "Contacts";

    public static final int DATABASE_VERSION = 1;

    private DbHelper Helper;
    private static Context Context;
    private static SQLiteDatabase db;
    private static String values="id";

    // creating table
    static final String DATABASE_CREATE =
            "create table contacts (id text primary key, "
                    + "Name text not null, PhoneNo text not null);";


    //Declaring DbHelper
    public DBAdapterException(Context ctx) {
        Context = ctx;
        Helper = new DbHelper(Context);
    }

    //Function for creating database
    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        //---updating database---
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DBAdapterException", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    public DBAdapterException open() throws SQLException {
        db = Helper.getWritableDatabase();
        return this;
    }

    //Closing Database
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
        /*Cursor c = db.query(true,DATABASE_TABLE, new String[] {"id",
                        "Name", "PhoneNo"}, values + "=" + rowId, null,
                null, null, null, null);
        String contact_name = c.getString(c.getColumnIndex("Name"));
        c.close();
        Cursor c1 = db.query(true,DATABASE_TABLE, new String[] {"id"}, "Name" + "=" + contact_name, null,
                null, null, null, null);*/

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

    //---retrieves a particular contact---
    public Cursor getContact(String rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {"id",
                        "Name", "PhoneNo"}, "id" + "=" + rowId, null,
            null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves all the contacts---
    public Cursor getAllContacts()
    {
        return db.query(DATABASE_TABLE, new String[] {"id", "Name",
                "PhoneNo"}, null, null, null, null, null);
    }

}

