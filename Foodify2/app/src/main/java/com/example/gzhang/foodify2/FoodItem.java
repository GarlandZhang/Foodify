package com.example.gzhang.foodify2;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GZhang on 2017-12-31.
 */

public class FoodItem implements Parcelable {

    String name;
    String expiryDate;

    public FoodItem(String name, String expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
    }

    public FoodItem(Parcel parcel) {
        this.name = parcel.readString();
        this.expiryDate = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(expiryDate);
    }

    public static final Parcelable.Creator<FoodItem> CREATOR = new Parcelable.Creator<FoodItem>() {

        @Override
        public FoodItem createFromParcel(Parcel parcel) {
            return new FoodItem(parcel);
        }

        @Override
        public FoodItem[] newArray(int i) {
            return new FoodItem[i];
        }
    };

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getExpiryDate(){
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate){
        this.expiryDate = expiryDate;
    }

    public JSONObject getJSONObject(){
        JSONObject obj = new JSONObject();
        try{
            obj.put("name", name);
            obj.put("expiryDate", expiryDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
