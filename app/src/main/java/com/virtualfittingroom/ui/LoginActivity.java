package com.virtualfittingroom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.virtualfittingroom.R;
import com.virtualfittingroom.data.PreferenceManager;
import com.virtualfittingroom.data.api.AuthApi;
import com.virtualfittingroom.data.api.UserDataApi;
import com.virtualfittingroom.data.models.UserModel;
import com.virtualfittingroom.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // INIT DATA
        authApi = new AuthApi(getString(R.string.url_base_api), null);

        // ATTACH ACTION
        binding.loginInputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    binding.tilPassword.setError(null);
                    binding.tilPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                }
            }
        });
        // LOGIN
        binding.loginLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        // REGISTER
        binding.loginTextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void setInputEnabled(boolean state){
        binding.loginInputEmail.setEnabled(state);
        binding.loginInputPassword.setEnabled(state);
        binding.loginLoginBtn.setEnabled(state);
        binding.loginProgressbar.setVisibility(state?View.GONE: View.VISIBLE);
    }


    // >>>> LOGIN ROUTINES
    private void doLogin(){
        String ANDROID_ID
                = Settings.Secure.getString(
                getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        setInputEnabled(false);
        authApi.login(
                binding.loginInputEmail.getText().toString(),
                binding.loginInputPassword.getText().toString(),
                ANDROID_ID,
                new AuthApi.ApiLoginCallback() {
                    @Override
                    public void onSuccess(String authToken) {
                        onLoginSuccess(authToken);
                    }

                    @Override
                    public void onError(String message) {
                        onLoginError(message);
                    }

                    @Override
                    public void onFormError(AuthApi.LoginFormErrorData loginFormErrorData) {
                        LoginActivity.this.onFormError(loginFormErrorData);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        LoginActivity.this.onLoginFailure(t);
                    }
                }
        );
    }
    private void onLoginSuccess(String authToken){
        UserDataApi userDataApi = new UserDataApi(getString(R.string.url_base_api), authToken);
        userDataApi.getUserData(new UserDataApi.GetUserDataCallback() {
            @Override
            public void onSuccess(UserModel user) {
                PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                preferenceManager.setLocal(
                        new PreferenceManager.PreferenceData(user, authToken));
                Toast.makeText(LoginActivity.this, "Logged in as " + user.getName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onUnauthenticated() {
                Toast.makeText(LoginActivity.this, "getUserData: Unauthenticated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void onFormError(AuthApi.LoginFormErrorData loginFormErrorData){
        if(loginFormErrorData.getEmail()!=null){
            if(loginFormErrorData.getEmail().length>0){
                binding.loginInputEmail.setError(loginFormErrorData.getEmail()[0]);
            }
        }
        if(loginFormErrorData.getPassword()!=null){
            if(loginFormErrorData.getPassword().length>0){
                binding.tilPassword.setError(loginFormErrorData.getPassword()[0]);
            }
        }
        setInputEnabled(true);
    }

    private void onLoginError(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setInputEnabled(true);
    }

    private void onLoginFailure(Throwable t){
        Toast.makeText(this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        t.printStackTrace();
        setInputEnabled(true);
    }
    // <<<< LOGIN ROUTINES
}