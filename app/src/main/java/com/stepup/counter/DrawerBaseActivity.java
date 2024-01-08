package com.stepup.counter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.stepup.counter.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.drawer_base,null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        super.setContentView(view);

        Toolbar toolbar = drawerLayout.findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        } else if (itemId == R.id.nav_settings) {
            try {
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, show a toast, log it, or take appropriate action
                Toast.makeText(this, "Error starting Settings activity", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.nav_out) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
