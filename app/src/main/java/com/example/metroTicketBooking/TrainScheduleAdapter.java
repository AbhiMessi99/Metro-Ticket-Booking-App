package com.example.metroTicketBooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TrainScheduleAdapter extends BaseAdapter {
    private Context context;
    private List<TrainSchedule> trainScheduleList;

    public TrainScheduleAdapter(Context context, List<TrainSchedule> trainScheduleList) {
        this.context = context;
        this.trainScheduleList = trainScheduleList;
    }

    @Override
    public int getCount() {
        return trainScheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return trainScheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.train_schedule_item, parent, false);
        }

        TrainSchedule trainSchedule = trainScheduleList.get(position);

        TextView tvSource = convertView.findViewById(R.id.tvSource);
        TextView tvDepartureTime = convertView.findViewById(R.id.tvDepartureTime);
        TextView tvDestination = convertView.findViewById(R.id.tvDestination);
        TextView tvDestinationTime = convertView.findViewById(R.id.tvDestinationTime);
        TextView tvTimeDifference = convertView.findViewById(R.id.tvDuration);
        TextView tvStationBetween = convertView.findViewById(R.id.tvIntermediateStn);

        tvSource.setText(trainSchedule.getSource());
        tvDepartureTime.setText(trainSchedule.getDepartureTime());
        tvDestination.setText(trainSchedule.getDestination());
        tvDestinationTime.setText(trainSchedule.getDestinationTime());
        tvTimeDifference.setText(trainSchedule.getTimeDifference());
        tvStationBetween.setText(trainSchedule.getStationbtw());

        return convertView;
    }
}
