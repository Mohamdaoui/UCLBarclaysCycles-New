package com.mohamdaoui.uclbarclayscycles.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mohamdaoui.uclbarclayscycles.R;

import static com.mohamdaoui.uclbarclayscycles.utils.Constants.DESCRIPTION;

/**
 * Created by mohamdao on 07/01/2017.
 */

public class StationsSearchAdapter extends SimpleCursorAdapter {

    public StationsSearchAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView)view.findViewById(R.id.station_search_item_name);
        name.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
    }
}