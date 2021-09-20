package com.app.easycleanup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.easycleanup.R;
import com.app.easycleanup.models.EmployeeAttandenceResponse;
import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;

public class EmployeeAttandence extends AppCompatActivity implements View.OnClickListener {
    Button btApproved,btBack;
    TextView tvDateIn,tvDateOut,tvTimeIn,tvTimeOut,tvLable,tvTotal;
    ProgressBar progressBar;
    ImageView ivBackground;
    AppCompatActivity mContext;
    String empId,projectID,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attandence);
        mContext=this;
        viewInitialized();
        empId = getIntent().getStringExtra("emp_id");
        projectID = getIntent().getStringExtra("project_id");
        date = getIntent().getStringExtra("date");
        btApproved.setEnabled(false);

        submitGetEmployee(empId,projectID,date);
    }
    private void viewInitialized() {


        progressBar = findViewById(R.id.progressBar);
        ivBackground = findViewById(R.id.ivBackground);
        tvLable = findViewById(R.id.tvLable);

        tvDateIn = findViewById(R.id.tvDate);
        tvDateOut = findViewById(R.id.tvDate2);
        tvTimeIn = findViewById(R.id.tvTime);
        tvTotal = findViewById(R.id.tvTotal);
        tvTimeOut = findViewById(R.id.tvTimeOut);
        btBack = findViewById(R.id.bt_back);
        btApproved = findViewById(R.id.btApprove);
        tvLable.setText("Aanwezigheid van werknemers");
        btBack.setOnClickListener(this);
        btApproved.setOnClickListener(this);



    }
    private void setData(String dateIn,String dateOut,String timeIn,String timeOut,String status){
        if(status.equalsIgnoreCase("1")){
            btApproved.setEnabled(false);
            btApproved.setText("Goedgekeurd");
            btApproved.setBackground(mContext.getResources().getDrawable(R.drawable.bg_button_next));
            ivBackground.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        }else {
            btApproved.setEnabled(true);
            btApproved.setText("Goedkeuren");
            btApproved.setBackground(mContext.getResources().getDrawable(R.drawable.bg_end_button));
            ivBackground.setBackgroundColor(mContext.getResources().getColor(R.color.red));


        }

        tvDateIn.setText(dateIn);
        tvDateOut.setText(dateOut);
        tvTimeIn.setText(timeIn);
        tvTimeOut.setText(timeOut);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(timeIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(timeOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);

        tvTotal.setText(hours+" hour :"+min+" min");


    }
    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_back:

                onBackPressed();
                break;
            case R.id.btApprove:
                submitMarkedAttandence(empId,projectID,date,"1");

                break;

        }
    }


    private void submitGetEmployee(String empId,String ProjectId,String date) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callGetEmployee(empId,ProjectId,date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetEmployee(String empId,String ProjectId,String date) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callEmployeeAttendance(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),empId,ProjectId,date, new Callback<EmployeeAttandenceResponse>() {
            @Override
            public void onResponse(Call<EmployeeAttandenceResponse> call,
                                   final retrofit2.Response<EmployeeAttandenceResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(1)) {
                        if(response.body().getData().getAttendance()!=null && !response.body().getData().getAttendance().equals(""))
                        setData(response.body().getData().getAttendance().getDateIn(),response.body().getData().getAttendance().getDateOut(),response.body().getData().getAttendance().getTimeIn(),response.body().getData().getAttendance().getTimeOut(),response.body().getData().getAttendance().getApprovedBySupervisor());

                    }

                } else {
                    if (response.code() == 401)
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.data_not_found));
                    if (response.code() == 404)
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<EmployeeAttandenceResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_api_not_found));
                else if (t instanceof SocketTimeoutException)
                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else
                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    private void submitMarkedAttandence(String emp_id,String project_id,String date,String status) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);


            callMarked(empId,projectID,date,status);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callMarked(String emp_id,String project_id,String date1,String status) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callEmployeeAttendanceMark(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),emp_id,project_id,date1,status,new Callback<GeneralResponseData>() {

            @Override
            public void onResponse(Call<GeneralResponseData> call,
                                   final retrofit2.Response<GeneralResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {

                        CustomResponseDialog.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.success),
                               response.body().getMessage());

                        submitGetEmployee(empId,projectID,date);




                } else {
                    if (response.code() == 401)

                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.error_user_not_exist));

                    if (response.code() == 404)

                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));

                    if (response.code() == 500)

                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponseData> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)

                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_api_not_found));

                else if (t instanceof SocketTimeoutException)

                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));

                else

                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());

            }
        });
    }



}
