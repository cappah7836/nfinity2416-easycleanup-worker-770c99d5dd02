package com.app.easycleanup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easycleanup.R;

import com.app.easycleanup.models.SupervisorProjectPlanResponse;
import com.app.easycleanup.models.SupervisorProjectResponse;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.app.easycleanup.activities.EmployeeListActivity;
import com.app.easycleanup.adapter.SpinnerProjectListAdapter;
import com.app.easycleanup.adapter.SupervisorProjectDataAdapter;
import com.app.easycleanup.interfaces.OnItemClick;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class SupervisorProjectFragment extends Fragment {
    AppCompatActivity mContext;
    ProgressBar progressBar;
    SpinnerProjectListAdapter projectDataAdapter;
    SupervisorProjectDataAdapter supervisorProjectDataAdapter;
    ArrayList<SupervisorProjectResponse.Project> spinnerProjectList = new ArrayList<>();
    ArrayList<SupervisorProjectPlanResponse.Planning> supervisorProjectList = new ArrayList<>();
    String contact_id;
    Spinner spinnerProjects;
    RecyclerView rvProjects;
    public SupervisorProjectFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        (getActivity()).setTitle("Projecten / Planningen");
        mContext = (AppCompatActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_supervisor_project, container, false);
        viewInitialized(view);
        contact_id=SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ContactId);
        submitGetProject(contact_id);
        spinnerProjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                submitGetProjects(spinnerProjectList.get(position).getId());


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        return view;
    }

    private void viewInitialized(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        spinnerProjects = view.findViewById(R.id.spProjects);
        rvProjects = view.findViewById(R.id.rvProjects);



    }
    private void submitGetProject(String id) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callGetProject(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetProject(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callGetSupervisorProject(id,SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),new Callback<SupervisorProjectResponse>() {
            @Override
            public void onResponse(Call<SupervisorProjectResponse> call,
                                   final retrofit2.Response<SupervisorProjectResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if(response.body().getStatus().equals(1)) {

                        spinnerProjectList.clear();
                        spinnerProjectList.addAll(response.body().getData().getProjects());
                        projectDataAdapter = new SpinnerProjectListAdapter(mContext, spinnerProjectList);
                        spinnerProjects.setAdapter(projectDataAdapter);
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
            public void onFailure(Call<SupervisorProjectResponse> call, Throwable t) {
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

//
    private void showProgressBar(final boolean progressVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }
//


    private void submitGetProjects(String id) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(getActivity(),
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callProjects(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callProjects(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callGetSupervisorPlan(id,SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),new Callback<SupervisorProjectPlanResponse>() {
            @Override
            public void onResponse(Call<SupervisorProjectPlanResponse> call,
                                   final retrofit2.Response<SupervisorProjectPlanResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if(response.body().getStatus().equals(1)) {

                        supervisorProjectList.clear();
                        supervisorProjectList.addAll(response.body().getData().getPlanning());
                        supervisorProjectDataAdapter = new SupervisorProjectDataAdapter(mContext, supervisorProjectList, new OnItemClick() {
                            @Override
                            public void onClick(int position) {

                                Intent intent = new Intent(mContext, EmployeeListActivity.class);
                                intent.putExtra("planId",supervisorProjectList.get(position).getId());
                                startActivity(intent);
                            }


                        });
                        rvProjects.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                        rvProjects.setAdapter(supervisorProjectDataAdapter);
                        rvProjects.setNestedScrollingEnabled(false);
                    }

                } else {
                    if (response.code() == 401)
                        CustomResponseDialog.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.data_not_found));
                    if (response.code() == 404)
                        CustomResponseDialog.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        CustomResponseDialog.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<SupervisorProjectPlanResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    CustomResponseDialog.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_api_not_found));
                else if (t instanceof SocketTimeoutException)
                    CustomResponseDialog.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_request_timeout));
                else
                    CustomResponseDialog.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }
}
