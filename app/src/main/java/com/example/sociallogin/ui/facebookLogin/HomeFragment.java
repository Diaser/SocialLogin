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
import com.example.sociallogin.to.User;
import com.example.sociallogin.utils.MyUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

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
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("birthday","email","user_gender","user_birthday","public_profile"));
        ArrayList<String> permission =new ArrayList<String>();
        permission.add("email");
        permission.add("public_profile");
        permission.add("user_birthday");
        permission.add("user_gender");
        loginButton.setPermissions(permission);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String result = "";
                result = "Id Token:" + loginResult.getAccessToken().getUserId();

                Log.i("FbkDan","onSucces");
                try{
                    /* Esto por si quieres datos simples
                    Profile profile = Profile.getCurrentProfile();
                    if(profile.getFirstName() != null)
                        result = result + "\n First Name " + profile.getFirstName();
                    if(profile.getLastName() != null)
                        result = result + "\n Last Name " + profile.getLastName();
                    if(profile.getName() != null)
                        result = result + "\n Name " + profile.getName();
                    if(profile.getMiddleName() != null)
                        result = result + "\n Middle Name " + profile.getMiddleName();
                    if(profile.getProfilePictureUri(50,50) != null){
                        MyUtils.showMessage(getActivity(),"hay imagen");

                        Picasso.get().load(String.valueOf(profile.getProfilePictureUri(50,50) )).into(imgProfile);
                    }else
                        MyUtils.showMessage(getActivity(),"No hay imagen");
                    tvInfo.setText(result);
                     */

                    //
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    if(data!=null){
                                        MyUtils.logCatPrinter("DAN","Data: " + data.toString());
                                        User user = new User();
                                        if(data.has("name")){
                                            user.setName(data.getString("name"));
                                            tvInfo.setText("Nombre: "+ data.getString("name"));
                                        }

                                        if(data.has("first_name")){
                                            user.setFirstName(data.getString("first_name"));
                                            tvInfo.setText(tvInfo.getText() + "\nPrimer nombre: "+ data.getString("first_name"));
                                        }

                                        if(data.has("last_name")){
                                            user.setLastName(data.getString("last_name"));
                                            tvInfo.setText(tvInfo.getText() + "\nApellidos: "+ data.getString("last_name"));
                                        }

                                        if(data.has("middle_name")){
                                            user.setMiddleName(data.getString("middle_name"));
                                            tvInfo.setText(tvInfo.getText() + "\nNombre medio: "+ data.getString("middle_name"));
                                        }

                                        if(data.has("email")){
                                            user.setEmail(data.getString("email"));
                                            tvInfo.setText(tvInfo.getText() + "\nCorreo: "+ data.getString("email"));
                                        }

                                        if(data.has("birthday")){
                                            user.setBirthday(data.getString("birthday"));
                                            tvInfo.setText(tvInfo.getText() + "\nFecha nac: "+ data.getString("birthday"));
                                        }

                                        if(data.has("gender")){
                                            user.setGender(data.getString("gender"));
                                            tvInfo.setText(tvInfo.getText() + "\nSexo: "+ data.getString("gender"));
                                        }

                                    }


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
                    parameters.putString("fields", "id,name,first_name,last_name,middle_name,email,birthday,gender,picture");
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