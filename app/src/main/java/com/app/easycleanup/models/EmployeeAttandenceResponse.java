package com.app.easycleanup.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmployeeAttandenceResponse {

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
    public class Data {

        @SerializedName("attendance")
        @Expose
        private Attendance attendance;

        public Attendance getAttendance() {
            return attendance;
        }

        public void setAttendance(Attendance attendance) {
            this.attendance = attendance;
        }

    }
    public class Attendance {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("employee_id")
        @Expose
        private String employeeId;
        @SerializedName("plan_id")
        @Expose
        private String planId;
        @SerializedName("project_id")
        @Expose
        private String projectId;
        @SerializedName("date_in")
        @Expose
        private String dateIn;
        @SerializedName("time_in")
        @Expose
        private String timeIn;
        @SerializedName("date_out")
        @Expose
        private String dateOut;
        @SerializedName("time_out")
        @Expose
        private String timeOut;
        @SerializedName("approved_by_supervisor")
        @Expose
        private String approvedBySupervisor;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getDateIn() {
            return dateIn;
        }

        public void setDateIn(String dateIn) {
            this.dateIn = dateIn;
        }

        public String getTimeIn() {
            return timeIn;
        }

        public void setTimeIn(String timeIn) {
            this.timeIn = timeIn;
        }

        public String getDateOut() {
            return dateOut;
        }

        public void setDateOut(String dateOut) {
            this.dateOut = dateOut;
        }

        public String getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(String timeOut) {
            this.timeOut = timeOut;
        }

        public String getApprovedBySupervisor() {
            return approvedBySupervisor;
        }

        public void setApprovedBySupervisor(String approvedBySupervisor) {
            this.approvedBySupervisor = approvedBySupervisor;
        }

    }
}
