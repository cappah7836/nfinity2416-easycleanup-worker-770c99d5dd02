package com.app.easycleanup.fragments;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.app.easycleanup.R;
import com.app.easycleanup.activities.AppliedLeavesAc;
import com.app.easycleanup.activities.EmployeePlaningActivity;
import com.app.easycleanup.activities.ProjectActivity;
import com.app.easycleanup.activities.SickLeaveAC;
import com.app.easycleanup.activities.VacationLeaveAC;
import com.app.easycleanup.network.ReminderBroadcast;

import static androidx.core.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */

public class DashboardFragment extends Fragment {

    CardView cvProjects, cvsickLeaves, cvVacationLeaves, cvleaves;
    Context mContext;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mContext = getActivity();
        (getActivity()).setTitle("Dashboard");


        cvProjects = view.findViewById(R.id.cv_projects);
        cvsickLeaves = view.findViewById(R.id.cv_sick_leave);
        cvVacationLeaves = view.findViewById(R.id.cv_vacation_leave);
        cvleaves = view.findViewById(R.id.cv_appliedleaves);


        cvProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), ProjectActivity.class);
                startActivity(intent1);
            }
        });

        cvsickLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), SickLeaveAC.class);
                startActivity(intent1);
            }
        });

        cvVacationLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), VacationLeaveAC.class);
                startActivity(intent1);
            }
        });

        cvleaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), AppliedLeavesAc.class);
                startActivity(intent1);
            }
        });


        return view;

    }

}