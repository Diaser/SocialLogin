package com.example.sociallogin.ui.googleLogin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sociallogin.R;
import com.example.sociallogin.utils.MyUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    GoogleSignInClient mGoogleSignInClient;
    TextView tvInfo, tvExit;
    ImageView imgProfile;
    private SignInButton signInButton;
    private final int RC_SIGN_IN = 1;
    private final String TAG = "DanGoog";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.tv_info);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signInButton = view.findViewById(R.id.sign_in_button);
        tvInfo = view.findViewById(R.id.tv_info);
        tvExit = view.findViewById(R.id.tv_exit);
        imgProfile = view.findViewById(R.id.img_profile);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account !=null){
            showData(account);
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singOut();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            showData(account);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            MyUtils.showMessage(getActivity(),"ingresó correctamente");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            MyUtils.showMessage(getActivity(),"Error: " + e.getStatusCode());
            //updateUI(null);
        }
    }
    private void showData(GoogleSignInAccount account){
        if(account!=null){
            String result = "";
            String personName =  account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            if(isEmpty(personId) == false)
                result = result + "Id: " + personId;
            if(isEmpty(personName) == false)
                result = result + "\nName: " + personName;
            if(isEmpty(personGivenName) == false)
                result = result + "\nNombre dado: " + personGivenName;
            if(isEmpty(personFamilyName) == false)
                result = result + "\nNombre familia: " + personFamilyName;
            if(isEmpty(personEmail) == false)
                result = result + "\nEmail: " + personEmail;

            tvInfo.setText(result);
            tvExit.setVisibility(View.VISIBLE);
            Picasso.get().load(String.valueOf(personPhoto)).into(imgProfile);
        }
    }

    private boolean isEmpty(String parameter){
        if(parameter !=null && !parameter.equals(""))
            return false;
        return true;
    }
    private void singOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        tvInfo.setText("");
                        tvExit.setVisibility(View.GONE);
                    }
                });
    }
}