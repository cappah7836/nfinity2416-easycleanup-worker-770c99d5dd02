package com.app.easycleanup.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralResponseData {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("status")
    @Expose
    private String status2;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("message")
    @Expose
    private String message2;

    public String getStatus() {
        return status;
    }

    public String getStatus2() {
        return status2;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public void setStatus2(String status2) {
        this.status2 = status2;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}