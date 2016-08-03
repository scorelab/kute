package com.kute.app.Views;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.kute.app.Helpers.DBLatLon;
import com.kute.app.Views.LocationUpdateService;
import com.kute.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class IndividualShareLocationActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerDragListener
        , GoogleMap.OnMapLongClickListener, GoogleApiClient.OnConnectionFailedListener
        , View.OnClickListener {

    // Map object
    private GoogleMap mMap;
    // Current latitude and longitude;
    private double longitude, latitude;
    // This is the source coordinate
    private double fromLongitude, fromLatitude;
    // This is the destination coordinate
    private double toLongitude, toLatitude;
    // A client object to handle calls in the API
    private GoogleApiClient google;
    // From and To time
    private long timeFrom, timeTo;

    private Button buttonSetTo;
    private Button buttonSetFrom;
    private Button buttonCalcDistance;
    public DBLatLon db;
    public MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initiate the activity with the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_individual_share_location);

        IntentFilter filter = new IntentFilter("com.pycitup.BroadcastReceiver");
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, filter);

        startService(new Intent(this, LocationUpdateService.class));

        db = new DBLatLon(getApplicationContext());

        // Get selected vehicle from previous intent and set it as the vehicle name
        TextView vehicleName = (TextView) findViewById(R.id.vehicleID);
        Bundle data = getIntent().getExtras().getBundle("bundle");
        if (data != null) {
            String selectedVehicle = data.getString("Selected Vehicle");
            vehicleName.setText(selectedVehicle);
        }

        // Set map fragment to the activity and initiate it
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initiate Google API client
        google = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

        buttonSetTo = (Button) findViewById(R.id.btnTo);
        buttonSetFrom = (Button) findViewById(R.id.btnFrom);
        buttonCalcDistance = (Button) findViewById(R.id.btnCalc);

        buttonSetTo.setOnClickListener(this);
        buttonSetFrom.setOnClickListener(this);
        buttonCalcDistance.setOnClickListener(this);
    }

    /***********************************************************************************************
     * Calculate the current location of the device and set marker to that location
     **********************************************************************************************/
    private void getCurrentLocation() {

        // Clear map for any marker
        mMap.clear();
        // Get current location coordinates
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(google);

        if (myLocation != null) {
            // Set system latitudes and longitudes to current position
            longitude = myLocation.getLongitude();
            latitude = myLocation.getLatitude();
            // Adjust the map view to current position
            moveMap();
        }
    }

    /***********************************************************************************************
     * Move the camera to a given coordinate
     **********************************************************************************************/
    private void moveMap() {

        LatLng latLng = new LatLng(latitude, longitude);

        // Adding a marker and get the camera to that coordinate
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Current Location"));

        // Move camera to given coordinates
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    /***********************************************************************************************
     * Get directions between two points
     **********************************************************************************************/
    private void getDirection() {

        // Get a formatted URL to send
        String url = makeURL(fromLatitude, fromLongitude, toLatitude, toLongitude);

        // Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this,
                "Calculating Route",
                "Please wait...",
                false, false);

        // Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Remove the loading dialog
                        loading.dismiss();
                        // Try drawing the path according to the response
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        // Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /***********************************************************************************************
     * Draw a path between two points
     **********************************************************************************************/
    public void drawPath(String result) {

        // Getting both the coordinates
        LatLng from = new LatLng(fromLatitude, fromLongitude);
        LatLng to = new LatLng(toLatitude, toLongitude);

        // Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(from, to);
        // Displaying the distance
        // Toast.makeText(this, String.valueOf(distance + " meters"), Toast.LENGTH_SHORT).show();
        long timeTaken = (Math.abs(timeTo - timeFrom)) * 1000;
        Double speed = distance/timeTaken;
        Toast.makeText(getApplicationContext(), "Speed is " + speed + " and distance is "+ distance
                + " meters", Toast.LENGTH_LONG).show();

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolyLines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolyLines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(10)
                            .color(Color.BLUE)
                            .geodesic(true)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add a marker and move the camera
        LatLng current = new LatLng(7.206950, 79.841290);

        mMap.addMarker(new MarkerOptions()
                .position(current)
                .title("Bus")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);

        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(current).zoom(12).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();
        }
    }

    private void writeToFile(String data) throws IOException {

        FileOutputStream fileout= openFileOutput("mytextfile.txt", MODE_PRIVATE);
        OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
        outputWriter.write(data);
        outputWriter.close();

        //display file saved message
        Toast.makeText(getBaseContext(), "File saved successfully!",
                Toast.LENGTH_SHORT).show();
        /*try {
            File path = getApplicationContext().getFilesDir();
            File file = new File(path, "Logger.txt");
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(data.getBytes());
            } finally {
                stream.close();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/
    }

    @Override
    public void onClick(View v) {

        if (v == buttonSetFrom) {
            timeFrom = System.currentTimeMillis();
            fromLatitude = latitude;
            fromLongitude = longitude;
            Toast.makeText(this, "From set to " + fromLatitude + " " + fromLongitude,
                    Toast.LENGTH_SHORT).show();
        }

        if (v == buttonSetTo) {
            timeTo = System.currentTimeMillis();
            toLatitude = latitude;
            toLongitude = longitude;
            Toast.makeText(this, "To set to " + toLatitude + " " + toLongitude,
                    Toast.LENGTH_SHORT).show();
        }

        if (v == buttonCalcDistance) {
            getDirection();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Clearing all the markers
        mMap.clear();
        // Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));

        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    /***********************************************************************************************
     * Generate a String containing the request to be sent to the server to get directions
     **********************************************************************************************/
    public String makeURL(double sourceLat, double sourceLog, double destLat, double destLog) {

        String urlString = ("https://maps.googleapis.com/maps/api/directions/json?origin=");
        urlString += (Double.toString(sourceLat) + "," + Double.toString(sourceLog));
        urlString += ("&destination=");
        urlString += (Double.toString(destLat) + "," + Double.toString(destLog));
        urlString += ("&sensor=false&mode=driving&alternatives=true");
        urlString += ("&key=AIzaSyBO4xpfck8_W7WkAJ1LtoK4JQg_VAga9Xc");

        return urlString;
    }

    /***********************************************************************************************
     * Creating a polyline to draw the path on the map
     **********************************************************************************************/
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> polyLine = new ArrayList<>();
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

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            polyLine.add(p);
        }

        return polyLine;
    }

    /***********************************************************************************************
     * Connect and disconnect from GoogleClientApi
     **********************************************************************************************/
    @Override
    protected void onStart() {

        google.connect();
        super.onStart();
        IntentFilter filter = new IntentFilter("com.pycitup.BroadcastReceiver");
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, filter);

        Action action = Action.newAction(
                Action.TYPE_VIEW,
                "Individual Map", Uri.parse("http://host/path"),
                Uri.parse("android-app://com.kute.app/http/host/path")
        );
        AppIndex.AppIndexApi.start(google, action);
    }

    @Override
    protected void onStop() {
        google.disconnect();
        super.onStop();

        Action action = Action.newAction(
                Action.TYPE_VIEW,
                "Individual Map", Uri.parse("http://host/path"),
                Uri.parse("android-app://com.kute.app/http/host/path")
        );
        AppIndex.AppIndexApi.end(google, action);
    }

    /***********************************************************************************************
     * Broadcast receiver to catch new latitudes and longitudes and update map
     **********************************************************************************************/
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!DBLatLon.isRecord) {
                return;
            }
            double lat = intent.getDoubleExtra("Lat", 0);
            double lon = intent.getDoubleExtra("Lon", 0);
            if (mMap != null) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
                Toast.makeText(getApplicationContext(), lat + " " + lon, Toast.LENGTH_LONG).show();
                Log.d("Dilu", lat + " " + lon);
            }
        }
    }

    /***********************************************************************************************
     * Options menu items and stuff
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sign_out:
                Intent goBack = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(goBack);
                finish();
                return true;

            case R.id.track:
                db.addRecord(0, 0);
                DBLatLon.isRecord = !DBLatLon.isRecord;
                if (DBLatLon.isRecord) {
                    item.setTitle("Stop Recording");
                } else {
                    item.setTitle("Start Recording");
                }
                return true;

            case R.id.clear:
                mMap.clear();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {/**/}

    @Override
    public void onMarkerDragStart(Marker marker) {/**/}

    @Override
    public void onMarkerDrag(Marker marker) {/**/}

    @Override
    public void onConnectionSuspended(int i) {/**/}
}