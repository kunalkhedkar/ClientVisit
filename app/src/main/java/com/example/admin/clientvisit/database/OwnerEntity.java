package com.example.admin.clientvisit.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

@Entity
public class OwnerEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int oid=0;

    private  int last_update;

    private String ownerName;

    public OwnerEntity(int oid, String ownerName) {
        this.oid = oid;
        this.ownerName = ownerName;
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
        ownerName = ownerName;
    }

    public int getLast_update() {
        return last_update;
    }

    public void setLast_update(int last_update) {
        this.last_update = last_update;
    }


    @Override
    public String toString() {
        return "OwnerEntity{" +
                "oid=" + oid +
                ", last_update=" + last_update +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
