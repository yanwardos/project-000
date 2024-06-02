package com.virtualfittingroom.ui.settings;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.virtualfittingroom.data.models.UserModel;

public class SettingsViewModel extends ViewModel {

    // user data
    private MutableLiveData<UserModel> user;
    private MutableLiveData<Boolean> isUserdataUpdateOnProgress;
    private MutableLiveData<Boolean> isUserDataInputChanged;

    // avatar
    private MutableLiveData<Boolean> isAvatarUpdateOnProgress;
    private MutableLiveData<Uri> inputAvatarLocalImagePath;
    private MutableLiveData<Boolean> isLocalImageLoaded;
    private MutableLiveData<String> avatarUpdateMessage;
    private MutableLiveData<Boolean> isMessageError;
    private MutableLiveData<Boolean> isPickingImage;


    // password
    private MutableLiveData<Boolean> isPasswodDataInputChanged;
    private MutableLiveData<Boolean> isPasswordUpdateOnProgress;

    private MutableLiveData<Boolean> isPasswordChangeReadyForInput;

    public SettingsViewModel() {
        // userdata
        user = new MutableLiveData<>();
        isUserdataUpdateOnProgress = new MutableLiveData<>();
        isUserDataInputChanged = new MutableLiveData<>();

        user.setValue(null);
        isUserdataUpdateOnProgress.setValue(false);
        isUserDataInputChanged.setValue(false);


        // avatar
        isAvatarUpdateOnProgress = new MutableLiveData<>();
        inputAvatarLocalImagePath = new MutableLiveData<>();
        isLocalImageLoaded = new MutableLiveData<>();
        avatarUpdateMessage = new MutableLiveData<>();
        isMessageError = new MutableLiveData<>();
        isPickingImage = new MutableLiveData<>();

        isAvatarUpdateOnProgress.setValue(false);
        isLocalImageLoaded.setValue(false);
        avatarUpdateMessage.setValue("");
        isMessageError.setValue(false);
        isPickingImage.setValue(false);

        // password
        isPasswodDataInputChanged = new MutableLiveData<>();
        isPasswordUpdateOnProgress = new MutableLiveData<>();
        isPasswordUpdateOnProgress.setValue(false);
        isPasswordChangeReadyForInput = new MutableLiveData<>();
        isPasswordChangeReadyForInput.setValue(false);
    }

    public MutableLiveData<UserModel> getUser() {
        return user;
    }

    public MutableLiveData<String> getAvatarUpdateMessage() {
        return avatarUpdateMessage;
    }

    public MutableLiveData<Uri> getInputAvatarLocalImagePath() {
        return inputAvatarLocalImagePath;
    }

    public MutableLiveData<Boolean> getIsUserdataUpdateOnProgress() {
        return isUserdataUpdateOnProgress;
    }

    public MutableLiveData<Boolean> getIsAvatarUpdateOnProgress() {
        return isAvatarUpdateOnProgress;
    }

    public MutableLiveData<Boolean> getIsMessageError() {
        return isMessageError;
    }

    public MutableLiveData<Boolean> getIsLocalImageLoaded() {
        return isLocalImageLoaded;
    }

    public MutableLiveData<Boolean> getIsUserDataInputChanged() {
        return isUserDataInputChanged;
    }

    public MutableLiveData<Boolean> getIsPasswodDataInputChanged() {
        return isPasswodDataInputChanged;
    }

    public MutableLiveData<Boolean> getIsPasswordUpdateOnProgress() {
        return isPasswordUpdateOnProgress;
    }

    public MutableLiveData<Boolean> getIsPasswordChangeReadyForInput() {
        return isPasswordChangeReadyForInput;
    }

    public MutableLiveData<Boolean> getIsPickingImage() {
        return isPickingImage;
    }
}