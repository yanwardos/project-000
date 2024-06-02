package com.virtualfittingroom.data.api;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.virtualfittingroom.data.models.UserModel;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class UserDataApi extends BaseApi{
    private static final String TAG = "UserDataApi";
    private final UserDataApiInterface userDataApiInterface;

    // construct
    public UserDataApi(String base_url, @Nullable String authToken){
        super(base_url, authToken);
        userDataApiInterface = retrofit.create(UserDataApiInterface.class);
    }

    // API INTERFACE
    public interface UserDataApiInterface{
        @GET("userData")
        Call<GetUserDataResponseBody> getUserData();

        @PATCH("userData")
        Call<UpdateUserDataResponseBody> updateUserData(@Body UpdateUserDataRequestBody updateUserDataRequestBody);

        @POST("userAvatar")
        @Multipart
        Call<UpdateAvatarResponseBody> updateAvatar(@Part MultipartBody.Part imgAvatar);

        @PATCH("userPassword")
        Call<PasswordChangeResponseBody> changePassword(@Body PasswordChangeRequestBody changePasswordRequestBody);

    }
    // >>>> CHANGE PASSWORD
    public void changePassword(String newPassword, String confirmPassword, String oldPassword, ApiChangePasswordCallback apiChangePasswordCallback){
        Call<PasswordChangeResponseBody> passwordChangeResponseBodyCall
                = this.userDataApiInterface.changePassword(
                new PasswordChangeRequestBody(newPassword, confirmPassword, oldPassword));

        Log.i(TAG, "changePassword: Enqueue");
        try{
            passwordChangeResponseBodyCall.enqueue(new Callback<PasswordChangeResponseBody>() {
                @Override
                public void onResponse(Call<PasswordChangeResponseBody> call, Response<PasswordChangeResponseBody> response) {
                    if(response.code()>=400){
                        String responseJson = "";
                        try {
                            PasswordChangeResponseBody passwordChangeResponseBody = null;
                            assert response.errorBody() != null;
                            responseJson = response.errorBody().string();
                            passwordChangeResponseBody
                                    = (new Gson()).fromJson(responseJson, PasswordChangeResponseBody.class);

                            if(response.code()==422){
                                apiChangePasswordCallback.onFormError(passwordChangeResponseBody.getData().getFormError());
                                return;
                            }
                            Log.i(TAG, "onResponse: "+ responseJson);
                            apiChangePasswordCallback.onFailure(new Throwable("Unexpected error : " + response.code()));

                        }catch (Throwable t){
                            Log.i(TAG, "onResponse: "+responseJson);
                            apiChangePasswordCallback.onFailure(new Throwable("Error reading error body. Http code: " + response.code()));
                            t.printStackTrace();
                        }
                    }

                    if(response.code()==200){
                        apiChangePasswordCallback.onSuccess(response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<PasswordChangeResponseBody> call, Throwable throwable) {
                    apiChangePasswordCallback.onFailure(new Throwable("changePassword request failed. "));
                    throwable.printStackTrace();
                }
            });
        } catch (Throwable throwable){
            apiChangePasswordCallback.onFailure(new Throwable("changePassword call failed"));
            throwable.printStackTrace();
        }
    }

    // REQUEST
    public class PasswordChangeRequestBody{
        private String newPassword, confirmPassword, currentPassword;

        public PasswordChangeRequestBody(String newPassword, String confirmPassword, String currentPassword) {
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
            this.currentPassword = currentPassword;
        }
    }

    // RESPONSE
    public class PasswordChangeResponseBody extends ResponseBody{
        private PasswordChangeResponseData data;

        public PasswordChangeResponseBody(@Nullable String status, @Nullable String message, PasswordChangeResponseData data) {
            super(status, message);
            this.data = data;
        }

        public PasswordChangeResponseData getData() {
            return data;
        }
    }

    public class PasswordChangeResponseData{
        private @Nullable PasswordChangeFormErrorData formError;

        public PasswordChangeResponseData(@Nullable PasswordChangeFormErrorData formError) {
            this.formError = formError;
        }

        @Nullable
        public PasswordChangeFormErrorData getFormError() {
            return formError;
        }
    }

    public class PasswordChangeFormErrorData{
        private @Nullable String[] newPassword, confirmPassword, currentPassword;

        public PasswordChangeFormErrorData(@Nullable String[] newPassword, @Nullable String[] confirmPassword, @Nullable String[] currentPassword) {
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
            this.currentPassword = currentPassword;
        }

        @Nullable
        public String[] getNewPassword() {
            return newPassword;
        }

        @Nullable
        public String[] getConfirmPassword() {
            return confirmPassword;
        }

        @Nullable
        public String[] getCurrentPassword() {
            return currentPassword;
        }
    }

    // CALLBACK
    public interface ApiChangePasswordCallback{
        void onSuccess(String message);
        void onFormError(PasswordChangeFormErrorData changePasswordFormErrorData);
        void onError(String message);
        void onFailure(Throwable t);
    }
    // <<<< CHANGE PASSWORD

    // >>>> GET USER DATA
    public void getUserData(GetUserDataCallback getUserDataCallback){
        Call<GetUserDataResponseBody> getUserDataResponseBodyCall
                = this.userDataApiInterface.getUserData();

        try {
            getUserDataResponseBodyCall.enqueue(new Callback<GetUserDataResponseBody>() {
                @Override
                public void onResponse(Call<GetUserDataResponseBody> call, Response<GetUserDataResponseBody> response) {
                    if(response.code()>=400){
                        try {
                            GetUserDataResponseBody getUserDataResponseBody = null;
                            assert response.errorBody() != null;
                            String responseJson = response.errorBody().string();
                            getUserDataResponseBody
                                    = (new Gson()).fromJson(responseJson, GetUserDataResponseBody.class);

                            if(response.code()==401){
                                getUserDataCallback.onUnauthenticated();
                                return;
                            }

                            getUserDataCallback.onError("Unexpected Error. Http code: " + response.code());
                        }catch (Throwable t){
                            getUserDataCallback.onFailure(new Throwable("Error reading error body. " +
                                    "Http: code: " + response.code()));;
                            t.printStackTrace();
                        }
                    }

                    if(response.code()==200){
                        getUserDataCallback.onSuccess(response.body().getData().getUser());
                        return;
                    }

                    getUserDataCallback.onFailure(new Throwable("Unexpected error. Http code: " + response.code()));
                }

                @Override
                public void onFailure(Call<GetUserDataResponseBody> call, Throwable throwable) {
                    getUserDataCallback.onFailure(new Throwable("GetUserData request failed."));;
                    throwable.printStackTrace();

                }
            });
        }catch (Throwable t){
            getUserDataCallback.onFailure(new Throwable("GetUserData call failed."));
            t.printStackTrace();
        }
    }

    // == RESPONSE
    public class GetUserDataResponseBody extends ResponseBody{
        GetUserDataResponseData data;

        public GetUserDataResponseBody(String status, String message, GetUserDataResponseData data) {
            super(status, message);
            this.data = data;
        }

        public GetUserDataResponseData getData() {
            return data;
        }
    }

    // == RESPONSE MODEL
    public class GetUserDataResponseData{
        UserModel user;

        public GetUserDataResponseData(UserModel user) {
            this.user = user;
        }

        public UserModel getUser() {
            return user;
        }
    }

    // == CALLBACK
    public interface GetUserDataCallback{
        void onSuccess(UserModel user);
        void onUnauthenticated();
        void onError(String message);
        void onFailure(Throwable t);
    }
    // <<<< GET USER DATA
 


    // = UPDATE USER DATA
    public void updateUserData(String nama, UpdateUserDataCallback updateUserDataCallback){
        Call<UpdateUserDataResponseBody> updateUserDataResponseBodyCall
                = this.userDataApiInterface.updateUserData(
                        new UpdateUserDataRequestBody(nama));

        try {
            updateUserDataResponseBodyCall.enqueue(new Callback<UpdateUserDataResponseBody>() {
                @Override
                public void onResponse(Call<UpdateUserDataResponseBody> call, Response<UpdateUserDataResponseBody> response) {
                    if(response.code()>=400){
                        try {
                            UpdateUserDataResponseBody updateUserDataResponseBody = null;
                            assert response.errorBody() != null;
                            String responseJson = response.errorBody().string();
                            Log.i(TAG, "onResponse: " + responseJson);

                            updateUserDataResponseBody
                                    = (new Gson()).fromJson(responseJson, UpdateUserDataResponseBody.class);

                            if(response.code()==422){
                                updateUserDataCallback.onFormError(updateUserDataResponseBody.getData().getFormError());
                                return;
                            }

                            updateUserDataCallback.onFailure(
                                    new Throwable("Unexpected error. Http code: " + response.code() +
                                            ". Status: " + updateUserDataResponseBody.getStatus() +
                                            ". Message: " + updateUserDataResponseBody.getMessage()));
                            return;
                        }catch (Throwable t){
                            updateUserDataCallback.onFailure(new Throwable("Error reading error body. " +
                                    "Http code: " + response.code()));
                            t.printStackTrace();
                        }
                    }

                    if(response.code()==200){
                        updateUserDataCallback.onSuccess(response.body().getData().getUser());
                    }
                }

                @Override
                public void onFailure(Call<UpdateUserDataResponseBody> call, Throwable throwable) {
                    updateUserDataCallback.onFailure(new Throwable("UpdateUserData request failed."));
                    throwable.printStackTrace();
                }
            });
        }catch (Throwable throwable){
            updateUserDataCallback.onFailure(new Throwable("UpdateUserData call failed."));
            throwable.printStackTrace();
        }
    }
    // == REQUEST
    public class UpdateUserDataRequestBody{
        private String name;

        public UpdateUserDataRequestBody(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    // === REQUEST MODEL

    // == RESPONSE
    public class UpdateUserDataResponseBody extends ResponseBody{
        private UpdateUserDataResponseData data;

        public UpdateUserDataResponseBody(String status, String message, UpdateUserDataResponseData data) {
            super(status, message);
            this.data = data;
        }

        public UpdateUserDataResponseData getData() {
            return data;
        }
    }

    // === RESPONSE MODEL
    public class UpdateUserDataResponseData{
        private UpdateUserDataFormError formError;
        private UserModel user;

        public UpdateUserDataResponseData(UpdateUserDataFormError formError, UserModel user) {
            this.formError = formError;
            this.user = user;
        }

        public UpdateUserDataFormError getFormError() {
            return formError;
        }

        public UserModel getUser() {
            return user;
        }
    }
    public class UpdateUserDataFormError{
        private String[] name;

        public UpdateUserDataFormError(String[] name) {
            this.name = name;
        }

        public String[] getName() {
            return name;
        }

        public void setName(String[] name) {
            this.name = name;
        }
    }

    // = CALLBACK
    public interface UpdateUserDataCallback{
        void onSuccess(UserModel user);
        void onFormError(UpdateUserDataFormError updateUserDataFormError);
        void onFailure(Throwable t);
    }

    // >>>> UPDATE AVATAR
    public void updateAvatar(String imageRealPath, UpdateAvatarCallback updateAvatarCallback){
        // get image
        File image = new File(imageRealPath);
        // create image body
        RequestBody imageBody = RequestBody.create(MediaType.parse("*/*"), image);

        // compile request body
        MultipartBody.Part body = null;
        body = MultipartBody.Part.createFormData("imgAvatar", image.getName(), imageBody);

        Call<UpdateAvatarResponseBody> updateAvatarResponseBodyCall
                = this.userDataApiInterface.updateAvatar(body);

        try {
            updateAvatarResponseBodyCall.enqueue(new Callback<UpdateAvatarResponseBody>() {
                @Override
                public void onResponse(Call<UpdateAvatarResponseBody> call, Response<UpdateAvatarResponseBody> response) {
                    if(response.code()>=400){
                        try {
                            UpdateAvatarResponseBody updateAvatarErrorBody = null;
                            assert response.errorBody() != null;

                            String responseJson = response.errorBody().string();
                            Log.i(TAG, "onResponse: " + responseJson);

                            updateAvatarErrorBody
                                    = (new Gson()).fromJson(responseJson, UpdateAvatarResponseBody.class);

                            if(response.code()==422){
                                updateAvatarCallback.onFormError(updateAvatarErrorBody.getData().getFormError());
                                return;
                            }

                            updateAvatarCallback.onFailure(new Throwable("Unexpected error." +
                                    "Http code: " + response.code() +
                                    "Status: " + updateAvatarErrorBody.getStatus() +
                                    "Message: " + updateAvatarErrorBody.getMessage()));
                            return;
                        }catch (Throwable t){
                            updateAvatarCallback.onFailure(new Throwable("Error reading error body. " +
                                    "Http code: " + response.code()));
                            t.printStackTrace();
                        }
                    }
                    if(response.code()==200){
                        updateAvatarCallback.onSuccess(response.body().getData().getUser());
                        return;
                    }

                }

                @Override
                public void onFailure(Call<UpdateAvatarResponseBody> call, Throwable throwable) {
                    updateAvatarCallback.onFailure(new Throwable("UpdateAvatar request failed."));
                    throwable.printStackTrace();
                }
            });
        }catch (Throwable throwable){
            updateAvatarCallback.onFailure(new Throwable("UpdateAvatar call failed"));
            throwable.printStackTrace();
        }
    }
    // RESPONSE
    public class UpdateAvatarResponseBody extends ResponseBody{
        private UpdateAvatarResponseData data;

        public UpdateAvatarResponseBody(@Nullable String status, @Nullable String message, UpdateAvatarResponseData data) {
            super(status, message);
            this.data = data;
        }

        public UpdateAvatarResponseData getData() {
            return data;
        }
    }

    public class UpdateAvatarResponseData{
        private UpdateAvatarFormError formError;
        private UserModel user;

        public UpdateAvatarResponseData(UpdateAvatarFormError formError, UserModel user) {
            this.formError = formError;
            this.user = user;
        }

        public UpdateAvatarFormError getFormError() {
            return formError;
        }

        public UserModel getUser() {
            return user;
        }
    }

    public class UpdateAvatarFormError{
        private String[] imgAvatar;

        public UpdateAvatarFormError(String[] imgAvatar) {
            this.imgAvatar = imgAvatar;
        }

        public String[] getImgAvatar() {
            return imgAvatar;
        }
    }
    // CALLBACK
    public interface UpdateAvatarCallback{
        void onSuccess(UserModel user);
        void onError(String message);
        void onFormError(UpdateAvatarFormError updateAvatarFormError);
        void onFailure(Throwable throwable);
    }
    // <<<< UPDATE AVATAR
}
