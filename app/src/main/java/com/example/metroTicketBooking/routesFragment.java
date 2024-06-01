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

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link routesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class routesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    allvars arrays;
    private String selectedRoute;
    ListView Stations;
    private String mParam2;

    AutoCompleteTextView route;
    ImageView back;
    public routesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment routesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static routesFragment newInstance(String param1, String param2) {
        routesFragment fragment = new routesFragment();
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
        View view1 = inflater.inflate(R.layout.fragment_routes, container, false);
        arrays = new allvars();
        back = view1.findViewById(R.id.imageViewRoutesBack);
        route = view1.findViewById(R.id.routeRDropDown);
        Stations = view1.findViewById(R.id.listViewStations);
        ArrayAdapter routenames = new ArrayAdapter(getActivity(), R.layout.dropdown_item, arrays.routes);
        route.setAdapter(routenames);
        route.setOnItemClickListener((parent, view, position, id) -> {
            stationAdapter adapterStations;
            selectedRoute = (String) parent.getItemAtPosition(position);
            if (selectedRoute.equals("Blue Line (Line-1): Dakshineswar to New Garia")) {
                adapterStations  = new stationAdapter(getActivity(), Arrays.asList(arrays.Line1));
            } else if (selectedRoute.equals("Green Line (Line-2A): Sector V to Sealdah")) {
                adapterStations = new stationAdapter(getActivity(), Arrays.asList(arrays.Line2A));
            } else if (selectedRoute.equals("Green Line (Line-2B): Howrah Maidan to Esplanade")) {
                adapterStations = new stationAdapter(getActivity(), Arrays.asList(arrays.Line2B));
            } else if (selectedRoute.equals("Purple Line (Line-3): Joka to Majerhat")) {
                adapterStations = new stationAdapter(getActivity(), Arrays.asList(arrays.Line3));
            }else {
                adapterStations = new stationAdapter(getActivity(), Arrays.asList(arrays.Line6));
            }
            Stations.setAdapter(adapterStations);
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment destFragment = new HomeFragment();
                FragmentManager fgm = getFragmentManager();
                fgm.beginTransaction()
                        .replace(R.id.frameLayoutRoute, destFragment)
                        .commitAllowingStateLoss();
            }
        });
        return  view1;
    }
}