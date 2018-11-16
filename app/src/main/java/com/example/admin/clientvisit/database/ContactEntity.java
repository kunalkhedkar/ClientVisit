package com.example.admin.clientvisit.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Arrays;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = BusinessEntity.class,
        parentColumns = "bid",
        childColumns = "businessId",
        onDelete = CASCADE))
public class ContactEntity {

    @PrimaryKey(autoGenerate = true)
    private int cid;

    private int businessId;
    private String mobile, phone, email, addressArea, latitude, longitude, addressBrief, pincode, website;
    private byte[] imageByteArray;

    public ContactEntity(int cid, int businessId, String mobile, String phone, String email, String addressArea, String latitude, String longitude, String addressBrief, String pincode, String website, byte[] imageByteArray) {
        this.cid = cid;
        this.businessId = businessId;
        this.mobile = mobile;
        this.phone = phone;
        this.email = email;
        this.addressArea = addressArea;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressBrief = addressBrief;
        this.pincode = pincode;
        this.website = website;
        this.imageByteArray = imageByteArray;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
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

    public String getAddressArea() {
        return addressArea;
    }

    public void setAddressArea(String addressArea) {
        this.addressArea = addressArea;
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

    public String getAddressBrief() {
        return addressBrief;
    }

    public void setAddressBrief(String addressBrief) {
        this.addressBrief = addressBrief;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }


    @Override
    public String toString() {
        return "ContactEntity{" +
                "cid=" + cid +
                ", businessId=" + businessId +
                ", mobile='" + mobile + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", addressArea='" + addressArea + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", addressBrief='" + addressBrief + '\'' +
                ", pincode='" + pincode + '\'' +
                ", website='" + website + '\'' +
                ", imageByteArray=" + Arrays.toString(imageByteArray) +
                '}';
    }
}