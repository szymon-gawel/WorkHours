package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SalaryActivity extends AppCompatActivity {

    EditText hourlyRate;
    TextView salaryTextView;
    EditText currencyEditText;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    int spHours;
    int spMinutes;
    double salary;
    String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        preferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);
        editor = preferences.edit();

        spHours = preferences.getInt("Hours", 0);
        spMinutes = preferences.getInt("Minutes", 0);
        currency = preferences.getString("Currency", "");

        hourlyRate = findViewById(R.id.hourlyRate);
        salaryTextView = findViewById(R.id.salaryAmountTextView);
        currencyEditText = findViewById(R.id.currencyEditText);

        String salarySP = preferences.getString("Salary", "0");

        salaryTextView.setText(salarySP);
    }

    public void onCalculateButtonClick(View view){
        try {
            String hourlyRateString = hourlyRate.getText().toString();

            salary = Double.parseDouble(hourlyRateString) * (spHours + spMinutes/60);
            salaryTextView.setText(String.valueOf(new DecimalFormat("##.##").format(salary)) + " " + currency);
            editor.putString("Salary", String.valueOf(salary)).apply();
            editor.commit();
        } catch (Exception e){
            e.printStackTrace();
            showEmptyErrorDialog();
        }
    }

    public void onApplyButtonClick(View view){
        String currencyString = currencyEditText.getText().toString();
        editor.putString("Currency", currencyString).apply();
        editor.commit();
        currency = currencyString;
    }

    public void showEmptyErrorDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(SalaryActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Please, type your hourly rate correctly");
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
        }

        return true;
    }
}