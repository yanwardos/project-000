package com.virtualfittingroom.ui;


import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.virtualfittingroom.R;
import com.virtualfittingroom.data.PreferenceManager;
import com.virtualfittingroom.data.api.AuthApi;
import com.virtualfittingroom.data.api.UserDataApi;
import com.virtualfittingroom.data.models.UserModel;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String TAG = "SplashScreenActivity";

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.preferenceManager = new PreferenceManager(getApplicationContext());


        // 1 CHECK LOCAL DATA
        if(!preferenceManager.isInitiated()){
            launchLoginActivity();
            return;
        }

        String authToken = "";
        if(preferenceManager.getLocal()!=null){
            try {
                authToken =
                     preferenceManager.getLocal().getAuthToken();
            }catch (Throwable t){
                Log.e(TAG, "onCreate: authToken not found", t);
            }
        }

        // 2 VERIFY LOCAL TOKEN
        AuthApi authApi = new AuthApi(getString(R.string.url_base_api), authToken);
        try {
            authApi.verifyAuth(new AuthApi.ApiVerifyAuthCallback() {
                @Override
                public void onValid() {
                    launchMainActivity();
                }

                @Override
                public void onInvalid() {
                    launchLoginActivity();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(SplashScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Throwable t){
            Log.e(TAG, "onCreate: Failed verifying auth.", t);
        }

    }

    public void launchMainActivity(){
        // get user data (and preferences)

        UserDataApi userDataApi = new UserDataApi(getString(R.string.url_base_api), preferenceManager.getLocal().getAuthToken());
        userDataApi.getUserData(new UserDataApi.GetUserDataCallback() {
            @Override
            public void onSuccess(UserModel user) {
                // update local preference
                preferenceManager.setLocal(new PreferenceManager.PreferenceData(
                        user, preferenceManager.getLocal().getAuthToken()
                ));

                // launch
                Toast.makeText(SplashScreenActivity.this, "Welcome! " + user.getName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onUnauthenticated() {
                launchLoginActivity();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SplashScreenActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(SplashScreenActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void launchLoginActivity(){
        Toast.makeText(SplashScreenActivity.this, "Not logged in. Please login.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }

}