package com.techsalt.tadmin.views.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveTrackingActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private GoogleMap mMap;
    FirebaseAuth mAuth;

    String showingLocationFor="";

    double Latitude;
    double Longitude;
    String name, dateTimestamp;
    LatLng mLatLng;

    ProgressView progressView;
    ApiInterface apiInterface;

    public static String callingActivity="";
    Bitmap markerBitmap;

    List<ApiResponse.DataBean> siteDetailsModel = new ArrayList<>();

    Bitmap flagThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);
        ButterKnife.bind(this);

        initialize();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

        flagThree= BitmapFactory.decodeResource(getResources(), R.drawable.landmark);
        flagThree = scaleBitmap(flagThree, 100, 100);

        Bundle bundle=getIntent().getExtras();
        if (bundle != null) {
            showingLocationFor=bundle.getString(callingActivity);
        }
        markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        markerBitmap = scaleBitmap(markerBitmap, 120, 120);

    }

    private void loginToFirebase() {
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);
        mAuth.signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful() && task.isComplete()) {

                    Log.e("firebaseSuccess","firebaseSuccess");

                    if (showingLocationFor.equalsIgnoreCase("operational")){
                        showOperationsLocation();
                    }else {
                        showGuardLocation();
                    }

                } else {
                    Log.e("firebaseFail","firebaseFail");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showOperationsLocation() {

        Log.e("CompanyName",PrefData.readStringPref(PrefData.PREF_Company_name));
        Log.e("empDesignation",PrefData.readStringPref(PrefData.emp_designation));
        Log.e("empId",PrefData.readStringPref(PrefData.emp_Id));

        final String path = PrefData.readStringPref(PrefData.PREF_Company_name) + "*" + PrefData.readStringPref(PrefData.emp_designation);

        Log.e("path",path);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

        ref.child(PrefData.readStringPref(PrefData.emp_Id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("onDataChanged","onDataChanged");
                Log.e("dataSnapshot",dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    name = dataSnapshot.child("n").getValue(String.class);
                    dateTimestamp = dataSnapshot.child("d").getValue(String.class);
                    Latitude = dataSnapshot.child("la").getValue(Double.class);
                    Longitude = dataSnapshot.child("lo").getValue(Double.class);

                    Log.e("name",name);
                    Log.e("dateTimestamp",dateTimestamp);
                    Log.e("Latitude",String.valueOf(Latitude));
                    Log.e("Longitude",String.valueOf(Longitude));

                    showMarkerOnMap();
                }else{
                    progressView.hideLoader();
                    Toast.makeText(LiveTrackingActivity.this, "Live location is under processing please retry after sometime", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("onCancelled",databaseError.getMessage());

                throw databaseError.toException();
            }
        });


        /*ref.child(PrefData.readStringPref(PrefData.emp_Id)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.getKey().equals("d")) {
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

    private void showGuardLocation() {

        final String path = PrefData.readStringPref(PrefData.PREF_Company_name) + "*" + PrefData.readStringPref(PrefData.route_Id);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

        ref.child(PrefData.readStringPref(PrefData.emp_Id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("n").getValue(String.class);
                dateTimestamp = dataSnapshot.child("t").getValue(String.class);
                Latitude = dataSnapshot.child("la").getValue(double.class);
                Longitude = dataSnapshot.child("lo").getValue(double.class);


                showMarkerOnMap();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ref.child(PrefData.readStringPref(PrefData.emp_Id)).addChildEventListener(new ChildEventListener() {
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
        });




    }

    private void showMarkerOnMap() {

        if (mMap != null) {

            if (progressView.isShowing()){
                progressView.hideLoader();
            }

            mMap.clear();
            mLatLng = new LatLng(Latitude, Longitude);
            MarkerOptions markerOptions = new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)).title("Current Location").snippet("Name :- " + name + "\nLast Sync :- " + dateTimestamp);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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
                    snippet.setTypeface(null, Typeface.BOLD);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 17));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.addMarker(markerOptions);

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

                            for (int i=0;i<siteDetailsModel.size();i++){

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(siteDetailsModel.get(i).getLatitude(), siteDetailsModel.get(i).getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(flagThree))
                                        .title("Site Name :- " + siteDetailsModel.get(i).getSiteName()));

                            }

                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(LiveTrackingActivity.this, R.string.bad_request, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(LiveTrackingActivity.this, R.string.network_busy, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(LiveTrackingActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LiveTrackingActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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
