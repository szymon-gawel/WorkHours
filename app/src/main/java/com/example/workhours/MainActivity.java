package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.MonthDay;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db;

    SharedPreferences sharedPreferences;
    EditText addHours;
    EditText deleteHours;
    TextView monthHoursText;
    int spHours;
    int spMinutes;
    int currentDay;
    int spLastHours;
    int spLastMinutes;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);

        addHours = findViewById(R.id.addHoursEditView);
        deleteHours = findViewById(R.id.deleteHoursEditView);
        monthHoursText = findViewById(R.id.hoursInMonth);

        //Leave for testing purpose
        /*sharedPreferences.edit().putInt("Hours", 0).apply();
        sharedPreferences.edit().putInt("Minutes", 0).apply();*/

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

    @SuppressLint("SetTextI18n")
    public void onDeleteHoursButtonClick(View view){
        try{
            String hours = deleteHours.getText().toString();
            String[] splitedHours = hours.split("\\.");

            if(Integer.parseInt(splitedHours[1]) < 60){
                int h = Integer.parseInt(splitedHours[0]);
                int m = Integer.parseInt(splitedHours[1]);

                spHours -= h;
                spMinutes -= m;

                if(spMinutes < 0){
                    spHours -= 1;
                    spMinutes += 60;
                }

                if(spHours >= 0 && spMinutes >= 0){
                    sharedPreferences.edit().putInt("Hours", spHours).apply();
                    sharedPreferences.edit().putInt("Minutes", spMinutes).apply();

                    int hoursToDisplay = sharedPreferences.getInt("Hours", 0);
                    int minutesToDisplay = sharedPreferences.getInt("Minutes", 0);

                    if(minutesToDisplay < 10) {
                        monthHoursText.setText(hoursToDisplay + ".0" + minutesToDisplay);
                    } else {
                        monthHoursText.setText(hoursToDisplay + "." + minutesToDisplay);
                    }
                } else {
                    showNegativeNumberDialog();
                }
            } else {
                showErrorMinutesDialog();
            }
        } catch (Exception e){
            e.printStackTrace();
            showFormatDialog();
        }

    }

    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    public void onAddHoursButtonClick(View view){
        try {
            String hours = addHours.getText().toString();
            String[] splitedHours = hours.split("\\.");

            if(Integer.parseInt(splitedHours[1]) < 60){
                int h = Integer.parseInt(splitedHours[0]);
                int m = Integer.parseInt(splitedHours[1]);

                spHours += h;
                spMinutes += m;

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
            } else {
                showErrorMinutesDialog();
            }
        } catch (Exception e){
            e.printStackTrace();
            showFormatDialog();
        }

    }
    
    public void showFormatDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Use shown format");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showNegativeNumberDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Working time below 0 is not possible");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showErrorMinutesDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Please, use range of minutes between 0-59");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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