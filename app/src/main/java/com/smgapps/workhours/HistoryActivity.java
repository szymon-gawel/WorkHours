package com.smgapps.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore database;
    private String android_id;

    private ListView historyListView;
    private ProgressBar loadingBar;

    private static final String TAG = "DATABASE";

    private ArrayList<String> logs;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        preferences = getSharedPreferences("com.smgapps.workhours", MODE_PRIVATE);
        editor = preferences.edit();
        database = FirebaseFirestore.getInstance();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        historyListView = findViewById(R.id.historyListView);
        loadingBar = findViewById(R.id.progressBar);

        lang = preferences.getString("Language", "eng");

        logs = new ArrayList<String>();

        database.collection("historyLogs" + android_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String documentData = document.getData().toString();
                                String[] dataSplitted = documentData.split("\\,\\s");

                                String finalLog = createHistoryLogText(dataSplitted[1], dataSplitted[0], dataSplitted[2]);

                                logs.add(finalLog);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, logs);
                            historyListView.setAdapter(adapter);
                            loadingBar.setVisibility(View.INVISIBLE);
                        } else {
                        }
                    }
                });
    }

    public String createHistoryLogText(String month, String hours, String salary){
        String finalMonth;
        String[] monthSeparated = month.split("\\=");
        finalMonth = monthSeparated[1];

        String finalHours;
        String[] hoursSeparated = hours.split("\\=");
        finalHours = hoursSeparated[1];

        String finalSalary;
        String[] salarySeparated = salary.split("\\=");
        String[] salaryFinalSeparated = salarySeparated[1].split("\\}");
        finalSalary = salaryFinalSeparated[0];

        String currency = preferences.getString("Currency", "");

        String salaryStringToShow = String.valueOf(new DecimalFormat("##.##").format(finalSalary));

        String result = "Month: " + finalMonth + ", hours: " + finalHours + ", salary: " + salaryStringToShow + " " + currency;
        return result;
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