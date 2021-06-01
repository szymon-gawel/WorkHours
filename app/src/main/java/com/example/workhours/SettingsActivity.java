package com.example.workhours;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch themeSwitch;
    private Spinner languageSpinner;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore database;

    private TextView settingsTextView;
    private TextView changeThemeTextView;
    private TextView lightTextView;
    private TextView darkTextView;
    private TextView selectLanguageTextView;
    private TextView resetValuesTextView;

    private String theme;
    private String lang;
    private String chooseLanguage;
    private String android_id;
    private String TAG = "DATABASE";

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);
        editor = preferences.edit();
        database = FirebaseFirestore.getInstance();

        theme = preferences.getString("Theme", "Light");
        lang = preferences.getString("Language", "eng");

        themeSwitch = findViewById(R.id.themeSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);
        settingsTextView = findViewById(R.id.settingsTextView);
        changeThemeTextView = findViewById(R.id.changeThemeTextView);
        lightTextView = findViewById(R.id.lightTextView);
        darkTextView = findViewById(R.id.darkTextView);
        selectLanguageTextView = findViewById(R.id.selectLanguageTextView);
        resetValuesTextView = findViewById(R.id.resetValuesTextView);

        String[] languages;

        setLanguage();

        languages = new String[]{chooseLanguage,"eng", "pl"};

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        editor.putString("Language", "eng").apply();
                        editor.commit();
                        lang = preferences.getString("Language", "eng");
                        setLanguage();
                        break;
                    case 2:
                        editor.putString("Language", "pl").apply();
                        editor.commit();
                        lang = preferences.getString("Language", "pl");
                        setLanguage();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        languageSpinner.setAdapter(adapter);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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
                lang = preferences.getString("Language", "eng");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putString("Theme", "Light").apply();
                editor.commit();
                lang = preferences.getString("Language", "eng");
            }
        });
    }

    public void onResetButtonClicked(View view){
        showValuesResetConfirmationDialog();
    }

    public void showValuesResetConfirmationDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        if(lang.equals("pl")){
            alertDialog.setTitle("Uwaga");
            alertDialog.setMessage("Na pewno chcesz zresetować wartości? Logi nie zostaną usunięte");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Usuń",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt("Hours", 0).apply();
                            editor.commit();
                            editor.putInt("Minutes", 0).apply();
                            editor.commit();
                            editor.putString("Currency", "").apply();
                            editor.commit();
                            editor.putString("Salary", "0").apply();
                            editor.commit();
                            editor.putString("HourlyRate", "0").apply();
                            editor.commit();
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Anuluj",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Are you sure you want to reset all values? Logs will not be deleted");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt("Hours", 0).apply();
                            editor.commit();
                            editor.putInt("Minutes", 0).apply();
                            editor.commit();
                            editor.putString("Currency", "").apply();
                            editor.commit();
                            editor.putString("Salary", "0").apply();
                            editor.commit();
                            editor.putString("HourlyRate", "0").apply();
                            editor.commit();
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        alertDialog.show();
    }

    public void setLanguage(){
        switch (lang){
            case "pl":
                settingsTextView.setText("Ustawienia");
                changeThemeTextView.setText("Zmień motyw");
                lightTextView.setText("Jasny");
                darkTextView.setText("Ciemny");
                selectLanguageTextView.setText("Wybierz język");
                resetValuesTextView.setText("Zresetuj wartości");
                chooseLanguage = "Wybierz język";
                break;
            case "eng":
                settingsTextView.setText("Settings");
                changeThemeTextView.setText("Change theme");
                lightTextView.setText("Light");
                darkTextView.setText("Dark");
                selectLanguageTextView.setText("Select language");
                resetValuesTextView.setText("Reset values");
                chooseLanguage = "Choose language";
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        if(lang.equals("eng")){
            menuInflater.inflate(R.menu.menu, menu);
        } else {
            menuInflater.inflate(R.menu.menu_pl, menu);
        }

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