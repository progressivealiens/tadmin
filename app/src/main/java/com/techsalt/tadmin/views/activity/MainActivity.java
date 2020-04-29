package com.techsalt.tadmin.views.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.CustomTypefaceSpan;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;
import com.techsalt.tadmin.webapi.BarGraphResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.tv_subtitle)
    MyTextview tvSubtitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.card_operational_attendance)
    CardView cardOperationalAttendance;
    @BindView(R.id.card_guard_attendance)
    CardView cardGuardAttendance;
    @BindView(R.id.tv_company_name)
    MyTextview tvCompanyName;
    @BindView(R.id.card_site_visit)
    CardView cardSiteVisit;
    @BindView(R.id.iv_company_logo)
    ImageView ivCompanyLogo;
    @BindView(R.id.bar_chart_view)
    AnyChartView barChartView;

    MyTextview companyName,adminEmail;
    PrefData prefData;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 100;

    ApiInterface apiInterface;
    ProgressView progressView;


    @Override
    protected void onStart() {
        super.onStart();
        if (!startRequestPermission()) {
            startRequestPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initialize();

        Menu m = navView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "aver_bold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("HOME");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        apiInterface = ApiClient.getClient(MainActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(MainActivity.this);

        tvCompanyName.setText("Welcome , " + PrefData.readStringPref(PrefData.PREF_Company_name));

        View headerView = navView.getHeaderView(0);
        companyName = headerView.findViewById(R.id.drawer_company_name);
        adminEmail = headerView.findViewById(R.id.drawer_email_admin);
        companyName.setText(PrefData.readStringPref(PrefData.PREF_Company_name));
        adminEmail.setText(PrefData.readStringPref(PrefData.PREF_admin_email));

        navView.setNavigationItemSelectedListener(this);
        cardOperationalAttendance.setOnClickListener(this);
        cardGuardAttendance.setOnClickListener(this);
        cardSiteVisit.setOnClickListener(this);
        prefData = new PrefData(MainActivity.this);

        Picasso.get().load(Utils.BASE_COMPANY_LOGO + PrefData.readStringPref(PrefData.company_logo)).placeholder(R.drawable.progress_image).into(ivCompanyLogo);

        connectApiToFetchBarGraphData();

    }

    private void connectApiToFetchBarGraphData() {
        if (CheckNetworkConnection.isConnection1(MainActivity.this, true)) {
            progressView.showLoader();
            Call<BarGraphResponse> call = apiInterface.BarGraph(PrefData.readStringPref(PrefData.PREF_Company_name));
            call.enqueue(new Callback<BarGraphResponse>() {
                @Override
                public void onResponse(Call<BarGraphResponse> call, Response<BarGraphResponse> response) {
                    progressView.hideLoader();
                    try {
                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            int noOfColumn = 4;

                            // barChartView.setBackgroundColor("#e8e8e8");
                            APIlib.getInstance().setActiveAnyChartView(barChartView);
                            barChartView.setLicenceKey(getResources().getString(R.string.licence));
                            Cartesian cartesian = AnyChart.line();
                            cartesian.background().enabled(true);
                            cartesian.background().fill("#e8e8e8");
                            cartesian.animation(true);
                            cartesian.padding(10d, 20d, 5d, 20d);
                            cartesian.crosshair().enabled(true);
                            cartesian.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.title("CHECK-IN, SITE-VISIT, QR-SCAN(LAST 7 DAYS)");
                            cartesian.yAxis(0).title("Activity");
                            cartesian.xAxis(0).title("Date");
                            cartesian.xAxis(0).labels().padding(10d, 10d, 10d, 10d);
                            cartesian.xAxis(0).labels().rotation(-90);
                            cartesian.xAxis(0).labels().padding(5, 0, 5, 0);

                            List<DataEntry> seriesData = new ArrayList<>();
                            seriesData.clear();
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                String Date = response.body().getData().get(i).getDate();
                                String checkins = String.valueOf(response.body().getData().get(i).getCheckins());
                                String sitevisit = String.valueOf(response.body().getData().get(i).getSitevisit());
                                String qrscan = String.valueOf(response.body().getData().get(i).getQrscan());
                                String[] contentSingleParts = {Date, checkins, sitevisit, qrscan};

                                String[] arraytopass = new String[noOfColumn - 1];
                                String arraytopassZeroIndex = "";
                                for (int j = 0; j < noOfColumn; j++) {
                                    if (j == 0) {
                                        arraytopassZeroIndex = contentSingleParts[j].replaceAll("\"", "");
                                    } else {
                                        arraytopass[j - 1] = contentSingleParts[j];
                                    }
                                }
                                seriesData.add(new CustomDataEntry(arraytopassZeroIndex, arraytopass));
                            }
                            Set set = Set.instantiate();
                            set.data(seriesData);

                            List<String> lineValues = new ArrayList<>();
                            lineValues.clear();
                            lineValues.add("Date");
                            lineValues.add("Check-In");
                            lineValues.add("Site Visit");
                            lineValues.add("Qr Scan");

                            for (int j = 1; j < noOfColumn; j++) {
                                String v = "value" + (j + 1);
                                Mapping column1Data = set.mapAs("{ x: 'x', value: \'" + v + "\' }");
                                Line series1 = cartesian.line(column1Data);
                                series1.name(lineValues.get(j));
                                series1.hovered().markers().enabled(true);
                                series1.hovered().markers().type(MarkerType.DIAMOND).size(5d);
                                series1.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
                            }

                            cartesian.legend().enabled(true);
                            cartesian.legend().fontSize(13d);
                            cartesian.legend().padding(0d, 0d, 10d, 0d);
                            barChartView.setChart(cartesian);
                        } else {
                            Utils.showSnackBar(drawerLayout, response.body().getMsg(), MainActivity.this);
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(MainActivity.this,getResources().getString(R.string.bad_request),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(MainActivity.this,getResources().getString(R.string.network_busy),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(MainActivity.this,getResources().getString(R.string.not_found),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(MainActivity.this,getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BarGraphResponse> call, Throwable t) {
                    t.printStackTrace();
                    progressView.hideLoader();
                }
            });
        }
    }

    private boolean startRequestPermission() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            drawerLayout.closeDrawers();
            finish();
            startActivity(getIntent());
        } else if (id == R.id.nav_share) {
            share();
        } else if (id == R.id.nav_rate_us) {
            rateUs();
        } else if (id == R.id.nav_logout) {
            connectApiToLogout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectApiToLogout() {
        if (CheckNetworkConnection.isConnection1(MainActivity.this, true)) {
            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.Logout(PrefData.readStringPref(PrefData.admin_id));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {
                        if (response.body()!=null && response.body().getStatus()!=null){
                            if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {
                                Utils.showToast(MainActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorLightGreen), getResources().getColor(R.color.colorWhite));

                                PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS, false);

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Utils.showSnackBar(drawerLayout, response.body().getMsg(), MainActivity.this);
                            }
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(MainActivity.this, getResources().getString(R.string.bad_request), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(MainActivity.this, getResources().getString(R.string.network_busy), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(MainActivity.this, getResources().getString(R.string.not_found), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(MainActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    progressView.hideLoader();
                }
            });
        }
    }

    private void share() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "TAdmin");
            String sAux = "TAdmin \n\nLet me recommend you this app to view your employee attendance and realtime tracking system\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.techsalt.tadmin";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Utils.showToast(MainActivity.this,getResources().getString(R.string.unable_to_find),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorPink),getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_operational_attendance:
                startActivity(new Intent(MainActivity.this, OperationalAttendanceActivity.class));
                break;
            case R.id.card_guard_attendance:
                startActivity(new Intent(MainActivity.this, GuardAttendanceActivity.class));
                break;
            case R.id.card_site_visit:
                startActivity(new Intent(MainActivity.this, SiteVisitHistory.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length <= 0) {
                Log.e("tag", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showToast(MainActivity.this,getResources().getString(R.string.permission_granted),Toast.LENGTH_SHORT,getResources().getColor(R.color.colorLightGreen),getResources().getColor(R.color.colorWhite));
            }
        }
    }

    public class CustomDataEntry extends ValueDataEntry {
        String val;

        CustomDataEntry(String x, String array[]) {
            super(x, 0);
            val = "";
            for (int i = 1; i <= array.length; i++) {
                val = "value" + (i + 1);
                if (array[i - 1].equalsIgnoreCase("\"\"") || array[i - 1].equalsIgnoreCase("") || array[i - 1].equalsIgnoreCase(" ") || array[i - 1].isEmpty()) {
                    array[i - 1] = "0.0";
                }

                try {
                    //Log.e("insertedValues",Double.valueOf(array[i - 1].replaceAll("\"", ""))+"");
                    setValue(val.replaceAll("_", " "), Double.valueOf(array[i - 1].replaceAll("\"", "")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();

                    Utils.showToast(MainActivity.this,getResources().getString(R.string.number_format_exception)+ e,Toast.LENGTH_SHORT,getResources().getColor(R.color.colorLightGreen),getResources().getColor(R.color.colorWhite));
                }
            }
        }

    }

}
