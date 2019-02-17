package com.example.hp.mytravelpartner;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {

    private ArrayList<Integer> waypoints = new ArrayList<Integer>();

    public ArrayList<Integer> getWaypoints() {
        return waypoints;
    }

    private double totalDistance = 0.0;
    private double totalTime = 0.0;

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes;
        routes = new ArrayList<List<HashMap<String,String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONArray jWaypoints = null;

        int tempDistance = 0;
        int tempDuration = 0;

        try {

            jRoutes = jObject.getJSONArray("routes");

            jWaypoints = jRoutes.getJSONObject(0).getJSONArray("waypoint_order");
            int numberofwaypoints = jWaypoints.length();
            for(int i = 0; i < numberofwaypoints; i++) {
                waypoints.add(jWaypoints.getInt(i));
            }

            Log.i("Gadam", waypoints.toString());

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                List path = new ArrayList<HashMap<String, String>>();

                Log.i("Gadam", "Value of Legs: " + String.valueOf(jLegs.length()));

                /** Traversing all legs */
                for(int j=0;j<jLegs.length() - 1;j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    tempDistance += (Integer)((JSONObject)jLegs.get(j)).getJSONObject("distance").get("value");
                    tempDuration += (Integer)((JSONObject)jLegs.get(j)).getJSONObject("duration").get("value");

                    Log.i("Gadam", "Distance: " + String.valueOf(tempDistance));
                    Log.i("Gadam", "Duration: " + String.valueOf(tempDuration));

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l <list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);

                }

                totalDistance = convertDistanceKM(tempDistance);
                totalTime = convertTimeMinutes(tempDuration);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
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

    private double convertTimeMinutes(int time){
        return time/60;
    }

    private double convertDistanceKM(int distance){
        return distance/1000;
    }

    public double TTime(){
        return totalTime;
    }

    public double DDistance(){
        return totalDistance;
    }
}
