package com.app.easycleanup.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.easycleanup.R;
import com.app.easycleanup.models.GeneralResponseData;

import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;

import java.io.IOException;
import java.net.SocketTimeoutException;

import in.aabhasjindal.otptextview.OtpTextView;
import retrofit2.Call;
import retrofit2.Callback;

public class OtpAC extends AppCompatActivity implements View.OnClickListener{
    AppCompatActivity mContext;
    ImageView back;
    Button verify;
    TextView emailtv, resend;
    String empId, emailAddress, otp;
    OtpTextView otpTextView;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mContext = this;

        emailAddress =getIntent().getStringExtra("email");
      //  Toast.makeText(mContext, ""+ emailAddress, Toast.LENGTH_SHORT).show();

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
    }

    private void viewInitialized() {


        back = findViewById(R.id.goback2);
        emailtv = findViewById(R.id.tv_emailuser);
        otpTextView = findViewById(R.id.otp_view);
        verify = findViewById(R.id.btn_verify);
        progressBar = findViewById(R.id.progressBarotp);
        emailtv.setText(emailAddress);
        back.setOnClickListener(this);
        verify.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goback2:
                onBackPressed();
                break;
            case R.id.btn_verify:
                VerifyOtp();
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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateOtp() {
        if (otpTextView.getOTP().trim().isEmpty()) {
            otpTextView.showError();
            requestFocus(otpTextView);
            return false;
        }
        return true;
    }

    private void VerifyOtp() {
        if (!validateOtp()) {
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
            otp = otpTextView.getOTP();

           callOtpVerify(emailAddress, otp, empId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callOtpVerify(String email, String code, String emp_id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callVerifyCode(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),email,code,emp_id, new Callback<GeneralResponseData>() {
            @Override
            public void onResponse(Call<GeneralResponseData> call,
                                   final retrofit2.Response<GeneralResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    Log.d("Response", response.body().getMessage());

                    GeneralResponseData res = response.body();

                    if (res.getMessage().equals("Your Email Has been Updated")) {
                                CustomResponseDialog.showDropDownSuccessNotification(
                                        mContext,
                                        mContext.getString(R.string.success),
                                        "Code geverifieerd, e-mail bijgewerkt");
                        final int interval = 2000; // 1 Second
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable(){
                            public void run() {
                                submitAuthLogout();
                            }
                        };
                        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
                        handler.postDelayed(runnable, interval);

                    }
                    else if(res.getMessage().equals("Code Not Matched Please Try Again")){
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Code komt niet overeen Probeer het opnieuw");
                    }
                    else if(res.getMessage().equals("User not Exist")){
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Gebruiker bestaat niet");
                    }
                    else if(res.getMessage().equals("Request not Found to change Email")){
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Verzoek niet gevonden om e-mail te wijzigen");
                    }



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
    private void submitAuthLogout() {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callAuthLogOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAuthLogOut() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.userLogoutApi(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),new Callback<GeneralResponseData>() {
            @Override
            public void onResponse(Call<GeneralResponseData> call,
                                   final retrofit2.Response<GeneralResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {

                    SharedPreference.getInstance(mContext).set(SharedPreference.Key.ID, "");
                    SharedPreference.getInstance(mContext).set(SharedPreference.Key.Email, "");
                    SharedPreference.getInstance(mContext).set(SharedPreference.Key.Name, "");
                    SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, false);
                    SharedPreference.getInstance(mContext).set(SharedPreference.Key.Password, "");
                    Intent intent = new Intent(OtpAC.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

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
