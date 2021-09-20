package com.app.easycleanup.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.app.easycleanup.R;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

public final class CustomResponseDialog {

    public static void createSimpleOkDialog(Context context, String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setBackgroundResource(R.color.colorPrimaryDark);
        tv.setPadding(10, 10, 10, 10);
        tv.setGravity(Gravity.CENTER); // this is required to bring it to center.
        tv.setTextSize(20);
        tv.setTextColor(Color.WHITE);
        alertDialog.setCustomTitle(tv);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAlertDialogStyle;
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnShowListener(
                new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface arg0) {
                                          alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                      }
                                  }
        );
        alertDialog.show();
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert).setMessage(message);
        return alertDialog.create();
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }



    public static void showDropDownNotification(Activity mContext, String title, String message) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(R.color.error_bg_color)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_alert)
                .setIconColorFilter(0)
                .setDuration(2000)
                .show();
    }

    public static void showDropDownSuccessNotification(Activity mContext, String title, String message) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(R.color.success_bg_color)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_success)
                .setIconColorFilter(0)
                .setDuration(2000)
                .show();
    }
    public static void showDropDownSuccessNotificationBack(Activity mContext, String title, String message) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(R.color.success_bg_color)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_success)
                .setIconColorFilter(0)
                .setDuration(2000)
                .show();
        mContext.onBackPressed();
    }

    public static void showDropDownSuccessNotificationAndMoveToNextActivity(final Activity mContext, String title, final String message,final Class<?> cls) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(R.color.success_bg_color)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_success)
                .setIconColorFilter(0)
                .setDuration(2000)

                .setOnShowListener(new OnShowAlertListener() {
                    @Override
                    public void onShow() {
                    }
                })
                .setOnHideListener(new OnHideAlertListener() {
                    @Override
                    public void onHide() {
                        Intent myIntent = new Intent(mContext, cls);
                        mContext.startActivity(myIntent);
                        mContext.finish();
                    }
                })
                .show();
    }



}
