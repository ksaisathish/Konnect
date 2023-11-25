package com.manet.konnect.utils;

public class Constants {
    public static final String BASE_URL;

    static {
        BASE_URL = "https://konnect-backend.onrender.com/"; // Set a default URL or any initial value

        // Update BASE_URL with the value from resources
        // For example, if you're in an Activity or Context:
        // BASE_URL = getResources().getString(R.string.base_url);
    }
}