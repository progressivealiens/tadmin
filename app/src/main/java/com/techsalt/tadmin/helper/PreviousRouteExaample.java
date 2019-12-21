package com.techsalt.tadmin.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.model.TrackingHistoryModel;
import com.techsalt.tadmin.views.activity.TrackingHistoryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.ButterKnife;

public class PreviousRouteExaample {

   /* private GoogleMap mMap;
    FirebaseFirestore firebaseFirestoreDB;
    List<String> timeKeys;

    List<String> sortedTimeKeys;

    List<TrackingHistoryModel> trackingHistoryModels;
    public String URL = "";
    ProgressView progressView;
    public TrackingHistoryActivity.asyncTaskToFetchRoute fetchRouteApi;

    int totalCount = 0, iterationValue = 0;
    Bitmap markerBitmap;


    double sourceLatitude = 0, sourceLongitude = 0, destLatitude = 0, destLongitude = 0;
    ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_history);
        ButterKnife.bind(this);

        initialize();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_tracking_history);
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
        progressView = new ProgressView(TrackingHistoryActivity.this);
        markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        markerBitmap = scaleBitmap(markerBitmap, 120, 120);
    }


    private void getDataFromTheFirestoreDB() {

        Log.e("searchedDataFor", PrefData.readStringPref(PrefData.date_searched));
        DocumentReference docRef = firebaseFirestoreDB.collection("userPath").document(PrefData.readStringPref(PrefData.emp_Id) + "*" + PrefData.readStringPref(PrefData.date_searched));


        EventListener<DocumentSnapshot> eventListener=new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    Log.e("AllData", documentSnapshot.toString());


                    timeKeys.clear();
                    trackingHistoryModels.clear();
                    timeKeys.addAll(documentSnapshot.getData().keySet());

                    Collections.sort(timeKeys, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });

                    *//*int startIndex=Collections.binarySearch(timeKeys,PrefData.readStringPref(PrefData.checkin_time));
                    int endIndex=Collections.binarySearch(timeKeys,PrefData.readStringPref(PrefData.checkout_time));
                    int insertionPoint= -(startIndex+1);
                    //int uptoIndex=endIndex

                    sortedTimeKeys=timeKeys.subList(insertionPoint,timeKeys.size());
                    Log.e("startIndex",startIndex+"");*//*


                    for (int i = 0; i < documentSnapshot.getData().size(); i++) {
                        GeoPoint geoPoint = documentSnapshot.getGeoPoint(timeKeys.get(i));
                        trackingHistoryModels.add(new TrackingHistoryModel(timeKeys.get(i), geoPoint.getLatitude(), geoPoint.getLongitude()));
                    }

                    *//*for (int la = 0; la < trackingHistoryModels.size(); la++) {
                        Log.e("finalModel", trackingHistoryModels.get(la).getTimeStamp() + " * " + trackingHistoryModels.get(la).getLati() + " * " + trackingHistoryModels.get(la).getLongi());
                    }*//*


                    sourceLatitude = trackingHistoryModels.get(0).getLati();
                    sourceLongitude = trackingHistoryModels.get(0).getLongi();

                    destLatitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLati();
                    destLongitude = trackingHistoryModels.get(trackingHistoryModels.size() - 1).getLongi();

                    URL = getMapsApiDirectionsUrl();
                    fetchRouteApi = new TrackingHistoryActivity.asyncTaskToFetchRoute(URL);
                    fetchRouteApi.execute();


                } else {
                    Toast.makeText(TrackingHistoryActivity.this, "No Location History Found for date " + PrefData.readStringPref(PrefData.date_searched) + " .", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (listenerRegistration == null ) {
            listenerRegistration = docRef.addSnapshotListener(eventListener);
        }

    }

    private String getMapsApiDirectionsUrl() {
        String origin = "origin=" + sourceLatitude + "," + sourceLongitude;
        String destination = "destination=" + destLatitude + "," + destLongitude;
        String wayPoint = "waypoints=";
        String finalURL;

        Log.e("ListCount", trackingHistoryModels.size() + "");

        if (trackingHistoryModels.size() >= 27) {

            totalCount = trackingHistoryModels.size();
            iterationValue = (trackingHistoryModels.size() / 25) + 1;
            Log.e("iterateValue", iterationValue + "");
            int counter = 1;
            for (int z = iterationValue; z < trackingHistoryModels.size() - 1; z += iterationValue) {
                LatLng points = new LatLng(trackingHistoryModels.get(z).getLati(), trackingHistoryModels.get(z).getLongi());
                if (counter < 26) {
                    wayPoint = wayPoint + points.latitude + "," + points.longitude + "%7C";
                    Log.e("counter", counter + "");
                    Log.e("counterIndex", z + "");
                    counter++;
                }
            }

            String key = "key=AIzaSyAADDGaF-1C9eEbbPvc3YCx1lGwKb5t_Vc";
            String sensor = "sensor=false";
            String params = origin + "&" + destination + "&" + sensor + "&" + wayPoint + "&" + key;
            String output = "json";

            finalURL = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
        } else {
            totalCount = trackingHistoryModels.size() - 2;
            iterationValue = 1;

            for (int z = iterationValue; z < trackingHistoryModels.size() - 1; z += iterationValue) {
                LatLng points = new LatLng(trackingHistoryModels.get(z).getLati(), trackingHistoryModels.get(z).getLongi());
                wayPoint = wayPoint + points.latitude + "," + points.longitude + "%7C";
            }

            String key = "key=AIzaSyAADDGaF-1C9eEbbPvc3YCx1lGwKb5t_Vc";
            String sensor = "sensor=false";
            String params = origin + "&" + destination + "&" + sensor + "&" + wayPoint + "&" + key;
            String output = "json";

            finalURL = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;

        }


        Log.e("finalURL", finalURL);

        return finalURL;
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

    public void drawPath(String result) {

        try {

            final JSONObject json = new JSONObject(result);
            String status = json.optString("status");
            if (status.equalsIgnoreCase("OK")) {
                JSONArray routeArray = json.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);

                Log.e("resultList", list.toString());

                mMap.clear();

                *//*Polyline line = mMap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(12)
                        .color(Color.parseColor("#286566"))//Google maps blue color
                        .geodesic(true)
                );*//*


                PolylineOptions options = new PolylineOptions()
                        .width(12)
                        .addAll(list)
                        .color(Color.parseColor("#286566"))
                        .geodesic(true);

                Polyline line = mMap.addPolyline(options);


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latlong : list) {
                    builder.include(latlong);
                }

                LatLngBounds bounds = builder.build();

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(sourceLatitude, sourceLongitude))
                        .title("Start Point")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                if (trackingHistoryModels.size() > 1) {

                    if (PrefData.readStringPref(PrefData.checkout_time).equalsIgnoreCase("On Work")) {

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(destLatitude, destLongitude))
                                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                                .title("Current Location"));

                    } else {

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(destLatitude, destLongitude))
                                .title("End Point")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    }
                }


                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            } else {
                Toast.makeText(TrackingHistoryActivity.this, "Unable to parse Directions Data", Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getDataFromTheFirestoreDB();
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
        if (!fetchRouteApi.isCancelled()) {
            fetchRouteApi.cancel(true);
        }

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        finish();
    }

    public class asyncTaskToFetchRoute extends AsyncTask<Void, Void, String> {
        String url;

        asyncTaskToFetchRoute(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (!isCancelled()) {
                progressView.showLoader();
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            String json = "";

            if (!isCancelled()) {
                JSONParser jParser = new JSONParser();
                json = jParser.getJSONFromUrl(url);
            } else {
                cancel(true);
            }


            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!isCancelled()) {
                progressView.hideLoader();
                if (result != null) {
                    drawPath(result);
                }
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }

*/



}
