package com.smgapps.workhours;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LogsActivity extends AppCompatActivity {

    private FirebaseFirestore database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ListView logsView;
    private String android_id;
    private ProgressBar loadingBar;
    private boolean isLoaded;
    private String lang;

    private static final String TAG = "DATABASE";

    private ArrayList<String> logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        preferences = getSharedPreferences("com.smgapps.workhours", MODE_PRIVATE);
        editor = preferences.edit();

        database = FirebaseFirestore.getInstance();
        logsView = findViewById(R.id.logsView);
        loadingBar = findViewById(R.id.loadingBar);
        isLoaded = false;
        logs = new ArrayList<String>();
        lang = preferences.getString("Language", "eng");
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        database.collection("logs" + android_id)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String documentData = document.getData().toString();
                                String[] splittedDocument = documentData.split("\\,\\s");

                                String completeLog;

                                String finalLog;

                                if(splittedDocument.length == 6){
                                    completeLog = createLogText(splittedDocument[0], splittedDocument[1], splittedDocument[2], splittedDocument[3], splittedDocument[4], splittedDocument[5]);
                                    finalLog = completeLog;
                                } else {
                                    completeLog = createLogText(splittedDocument[0], splittedDocument[1], splittedDocument[2], splittedDocument[3]);

                                    String[] finalLogString = completeLog.split("\\}");
                                    finalLog = finalLogString[0] + finalLogString[1];
                                }

                                logs.add(finalLog);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(LogsActivity.this, android.R.layout.simple_list_item_1, logs);
                            logsView.setAdapter(adapter);
                            loadingBar.setVisibility(View.INVISIBLE);
                        } else {
                        }
                    }
                });
    }

    public String createLogText(String data, String hours, String minutes, String action, String startTime, String endTime){
        String[] dataSeparated = data.split("\\=");
        String[] hoursSeparated = hours.split("\\=");
        String[] minutesSeparated = minutes.split("\\=");
        String[] actionSeparated = action.split("\\=");
        String[] startTimeSeparated = startTime.split("\\=");
        String[] endTimeSeparated = endTime.split("\\=");

        String[] endTimeFinal = endTimeSeparated[1].split("\\}");

        String result = dataSeparated[1] + ", " + actionSeparated[1] + " " + hoursSeparated[1] + ":" + minutesSeparated[1] + ", " + startTimeSeparated[1] + "-" + endTimeFinal[0];

        return result;
    }

    public String createLogText(String data, String hours, String minutes, String action){
        String[] dataSeparated = data.split("\\=");
        String[] hoursSeparated = hours.split("\\=");
        String[] minutesSeparated = minutes.split("\\=");
        String[] actionSeparated = action.split("\\=");

        String result = dataSeparated[1] + ", " + actionSeparated[1] + " " + hoursSeparated[1] + ":" + minutesSeparated[1];

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