package com.example.workhours;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class LogsAdapter extends ArrayAdapter<WorkLog> {
    private Activity context;
    private List<WorkLog> logs;

    public LogsAdapter(Activity context, List<WorkLog> logs){
        super(context, R.layout.work_log_list_item, logs);
        this.context = context;
        this.logs = logs;
    }
}
