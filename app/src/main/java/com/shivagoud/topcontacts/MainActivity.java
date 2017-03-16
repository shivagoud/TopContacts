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

public class MainActivity extends AppCompatActivity{

    private ContactListAdapter contactsAdapter = new ContactListAdapter();

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
            case R.id.refresh:
                Toast.makeText(this,"Loading contact details", Toast.LENGTH_LONG).show();
                loadAllContactsFromSystem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void loadAllContactsFromSystem(){
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if(cur == null)
            return;

        contactsAdapter.clearData();
        int i;
        for (cur.moveToFirst(),i=0; ! cur.isAfterLast() && i<20; cur.moveToNext()) {

            if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Contact contact = new Contact(id, name);
                contact.fetchContactDetails();

                //// TODO: 16-03-2017 Save the details to local database
                contactsAdapter.addData(contact);
                i++;
            }
        }
        cur.close();
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

    private class Contact {
        String id;
        String name;
        String number;
        int rank;
        Contact(String id, String name){
            this.id = id;
            this.name = name;
            rank =0;
        }

        void fetchContactDetails(){
            Cursor pCur = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);
            if(pCur == null)
                return;

            if(pCur.moveToFirst()) {
                number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            pCur.close();
        }


    }
}
