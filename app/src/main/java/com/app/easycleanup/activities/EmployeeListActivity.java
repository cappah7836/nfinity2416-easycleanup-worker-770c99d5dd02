package com.app.easycleanup.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easycleanup.R;
import com.app.easycleanup.models.EmployeeListResponse;
import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;
import com.app.easycleanup.adapter.EmployeeDataAdapter;
import com.app.easycleanup.interfaces.OnItemClick;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

;

public class EmployeeListActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatActivity mContext;
    ProgressBar progressBar;
    ImageView ivBackground;
    RecyclerView rvEmployee;
    EmployeeDataAdapter empoyeeDataAdapter;
    EditText etSearch;
    TextView tvLable;
    Button btBack;
    ArrayList<EmployeeListResponse.Employee> arrayList = new ArrayList<>();
    final Calendar myCalendarIn = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    String planId,dateString,emp_id,project_id;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_list_activity);
        mContext = this;

        intializedViews();

        Window window = mContext.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        planId = getIntent().getStringExtra("planId");
        submitGetEmployee(planId);

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarIn.set(Calendar.YEAR, year);
                myCalendarIn.set(Calendar.MONTH, monthOfYear);
                myCalendarIn.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat= "yyyy-mm-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dateString=(sdf.format(myCalendarIn.getTime()));



            }

        };

    }


    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

            }
        });
    }

    private void intializedViews() {
        progressBar = findViewById(R.id.progressBar);
        rvEmployee = findViewById(R.id.rvEmployee);
        tvLable = findViewById(R.id.tvLable);
        btBack = findViewById(R.id.bt_back);
        etSearch = findViewById(R.id.etSearch);
        tvLable.setText("Werknemerslijst");
        searchProject();
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setFocusable(true);
            }
        });
        btBack.setOnClickListener(this);

    }

    public void searchProject() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                query = query.toString();
                if (query.toString().length() == 0) {
                    ApplicationUtils.hideKeyboard(mContext);

                }
                final ArrayList<EmployeeListResponse.Employee> filteredList = new ArrayList<>();


                for (int i = 0; i < arrayList.size(); i++) {

                    final String text = arrayList.get(i).getFirstname();
                    if (containsIgnoreCase(text, query.toString())) {

                        filteredList.add(arrayList.get(i));
                    }
                }
                if (filteredList.size() > 0) {
                    empoyeeDataAdapter = new EmployeeDataAdapter(mContext, filteredList, new OnItemClick() {
                        @Override
                        public void onClick(int position) {

                            emp_id=filteredList.get(position).getEmployeeId();
                            project_id=filteredList.get(position).getProjectId();

                            Intent intent = new Intent(mContext, EmployeeAttandence.class);
                            intent.putExtra("emp_id",emp_id);
                            intent.putExtra("project_id",project_id);
                            intent.putExtra("date",filteredList.get(position).getDate());
                            startActivity(intent);

                        }


                    });
                    rvEmployee.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rvEmployee.setAdapter(empoyeeDataAdapter);
                    rvEmployee.setNestedScrollingEnabled(false);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_back:

                onBackPressed();
                break;

        }


    }


    private void submitGetEmployee(String planId) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callGetEmployee(planId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetEmployee(String planId) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callGetEmployee(planId, SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token), new Callback<EmployeeListResponse>() {
            @Override
            public void onResponse(Call<EmployeeListResponse> call,
                                   final retrofit2.Response<EmployeeListResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(1)) {
                        arrayList.clear();
                        arrayList.addAll(response.body().getData().getEmployees());
                        empoyeeDataAdapter = new EmployeeDataAdapter(mContext, arrayList, new OnItemClick() {
                            @Override
                            public void onClick(int position) {

                                emp_id=arrayList.get(position).getEmployeeId();
                                project_id=arrayList.get(position).getProjectId();
                                Intent intent = new Intent(mContext, EmployeeAttandence.class);
                                intent.putExtra("emp_id",emp_id);
                                intent.putExtra("project_id",project_id);
                                intent.putExtra("date",arrayList.get(position).getDate());
                                startActivity(intent);

                            }



                        });
                        rvEmployee.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                        rvEmployee.setAdapter(empoyeeDataAdapter);
                        rvEmployee.setNestedScrollingEnabled(false);
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
            public void onFailure(Call<EmployeeListResponse> call, Throwable t) {
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
