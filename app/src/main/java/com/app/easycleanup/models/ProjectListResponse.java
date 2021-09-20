package com.app.easycleanup.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectListResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Record {

        @SerializedName("project_id")
        @Expose
        private String projectId;
        @SerializedName("project_name")
        @Expose
        private String projectName;
        @SerializedName("Location")
        @Expose
        private String location;
        @SerializedName("plan_date")
        @Expose
        private String planDate;
        @SerializedName("plan_id")
        @Expose
        private String planId;
        @SerializedName("Notes")
        @Expose
        private String notes;
        @SerializedName("Geschikt")
        @Expose
        private String geschikt;
        @SerializedName("functie")
        @Expose
        private String functie;
        @SerializedName("employee_id")
        @Expose
        private String employeeId;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPlanDate() {
            return planDate;
        }

        public void setPlanDate(String planDate) {
            this.planDate = planDate;
        }

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getGeschikt() {
            return geschikt;
        }

        public void setGeschikt(String geschikt) {
            this.geschikt = geschikt;
        }

        public String getFunctie() {
            return functie;
        }

        public void setFunctie(String functie) {
            this.functie = functie;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

    }
    public class Data {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("records")
        @Expose
        private List<Record> records = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<Record> getRecords() {
            return records;
        }

        public void setRecords(List<Record> records) {
            this.records = records;
        }

    }
}
