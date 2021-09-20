package com.app.easycleanup.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.easycleanup.R;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Author Muhammad Mubashir 10/30/2018

public class ApplicationUtils {

    private static ProgressDialog dialog;
    private static ProgressDialog mProgressDialog;
    private static double latitude;
    private static double longitude;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static Locale locale;
    private static String languageCode;
    private Context mContext;

    public ApplicationUtils(Context ctx) {
        this.mContext = ctx;
    }

    /********************
     * For Images Rotation
     *******************/

    @NonNull
    public static Boolean imageGettingFromAndroid(String image) {
        String[] separated = image.split("_");

        if (separated[2].equalsIgnoreCase("IMG")) {
            Log.d("image", separated[2]);
            return true;
        } else {
            return false;
        }

    }

    /********************
     * For Bitmap Streaming
     *******************/
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /********************
     * For Bitmap Rotation
     *******************/
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**********************
     * For email validation
     **********************/
    public static boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isemailValid(Context context, String email, EditText editText) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        } else {
            editText.requestFocus();
            editText.setError("Voer een geldig e-mailadres in.");
            return false;
        }
    }

    /****************************************
     * Get "String" to check is it null or not
     ****************************************/
    public static boolean isSet(String string) {
        if (string != null && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /****************************************
     * Get "String" to check is it null or not
     ****************************************/
    public String getErrorDefinition(int errorCode) {
        switch (errorCode) {
            case 400:
                return "Missing required parameters or invalid parameters/values (" + errorCode + ")";
            case 401:
                return "Incorrect email or password.(" + errorCode + ")";
            case 403:
                return "Account exists and user provided correct password, but account does not have a valid status.(" + errorCode + ")";
            case 500:
                return "Server Failure. (" + errorCode + ")";
            default:
                return "An error has occurred. (" + errorCode + ")";
        }
    }

    /**********************************
     * Get city and Country name method
     *********************************/
    public static String getcity(Context context, double latitude, double longitude) {
        StringBuilder results = new StringBuilder();
        try {
            Geocoder geocoderr = new Geocoder(context, Locale.getDefault());
            List<Address> addresses_city = geocoderr.getFromLocation(latitude, longitude, 1);
            if (addresses_city.size() > 0) {
                Address address_city = addresses_city.get(0);
                results.append(address_city.getLocality()).append(" ,");
                results.append(address_city.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return results.toString();
    }

    /***********************************
     * Get city and Country name method
     ***********************************/
    public static String getAddress(Context context, double latitude, double longitude) {
        StringBuilder results = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                results.append(address.getThoroughfare()).append(" ,");
                results.append(address.getLocality()).append(" ,");
                results.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return results.toString();
    }

    /***************
     * Alert Dialog
     **************/
    @SuppressWarnings("deprecation")
    public static void AlertEdit(Context context, String string) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setTitle(R.string.app_name);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAlertDialogStyle;
        alertDialog.setMessage(string);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /*****************************************************
     * Apply Blink Effect On Every TextView And Button etc
     *****************************************************/
    public static void buttonEffect(View button) {
        final int color = Color.parseColor("#00000000");
        try {
            if (button != null) {
                button.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                                v.invalidate();
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                v.getBackground().clearColorFilter();
                                v.invalidate();
                                break;
                            }
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******************************
     * Apply Effect On Every View(Button, TextView, LinearLayout etc)
     * Please Add In Gradle This Code First (compile 'com.balysv:material-ripple:1.0.2')
     *****************************/
    public static void setRippleEffect(View view) {
        MaterialRippleLayout.on(view)
                .rippleColor(Color.parseColor("#0183b5"))
                .rippleAlpha(0.1f)
                .rippleHover(true)
                .create();
    }

    /******************************
     * Apply Font On Whole Activity
     *****************************/
    public static void applyFont(final Context context, final View root, final String fontPath) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i), fontPath);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************
     * Set First Word Capitalize Of String
     ***************************************/
    public static String firstWordCapitalize(final String line) {
        if (line != null && line.length() > 0) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        } else {
            return "";
        }
    }

    /*******************************************
     * Decode File For "Out Of Memory" Exception
     *******************************************/
    public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /*************************
     * To set Image Rotation
     ************************/
    public static int getCameraPhotoOrientation(Context context, Uri imageUri,
                                                String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static void openCamera(Activity activity, int code) {


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        activity.startActivityForResult(cameraIntent, code);
    }

    /******************************************
     * To set first letter capitalize of particular string.
     ******************************************/
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public boolean checkDates(Context context, String Todate, String FromDate) {
        //SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        boolean b = false;
        try {
            if (dateFormatter.parse(Todate).before(dateFormatter.parse(FromDate))) {
                b = true;
                //AppUtils.ShowToast(context, "Valid Date");//If start date is before end date
            } else if (dateFormatter.parse(Todate).equals(dateFormatter.parse(FromDate))) {
                b = true;
            } else {
                //AppUtils.ShowToast(context, "InValid Date");
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }
    public static boolean isEditText(Context context, String string, EditText editText) {
        if (string.isEmpty()) {
            editText.requestFocus();
            editText.setError("Binnenkomen" + " " + editText.getHint());
            return false;
        } else {
            return true;
        }
    }

    public static boolean isStringMatched(Context context, String password, String
            confirmPass, EditText editText) {
        if (!password.equals(confirmPass)) {
            editText.requestFocus();
            editText.setError(editText.getHint() + " " + "Wachtwoord komt niet overeen");
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPassNotMatch(Context context, String password, String
            confirmPass, EditText editText) {
        if (password.equals(confirmPass)) {
            editText.requestFocus();
            editText.setError(editText.getHint() + " " + "moet anders zijn");
            return false;
        } else {
            return true;
        }
    }


    /*********************************************************
     * Multiple texts with different color into single TextView
     *********************************************************/
    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    public static String parseTodaysDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MM-dd-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //URLogs.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTodayCalenderDateOrTime(String time) {
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        //String outputPattern = "dd-MM-yyyy";
        String outputPattern = "dd-MM-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

            Log.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTodaysDateII(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy  HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTime(String time) {

        String inputPattern = "hh:mm a";
        String outputPattern = "HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTimeReverse(String time) {

        String outputPattern = "hh:mm a";
        String inputPattern = "HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTimeReverseForOpeningHour(String time) {

        String outputPattern = "h:mm a";
        String inputPattern = "HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTimeFromTimePicker(String time) {
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        //String outputPattern = "HH:mm a";
        String outputPattern = "HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String formatTimeFromServer(String dateStr) {
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String outputPattern = "hh:mm aa";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
            dateStr = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * @param s H:m timestamp, i.e. [Hour in day (0-23)]:[Minute in hour (0-59)]
     * @return total minutes after 00:00
     */
    public static int parseHrstoMins(String s) {
        String[] str = s.split(" ");
        String stringHourMins = str[0];
        String[] hourMin = stringHourMins.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /*******************************
     * Method for network is in working state or not.
     ******************************/
    public static boolean isOnline(Context cntext) {
        ConnectivityManager cm = (ConnectivityManager) cntext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }

    /*******************************
     * Hide keyboard from edit text
     ******************************/
    public static void hideKeyboard(Activity context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*******************************
     * Show keyboard with edit text
     ******************************/
    public static void showKeyboardWithFocus(View v, Activity a) {
        try {
            v.requestFocus();
            InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************************
     * Show toast with your custom messages
     *************************************/
    public static void showToast(Context context, String txt) {
        Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
    }

    /*************************************
     * Show progress bar with callback true or false
     *************************************/
    private static void showProgress(Button btn, ProgressBar progressBar, boolean progressVisible) {
        btn.setEnabled(!progressVisible);
        progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
    }
    public static LatLng getLocationFromAddress(AppCompatActivity context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    /*************************************
     * Custom progress dialog callback with context
     *************************************/
    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context) {
        showSimpleProgressDialog(context, null, "Loading...", false);
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************************
     * Just for checking network is available or not
     *************************************/
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*************************************
     * Get bitmap from Uri
     *************************************/
    public static Bitmap getBitmapFromUri(Uri uri, Context context)
            throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context
                .getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor
                .getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /************************************************
     * Show progress dialog with your custom messages
     ***********************************************/
    public static void showDialog(final String message, final Activity context) {
        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dialog = new ProgressDialog(context);
                dialog.setTitle(context.getString(R.string.app_name));
                dialog.setMessage(message);
                dialog.show();
            }
        });
    }

    /*****************************************************
     * Show error into edit text with your custom messages
     ****************************************************/
    public static void ShowError(EditText et, String error) {
        if (et.length() == 0) {
            et.setError(error);
        }
    }

    /*************************************
     * Get bitmap from Uri
     *************************************/
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // URLogs exception
            return null;
        }
    }

    public static int resolveTransparentStatusBarFlag(Context ctx) {
        String[] libs = ctx.getPackageManager().getSystemSharedLibraryNames();
        String reflect = null;
        if (libs == null)
            return 0;
        final String SAMSUNG = "touchwiz";
        final String SONY = "com.sonyericsson.navigationbar";
        for (String lib : libs) {
            if (lib.equals(SAMSUNG)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
            } else if (lib.startsWith(SONY)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT";
            }
        }
        if (reflect == null)
            return 0;
        try {
            Field field = View.class.getField(reflect);
            if (field.getType() == Integer.TYPE) {
                return field.getInt(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setTranslucentStatus(Window win, boolean on) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /*************************************
     * Get bitmap path with 100% quality
     *************************************/
    public static String getImagePath(Bitmap bitmap, int quality) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DEFAULT_IMAGE_DIRECTORY);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DEFAULT_IMAGE_DIRECTORY + Constants.DEFAULT_IMAGE_NAME;
        File f = new File(imagePath);
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return imagePath;
    }

    public static String getImagePath(Uri selectedImage, Context ctx) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor =
                ctx.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /********************************************************
     * Decodes image and scales it to reduce memory consumption
     *******************************************************/
    //
    public static Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 200;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***********************************************************
     * Below code Working for scale image as aspect ratio:
     Bitmap bitmapImage = BitmapFactory.decodeFile("Your path");
     int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
     Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
     your_imageview.setImageBitmap(scaled);
     Compress your image without losing quality like Whatsapp
     **********************************************************/
    public static String compressImage(String filePath) {
        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        //max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //check the rotation of the image and display it properly
        try {
            ExifInterface exif = new ExifInterface(filePath); //imgFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            e.getStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), Constants.DEFAULT_IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String formatDateFromServer(String dateStr) {
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
            dateStr = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

}
