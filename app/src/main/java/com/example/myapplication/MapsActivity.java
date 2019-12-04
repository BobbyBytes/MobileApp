package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
// implementation 'com.google.android.gms:play-services-location:17.0.0'


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    //used to store longitude and latitude to construct addresses.

    //Class Vars
    private GoogleMap mMap;
    private Boolean mLOcationPermissionGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "MAPSACTIVITY";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 12.5f;
    public static String temp;
    public static String name = "";
    public Location loc;
    public String temp1 = "";
    private LatLng latLng;
    private boolean isArtist;
    private String dataBaseCollectionPath;
     private List<Marker> markers;
    //Firebase connection

    //Create connection to DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();
        markers = new ArrayList<>();
        Intent caller = getIntent();
        isArtist = caller.getBooleanExtra("idIsArtist", false);
        if (isArtist) {
            dataBaseCollectionPath = "venues";
        } else {
            dataBaseCollectionPath = "users";
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    //Used this tutorial for getDeviceLocation() and moveCamera() https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=5
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLOcationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location curentLocation = (Location) task.getResult();
                            //moveCamera(new LatLng(curentLocation.getLatitude(), curentLocation.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComp  lete: current location is null");
                            Toast.makeText(MapsActivity.this, "unbal to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    //
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }



    //Source used to get location permission from users. https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    private void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLOcationPermissionGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
//Source used to get location permission from users. https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLOcationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
                            mLOcationPermissionGranted = false;
                        return;
                    }
                }
                mLOcationPermissionGranted = true;
            }
        }
    }


    public String get_addr_String_wrapper(){
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLOcationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location curentLocation = (Location) task.getResult();
                            temp = getCompleteAddressString(curentLocation.getLatitude(), curentLocation.getLongitude());

                        } else {
                            Log.d(TAG, "onComp  lete: current location is null");
                            Toast.makeText(MapsActivity.this, "unabale to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
        return temp;

    }


    //https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
    //Used source above to understand the geocoder
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            //A list of locations is returned from the geocoder.
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                String city = addresses.get(0).getLocality();
                strReturnedAddress.append(city).append("\n");
                String state = addresses.get(0).getAdminArea();
                strReturnedAddress.append(state).append("\n");

                //Used to get full address.
                /*
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                */

                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Hard coded venue locations. Will try to get locations from Firebase time permitting.
            mMap = googleMap;

            //Ran out of time trying to get location object from database with coordinates. Was able to get name of location.



        db.collection(dataBaseCollectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserData ud = document.toObject(UserData.class);
                                name = ud.getDisplayName();
                                Marker mkr;
                                double longitude = ud.getLongitude();
                                double latitude = ud.getLatitude();
                                // Location location = ud.getLocation();
                                latLng = new LatLng(latitude, longitude);

                                    if(longitude != 0.0 && latitude != 0.0) {
                                        mkr = mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                        markers.add(mkr);
                                    }


                            }
                            setCameraBounds();
                        } else {

                            Log.d("TAG", "Error getting map documents: ", task.getException());
                        }
                    }
                });


        if(mLOcationPermissionGranted){
            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }

        //These should eventually be initialized with Location object from the database
//        LatLng Olympia_s = new LatLng(42.646445, -71.316650);
//        LatLng Hearing_r = new LatLng(42.634200, -71.317904);
//        LatLng Tsongas_c = new LatLng(42.6502, -71.3132);
//        LatLng Nectars_v = new LatLng(44.478561, -73.212733);
//
//        mMap.addMarker(new MarkerOptions().position(Olympia_s).title("Olympia's Zorba Music Hall"));
//        mMap.addMarker(new MarkerOptions().position(Hearing_r).title("The Hearing Room"));
//        mMap.addMarker(new MarkerOptions().position(Tsongas_c).title(name));



        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
    }

    private void setCameraBounds(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (markers.isEmpty()) return;
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int padding = 10; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);

    }


}
