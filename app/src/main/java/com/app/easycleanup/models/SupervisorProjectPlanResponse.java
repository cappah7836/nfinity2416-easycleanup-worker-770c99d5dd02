package com.app.easycleanup.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SupervisorProjectPlanResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public class Planning {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("week_no")
        @Expose
        private String weekNo;
        @SerializedName("date")
        @Expose
        private String date;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getWeekNo() {
            return weekNo;
        }

        public void setWeekNo(String weekNo) {
            this.weekNo = weekNo;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }
    public class Data {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("planning")
        @Expose
        private List<Planning> planning = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<Planning> getPlanning() {
            return planning;
        }

        public void setPlanning(List<Planning> planning) {
            this.planning = planning;
        }

    }
}
