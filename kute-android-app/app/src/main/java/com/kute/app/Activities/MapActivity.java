package com.kute.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kute.app.Bussiness.Train;
import com.kute.app.Helpers.BottomSheetVehicleAdapter;
import com.kute.app.R;
import com.kute.app.Views.BottomSheetVehicleFragment;
import com.kute.app.Views.StationUtils;

import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static MapActivity reference;
    private GoogleMap mMap;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView mVehicleNameView;
    private ImageView mVehicleIconView;
    private SupportMapFragment mapFragment;
    private Fragment[] vehicleFragments = new Fragment[3];
    private StationUtils stationUtils;
    private FloatingActionButton mFab;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reference = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Firebase.setAndroidContext(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);


        stationUtils = new StationUtils(this);
        initialiseUI();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(stationUtils)
                .addOnConnectionFailedListener(stationUtils)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        setTrains();

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


    public void setSelectedVehicle(BottomSheetVehicleAdapter.VehicleViewHolder holder) {
        mVehicleIconView.setImageDrawable(getVehicleDrawable(holder.vehicleType, this));
        mVehicleNameView.setText(holder.vehicle.getTrainname());
        mFab.show();
        showTrain();
    }

    public static String getVehicleName(int vehicleType, Context context) {
        switch(vehicleType) {
            case 0:
                return context.getString(R.string.car);
            case 1:
                return context.getString(R.string.bus);
            case 2:
                return context.getString(R.string.train);
            default:
                return null;
        }
    }

    public static Drawable getVehicleDrawable(int vehicleType, Context context) {
        Drawable drawable;
        switch(vehicleType) {
            case 0:
                if (Build.VERSION.SDK_INT >= 21)
                    drawable = context.getResources().getDrawable(R.drawable.icon_car, context.getTheme());
                else
                    drawable = context.getResources().getDrawable(R.drawable.icon_car);
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= 21)
                    drawable = context.getResources().getDrawable(R.drawable.icon_bus, context.getTheme());
                else
                    drawable = context.getResources().getDrawable(R.drawable.icon_bus);
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= 21)
                    drawable = context.getResources().getDrawable(R.drawable.icon_train_vector, context.getTheme());
                else
                    drawable = context.getResources().getDrawable(R.drawable.icon_train_vector);
                break;
            default:
                drawable = null;
        }
        return drawable;
    }

    public void setBottomSheetScrollingEnabled(boolean enabled) {
        NestedScrollView bottomSheet = ((NestedScrollView) findViewById(R.id.bottom_sheet));
        if (bottomSheet.isNestedScrollingEnabled() != enabled)
            bottomSheet.setNestedScrollingEnabled(enabled);
    }


    public void setTrains(){
        Firebase ref = new Firebase("https://kute-37f82.firebaseio.com/android/TrainsNo/TrainData");
        Firebase alanRef = ref.child("SrilankanTrains").child("1");
        alanRef.child("fullName").setValue("UdarataManike");
        alanRef.child("Start").setValue("Colombo");
        alanRef.child("StartTime").setValue("5.00");
        alanRef.child("End").setValue("Badulla");
        alanRef.child("EndTime").setValue("7.00");

        alanRef = ref.child("SrilankanTrains").child("2");
        alanRef.child("fullName").setValue("Galukumari");
        alanRef.child("Start").setValue("Colombo");
        alanRef.child("StartTime").setValue("6.00");
        alanRef.child("End").setValue("Galle");
        alanRef.child("EndTime").setValue("8.00");

        alanRef = ref.child("SrilankanTrains").child("3");
        alanRef.child("fullName").setValue("Sudu");
        alanRef.child("Start").setValue("Colombo");
        alanRef.child("StartTime").setValue("6.00");
        alanRef.child("End").setValue("Mathara");
        alanRef.child("EndTime").setValue("8.00");


        alanRef.push();

    }

    public void getVehicles(final int vehicleType) {
        if (vehicleType == 2) {
            Firebase ref = new Firebase("https://kute-37f82.firebaseio.com/android/TrainsNo/TrainData/SrilankanTrains");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    BottomSheetVehicleFragment vehicleFragment = (BottomSheetVehicleFragment) vehicleFragments[vehicleType];
                    BottomSheetVehicleAdapter adapter = vehicleFragment.getAdapter();
                    List<Train> trains = vehicleFragment.getVehicleList();

                    trains.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Train train = postSnapshot.getValue(Train.class);
                        trains.add(train);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        stationUtils.setMap(mMap);

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
        mMap.setOnMarkerDragListener(stationUtils);
        mMap.setOnMapLongClickListener(stationUtils);
    }

    public void showTrain() {
        LatLng current = new LatLng(7.206950, 79.841290);

        mMap.addMarker(new MarkerOptions()
                .position(current)
                .title("Bus")
                .icon(getMarkerIconFromDrawable(getVehicleDrawable(2, this))));//index 2 stands for train
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(7.206950, 79.841290)).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initialiseUI() {
        Resources r = getResources();
        float dp360 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, r.getDisplayMetrics());
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;

        //set bottom sheet expanded height to 1/3 of screenHeight, or 360dp, whichever is larger
        int anchorHeight = Math.max((int) dp360, screenHeight/3);
        anchorHeight = Math.min(anchorHeight, screenHeight);
        findViewById(R.id.bottom_sheet).getLayoutParams().height = anchorHeight;

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        final View peekingViewHeight = findViewById(R.id.bottom_sheet_peek_height);
        final View peekingViewClickTarget = findViewById(R.id.bottom_sheet_peek_area);
        peekingViewHeight.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                bottomSheetBehavior.setPeekHeight(peekingViewHeight.getHeight());
                peekingViewHeight.removeOnLayoutChangeListener(this);
            }
        });
        peekingViewClickTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = bottomSheetBehavior.getState();
                bottomSheetBehavior.setState(state == BottomSheetBehavior.STATE_EXPANDED ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.bottom_sheet_tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.bottom_sheet_viewpager);

        for (int i = 0; i < vehicleFragments.length; i++) {
            final Fragment fragment = new BottomSheetVehicleFragment();
            Bundle args = new Bundle();
            args.putInt("vehicleType", i);
            fragment.setArguments(args);
            vehicleFragments[i] = fragment;
        }
        final FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return vehicleFragments[position];
            }

            @Override
            public int getCount() {
                return vehicleFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getVehicleName(position, MapActivity.this);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                BottomSheetVehicleFragment.activeVehicleType = tab.getPosition();
                setBottomSheetScrollingEnabled(((BottomSheetVehicleFragment) pagerAdapter.getItem(BottomSheetVehicleFragment.activeVehicleType)).isRecyclerViewAtTop());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                BottomSheetVehicleFragment.activeVehicleType = tab.getPosition();
                setBottomSheetScrollingEnabled(((BottomSheetVehicleFragment) pagerAdapter.getItem(BottomSheetVehicleFragment.activeVehicleType)).isRecyclerViewAtTop());
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    setBottomSheetScrollingEnabled(true);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    setBottomSheetScrollingEnabled(((BottomSheetVehicleFragment) pagerAdapter.getItem(BottomSheetVehicleFragment.activeVehicleType)).isRecyclerViewAtTop());
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mVehicleNameView = (TextView) findViewById(R.id.bottom_sheet_selected_name);
        mVehicleIconView = (ImageView) findViewById(R.id.bottom_sheet_selected_icon);

        mFab = (FloatingActionButton) findViewById(R.id.bottom_sheet_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null)
                    stationUtils.getStations();
            }
        });
        mFab.hide();
    }

    public void collapseBottomSheet() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }


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
                Intent goBack = new Intent(getApplicationContext(),
                        SplashActivity.class);
                startActivity(goBack);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
