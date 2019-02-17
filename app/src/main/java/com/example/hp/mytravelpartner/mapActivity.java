package com.example.hp.mytravelpartner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.mytravelpartner.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class mapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    //drawer variables
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;


    double Lat;
    double Long;

    private int RbuttonId;
    Fragment Directions = new Fragment();

    public void transmit(ArrayList<LatLng> points, List<String> place) {
        if (points != null && place != null) {
            received(points, place);
        }
    }


    public void transmitclear(ArrayList<LatLng> points, List<String> place) {
        recievedclear(points,place);
    }

    protected void received(ArrayList<LatLng> points, List<String> place) {
        listpoints = points;
        placeName = place;

        if (listpoints.size() > 1) {
            String url = getRequestURL(listpoints);
            //textView.setText("Show Button Successful - " + listpoints.get(0).toString() + " " + listpoints.get(1).toString());

            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
            Fragment fr= new Directions();
            ((Directions) fr).DirStep(url);
        }

        else{
            Log.d(TAG, "received: no listpoints! ");
        }


    }

    protected void recievedclear(ArrayList<LatLng> points, List<String> place) {
        listpoints = points;
        placeName = place;
        listpoints.clear();
        placeName.clear();

        mMap.clear();

        TextTime.setText("");
        TextDistance.setText("");
    }


    @SuppressLint("MissingPermission")
    protected String getRequestURL(/*LatLng src, LatLng dest,*/ ArrayList<LatLng> points) {
        int size = points.size();

        // String temp = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
        String temp = Lat + "," + Long;


        String str_src = "origin=" + temp;//points.get(0).latitude + "," + points.get(0).longitude;
        String str_des = "destination=" + temp;//points.get(0).latitude + "," + points.get(0).longitude;

        String waypoints = "waypoints=optimize:true";

        String sensor = "sensor=true";
        String alternatives = "alternatives=true";
        String mode=null;
        String param = str_src + "&" + str_des + "&" + sensor;

        if (size > 1) {
            for (int i = 0; i < size; i++) {
                waypoints += "|" + points.get(i).latitude + "," + points.get(i).longitude ;
            }
            param += "&" + waypoints;
        }

        String output = "json";
        //String url= "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        String url;
        Fragment fr= new RouteFrag();
        mode= ((RouteFrag) fr).transfer();

        if(mode=="driving")
        {
            url= "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&departure_time=now" + "&mode=driving&key=AIzaSyBY-dckgyTPC6xPRp3ftqUilTPMpXZZ-d0";
        }
        else if(mode=="walking"){
            Log.d(TAG, "getRequestURL: mode is walking");
            url= "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&mode=walking&key=AIzaSyBY-dckgyTPC6xPRp3ftqUilTPMpXZZ-d0";
        }
        else if(mode=="bicycling"){
            url= "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&mode=BICYCLING&key=AIzaSyBY-dckgyTPC6xPRp3ftqUilTPMpXZZ-d0";
        }
        else{
            url= "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&mode=TRANSIT&key=AIzaSyBY-dckgyTPC6xPRp3ftqUilTPMpXZZ-d0";
        }
        return url;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";

            try {
                responseString = reqDirections(strings[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Parse Json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    private String reqDirections(String reqURL) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        //File file = new File("myText.txt");

        try {
            URL url = new URL(reqURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            //BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("myText.txt")));

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";

            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
                Log.i(TAG, line);
            }

            responseString = stringBuffer.toString();
            /*bwr.write(responseString);
            bwr.flush();
            bwr.close();*/
            //textView.setText("reDirection");
            br.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }


    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                routes = directionsJSONParser.parse(jsonObject);
                waypoints = directionsJSONParser.getWaypoints();
                totalDistance = directionsJSONParser.DDistance();
                totalTime = directionsJSONParser.TTime();
            } catch (JSONException e) {
                e.printStackTrace();
            }

           // Fragment fr= new Directions();

            //String instruction = null;

            //try {
             //   instruction = jsonObject.getString("html_instructions");
           // } catch (JSONException e) {
            //    e.printStackTrace();
           // }
           // ((Directions) fr).obj(instruction);
            return routes;
        }


        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            Log.i(TAG, "IN ONPOST METHOD");

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points)
                        .width(20)
                        .color(Color.BLUE)
                        .geodesic(true);
            }

            IconGenerator iconFactory = new IconGenerator(getApplicationContext());
            iconFactory.setStyle(IconGenerator.STYLE_GREEN);

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);

               // mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("My Location"))));
                mMap.addMarker(new MarkerOptions().position(new LatLng(Lat,Long)).icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("My Location"))));
                for (int i = 0; i < waypoints.size(); i++) {

                    mMap.addMarker(new MarkerOptions().position(new LatLng(listpoints.get(waypoints.get(i)).latitude, listpoints.get(waypoints.get(i)).longitude)).icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(i + 1)))));
                }

                TextDistance.setText(String.valueOf(totalDistance) + " km");
                TextTime.setText(String.valueOf(totalTime) + " min");



            } else {
                //Toast.makeText(, "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }





    //Google Map Attachment

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setPadding(0,900,0,0);
            init();
        }
    }

    private static final String TAG = "mapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //var
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;

    private ArrayList<LatLng> listpoints;

    private List<String> placeName;
    private ArrayList<Integer> waypoints;
    private Location currentLocation;
    private TextView TextDistance;
    private TextView TextTime;


    //location req var
    private double totalDistance = 0.0;
    private double totalTime = 0.0;

    private static final long UPDATE_INTERVAL = 1000;
    private static final long FAST_INTERVAL = UPDATE_INTERVAL / 2;

    private boolean mTrackingLocation = false;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    //widgets
    private AutoCompleteTextView mSearchText;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymap);
        mSearchText = findViewById(R.id.input_search);
        getLocationPermission();


        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Find our drawer view

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Set a Toolbar to replace the ActionBar.

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer =(DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();


        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();

        TextDistance = (TextView) findViewById(R.id.distance);
        TextTime = (TextView) findViewById(R.id.Dtime);


        mTrackingLocation = false;
        mSettingsClient = LocationServices.getSettingsClient(this);
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("Latitude", Lat);
        outState.putDouble("Longitude", Long);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Lat= savedInstanceState.getDouble("Latitude");
        Long = savedInstanceState.getDouble("Longitude");
    }

    @Override

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override

                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);

                        return true;

                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {

        // Create a new fragment and specify the fragment to show based on nav item clicked

      //  android.support.v4.app.Fragment fragment= null;
        //Class fragmentClass;

        //Fragment frag=null;
        switch (menuItem.getItemId()) {

            case R.id.nav_addRoute:

                //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new RouteFrag()).commit();
                Fragment frag= new RouteFrag();
                FragmentManager fm= getSupportFragmentManager();
                FragmentTransaction ft= fm.beginTransaction();
                ft.add(R.id.fragmentContainer, frag, "RF" );
                ft.addToBackStack("screen");
                ft.commit();

                break;

            case R.id.nav_savedRoute:

                //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new RouteFrag()).commit();
                frag= new Directions();
                fm= getSupportFragmentManager();
                ft= fm.beginTransaction();
                ft.add(R.id.fragmentContainer, frag, "RF" );
                //ft.addToBackStack("screen");
                ft.commit();

                break;

            default:
        }

        try {
           // fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Insert the fragment by replacing any existing fragment
       //--- android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        //---fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Set action bar title
        setTitle(menuItem.getTitle());

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {

        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it

        // and will not render the hamburger icon without it.

        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        Log.d(TAG, "init: Initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mPlaceAutoCompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate() {

        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(mapActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting your current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null)  {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                          moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "Starting Point");
                         Lat= currentLocation.getLatitude();
                         Long= currentLocation.getLongitude();
                        }

                        else {
                            LocationRequest mLocationRequest = LocationRequest.create();
                                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    mLocationRequest.setInterval(10 * 1000);     // 10 seconds, in milliseconds
                                    mLocationRequest.setFastestInterval(1000); // 1 second, in milliseconds
                            Log.d(TAG, "onComplete: current location is null");
                            LocationServices.getFusedLocationProviderClient(mapActivity.this).requestLocationUpdates(mLocationRequest,null);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()

                .position(latLng)
                .title(title);
        mMap.addMarker(options);
        hideSoftKeyboard();
    }


    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(mapActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission:Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }



    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    ----------------Google places API autocomplete suggestion---------------------
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {

        @Override

        //Submits request for the place
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();
            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    //will get the place if request is received successfully
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {

                mPlace = new PlaceInfo();

                mPlace.setName(place.getName().toString());

                mPlace.setAddress(place.getAddress().toString());

                mPlace.setId(place.getId());

                mPlace.setLatlng(place.getLatLng());

                mPlace.setRating(place.getRating());

                mPlace.setPhoneNumber(place.getPhoneNumber().toString());

                mPlace.setWebsiteUri(place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());

            } catch (NullPointerException e) {

                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());

            }


            moveCamera(new LatLng(Objects.requireNonNull(place.getViewport()).getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());
            places.release();
        }
    };}

