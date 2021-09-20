package com.app.easycleanup.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.easycleanup.R;
import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.models.PlanningResponseData;
import com.app.easycleanup.network.ApiClient;

import com.app.easycleanup.network.ReminderBroadcast;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.LocationTracker;
import com.app.easycleanup.utils.SharedPreference;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

;

public class EmployeePlaningActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    ////////////////////////////////////////////
    private int seconds;
    private boolean running;
    private boolean wasrunning;

    String time;

    private static final String CHANNEL_ID = "CHECKOUT ALERTER";
    private static final String CHANNEL_NAME = "Alert!";
    private static final String CHANNEL_DESC = "Vergeet niet af te rekenen als u weggaat, anders wordt u als afwezig beschouwd.";
    ////////////////////////////////////////////
    private GoogleMap mMap;
    double lat, lng;
    LatLng location;
    double radiusLocation;
    String Location;
    Geocoder geoCoder;
    LocationTracker locationTracker;
    ////////////////////////////////////////////
    AppCompatActivity mContext;
    Button btStart, btEnd, btBack;
    ProgressBar progressBar;
    TextView tvProjectName, tvCustomerName, tvAddress, tvDepartment, tvPhoneNumber, tvTotal, tvTotaltime;
    String empId, projectId, date, planId, timeIn, timeOut, pname;
    final Calendar myCalendarIn = Calendar.getInstance();
    final Calendar myCalendarOut = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateInLis;
    DatePickerDialog.OnDateSetListener dateOutLis;
    String currentDate;
    Boolean didCheckIn, didCheckOut;
    SimpleDateFormat simpleDateFormat;

    Date currentTime, currentTime2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);


        mContext = this;


        createNotificationChannel();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        intializedViews();
        locationTracker = new LocationTracker(mContext);
        Location = getIntent().getStringExtra("location");
        //   Toast.makeText(mContext, "Location is" + Location, Toast.LENGTH_SHORT).show();
        getAddress(Location, getApplicationContext());

        Log.d("LatLng", "onCreate: " + lat + " : " + lng);
        //   lat =31.52189361092499;
        //   lng =74.4375276659461;
        location = new LatLng(lat, lng);


        Window window = mContext.getWindow();


// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));

        setTitle("PLANNING DETAILS");


        empId = getIntent().getStringExtra("emp_id");
        projectId = getIntent().getStringExtra("project_id");
        date = getIntent().getStringExtra("date");
        planId = getIntent().getStringExtra("planId");
        pname = getIntent().getStringExtra("pname");


        submitGetPlanning(projectId, empId, date);
        geoCoder = new Geocoder(mContext, Locale.getDefault());

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(c);

    }

    public void getAddress(final String locationAddress, final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;

        try {
            List addresslist = geocoder.getFromLocationName(locationAddress, 1);
            if (addresslist != null && addresslist.size() > 0) {
                Address address = (Address) addresslist.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        //   double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        //    int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult/* + "   KM  " + kmInDec*/
                + " Meter   " + meterInDec);
        radiusLocation = Radius * c;
        return radiusLocation;
    }

    private void DrawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(30);

        // Border color of the circle
        circleOptions.strokeColor(getResources().getColor(R.color.colorAquaBlue));

        // Fill color of the circle
        circleOptions.fillColor(0x3003A9F4);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("seconds", seconds);
        editor.putBoolean("running", running);
        editor.putBoolean("wasrunning", wasrunning);
        editor.apply();
        running = false;


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        seconds = prefs.getInt("seconds", 0);
        running = prefs.getBoolean("running", false);
        wasrunning = prefs.getBoolean("wasrunning", running);
        runTimer();
        if (running) {
            seconds = prefs.getInt("seconds", 0);
            if (seconds < 0) {
                seconds = 0;
                running = false;
            } else {
                running = true;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasrunning) {
            running = true;
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

    private void intializedViews() {

        progressBar = findViewById(R.id.progressBar);
        btEnd = findViewById(R.id.btEnd);
        btBack = findViewById(R.id.bt_back);

        tvAddress = findViewById(R.id.tvAddress);
        tvTotal = findViewById(R.id.tvTotal);
        tvProjectName = findViewById(R.id.tvProjectName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvCustomerName = findViewById(R.id.tvCustomer);
        tvDepartment = findViewById(R.id.tvDepartment);
        btStart = findViewById(R.id.btStart);
        tvTotaltime = findViewById(R.id.tvTotaltime);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btStart.setOnClickListener(this);
        btEnd.setOnClickListener(this);
        btBack.setOnClickListener(this);



        dateInLis = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarIn.set(Calendar.YEAR, year);
                myCalendarIn.set(Calendar.MONTH, monthOfYear);
                myCalendarIn.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-mm-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                date_in = (sdf.format(myCalendarIn.getTime()));

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_in = selectedHour + ":" + selectedMinute;
                        submitCallStartJob();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Selecteer Inchecktijd");
                mTimePicker.show();

            }

        };

        dateOutLis = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarOut.set(Calendar.YEAR, year);
                myCalendarOut.set(Calendar.MONTH, monthOfYear);
                myCalendarOut.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-mm-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                date_out = (sdf.format(myCalendarIn.getTime()));

                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker1 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_out = selectedHour + ":" + selectedMinute;
                        submitCallEndJob();
                    }
                }, hour1, minute1, true);//Yes 24 hour time
                mTimePicker1.setTitle("Selecteer Uitchecken Tijd");
                mTimePicker1.show();


            }

        };


        simpleDateFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());

         currentTime = Calendar.getInstance().getTime();

        timeIn = simpleDateFormat.format(currentTime);

        currentTime2 = Calendar.getInstance().getTime();
        timeOut = simpleDateFormat.format(currentTime2);
        // Toast.makeText(getApplicationContext(),"" + timeOut, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       // googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(location).title(Location));
        LatLng coordinate = new LatLng(lat, lng);
        CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
        mMap.animateCamera(location1);
        DrawCircle(new LatLng(lat, lng));
        CalculationByDistance(location, new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude()));

    }


    String project_id, emp_id, plan_id, time_in, date_in, time_out, date_out;


    private void submitCallStartJob() {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            String check_in_location = "[" + new LocationTracker(mContext).getLatitude() + "," + new LocationTracker(mContext).getLongitude() + "]";
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            if (!currentDate.equals(date)) {
                running = false;
                seconds= 0;
            }
            else{
                callStartJob(empId, projectId, planId, date, currentTime, check_in_location);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callStartJob(String emp_id, String project_id, String plan_id, String date_in, String time_in, String location) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callEmployeeCheckIn(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token), emp_id, project_id, plan_id, date_in, time_in, location, new Callback<GeneralResponseData>() {
            @Override
            public void onResponse(Call<GeneralResponseData> call,
                                   final retrofit2.Response<GeneralResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus2().equals("1")) {

                        CustomResponseDialog.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.success),
                                "Succesvol ingecheckt");
                            runTimer();
                            running = true;
                            submitGetPlanning(projectId, empId, date);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent(EmployeePlaningActivity.this, ReminderBroadcast.class);
                        PendingIntent pendingentIntent = PendingIntent.getBroadcast(EmployeePlaningActivity.this, 0, intent, 0);
               /*         Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, 12);
                        calendar.set(Calendar.MINUTE, 45);*/
                        long timeAtClick = System.currentTimeMillis();
                        long tensec = 1000 * 2;
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                timeAtClick + tensec,
                                pendingentIntent);

                      //  alarmManager.setTime();

                    } else {
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                response.body().getMessage2());
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
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int mins = (seconds % 3600) / 60;
                int secs = seconds % 60;

                time = String.format(Locale.getDefault(), "%d hours:%02d mins:%02d secs", hours, mins, secs);
                tvTotal.setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
    private void submitCallEndJob() {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            String check_out_location = "[" + new LocationTracker(mContext).getLatitude() + "," + new LocationTracker(mContext).getLongitude() + "]";
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            callEndJob(empId, projectId, planId, date, currentTime, check_out_location);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callEndJob(String emp_id, String project_id, String plan_id, String date_in, String time_in, String location) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callEmployeeCheckOut(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token), emp_id, project_id, plan_id, date_in, time_in, location, new Callback<GeneralResponseData>() {

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
                                mContext.getString(R.string.success),
                                mContext.getString(R.string.task_end));
                    running = false;
                    seconds = 0;
                    submitGetPlanning(projectId, empId, date);



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


    @SuppressLint({"SetTextI18n", "DefaultLocale", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btEnd:
                if (radiusLocation < 0.09) {
                    if (currentDate.equalsIgnoreCase(date)) {
                        if (didCheckOut) {
                            CustomResponseDialog.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.alert_information),
                                    getString(R.string.checkout_already));
                        } else {
                            if (didCheckIn) {
                                submitCallEndJob();


                            } else {
                                CustomResponseDialog.showDropDownNotification(
                                        mContext,
                                        mContext.getString(R.string.alert_information),
                                        getString(R.string.check_in_first));
                            }
                        }
                    } else {
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                getString(R.string.checkout_error_date));
                    }
                }
                else{
                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            "Buiten bereik",
                            "Verkeerde locatie");

                }
                break;

            case R.id.btStart:
                if (radiusLocation < 0.09) {
                    // TODO Auto-generated method stub
                    if (currentDate.equalsIgnoreCase(date)) {
                        if (didCheckOut) {
                            CustomResponseDialog.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.alert_information),
                                    getString(R.string.checkout_already));
                        } else {

                            submitCallStartJob();

                        }
                    } else {
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                getString(R.string.checkin_error_date));
                        running = false;
                        seconds= 0;
                    }

                }
                else{
                    CustomResponseDialog.showDropDownNotification(
                            mContext,
                            "Buiten bereik",
                            "Verkeerde locatie");

                }

                break;

            case R.id.bt_back:

                onBackPressed();
                break;

        }


    }



    private void submitGetPlanning(String projectId, String empId, String date) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.IsTokenApplied, true);


        if (!ApplicationUtils.isOnline(mContext)) {
            CustomResponseDialog.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));

            return;
        }
        try {

            showProgressBar(true);

            callGetPlanning(projectId, empId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetPlanning(String projectId, String empId, String date) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callGetProjectPlanning(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token), empId, date, projectId, new Callback<PlanningResponseData>() {
            @Override
            public void onResponse(Call<PlanningResponseData> call,
                                   final retrofit2.Response<PlanningResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    tvProjectName.setText(response.body().getData().getProjectDetails().getProjectName());
                    tvPhoneNumber.setText(response.body().getData().getProjectDetails().getContactPhone());
                    tvCustomerName.setText(response.body().getData().getProjectDetails().getCustomerName());
                    tvDepartment.setText(response.body().getData().getProjectDetails().getDeptName());
                    tvAddress.setText(response.body().getData().getProjectDetails().getAddress());
                    didCheckIn = response.body().getData().getDidCheckIn();
                    didCheckOut = response.body().getData().getDidCheckOut();
                    if(response.body().getData().getCheckInTime()!=null) {
                        timeIn = response.body().getData().getCheckInTime();
                    }
                    if(response.body().getData().getCheckOutTime()!=null) {
                        timeOut = response.body().getData().getCheckOutTime();

                    }



                    Date date1 = null;
                    try {
                        date1 = simpleDateFormat.parse(timeIn);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date date2 = null;
                    try {
                        date2 = simpleDateFormat.parse(timeOut);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(timeOut.equals("")){
                        tvTotaltime.setText("0 hour :0 min");

                    }else {
                        long difference = date2.getTime() - date1.getTime();


                        int days = (int) (difference / (1000 * 60 * 60 * 24));
                        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                  //      hours = (hours < 0 ? -hours : hours);
                        tvTotaltime.setText(hours+" hour :"+min+" min");

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
            public void onFailure(Call<PlanningResponseData> call, Throwable t) {
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
