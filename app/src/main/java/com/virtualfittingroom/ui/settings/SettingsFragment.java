package com.virtualfittingroom.ui.settings;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.virtualfittingroom.R;
import com.virtualfittingroom.data.PreferenceManager;
import com.virtualfittingroom.data.api.UserDataApi;
import com.virtualfittingroom.data.models.UserModel;
import com.virtualfittingroom.databinding.FragmentSettingsBinding;
import com.virtualfittingroom.ui.MainActivity;

import java.io.File;

public class SettingsFragment extends Fragment {
    public static final String TAG = "SettingsFragment";

    private SettingsViewModel mViewModel;

    private FragmentSettingsBinding binding;

    public static final String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final int REQUEST_IMAGE_PICKER = 199;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        >>>>> VIEW INIT
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
//        <<<<< VIEW INIT

        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // >>>> SETUP DATA
        PreferenceManager.PreferenceData preferenceData = (new PreferenceManager(getActivity().getApplicationContext()).getLocal());
        UserDataApi userDataApi = new UserDataApi(getString(R.string.url_base_api), preferenceData.getAuthToken());
        // <<<< SETUP DATA

        // >>>> SETUP VIEW DATA
        mViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if(userModel==null) return;
                binding.etNama.setText(userModel.getName());
                binding.etEmail.setText(userModel.getEmail());
                Picasso.get()
                        .load(userModel.getAvatar())
                        .placeholder(R.drawable.placehold_avatar)
                        .into(binding.ivAvatar);
                mViewModel.getIsLocalImageLoaded().setValue(false);
                mViewModel.getIsUserDataInputChanged().setValue(false);
            }
        });
        mViewModel.getIsUserdataUpdateOnProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etNama.setEnabled(!aBoolean);
                binding.btnUpdateUserInfo.setEnabled(!aBoolean);
                binding.progress.setVisibility(aBoolean?View.VISIBLE:View.GONE);
            }
        });
        mViewModel.getIsUserDataInputChanged().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.btnUpdateUserInfo.setEnabled(aBoolean);
            }
        });
        binding.etNama.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mViewModel.getUser().getValue()==null) return;

                if(!s.toString().equals(mViewModel.getUser().getValue().getName())){
                    mViewModel.getIsUserDataInputChanged().setValue(true);
                }else{
                    mViewModel.getIsUserDataInputChanged().setValue(false);
                }
            }
        });

        mViewModel.getIsAvatarUpdateOnProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.btnUpdateAvatar.setEnabled(!aBoolean);
                binding.ivAvatar.setEnabled(!aBoolean);
                binding.progress.setVisibility(aBoolean?View.VISIBLE:View.GONE);
            }
        });
        mViewModel.getInputAvatarLocalImagePath().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                if(uri==null){
                    Log.i(TAG, "onChanged: uri null");
                    mViewModel.getAvatarUpdateMessage().setValue("Gambar tidak ditemukan");
                    mViewModel.getIsMessageError().setValue(true);
                    Log.i(TAG, "onChanged: file not found: uri null");
                    return;
                }

                File file = new File(getRealPathFromURI(uri));
                if(!file.exists()){
                    mViewModel.getAvatarUpdateMessage().setValue("Gambar tidak ditemukan");
                    mViewModel.getIsMessageError().setValue(true);
                    Log.i(TAG, "onChanged: file not found: file not exist");
                    return;
                }
                Log.i(TAG, "onChanged: uri: " + uri.getPath());
                binding.ivAvatar.setImageURI(uri);
            }
        });
        mViewModel.getIsLocalImageLoaded().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.btnUpdateAvatar.setEnabled(aBoolean);
            }
        });
        mViewModel.getAvatarUpdateMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s==null) return;
                binding.tvAvatarMessage.setText(s);
            }
        });
        mViewModel.getIsMessageError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.tvAvatarMessage.setTextColor(
                        aBoolean?getResources().getColor(R.color.error):getResources().getColor(R.color.secondary)
                );
            }
        });
        mViewModel.getUser().setValue(preferenceData.getUser());
        mViewModel.getIsUserDataInputChanged().setValue(false);
        // <<<< SETUP VIEW DATA

        // >>>> PASSWORD
        mViewModel.getIsPasswordUpdateOnProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etNewPassword.setEnabled(!aBoolean);
                binding.etConfirmPassword.setEnabled(!aBoolean);
                binding.etCurrentPassword.setEnabled(!aBoolean);
                binding.btnUpdatePassword.setEnabled(!aBoolean);
                binding.progress.setVisibility(aBoolean?View.VISIBLE:View.GONE);
            }
        });
        mViewModel.getIsPasswordChangeReadyForInput().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etNewPassword.setEnabled(aBoolean);
                binding.etNewPassword.setText("");
                binding.etConfirmPassword.setEnabled(aBoolean);
                binding.etConfirmPassword.setText("");
                binding.etCurrentPassword.setEnabled(true);
                binding.etCurrentPassword.setText("");
            }
        });
        binding.etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilNewPassword.setError(null);
                binding.tilNewPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
        binding.etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilConfirmPassword.setError(null);
                binding.tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
        binding.etCurrentPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) return;
                binding.tilCurrentPassword.setError(null);
                binding.tilCurrentPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.getIsPasswordUpdateOnProgress().setValue(true);

                // CALL API
                userDataApi.changePassword(
                        binding.etNewPassword.getText().toString(),
                        binding.etConfirmPassword.getText().toString(),
                        binding.etCurrentPassword.getText().toString(),
                        new UserDataApi.ApiChangePasswordCallback() {
                            @Override
                            public void onSuccess(String message) {
                                mViewModel.getIsPasswordUpdateOnProgress().setValue(false);
                                mViewModel.getIsPasswordChangeReadyForInput().setValue(true);

                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFormError(UserDataApi.PasswordChangeFormErrorData changePasswordFormErrorData) {
                                if(changePasswordFormErrorData.getNewPassword()!=null){
                                    if(changePasswordFormErrorData.getNewPassword().length>0){
                                        binding.tilNewPassword.setError(changePasswordFormErrorData.getNewPassword()[0]);
                                    }
                                }

                                if( changePasswordFormErrorData.getConfirmPassword()!=null){
                                    if(changePasswordFormErrorData.getConfirmPassword().length>0){
                                        binding.tilConfirmPassword.setError(changePasswordFormErrorData.getConfirmPassword()[0]);
                                    }
                                }

                                if(changePasswordFormErrorData.getCurrentPassword()!=null){
                                    if(changePasswordFormErrorData.getCurrentPassword().length>0){
                                        binding.tilCurrentPassword.setError(changePasswordFormErrorData.getCurrentPassword()[0]);
                                    }
                                }

                                mViewModel.getIsPasswordUpdateOnProgress().setValue(false);
                                Toast.makeText(getContext(), "Periksa data!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                mViewModel.getIsPasswordUpdateOnProgress().setValue(false);

                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                mViewModel.getIsPasswordUpdateOnProgress().setValue(false);

                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onFailure: ", t);
                            }
                        }
                );
            }
        });
        mViewModel.getIsPasswordChangeReadyForInput().setValue(true);
        // <<<< PASSWORD

        // >>>> SETUP BUTTONS
        binding.btnUpdateUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.getIsUserdataUpdateOnProgress().setValue(true);
                try {
                    userDataApi.updateUserData(binding.etNama.getText().toString(), new UserDataApi.UpdateUserDataCallback() {
                        @Override
                        public void onSuccess(UserModel user) {
                            ((MainActivity) getActivity()).updateUserData();

                            mViewModel.getIsUserdataUpdateOnProgress().setValue(false);
                            mViewModel.getIsUserDataInputChanged().setValue(false);
                            mViewModel.getUser().setValue(user);
                            binding.etNama.setEnabled(true);
                            Toast.makeText(getContext(), "Berhasil memperbarui data", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFormError(UserDataApi.UpdateUserDataFormError updateUserDataFormError) {
                            if(updateUserDataFormError.getName()!=null){
                                if(updateUserDataFormError.getName().length>0){
                                    binding.tilNama.setError(updateUserDataFormError.getName()[0]);
                                }
                            }
                            mViewModel.getIsUserdataUpdateOnProgress().setValue(false);
                            binding.etNama.setEnabled(true);
                            Toast.makeText(getContext(), "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            mViewModel.getIsUserdataUpdateOnProgress().setValue(false);
                            binding.etNama.setEnabled(true);

                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
                }catch (Throwable throwable){
                    mViewModel.getIsUserdataUpdateOnProgress().setValue(false);
                    binding.etNama.setEnabled(true);

                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Error calling api.", throwable);
                }
            }
        });
        binding.btnUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewModel.getInputAvatarLocalImagePath().getValue()==null){
                    mViewModel.getAvatarUpdateMessage().setValue("Gambar tidak ditemukan");
                    mViewModel.getIsMessageError().setValue(true);
                    return;
                }
                mViewModel.getIsAvatarUpdateOnProgress().setValue(true);
                String imageRealPath = getRealPathFromURI(mViewModel.getInputAvatarLocalImagePath().getValue());
                try {
                    userDataApi.updateAvatar(imageRealPath, new UserDataApi.UpdateAvatarCallback() {
                        @Override
                        public void onSuccess(UserModel user) {
                            ((MainActivity) getActivity()).updateUserData();

                            mViewModel.getIsAvatarUpdateOnProgress().setValue(false);
                            mViewModel.getIsLocalImageLoaded().setValue(false);
                            mViewModel.getUser().setValue(user);
                            mViewModel.getIsMessageError().setValue(false);
                            mViewModel.getAvatarUpdateMessage().setValue("Berhasil memperbarui avatar!");
                            Toast.makeText(getContext(), "Berhasil memperbarui avatar", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            mViewModel.getIsAvatarUpdateOnProgress().setValue(false);
                            mViewModel.getIsLocalImageLoaded().setValue(false);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFormError(UserDataApi.UpdateAvatarFormError updateAvatarFormError) {
                            onAvatarFormError(updateAvatarFormError);
                            mViewModel.getIsAvatarUpdateOnProgress().setValue(false);
                            mViewModel.getIsLocalImageLoaded().setValue(false);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            mViewModel.getIsAvatarUpdateOnProgress().setValue(false);
                            mViewModel.getIsLocalImageLoaded().setValue(false);

                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onFailure: ", throwable);
                        }
                    });
                } catch (Throwable throwable){
                    Log.e(TAG, "onClick: Error calling api", throwable);
                    mViewModel.getIsAvatarUpdateOnProgress().setValue(false);
                    mViewModel.getIsLocalImageLoaded().setValue(false);
                }
            }
        });
        binding.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Boolean.TRUE.equals(mViewModel.getIsPickingImage().getValue())) return;

                mViewModel.getIsPickingImage().setValue(true);
                if(!isStoragePermissionGranted()){
                    askStoragePermission();
                }

                // ACTION_PICK
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(intent, REQUEST_IMAGE_PICKER);
            }
        });
        // <<<< SETUP BUTTONS
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // UTILS
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null,
                    null);

            if(cursor == null) return "";
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

