package com.example.admin.clientvisit.model;

import android.graphics.Bitmap;

import com.example.admin.clientvisit.database.OwnerEntity;

import java.io.Serializable;
import java.util.List;

public class ClientData implements Serializable {

    private int businessId;
    List<OwnerEntity> ownerList;
    private String businessName, website, mobile, phone, email, latitude, longitude, addressArea, addressBrief, addressPincode;
    private byte[] image;
//    private Bitmap bitmap;

//    public ClientData(int businessId, List<OwnerEntity> ownerList, String businessName, String website, String mobile, String phone, String email, String latitude, String longitude, String addressArea, String addressBrief, String addressPincode, Bitmap bitmap) {
    public ClientData(int businessId, List<OwnerEntity> ownerList, String businessName, String website, String mobile, String phone, String email, String latitude, String longitude, String addressArea, String addressBrief, String addressPincode, byte[] image) {
    this.businessId = businessId;
    this.ownerList = ownerList;
    this.businessName = businessName;
    this.website = website;
    this.mobile = mobile;
    this.phone = phone;
    this.email = email;
    this.latitude = latitude;
    this.longitude = longitude;
    this.addressArea = addressArea;
    this.addressBrief = addressBrief;
    this.addressPincode = addressPincode;
        this.image = image;
//    this.bitmap=bitmap;

}

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public List<OwnerEntity> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(List<OwnerEntity> ownerList) {
        this.ownerList = ownerList;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddressArea() {
        return addressArea;
    }

    public void setAddressArea(String addressArea) {
        this.addressArea = addressArea;
    }

    public String getAddressBrief() {
        return addressBrief;
    }

    public void setAddressBrief(String addressBrief) {
        this.addressBrief = addressBrief;
    }

    public String getAddressPincode() {
        return addressPincode;
    }

    public void setAddressPincode(String addressPincode) {
        this.addressPincode = addressPincode;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


//    public Bitmap getBitmap() {
//        return bitmap;
//    }
//
//    public void setBitmap(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }
}