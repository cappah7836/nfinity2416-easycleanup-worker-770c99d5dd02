package com.app.easycleanup.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.app.easycleanup.R;


import com.app.easycleanup.fragments.DashboardFragment;

import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.Callback;

public class EmployeeActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatActivity mContext;
    ProgressBar progressBar;
    FloatingNavigationView mFloatingNavigationView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_employee);

        Window window = mContext.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        Initializing();
        viewInitialized();


    }

    private void viewInitialized() {


        progressBar = findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFloatingNavigationView = (FloatingNavigationView) findViewById(R.id.floating_navigation_view);
        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(item.getTitle().toString().equalsIgnoreCase("UITLOGGEN")){
                    mFloatingNavigationView.close();
                    submitAuthLogout();

                }

                else if(item.getTitle().toString().equalsIgnoreCase("ALLE PROJECTEN")){
                    mFloatingNavigationView.close();
                    gotoProjects();
                }
                else if(item.getTitle().toString().equalsIgnoreCase("PROFIEL")){
                    mFloatingNavigationView.close();
                    gotoProfile();
                }

                else {
                    mFloatingNavigationView.close();
                }
                return true;


            }
        });

    }

    private void gotoProfile() {
        Intent intent1 = new Intent(mContext, ProfileActivity.class);
        startActivity(intent1);
    }

    private void gotoProjects() {
        Intent intent1 = new Intent(mContext, AllProjectsAC.class);
        startActivity(intent1);
    }



    @Override
    public void onBackPressed() {
        if (mFloatingNavigationView.isOpened()) {
            mFloatingNavigationView.close();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    public void Initializing() {

        Fragment fragment = null;
        Class fragmentClass = DashboardFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }// Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fMain, fragment).commit();
    }



    @Override
    public void onClick(View v) {



    }
    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

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
                        Intent intent = new Intent(EmployeeActivity.this, LoginActivity.class);
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
