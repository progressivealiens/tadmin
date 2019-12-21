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

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.CustomTypefaceSpan;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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

    MyTextview companyName;
    PrefData prefData;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 100;


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

        tvCompanyName.setText("Welcome , " + PrefData.readStringPref(PrefData.PREF_Company_name));

        View headerView = navView.getHeaderView(0);
        companyName = headerView.findViewById(R.id.drawer_company_name);
        companyName.setText(PrefData.readStringPref(PrefData.PREF_Company_name));

        navView.setNavigationItemSelectedListener(this);
        cardOperationalAttendance.setOnClickListener(this);
        cardGuardAttendance.setOnClickListener(this);
        cardSiteVisit.setOnClickListener(this);
        prefData = new PrefData(MainActivity.this);

        Picasso.get().load(Utils.BASE_COMPANY_LOGO + PrefData.readStringPref(PrefData.company_logo)).placeholder(R.drawable.progress_image).into(ivCompanyLogo);
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
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }


    private void logout() {
        PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS, false);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
