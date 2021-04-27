package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText addHours;
    TextView monthHoursText;
    TextView weekHoursText;
    double monthHours;
    double weekHours;
    double hoursCount;
    double minutesCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);

        addHours = findViewById(R.id.addHoursEditView);
        monthHoursText = findViewById(R.id.hoursInMonth);
        weekHoursText = findViewById(R.id.hoursInWeek);
        
    }

    public void onAddHoursButtonClick(View view){
        String hours = addHours.toString();
        String[] splitedHours = hours.split("\\.");

        hoursCount = Double.parseDouble(splitedHours[0]);
        minutesCount = 1/(Double.parseDouble(splitedHours[1])/60);

        monthHours += hoursCount + minutesCount;
        weekHours += hoursCount + minutesCount;

        String currentHoursCount = String.valueOf(hoursCount);
        String currentMinutesCount = String.valueOf(60*minutesCount);

        String currentHours = currentHoursCount + "." + currentMinutesCount;
        monthHoursText.setText(currentHours);
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