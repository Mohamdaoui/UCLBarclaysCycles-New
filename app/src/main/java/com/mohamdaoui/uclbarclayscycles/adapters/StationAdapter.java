package com.mohamdaoui.uclbarclayscycles.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mohamdaoui.uclbarclayscycles.R;
import com.mohamdaoui.uclbarclayscycles.models.Station;

import java.util.List;

/**
 * Created by mohamdao on 07/01/2017.
 */

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MyViewHolder>{

    private List<Station> stationsList;
    Context context;
    OnItemClickListener mItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public RelativeLayout stationHolder;
        public TextView description;
        public TextView nb_bicycles;
        public TextView nb_empty;
        public TextView distance;

        public MyViewHolder(View view) {
            super(view);
            stationHolder = (RelativeLayout) view.findViewById(R.id.stationHolder);
            description = (TextView) view.findViewById(R.id.description);
            nb_bicycles = (TextView) view.findViewById(R.id.nb_bicycles);
            nb_empty = (TextView) view.findViewById(R.id.nb_empty);
            distance = (TextView) view.findViewById(R.id.distance);

            stationHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public StationAdapter(Context context, List<Station> stationsList) {
        this.context = context;
        this.stationsList = stationsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Station station = stationsList.get(position);
        holder.description.setText(station.getName());
        holder.nb_bicycles.setText(station.getNbBikes());
        holder.nb_empty.setText(station.getNbEmptyDocks());
        holder.distance.setText(String.valueOf((int)station.getDistance()) + " m");
    }

    @Override
    public int getItemCount() {
        return stationsList.size();
    }

}