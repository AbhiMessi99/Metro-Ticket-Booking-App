package com.example.metroTicketBooking;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.*;

import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link bookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bookingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int fare = 0;

    int btStations;
    TextView reset;
    allvars arrays;
    AutoCompleteTextView route;
    AutoCompleteTextView source;
    AutoCompleteTextView destination;
    AutoCompleteTextView journeyType;
    AutoCompleteTextView passengers;
    private AdView adView;
    Button proceed;
    TextView time;

    private Handler handler = new Handler();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int destPos;
    private int sourPos;
    ImageView back;

    private String selectedRoute;
    private TextView currentTimeTextView;

    public bookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static bookingFragment newInstance(String param1, String param2) {
        bookingFragment fragment = new bookingFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_booking, container, false);
        arrays = new allvars();
        reset = view1.findViewById(R.id.reset);
        ArrayAdapter<String> routenames = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.routes);
        ArrayAdapter<String> jType = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.JourneyType);
        route = view1.findViewById(R.id.routeDropDown);
        source = view1.findViewById(R.id.sourceDropDown);
        destination = view1.findViewById(R.id.destinationDropDown);
        currentTimeTextView = view1.findViewById(R.id.timeOftravel);
        journeyType = view1.findViewById(R.id.JourneyTypeDropdown);
        passengers = view1.findViewById(R.id.passengersDropDown);
        proceed = view1.findViewById(R.id.buttonProceed);
        time = view1.findViewById(R.id.timeOftravel);
        back = view1.findViewById(R.id.imageViewBookBack);
        updateTime();
        route.setAdapter(routenames);
        journeyType.setAdapter(jType);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateDropdowns();
            }
        });
        source.setOnItemClickListener((parent, view, position, id) -> {
            sourPos = position;
        });
        destination.setOnItemClickListener((parent, view, position, id) -> {
            destPos = position;
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment destFragment = new HomeFragment();
                FragmentManager fgm = getFragmentManager();
                fgm.beginTransaction()
                        .replace(R.id.main, destFragment)
                        .commitAllowingStateLoss();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from = source.getText().toString();
                String to = destination.getText().toString();
                String rout = route.getText().toString();
                String jt = journeyType.getText().toString();
                String pas = passengers.getText().toString();
                String timeBook = time.getText().toString();
                reviewFragment destFragment = new reviewFragment();
                if (rout.equals(""))
                    Toast.makeText(getActivity(), "Please select Route", Toast.LENGTH_SHORT).show();
                else if (to.equals("") && from.equals(""))
                    Toast.makeText(getActivity(), "Please specify Source and Destination", Toast.LENGTH_SHORT).show();
                else if (jt.equals(""))
                    Toast.makeText(getActivity(), "Select Journey Type", Toast.LENGTH_SHORT).show();
                else if (pas.equals(""))
                    Toast.makeText(getActivity(), "Please Specify Passengers", Toast.LENGTH_SHORT).show();
                else if(to.equals(from))
                    Toast.makeText(getActivity(), "Destination and Source cannot be same", Toast.LENGTH_SHORT).show();
                else
                {
                    calculateFare();
                    Bundle bundle = new Bundle();
                    bundle.putString("destination", to);
                    bundle.putString("source", from);
                    bundle.putString("route", rout);
                    bundle.putString("journeyType", jt);
                    bundle.putString("passenger", pas);
                    bundle.putString("journeyTime", timeBook);
                    bundle.putString("fare", String.valueOf(fare));
                    destFragment.setArguments(bundle);
                    resetAutoCompleteTextViews();
                    FragmentManager fgm = getFragmentManager();
                    fgm.beginTransaction()
                            .replace(R.id.main, destFragment) // Replace fragment_container with the ID of the container in your activity layout
                            .addToBackStack(null) // Optional: Add the transaction to the back stack
                            .commitAllowingStateLoss();
                }
            }
        });
        journeyType.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            passengers.setText("");
            ArrayAdapter<String> pas;
            if (selectedItem.equals("Single Journey")) {
                pas = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.single);
            } else if (selectedItem.equals("Return Journey")) {
                pas = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.single);
            } else {
                pas = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, arrays.group);
            }
            passengers.setAdapter(pas);
        });

        return view1;
    }

    private void updateTime() {
        // Create a new thread to update the time continuously
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Get the current time
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                // Set the current time to the TextView
                if (currentTimeTextView != null) {
                    currentTimeTextView.setText(currentTime);
                }

                // Call this method again after 1 second (1000 milliseconds)
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
    private void resetAutoCompleteTextViews() {
        route.setText("");
        source.setText("");
        destination.setText("");
        journeyType.setText("");
        passengers.setText("");

        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, new ArrayList<>());
        route.setAdapter(emptyAdapter);
        source.setAdapter(emptyAdapter);
        destination.setAdapter(emptyAdapter);
        journeyType.setAdapter(emptyAdapter);
        passengers.setAdapter(emptyAdapter);
    }

    public void activateDropdowns() {
        // Reinitialize adapters and set them to the AutoCompleteTextView fields
        route.setText("");
        source.setText("");
        destination.setText("");
        journeyType.setText("");
        passengers.setText("");

        ArrayAdapter<String> routenames = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, arrays.routes);
        route.setAdapter(routenames);

        ArrayAdapter<String> jType = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, arrays.JourneyType);
        journeyType.setAdapter(jType);

    }
     public void calculateFare()
     {
         btStations = Math.abs(destPos - sourPos);
         if (selectedRoute.equals("Blue Line (Line-1): Dakshineswar to New Garia")) {
             if(btStations<=4)
             {
                 fare = btStations * 5;
             }
             else if(btStations<=10)
             {
                 fare = 30;
             }
             else if(btStations<15)
             {
                 fare = 40;
             }
             else
             {
                 fare = 45;
             }
         } else if (selectedRoute.equals("Green Line (Line-2A): Sector V to Sealdah")) {
             fare  = btStations * 5;
             if(fare>20){
                 fare = 20;
             }
         } else if (selectedRoute.equals("Green Line (Line-2B): Howrah Maidan to Esplanade")) {
             fare = (int) (btStations * 5);
         } else if (selectedRoute.equals("Purple Line (Line-3): Joka to Majerhat")) {
             fare = (int)(btStations * 5);
             if(fare > 20)
                 fare = 20;
         } else {
             fare = btStations * 5;
             if(fare > 20)
             {
                 fare = 20;
             }
         }
         String jT = journeyType.getText().toString();
         fare = fare * Integer.parseInt(passengers.getText().toString());
         if(jT.equals("Return Journey")){
             fare = fare * 2;
         }
     }
}