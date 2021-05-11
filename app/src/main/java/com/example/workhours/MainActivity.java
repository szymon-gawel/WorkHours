package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.MonthDay;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText addHours;
    EditText deleteHours;
    TextView monthHoursText;
    int spHours;
    int spMinutes;
    int currentDay;
    int spLastHours;
    int spLastMinutes;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);

        addHours = findViewById(R.id.addHoursEditView);
        deleteHours = findViewById(R.id.deleteHoursEditView);
        monthHoursText = findViewById(R.id.hoursInMonth);

        //Leave for testing purpose
        sharedPreferences.edit().putInt("Hours", 0).apply();
        sharedPreferences.edit().putInt("Minutes", 0).apply();

        spHours = sharedPreferences.getInt("Hours", 0);
        spMinutes = sharedPreferences.getInt("Minutes", 0);

        Calendar cal = Calendar.getInstance();
        currentDay = cal.get(Calendar.DAY_OF_MONTH);

        if(currentDay == 1){
            spLastHours = spHours;
            spLastMinutes = spMinutes;
            spHours = 0;
            spMinutes = 0;
        }

        if(spMinutes < 10) {
            monthHoursText.setText(spHours + ".0" + spMinutes);
        } else {
            monthHoursText.setText(spHours + "." + spMinutes);
        }
    }

    public void onDeleteHoursButtonClick(View view){
        String hours = deleteHours.getText().toString();
        String[] splitedHours = hours.split("\\.");

        Log.i("Hours", splitedHours[0]);
        Log.i("Minutes", splitedHours[1]);

        int h = Integer.parseInt(splitedHours[0]);
        int m = Integer.parseInt(splitedHours[1]);

        spHours -= h;
        spMinutes -= m;

        Log.i("Hours", String.valueOf(spHours));
        Log.i("Minutes", String.valueOf(spMinutes));

        if(spMinutes < 0){
            spHours -= 1;
            spMinutes += 60;
        }

        sharedPreferences.edit().putInt("Hours", spHours).apply();
        sharedPreferences.edit().putInt("Minutes", spMinutes).apply();

        int hoursToDisplay = sharedPreferences.getInt("Hours", 0);
        int minutesToDisplay = sharedPreferences.getInt("Minutes", 0);

        if(minutesToDisplay < 10) {
            monthHoursText.setText(hoursToDisplay + ".0" + minutesToDisplay);
        } else {
            monthHoursText.setText(hoursToDisplay + "." + minutesToDisplay);
        }
    }

    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    public void onAddHoursButtonClick(View view){
        String hours = addHours.getText().toString();
        String[] splitedHours = hours.split("\\.");

        Log.i("Hours", splitedHours[0]);
        Log.i("Minutes", splitedHours[1]);

        int h = Integer.parseInt(splitedHours[0]);
        int m = Integer.parseInt(splitedHours[1]);

        Log.i("Hours", String.valueOf(h));
        Log.i("Minutes", String.valueOf(m));

        spHours += h;
        spMinutes += m;

        Log.i("Hours", String.valueOf(spHours));
        Log.i("Minutes", String.valueOf(spMinutes));

        if(spMinutes == 60){
            spHours += 1;
            spMinutes = 0;
        } else if (spMinutes > 60) {
            spHours += 1;
            spMinutes -= 60;
        }

        sharedPreferences.edit().putInt("Hours", spHours).apply();
        sharedPreferences.edit().putInt("Minutes", spMinutes).apply();

        int hoursToDisplay = sharedPreferences.getInt("Hours", 0);
        int minutesToDisplay = sharedPreferences.getInt("Minutes", 0);

        if(minutesToDisplay < 10) {
            monthHoursText.setText(hoursToDisplay + ".0" + minutesToDisplay);
        } else {
            monthHoursText.setText(hoursToDisplay + "." + minutesToDisplay);
        }
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
                break;
        }

        return true;
    }
}