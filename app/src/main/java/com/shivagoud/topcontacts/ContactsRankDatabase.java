package com.shivagoud.topcontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by user on 16-03-2017.
 */
class ContactsRankDatabase {


    private Context mContext;
    private ContactsRankDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    final static String CONTACT_RANK_TABLE="ContactRanks"; // name of table

    final static String CONTACT_ID="_id";
    final static String CONTACT_NAME="name";
    final static String CONTACT_RANK="rank";
    static final String DATABASE_CREATE = "create table ContactRanks ( _id integer primary key,name text not null,rank integer);";

    private Cursor loadCursor;

    void init(Context context){
        mContext = context;
        if(dbHelper == null)
            dbHelper = new ContactsRankDatabaseHelper(mContext);

        if(database == null || !database.isOpen())
            database = dbHelper.getWritableDatabase();
    }

    void reset(){
        if(loadCursor!=null && !loadCursor.isClosed())
            loadCursor.moveToFirst();
    }

    long addContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(CONTACT_ID, contact.id);
        values.put(CONTACT_NAME, contact.name);
        values.put(CONTACT_RANK, contact.rank);
        return database.replace(CONTACT_RANK_TABLE, null, values);
    }

    private void loadContactsQuery() {
        if(loadCursor==null || loadCursor.isClosed()) {

            String[] cols = new String[]{CONTACT_ID, CONTACT_NAME, CONTACT_RANK};
            loadCursor = database.query(true, CONTACT_RANK_TABLE, cols, null
                    , null, null, null, CONTACT_RANK, null);
            if (loadCursor != null) {
                loadCursor.moveToFirst();
            }
        }
    }

    ArrayList<Contact> loadNextContacts(int max){
        ArrayList<Contact> contacts = new ArrayList<>();
        loadContactsQuery();

        while(max-- > 0 && loadCursor.moveToNext()){

            String id = loadCursor.getString(loadCursor.getColumnIndex(CONTACT_ID));
            String name = loadCursor.getString(loadCursor.getColumnIndex(CONTACT_NAME));
            int rank = loadCursor.getInt(loadCursor.getColumnIndex(CONTACT_RANK));

            Contact contact = new Contact(id, name, rank);
            contact.fetchContactDetails(mContext);

            contacts.add(contact);
        }

        return contacts;
    }

    void closeDatabase(){
        if(loadCursor!=null && !loadCursor.isClosed())
            loadCursor.close();
        database.close();
    }
}
