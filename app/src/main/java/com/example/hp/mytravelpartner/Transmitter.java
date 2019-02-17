package com.example.hp.mytravelpartner;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;



public interface Transmitter {
    public void transmit(ArrayList<LatLng> points, List<String> place);
    public void transmitclear();
}
