package com.techsalt.tadmin.views.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;
import com.techsalt.tadmin.webapi.WeatherApiResponse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveTrackingActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_current_address)
    MyTextview tvCurrentAddress;
    @BindView(R.id.tv_name)
    MyTextview tvName;
    @BindView(R.id.tv_last_sync)
    MyTextview tvLastSync;
    @BindView(R.id.tv_temperature)
    MyTextview tvTemperature;
    @BindView(R.id.tv_climate)
    MyTextview tvClimate;
    @BindView(R.id.tv_wind_speed)
    MyTextview tvWindSpeed;
    private GoogleMap mMap;
    FirebaseAuth mAuth;

    String showingLocationFor = "", address = "", climate = "", windSpeed = "";

    double Latitude;
    double Longitude;
    String name, dateTimestamp;
    LatLng mLatLng;
    Geocoder geocoder;
    List<Address> addresses;

    ProgressView progressView;
    ApiInterface apiInterface, apiInterfaceWeather;

    public static String callingActivity = "";
    Bitmap markerBitmap;

    List<ApiResponse.DataBean> siteDetailsModel = new ArrayList<>();

    Bitmap flagThree;
    DatabaseReference ref;
    public boolean isOperationDataShowing = false, isGuardDataShowing = false;
    double tempInFahrenheit, tempInCelsius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);
        ButterKnife.bind(this);

        initialize();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        loginToFirebase();
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Live Tracking");
        ivBack.setOnClickListener(this);
        progressView = new ProgressView(LiveTrackingActivity.this);
        progressView.showLoader();

        apiInterface = ApiClient.getClient(LiveTrackingActivity.this).create(ApiInterface.class);
        apiInterfaceWeather = ApiClient.getClientWeather(LiveTrackingActivity.this).create(ApiInterface.class);
        geocoder = new Geocoder(this, Locale.getDefault());

        flagThree = BitmapFactory.decodeResource(getResources(), R.drawable.landmark);
        flagThree = scaleBitmap(flagThree, 100, 100);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            showingLocationFor = bundle.getString(callingActivity);
        }
        markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        markerBitmap = scaleBitmap(markerBitmap, 120, 120);

    }

    private void loginToFirebase() {
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful() && task.isComplete()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (showingLocationFor.equalsIgnoreCase("operational")) {
                            isOperationDataShowing = true;
                            showOperationsLocation();
                        } else {
                            isGuardDataShowing = true;
                            showGuardLocation();
                        }
                    } else {
                        Toast.makeText(LiveTrackingActivity.this, "Unable to identify the user. Please logout and login again", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("firebaseFail", "firebaseFail");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showOperationsLocation() {

        final String path = PrefData.readStringPref(PrefData.PREF_Company_name) + "*" + PrefData.readStringPref(PrefData.emp_designation);

        ref = FirebaseDatabase.getInstance().getReference(path).child(PrefData.readStringPref(PrefData.emp_Id));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child("n").getValue(String.class);
                    dateTimestamp = dataSnapshot.child("d").getValue(String.class);
                    Latitude = dataSnapshot.child("la").getValue(Double.class);
                    Longitude = dataSnapshot.child("lo").getValue(Double.class);

                    try {
                        addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    connectApiToGetTemperature(Latitude, Longitude);

                } else {
                    progressView.hideLoader();
                    Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.live_location_is_under_processing), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("onCancelled", databaseError.getMessage());

                throw databaseError.toException();
            }
        });
    }

    private void connectApiToGetTemperature(double latitude, double longitude) {
        if (CheckNetworkConnection.isConnection1(LiveTrackingActivity.this, true)) {

            progressView.showLoader();

            Call<WeatherApiResponse> call = apiInterfaceWeather.fetchTemp(String.valueOf(latitude), String.valueOf(longitude));
            call.enqueue(new Callback<WeatherApiResponse>() {
                @Override
                public void onResponse(Call<WeatherApiResponse> call, Response<WeatherApiResponse> response) {
                    progressView.hideLoader();
                    try {
                        if (response.code() == 200) {
                            tempInFahrenheit = response.body().getCurrently().getTemperature();
                            tempInCelsius = ((tempInFahrenheit - 32) / 1.8);
                            climate = response.body().getCurrently().getSummary();
                            windSpeed = String.valueOf(response.body().getCurrently().getWindSpeed());

                            showMarkerOnMap();
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.bad_request), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.network_busy), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.not_found), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<WeatherApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    progressView.hideLoader();
                }
            });
        }
    }

    private void showGuardLocation() {

        final String path = PrefData.readStringPref(PrefData.PREF_Company_name) + "*" + PrefData.readStringPref(PrefData.route_Id);
        ref = FirebaseDatabase.getInstance().getReference(path);

        ref.child(PrefData.readStringPref(PrefData.emp_Id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("n").getValue(String.class);
                dateTimestamp = dataSnapshot.child("d").getValue(String.class);
                Latitude = dataSnapshot.child("la").getValue(double.class);
                Longitude = dataSnapshot.child("lo").getValue(double.class);

                Log.e("dateTimeGuard", dateTimestamp);

                try {
                    addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connectApiToGetTemperature(Latitude, Longitude);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*childListener=ref.child(PrefData.readStringPref(PrefData.emp_Id)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.getKey().equals("t")) {
                    dateTimestamp = dataSnapshot.getValue(String.class);
                } else if (dataSnapshot.getKey().equalsIgnoreCase("la")) {
                    Latitude = dataSnapshot.getValue(double.class);
                } else if (dataSnapshot.getKey().equalsIgnoreCase("lo")) {
                    Longitude = dataSnapshot.getValue(double.class);
                }

                showMarkerOnMap();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void showMarkerOnMap() {

        if (mMap != null) {

            if (progressView.isShowing()) {
                progressView.hideLoader();
            }

            mMap.clear();
            mLatLng = new LatLng(Latitude, Longitude);

            if (address.equalsIgnoreCase("")) {
                mMap.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)).title("Current Location"));
                tvCurrentAddress.setText("Address");
                tvName.setText(name);
                tvLastSync.setText(dateTimestamp);
                tvTemperature.setText(new DecimalFormat("##.##").format(tempInCelsius) + "° C");
                tvClimate.setText(climate);
                tvWindSpeed.setText(windSpeed + " m/s");

            } else {
                mMap.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)).title(name));

                tvCurrentAddress.setText(address);
                tvName.setText(name);
                tvLastSync.setText(dateTimestamp);
                tvTemperature.setText(new DecimalFormat("##.##").format(tempInCelsius) + "° C");
                tvClimate.setText(climate);
                tvWindSpeed.setText(windSpeed + " m/s");
            }

            /*mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    LinearLayout info = new LinearLayout(LiveTrackingActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(LiveTrackingActivity.this);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(LiveTrackingActivity.this);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setGravity(Gravity.CENTER);
                    snippet.setTypeface(null, Typeface.BOLD);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });*/
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 17));
            mMap.getUiSettings().setZoomControlsEnabled(true);

            connectApiToFetchSiteDetailsForFO();
        }
    }

    private void connectApiToFetchSiteDetailsForFO() {

        if (CheckNetworkConnection.isConnection1(LiveTrackingActivity.this, true)) {
            progressView.showLoader();
            Call<ApiResponse> call = apiInterface.employeeFlagDetails(PrefData.readStringPref(PrefData.emp_Id));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            siteDetailsModel.size();
                            siteDetailsModel.addAll(response.body().getData());

                            for (int i = 0; i < siteDetailsModel.size(); i++) {

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(siteDetailsModel.get(i).getLatitude(), siteDetailsModel.get(i).getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(flagThree))
                                        .title("Site Name :- " + siteDetailsModel.get(i).getSiteName()));

                            }

                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.bad_request), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 500) {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.network_busy), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else if (response.code() == 404) {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.not_found), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
                        } else {
                            Utils.showToast(LiveTrackingActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT, getResources().getColor(R.color.colorPink), getResources().getColor(R.color.colorWhite));
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

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

}
