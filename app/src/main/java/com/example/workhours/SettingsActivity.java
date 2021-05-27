package com.example.workhours;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch themeSwitch;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);
        editor = preferences.edit();

        theme = preferences.getString("Theme", "Light");

        themeSwitch = findViewById(R.id.themeSwitch);

        if(theme.equals("Light")){
            themeSwitch.setChecked(false);
        } else {
            themeSwitch.setChecked(true);
        }

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putString("Theme", "Dark").apply();
                editor.commit();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putString("Theme", "Light").apply();
                editor.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.showDetails:
                Intent showDetailsIntent = new Intent(this, LogsActivity.class);
                startActivity(showDetailsIntent);
                break;
            case R.id.mainScreen:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(mainActivityIntent);
                break;
            case R.id.salaryScreen:
                Intent salaryIntent = new Intent(this, SalaryActivity.class);
                startActivity(salaryIntent);
                break;
            case R.id.settingsScreen:
                Intent settingsScreen = new Intent(this, SettingsActivity.class);
                startActivity(settingsScreen);
                break;
            case R.id.infoScreen:
                Intent infoScreen = new Intent(this, InfoActivity.class);
                startActivity(infoScreen);
                break;
        }

        return true;
    }
}