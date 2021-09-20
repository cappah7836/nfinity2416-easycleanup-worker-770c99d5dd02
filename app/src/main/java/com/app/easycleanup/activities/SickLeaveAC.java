package com.app.easycleanup.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.easycleanup.R;
import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;

public class SickLeaveAC extends AppCompatActivity implements View.OnClickListener{
    AppCompatActivity mContext;

    ProgressBar progressBar;
    Button btStartDate, btEndDate, btSend;
    EditText etRemarks;
    CalendarView cvStartDate, cvEndDate;
    LinearLayout llStartDate, llEndDate;
    String endDate = "";
    String startDate = "";
    String empId, token;
    String remarks;
    long leavedays = 1;
    String leavetype ="Sick Leave";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        (mContext).setTitle("Ziekteverlof");
        setContentView(R.layout.fragment_report_sick);

        Window window = mContext.getWindow();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        viewInitialized();
        empId = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ID);
        token =SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token);
    }

    private void viewInitialized() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        btSend = findViewById(R.id.btSend);
        btStartDate = findViewById(R.id.btStartDate);
        btEndDate = findViewById(R.id.btEndDate);
        etRemarks = findViewById(R.id.etRemarks);
        cvStartDate = findViewById(R.id.cvStartDate);
        cvEndDate = findViewById(R.id.cvEndDate);
        llStartDate = findViewById(R.id.llStartDate);
        llEndDate = findViewById(R.id.llEndDate);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.back_viewsick);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btEndDate.setOnClickListener(this);
        btStartDate.setOnClickListener(this);
        btSend.setOnClickListener(this);
        cvStartDate.setMaxDate(System.currentTimeMillis());
        cvStartDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                startDate = dayOfMonth + "/" + (month+1) + "/" + year;
                btStartDate.setText(startDate);


            }
        });


        cvEndDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                endDate = dayOfMonth + "/" + (month+1) + "/" + year;
                btEndDate.setText(endDate);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btEndDate:
                ApplicationUtils.hideKeyboard(mContext);
                llEndDate.setVisibility(View.VISIBLE);
                llStartDate.setVisibility(View.GONE);

                break;
            case R.id.btStartDate:
                ApplicationUtils.hideKeyboard(mContext);
                llEndDate.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);

                break;
            case R.id.btSend:
                ApplicationUtils.hideKeyboard(mContext);
                remarks = etRemarks.getText().toString().trim();
                submitLeave();
                break;


        }
    }

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateRemarks() {
        if (etRemarks.getText().toString().trim().isEmpty()) {
            etRemarks.setError(mContext.getString(R.string.error_remarks));
            requestFocus(etRemarks);
            return false;
        }
        return true;
    }

    private boolean validateStartDate() {
        if(startDate.equals("")) {

            return false;
        }
        return true;
    }


    private void submitLeave() {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);

        if (!validateStartDate()) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert),
                    mContext.getString(R.string.error_startDate));
            return;
        }

        if (!validateRemarks()) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert),
                    mContext.getString(R.string.error_remarks));
            return;
        }

        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {
            showProgressBar(true);

            callLeave(empId, startDate, endDate, leavetype, leavedays, remarks);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLeave(String emp_id, String s_date,String e_date, String l_type, long l_days,String remarks) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.submitVacationLeaves(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token), emp_id, s_date, e_date, l_type, l_days, remarks, new Callback<GeneralResponseData>() {
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
                            mContext.getString(R.string.submit),
                            mContext.getString(R.string.leave_message));
                        etRemarks.setText("");


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
