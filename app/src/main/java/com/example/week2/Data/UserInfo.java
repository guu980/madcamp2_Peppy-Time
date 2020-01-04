package com.example.week2.Data;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
    private String id;
    private String name;
    private String phoneNumber;
    private String thumbNail;
    private JSONObject json_form;

    public UserInfo(String id, String phoneNumber, String name, String thumbNail) throws JSONException {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.thumbNail = thumbNail;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("name", name);
        jsonObject.put("phoneNumber", phoneNumber);
        jsonObject.put("thumbnail", thumbNail);

        this.json_form = jsonObject;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public JSONObject getJson() {return json_form;}

    @NonNull
    @Override
    public String toString() {
        return "[ " + this.id + ", " + this.name + ", " + this.phoneNumber + ", " + this.thumbNail + "]";
    }
}
