package com.example.admin.clientvisit.model;

import android.arch.persistence.room.Ignore;

public class SelectOwnerData {

    private int oid;
    private String ownerName, businessName;

    @Ignore
    boolean status;

    @Ignore
    public SelectOwnerData(int oid, String ownerName, String businessName, boolean status) {
        this.oid = oid;
        this.ownerName = ownerName;
        this.businessName = businessName;
        this.status = status;
    }


    public SelectOwnerData(int oid, String ownerName, String businessName) {
        this.oid = oid;
        this.ownerName = ownerName;
        this.businessName = businessName;

    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SelectOwnerData{" +
                "oid=" + oid +
                ", ownerName='" + ownerName + '\'' +
                ", businessName='" + businessName + '\'' +
                ", status=" + status +
                '}';
    }
}