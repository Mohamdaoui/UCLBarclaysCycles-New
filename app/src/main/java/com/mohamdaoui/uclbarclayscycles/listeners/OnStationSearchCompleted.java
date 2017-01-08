package com.mohamdaoui.uclbarclayscycles.listeners;

import com.mohamdaoui.uclbarclayscycles.models.Station;

import java.util.ArrayList;

/**
 * Created by mohamdao on 07/01/2017.
 */

public interface OnStationSearchCompleted {
    void onStationSearchCompleted(ArrayList<Station> stations);
}
