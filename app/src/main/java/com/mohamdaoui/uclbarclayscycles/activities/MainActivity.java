package com.mohamdaoui.uclbarclayscycles.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mohamdaoui.uclbarclayscycles.R;
import com.mohamdaoui.uclbarclayscycles.adapters.StationAdapter;
import com.mohamdaoui.uclbarclayscycles.adapters.StationsSearchAdapter;
import com.mohamdaoui.uclbarclayscycles.listeners.OnLocationSearchCompleted;
import com.mohamdaoui.uclbarclayscycles.listeners.OnStationSearchCompleted;
import com.mohamdaoui.uclbarclayscycles.listeners.OnSuggestionSearchCompleted;
import com.mohamdaoui.uclbarclayscycles.models.Station;
import com.mohamdaoui.uclbarclayscycles.webservices.PlacesSuggestionService;
import com.mohamdaoui.uclbarclayscycles.webservices.SearchStationsService;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mohamdaoui.uclbarclayscycles.utils.Constants.DESCRIPTION;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.REFERENCE;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_BY_NAME;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_BY_REF;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_LAT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_LNG;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.STATION_LAT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.STATION_LNG;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.STATION_NAME;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants._ID;
import static com.mohamdaoui.uclbarclayscycles.utils.Utils.convertToCursor;
import static com.mohamdaoui.uclbarclayscycles.utils.Utils.isNetworkAvailable;

public class MainActivity extends AppCompatActivity  implements OnSuggestionSearchCompleted, OnLocationSearchCompleted, OnStationSearchCompleted,
        SearchView.OnQueryTextListener, SearchView.OnSuggestionListener{

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private ProgressBar progressBar;
    private ImageView idleImage;
    private RecyclerView stationsRecyclerView;
    private PlacesSuggestionService suggestionService;
    private SearchStationsService searchStationsService;
    private StationAdapter stationAdapter;
    private ArrayList<Station> stationsToShow;
    private LatLng searchLocation;
    public static String[] columns = new String[]{_ID, DESCRIPTION, REFERENCE};
    private String searchCriteria = SEARCH_BY_NAME;
    private String searchReference;
    StationsSearchAdapter mStationsSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stationsRecyclerView = (RecyclerView) findViewById(R.id.stationsRecyclerView);
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        stationsRecyclerView.setLayoutManager(myLayoutManager);
        stationsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        idleImage = (ImageView) findViewById(R.id.idleImage);

        // Adding a divider to our recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(stationsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.devider, null));
        stationsRecyclerView.addItemDecoration(dividerItemDecoration);

        stationsRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        mStationsSearchAdapter = new StationsSearchAdapter(this, R.layout.station_search_item, null, columns, null, -1000);
        searchView.setSuggestionsAdapter(mStationsSearchAdapter);

        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        // Sometimes when we click on submit, the keyboar doesn't hide, so we must force it
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if(isNetworkAvailable(getApplicationContext())){
            idleImage.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            searchStationsService = new SearchStationsService(this, this);
            String[] params = new String[] {searchCriteria, s};
            if(searchCriteria.equals(SEARCH_BY_REF))
                params = new String[] {searchCriteria, searchReference};
            searchStationsService.execute(params);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        searchCriteria = SEARCH_BY_NAME;
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        loadData(s);
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        searchCriteria = SEARCH_BY_REF;
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        searchReference = cursor.getString(cursor.getColumnIndex(REFERENCE));
        String name = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        searchView.setQuery(name, true);
        searchView.clearFocus();
        return true;
    }

    private void loadData(String searchText) {
        suggestionService = new PlacesSuggestionService(this);
        suggestionService.execute(new String[]{searchText});
    }

    @Override
    public void onSuggestionSearchCompleted(ArrayList<HashMap<String, String>> hashMap) {
        if(hashMap != null){
            MatrixCursor matrixCursor = convertToCursor(hashMap);
            mStationsSearchAdapter.changeCursor(matrixCursor);
        }
    }

    @Override
    public void onStationSearchCompleted(ArrayList<Station> stations) {
        progressBar.setVisibility(View.INVISIBLE);
        if(stations == null){
            Toast.makeText(getApplicationContext(), R.string.no_place_found, Toast.LENGTH_SHORT).show();
            stationsRecyclerView.setVisibility(View.INVISIBLE);
        } else {
        if(stations.size()>10)
            stationsToShow = new ArrayList<>(stations.subList(0,10));
        else
            stationsToShow = stations;
        stationsRecyclerView.setVisibility(View.VISIBLE);
        stationAdapter = new StationAdapter(this, stationsToShow);
        stationsRecyclerView.setAdapter(stationAdapter);
        stationAdapter.setOnItemClickListener(mItemClickListener);
        }
    }

    StationAdapter.OnItemClickListener mItemClickListener = new StationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapIntent.putExtra(SEARCH_LAT, searchLocation.latitude);
            mapIntent.putExtra(SEARCH_LNG, searchLocation.longitude);
            mapIntent.putExtra(STATION_NAME, stationsToShow.get(position).getName());
            mapIntent.putExtra(STATION_LAT, stationsToShow.get(position).getLat());
            mapIntent.putExtra(STATION_LNG, stationsToShow.get(position).getLng());
            startActivity(mapIntent);
        }
    };

    @Override
    public void onLocationSearchCompleted(LatLng location) {
        searchLocation = location;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(suggestionService != null)
            suggestionService.cancel(true);
        if(searchStationsService != null)
            searchStationsService.cancel(true);
    }
}
