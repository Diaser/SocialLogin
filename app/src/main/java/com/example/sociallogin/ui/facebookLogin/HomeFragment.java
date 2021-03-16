package com.example.sociallogin.ui.facebookLogin;

import android.content.Intent;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private LoginButton loginButton;
    TextView tvInfo;
    ImageView imgProfile;
    CallbackManager callbackManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.tv_info);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        //https://www.facebook.com/photo?fbid=1148893051792824
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        tvInfo = view.findViewById(R.id.tv_info);
        imgProfile = view.findViewById(R.id.img_profile);
        callbackManager =  CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String result = "";
                result = "Id Token:" + loginResult.getAccessToken().getUserId();

                Log.i("FbkDan","onSucces");
                try{
                    Profile profile = Profile.getCurrentProfile();
                    if(profile.getFirstName() != null){
                        result = result + "\n FirstName" + profile.getFirstName();
                    }
                    if(profile.getLastName() != null)
                        result = result + "\n FirstName" + profile.getLastName();
                    if(profile.getName() != null)
                        result = result + "\n Name" + profile.getName();
                    //if(profile.getProfilePictureUri())

                        tvInfo.setText(result);
                    //String imgFbk = "https://graph.facebook.com/" + profile.getId() + "/picture?type=large&width=720&height=720"; //"https://www.facebook.com/photo?fbid=1148893051792824";   //"https://graph.facebook.com/"+ profile.getId()+ "/picture?fields=picture.width(720).height(720)";  //"https://www.facebook.com/photo?fbid=" + profile.getId();
                    //Picasso.get().load(imgFbk).into(imgProfile);

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    if (data.has("picture")) {
                                        String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        Picasso.get().load(profilePicUrl).into(imgProfile);
                                        //Bitmap profilePic= BitmapFactory.decodeStream(profilePicUrl .openConnection().getInputStream());
                                        //mImageView.setBitmap(profilePic);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,picture");
                    request.setParameters(parameters);
                    request.executeAsync();
                }catch (Exception e){
                    Log.e("FbkDan",e.getMessage());
                }


            }

            @Override
            public void onCancel() {
                Log.i("FbkDan","onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("FbkDan","FacebookException : " + error.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}