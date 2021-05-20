package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogActivity extends AppCompatActivity {

    private ListView listView;
    private LogsDbAdapter logsDbAdapter;
    ArrayList<WorkLog> logs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        listView = (ListView) findViewById(R.id.logsView);
        logs = new ArrayList<WorkLog>();
        logsDbAdapter = new LogsDbAdapter(getApplicationContext());
        int i = 0;

        Cursor cursor = logsDbAdapter.getAllLogs();

        while(cursor.moveToNext()){
            long id = cursor.getLong(0);
            String date = cursor.getString(1);
            String hours = cursor.getString(2);
            String minutes = cursor.getString(3);
            WorkLog log = new WorkLog(id, date, hours, minutes);
            logs.add(log);
        }

        LogsAdapter adapter = new LogsAdapter(this, logs);
        listView.setAdapter(adapter);
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