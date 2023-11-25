package com.manet.konnect.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.manet.konnect.db.entity.ProfileEntity;

import java.util.List;

@Dao
public interface ProfileDao {
    @Insert
    void insertProfile(ProfileEntity profile);

    @Delete
    void deleteProfile(ProfileEntity profile);

    @Query("SELECT * FROM profile WHERE userid = :userid")
    ProfileEntity getProfileByUsername(String userid);

    @Query("SELECT * FROM profile ")
    List<ProfileEntity> getAllProfiles();

    // Add other queries or methods as needed
}

