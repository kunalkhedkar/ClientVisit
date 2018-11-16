package com.example.admin.clientvisit.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class BusinessEntity {

    @PrimaryKey(autoGenerate = true)
    private int bid;

    private String businessName;

    public BusinessEntity(int bid, String businessName) {
        this.bid = bid;
        this.businessName = businessName;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        businessName = businessName;
    }

    @Override
    public String toString() {
        return "BusinessEntity{" +
                "bid=" + bid +
                ", businessName='" + businessName + '\'' +
                '}';
    }
}
