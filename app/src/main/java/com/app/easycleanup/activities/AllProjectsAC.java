package com.app.easycleanup.activities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easycleanup.R;
import com.app.easycleanup.adapter.AllProjectsAdapter;
import com.app.easycleanup.interfaces.OnOpenJobDetails;
import com.app.easycleanup.models.AllProjectListResponse;
import com.app.easycleanup.network.ApiInterface;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class AllProjectsAC extends AppCompatActivity {
    AppCompatActivity mContext;
    String empId;
    ProgressBar progressBar;
    String token;
    SearchView searchView;
    ArrayList<AllProjectListResponse> allProjectListResponses=new ArrayList<>();
    AllProjectsAdapter adapter;
    RecyclerView rvProjects;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_projects);
        mContext = this;
        Window window = mContext.getWindow();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        setTitle("Alle projecten");

        empId = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ContactId);
        token = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token);
        viewInitialized();
        submitGetProjects();

    }

    private void viewInitialized() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.back_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progressBarbt);
        searchView= findViewById(R.id.search_barbt);
        rvProjects = findViewById(R.id.rvallprojects);
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
        List<AllProjectListResponse> l = new ArrayList<>();
        for (AllProjectListResponse all : allProjectListResponses) {
            if (all.getWeekcardId().toLowerCase().contains(query))
                l.add(all);
        }
        adapter.filterProjectList(l);
    }

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }


    private void submitGetProjects() {
        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);
            token = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token);
            callAllProjects(empId, token);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void callAllProjects(String emp_id, String token){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://app.easycleanup.nl/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface requestInteface=retrofit.create(ApiInterface.class);
        Call<List<AllProjectListResponse>> call=requestInteface.callAllProjectsList(emp_id, token);
        call.enqueue(new Callback<List<AllProjectListResponse>>() {
            @Override
            public void onResponse(Call<List<AllProjectListResponse>> call, Response<List<AllProjectListResponse>> response) {

                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(response.isSuccessful()){
                    allProjectListResponses.clear();
                    allProjectListResponses=new ArrayList<>(response.body());
                    adapter=new AllProjectsAdapter(mContext, allProjectListResponses, new OnOpenJobDetails() {
                        @Override
                        public void onOpen(int position) {
                            openDetails(position);
                        }
                    });
                    rvProjects.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rvProjects.setAdapter(adapter);
                    rvProjects.setNestedScrollingEnabled(false);
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
            public void onFailure(Call<List<AllProjectListResponse>> call, Throwable t) {
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDetails(int position) {
        TextView tvCustomer, tvProjectname, tvAddress, tvProjectid, tvtotal;
        ImageButton btDown;
        LinearLayout llWeeks;
        View dialogView = getLayoutInflater().inflate(R.layout.project_details, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        btDown = dialog.findViewById(R.id.btDown);
        tvProjectname = dialog.findViewById(R.id.tvProject);
        tvProjectid = dialog.findViewById(R.id.tvProjectid);
        tvCustomer = dialog.findViewById(R.id.tvcustomer);
        tvtotal = dialog.findViewById(R.id.week_no);
        tvAddress = dialog.findViewById(R.id.tvLocation);

        llWeeks = dialog.findViewById(R.id.llWeeks);


        TextView tvMon = dialog.findViewById(R.id.tvMon);
        TextView tvTue = dialog.findViewById(R.id.tvTue);
        TextView tvWed = dialog.findViewById(R.id.tvWed);
        TextView tvThu = dialog.findViewById(R.id.tvThu);
        TextView tvFri = dialog.findViewById(R.id.tvFri);
        TextView tvSat = dialog.findViewById(R.id.tvSat);
        TextView tvSun = dialog.findViewById(R.id.tvSun);

            if (allProjectListResponses.get(position).getProject_name() != null) {
                tvProjectname.setText(allProjectListResponses.get(position).getProject_name());
            } else {
                tvProjectname.setText("");

            }
            if (allProjectListResponses.get(position).getEmp_name() != null) {
                tvCustomer.setText(allProjectListResponses.get(position).getEmp_name());
            } else {
                tvCustomer.setText("");

            }
            tvAddress.setText(allProjectListResponses.get(position).getAddress());
        tvProjectid.setText(allProjectListResponses.get(position).getProject_id());
       tvtotal.setText(allProjectListResponses.get(position).getTotal());


        llWeeks.setVisibility(View.VISIBLE);
        tvProjectname.setVisibility(View.VISIBLE);



        //            Show Days
        daysHighlightHandle(tvMon, tvTue, tvWed, tvThu, tvFri, tvSat, tvSun, position);
        btDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();


        }
    private void daysHighlightHandle(TextView tvMon, TextView tvTue, TextView tvWed, TextView tvThu, TextView tvFri, TextView tvSat, TextView tvSun, int position) {
        if (allProjectListResponses.get(position).getMon().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvMon.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvMon.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

        if (allProjectListResponses.get(position).getTue().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvTue.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvTue.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

        if (allProjectListResponses.get(position).getWed().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvWed.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvWed.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

        if (allProjectListResponses.get(position).getThu().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvThu.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvThu.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

        if (allProjectListResponses.get(position).getFri().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvFri.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvFri.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

        if (allProjectListResponses.get(position).getSat().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvSat.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvSat.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

        if (allProjectListResponses.get(position).getSun().equalsIgnoreCase("0")) {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_uncheck);
            tvSun.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        } else {
            Drawable unCheck = getResources().getDrawable(R.drawable.ic_check);
            tvSun.setCompoundDrawablesWithIntrinsicBounds(null, unCheck, null, null);
        }

    }


}


