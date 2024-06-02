package com.virtualfittingroom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.virtualfittingroom.R;
import com.virtualfittingroom.data.api.AuthApi;
import com.virtualfittingroom.data.api.UserDataApi;
import com.virtualfittingroom.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthApi authApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // INIT API
        authApi = new AuthApi(getString(R.string.url_base_api), null );

        binding.registerInputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilName.setError(null);
            }
        });
        binding.registerInputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilEmail.setError(null);
            }
        });
        binding.registerInputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilPassword.setError(null);
                binding.tilPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
        binding.registerInputPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilPasswordConfirm.setError(null);
                binding.tilPasswordConfirm.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

        binding.registerRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputEnabled(false);

                authApi.register(
                        binding.registerInputName.getText().toString(),
                        binding.registerInputEmail.getText().toString(),
                        binding.registerInputPassword.getText().toString(),
                        binding.registerInputPasswordConfirm.getText().toString(),
                        new AuthApi.ApiRegisterCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }

                            @Override
                            public void onFormError(AuthApi.RegisterFormErrorData registerFormErrorData) {
                                Toast.makeText(RegisterActivity.this, "Form error", Toast.LENGTH_LONG).show();
                                if(registerFormErrorData.getName()!=null){
                                    if(registerFormErrorData.getName().length>0){
                                        binding.tilName.setError(registerFormErrorData.getName()[0]);
                                    }
                                }
                                if(registerFormErrorData.getEmail()!=null){
                                    if(registerFormErrorData.getEmail().length>0){
                                        binding.tilEmail.setError(registerFormErrorData.getEmail()[0]);
                                    }
                                }
                                if(registerFormErrorData.getPassword()!=null){
                                    if(registerFormErrorData.getPassword().length>0){
                                        binding.tilPassword.setError(registerFormErrorData.getPassword()[0]);
                                    }
                                }
                                if(registerFormErrorData.getPasswordConfirm()!=null){
                                    if(registerFormErrorData.getPasswordConfirm().length>0){
                                        binding.tilPasswordConfirm.setError(registerFormErrorData.getPasswordConfirm()[0]);
                                    }
                                }

                                setInputEnabled(true);
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                                setInputEnabled(true);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                setInputEnabled(true);
                            }
                        }
                );
            }
        });

        binding.registerTextJumptoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setInputEnabled(boolean state){
        binding.registerInputName.setEnabled(state);
        binding.registerInputEmail.setEnabled(state);
        binding.registerInputPassword.setEnabled(state);
        binding.registerInputPasswordConfirm.setEnabled(state);
        binding.registerRegisterBtn.setEnabled(state);
        binding.registerProgressbar.setVisibility(state? View.GONE: View.VISIBLE);
    }
}