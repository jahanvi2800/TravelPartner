package com.example.hp.mytravelpartner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class RouteFrag extends Fragment {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712),
            new LatLng(28.20453, 97.34466));

    View v;
    private static final String TAG = "MyTravelPartner";

    protected GeoDataClient mGeoDataClient;

    private PlaceAutoCompleteAdapter2 mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private ArrayList<LatLng> listpoints;

    private RecyclerView recyclerView;
    private List<String> placeName;

    private Button clearRoute;
    private Button addPlace;
    private Button showRoute;


    private RadioGroup rg;
    public int id;

    public static String mode1;
    private LatLng mCurrentPlaceRelease;
    private String mCurrentPlaceReleaseName;

    //Transmitter t;
    mapActivity ma= new mapActivity();

    public RouteFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_route, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycle_id);
        final RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(getContext(), placeName);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recycleViewAdapter);

        initAutocomplete(v);

        addPlace = (Button) v.findViewById(R.id.addPlace);
        clearRoute = (Button) v.findViewById(R.id.clear_id);
        showRoute = (Button) v.findViewById(R.id.show_route);
        rg= (RadioGroup) v.findViewById(R.id.rgroup);
        rg.clearCheck();



        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    Toast.makeText(getContext(), rb.getText(), Toast.LENGTH_SHORT).show();

                    switch (checkedId) {
                        case R.id.radioButton6:
                            mode1 = "transit";
                            break;

                        case R.id.radioButton7:
                            mode1 = "driving";
                            break;

                        case R.id.radioButton8:
                            mode1 = "walking";
                            break;

                        case R.id.radioButton9:
                            mode1 = "bicycling";
                            break;
                    }
                }
            }
        });


        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeName.add(mCurrentPlaceReleaseName);
                listpoints.add(mCurrentPlaceRelease);
                recycleViewAdapter.notifyItemInserted(placeName.size() - 1);
                mAutocompleteView.getText().clear();
            }
        });

        clearRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeName!=null) {
                    int i = placeName.size();
                    placeName.clear();
                    listpoints.clear();
                    recycleViewAdapter.notifyItemRangeRemoved(0, i);
                    mAutocompleteView.getText().clear();
                    //ma.transmitclear(listpoints, placeName);

                    //t.transmitclear();
                }
                else{
                    Toast.makeText(getContext(),"No routes to delete.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        showRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapActivity ma= (mapActivity) getActivity();

                //startActivityForResult(intent,1);
                ma.transmit(listpoints, placeName);
                getActivity().onBackPressed();

            }
        });


        return v;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //t= {Transmitter) getActivity();
        ma = (mapActivity) getActivity();
        ma.transmitclear(listpoints,placeName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listpoints = new ArrayList<>();
        placeName = new ArrayList<>();
    }

    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void initAutocomplete(View v) {
        mGeoDataClient = Places.getGeoDataClient(this.getActivity());

        mAutocompleteView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView2);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutoCompleteAdapter2(this.getActivity(), mGeoDataClient, BOUNDS_INDIA, null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    public String transfer()
    {
        Log.d(TAG, "transfer: mode is"+ mode1);
        return mode1;
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            //Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            =  new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);

                //LatLng latLng = place.getLatLng();
                //mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                //listpoints.add(latLng);
                mCurrentPlaceRelease = place.getLatLng();
                mCurrentPlaceReleaseName = (String) place.getName();

                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
            }
        }
    };


}
