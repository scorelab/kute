package com.kute.app.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kute.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IndividualShareLocationActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private List<Station> stationList;

    private double longitude;
    private double latitude;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_individual_share_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

        Button searchButton = (Button) findViewById(R.id.searchStations);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStations(latitude, longitude);
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cancelled = new Intent(getApplicationContext(), ShareLocationActivity.class);
                startActivity(cancelled);
                finish();
            }
        });
        stationList = new ArrayList<>();
    }

    private String stationGetURL(double lat, double lon) {

        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");
        urlString.append("&location=");
        urlString.append(Double.toString(lat));
        urlString.append(",");
        urlString.append(Double.toString(lon));
        urlString.append("&radius=5000");
        urlString.append("&types=train_station");
        urlString.append("&sensor=false&key=AIzaSyC-WmhurKxFMzwB4nFVh_VTSy1A7ZQTyaM");
        return urlString.toString();
    }

    public String pathGetURL(double sourcelat, double sourcelog, double destlat, double destlog) {

        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        urlString.append("&key=AIzaSyDHVUR0qSd8EPcMssK6ccl3KH4gB4xp4K0");
        return urlString.toString();
    }

    private void getStations(double lat, double lon) {
        //Getting the URL
        String url = stationGetURL(lat, lon);
        //Showing a dialog till we get the list
        final ProgressDialog loading = ProgressDialog.show(this, null, "Searching ...", false, false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            stationList.clear();
                            mMap.clear();
                            JSONObject responseFromMaps = new JSONObject(response);
                            JSONArray listOfStations = new JSONArray(responseFromMaps.getString("results"));

                            for (int i = 0; i < listOfStations.length(); i++) {

                                JSONObject station = new JSONObject(listOfStations.get(i).toString());

                                JSONObject geometry = new JSONObject(station.get("geometry").toString());
                                JSONObject location = new JSONObject(geometry.get("location").toString());

                                Station stationItem = new Station();

                                stationItem.setStationName(station.getString("name"));
                                stationItem.setLatitude(Double.valueOf(location.getString("lat")));
                                stationItem.setLongitude(Double.valueOf(location.getString("lng")));

                                stationList.add(stationItem);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addMarkersToStations(stationList);
                        try {
                            Station nearestStation = stationList.get(0);
                            showDirection(nearestStation.getLatitude(), nearestStation.getLongitude());

                        } catch (IndexOutOfBoundsException | NullPointerException e) {
                            Toast.makeText(getBaseContext(), "No stations found", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void addMarkersToStations(List<Station> list) {

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_current_location)));

        for (int i = 0; i < list.size(); i++) {
            // Create a LatLan object
            LatLng latLng = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
            // Add a marker
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(false)
                    .title(list.get(i).getStationName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_train)));
        }
    }

    private void showDirection(double lat, double lon) {

        // Create the URL
        String url = pathGetURL(latitude, longitude, lat, lon);
        // Showing a dialog till we get the list
        final ProgressDialog loading = ProgressDialog.show(this, null, "Drawing path ...", false, false);
        // Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
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

    public void drawPath(String response) {

        try {
            final JSONObject json = new JSONObject(response);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolyLines = routes.getJSONObject("overview_polyline");
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
            Toast.makeText(getBaseContext(), "Station is " + distance.getString("text") + " away", Toast.LENGTH_LONG).show();
            String encodedString = overviewPolyLines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(10)
                            .color(Color.RED)
                            .geodesic(true)
            );
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
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
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Maps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.kute.app/http/host/path")
        );
        AppIndex.AppIndexApi.start(googleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Maps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.kute.app/http/host/path")
        );
        AppIndex.AppIndexApi.end(googleApiClient, viewAction);
    }

    private void getCurrentLocation() {
        mMap.clear();
        // Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            // Moving the map to location
            moveMap();
        }
    }

    private void moveMap() {
        // Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);
        // Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_current_location)));
        // Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Create a map
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(6.5, 78.5);
        // Create a default location
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        // Add listeners
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {/**/}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {/**/}

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Clear all the markers
        mMap.clear();
        // Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_current_location)));
        // Set location to selected location
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {/**/}

    @Override
    public void onMarkerDrag(Marker marker) {/**/}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // Get the new coordinate
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        // Center the map
        moveMap();
    }

}