package com.example.admin.clientvisit.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface OwnerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertOwnerData(OwnerEntity ownerEntity);

    @Query("SELECT * FROM OwnerEntity")
    List<OwnerEntity> getAllOwnerData();


    @Query("SELECT * FROM OwnerEntity")
    LiveData<List<OwnerEntity>> getAllOwnerLiveData();

    @Query("SELECT * FROM OwnerEntity WHERE oid=:mOId")
    List<OwnerEntity> getOwnerNameByOid(Integer mOId);

    @Update
    int updateOwner(OwnerEntity ownerEntity);

//    @Query("DELETE FROM OwnerEntity INNER JOIN OwnerEntity ON WHERE BusinessOwnerEntity WHERE OwnerEntity.oid!=BusinessOwnerEntity.ownerId")
//    int deleteBusinessLessOwners();

    @Query("DELETE FROM OwnerEntity WHERE oid NOT IN (SELECT ownerId FROM BusinessOwnerEntity)")
    int deleteBusinessLessOwners();


}

