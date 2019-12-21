package com.techsalt.tadmin.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.techsalt.tadmin.customviews.MyTextview;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Process.killProcess;

/**
 * Created by Hp on 2/13/2018.
 */

public class Utils {
    public static String BASE = "https://www.trackkers.com/api/tadmin/";
    public static String BASE_COMPANY_LOGO = "https://trackkers.com/public/uploads/companyLogo/";
    public static String BASE_IMAGE = "https://www.trackkers.com/public/storage/operationalCheckInImages/";
    public static String BASE_IMAGE_COMMUNICATION = "https://www.trackkers.com/public/uploads/communication/";
    public static String BASE_IMAGE_GUARD = "https://www.trackkers.com/public/storage/guardSelfie/";
    public static String BASE_IMAGE_COMMUNICATION_FO = "https://trackkers.com/public/storage/siteVisitCommunication/";
    public static String BASE_IMAGE_SITE_START_SELFIE = "https://www.trackkers.com/public/storage/siteVisitSelfie/";
    public static String BASE_IMAGE_QR_PETROL_HISTORY = "https://www.trackkers.com/public/storage/siteVisitPresence/";
    public static String BASE_IMAGE_GUARD_PATROLLING_HISTORY = "https://www.trackkers.com/public/storage/guardScanImage/";

    public static boolean isInternetOn(Activity context) {
        boolean isActive = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            isActive = true;
        } else if (

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            isActive = false;
        }


        return isActive;
    }



    public static boolean isLocationServicesEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            if (lm != null) {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ex) {
        }

        return gps_enabled ;
    }



    public void changeStatusBarColor(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView=activity.getCurrentFocus();

        if (focusedView !=null){
            inputMethodManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(), 0);
        }
    }

    public static void stopAlarmService(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        Iterator<ActivityManager.RunningAppProcessInfo> iter = runningAppProcesses.iterator();

        while(iter.hasNext()){
            ActivityManager.RunningAppProcessInfo next = iter.next();

            String pricessName = context.getPackageName() + ":service";

            if(next.processName.equals(pricessName)){
                killProcess(next.pid);
                break;
            }
        }
    }

    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        double level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        double scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        double batteryPct = level / scale;

        return (int) (batteryPct * 100D);
    }

    public static void log(String key, String value) {
        Log.e(key, value);
    }

    public static void toast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(View view, String msg, TextInputEditText textView, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        textView.requestFocus();
    }

    public static void showSnackBar(View view, String msg, AppCompatEditText textView, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        textView.requestFocus();
    }

    public static void showSnackBar(View view, String msg, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void noConnection(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public static void oldDatePicker(Context context, final TextView textview) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DecimalFormat mFormat = new DecimalFormat("00");
                textview.setText(mFormat.format(Double.valueOf(dayOfMonth)) + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.YEAR, -18);
        datePicker.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePicker.show();
    }


    public static void showDatePicker(Context context, final MyTextview textview) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DecimalFormat mFormat = new DecimalFormat("00");
                textview.setText(mFormat.format(Double.valueOf(dayOfMonth)) + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.YEAR,0);
        datePicker.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePicker.show();
    }


    public static void showDatePicker(Context context, final AppCompatEditText editText, String old) {
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DecimalFormat mFormat = new DecimalFormat("00");
                editText.setText(mFormat.format(monthOfYear + 1) + "/" + year);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        Calendar end = Calendar.getInstance();
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            end.setTime(sdf.parse(old));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.add(end.get(Calendar.YEAR), 0);
        cal.add(end.get(Calendar.MONTH), 0);
        datePicker.getDatePicker().setMinDate(end.getTimeInMillis());
        datePicker.show();
    }

    public static String selectedDateAndTime(long x) {
        long epoch = x / 1000;
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(epoch * 1000));
        String selectedDate = date.substring(0, 10);
        String selectedTime = date.substring(11, 19);
        //edittext.setText(selectedTime);

        return selectedTime;
    }

    public static String selectedDate(long x) {
        long epoch = x / 1000;
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(epoch * 1000));
        String selectedDate = date.substring(0, 10);
        String selectedTime = date.substring(11, 19);
        //edittext.setText(selectedTime);

        return selectedDate;
    }

    public static String selectedDateAndTimeFormat(long x, TextView textview) {
        long epoch = x / 1000;
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(epoch * 1000));
        String selectedDate = date.substring(0, 10);
        String selectedTime = date.substring(11, 19);
        textview.setText(selectedDate);

        return selectedDate;
    }

    public static String selectedDateFormat(long x, TextView textview) {
        long epoch = x / 1000;
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(epoch * 1000));
        String selectedDate = date.substring(0, 10);
        String selectedTime = date.substring(11, 19);
        textview.setText("Date : "+selectedDate);

        return selectedDate;
    }

    public static String selectedTimeFormat(long x, TextView textview) {
        long epoch = x / 1000;
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(epoch * 1000));
        String selectedDate = date.substring(0, 10);
        String selectedTime = date.substring(11, 19);
        textview.setText("Time : "+selectedTime);

        return selectedTime;
    }

    public static String currentTimeStamp() {
        Long tsLong = System.currentTimeMillis();
        return tsLong.toString();
    }

    public static long fromDateToMillis(String date) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return android_id;
    }

    public static String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }

}
