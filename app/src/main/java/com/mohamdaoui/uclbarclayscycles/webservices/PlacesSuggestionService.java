package com.mohamdaoui.uclbarclayscycles.webservices;

import android.os.AsyncTask;

import com.mohamdaoui.uclbarclayscycles.listeners.OnSuggestionSearchCompleted;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mohamdaoui.uclbarclayscycles.utils.Utils.autocomplete;


/**
 * Created by mohamdao on 07/01/2017.
 */

public class PlacesSuggestionService extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    private OnSuggestionSearchCompleted listener;

    public PlacesSuggestionService(OnSuggestionSearchCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
        return autocomplete(params[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        listener.onSuggestionSearchCompleted(hashMaps);
    }
}
