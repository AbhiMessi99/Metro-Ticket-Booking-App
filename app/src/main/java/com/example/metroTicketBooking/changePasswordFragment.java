package com.example.metroTicketBooking;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link changePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class changePasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String currPass, newPass, reNewPass;
    EditText currentPassword, newPassword, reNewPassword;
    Button changePassword;
    SessionManager sessionManager;
    String currUserPassword, currUser;
    DBhelperClass DB;

    public changePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment changePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static changePasswordFragment newInstance(String param1, String param2) {
        changePasswordFragment fragment = new changePasswordFragment();
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
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        currentPassword = view.findViewById(R.id.editTextCPassword);
        newPassword = view.findViewById(R.id.editTextCNewPassword);
        reNewPassword = view.findViewById(R.id.editTextCReNewPassword);
        changePassword = view.findViewById(R.id.changePasswordButton);
        DB = new DBhelperClass(getContext());
        sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetail= sessionManager.getUserDetail();
        currUserPassword = userDetail.get(sessionManager.KEY_PASSWORD);
        currUser = userDetail.get(sessionManager.KEY_USER);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currPass = currentPassword.getText().toString();
                newPass = newPassword.getText().toString();
                reNewPass = reNewPassword.getText().toString();
                if(newPass.length()>=8) {
                    if (currUserPassword.equals(currPass)) {
                        if (newPass.equals(reNewPass)) {
                            if (currPass.equals(newPass)) {
                                Toast.makeText(getActivity(), "Current and new password cannot be same", Toast.LENGTH_LONG).show();
                            } else {
                                int updates = DB.updatePassword(newPass, currUser);
                                if (updates > 0) {
                                    Toast.makeText(getActivity(), "Password changed Successfully", Toast.LENGTH_LONG).show();
                                    myProfileFragment destFragment = new myProfileFragment();
                                    FragmentManager fgm = getFragmentManager();
                                    sessionManager.updatePassword(newPass);
                                    fgm.beginTransaction()
                                            .replace(R.id.frame_layout, destFragment) // Replace fragment_container with the ID of the container in your activity layout
                                            .addToBackStack(null) // Optional: Add the transaction to the back stack
                                            .commitAllowingStateLoss();
                                } else {
                                    Toast.makeText(getActivity(), "Password not changed Try Again", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "New password not matched with re-entered password", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Current Password is wrong", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Password must be of minimum 8 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}