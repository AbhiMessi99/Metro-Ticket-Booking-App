package com.example.metroTicketBooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
public class listAdapter extends ArrayAdapter<TicketDetails> {

    private Context mContext;
    private List<TicketDetails> mItemList;

    public listAdapter(Context context, List<TicketDetails> itemList) {
        super(context, 0, itemList);
        mContext = context;
        mItemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_tickets, parent, false);
            holder = new ViewHolder();
            holder.textViewJtype = convertView.findViewById(R.id.textViewTicketJourney);
            holder.textViewSource = convertView.findViewById(R.id.textViewTicketSource);
            holder.textViewDestination = convertView.findViewById(R.id.textViewTicketDestination);
            holder.textViewFare = convertView.findViewById(R.id.textViewTicketCost);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Get the current item object
        TicketDetails currentItem = mItemList.get(position);
        // Set the TextViews and ImageView with data from the current item
        holder.textViewJtype.setText(currentItem.getJourney_type());
        holder.textViewSource.setText(currentItem.getSource());
        holder.textViewDestination.setText(currentItem.getDestination());
        holder.textViewFare.setText("₹ "+currentItem.getFare());

        return convertView;
    }
    static class ViewHolder {
        TextView textViewJtype;
        TextView textViewSource;
        TextView textViewDestination;
        TextView textViewFare;
    }
}
