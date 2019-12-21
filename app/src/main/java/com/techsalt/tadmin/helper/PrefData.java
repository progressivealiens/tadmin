package com.techsalt.tadmin.helper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Hp on 4/5/2018.
 */

public class PrefData extends Application {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;

    private static PrefData mInstance;
    private static String sharedPrefName = "Agile_contact";

    public static String PREF_LOGINSTATUS = "pref_loginstatus";
    public static String PREF_Company_name = "pref_company_name";
    public static String company_logo = "company_logo";
    public static String emp_Id = "pref_emp_Id";
    public static String route_Id = "pref_route_Id";
    public static String emp_designation = "pref_emp_designation";
    public static String date_searched = "pref_date_searched";
    public static String checkin_time = "pref_checkin_time";
    public static String checkout_time = "pref_checkout_time";
    public static String checkout_date = "pref_checkout_date";
    public static String checkin_date = "pref_checkin_date";
    public static String checkin_image = "pref_checkin_image";
    public static String emp_name = "pref_emp_name";


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;

    }

    public PrefData() {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized PrefData getInstance() {
        return mInstance;
    }

    public PrefData(Context con) {
        mSharedPreferences = con.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
    }

    public void clear() {
        editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearPref() {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearKeyPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String readStringPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        return mSharedPreferences.getString(key, "");
    }

    public static void writeStringPref(String key, String data) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        editor = mSharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();

    }

    public static boolean readBooleanPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        return mSharedPreferences.getBoolean(key, false);


    }

    public static void writeBooleanPref(String key, boolean data) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        editor = mSharedPreferences.edit();
        editor.putBoolean(key, data);
        editor.apply();

    }

    public static long readLongPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        return mSharedPreferences.getLong(key, 0);
    }

    public static void writeLongPref(String key, long data) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        editor = mSharedPreferences.edit();
        editor.putLong(key, data);
        editor.apply();

    }

    public SharedPreferences getmSharedPreferences() {
        return mSharedPreferences;
    }

    public void setmSharedPreferences(SharedPreferences mSharedPreferences) {
        PrefData.mSharedPreferences = mSharedPreferences;
    }

    public String getSharedPrefName() {
        return sharedPrefName;
    }

    public void setSharedPrefName(String sharedPrefName) {
        PrefData.sharedPrefName = sharedPrefName;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        PrefData.editor = editor;
    }


}
