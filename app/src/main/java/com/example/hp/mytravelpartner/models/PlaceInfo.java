package com.example.hp.mytravelpartner.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {
    private String Name;
    private String Phone;
    private String Address;
    private String Id;
    private Uri WebsiteUri;
    private LatLng latlng;
    private float Rating;

    public PlaceInfo(String name, String phoneNumber, String address, String id, Uri websiteUri,
                     LatLng latlng, float rating) {
        this.Name = name;
        this.Address = address;
        this.Phone = phoneNumber;
        this.Id = id;
        this.WebsiteUri = websiteUri;
        this.latlng = latlng;
        this.Rating = rating;
    }

    public PlaceInfo() {

    }

    public String getName() {
        return Name;
    }


    public void setName(String name) {
        this.Name = name;
    }


    public String getAddress() {
        return Address;
    }


    public void setAddress(String address) {
        this.Address = address;
    }


    public String getPhoneNumber() {
        return Phone;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.Phone = phoneNumber;
    }



    public String getId() {
        return Id;
    }



    public void setId(String id) {
        this.Id = id;
    }



    public Uri getWebsiteUri() {
        return WebsiteUri;
    }



    public void setWebsiteUri(Uri websiteUri) {
        this.WebsiteUri = websiteUri;
    }



    public LatLng getLatlng() {
        return latlng;
    }



    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }



    public float getRating() {
        return Rating;
    }



    public void setRating(float rating) {
        this.Rating = rating;
    }

    public String toString() {

        return "PlaceInfo{" +
                "name='" + Name + '\'' +
                ", address='" + Address + '\'' +
                ", phoneNumber='" + Phone + '\'' +
                ", id='" + Id + '\'' +
                ", websiteUri=" + WebsiteUri +
                ", latlng=" + latlng +
                ", rating=" + Rating;
    }
}
