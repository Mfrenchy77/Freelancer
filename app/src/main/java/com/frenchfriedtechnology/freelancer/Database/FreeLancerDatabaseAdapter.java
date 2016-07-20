package com.frenchfriedtechnology.freelancer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.CLObject;
import com.frenchfriedtechnology.freelancer.Common.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * Helper Class to Create Freelancer DataBase and 2 Tables
 * 1). To store a list of clients with contact information, rate,
 * days if is a recurring client and any additional notes the user might need
 * 2). To store a daily Log of Clients served, Any money received (Cash or Check),
 * Mileage between clients that could be deducted for taxes, and any business expenses
 * that occurred for the day
 */
public class FreeLancerDatabaseAdapter {
    FreeLancerHelper helper;

    public FreeLancerDatabaseAdapter(Context context) {
        helper = new FreeLancerHelper(context);
    }

    /**
     * These two methods insert data into their respective tables
     */
    public long insertDataClientList(String name, String name_2,
                                     String email, String phone, String address,
                                     String rate, String recurrence, String notes) {
        Log.d(Logger.TAG, "Added to Client List " + name + " " + name_2 + " " + email + " "
                + phone + " " + address + " " + rate + " " + recurrence + " " + notes);

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FreeLancerHelper.NAME, name);
        contentValues.put(FreeLancerHelper.NAME_2, name_2);
        contentValues.put(FreeLancerHelper.EMAIL, email);
        contentValues.put(FreeLancerHelper.PHONE, phone);
        contentValues.put(FreeLancerHelper.ADDRESS, address);
        contentValues.put(FreeLancerHelper.RATE, rate);
        contentValues.put(FreeLancerHelper.RECURRENCE, recurrence);
        contentValues.put(FreeLancerHelper.NOTES, notes);
        long id = db.insert(FreeLancerHelper.TABLE_NAME_CLIENT_LIST, null, contentValues);
        db.close();
        return id;
    }

    public long insertDataDailyLog(String logDay, String totalClients,
                                   String cashReceived, String checksReceived,
                                   String mileage, String expenses) {
        Log.d(Logger.TAG, "Added to Log " + logDay + " " + totalClients + " "
                + cashReceived + " " + checksReceived + " " + mileage + " " + expenses);

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FreeLancerHelper.DAY, logDay);
        contentValues.put(FreeLancerHelper.TOTAL_CLIENTS_FOR_DAY, totalClients);
        contentValues.put(FreeLancerHelper.CASH_RECEIVED, cashReceived);
        contentValues.put(FreeLancerHelper.CHECKS_RECEIVED, checksReceived);
        contentValues.put(FreeLancerHelper.MILEAGE, mileage);
        contentValues.put(FreeLancerHelper.EXPENSES, expenses);
        long id = db.insert(FreeLancerHelper.TABLE_NAME_DAILY_LOG, null, contentValues);
        db.close();
        return id;
    }


    //Beginning of Queries


    /**
     * This query to the ClientList Table to retrieve all clients and their data in an
     * object that we can use in the recyclerView
     */
    public ArrayList<CLObject> getClientList() {
        ArrayList<CLObject> listClient = new ArrayList<>();

        //list of columns to retrieve
        String[] columns = {FreeLancerHelper.UID,
                FreeLancerHelper.NAME,
                FreeLancerHelper.NAME_2,
                FreeLancerHelper.EMAIL,
                FreeLancerHelper.PHONE,
                FreeLancerHelper.ADDRESS,
                FreeLancerHelper.RATE,
                FreeLancerHelper.RECURRENCE,
                FreeLancerHelper.NOTES};

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(
                FreeLancerHelper.TABLE_NAME_CLIENT_LIST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(Logger.TAG, "Loading Client List " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {
                CLObject client = new CLObject();
                client.setId(cursor.getInt(cursor.getColumnIndex(FreeLancerHelper.UID)));
                client.setName(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.NAME)));
                client.setName2(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.NAME_2)));
                client.setEmail(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.EMAIL)));
                client.setPhone(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.PHONE)));
                client.setAddress(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.ADDRESS)));
                client.setRate(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.RATE)));
                client.setRecurrence(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.RECURRENCE)));
                client.setNotes(cursor.getString(cursor.getColumnIndex(FreeLancerHelper.NOTES)));

                listClient.add(client);
            }
            while (cursor.moveToNext());
            db.close();
            cursor.close();// FIXME: 2/4/16 this might cuase an err
        }
        return listClient;
    }

    /**
     * This query returns a list of names for the current day of the week to be auto entered into
     * {@link com.frenchfriedtechnology.freelancer.View.Activity.UpdateLog}
     */
    // TODO: 2/4/16 finish setting the recurrence query
    public String getClientRecurrence() {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {FreeLancerHelper.RECURRENCE};

        Cursor cursor = db.query(FreeLancerHelper.TABLE_NAME_CLIENT_LIST, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            //todo going to get the names of clients who have days of the week in there recurrence and
            // pre populate the client list in the update log

        }
        return buffer.toString();
    }

    /**
     * Query returns list of all client names for
     * {@link com.frenchfriedtechnology.freelancer.View.Dialog.DialogSelectClients}
     */
    public ArrayList<String> getClientNames() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<String> list = new ArrayList<>();
        String[] columns = {FreeLancerHelper.NAME
                              /* , FreeLancerHelper.NAME_2, FreeLancerHelper.EMAIL
                , FreeLancerHelper.PHONE, FreeLancerHelper.ADDRESS,
                FreeLancerHelper.RATE, FreeLancerHelper.RECURRENCE, FreeLancerHelper.NOTES*/};

        Cursor cursor = db.query(FreeLancerHelper.TABLE_NAME_CLIENT_LIST, columns,
                null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(FreeLancerHelper.NAME);
            /*int index2 = cursor.getColumnIndex(FreeLancerHelper.NAME_2);
            int index3 = cursor.getColumnIndex(FreeLancerHelper.EMAIL);
            int index4 = cursor.getColumnIndex(FreeLancerHelper.PHONE);
            int index5 = cursor.getColumnIndex(FreeLancerHelper.ADDRESS);
            int index6 = cursor.getColumnIndex(FreeLancerHelper.RATE);
            int index7 = cursor.getColumnIndex(FreeLancerHelper.RECURRENCE);
            int index8 = cursor.getColumnIndex(FreeLancerHelper.NOTES);
*/
            String name = cursor.getString(index1);
           /* String name2 = cursor.getString(index2);
            String email = cursor.getString(index3);
            String phone = cursor.getString(index4);
            String address = cursor.getString(index5);
            String rate = cursor.getString(index6);
            String recurrence = cursor.getString(index7);
            String notes = cursor.getString(index8);*/
            list.add(name + " ");
           /* buffer.append(name + " "*//* + name2 + " " + email + " " + phone
                    + " " + address + " " + rate + " " + recurrence + " " + notes + "\n"*//*);
       */
        }
        db.close();
        cursor.close();
        return list;
    }

    public String getAllDataForClientList() {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {FreeLancerHelper.UID, FreeLancerHelper.NAME,
                FreeLancerHelper.NAME_2, FreeLancerHelper.EMAIL
                , FreeLancerHelper.PHONE, FreeLancerHelper.ADDRESS,
                FreeLancerHelper.RATE, FreeLancerHelper.RECURRENCE, FreeLancerHelper.NOTES};

        Cursor cursor = db.query(FreeLancerHelper.TABLE_NAME_CLIENT_LIST
                , columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int index0 = cursor.getColumnIndex(FreeLancerHelper.UID);
            int index1 = cursor.getColumnIndex(FreeLancerHelper.NAME);
            int index2 = cursor.getColumnIndex(FreeLancerHelper.NAME_2);
            int index3 = cursor.getColumnIndex(FreeLancerHelper.EMAIL);
            int index4 = cursor.getColumnIndex(FreeLancerHelper.PHONE);
            int index5 = cursor.getColumnIndex(FreeLancerHelper.ADDRESS);
            int index6 = cursor.getColumnIndex(FreeLancerHelper.RATE);
            int index7 = cursor.getColumnIndex(FreeLancerHelper.RECURRENCE);
            int index8 = cursor.getColumnIndex(FreeLancerHelper.NOTES);

            int cid = cursor.getInt(index0);
            String name = cursor.getString(index1);
            String name2 = cursor.getString(index2);
            String email = cursor.getString(index3);
            String phone = cursor.getString(index4);
            String address = cursor.getString(index5);
            String rate = cursor.getString(index6);
            String recurrence = cursor.getString(index7);
            String notes = cursor.getString(index8);

            buffer.append(cid + " " + name + " " + name2 + " " + email + " " + phone
                    + " " + address + " " + rate + " " + recurrence + " " + notes + "\n");

        }
        cursor.close();
        return buffer.toString();
    }

    /**
     * Clears both tables in the database, called from
     * {@link com.frenchfriedtechnology.freelancer.View.Activity.SetupActivity}
     */
    public void clearTables() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(FreeLancerHelper.TABLE_NAME_CLIENT_LIST, null, null);
        db.delete(FreeLancerHelper.TABLE_NAME_DAILY_LOG, null, null);
        db.close();
    }

    /**
     * This is the main class to set up Client List Table and Daily Log table in a database
     */
    static class FreeLancerHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "Freelancer DataBase";
        private Context context;

        //Strings for creating Client List Table
        private static final String TABLE_NAME_CLIENT_LIST = "CLIENT_LIST";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_id";
        private static final String NAME = "Name";
        private static final String NAME_2 = "Name_2";
        private static final String EMAIL = "Email";
        private static final String PHONE = "Phone";
        private static final String ADDRESS = "Address";
        private static final String RATE = "Rate";
        private static final String RECURRENCE = "Recurrence";
        private static final String NOTES = "Notes";
        private static final String CREATE_TABLE_CLIENT_LIST = "CREATE TABLE "
                + TABLE_NAME_CLIENT_LIST + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " VARCHAR(255), " + NAME_2 + " VARCHAR(255), " + EMAIL + " VARCHAR(255), "
                + PHONE + " VARCHAR(255), " + ADDRESS + " VARCHAR(255), " + RATE + " VARCHAR(255), "
                + RECURRENCE + " VARCHAR(255), " + NOTES + " VARCHAR(255))";
        private static final String DROP_TABLE_CLIENT_LIST = "DROP TABLE IF EXISTS " + TABLE_NAME_CLIENT_LIST;

        //Strings for creating Daily Log Table
        private static final String TABLE_NAME_DAILY_LOG = "DAILY_LOG";
        private static final String DAY = "Day";
        private static final String TOTAL_CLIENTS_FOR_DAY = "Total_Clients_For_Day";
        private static final String CASH_RECEIVED = "Cash_Received";
        private static final String CHECKS_RECEIVED = "Checks_Received";
        private static final String MILEAGE = "Mileage";
        private static final String EXPENSES = "Expenses";
        private static final String CREATE_TABLE_DAILY_LOG = "CREATE TABLE " + TABLE_NAME_DAILY_LOG + "" +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DAY + " VARCHAR(255), "
                + TOTAL_CLIENTS_FOR_DAY + " VARCHAR(255), " + CASH_RECEIVED + " VARCHAR(255), "
                + CHECKS_RECEIVED + " VARCHAR(255), " + MILEAGE + " VARCHAR(255), "
                + EXPENSES + " VARCHAR(255))";
        private static final String DROP_TABLE_DAILY_LOG = "DROP TABLE IF EXISTS " + TABLE_NAME_DAILY_LOG;

        public FreeLancerHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
            Log.d(Logger.TAG,"DB Constructor Called");
        }

        /**
         * Creates 2 Tables: Client List and Daily Log
         */
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_DAILY_LOG);
                db.execSQL(CREATE_TABLE_CLIENT_LIST);
            } catch (SQLException e) {
                Log.d(Logger.TAG, "SQL Exception = " + e);
                Toast.makeText(context, "DataBase Error", Toast.LENGTH_SHORT).show();
            }
            Log.d(Logger.TAG, "DB onCreate Called");
        }

        /**
         * Updates both Tables
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE_CLIENT_LIST);
                db.execSQL(DROP_TABLE_DAILY_LOG);
                db.execSQL(CREATE_TABLE_CLIENT_LIST);
                db.execSQL(CREATE_TABLE_DAILY_LOG);
            } catch (SQLException e) {
                Toast.makeText(context, "DataBase Error", Toast.LENGTH_SHORT).show();
                Log.d(Logger.TAG, "SQL Exception = " + e);
            }
            Log.d(Logger.TAG, "DB onUpgrade Called");
        }
    }
}
