package com.manet.konnect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.manet.konnect.R;
import com.manet.konnect.utils.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {

    private static final String BASE_URL = Constants.BASE_URL;

    private static final String REGISTER_URL = BASE_URL+"register";
    private static final String CHECK_USERID_URL = BASE_URL+"checkUserID";
    private static final String TAG="RegistrationActivity";

    Button registerButton;
    TextInputEditText fullname, phonenumber, email, userid;
    TextInputLayout fullnameLayout, phonenumberLayout, emailLayout, useridLayout;
    CheckBox tandcs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registerButton= findViewById(R.id.registerButton);
        fullname=findViewById(R.id.nameInputField);
        phonenumber=findViewById(R.id.phoneInputField);
        email=findViewById(R.id.emailInputField);
        userid=findViewById(R.id.useridInputField);
        tandcs=findViewById(R.id.tandcs);

        fullnameLayout = findViewById(R.id.nameInputFieldLayout);
        phonenumberLayout = findViewById(R.id.phoneInputFieldLayout);
        emailLayout = findViewById(R.id.emailInputFieldLayout);
        useridLayout = findViewById(R.id.useridInputFieldLayout);


        attachFocusChangeListener(fullname);
        attachFocusChangeListener(phonenumber);
        attachFocusChangeListener(email);

        fullname.addTextChangedListener(inputValidationWatcher(fullname,fullnameLayout));
        phonenumber.addTextChangedListener(inputValidationWatcher(phonenumber,phonenumberLayout));
        email.addTextChangedListener(inputValidationWatcher(email,emailLayout));
        userid.addTextChangedListener(inputValidationWatcher(userid,useridLayout));


        Executor executor = Executors.newSingleThreadExecutor();

        registerButton.setOnClickListener(v -> {
            if (validInput()) {
                executor.execute(() -> {
                    int registrationResult = performRegistration(
                            fullname.getText().toString(),
                            phonenumber.getText().toString(),
                            email.getText().toString(),
                            userid.getText().toString()
                    );
                    // Handle results based on boolean and integer values
                    new Handler(Looper.getMainLooper()).post(() -> handleRegistrationResult(registrationResult));


                });
            }
        });

        userid.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                executor.execute(() -> {
                    String text = userid.getText().toString().trim();
                    if (!userid.getText().toString().equals(text)) {
                        userid.setText(text);
                    }
                    int isUserIdAvailable = checkUserIdAvailability(userid.getText().toString());
                    new Handler(Looper.getMainLooper()).post(() -> handleUserIdAvailabilityResult(isUserIdAvailable));

                });
            }
        });




    }

    private boolean validInput() {
        boolean isValid = true;

        fullnameLayout.setError(null);
        phonenumberLayout.setError(null);
        emailLayout.setError(null);
        useridLayout.setError(null);

        if (TextUtils.isEmpty(fullname.getText())) {
            fullnameLayout.setError("Please enter your full name");
            isValid = false;
        }

        if (TextUtils.isEmpty(phonenumber.getText())) {
            phonenumberLayout.setError("Please enter your phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(email.getText()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            emailLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(userid.getText())) {
            useridLayout.setError("Please enter a user ID");
            isValid = false;
        }

        return isValid;
    }

    private int performRegistration(String fullName, String phoneNumber, String email, String userId) {
        int registrationResult = -1; // Default error value

        try {
            URL registerUrl = new URL(REGISTER_URL);
            HttpURLConnection registerConnection = (HttpURLConnection) registerUrl.openConnection();
            registerConnection.setRequestMethod("POST");
            registerConnection.setDoOutput(true);

            String postData = "fullname=" + fullName +
                    "&phonenumber=" + phoneNumber +
                    "&email=" + email +
                    "&userid=" + userId;

            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
            registerConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            OutputStream outputStream = registerConnection.getOutputStream();
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();

            int responseCode = registerConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Registration successful
                registrationResult = 0; // Success
            } else {
                // Registration failed
                registrationResult = responseCode; // Error code
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException
        }

        return registrationResult;
    }

    private int checkUserIdAvailability(String userId) {

        Log.i(TAG,"Checking UserID availability");
        try {
            URL checkUserIdUrl = new URL(CHECK_USERID_URL);
            HttpURLConnection checkUserIdConnection = (HttpURLConnection) checkUserIdUrl.openConnection();
            checkUserIdConnection.setRequestMethod("POST");
            checkUserIdConnection.setDoOutput(true);

            String postData = "userid=" + userId;
            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
            checkUserIdConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            OutputStream outputStream = checkUserIdConnection.getOutputStream();
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();

            int responseCode = checkUserIdConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the server
                try (InputStream inputStream = checkUserIdConnection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
                {

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Check if 'isAvailable' flag exists in JSON
                    if (jsonResponse.has("isAvailable")) {
                        Boolean isAvailable = jsonResponse.getBoolean("isAvailable");
                        // Use the 'isAvailable' flag as needed

                        if (isAvailable){
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                // Handle other response codes or errors if needed
            }

            // Close the connection
            checkUserIdConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
            // Handle IOException
        }

        return -1;
    }


    private void handleRegistrationResult(int registrationResult) {
        // Handle registration result (integer)
        if (registrationResult == 0) {

            Log.i(TAG,"Registration Success.");
            showToast("Registration Success",Toast.LENGTH_SHORT);
            // Registration success
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            Log.i(TAG,"Registration Failure!! Error - "+registrationResult);
            // Registration failed, handle based on error code
        }
    }

    private void handleUserIdAvailabilityResult(int isUserIdAvailable) {
        // Handle user ID availability result (boolean)
        if (isUserIdAvailable == 1) {

            Log.i(TAG,"User ID available.");
            // User ID is available
        } else if (isUserIdAvailable == 0) {
            Log.i(TAG,"User ID not available.");
            useridLayout.setError("Username already exists. Try something else!");
            // User ID is not available
        }
        else{
            Log.i(TAG,"Internal Error.");
            useridLayout.setError("Username Checking failed.Check your network!");
        }
    }

    private TextWatcher inputValidationWatcher(final TextInputEditText editText, final TextInputLayout inputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used, but required method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used, but required method
            }
        };
    }

    private void attachFocusChangeListener(final TextInputEditText editText) {
        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                // Remove trailing spaces when focus is lost
                String text = editText.getText().toString().trim();
                if (!editText.getText().toString().equals(text)) {
                    editText.setText(text);
                }
            }
        });
    }



    private void showToast(String message, int duration) {
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }
}