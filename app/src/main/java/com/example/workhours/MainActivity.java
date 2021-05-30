package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity<DocumentReference> extends AppCompatActivity {

    private List<WorkLog> logs;
    private com.google.firebase.firestore.DocumentReference docRef;
    private com.google.firebase.firestore.DocumentReference secDocRef;
    private String android_id;

    public static final String DATE_KEY = "date";
    public static final String HOURS_KEY = "hours";
    public static final String MINUTES_KEY = "minutes";
    public static final String ACTION_KEY = "action";
    public static final String SEC_ID_KEY = "id";
    public static final String TAG = "DATABASE";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText addHours;
    EditText deleteHours;
    EditText hourlyRate;
    Button addButton;
    Button deleteButton;
    TextView monthHoursText;
    TextView manageHoursOptions;
    TextView hoursLabel;
    int spHours;
    int spMinutes;
    int currentDay;
    int currentMonth;
    int spLastHours;
    int spLastMinutes;
    int docNumber;
    int historyDocNum;
    int monthChanged;
    int spCurrentMonth;
    double salary;
    String action;
    String logDate;
    String theme;
    String lang;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        addHours = findViewById(R.id.addHoursEditView);
        deleteHours = findViewById(R.id.deleteHoursEditView);
        monthHoursText = findViewById(R.id.hoursInMonth);
        addButton = findViewById(R.id.addHoursButton);
        deleteButton = findViewById(R.id.deleteHoursButton);
        manageHoursOptions = findViewById(R.id.addHoursText);
        hoursLabel = findViewById(R.id.salaryLabel);

        //Leave for testing purpose
        //resetSharedPreferences();

        spHours = sharedPreferences.getInt("Hours", 0);
        spMinutes = sharedPreferences.getInt("Minutes", 0);
        docNumber = sharedPreferences.getInt("DocNum", 0);
        historyDocNum = sharedPreferences.getInt("HistoryDocNum", 0);
        theme = sharedPreferences.getString("Theme", "Light");
        lang = sharedPreferences.getString("Language", "eng");
        spCurrentMonth = sharedPreferences.getInt("CurrentMonth", 0);

        setLanguage();

        if(theme.equals("Dark")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Calendar cal = Calendar.getInstance();
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        currentMonth = cal.get(Calendar.MONTH);
        monthChanged = 0;

        if(currentMonth == spCurrentMonth+1){
            if (monthChanged == 0){
                spLastHours = spHours;
                spLastMinutes = spMinutes;
                createHistoryLog();
                spHours = 0;
                spMinutes = 0;
                monthChanged = 1;
                editor.putInt("CurrentMonth", currentMonth).apply();
                editor.commit();
            }
        }

        if (currentDay == 2){
            monthChanged = 0;
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

        currentMonth = cal.get(Calendar.MONTH);
    }


    @SuppressLint("SetTextI18n")
    public void onDeleteHoursButtonClick(View view){
        action = "delete";
        editor.putInt("CurrentMonth", Calendar.MONTH).apply();
        editor.commit();
        try {
            editor.putString("Doc", "log" + String.valueOf(docNumber)).apply();
            editor.commit();
            String hours = deleteHours.getText().toString();
            String[] splitedHours = hours.split("\\.");

            if(splitedHours.length == 1){
                String[] newSplittedHours = new String[2];
                newSplittedHours[0] = splitedHours[0];
                newSplittedHours[1] = "00";

                splitedHours = newSplittedHours;
            }

            String currentDate = getCurrentDate();

            try {
                createLog(currentDate, splitedHours[0], splitedHours[1]);
            } catch(Exception e){
                e.printStackTrace();
            }

            if(Integer.parseInt(splitedHours[1]) < 60){
                int h = Integer.parseInt(splitedHours[0]);
                int m = Integer.parseInt(splitedHours[1]);

                if(h <= spHours){
                    if(h == spHours){
                        if(m <= spMinutes){
                            spHours -= h;
                            spMinutes -= m;
                        } else {
                            showNegativeNumberDialog();
                        }
                    } else {
                        spHours -= h;
                        spMinutes -= m;
                    }
                } else {
                    showNegativeNumberDialog();
                }

                if(spMinutes < 0){
                    spHours -= 1;
                    spMinutes += 60;
                }

                if(spHours >= 0 && spMinutes >= 0){
                    editor.putInt("Hours", spHours).apply();
                    editor.commit();
                    editor.putInt("Minutes", spMinutes).apply();
                    editor.commit();

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
        editor.putInt("CurrentMonth", Calendar.MONTH).apply();
        editor.commit();
        try {
            editor.putString("Doc", "log" + String.valueOf(docNumber)).apply();
            editor.commit();
            String hours = addHours.getText().toString();
            String[] splitedHours = hours.split("\\.");

            if(splitedHours.length == 1){
                String[] newSplittedHours = new String[2];
                newSplittedHours[0] = splitedHours[0];
                newSplittedHours[1] = "00";

                splitedHours = newSplittedHours;
            }

            String currentDate = getCurrentDate();

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

                editor.putInt("Hours", spHours).apply();
                editor.commit();
                editor.putInt("Minutes", spMinutes).apply();
                editor.commit();

                int hoursToDisplay = sharedPreferences.getInt("Hours", 0);
                int minutesToDisplay = sharedPreferences.getInt("Minutes", 0);

                if(minutesToDisplay < 10) {
                    monthHoursText.setText(hoursToDisplay + ".0" + minutesToDisplay);
                } else {
                    monthHoursText.setText(hoursToDisplay + "." + minutesToDisplay);
                }

                try {
                    createLog(currentDate, splitedHours[0], splitedHours[1]);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                showErrorMinutesDialog();
            }
        } catch (Exception e){
            e.printStackTrace();
            showFormatDialog();
        }

    }

    public void createHistoryLog(){
        String month;

        switch (currentMonth){
            case 0:
                month = "January";
                break;
            case 1:
                month = "February";
                break;
            case 2:
                month = "March";
                break;
            case 3:
                month = "April";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "June";
                break;
            case 6:
                month = "July";
                break;
            case 7:
                month = "August";
                break;
            case 8:
                month = "September";
                break;
            case 9:
                month = "October";
                break;
            case 10:
                month = "November";
                break;
            case 11:
                month = "December";
                break;
            default:
                month = "Last month";
                break;
        }

        String historyLog = "Month: " + month + ", worked hours: " + spLastHours + "." + spLastMinutes + ", salary: " + salary;

        editor.putString("HistoryDoc", "log" + String.valueOf(historyDocNum)).apply();
        editor.commit();

        historyDocNum += 1;
        editor.putInt("HistoryDocNum", historyDocNum).apply();
        editor.commit();

        String historyDocName = sharedPreferences.getString("HistoryDoc", null);

        docRef = FirebaseFirestore.getInstance().document("historyLogs" + android_id + "/" + historyDocName);

        docRef.set(historyLog).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.i(TAG, "Failed!", e);
            }
        });
    }

    public void createLog(String date, String hours, String minutes){
        WorkLog log = new WorkLog(date, hours, minutes);

        Map<String, Object> logToSave = new HashMap<String, Object>();
        logToSave.put(DATE_KEY, log.getDate());
        logToSave.put(HOURS_KEY, log.getHours());
        logToSave.put(MINUTES_KEY, log.getMinutes());
        logToSave.put(ACTION_KEY, action);

        String docName = sharedPreferences.getString("Doc", null);
        docRef = FirebaseFirestore.getInstance().document("logs" + android_id + "/" + docName);

        docRef.set(logToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.i(TAG, "Failed!", e);
            }
        });

        docNumber += 1;
        editor.putInt("DocNum", docNumber).apply();
        editor.commit();
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
        if(lang.equals("pl")){
            alertDialog.setTitle("Uwaga");
            alertDialog.setMessage("Użyj wskazanego formatu");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Use shown format");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        alertDialog.show();
    }

    public void showNegativeNumberDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        if(lang.equals("pl")){
            alertDialog.setTitle("Uwaga");
            alertDialog.setMessage("Czas pracy poniżej zera jest niemożliwy");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Working time below 0 is not possible");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        alertDialog.show();
    }

    public void showErrorMinutesDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        if(lang.equals("pl")){
            alertDialog.setTitle("Uwaga");
            alertDialog.setMessage("Proszę użyć liczby minut z przedziału 0-59");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Please, use range of minutes between 0-59");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        alertDialog.show();
    }

    public void resetSharedPreferences(){
        editor.putInt("Hours", 0).apply();
        editor.commit();
        editor.putInt("Minutes", 0).apply();
        editor.commit();
        editor.putInt("DocNum", 0).apply();
        editor.commit();
        editor.putString("Currency", "").apply();
        editor.commit();
        editor.putString("Salary", "0").apply();
        editor.commit();
    }

    public void setLanguage(){
        switch(lang){
            case "pl":
                addButton.setText("Dodaj godziny");
                deleteButton.setText("Usuń godziny");
                manageHoursOptions.setText("Zarządzaj godzinami");
                addHours.setHint("4.15 (godz.min)");
                deleteHours.setHint("4.15 (godz.min)");
                hoursLabel.setText("Obecny miesiąc");
                break;
            case "eng":
                addButton.setText("Add hours");
                deleteButton.setText("Delete hours");
                manageHoursOptions.setText("Manage hours");
                addHours.setHint("4.15 (hours.minutes)");
                deleteHours.setHint("4.15 (hours.minutes)");
                hoursLabel.setText("This month");
                break;
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
            case R.id.historyScreen:
                Intent historyScreen = new Intent(this, HistoryActivity.class);
                startActivity(historyScreen);
                break;
        }
        return true;
    }
}