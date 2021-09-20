package com.app.easycleanup.activities;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.easycleanup.R;
import com.app.easycleanup.adapter.LeavesDataAdapter;
import com.app.easycleanup.models.SubmitedLeavesResponse;

import com.app.easycleanup.network.ApiInterface;

import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppliedLeavesAc extends AppCompatActivity {
    RecyclerView rvLeaves;
    AppCompatActivity mContext;
    ProgressBar progressBar;
    String empId;
    String token;
    SearchView searchView;
    ArrayList<SubmitedLeavesResponse> submitedLeavesResponses=new ArrayList<>();
    LeavesDataAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaves_list);
        mContext = this;


        Window window = mContext.getWindow();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));

        empId = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ID);
        intializedViews();

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Toegepaste bladeren");
        submitGetLeaves();


    }


    private void intializedViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.back_leaves);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        searchView= findViewById(R.id.search_bar);
        searchView.setQueryHint("Zoekweergave");
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setFocusable(true);
            }
        });
        rvLeaves = findViewById(R.id.rvLeaves);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText.toLowerCase());
                return false;
            }
        });

    }
    private void filterData(String query) {
        List<SubmitedLeavesResponse> l = new ArrayList<>();
        for (SubmitedLeavesResponse submitedLeaves : submitedLeavesResponses) {
            if (submitedLeaves.getStartDate().toLowerCase().contains(query))
                l.add(submitedLeaves);
        }
        adapter.filterList(l);
    }

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }


    private void submitGetLeaves() {
        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);
            token = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token);
            callAllLeaves(empId, token);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void callAllLeaves(String emp_id, String token){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://app.easycleanup.nl/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface requestInteface=retrofit.create(ApiInterface.class);
        Call<List<SubmitedLeavesResponse>> call=requestInteface.callLeavesList(emp_id, token);
        call.enqueue(new Callback<List<SubmitedLeavesResponse>>() {
            @Override
            public void onResponse(Call<List<SubmitedLeavesResponse>> call, Response<List<SubmitedLeavesResponse>> response) {

                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(response.isSuccessful()){
                    submitedLeavesResponses.clear();
                    submitedLeavesResponses=new ArrayList<>(response.body());
                    adapter=new LeavesDataAdapter(AppliedLeavesAc.this,submitedLeavesResponses);
                    rvLeaves.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rvLeaves.setAdapter(adapter);
                    rvLeaves.setNestedScrollingEnabled(false);
                }
                else {
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
            public void onFailure(Call<List<SubmitedLeavesResponse>> call, Throwable t) {
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
