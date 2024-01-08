package com.stepup.counter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.stepup.counter.databinding.SettingsActivityBinding;

public class Settings extends DrawerBaseActivity {
    private SharedPreferences spMode, spUserInfo;
    private SharedPreferences.Editor editor;
    private SettingsActivityBinding binding;

    private Boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spMode = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = spMode.getBoolean("nightMode", false);

        spUserInfo = getSharedPreferences("user_info",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = getSharedPreferences("goal", Context.MODE_PRIVATE).edit();

        binding.username.setText(spUserInfo.getString("username", "Username"));
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentmain = new Intent(Settings.this, Home.class);
                startActivity(intentmain);
            }
        });

        binding.goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goalString = binding.goal.getText().toString();
                if (!goalString.isEmpty()) {
                    int goalValue = Integer.parseInt(goalString);
                    editor.putInt("goal", goalValue);
                    editor.apply();
                } else {
                    Toast.makeText(Settings.this, "Please enter a valid goal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.privacyArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.freeprivacypolicy.com/live/ae6c4309-8ae3-4f1a-8fd0-e396ae12665d");
            }
        });

        // set night mode
        if (nightMode) {
            binding.nightModeSwitch.setChecked(true);
            setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // listener for each switch
        binding.nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                toggleNightMode());

        binding.notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                handleNotificationsSwitchChange(isChecked));

    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }


    private void toggleNightMode() {
        nightMode = !nightMode;

        binding.nightModeSwitch.setChecked(nightMode);

        setNightMode(nightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        editor = spMode.edit();
        editor.putBoolean("nightMode", nightMode);
        editor.apply();

        String message = nightMode ? "Night Mode Enabled" : "Night Mode Disabled";
        Toast.makeText(Settings.this, message, Toast.LENGTH_SHORT).show();
    }

    private void setNightMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }
    private void handleNightModeSwitchChange(boolean isChecked) {
        String message = isChecked ? "Night Mode Enabled" : "Night Mode Disabled";
        Toast.makeText(Settings.this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleNotificationsSwitchChange(boolean isChecked) {
        String message = isChecked ? "Notifications Enabled" : "Notifications Disabled";
        Toast.makeText(Settings.this, message, Toast.LENGTH_SHORT).show();
    }
}

