package com.shivagoud.topcontacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ContactListAdapter contactsAdapter = new ContactListAdapter();
    ContactsRankDatabase db;

    final static int CONTACT_COUNT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView contactsView = (RecyclerView) findViewById(R.id.contactsListView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactsView.setLayoutManager(layoutManager);
        contactsView.setAdapter(contactsAdapter);

        contactsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i =layoutManager.findLastCompletelyVisibleItemPosition();
                int j =layoutManager.findLastVisibleItemPosition();
                if(i==j)
                    contactsAdapter.addData(db.loadNextContacts(CONTACT_COUNT));
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

        });


        db = ContactsRankDatabase.getInstance(getApplicationContext());
        contactsAdapter.addData(db.loadNextContacts(CONTACT_COUNT));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more:
                db = ContactsRankDatabase.getInstance(this);
                Toast.makeText(this,"Loading contact details", Toast.LENGTH_LONG).show();
                contactsAdapter.addData(db.loadNextContacts(CONTACT_COUNT));
                return true;
            case R.id.refresh:
                Toast.makeText(this,"Fetching contact list", Toast.LENGTH_LONG).show();
                loadAllContactsFromSystem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void loadAllContactsFromSystem(){
        db = ContactsRankDatabase.getInstance(this);
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if(cur == null)
            return;

        contactsAdapter.clearData();
        for (cur.moveToFirst(); ! cur.isAfterLast(); cur.moveToNext()) {

            if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Contact contact = new Contact(id, name);
                contact.fetchContactDetails(this);

                db.addContact(contact);
            }
        }
        cur.close();
        contactsAdapter.addData(db.loadFirstContacts(CONTACT_COUNT));

    }

}
