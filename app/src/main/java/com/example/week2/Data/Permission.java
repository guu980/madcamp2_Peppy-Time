package com.example.week2.Data;

import android.Manifest;

public class Permission {
    private static String [] permissions = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CONTACTS,
    };

    public static String[] getPermissions() {
        return permissions;
    }

    public static String getCertainPerm(int index) {
        return permissions[index];
    }


}
