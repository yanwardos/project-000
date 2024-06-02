package com.virtualfittingroom.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SignalThresholdInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.virtualfittingroom.R;
import com.virtualfittingroom.data.models.UserModel;

import java.util.Calendar;
import java.util.Date;

public class PreferenceManager {
    public static final String TAG = "PreferenceManager";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public PreferenceManager(Context context){
        this.context = context;
        this.sharedPreferences
                = context.getSharedPreferences(
                        context.getString(R.string.KEY_PREFERENCE), Context.MODE_PRIVATE);;
        this.editor = this.sharedPreferences.edit();
        this.gson = new Gson();

        getLocal();
    }

    public PreferenceData getLocal(){
        String jsonData = "";
        try{
            jsonData
                     = this.sharedPreferences.getString(
                             context.getString(R.string.KEY_DATA), "");
            //Log.i(TAG, "getLocal: json: " + jsonData);
        }catch (Throwable t){
            //Log.e(TAG, "getLocal: Failed extracting local json data.", t);
            return null;
        }

        try {
            return this.gson.fromJson(jsonData, PreferenceData.class);
        }catch (Throwable t){
            //Log.e(TAG, "getLocal: Failed casting local json data.", t);
            return null;
        }

    }

    public boolean isInitiated(){
        return this.sharedPreferences.contains(context.getString(R.string.KEY_DATA));
    }

    public boolean setLocal(PreferenceData preferenceData){
        preferenceData.setUnixTime(Calendar.getInstance().getTimeInMillis());
        String preferenceDataString;
        try {
            preferenceDataString
                    = this.gson.toJson(preferenceData);
        }catch (Throwable t){
            //Log.e(TAG, "setLocal: Failed casting preference data to string.", t);
            return false;
        }

        try {
            this.editor.putString(
                    context.getString(R.string.KEY_DATA), preferenceDataString
            );
            return this.editor.commit();
        }catch (Throwable t){
            //Log.e(TAG, "setLocal: Failed saving preference data to memory.", t);
            return false;
        }
    }

    public boolean clearLocal(){
        this.editor.clear();
        return this.editor.commit();
    }

    public static class PreferenceData{
        private long unixTime;
        private UserModel user;
        private String authToken;

        public PreferenceData(UserModel user, String authToken) {
            this.user = user;
            this.authToken = authToken;
        }

        public void setUnixTime(long unixTime) {
            this.unixTime = unixTime;
        }

        public long getUnixTime() {
            return unixTime;
        }

        public UserModel getUser() {
            return user;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setUser(UserModel user) {
            this.user = user;
        }
    }
}
