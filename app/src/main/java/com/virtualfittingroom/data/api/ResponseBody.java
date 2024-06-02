package com.virtualfittingroom.data.api;

import androidx.annotation.Nullable;

public class ResponseBody {
    @Nullable
    private String status, message;

    public ResponseBody(@Nullable String status, @Nullable String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        if(status==null) return "";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        if(message==null) return "";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
