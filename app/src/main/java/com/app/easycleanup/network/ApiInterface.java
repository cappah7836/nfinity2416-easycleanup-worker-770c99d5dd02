package com.app.easycleanup.network;

//Author Muhammad Mubashir 10/30/2018



import com.app.easycleanup.models.AllProjectListResponse;
import com.app.easycleanup.models.EmployeeAttandenceResponse;
import com.app.easycleanup.models.EmployeeListResponse;
import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.models.NameResponseData;

import com.app.easycleanup.models.PlanningResponseData;
import com.app.easycleanup.models.ProjectListResponse;
import com.app.easycleanup.models.SubmitedLeavesResponse;
import com.app.easycleanup.models.SupervisorProjectPlanResponse;
import com.app.easycleanup.models.SupervisorProjectResponse;
import com.app.easycleanup.models.UserAuthResponse;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    //Employee Apis

    @POST("auth/signin")
    Call<UserAuthResponse> userAuthApi(@Body JsonObject params);
    @GET("auth/signout?")
    Call<GeneralResponseData> userLogoutApi(@Query("token") String data);
    @GET("auth/getEmployeePlanningHistory?")
    Call<ProjectListResponse> callGetProject(@Query("token") String data);
    @GET("auth/getEmployeePlanning?")
    Call<PlanningResponseData> callGetProjectPlanning(@Query("token") String data,@Query("emp_id") String emp_id,@Query("plan_date") String plan_date,@Query("project_id") String project_id);
    @POST("auth/employeeCheckIn?")
    Call<GeneralResponseData> callEmployeeCheckIn(@Query("token") String data,@Query("emp_id") String emp_id,@Query("project_id") String project_id,@Query("plan_id") String plan_id,@Query("date_in") String date_in,@Query("time_in") String time_in,@Query("check_in_location") String check_in_location);
    @POST("auth/employeeCheckOut?")
    Call<GeneralResponseData> callEmployeeCheckOut(@Query("token") String data,@Query("emp_id") String emp_id,@Query("project_id") String project_id,@Query("plan_id") String plan_id,@Query("date_out") String date_in,@Query("time_out") String time_in,@Query("check_out_location") String check_in_location);

   //Leaves Api
    @POST("auth/addleaves?")
    Call<GeneralResponseData> submitVacationLeave(@Query("token") String data, @Query("UserId") String emp_id, @Query("StartDate") String s_date, @Query("EndDate") String e_date, @Query("Leavetype") String l_type, @Query("Leavedays") long l_days, @Query("Details") String remarks);

    //All leaves
   @GET("auth/AllLeaves?")
   Call<List<SubmitedLeavesResponse>> callLeavesList(@Query("UserId") String emp_id, @Query("token") String data);

    //All Projects
    @GET("auth/WorkingHistory?")
    Call<List<AllProjectListResponse>> callAllProjectsList(@Query("Id") String emp_id,@Query("token") String data);

   //Email Verificaiton
   @POST("auth/UpdateEmail?")
   Call<GeneralResponseData> sendVerificationEmail(@Query("token") String emp_id, @Query("Email") String email);

    //CODE Verificaiton
    @POST("auth/Verifytoken?")
    Call<GeneralResponseData> callverifyCode(@Query("token") String data, @Query("Email") String email, @Query("CODE") String code , @Query("UserId") String emp_id);

    //Name Change
    @POST("auth/ChangeName?")
    Call<NameResponseData> callchangeName(@Query("token") String data, @Query("UserID") String emp_id, @Query("name") String name);

    //Password Change
    @POST("auth/ChangePassword?")
    Call<GeneralResponseData> callchangePassword(@Query("token") String data, @Query("Password") String password,@Query("UserID") String emp_id, @Query("CurrentPass") String currentpassword);



    //Supervisor Apis

    @GET("auth/getProjectsList/{contact_id}?")
    Call<SupervisorProjectResponse> callGetSupervisorProject(@Path("contact_id") String contact_id,@Query("token") String data);

    @GET("auth/projectPlanning/{project_id}?")
    Call<SupervisorProjectPlanResponse> callGetSupervisorPlan(@Path("project_id") String project_id, @Query("token") String data);

    @GET("auth/employees_list/{plan_id}?")
    Call<EmployeeListResponse> callGetEmployee(@Path("plan_id") String plan_id, @Query("token") String data);

    @GET("auth/checkEmployeeAttendance?")
    Call<EmployeeAttandenceResponse> callEmployeeAttendance(@Query("token") String data, @Query("employee_id") String emp_id,@Query("project_id") String project_id, @Query("date") String date);

    @POST("auth/approveEmployeeAttendance?")
    Call<GeneralResponseData> callEmployeeAttendanceMark(@Query("token") String data, @Query("employee_id") String emp_id,@Query("project_id") String project_id, @Query("date") String date,@Query("approved") String status);

}