//    >>>> UTILS - IMAGE PICKER
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); 
        if(requestCode== REQUEST_IMAGE_PICKER){
            if(resultCode==getActivity().RESULT_CANCELED) return;
            if(data==null) return;
            if(data.getData()==null) return;
            mViewModel.getInputAvatarLocalImagePath().setValue(data.getData());
            mViewModel.getIsLocalImageLoaded().setValue(true);
            mViewModel.getAvatarUpdateMessage().setValue("Gambar siap diupload");
            mViewModel.getIsMessageError().setValue(false);
            mViewModel.getIsPickingImage().setValue(false);
        }
    }
//    <<<< UTILS - IMAGE PICKER

//    >>>> UTILS - PERMISSION
    private boolean isStoragePermissionGranted() {
        for (String permission : PERMISSION_STORAGE) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return  true;
    }

    private void askStoragePermission(){
        for(String permission : PERMISSION_STORAGE){
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, 1);
        }
    }
//    <<<< UTILS - PERMISSION

    // ERRORS
    private void onUserDataFormError(UserDataApi.UpdateUserDataFormError updateUserDataFormError){
    }

    private void onAvatarFormError(UserDataApi.UpdateAvatarFormError updateAvatarFormError){
        if(updateAvatarFormError.getImgAvatar()!=null){
            if(updateAvatarFormError.getImgAvatar().length>0){
                mViewModel.getAvatarUpdateMessage().setValue(updateAvatarFormError.getImgAvatar()[0]);
                mViewModel.getIsMessageError().setValue(true);
            }
        }
    }
}