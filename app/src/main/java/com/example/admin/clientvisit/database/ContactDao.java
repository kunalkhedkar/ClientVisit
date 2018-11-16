package com.example.admin.clientvisit.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.admin.clientvisit.model.MyLocation;

import java.util.List;

@Dao
public interface ContactDao {

    @Insert()
    long insertContactData(ContactEntity contactEntity);

    @Query("SELECT * FROM ContactEntity")
    List<ContactEntity> getAllContactData();

    @Query("SELECT * FROM ContactEntity WHERE businessId=:bid")
    ContactEntity getContactByBusinessId(int bid);

    @Query("UPDATE ContactEntity SET mobile=:mobile , phone=:phone,email=:email,addressArea=:addressArea,latitude=:latitude,longitude=:longitude,addressBrief=:addressBrief,pincode=:pincode,website=:website " +
            "WHERE businessId=:bid")
    int updateContact(int bid,String mobile, String phone, String email, String addressArea, String latitude, String longitude, String addressBrief, String pincode, String website);

    @Query("UPDATE ContactEntity SET imageByteArray=:image WHERE businessId=:bid")
    void updateImage(int bid,byte[] image);

    @Query("SELECT latitude, longitude from ContactEntity WHERE businessId=:bid")
    MyLocation getBusinessLocation(int bid);


    @Query("SELECT * FROM ContactEntity WHERE businessId IN(SELECT bid FROM BusinessEntity)")
    List<ContactEntity> getContact();


}




