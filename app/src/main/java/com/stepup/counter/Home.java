package com.stepup.counter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.stepup.counter.R;
import com.stepup.counter.databinding.HomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class Home extends DrawerBaseActivity implements SensorEventListener, NavigationView.OnNavigationItemSelectedListener {
    private SensorManager eSensorManager;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private int prevTotalSteps = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    HomeBinding binding;
    Toolbar toolbar;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    SharedPreferences sharedPreferences;
    int goalInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("goal",Context.MODE_PRIVATE);
        goalInt = sharedPreferences.getInt("goal",6000);
        binding.goal.setText("Goal : " + String.valueOf(goalInt));

        drawerLayout = findViewById(R.id.drawer_layout);
        binding.navView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        resetSteps();
        loadData();
        eSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = eSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        binding.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = binding.drawerLayout;
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

    }

    public void onMenuButtonClick(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }


    protected void onResume() {
        super.onResume();

        if (stepSensor == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Home.this, "This device is not equipped with a sensor", Toast.LENGTH_SHORT).show();
                }
            }, 2000);}
        else {
            eSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();
        eSensorManager.unregisterListener(this);
    }

    //calculating steps, distance, calories
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - prevTotalSteps;

            double caloriesCal = currentSteps * 0.04;
            binding.calories.setText(String.format(Locale.getDefault(), "%.2f kcal", caloriesCal));
            double distance =  currentSteps * 76.2 / 1000.0;
            binding.distance.setText(String.format(Locale.getDefault(), "%.2f km", distance));

            binding.steps.setText(String.valueOf(currentSteps));

            int progress = (int) ((float) currentSteps / goalInt * 100);
            binding.progressBar.setProgress(progress);

            int secondaryProgress = 100 - progress;
            binding.progressBar.setSecondaryProgress(secondaryProgress);

            prevTotalSteps = totalSteps;
        }
    }

    protected void resetSteps() {
        binding.steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "Long press to reset steps", Toast.LENGTH_SHORT).show();
            }
        });
        binding.steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                prevTotalSteps = totalSteps;
                binding.steps.setText("0");
                binding.progressBar.setProgress(0);
                saveData();
                return true;
            }
        });
    }

    private void saveData() {
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("key1", prevTotalSteps);
        editor.apply();
    }

    protected void loadData() {
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int savedNumber = sharedPref.getInt("key1", 0);
        prevTotalSteps = savedNumber;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void gSignOut() {
        if (gsc != null) {
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    finish();
                    startActivity(new Intent(Home.this, MainActivity.class));
                }
            });
        }
    }
    //menu items actions
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(Home.this, Home.class);
            startActivity(intent);
            finish();
        } else if (itemId == R.id.nav_settings) {
            try {
                Intent intent = new Intent(Home.this, Settings.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, show a toast, log it, or take appropriate action
                Toast.makeText(Home.this, "Error starting Settings activity", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.nav_out) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                gSignOut();
            } else {
                SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intentMain = new Intent(Home.this, MainActivity.class);
                startActivity(intentMain);
                finish();
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

