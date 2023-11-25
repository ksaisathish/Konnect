package com.manet.konnect.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.manet.konnect.db.dao.ProfileDao;
import com.manet.konnect.db.entity.ProfileEntity;

@Database(entities = {ProfileEntity.class}, version = 1, exportSchema = false)
public abstract class KonnectDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "konnect_database";

    public abstract ProfileDao profileDao();

    // Define other DAOs for future tables

    private static volatile KonnectDatabase INSTANCE;

    public static synchronized KonnectDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            KonnectDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}

