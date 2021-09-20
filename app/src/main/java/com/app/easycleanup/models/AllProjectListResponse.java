package com.app.easycleanup.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllProjectListResponse {

    @SerializedName("Weeknumber")
    @Expose
    private String project_id;
    @SerializedName("Weekcard_Id")
    @Expose
    private String weekcardId;
    @SerializedName("Project_name")
    @Expose
    private String Project_name;
    @SerializedName("emp_name")
    @Expose
    private String emp_name;
    @SerializedName("Total")
    @Expose
    private String Total;
    @SerializedName("Adress")
    @Expose
    private String Address;
    @SerializedName("Mon")
    @Expose
    private String mon;
    @SerializedName("Tue")
    @Expose
    private String tue;
    @SerializedName("Wed")
    @Expose
    private String wed;
    @SerializedName("Thu")
    @Expose
    private String thu;
    @SerializedName("Fri")
    @Expose
    private String fri;
    @SerializedName("Sat")
    @Expose
    private String sat;
    @SerializedName("Sun")
    @Expose
    private String sun;

    public String getWeekcardId() {
        return weekcardId;
    }

    public void setWeekcardId(String weekcardId) {
        this.weekcardId = weekcardId;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return Project_name;
    }

    public void setProject_name(String project_name) {
        Project_name = project_name;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
