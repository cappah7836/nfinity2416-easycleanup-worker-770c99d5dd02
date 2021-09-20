package com.app.easycleanup.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.easycleanup.R;
import com.app.easycleanup.adapter.ProjectDataAdapter;
import com.app.easycleanup.interfaces.OnItemClick;
import com.app.easycleanup.models.ProjectListResponse;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ProjectActivity extends AppCompatActivity {
    RecyclerView rvProjects;
    AppCompatActivity mContext;
    ProgressBar progressBar;
    ProjectDataAdapter projectAdapter;
    EditText etSearch;
    ArrayList<ProjectListResponse.Record> arrayList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        (mContext).setTitle("Projecten / Planningen");

        setContentView(R.layout.fragment_project);

        Window window = mContext.getWindow();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        viewInitialized();
        submitGetProjects();
        searchProject();



    }
    private void viewInitialized() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar);
        rvProjects = findViewById(R.id.rvProjects);
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setFocusable(true);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.back_view);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });
    }


    public void searchProject(){
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                query = query.toString();
                if(query.toString().length()==0){
                    ApplicationUtils.hideKeyboard(mContext);

                }
                ArrayList<ProjectListResponse.Record> filteredList = new ArrayList<>();


                for (int i = 0; i < arrayList.size(); i++) {

                    final String text = arrayList.get(i).getProjectName();
                    if (containsIgnoreCase(text, query.toString())) {

                        filteredList.add(arrayList.get(i));
                    }
                }
                if (filteredList.size() > 0) {
                    projectAdapter = new ProjectDataAdapter(mContext, filteredList, new OnItemClick() {
                        @Override
                        public void onClick(int position) {
                            Intent intent = new Intent(mContext, EmployeePlaningActivity.class);
                            intent.putExtra("emp_id",arrayList.get(position).getEmployeeId());
                            intent.putExtra("project_id",arrayList.get(position).getProjectId());
                            intent.putExtra("planId",arrayList.get(position).getPlanId());
                            intent.putExtra("date",arrayList.get(position).getPlanDate());
                            startActivity(intent);
                        }


                    });
                    rvProjects.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rvProjects.setAdapter(projectAdapter);
                    rvProjects.setNestedScrollingEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    private void submitGetProjects() {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callProjects();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //
    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }
    //
    private void callProjects() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callGetProject(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call,
                                   final retrofit2.Response<ProjectListResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {

                    arrayList.clear();
                    arrayList.addAll(response.body().getData().getRecords());
                    projectAdapter = new ProjectDataAdapter(mContext, arrayList, new OnItemClick() {
                        @Override
                        public void onClick(int position) {

                            Intent intent = new Intent(mContext, EmployeePlaningActivity.class);

                            intent.putExtra("emp_id",arrayList.get(position).getEmployeeId());
                            intent.putExtra("project_id",arrayList.get(position).getProjectId());
                            intent.putExtra("pname",arrayList.get(position).getProjectName());
                            intent.putExtra("planId",arrayList.get(position).getPlanId());
                            intent.putExtra("date",arrayList.get(position).getPlanDate());
                            intent.putExtra("location",arrayList.get(position).getLocation());
                            startActivity(intent);

                        }


                    });
                    rvProjects.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rvProjects.setAdapter(projectAdapter);
                    rvProjects.setNestedScrollingEnabled(false);

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
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
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
