package com.app.easycleanup.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlanningResponseData {

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


public class ProjectDetails_ {

    @SerializedName("DeptName")
    @Expose
    private String deptName;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("Contact_ID")
    @Expose
    private String contactID;
    @SerializedName("Contact_phone")
    @Expose
    private String contactPhone;
    @SerializedName("Contact_Firstname")
    @Expose
    private String contactFirstname;
    @SerializedName("Contact_Lastname")
    @Expose
    private String contactLastname;
    @SerializedName("Zipcode")
    @Expose
    private String zipcode;
    @SerializedName("City")
    @Expose
    private String city;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactFirstname() {
        return contactFirstname;
    }

    public void setContactFirstname(String contactFirstname) {
        this.contactFirstname = contactFirstname;
    }

    public String getContactLastname() {
        return contactLastname;
    }

    public void setContactLastname(String contactLastname) {
        this.contactLastname = contactLastname;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
    public class Data {

        @SerializedName("project_details")
        @Expose
        private ProjectDetails_ projectDetails;
        @SerializedName("did_check_in")
        @Expose
        private Boolean didCheckIn;
        @SerializedName("did_check_out")
        @Expose
        private Boolean didCheckOut;
        @SerializedName("check_in_time")
        @Expose
        private String checkInTime;
        @SerializedName("check_out_time")
        @Expose
        private String checkOutTime;

        public ProjectDetails_ getProjectDetails() {
            return projectDetails;
        }

        public void setProjectDetails(ProjectDetails_ projectDetails) {
            this.projectDetails = projectDetails;
        }

        public Boolean getDidCheckIn() {
            return didCheckIn;
        }

        public void setDidCheckIn(Boolean didCheckIn) {
            this.didCheckIn = didCheckIn;
        }

        public Boolean getDidCheckOut() {
            return didCheckOut;
        }

        public void setDidCheckOut(Boolean didCheckOut) {
            this.didCheckOut = didCheckOut;
        }

        public String getCheckInTime() {
            return checkInTime;
        }

        public void setCheckInTime(String checkInTime) {
            this.checkInTime = checkInTime;
        }

        public String getCheckOutTime() {
            return checkOutTime;
        }

        public void setCheckOutTime(String checkOutTime) {
            this.checkOutTime = checkOutTime;
        }

    }

    }