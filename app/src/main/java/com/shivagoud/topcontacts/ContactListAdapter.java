package com.shivagoud.topcontacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-03-2017.
 */
class ContactListAdapter extends RecyclerView.Adapter {
    private ArrayList<Contact> contactsList = new ArrayList<>();

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
                ContactsRankDatabase db = ContactsRankDatabase.getInstance(v.getContext());
                db.addContact(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
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

