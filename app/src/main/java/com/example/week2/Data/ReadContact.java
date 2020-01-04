package com.example.week2.Data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;


import org.json.JSONArray;

import java.util.ArrayList;

public class ReadContact {
    private JSONArray phoneContacts;

    private Context context;

    public ReadContact(Context context) {
        this.context = context;
    }

    public ArrayList<UserInfo> getContactList() {
        phoneContacts = new JSONArray();

        ArrayList<UserInfo> users = new ArrayList<>();
        Cursor contactCursor = null;
        try {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            String[] projection = new String[] {
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
            };


            contactCursor = context.getContentResolver().query(uri, projection, null, null, null);



            if (contactCursor.moveToFirst()) {
                do {
                    String thumbnail = contactCursor.getString(3);
                    String id = contactCursor.getString(0);
                    String name = contactCursor.getString(2);
                    String phoneNumber = contactCursor.getString(1).replaceAll("-", "");

                    if (phoneNumber.substring(0, 3).equals("010")) {
                        phoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 7) + "-" + phoneNumber.substring(7, 11);
                    } else if (phoneNumber.length() == 10){
                        phoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
                    }
                    UserInfo userInfo = new UserInfo(id, phoneNumber, name, thumbnail);
                    users.add(userInfo);
                    phoneContacts.put(userInfo.getJson());
                } while (contactCursor.moveToNext());
            }
            return users;

        } catch (Exception e) {
            contactCursor.close();
            return users;
        } finally {
            contactCursor.close();
        }
    }

    public JSONArray getPhoneContacts() {return phoneContacts;}
}
