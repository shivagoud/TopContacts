package com.shivagoud.topcontacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by shiva on 16-03-2017.
 */
class Contact {
    String id;
    String name;
    String number;
    int rank;
    Contact(String id, String name){
        this(id,name,0);
    }

    Contact(String id, String name, int rank){
        this.id = id;
        this.name = name;
        this.rank = rank;
    }

    void fetchContactDetails(Context context){
        Cursor pCur = context.getContentResolver().query(
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