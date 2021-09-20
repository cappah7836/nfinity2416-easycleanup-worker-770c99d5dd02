package com.app.easycleanup.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.easycleanup.R;
import com.app.easycleanup.models.UserAuthResponse;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatActivity mContext;
    TextView tvVersion;
    ProgressBar progressBar;
    Button btnSignIn;
    EditText etUserEmail, etPassword;
    ImageView hidePass;
    private boolean isVisible = false;

    private String android_id;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_login);
        android_id= Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Window window = mContext.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        viewInitialized();
        setLoggedInUser();


    }

    private void setLoggedInUser() {


        String id = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ID);
        if (ApplicationUtils.isSet(id)) {
            etUserEmail.setText(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Email));
            etPassword.setText(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Password));
            submitAuthLogin();
        }


    }

    private void viewInitialized() {

        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();
        btnSignIn = findViewById(R.id.btn_login);
        etUserEmail = findViewById(R.id.et_user);
        etPassword = findViewById(R.id.et_password);
        tvVersion = findViewById(R.id.tvVersion);
        hidePass = findViewById(R.id.hidepass);

        hidePass.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText("ECU-V: "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                ApplicationUtils.hideKeyboard(mContext);
                submitAuthLogin();
                break;
            case R.id.hidepass:
                passwordVisibility();


        }


    }

    private void passwordVisibility() {

        if (isVisible) {
            isVisible = false;
            hidePass.setImageResource(R.drawable.ic_hide);
            hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            isVisible = true;
            hidePass.setImageResource(R.drawable.ic_show);
            hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorCharcoalGrey), PorterDuff.Mode.SRC_IN);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
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

    private boolean validateEmail() {
        if (etUserEmail.getText().toString().trim().isEmpty()) {
            etUserEmail.setError(mContext.getString(R.string.error_email));
            requestFocus(etUserEmail);
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (etPassword.getText().toString().length() < 4) {
            etPassword.setError(mContext.getString(R.string.error_password));
            requestFocus(etPassword);
            return false;
        }
        return true;
    }


    private void submitAuthLogin() {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, false);
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.Password, etPassword.getText().toString());

        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
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
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("email", etUserEmail.getText().toString().trim());
            paramObject.addProperty("password", etPassword.getText().toString().trim());

            callAuthLogin(paramObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAuthLogin(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.userAuthApi(params, new Callback<UserAuthResponse>() {
            @Override
            public void onResponse(Call<UserAuthResponse> call,
                                   final retrofit2.Response<UserAuthResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if(response.body().getStatus()==1) {

                        SharedPreference.getInstance(mContext).set(SharedPreference.Key.ID, response.body().getResponse().getUser().getId().toString());
                        SharedPreference.getInstance(mContext).set(SharedPreference.Key.Email, response.body().getResponse().getUser().getEmail());
                        SharedPreference.getInstance(mContext).set(SharedPreference.Key.Role, response.body().getResponse().getUser().getGroup());
                        SharedPreference.getInstance(mContext).set(SharedPreference.Key.Name, response.body().getResponse().getUser().getName());
                        SharedPreference.getInstance(mContext).set(SharedPreference.Key.Token, response.body().getResponse().getToken());
                        SharedPreference.getInstance(mContext).set(SharedPreference.Key.ContactId, response.body().getResponse().getUser().getContactId());

                        if( response.body().getResponse().getUser().getGroup().equals("2")) {
                            Intent intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            CustomResponseDialog.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.invalid_user),
                                    response.body().getMessage());
                        }
                    }else {

                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                response.body().getMessage());
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
            public void onFailure(Call<UserAuthResponse> call, Throwable t) {
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
