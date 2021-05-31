package com.example.workhours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InfoActivity extends AppCompatActivity {

    SharedPreferences preferences;

    TextView informationTextView;
    TextView textInformation;
    TextView supportTextView;
    TextView linkTextView;
    TextView contactTextView;

    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        preferences = getSharedPreferences("com.example.workhours", MODE_PRIVATE);

        lang = preferences.getString("Language", "eng");

        informationTextView = findViewById(R.id.informationTextView);
        textInformation = findViewById(R.id.textInformation);
        supportTextView = findViewById(R.id.supportTextView);
        linkTextView = findViewById(R.id.linkTextView);
        contactTextView = findViewById(R.id.contactTextView);

        setLanguage();

    }

    public void setLanguage(){
        switch (lang){
            case "pl":
                informationTextView.setText("Informacje");
                textInformation.setText("Dziękuję za pobranie mojej aplikacji! Jeśli znajdziesz jakiś problem, nie wahaj się napisać do mnie w tej sprawie (informacje kontaktowe poniżej).");
                supportTextView.setText("Jeśli chcesz wesprzeć moją pracę, możesz to zrobić na Patronite");
                linkTextView.setText("Link już niedługo");
                contactTextView.setText("Konatkt");
                break;
            case "eng":
                informationTextView.setText("Informations");
                textInformation.setText("Thank you for installing my app! If you find any problems with it, do not hesitate to email me (contact info below).");
                supportTextView.setText("If you want to support my work, you can do it on Patronite");
                linkTextView.setText("Link soon");
                contactTextView.setText("Contact");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
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