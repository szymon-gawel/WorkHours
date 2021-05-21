package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogActivity extends AppCompatActivity {

    private ListView listView;
    private SQLiteDatabase database;
    List<WorkLog> logs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        listView = (ListView) findViewById(R.id.logsView);
        logs = getLogsFromDatabase();

        ArrayAdapter<WorkLog> adapter = new ArrayAdapter<WorkLog>(this, android.R.layout.list_content, logs);
        listView.setAdapter(adapter);
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