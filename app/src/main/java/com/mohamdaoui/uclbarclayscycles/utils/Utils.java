package com.mohamdaoui.uclbarclayscycles.utils;

import android.content.Context;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mohamdaoui.uclbarclayscycles.models.Station;
import com.mohamdaoui.uclbarclayscycles.parsers.XMLParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.mohamdaoui.uclbarclayscycles.activities.MainActivity.columns;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.API_KEY;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.DESCRIPTION;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.GEOMETRY;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.HTTP_ERROR;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.INVALID_URL;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.LAT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.LNG;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.LOCATION;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.LOG_TAG;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.LONDON;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.MAP_API_BASE;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.OUT_JSON;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.PLACES_API_BASE;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.PLACES_API_DETAIL;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.PREDICTIONS;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.PROVIDER_URL;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.REFERENCE;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.RESULT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.RESULTS;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_BY_REF;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.TIMEOUT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.TYPE_AUTOCOMPLETE;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.UNKNOWN_ERROR;

/**
 * Created by mohamdao on 07/01/2017.
 */

public class Utils {

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static ArrayList<HashMap<String, String>> autocomplete(String input) {
        ArrayList<HashMap<String, String>> resultList = null;
        String results;
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&location=" + LONDON);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            results = post(sb.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(results);
            JSONArray predsJsonArray = jsonObj.getJSONArray(PREDICTIONS);

            // Extract the Place descriptions from the results
            resultList = new ArrayList<HashMap<String, String>>(
                    predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(DESCRIPTION, predsJsonArray.getJSONObject(i)
                        .getString(DESCRIPTION));
                map.put(REFERENCE, predsJsonArray.getJSONObject(i)
                        .getString(REFERENCE));
                resultList.add(map);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public static String post(String url) {
        String result;
        HttpsURLConnection connection = null;
        try {
            // Create connection
            connection = (HttpsURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);

            // Get Response
            int code = connection.getResponseCode();
            if(code != HttpsURLConnection.HTTP_OK)
                result = HTTP_ERROR;
            else {
                InputStream in = connection.getInputStream();
                if (in != null)
                    result = convertInputStreamToString(in);
                else
                    result = UNKNOWN_ERROR;

                in.close();
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing URL", e);
            result = INVALID_URL;
        } catch (SocketTimeoutException e) {
            Log.e(LOG_TAG, "Timeout", e);
            result = TIMEOUT;
        } catch (Throwable e){
            Log.e(LOG_TAG, "Error", e);
            result = HTTP_ERROR;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static MatrixCursor convertToCursor(ArrayList<HashMap<String, String>> hashMaps) {
        MatrixCursor cursor = new MatrixCursor(columns);
        int i = 0;
        for (HashMap<String, String> searchResult : hashMaps) {
            String[] temp = new String[3];
            i = i + 1;
            temp[0] = Integer.toString(i);
            temp[1] = searchResult.get(DESCRIPTION);
            temp[2] = searchResult.get(REFERENCE);
            cursor.addRow(temp);
        }
        return cursor;
    }

    public static LatLng getLocation(String searchCriteria, String userInput) {

        LatLng position = new LatLng(0, 0);
        HttpURLConnection conn = null;
        String jsonResults;
        try {
            StringBuilder sb;
            if (searchCriteria.equals(SEARCH_BY_REF)) {
                sb = new StringBuilder(PLACES_API_DETAIL + OUT_JSON);
                sb.append("?key=" + API_KEY);
                sb.append("&reference=" + userInput);
            } else {
                userInput = userInput.replace(" ", "");
                sb = new StringBuilder(MAP_API_BASE + userInput);
            }
            jsonResults = post(sb.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults);
            JSONObject jsonresult = null;
            if (searchCriteria.equals(SEARCH_BY_REF))
                jsonresult = jsonObj.getJSONObject(RESULT);
            else {
                JSONArray jsonresults = jsonObj.getJSONArray(RESULTS);
                if(jsonresults != null && jsonresults.length() > 0)
                jsonresult = jsonresults.getJSONObject(0);
            }
            if(jsonresult != null){
                JSONObject jsonGeo = jsonresult.getJSONObject(GEOMETRY);
                JSONObject jsonloc = jsonGeo.getJSONObject(LOCATION);
                position = new LatLng(jsonloc.getDouble(LAT),
                        jsonloc.getDouble(LNG));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return position;
    }

    public static ArrayList<Station> GetXmlAndParseStations(LatLng targetLocation){
        ArrayList<Station> stationsList = new ArrayList<Station>();
        XMLParser parser = new XMLParser();
        String xml = post(PROVIDER_URL);
        Document doc = parser.getDomElement(xml);
        Node n = doc.getFirstChild();
        NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                String id = ((((Element) nl.item(i))
                        .getElementsByTagName("id")).item(0))
                        .getFirstChild().getNodeValue();
                String name = ((((Element) nl.item(i))
                        .getElementsByTagName("name")).item(0))
                        .getFirstChild().getNodeValue();
                String terminalName = ((((Element) nl.item(i))
                        .getElementsByTagName("terminalName")).item(0))
                        .getFirstChild().getNodeValue();
                String lat = ((((Element) nl.item(i))
                        .getElementsByTagName("lat")).item(0))
                        .getFirstChild().getNodeValue();
                String lng = ((((Element) nl.item(i))
                        .getElementsByTagName("long")).item(0))
                        .getFirstChild().getNodeValue();
                String installed = ((((Element) nl.item(i))
                        .getElementsByTagName("installed")).item(0))
                        .getFirstChild().getNodeValue();
                String locked = ((((Element) nl.item(i))
                        .getElementsByTagName("locked")).item(0))
                        .getFirstChild().getNodeValue();
                String installDate = ((((Element) nl.item(i))
                        .getElementsByTagName("installDate")).item(0))
                        .getFirstChild().getNodeValue();
                String temporary = ((((Element) nl.item(i))
                        .getElementsByTagName("temporary")).item(0))
                        .getFirstChild().getNodeValue();
                String nbBikes = ((((Element) nl.item(i))
                        .getElementsByTagName("nbBikes")).item(0))
                        .getFirstChild().getNodeValue();
                String nbEmptyDocks = ((((Element) nl.item(i))
                        .getElementsByTagName("nbEmptyDocks")).item(0))
                        .getFirstChild().getNodeValue();
                String nbDocks = ((((Element) nl.item(i))
                        .getElementsByTagName("nbDocks")).item(0))
                        .getFirstChild().getNodeValue();

                double distance = calculateDistance(
                        targetLocation,
                        new LatLng(Double.parseDouble(lat), Double
                                .parseDouble(lng)));
                Station station = new Station(id, name, terminalName, lat,
                        lng, installed, locked, installDate, temporary,
                        nbBikes, nbEmptyDocks, nbDocks, distance);
                stationsList.add(station);
            } catch (Exception e) {

            }
        }
        Collections.sort(stationsList, new Comparator<Station>() {
            @Override
            public int compare(Station station1, Station station2) {
                if (station1.getDistance() <= station2.getDistance())
                    return -1;
                return 1;
            }
        });
        return stationsList;
    }

    public static float calculateDistance(LatLng StartP, LatLng EndP) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(EndP.latitude - StartP.latitude);
        double lngDiff = Math.toRadians(EndP.longitude - StartP.longitude);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(StartP.latitude))
                * Math.cos(Math.toRadians(EndP.latitude))
                * Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

}
