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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<WorkLog> logs;
    private SQLiteDatabase database;

    SharedPreferences sharedPreferences;
    EditText addHours;
    EditText deleteHours;
    TextView monthHoursText;
    int spHours;
    int spMinutes;
    int currentDay;
    int spLastHours;
    int spLastMinutes;
    String action;
    String logDate;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);
        database = this.openOrCreateDatabase("Logs", MODE_PRIVATE, null);

        addHours = findViewById(R.id.addHoursEditView);
        deleteHours = findViewById(R.id.deleteHoursEditView);
        monthHoursText = findViewById(R.id.hoursInMonth);

        createLogsTable();

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

        String currentDate = LocalDateTime.now().toString();
        String[] dateAndTime = currentDate.split("T");
        String dateOnly = dateAndTime[0];

        String timeAndMilisecondsString = dateAndTime[1];
        String[] timeAndMiliseconds = timeAndMilisecondsString.split("\\.");
        String timeOnly = timeAndMiliseconds[0];

        logDate = dateOnly + " " + timeOnly;
    }

    public void createLogsTable(){
        database.execSQL("CREATE TABLE IF NOT EXISTS logs (date VARCHAR, hours VARCHAR, minutes VARCHAR)");
    }

    public void addLogToDatabase(WorkLog log){
        String logDate = log.getDate();
        String logHours = log.getHours();
        String logMinutes = log.getMinutes();
        database.execSQL("INSERT INTO logs (date, hours, minutes) VALUES (" + "'" + logDate + "'" + ", " + "'" + logHours + "'" + ", " + "'" + logMinutes + "'" + ")");
    }

    public List<WorkLog> getLogsFromDatabase(){
        List<WorkLog> logs = new ArrayList<WorkLog>();
        @SuppressLint("Recycle")
        Cursor c = database.rawQuery("SELECT * FROM logs", null);

        int dateIndex = c.getColumnIndex("date");
        int hoursIndex = c.getColumnIndex("hours");
        int minutesIndex = c.getColumnIndex("minutes");

        c.moveToFirst();

        while(!c.isAfterLast()){
            WorkLog log = new WorkLog(c.getString(dateIndex), c.getString(hoursIndex), c.getString(minutesIndex));
            logs.add(log);

            c.moveToNext();
        }

        return logs;
    }

    @SuppressLint("SetTextI18n")
    public void onDeleteHoursButtonClick(View view){
        action = "delete";
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
        action = "add";
        try {
            String hours = addHours.getText().toString();
            String[] splitedHours = hours.split("\\.");

            String currentDate = getCurrentDate();

            try {
                WorkLog log = new WorkLog(currentDate, splitedHours[0], splitedHours[1]);

                addLogToDatabase(log);
            } catch (Exception e){
                e.printStackTrace();
            }



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

    public String getCurrentDate(){
        String currentDate = LocalDateTime.now().toString();
        String[] dateAndTime = currentDate.split("T");
        String dateOnly = dateAndTime[0];

        String timeAndMilisecondsString = dateAndTime[1];
        String[] timeAndMiliseconds = timeAndMilisecondsString.split("\\.");
        String timeOnly = timeAndMiliseconds[0];

        logDate = dateOnly + " " + timeOnly;
        return logDate;
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
                Intent showDetailsIntent = new Intent(this, LogActivity.class);
                startActivity(showDetailsIntent);
                break;
            case R.id.mainScreen:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(mainActivityIntent);
                break;
        }

        return true;
    }
}