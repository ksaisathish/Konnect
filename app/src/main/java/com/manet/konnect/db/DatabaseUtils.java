package com.manet.konnect.db;

import android.content.Context;
import android.os.AsyncTask;

import com.manet.konnect.db.entity.ProfileEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseUtils {
    private static final Object LOCK = new Object();
    private static KonnectDatabase konnectDatabase;
    private static DatabaseUtils instance;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DatabaseUtils(Context context) {
        konnectDatabase = KonnectDatabase.getInstance(context);
    }

    public static DatabaseUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DatabaseUtils(context);
                }
            }
        }
        return instance;
    }

    public interface ProfileExistenceCheckCallback {
        void onProfileExists(boolean exists);
    }

    public void checkIfProfileExists(ProfileExistenceCheckCallback callback) {
        executor.execute(() -> {
            List<ProfileEntity> profiles = konnectDatabase.profileDao().getAllProfiles();
            callback.onProfileExists(profiles != null && !profiles.isEmpty());
        });
    }

    public void insertProfile(ProfileEntity profile) {
        executor.execute(() -> {
            try {
                konnectDatabase.profileDao().insertProfile(profile);
            } catch (Exception e) {
                // Handle exception or rethrow it to propagate to the calling code
                e.printStackTrace();
                //throw e; // Rethrow the exception
            }
        });
    }

    public void deleteProfile(String username) {
        executor.execute(() -> {
            ProfileEntity profileToDelete = konnectDatabase.profileDao().getProfileByUsername(username);
            if (profileToDelete != null) {
                konnectDatabase.profileDao().deleteProfile(profileToDelete);
            }
        });
    }
}
