package com.app.easycleanup.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import com.app.easycleanup.R;

import com.app.easycleanup.models.GeneralResponseData;
import com.app.easycleanup.models.NameResponseData;

import com.app.easycleanup.network.ApiClient;
import com.app.easycleanup.network.ReminderBroadcast;
import com.app.easycleanup.utils.ApplicationUtils;
import com.app.easycleanup.utils.CustomResponseDialog;
import com.app.easycleanup.utils.SharedPreference;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;

import static androidx.core.content.ContextCompat.getSystemService;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatActivity mContext;
    ImageView back;
    String empId, emailAddress, mobileNo;
    TextView UserName, UserEmail, ChangePassword;
    LinearLayout linearName, linearEmail;


    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_profile);




        Window window = mContext.getWindow();
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));

        viewInitialized();
        empId = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ID);



    }

    private void viewInitialized() {
        back = findViewById(R.id.goback);
        UserName = findViewById(R.id.user_namea);
        UserEmail = findViewById(R.id.user_emaila);

        ChangePassword = findViewById(R.id.change_passworda);
        linearName = findViewById(R.id.lin_namea);
        progressBar = findViewById(R.id.progbar);
        linearEmail = findViewById(R.id.lin_emaila);


        back.setOnClickListener(this);
        linearEmail.setOnClickListener(this);
        ChangePassword.setOnClickListener(this);
        linearName.setOnClickListener(this);


        UserName.setText(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Name));
        emailAddress = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Email);
        mobileNo = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.ContactId);

        if (TextUtils.isEmpty(emailAddress)) {
            //  AddEmail.setVisibility(View.VISIBLE);
            //  EmailQuestion.setVisibility(View.VISIBLE);
            UserEmail.setVisibility(View.GONE);
        } else {
            UserEmail.setVisibility(View.VISIBLE);
            UserEmail.setText(emailAddress);
            //  AddEmail.setVisibility(View.GONE);
            //   EmailQuestion.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goback:
                onBackPressed();
                break;

            case R.id.lin_emaila:
                changeEmail();

                break;

            case R.id.change_passworda:
                changePassword();
                break;

            case R.id.lin_namea:
                changeName();
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


    private void changeEmail() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.email_dialog);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.password_shape));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        TextView update = dialog.findViewById(R.id.update);
        TextView cancel = dialog.findViewById(R.id.cancel_action);
        final EditText newemail = dialog.findViewById(R.id.new_email);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 emailAddress = newemail.getText().toString();
                if (ApplicationUtils.isemailValid(mContext, emailAddress, newemail)) {
                    if (ApplicationUtils.isEditText(mContext, emailAddress, newemail)) {
                        if (!ApplicationUtils.isOnline(mContext)) {
                            CustomResponseDialog.showDropDownNotification(mContext,
                                    mContext.getString(R.string.alert_information),
                                    mContext.getString(R.string.alert_internet_connection));

                            return;
                        }
                        Toast.makeText(mContext, "Sending Please Wait!", Toast.LENGTH_SHORT).show();
                        sendOtp(emailAddress, dialog);
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });
        dialog.show();
    }

    private void sendOtp(final String email, final Dialog dialog) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.submitEmailVerification(SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token), email, new Callback<GeneralResponseData>() {
            @Override
            public void onResponse(Call<GeneralResponseData> call,
                                   final retrofit2.Response<GeneralResponseData> response) {

                if (response.isSuccessful()) {
                    GeneralResponseData res = response.body();
                    if (res.getMessage().equals("Verification Send to your Email")) {
                        CustomResponseDialog.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.success),
                                "Verificatie code verzonden");
                        dialog.dismiss();
                        final int interval = 2000; // 1 Second
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable(){
                            public void run() {
                                Intent intent = new Intent(mContext, OtpAC.class);
                                intent.putExtra("email", email);
                                Log.d("Email", email);
                                startActivity(intent);

                            }
                        };
                        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
                        handler.postDelayed(runnable, interval);
                    }
                    else if(res.getMessage().equals("Your Request can't proceed Please try again later")){
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Uw verzoek kan niet doorgaan. Probeer het later opnieuw");
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

    private void changeName () {
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.name_dialog);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.password_shape));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

            TextView update = dialog.findViewById(R.id.update);
            TextView cancel = dialog.findViewById(R.id.cancel_action);
            final EditText newName = dialog.findViewById(R.id.new_name);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String new_name = newName.getText().toString();
                    if (ApplicationUtils.isEditText(mContext, new_name, newName)) {
                        if (!ApplicationUtils.isOnline(mContext)) {
                            CustomResponseDialog.showDropDownNotification(mContext,
                                    mContext.getString(R.string.alert_information),
                                    mContext.getString(R.string.alert_internet_connection));

                            return;
                        }


                        try {
                            changeuserName(new_name, empId, dialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });


            dialog.show();
        }

    private void changeuserName(final String name, String emp_id, final Dialog dialog) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callChangeName( SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),emp_id,name, new Callback<NameResponseData>() {
            @Override
            public void onResponse(Call<NameResponseData> call,
                                   final retrofit2.Response<NameResponseData> response) {

                if (response.isSuccessful()) {
                    Log.d("Response", response.body().getStatus());
                    NameResponseData res = response.body();

                    if (res.getMessage().equals("Your Name has been Changed")) {
                        updateInfoInSharePref(response.body().getData().getName());
                        UserName.setText(response.body().getData().getName());
                        CustomResponseDialog.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.success),
                                "Naam succesvol gewijzigd");
                        dialog.dismiss();

                    }
                    else if (res.getMessage().equals("User not exist")) {
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Gebruiker bestaat niet");
                        dialog.dismiss();
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
            public void onFailure(Call<NameResponseData> call, Throwable t) {

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

    private void updateInfoInSharePref(String name) {
        SharedPreference.getInstance(mContext).set(SharedPreference.Key.Name, name);
    }

    private void changePassword () {

            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.change_password_dialog);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.password_shape));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        TextView cancel = dialog.findViewById(R.id.cancel_action);
        final EditText currentPassword = dialog.findViewById(R.id.current_password);
        final EditText newPassword = dialog.findViewById(R.id.new_password);
        final EditText confirmPassword = dialog.findViewById(R.id.confirm_password);

        final String password = SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Password);

            final boolean[] isVisible = {false};
            TextView update = dialog.findViewById(R.id.update);
            final ImageView hidePass= dialog.findViewById(R.id.hidepassc);
            final ImageView hidePassnp= dialog.findViewById(R.id.hidepassnp);
            final ImageView hidePasscp= dialog.findViewById(R.id.hidepasscp);

             hidePass.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if (isVisible[0]) {
                         isVisible[0] = false;
                         hidePass.setImageResource(R.drawable.ic_hide);
                         hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                         currentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                     } else {
                         isVisible[0] = true;
                         hidePass.setImageResource(R.drawable.ic_show);
                         hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorCharcoalGrey), PorterDuff.Mode.SRC_IN);
                         currentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                     }
                 }
              });
             hidePassnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible[0]) {
                    isVisible[0] = false;
                    hidePass.setImageResource(R.drawable.ic_hide);
                    hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else {
                    isVisible[0] = true;
                    hidePass.setImageResource(R.drawable.ic_show);
                    hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorCharcoalGrey), PorterDuff.Mode.SRC_IN);
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
               }
              });
            hidePasscp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible[0]) {
                    isVisible[0] = false;
                    hidePass.setImageResource(R.drawable.ic_hide);
                    hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else {
                    isVisible[0] = true;
                    hidePass.setImageResource(R.drawable.ic_show);
                    hidePass.setColorFilter(ContextCompat.getColor(mContext, R.color.colorCharcoalGrey), PorterDuff.Mode.SRC_IN);
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
              }
              });


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String current_password = currentPassword.getText().toString();
                    Log.d("Password", current_password);

                    String new_password = newPassword.getText().toString();
                    String confirm_password = confirmPassword.getText().toString();

                    if (ApplicationUtils.isStringMatched(mContext, current_password, password, currentPassword)) {
                        if (ApplicationUtils.isEditText(mContext, new_password, newPassword)
                                && ApplicationUtils.isEditText(mContext, confirm_password, confirmPassword)) {
                            if (ApplicationUtils.isStringMatched(mContext, new_password, confirm_password, newPassword)) {
                                if (!ApplicationUtils.isOnline(mContext)) {
                                    CustomResponseDialog.showDropDownNotification(mContext,
                                            mContext.getString(R.string.alert_information),
                                            mContext.getString(R.string.alert_internet_connection));

                                    return;
                                }
                                try {
                                    changeuserPass(empId,confirm_password,current_password,dialog);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            });
            dialog.show();

        }

    private void changeuserPass(String emp_id, String current_password, String password, final Dialog dialog) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.callChangePassword( SharedPreference.getInstance(mContext).getString(SharedPreference.Key.Token),current_password,emp_id,password, new Callback<GeneralResponseData>() {
            @Override
            public void onResponse(Call<GeneralResponseData> call,
                                   final retrofit2.Response<GeneralResponseData> response) {

                if (response.isSuccessful()) {
                    Log.d("Response", response.body().getMessage());

                    GeneralResponseData res = response.body();
                    if (res.getMessage().equals("Your Password has been Changed")) {
                        CustomResponseDialog.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.success),
                                "Wachtwoord succesvol veranderd");
                        dialog.dismiss();
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
                    else if(res.getMessage().equals("Password Do not Matched to Old Password")){
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Wachtwoord komt niet overeen met oud wachtwoord");
                        dialog.dismiss();
                    }
                    else if(res.getMessage().equals("User not Exist")){
                        CustomResponseDialog.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert),
                                "Gebruiker bestaat niet");
                        dialog.dismiss();
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
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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



