package com.virtualfittingroom.data.api;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.virtualfittingroom.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class AuthApi extends BaseApi{
    private static final String TAG = "AuthApi";

    private final AuthApiInterface authApiInterface;

    public AuthApi(String base_url, @Nullable String authToken) {
        super(base_url, authToken);
        authApiInterface = retrofit.create(AuthApiInterface.class);
    }

    // >>>> MAIN INTERFACE
    interface AuthApiInterface{
        @POST("login")
        Call<LoginResponseBody> login(@Body LoginRequestBody loginRequest);

        @POST("registerUser")
        Call<RegisterResponseBody> register(@Body RegisterRequestBody registerRequestBody);

        @GET("verifyAuth")
        Call<VerifyAuthResponseBody> verifyAuth();
    }
    // <<<< MAIN INTERFACE

    // >>>> LOGIN
    public void login(String email, String password, String deviceId, ApiLoginCallback apiLoginCallback){
        Call<LoginResponseBody> loginResponseBodyCall
                 = this.authApiInterface.login(
                         new LoginRequestBody(email, password, deviceId));

        try {
            loginResponseBodyCall.enqueue(new Callback<LoginResponseBody>() {
                @Override
                public void onResponse(Call<LoginResponseBody> call, Response<LoginResponseBody> response) {
                    if(response.code()>=400){
                        LoginResponseBody loginResponseBody = null;
                        try {
                            assert response.errorBody() != null;
                            String responseJson = response.errorBody().string();
                            Log.d(TAG, "onResponse: " + responseJson);
                            loginResponseBody
                                    = (new Gson()).fromJson(responseJson, LoginResponseBody.class);

                            if(response.code()==422){
                                apiLoginCallback.onFormError(loginResponseBody.getData().getFormError());
                                return;
                            }

                            if(response.code()==401){
                                apiLoginCallback.onError(loginResponseBody.getMessage());
                                return;
                            }
                        }catch (Throwable t){
                            apiLoginCallback.onError("Error reading error body. Http code: " + response.code());
                            t.printStackTrace();
                        }

                    }

                    if(response.code()==200){
                            assert response.body() != null;
                            apiLoginCallback.onSuccess(response.body().getData().getAuthToken());
                            return;
                    }

                    apiLoginCallback.onFailure(new Throwable("Unexpected error. Code: " + response.code()));
                }

                @Override
                public void onFailure(Call<LoginResponseBody> call, Throwable throwable) {
                    apiLoginCallback.onFailure(new Throwable("Login request failed."));
                    throwable.printStackTrace();
                }
            });
        }catch (Throwable t){
            apiLoginCallback.onFailure(new Throwable("Login call failed."));
            t.printStackTrace();
        }
    }

    // LOGIN REQUEST
    public static class LoginRequestBody {
        private String email, password, deviceId;

        public LoginRequestBody(String email, String password, String deviceId) {
            this.email = email;
            this.password = password;
            this.deviceId = deviceId;
        }
    }

    // LOGIN RESPONSE
    public static class LoginResponseBody extends ResponseBody {
        private LoginResponseData data;

        public LoginResponseBody(String status, String message, LoginResponseData loginResponseData) {
            super(status, message);
            this.data = loginResponseData;
        }

        public LoginResponseData getData() {
            return data;
        }
    }

    public static class LoginResponseData{
        private String authToken;
        private LoginFormErrorData formError;

        public LoginResponseData(String authToken, LoginFormErrorData formError) {
            this.authToken = authToken;
            this.formError = formError;
        }

        public String getAuthToken() {
            return authToken;
        }

        public LoginFormErrorData getFormError() {
            return formError;
        }
    }

    public static class LoginFormErrorData{
        private String[] password, email, deviceId;

        public LoginFormErrorData(String[] password, String[] email, String[] deviceId) {
            this.password = password;
            this.email = email;
            this.deviceId = deviceId;
        }

        public String[] getPassword() {
            return password;
        }

        public String[] getEmail() {
            return email;
        }

        public String[] getDeviceId() {
            return deviceId;
        }
    }

    // LOGIN CALLBACK
    public interface ApiLoginCallback{
        void onSuccess(String authToken);
        void onError(String message);
        void onFormError(LoginFormErrorData loginFormErrorData);
        void onFailure(Throwable t);
    }
    // <<<< LOGIN

    // >>>> REGISTER
    public void register(String name, String email, String password, String passwordConfirm, ApiRegisterCallback apiRegisterCallback){
        Call<RegisterResponseBody> registerResponseBodyCall
                = this.authApiInterface.register(
                        new RegisterRequestBody(name, email, password, passwordConfirm));

        try {
            registerResponseBodyCall.enqueue(new Callback<RegisterResponseBody>() {
                @Override
                public void onResponse(Call<RegisterResponseBody> call, Response<RegisterResponseBody> response) {

                    if(response.code()>=400){
                        RegisterResponseBody registerResponseBody = null;
                        // cast
                        try {
                            assert response.errorBody() != null;
                            String responseJson = response.errorBody().string();
                            Log.d(TAG, "onResponse: Error Body: " + responseJson);
                            registerResponseBody
                                    = (new Gson()).fromJson(responseJson, RegisterResponseBody.class);

                            if(response.code()==422){
                                apiRegisterCallback.onFormError(registerResponseBody.getData().getFormError());
                                return;
                            }
                        }catch (Throwable t){
                            apiRegisterCallback.onFailure(new Throwable("Error reading error body. Http code: " + response.code()));
                            t.printStackTrace();
                        }
                    }

                    if(response.code()==201){
                        assert response.body() != null;
                        apiRegisterCallback.onSuccess(response.body().getMessage());
                        return;
                    }

                    apiRegisterCallback.onFailure(new Throwable("Unexpcected error. Http code: " + response.code()));
                }

                @Override
                public void onFailure(Call<RegisterResponseBody> call, Throwable throwable) {
                    apiRegisterCallback.onFailure(new Throwable("Register request error."));
                    throwable.printStackTrace();
                }
            });
        }catch (Throwable t){
            apiRegisterCallback.onFailure(new Throwable("Register call failed."));
            t.printStackTrace();
        }
    }

    // REGISTER REQUEST
    public static class RegisterRequestBody{
        private String name, email, password, passwordConfirm;

        public RegisterRequestBody(String name, String email, String password, String passwordConfirm) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.passwordConfirm = passwordConfirm;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getPasswordConfirm() {
            return passwordConfirm;
        }
    }

    // REGISTER RESPONSE
    public static class RegisterResponseBody extends ResponseBody {
        private RegisterResponseData data;

        public RegisterResponseBody(String status, String message, RegisterResponseData data) {
            super(status, message);
            this.data = data;
        }

        public RegisterResponseData getData() {
            return data;
        }
    }

    public static class RegisterResponseData{
        RegisterFormErrorData formError;

        public RegisterResponseData(RegisterFormErrorData formError) {
            this.formError = formError;
        }

        public RegisterFormErrorData getFormError() {
            return formError;
        }
    }

    public static class RegisterFormErrorData{
        private String[] name, email, password, passwordConfirm;

        public RegisterFormErrorData(String[] name, String[] email, String[] password, String[] passwordConfirm) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.passwordConfirm = passwordConfirm;
        }

        public String[] getName() {
            return name;
        }

        public String[] getEmail() {
            return email;
        }

        public String[] getPassword() {
            return password;
        }

        public String[] getPasswordConfirm() {
            return passwordConfirm;
        }
    }

    // REGISTER CALLBACK
    public interface ApiRegisterCallback{
        void onSuccess(String message);
        void onFormError(RegisterFormErrorData registerFormErrorData);
        void onError(String message);
        void onFailure(Throwable t);
    }
    // <<<< REGISTER


    // >>>> VERIFYAUTH
    public void verifyAuth(ApiVerifyAuthCallback apiVerifyAuthCallback){
        Call<VerifyAuthResponseBody> verifyAuthResponseBodyCall
                = this.authApiInterface.verifyAuth();

        try {
            verifyAuthResponseBodyCall.enqueue(new Callback<VerifyAuthResponseBody>() {
                @Override
                public void onResponse(Call<VerifyAuthResponseBody> call, Response<VerifyAuthResponseBody> response) {
                    if(response.code()>=400){
                        VerifyAuthResponseBody verifyAuthResponseBody = null;
                        try {
                            assert response.errorBody() != null;
                            verifyAuthResponseBody
                                    = (new Gson()).fromJson(response.errorBody().string(), VerifyAuthResponseBody.class);

                            if(response.code()==401){
                                apiVerifyAuthCallback.onInvalid();
                                return;
                            }

                            apiVerifyAuthCallback.onFailure(new Throwable("Error. Http code: " + response.code()));
                            return;
                        }catch (Throwable t){
                            apiVerifyAuthCallback.onFailure(new Throwable("Error reading error body."));
                            t.printStackTrace();
                        }
                    }

                    if(response.code()==200){
                        apiVerifyAuthCallback.onValid();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<VerifyAuthResponseBody> call, Throwable throwable) {
                    apiVerifyAuthCallback.onFailure(new Throwable("verifyAuth request failed."));
                    throwable.printStackTrace();
                }
            });
        }catch (Throwable t){
            apiVerifyAuthCallback.onFailure(new Throwable("verifyAuth call failed."));
            t.printStackTrace();
        }
    }

    // VERIFY AUTH RESPONSE
    public static class VerifyAuthResponseBody extends ResponseBody{
        public VerifyAuthResponseBody(String status, String message) {
            super(status, message);
        }
    }

    // VERIFY AUTH CALLBACK
    public interface ApiVerifyAuthCallback {
        void onValid();
        void onInvalid();
        void onFailure(Throwable t);
    }
    // <<<< VERIFYAUTH

}
