package com.example.admin.clientvisit.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.example.admin.clientvisit.model.SelectOwnerData;

import java.util.List;

@Dao
public interface BusinessOwnerDao {

    @Insert()
    long insertBusinessOwnerData(BusinessOwnerEntity businessOwnerEntity);

    @Query("SELECT * FROM BusinessOwnerEntity")
    List<BusinessOwnerEntity> getAllBusinessOwnerData();

//    @Query("SELECT ownerId FROM BusinessOwnerEntity where businessId=:bid")
//    List<Integer> getListOfOwnerIdByBusinessId(int bid);

    @Query("SELECT oid,bid,ownerName,businessName " +
            "FROM BusinessOwnerEntity,OwnerEntity,BusinessEntity " +
            "WHERE BusinessOwnerEntity.ownerId=OwnerEntity.oid AND BusinessOwnerEntity.businessId=BusinessEntity.bid")
    List<SelectOwnerData> getAllOwnerAndBusiness();

    @Query("SELECT bid,businessName " +
            "FROM BusinessEntity,Businessownerentity " +
            "WHERE Businessownerentity.businessId=BusinessEntity.bid AND Businessownerentity.ownerId=:oid")
    List<BusinessEntity> getBusinessDatabyOwnerId(int oid);


    @Query("SELECT oid,ownerName,last_update " +
            "FROM BusinessOwnerEntity,OwnerEntity,BusinessEntity " +
            "WHERE BusinessOwnerEntity.businessId=:bid AND BusinessEntity.bid=:bid AND BusinessOwnerEntity.ownerId=OwnerEntity.oid")
    List<OwnerEntity> getListOfOwnerObjByBusinessId(int bid);



    //Delete
    @Query("DELETE FROM BusinessOwnerEntity WHERE businessId=:bid")
    void deleteAllByBusinessId(int bid);


    @Query("SELECT * FROM BusinessOwnerEntity")
    Cursor getTestData();




}
