package com.shivagoud.topcontacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 16-03-2017.
 */
class ContactsRankDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TopContacts";

    private static final int DATABASE_VERSION = 1;


    public ContactsRankDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(ContactsRankDatabase.DATABASE_CREATE);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        database.execSQL("DROP TABLE IF EXISTS ContactRanks");
        onCreate(database);
    }
}