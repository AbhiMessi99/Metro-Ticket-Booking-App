package com.example.metroTicketBooking;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reviewFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Handler handler = new Handler();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView back;
    String fare;
    Button payment;
    TextView journeyType, money, source, destination, route, currentTimeTextView, passenger;
    ImageView interchange;
    CardView cardView;

    public reviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static reviewFragment newInstance(String param1, String param2) {
        reviewFragment fragment = new reviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        Bundle args = getArguments();
        back = view.findViewById(R.id.imageViewReviewBack);
        journeyType = view.findViewById(R.id.textViewJType);
        cardView = view.findViewById(R.id.cardViewReviewJourney);
        cardView.setVisibility(View.VISIBLE);
        source = view.findViewById(R.id.textViewSource);
        destination = view.findViewById(R.id.textViewDestination);
        money = view.findViewById(R.id.textViewMoney);
        route = view.findViewById(R.id.textViewRoute);
        currentTimeTextView = view.findViewById(R.id.textViewTimeOftravel);
        passenger = view.findViewById(R.id.textViewPassenger);
        interchange = view.findViewById(R.id.imageViewInterchnge);
        payment = view.findViewById(R.id.buttonPay);
        journeyType.setText(args.getString("journeyType"));
        source.setText(args.getString("source"));
        destination.setText(args.getString("destination"));
        route.setText(args.getString("route"));
        passenger.setText(args.getString("passenger"));
        fare = args.getString("fare");
        money.setText("₹ "+fare);
        updateTime();
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity() , razorpayActivity.class);
                intent.putExtra("journeyType", journeyType.getText().toString());
                intent.putExtra("source", source.getText().toString());
                intent.putExtra("destination", destination.getText().toString());
                intent.putExtra("route", route.getText().toString());
                intent.putExtra("passenger", passenger.getText().toString());
                intent.putExtra("booktime", currentTimeTextView.getText().toString());
                intent.putExtra("money", fare);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingFragment destFragment = new bookingFragment();
                FragmentManager fgm = getFragmentManager();
                fgm.beginTransaction()
                        .replace(R.id.reviewLayout, destFragment)
                        .commitAllowingStateLoss();
                cardView.setVisibility(View.GONE);
            }
        });

        interchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Swap the text of source and destination TextViews
                String temp = source.getText().toString();
                source.setText(destination.getText());
                destination.setText(temp);
            }
        });
        return view;

    }
    private void makepayment()
    {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_GaxlJ9Hue5xPUo");
        checkout.setImage(R.drawable.ic_launcher_background);
        try {
            JSONObject options = new JSONObject();

            options.put("name", "JACKY");
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "50000");//pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact","8789629431");
            options.put("method","upi");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(getActivity(), options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }
    private void updateTime(){
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
}