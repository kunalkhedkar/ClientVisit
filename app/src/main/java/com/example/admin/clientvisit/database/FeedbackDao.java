package com.example.admin.clientvisit.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FeedbackDao {

    @Insert()
    long insertFeedbackData(FeedbackEntity feedbackEntity);

    @Query("SELECT * FROM FeedbackEntity")
    LiveData<List<FeedbackEntity>> getAllFeedbackData();

    @Query("SELECT * FROM FeedbackEntity")
    List<FeedbackEntity> getAllPlainFeedbackData();


    @Query("SELECT * FROM FeedbackEntity WHERE businessId=:bid")
     List<FeedbackEntity> getFeedbackByBusinessId(int bid);


    @Query("SELECT * FROM FeedbackEntity WHERE fid=:fid")
    List<FeedbackEntity> getFeedbackByIndex(int fid);
}
