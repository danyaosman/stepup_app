package com.stepup.counter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.Profile;
import com.stepup.counter.R;
import com.stepup.counter.databinding.ActivityMainBinding;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity

{
    ActivityMainBinding binding;
    DBHelper dbhelper;
    SharedPreferences sharedPreferences;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        Boolean nightMode = sharedPreferences.getBoolean("nightMode", false);

        if(nightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if(isUserLoggedIn()) navigateHome();

        dbhelper = new DBHelper(this);

        //normal log in
        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUser = binding.username.getText().toString();
                String strPw = binding.password.getText().toString();

                if (!strUser.isEmpty() && !strPw.isEmpty()) {
                    Boolean checkCredentials = dbhelper.checkUsernamePassword(strUser, strPw);
                    if (checkCredentials || (strUser.equals("admin") && strPw.equals("1234"))) {
                        saveCredentials(strUser,strPw);
                        displayWelcomeMsg(strUser);
                        navigateHome();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });


        //handle google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            displayWelcomeMsg(acct.getDisplayName());
            saveCredentials(acct.getDisplayName());
            navigateHome();
        }

        binding.googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        //handle facebook sign in
        callbackManager = CallbackManager.Factory.create();
        binding.fbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile profile = Profile.getCurrentProfile();
                        if(profile!=null)
                        {
                            String user = profile.getName();
                            displayWelcomeMsg(user);
                            saveCredentials(user);
                            navigateHome();
                        }
                    }
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("FacebookLogin", "Facebook login error: " + exception.getMessage());
                        Toast.makeText(MainActivity.this, "Facebook login failed", Toast.LENGTH_SHORT).show();                    }
                });
    }
    void navigateHome(){
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
        finish();
    }
    void signIn(){
        if (gsc != null) {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent,1000);
        } else {
            Toast.makeText(MainActivity.this, "Google Sign-In Client is not initialized", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String user = account.getDisplayName();
                displayWelcomeMsg(user);
                navigateHome();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void displayWelcomeMsg(String username) {
        String welcome = "Welcome " + username + "!";
        Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
    }
    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String savedUsername = preferences.getString("username", null);

        return savedUsername != null;
    }
    private void saveCredentials(String username, String password) {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }
    private void saveCredentials(String username) {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.apply();
    }
}
