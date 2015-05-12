package applications.haywood.torideornottoride;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public static final String FORECAST_URL = "http://forecast.io/";

    private static Context myContext;
    private TextView statusTextView;

    public static Context GetMyContext() {
        return myContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ModifyActivity.UpdateMainActivity(this);
        myContext = this.getApplicationContext();
        View myView = getWindow().getDecorView().findViewById(android.R.id.content);
        this.UpdateAllWeather(myView);
    }

    public void UpdateAllWeather(View view) {
        WeatherManager weatherManager = new WeatherManager(this);
        weatherManager.UpdateAllWeather();
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        Integer maxChanceRain = weatherManager.GetMaxChanceOfRain();
        statusTextView.setText(String.format("%s%% Chance of Precipitation", maxChanceRain));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Starts the Details activity.
    public void ShowDetails(View view)
    {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

        startActivity(intent);
    }

    // Starts the Modify activity.
    public void ShowModify(View view)
    {
        Intent intent = new Intent(MainActivity.this, ModifyActivity.class);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_zipCodes:
                this.ShowAllZipCodes();
                return true;
            case R.id.action_settings:
                this.ShowSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowAllZipCodes()
    {
        Intent intent = new Intent(MainActivity.this, ZipCodesActivity.class);

        startActivity(intent);
    }

    public void ShowSettings()
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

        startActivity(intent);

    }

    public void ShowForecastWebsite(View view) {
        // Get the string from the URL textBox.
        String url = MainActivity.FORECAST_URL;

        // If the URL already starts wtih http:// or https:// there is nothing to do.
        if ((url.startsWith("http://")) || (url.startsWith("https://"))) {
            // Already full URL, do nothing.
        }
        // Otherwise, if it was not provided, add http://
        else {
            url = "http://" + url;
        }

        // Start the default web browser with the URL provided.
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);

        finish();
    }
}
