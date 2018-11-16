package com.example.admin.clientvisit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BusinessDao {

    @Insert
    long insertBusinessData(BusinessEntity businessEntity);

    @Query("SELECT * FROM BusinessEntity")
    List<BusinessEntity> getAllBusinessData();

    @Query("UPDATE BusinessEntity SET businessName=:NewBusinessName WHERE bid=:bid")
    int updateBusinessName(String NewBusinessName,int bid);


    @Delete
    int deleteBusinessById(BusinessEntity businessEntity);

}
