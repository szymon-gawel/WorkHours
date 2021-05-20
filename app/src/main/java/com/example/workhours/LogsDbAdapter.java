package com.example.workhours;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LogsDbAdapter {
    private static final String DEBUG_TAG = "SqLiteLogsManager";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_LOGS_TABLE = "logs";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_DATE = "date";
    public static final String DATE_OPTIONS = "TEXT NOT NULL";
    public static final int DATE_COLUMN = 1;
    public static final String KEY_HOURS = "hours";
    public static final String HOURS_OPTIONS = "TEXT NOT NULL";
    public static final int HOURS_COLUMN = 2;
    public static final String KEY_MINUTES = "minutes";
    public static final String MINUTES_OPTIONS = "TEXT NOT NULL";
    public static final int MINUTES_COLUMN = 3;

    private static final String DB_CREATE_LOGS_TABLE =
            "CREATE TABLE " + DB_LOGS_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_DATE + " " + DATE_OPTIONS + ", " +
                    KEY_HOURS + " " + HOURS_OPTIONS + ", " +
                    KEY_MINUTES + " " + MINUTES_OPTIONS +
                    ");";
    private static final String DROP_LOGS_TABLE =
            "DROP TABLE IF EXISTS " + DB_LOGS_TABLE;

    public LogsDbAdapter(Context context) {
        this.context = context;
    }

    public LogsDbAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertLog(String date, String hours, String minutes) {
        ContentValues newTodoValues = new ContentValues();
        newTodoValues.put(KEY_DATE, date);
        newTodoValues.put(KEY_HOURS, hours);
        newTodoValues.put(KEY_MINUTES, minutes);
        return db.insert(DB_LOGS_TABLE, null, newTodoValues);
    }

    public boolean deleteLog(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_LOGS_TABLE, where, null) > 0;
    }

    public Cursor getAllLogs() {
        String[] columns = {KEY_ID, KEY_DATE, KEY_HOURS, KEY_MINUTES};
        return db.query(DB_LOGS_TABLE, columns, null, null, null, null, null);
    }

    public WorkLog getLog(long id) {
        String[] columns = {KEY_ID, KEY_DATE, KEY_HOURS, KEY_MINUTES};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_LOGS_TABLE, columns, where, null, null, null, null);
        WorkLog log = null;
        if(cursor != null && cursor.moveToFirst()) {
            String date = cursor.getString(DATE_COLUMN);
            String hours = cursor.getString(HOURS_COLUMN);
            String minutes = cursor.getString(MINUTES_COLUMN);
            log = new WorkLog(id, date, hours, minutes);
        }
        return log;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_LOGS_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_LOGS_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_LOGS_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_LOGS_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }
}
