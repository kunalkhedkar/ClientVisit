package com.example.admin.clientvisit.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

@Database(entities = {BusinessEntity.class,
        OwnerEntity.class,
        ContactEntity.class,
        FeedbackEntity.class,
        BusinessOwnerEntity.class}
        , version = 4)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "visit_db";
    public static final String DATABASE_NAME_2 = "other_db";

    private static AppDatabase INSTANCE;
    private static AppDatabase INSTANCE_2ND;

    public static synchronized AppDatabase getInstance(Context context) {

        AppDatabase appDatabase= getInstance2nd(context);
        appDatabase.ownerDao().insertOwnerData(new OwnerEntity(0,"test"));

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).addCallback(new Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
//                    attach(DATABASE_NAME
//                            ,"/data/data/com.example.admin.clientvisit.database/databases/");
                }
            })
//                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)

                    .build();
        }
        return INSTANCE;


    }


//    private static void attach(final String databaseName, final String databasePath) {
//        String sql = "ATTACH DATABASE '" + databasePath + databaseName
//                + "' AS \"" + databaseName + "\";";
//
//        Log.d("TAG", "attach: datbase name :" +
//                " "+sql);
//        INSTANCE_2ND. mDatabase.execSQL(sql);
//    }



    public static final Migration MIGRATION_1_2 = new Migration(1,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE OwnerEntity "
                    + " ADD COLUMN last_update INTEGER NOT NULL default 101 ");
        }
    };

    public static synchronized AppDatabase getInstance2nd(Context context) {
        if (INSTANCE_2ND == null) {
            INSTANCE_2ND = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME_2)
//                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE_2ND;
    }

    public abstract BusinessDao businessDao();
    public abstract OwnerDao ownerDao();
    public abstract FeedbackDao feedbackDao();
    public abstract ContactDao contactDao();
    public abstract BusinessOwnerDao businessOwnerDao();
}
