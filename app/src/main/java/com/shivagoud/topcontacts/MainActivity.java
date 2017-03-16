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
    ContactsRankDatabase db = new ContactsRankDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView contactsView = (RecyclerView) findViewById(R.id.contactsListView);
        contactsView.setLayoutManager(new LinearLayoutManager(this));
        contactsView.setAdapter(contactsAdapter);

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
                db.init(this);
                Toast.makeText(this,"Loading contact details", Toast.LENGTH_LONG).show();
                contactsAdapter.addData(db.loadNextContacts(5));
                return true;
            case R.id.refresh:
                db.init(this);
                Toast.makeText(this,"Fetching contact list", Toast.LENGTH_LONG).show();
                loadAllContactsFromSystem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        db.closeDatabase();
        super.onDestroy();
    }

    void loadAllContactsFromSystem(){
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
                //// TODO: 16-03-2017 Save the details to local database
                //contactsAdapter.addData(contact);
            }
        }
        cur.close();
        db.reset();
        contactsAdapter.addData(db.loadNextContacts(5));
    }


    private class ContactListAdapter extends RecyclerView.Adapter {
        ArrayList<Contact> contactsList = new ArrayList<>();

        void clearData(){
            contactsList.clear();
        }

        void addData(Contact contact){
            contactsList.add(contact);
            notifyItemInserted(contactsList.size()-1);
        }
        void addData(List<Contact> contacts){
            contactsList.addAll(contacts);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_contact, parent, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ContactViewHolder vh = (ContactViewHolder)holder;
            final Contact contact = contactsList.get(position);
            vh.setData(contact.name, contact.number, contact.rank);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh.setRank(++contact.rank);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contactsList.size();
        }
    }

    private class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView name, number, rank;
        ContactViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.contactNameView);
            number = (TextView) view.findViewById(R.id.contactNumberView);
            rank = (TextView) view.findViewById(R.id.contactRankView);
        }

        void setData(String nm, String no, int r){
            name.setText(nm);
            number.setText(no);
            setRank(r);
        }

        public void setRank(int r) {
            rank.setText("Rank: " + r);
        }
    }


}
