package com.mohamdaoui.uclbarclayscycles.webservices;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.mohamdaoui.uclbarclayscycles.listeners.OnLocationSearchCompleted;
import com.mohamdaoui.uclbarclayscycles.listeners.OnStationSearchCompleted;
import com.mohamdaoui.uclbarclayscycles.models.Station;

import java.util.ArrayList;

import static com.mohamdaoui.uclbarclayscycles.utils.Utils.GetXmlAndParseStations;
import static com.mohamdaoui.uclbarclayscycles.utils.Utils.getLocation;

/**
 * Created by mohamdao on 07/01/2017.
 */

public class SearchStationsService extends AsyncTask<String, LatLng, ArrayList<Station>> {
    OnStationSearchCompleted listener;
    OnLocationSearchCompleted locationListener;

    public SearchStationsService(OnStationSearchCompleted listener, OnLocationSearchCompleted locationListener) {
        this.listener = listener;
        this.locationListener = locationListener;
    }

    @Override
    protected ArrayList<Station> doInBackground(String... params) {
        LatLng targetLocation = getLocation(params[0], params[1]);
        publishProgress(targetLocation);
        if(targetLocation.latitude != 0 || targetLocation.longitude !=0)
            return GetXmlAndParseStations(targetLocation);
        return null;
    }

    @Override
    protected void onProgressUpdate(LatLng... locations) {
        super.onProgressUpdate(locations);
        locationListener.onLocationSearchCompleted(locations[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<Station> stations) {
        super.onPostExecute(stations);
        listener.onStationSearchCompleted(stations);
    }
}
