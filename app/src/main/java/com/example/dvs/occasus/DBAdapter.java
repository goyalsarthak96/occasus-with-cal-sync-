package com.example.dvs.occasus;




import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DBAdapter{


    public static final String DATABASE_NAME = "Events";
    public static final String DATABASE_TABLE = "event_list";
    public static final int DATABASE_VERSION = 1;
    public static final String value= "_id";
    private DbHelper Helper;
    private static Context Context;
    private static SQLiteDatabase db;
    Context context;


    static final String DATABASE_CREATE =
            "create table event_list (_id integer primary key autoincrement, event_name text not null,description text, " +
                    "event_start_date text not null, event_end_date text not null,start_time text not null, end_time text not null," +
                    "event_start_date_time text not null,bluetooth text not null,wifi text not null,profile text not null, " +
                    "mobile_data text not null,repeat text not null,repeat_until text not null," +
                    "cur_dayofweek_for_cus_monthly_rep text,days_bw_start_n_end integer not null,next_date text not null," +
                    "from_sync integer not null);";



    public DBAdapter(Context ctx) {
        Context = ctx;
        context=ctx;
        Helper = new DbHelper(Context);
    }

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
                throw e;
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DbAdapter", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }





    public DBAdapter open() throws SQLException {

        db = Helper.getWritableDatabase();
        return this;
    }

    public void close() {

        Helper.close();
    }

    public long insertevent(String name,String desc, String start_date, String end_date,String stime, String etime,
                            String start_date_time, String bluetooth2,String wifi2, String profile,String mobile_data,
                            String repeat,String repeat_until,String cur_dayofweek_for_cus_monthly_rep,
                            int days_bw_start_n_end,String next_date,int from_sync)
    {

        ContentValues initialValues = new ContentValues();
        initialValues.put("event_name", name);
        initialValues.put("description",desc);
        initialValues.put("event_start_date", start_date);
        initialValues.put("event_end_date",end_date);
        initialValues.put("start_time",stime);
        initialValues.put("end_time",etime);
        initialValues.put("event_start_date_time",start_date_time);
        initialValues.put("bluetooth",bluetooth2);
        initialValues.put("wifi",wifi2);
        initialValues.put("profile",profile);
        initialValues.put("mobile_data",mobile_data);
        initialValues.put("repeat",repeat);
        initialValues.put("repeat_until",repeat_until);
        initialValues.put("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);
        initialValues.put("days_bw_start_n_end",days_bw_start_n_end);
        initialValues.put("next_date",next_date);
        initialValues.put("from_sync",from_sync);

        return db.insert(DATABASE_TABLE, null, initialValues);

    }



    //---deletes a particular event---
    public void deleteEvent(int id)
    {
        open();
        db.delete(DATABASE_TABLE,value + "= ?", new String[]{Integer.toString(id)});
        close();
    }


    //---retrieves all the contacts---
    public Cursor getAllEventsDetails()
    {
        return db.rawQuery("SELECT * from event_list",null);
    }

/*
    // retrieves contacts having name=req_name
    public Cursor getEventsDetail(String req_name)
    {
        String edit= "select * from event_list where event_name =?";
        return db.rawQuery(edit,new String[]{req_name});
    }
*/
    //retrieves contacts having _id=id
    public Cursor getEventDetail(int id)
    {
        String edit= "select * from event_list where _id=?";
        return db.rawQuery(edit,new String[] {Integer.toString(id)});
    }

    public Cursor getEventDetail1(String date_time)
    {
        String edit= "select * from event_list where event_start_date_time=?";
        return db.rawQuery(edit,new String[] {date_time});
    }


    public int update_Database(int id,String name,String desc, String start_date, String end_date,String stime, String etime,
                                String start_date_time, String bluetooth2,String wifi2, String profile,String mobile_data,
                                String repeat,String repeat_until,String cur_dayofweek_for_cus_monthly_rep,
                                int days_bw_start_n_end,String next_date,int from_sync)
    {


        ContentValues updatedValues = new ContentValues();
        updatedValues.put("event_name", name);
        updatedValues.put("description",desc);
        updatedValues.put("event_start_date", start_date);
        updatedValues.put("event_end_date",end_date);
        updatedValues.put("start_time",stime);
        updatedValues.put("end_time",etime);
        updatedValues.put("event_start_date_time",start_date_time);
        updatedValues.put("bluetooth",bluetooth2);
        updatedValues.put("wifi",wifi2);
        updatedValues.put("profile",profile);
        updatedValues.put("mobile_data",mobile_data);
        updatedValues.put("repeat",repeat);
        updatedValues.put("repeat_until",repeat_until);
        updatedValues.put("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);
        updatedValues.put("days_bw_start_n_end", days_bw_start_n_end);
        updatedValues.put("next_date", next_date);
        updatedValues.put("from_sync",from_sync);


        return db.update(DATABASE_TABLE,updatedValues,value + "= ?",new String[] {Integer.toString(id) });

        //return db.update(DATABASE_TABLE,updatedValues,value + "="+Integer.toString(id),null);

//
    }


}
