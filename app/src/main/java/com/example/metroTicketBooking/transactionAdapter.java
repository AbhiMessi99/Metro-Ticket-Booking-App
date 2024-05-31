package com.example.metroTicketBooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
public class transactionAdapter extends ArrayAdapter<PaymentDetails> {

    private Context mContext;
    private List<PaymentDetails> mItemList;



    public transactionAdapter(Context context, List<PaymentDetails> itemList) {
        super(context, 0, itemList);
        mContext = context;
        mItemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_transactions, parent, false);
            holder = new ViewHolder();
            holder.textViewTransactionId = convertView.findViewById(R.id.textViewTransactionId);
            holder.textViewTransactionAmount = convertView.findViewById(R.id.textViewTransactionAmount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Get the current item object
        PaymentDetails currentItem = mItemList.get(position);
        // Set the TextViews and ImageView with data from the current item
        holder.textViewTransactionId.setText(currentItem.getPaymentId());
        holder.textViewTransactionAmount.setText("₹ "+currentItem.getAmount());
        return convertView;
    }
    static class ViewHolder {
        TextView textViewTransactionAmount;
        TextView textViewTransactionId;
    }
}
