package com.techsalt.tadmin.views.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.adapter.OperationalAttendanceAdapter;
import com.techsalt.tadmin.customviews.MyTextview;
import com.techsalt.tadmin.helper.CheckNetworkConnection;
import com.techsalt.tadmin.helper.PrefData;
import com.techsalt.tadmin.helper.ProgressView;
import com.techsalt.tadmin.helper.Utils;
import com.techsalt.tadmin.model.TrackingHistoryModel;
import com.techsalt.tadmin.webapi.ApiClient;
import com.techsalt.tadmin.webapi.ApiInterface;
import com.techsalt.tadmin.webapi.ApiResponse;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingHistoryActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_map_image)
    ImageView ivMapImage;

    Marker markerPost;

    private GoogleMap mMap;
    FirebaseFirestore firebaseFirestoreDB;
    List<String> timeKeys, timeKeysNext;
    List<String> sortedTimeKeys;

    List<TrackingHistoryModel> trackingHistoryModels;

    ApiInterface apiInterface;
    ProgressView progressView;
    Bitmap markerBitmap, progressBitmap, markerWaypoints, flagThree;
    LatLngBounds bounds;

    DateFormat formatter, dateFormatter;
    String currentTime = "", incrementDate = "";

    Time checkinTime, checkoutTime, temp;
    Date checkinDate, checkoutDate;

    double sourceLatitude = 0, sourceLongitude = 0, destLatitude = 0, destLongitude = 0;
    ListenerRegistration listenerRegistration, listenerRegistrationNext;
    List<LatLng> waypoints;
    List<Time> timeKeysDate;
    float bearingRotation;
    PolylineOptions lineOptions;

    List<ApiResponse.DataBean> siteDetailsModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_history);
        ButterKnife.bind(this);

        initialize();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_tracking_history);
        mapFragment.getMapAsync(this);
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Tracking History");
        ivBack.setOnClickListener(this);
        firebaseFirestoreDB = FirebaseFirestore.getInstance();

        timeKeys = new ArrayList<>();
        trackingHistoryModels = new ArrayList<>();
        sortedTimeKeys = new ArrayList<>();
        waypoints = new ArrayList<>();
        timeKeysDate = new ArrayList<>();

        timeKeysNext = new ArrayList<>();
        lineOptions = new PolylineOptions();

        apiInterface = ApiClient.getClient(TrackingHistoryActivity.this).create(ApiInterface.class);
        progressView = new ProgressView(TrackingHistoryActivity.this);

        flagThree = BitmapFactory.decodeResource(getResources(), R.drawable.landmark);
        flagThree = scaleBitmap(flagThree, 80, 80);

        markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        markerBitmap = scaleBitmap(markerBitmap, 100, 100);

        progressBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.up_marker);
        progressBitmap = scaleBitmap(progressBitmap, 20, 20);

        /*Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap != null) {
                    markerWaypoints = bitmap;
                }else{
                    Toast.makeText(TrackingHistoryActivity.this, "Error in showing posted image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(Utils.BASE_IMAGE + PrefData.readStringPref(PrefData.checkin_image)).resize(300,300).placeholder(R.drawable.progress_image).into(target);
        markerWaypoints = scaleBitmap(markerWaypoints, 80, 80);
*/
        markerWaypoints=createCustomMarker(this, Utils.BASE_IMAGE + PrefData.readStringPref(PrefData.checkin_image));

    }


    public static Bitmap createCustomMarker(Context context, String imagePath) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        Picasso.get().load(imagePath).resize(300,300).placeholder(R.drawable.progress_image).into(markerImage);
        /*TextView txt_name = (TextView) marker.findViewById(R.id.name);
        txt_name.setText(_name);*/

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
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

    private void getDataFromTheFirestoreDB() {

        Log.e("searchedDataFor", PrefData.readStringPref(PrefData.date_searched));
        Log.e("empId", PrefData.readStringPref(PrefData.emp_Id));
        DocumentReference docRef = firebaseFirestoreDB.collection("userPath").document(PrefData.readStringPref(PrefData.emp_Id) + "*" + PrefData.readStringPref(PrefData.date_searched));

        EventListener<DocumentSnapshot> eventListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    Log.e("AllData", documentSnapshot.toString());

                    timeKeys.clear();
                    sortedTimeKeys.clear();
                    waypoints.clear();
                    trackingHistoryModels.clear();
                    timeKeysDate.clear();

                    timeKeys.addAll(documentSnapshot.getData().keySet());

                    Collections.sort(timeKeys, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });

                    try {

                        formatter = new SimpleDateFormat("HH:mm:ss");
                        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

                        checkinTime = new Time(formatter.parse(PrefData.readStringPref(PrefData.checkin_time)).getTime());

                        checkinDate = dateFormatter.parse(PrefData.readStringPref(PrefData.checkin_date));
                        checkoutDate = dateFormatter.parse(PrefData.readStringPref(PrefData.checkout_date));

                        if (PrefData.readStringPref(PrefData.checkout_time).equalsIgnoreCase("On Work")) {

                            currentTime = Utils.selectedDateAndTime(Long.valueOf(Utils.currentTimeStamp()));
                            checkoutTime = new Time(formatter.parse(currentTime).getTime());

                        } else {
                            checkoutTime = new Time(formatter.parse(PrefData.readStringPref(PrefData.checkout_time)).getTime());
                        }

                        Log.e("checkinTime", PrefData.readStringPref(PrefData.checkin_time));
                        Log.e("checkoutTime", checkoutTime + "");

                        Log.e("checkinDate", checkinDate + "");
                        Log.e("checkoutDate", checkoutDate + "");

                        if (checkinDate.compareTo(checkoutDate) < 0) {

                            Log.e("SearchedDate", PrefData.readStringPref(PrefData.date_searched));

                            for (int i = 0; i < timeKeys.size(); i++) {

                                temp = new Time(formatter.parse(timeKeys.get(i)).getTime());

                                if (temp.after(checkinTime)) {
                                    timeKeysDate.add(temp);
                                    GeoPoint geoPoint = documentSnapshot.getGeoPoint(timeKeys.get(i));
                                    trackingHistoryModels.add(new TrackingHistoryModel(timeKeys.get(i), geoPoint.getLatitude(), geoPoint.getLongitude()));
                                }
                            }
                            Log.e("timeKeysOriginal", timeKeys.toString());
                            Log.e("timeKeysModified", timeKeysDate.toString());

                            getDataFromTheFirestoreDBAnotherDate();

                        } else {

                            for (int i = 0; i < timeKeys.size(); i++) {

                                temp = new Time(formatter.parse(timeKeys.get(i)).getTime());

                                if (temp.after(checkinTime) && temp.before(checkoutTime)) {
                                    timeKeysDate.add(temp);
                                    GeoPoint geoPoint = documentSnapshot.getGeoPoint(timeKeys.get(i));
                                    trackingHistoryModels.add(new TrackingHistoryModel(timeKeys.get(i), geoPoint.getLatitude(), geoPoint.getLongitude()));
                                }
                            }

                            Log.e("betweenTimeKeys", timeKeys.toString());
                            Log.e("modifiedTimeKeys", timeKeysDate.toString());
                            Log.e("MyModel", trackingHistoryModels.size() + "");

                            if (trackingHistoryModels.size() == 0) {
                                Toast.makeText(TrackingHistoryActivity.this, "No Tracking History Found", Toast.LENGTH_SHORT).show();
                            } else if (trackingHistoryModels.size() < 2) {
                                Toast.makeText(TrackingHistoryActivity.this, "User has not moved!!!", Toast.LENGTH_SHORT).show();
                            } else {

                                sourceLatitude = trackingHistoryModels.get(0).getLati();
                                sourceLongitude = trackingHistoryModels.get(0).getLongi();

                                destLatitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLati();
                                destLongitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLongi();

                                double tempSouceLati = trackingHistoryModels.get(0).getLati();
                                double tempSouceLongi = trackingHistoryModels.get(0).getLongi();

                                sortedTimeKeys.add(trackingHistoryModels.get(0).getTimeStamp());
                                waypoints.add(new LatLng(tempSouceLati, tempSouceLongi));

                                for (int i = 1; i < trackingHistoryModels.size(); i++) {

                                    if (getDistance(tempSouceLati, tempSouceLongi, trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()) > 15) {

                                        tempSouceLati = trackingHistoryModels.get(i).getLati();
                                        tempSouceLongi = trackingHistoryModels.get(i).getLongi();

                                        sortedTimeKeys.add(trackingHistoryModels.get(i).getTimeStamp());
                                        waypoints.add(new LatLng(trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()));

                                        Log.e("waypointsTotalSize", trackingHistoryModels.get(i).getTimeStamp() + "Lati :- " + trackingHistoryModels.get(i).getLati() + "Longi :- " + trackingHistoryModels.get(i).getLongi());
                                    }
                                }


                                Log.e("sortedTimeKeys", sortedTimeKeys.size() + "");
                                Log.e("MyModel", waypoints.size() + "");

                                setMarkerOnMap();

                            }
                        }

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    Toast.makeText(TrackingHistoryActivity.this, "No Location History Found for date " + PrefData.readStringPref(PrefData.date_searched) + " .", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (listenerRegistration == null) {
            listenerRegistration = docRef.addSnapshotListener(eventListener);
        }

    }

    private void getDataFromTheFirestoreDBAnotherDate() {

        try {

            dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormatter.parse(PrefData.readStringPref(PrefData.date_searched)));
            c.add(Calendar.DATE, 1);
            incrementDate = dateFormatter.format(c.getTime());

        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        DocumentReference documentRef = firebaseFirestoreDB.collection("userPath").document(PrefData.readStringPref(PrefData.emp_Id) + "*" + incrementDate);

        EventListener<DocumentSnapshot> eventsListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.e("AllDataNew", documentSnapshot.toString());

                    timeKeysNext.addAll(documentSnapshot.getData().keySet());

                    Collections.sort(timeKeysNext, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });

                    for (int i = 0; i < timeKeysNext.size(); i++) {

                        try {
                            temp = new Time(formatter.parse(timeKeysNext.get(i)).getTime());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        if (temp.before(checkoutTime)) {
                            timeKeysDate.add(temp);
                            GeoPoint geoPoint = documentSnapshot.getGeoPoint(timeKeysNext.get(i));
                            trackingHistoryModels.add(new TrackingHistoryModel(timeKeysNext.get(i), geoPoint.getLatitude(), geoPoint.getLongitude()));
                        }
                    }

                    sourceLatitude = trackingHistoryModels.get(0).getLati();
                    sourceLongitude = trackingHistoryModels.get(0).getLongi();

                    destLatitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLati();
                    destLongitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLongi();

                    double tempSouceLati = trackingHistoryModels.get(0).getLati();
                    double tempSouceLongi = trackingHistoryModels.get(0).getLongi();

                    sortedTimeKeys.add(trackingHistoryModels.get(0).getTimeStamp());
                    waypoints.add(new LatLng(tempSouceLati, tempSouceLongi));


                    for (int i = 1; i < trackingHistoryModels.size(); i++) {

                        if (getDistance(tempSouceLati, tempSouceLongi, trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()) > 15) {

                            tempSouceLati = trackingHistoryModels.get(i).getLati();
                            tempSouceLongi = trackingHistoryModels.get(i).getLongi();

                            sortedTimeKeys.add(trackingHistoryModels.get(i).getTimeStamp());
                            waypoints.add(new LatLng(trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()));

                            Log.e("waypointsTotalSize", trackingHistoryModels.get(i).getTimeStamp() + "Lati :- " + trackingHistoryModels.get(i).getLati() + "Longi :- " + trackingHistoryModels.get(i).getLongi());
                        }
                    }

                    Log.e("sortedTimeKeys", sortedTimeKeys.size() + "");
                    Log.e("MyModel", waypoints.size() + "");

                    setMarkerOnMap();

                } else {

                    sourceLatitude = trackingHistoryModels.get(0).getLati();
                    sourceLongitude = trackingHistoryModels.get(0).getLongi();

                    destLatitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLati();
                    destLongitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLongi();

                    double tempSouceLati = trackingHistoryModels.get(0).getLati();
                    double tempSouceLongi = trackingHistoryModels.get(0).getLongi();


                    sortedTimeKeys.add(trackingHistoryModels.get(0).getTimeStamp());
                    waypoints.add(new LatLng(tempSouceLati, tempSouceLongi));


                    for (int i = 1; i < trackingHistoryModels.size(); i++) {

                        if (getDistance(tempSouceLati, tempSouceLongi, trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()) > 15) {

                            tempSouceLati = trackingHistoryModels.get(i).getLati();
                            tempSouceLongi = trackingHistoryModels.get(i).getLongi();

                            sortedTimeKeys.add(trackingHistoryModels.get(i).getTimeStamp());
                            waypoints.add(new LatLng(trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()));

                            Log.e("waypointsTotalSize", trackingHistoryModels.get(i).getTimeStamp() + "Lati :- " + trackingHistoryModels.get(i).getLati() + "Longi :- " + trackingHistoryModels.get(i).getLongi());
                        }
                    }

                    setMarkerOnMap();

                    Toast.makeText(TrackingHistoryActivity.this, "No Data Updated For Date " + incrementDate + " .", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (listenerRegistrationNext == null) {
            listenerRegistrationNext = documentRef.addSnapshotListener(eventsListener);
        }


    }

    private void setMarkerOnMap() {

        if (mMap != null) {

            mMap.clear();

            //For the Post marker
            if (OperationalAttendanceAdapter.postDataModels.size() > 0) {
                for (int i = 0; i < OperationalAttendanceAdapter.postDataModels.size(); i++) {
                    markerPost = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(OperationalAttendanceAdapter.postDataModels.get(i).getPostLatitude()), Double.valueOf(OperationalAttendanceAdapter.postDataModels.get(i).getPostLongitude())))
                            .title("Posted from here At :- " + OperationalAttendanceAdapter.postDataModels.get(i).getPostDate())
                            .snippet("\n\n" + "Posted Text :- " + OperationalAttendanceAdapter.postDataModels.get(i).getText() + "\n\n" + " Posted Address :- " + OperationalAttendanceAdapter.postDataModels.get(i).getPostAddress() + "\n\n")
                            .icon(BitmapDescriptorFactory.fromBitmap(markerWaypoints))
                    );

                }
            }


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {


                    LinearLayout info = new LinearLayout(TrackingHistoryActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(TrackingHistoryActivity.this);
                    title.setTextColor(Color.GRAY);
                    title.setGravity(Gravity.CENTER);
                    title.setText(marker.getTitle());

                    ImageView imageView = new ImageView(TrackingHistoryActivity.this);

                    if (marker.equals(markerPost)) {
                        Picasso.get().load(Utils.BASE_IMAGE_COMMUNICATION + OperationalAttendanceAdapter.postDataModels.get(0).getImage()).resize(200, 250).into(imageView);
                    }

                    TextView snippet = new TextView(TrackingHistoryActivity.this);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setGravity(Gravity.CENTER);
                    snippet.setTypeface(null, Typeface.BOLD);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);
                    info.addView(imageView);

                    return info;

                }
            });

            //For the Checkin marker

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(sourceLatitude, sourceLongitude))
                    .title("Checked-In Here At :- " + PrefData.readStringPref(PrefData.checkin_time))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            //For the Checkout marker

            if (trackingHistoryModels.size() > 3) {

                if (PrefData.readStringPref(PrefData.checkout_time).equalsIgnoreCase("On Work")) {

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(destLatitude, destLongitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                            .title("Current Location").snippet("Name :- " + PrefData.readStringPref(PrefData.emp_name)));

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            LinearLayout info = new LinearLayout(TrackingHistoryActivity.this);
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(TrackingHistoryActivity.this);
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(TrackingHistoryActivity.this);
                            snippet.setTextColor(Color.BLACK);
                            snippet.setTypeface(null, Typeface.BOLD);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });

                } else {

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(destLatitude, destLongitude))
                            .title("Checked-Out Here At :- " + PrefData.readStringPref(PrefData.checkout_time))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            }

            lineOptions.addAll(waypoints);

            //For the waypoints marker

            if (waypoints.size() > 3) {

                for (int i = 1; i < waypoints.size() - 1; i++) {

                    bearingRotation = (float) bearing(waypoints.get(i).latitude, waypoints.get(i).longitude, waypoints.get(i + 1).latitude, waypoints.get(i + 1).longitude);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(waypoints.get(i).latitude, waypoints.get(i).longitude))
                            .title("At:- " + sortedTimeKeys.get(i))
                            .rotation(bearingRotation)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromBitmap(progressBitmap)));

                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latlong : waypoints) {
                    builder.include(latlong);
                }
                bounds = builder.build();
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
                if (lineOptions != null) {
                    mMap.addPolyline(lineOptions);
                }
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            } else {
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sourceLatitude, sourceLongitude), 15));

                Toast.makeText(TrackingHistoryActivity.this, "User have not travelled", Toast.LENGTH_SHORT).show();
            }

            connectApiToFetchSiteDetailsForFO();

        }

    }

    private void connectApiToFetchSiteDetailsForFO() {

        if (CheckNetworkConnection.isConnection1(TrackingHistoryActivity.this, true)) {
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
                            Toast.makeText(TrackingHistoryActivity.this, R.string.bad_request, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(TrackingHistoryActivity.this, R.string.network_busy, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(TrackingHistoryActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TrackingHistoryActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getDataFromTheFirestoreDB();
    }

    public double rad(double x) {
        return x * Math.PI / 180;
    }

    public double getDistance(double p1lat, double p1long, double p2lat, double p2long) {

        int R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(p2lat - p1lat);
        double dLong = rad(p2long - p1long);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1lat)) * Math.cos(rad(p2lat)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; // returns the distance in meter

    }

    public double bearing(double startLat, double startLng, double endLat, double endLng) {
        double longi1 = startLng;
        double longi2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff = Math.toRadians(longi2 - longi1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
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

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        if (listenerRegistrationNext != null) {
            listenerRegistrationNext.remove();
        }

        finish();
    }

 /* public class GetRouteViaAsyncTask extends AsyncTask<List<LatLng>, Void, List<LatLng>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isCancelled()) {
                progressView.showLoader();
            }
        }


        @Override
        protected List<LatLng> doInBackground(List<LatLng>... params) {
            List<LatLng> snappedPoints = new ArrayList<>();

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL urll = new URL(buildRequestUrl(params[0]));
                connection = (HttpURLConnection) urll.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder jsonStringBuilder = new StringBuilder();

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    jsonStringBuilder.append(line);
                    jsonStringBuilder.append("\n");
                }

                JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());
                JSONArray snappedPointsArr = jsonObject.getJSONArray("snappedPoints");

                for (int i = 0; i < snappedPointsArr.length(); i++) {
                    JSONObject snappedPointLocation = ((JSONObject) (snappedPointsArr.get(i))).getJSONObject("location");
                    double lattitude = snappedPointLocation.getDouble("latitude");
                    double longitude = snappedPointLocation.getDouble("longitude");
                    snappedPoints.add(new LatLng(lattitude, longitude));
                }


            } catch (MalformedURLException e) {
                Log.e("MalformedException", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return snappedPoints;
        }

        @Override
        protected void onPostExecute(List<LatLng> latLngs) {
            super.onPostExecute(latLngs);

            if (!isCancelled()) {
                progressView.hideLoader();

                if (latLngs.isEmpty()) {
                    Toast.makeText(TrackingHistoryActivity.this, "No Location History Found for this date", Toast.LENGTH_SHORT).show();
                } else {

                    if (mMap != null) {

                        mMap.clear();

                        PolylineOptions polyLineOptions = new PolylineOptions();
                        polyLineOptions.addAll(latLngs);
                        polyLineOptions.width(12);
                        polyLineOptions.color(Color.parseColor("#90A036"));
                        mMap.addPolyline(polyLineOptions);

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(sourceLatitude, sourceLongitude))
                                .title("Checked-In Here")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                        for (int i = 1; i < trackingHistoryModels.size() - 1; i++) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(trackingHistoryModels.get(i).getLati(), trackingHistoryModels.get(i).getLongi()))
                                    .title("Updated Points")
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                        }
                        if (trackingHistoryModels.size() > 1) {

                            if (PrefData.readStringPref(PrefData.checkout_time).equalsIgnoreCase("On Work")) {

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(destLatitude, destLongitude))
                                        .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                                        .title("Current Location"));

                            } else {

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(destLatitude, destLongitude))
                                        .title("Checked-Out Here")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            }
                        }

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latlong : latLngs) {
                            builder.include(latlong);
                        }
                        LatLngBounds bounds = builder.build();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    }
                }
            }
        }
    }

    private String buildRequestUrl(List<LatLng> trackPoints) {
        StringBuilder url = new StringBuilder();
        url.append("https://roads.googleapis.com/v1/snapToRoads?path=");

        for (LatLng trackPoint : trackPoints) {
            url.append(String.format("%8.7f", trackPoint.latitude));
            url.append(",");
            url.append(String.format("%8.7f", trackPoint.longitude));
            url.append("|");
        }
        url.delete(url.length() - 1, url.length());
        url.append("&interpolate=true");
        url.append(String.format("&key=%s", "AIzaSyAADDGaF-1C9eEbbPvc3YCx1lGwKb5t_Vc"));

        Log.e("URLRoute", url.toString());

        return url.toString();
    }*/
}