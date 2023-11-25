package com.manet.konnect.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "profile")
public class ProfileEntity {

    @NotNull
    @PrimaryKey(autoGenerate = false)
    private String userid;
    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "profile_picture")
    private String profilePicture;

    @NotNull
    @ColumnInfo(name = "konnect_hash") // Field to store the hash from the server
    private String konnectHash;


    // Getters and setters for fields

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullname) {
        this.fullName = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getKonnectHash() {
        return konnectHash;
    }

    public void setKonnectHash(String konnectHash) {
        this.konnectHash = konnectHash;
    }
}

