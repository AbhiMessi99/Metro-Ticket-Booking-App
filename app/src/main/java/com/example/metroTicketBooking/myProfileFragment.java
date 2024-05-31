package com.example.metroTicketBooking;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link myProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SessionManager sessionManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    CardView cardView;
    DBhelperClass DB;
    TextView username, email, account, mobile, gender, dob, changePassword;
    ImageView profilepic, accDel;
    String currUser;
    GoogleSignInAccount acc;
    GoogleSignInOptions gso;
    LinearLayout l1,l2,l3,l4,l5,l6;
    GoogleSignInClient gsc;

    public myProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment myProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static myProfileFragment newInstance(String param1, String param2) {
        myProfileFragment fragment = new myProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        username = view.findViewById(R.id.textViewProfileUserName);
        email = view.findViewById(R.id.textViewProfileEmail);
        account = view.findViewById(R.id.textViewProfileAccType);
        profilepic = view.findViewById(R.id.imageViewProfilePic);
        mobile = view.findViewById(R.id.textViewProfileMobile);
        gender = view.findViewById(R.id.textViewProfileGender);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("MetroUsers");
        cardView = view.findViewById(R.id.cardViewUserDetails);
        dob = view.findViewById(R.id.textViewProfileDob);
        DB = new DBhelperClass(getContext());
        changePassword = view.findViewById(R.id.textViewClickChangePassword);
        sessionManager = new SessionManager(getContext());
        l1 = view.findViewById(R.id.layouttoVanish);
        l2 = view.findViewById(R.id.layouttoVanish2);
        l3 = view.findViewById(R.id.layouttoVanish3);
        l4 = view.findViewById(R.id.layouttoVanish4);
        l5 = view.findViewById(R.id.layouttoVanish5);
        l6 = view.findViewById(R.id.layouttoVanish6);
        accDel = view.findViewById(R.id.imageViewDeleteAccount);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getContext(),gso);
        acc = GoogleSignIn.getLastSignedInAccount(getContext());
        if(acc!=null){
            username.setText(acc.getDisplayName());
            email.setText(acc.getEmail());
            Uri personPhoto = acc.getPhotoUrl();
            if (personPhoto != null) {
                Glide.with(this)
                        .load(personPhoto)
                        .transform(new CircularTransformation())
                        .into(profilepic);

                account.setText("by Google");
            }
        }else {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            l3.setVisibility(View.VISIBLE);
            l4.setVisibility(View.VISIBLE);
            l5.setVisibility(View.VISIBLE);
            l6.setVisibility(View.VISIBLE);
            accDel.setVisibility(view.VISIBLE);
            HashMap<String, String> userDetail= sessionManager.getUserDetail();
            currUser = userDetail.get(sessionManager.KEY_USER);
            DBhelperClass dbHelper = new DBhelperClass(getContext());
            HashMap<String, String> userDetails = dbHelper.getUserDetails(currUser);
            username.setText(currUser);
            email.setText(userDetail.get(sessionManager.KEY_EMAIL));
            gender.setText(userDetails.get("gender"));
            mobile.setText(userDetails.get("mobile").substring(3));
            dob.setText(userDetails.get("birth"));
        }

        accDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Account Deletion");
                alert.setMessage("All actives details and tickets will also be deleted. Are You Sure.");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeUserTransactionAndTickets(currUser);
                        int quertRs=DB.deleteAcc(currUser);
                        if(quertRs>0){
                            Toast.makeText(getActivity(),"Account Delete Successfully",Toast.LENGTH_SHORT).show();
                            sessionManager.logoutUser();
                        }else{
                            Toast.makeText(getActivity(),"Account Delete Successfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alert.show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(acc==null) {
                    changePasswordFragment destFragment = new changePasswordFragment();
                    FragmentManager fgm = getFragmentManager();
                    fgm.beginTransaction()
                            .replace(R.id.profileLayout, destFragment) // Replace fragment_container with the ID of the container in your activity layout
                            // Optional: Add the transaction to the back stack
                            .commitAllowingStateLoss();
                    cardView.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getContext(), "You signed in from google cannot alter password", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public void removeUserTransactionAndTickets(String userId) {
        // Reference to the specific user node
        DatabaseReference userRef = databaseReference.child(userId);

        // Remove the user node
        userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // User node removed successfully
                } else {
                    // Failed to remove the user node
                }
            }
        });
    }

}