package com.example.metroTicketBooking;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link scheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class scheduleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    allvars arrays;
    ImageView back;

    AutoCompleteTextView route;
    AutoCompleteTextView source;
    AutoCompleteTextView destination;
    private TrainScheduleAdapter adapter;
    private List<TrainSchedule> trainScheduleList;
    TextView tvNoTrains;
    private int destPos;
    private int sourPos;
    String sour, des;
    private ListView listView;
    private String selectedRoute;
    public scheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment scheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static scheduleFragment newInstance(String param1, String param2) {
        scheduleFragment fragment = new scheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_schedule, container, false);
        arrays = new allvars();
        ArrayAdapter<String> routenames = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.routes);
        listView = view1.findViewById(R.id.ListViewScheduledTrains);
        tvNoTrains = view1.findViewById(R.id.textViewNoTrains);
        trainScheduleList = new ArrayList<>();
        route = view1.findViewById(R.id.routeRDropDown);
        source = view1.findViewById(R.id.sourceDropDown);
        destination = view1.findViewById(R.id.destinationDropDown);
        back = view1.findViewById(R.id.imageViewsSchBack);

        route.setAdapter(routenames);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment destFragment = new HomeFragment();
                FragmentManager fgm = getFragmentManager();
                fgm.beginTransaction()
                        .replace(R.id.frame_layout, destFragment)
                        .commit();
            }
        });


        route.setOnItemClickListener((parent, view, position, id) -> {
            selectedRoute = (String) parent.getItemAtPosition(position);
            source.setText("");
            destination.setText("");
            ArrayAdapter<String> lineAdapter;
            if (selectedRoute.equals("Blue Line (Line-1): Dakshineswar to New Garia")) {
                lineAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.Line1);
            } else if (selectedRoute.equals("Green Line (Line-2A): Sector V to Sealdah")) {
                lineAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.Line2A);
            } else if (selectedRoute.equals("Green Line (Line-2B): Howrah Maidan to Esplanade")) {
                lineAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.Line2B);
            } else if (selectedRoute.equals("Purple Line (Line-3): Joka to Majerhat")) {
                lineAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.Line3);
            } else if (selectedRoute.equals("Orange Line (Line-6): New Garia to Ruby Hospital")) {
                lineAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.Line6);
            } else {
                // Default adapter or any other action
                lineAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item);
            }
            source.setAdapter(lineAdapter);
            destination.setAdapter(lineAdapter);
        });

        destination.setOnItemClickListener((parent, view, position, id) -> {
            sour = source.getText().toString();
            des = destination.getText().toString();
            destPos = position;

            trainScheduleList.clear();
            if(!sour.isEmpty() && !des.isEmpty() && !sour.equals(des)) {
                Calendar calendar = Calendar.getInstance();
                destPos = position;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                addTrainSchedule(sour, des, calendar, 5);
                addTrainSchedule(sour, des, calendar, 15);
                addTrainSchedule(sour, des, calendar, 27);
                addTrainSchedule(sour, des, calendar, 35);
                addTrainSchedule(sour, des, calendar, 43);

                if (trainScheduleList.isEmpty()) {
                    tvNoTrains.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    tvNoTrains.setVisibility(View.GONE);
                    adapter = new TrainScheduleAdapter(getContext(), trainScheduleList);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }else{
            }
        });

        source.setOnItemClickListener((parent, view, position, id) -> {
            sour = source.getText().toString();
            des = destination.getText().toString();
            sourPos = position;
            trainScheduleList.clear();
            if(!sour.isEmpty() && !des.isEmpty() && !sour.equals(des)) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                addTrainSchedule(sour, des, calendar, 5);
                addTrainSchedule(sour, des, calendar, 15);
                addTrainSchedule(sour, des, calendar, 27);
                addTrainSchedule(sour, des, calendar, 35);
                addTrainSchedule(sour, des, calendar, 43);

                if (trainScheduleList.isEmpty()) {
                    tvNoTrains.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    tvNoTrains.setVisibility(View.GONE);
                    adapter = new TrainScheduleAdapter(getContext(), trainScheduleList);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }else{
            }
        });

        return view1;
    }
    private void addTrainSchedule(String source, String destination, Calendar calendar, int minutesToAdd) {

        int[] originalArray = {2, 3, 4, 5};
        int stationbtw = Math.abs(destPos-sourPos);
        int time = sumRandomElements(originalArray, stationbtw);
        Calendar departureTime = (Calendar) calendar.clone();
        departureTime.add(Calendar.MINUTE, minutesToAdd);
        int departureHour = departureTime.get(Calendar.HOUR_OF_DAY);

        // Ensure departure time is between 6 AM (06:00) and 9 PM (21:00)
        if (departureHour >= 6 && departureHour <= 21) {
            String formattedDepartureTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(departureTime.getTime());

            // Assuming destination time is 1 hour after departure
            Calendar destinationTime = (Calendar) departureTime.clone();
            destinationTime.add(Calendar.MINUTE, time);
            String formattedDestinationTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(destinationTime.getTime());

            // Calculate time difference
            long timeDifferenceInMillis = destinationTime.getTimeInMillis() - departureTime.getTimeInMillis();
            long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceInMillis);

            String timeDifference = String.format(Locale.getDefault(), "%d minutes", minutesDifference);
            String EDT = "EDT: "+formattedDepartureTime;
            String EAT = "EAT: "+formattedDestinationTime;
            String interStation = "Stoppages: "+stationbtw;
            String duration = "Duration: "+timeDifference;
            trainScheduleList.add(new TrainSchedule(source, destination, EDT, EAT, duration, interStation));
        }
    }
    public static int sumRandomElements(int[] arr, int count) {
        Random random = new Random();
        int sum = 0;
        int length = arr.length;
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(length);
            sum += arr[randomIndex];
        }

        return sum;
    }
}