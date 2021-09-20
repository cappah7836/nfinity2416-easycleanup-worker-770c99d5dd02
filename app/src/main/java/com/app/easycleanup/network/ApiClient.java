package com.app.easycleanup.network;


import com.app.easycleanup.models.EmployeeAttandenceResponse;
import com.app.easycleanup.models.EmployeeListResponse;
import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.models.NameResponseData;

import com.app.easycleanup.models.PlanningResponseData;
import com.app.easycleanup.models.ProjectListResponse;
import com.app.easycleanup.models.SupervisorProjectPlanResponse;
import com.app.easycleanup.models.SupervisorProjectResponse;
import com.app.easycleanup.models.UserAuthResponse;
import com.app.easycleanup.utils.AppController;
import com.app.easycleanup.utils.SharedPreference;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;


//Author Muhammad Manan 07/09/2021

public class ApiClient {
    private ApiInterface apiInterface;
    private static ApiClient apiClient;
    private Gson gson;


    private OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json");
                        ongoing.addHeader("Content-Type", "application/json");

                        if(SharedPreference.getInstance(AppController.getInstance().getApplicationContext()).getBoolean(SharedPreference.Key.IsTokenApplied)){
                            ongoing.addHeader("Authorization", (SharedPreference.getInstance(AppController.getInstance().getApplicationContext()).getString(SharedPreference.Key.Token)));

                        }


                        return chain.proceed(ongoing.build());
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }


    public ApiClient() {
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUrls.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        this.apiInterface = retrofit.create(ApiInterface.class);
    }

    public static ApiClient getInstance() {
        if (apiClient == null) {
            setInstance(new ApiClient());
        }
        return apiClient;
    }

    public static void setInstance(ApiClient apiClient) {
        ApiClient.apiClient = apiClient;
    }



    public void userAuthApi(@Body JsonObject params, Callback<UserAuthResponse> callback) {
        Call<UserAuthResponse> call = this.apiInterface.userAuthApi(params);
        call.enqueue(callback);
    }
    public void userLogoutApi(String token,Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.userLogoutApi(token);
        call.enqueue(callback);
    }
    public void callGetProject(String token ,Callback<ProjectListResponse> callback) {
        Call<ProjectListResponse> call = this.apiInterface.callGetProject(token);
        call.enqueue(callback);
    }
    public void callGetProjectPlanning(String token, String empId,String date,String projectId,Callback<PlanningResponseData> callback) {
        Call<PlanningResponseData> call = this.apiInterface.callGetProjectPlanning(token,empId,date,projectId);
        call.enqueue(callback);
    }

    public void callEmployeeCheckIn(String token, String emp_id,String project_id,String plan_id,String date_in,String time_in,String location,Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.callEmployeeCheckIn(token,emp_id,project_id,plan_id,date_in,time_in,location);
        call.enqueue(callback);
    }
    public void callEmployeeCheckOut(String token ,String emp_id,String project_id,String plan_id,String date_in,String time_in,String location, Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.callEmployeeCheckOut(token,emp_id,project_id,plan_id,date_in,time_in,location);
        call.enqueue(callback);
    }

    public void submitVacationLeaves(String token, String emp_id, String s_date,String e_date, String l_type, long l_days,String remarks, Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.submitVacationLeave(token,emp_id,s_date,e_date,l_type,l_days,remarks);
        call.enqueue(callback);
    }

    public void submitEmailVerification(String token, String email, Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.sendVerificationEmail(token,email);
        call.enqueue(callback);
    }

    public void callVerifyCode(String token, String email,String code,String emp_id, Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.callverifyCode(token,email,code,emp_id);
        call.enqueue(callback);
    }

    public void callChangeName(String token, String emp_id,String name, Callback<NameResponseData> callback) {
        Call<NameResponseData> call = this.apiInterface.callchangeName(token,emp_id,name);
        call.enqueue(callback);
    }

    public void callChangePassword(String token, String password,String emp_id,String currentpassword, Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.callchangePassword(token,password,emp_id,currentpassword);
        call.enqueue(callback);
    }


    public void callGetSupervisorProject(String id,String token, Callback<SupervisorProjectResponse> callback) {
        Call<SupervisorProjectResponse> call = this.apiInterface.callGetSupervisorProject(id,token);
        call.enqueue(callback);
    }
    public void callGetSupervisorPlan(String id,String token, Callback<SupervisorProjectPlanResponse> callback) {
        Call<SupervisorProjectPlanResponse> call = this.apiInterface.callGetSupervisorPlan(id,token);
        call.enqueue(callback);
    }
    public void callGetEmployee(String id,String token, Callback<EmployeeListResponse> callback) {
        Call<EmployeeListResponse> call = this.apiInterface.callGetEmployee(id,token);
        call.enqueue(callback);
    }
    public void callEmployeeAttendance(String token,String empId,String projectId,String date ,Callback<EmployeeAttandenceResponse> callback) {
        Call<EmployeeAttandenceResponse> call = this.apiInterface.callEmployeeAttendance(token,empId,projectId,date);
        call.enqueue(callback);
    }

    public void callEmployeeAttendanceMark(String token,String empId,String projectId,String date ,String status,Callback<GeneralResponseData> callback) {
        Call<GeneralResponseData> call = this.apiInterface.callEmployeeAttendanceMark(token,empId,projectId,date,status);
        call.enqueue(callback);
    }

}
