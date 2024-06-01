package com.example.metroTicketBooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class stationAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mItems;

    public stationAdapter(Context context, List<String> items) {
        super(context, 0, items);
        mContext = context;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.routestations_list, parent, false);
        }

        // Get the current item from the array
        String currentItem = mItems.get(position);

        // Initialize view in the custom layout
        TextView station = listItem.findViewById(R.id.label);

        // Set the title to the TextView
        station.setText(currentItem);

        return listItem;
    }
}
