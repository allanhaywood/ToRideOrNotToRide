package applications.haywood.torideornottoride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.WeakReference;


public class ModifyActivity extends ActionBarActivity {

    // Request code used to get results back from GetZipCodeActivity
    public static final int ZIPCODE_REQUEST_CODE = 0;

    // Name of shared preferences store shared across activities.
    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";

    // Name of the zipCode preferences in the preferences store.
    public static final String ZIPCODE_PREFERENCE = "ZIPCODE_PREFERENCE";

    // Name of the time preferences in the preferences store.
    public static final String HOUR_PREFERENCE = "HOUR_PREFERENCE";

    // Name of the time preferences in the preferences store.
    public static final String MINUTE_PREFERENCE = "MINUTE_PREFERENCE";

    private static WeakReference<Activity> activityWeakReference;
    private WeatherManager weatherManager;

    private int zipCodeToAdd;
    private int selectedHour;
    private int selectedMinute;

    public static void updateMainActivity(Activity activity) {
        activityWeakReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        this.PopulateWeatherData();
        weatherManager = new WeatherManager(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void PopulateWeatherData() {
        WeatherFragment weatherFragment = (WeatherFragment)
                getFragmentManager().findFragmentById(
                        R.id.weatherFragment);

        weatherFragment.RefreshView();
    }

    // This method is automatically called when the GetUserName activity completes.
    // This facilitates getting and updating the title with the username entered.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ModifyActivity.ZIPCODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Retrieve the zipcode stored in the shared preferences.
                SharedPreferences sharedPreferences = getSharedPreferences(ModifyActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                this.zipCodeToAdd = Integer.parseInt(sharedPreferences.getString(ModifyActivity.ZIPCODE_PREFERENCE, null));
                this.selectedHour = sharedPreferences.getInt(ModifyActivity.HOUR_PREFERENCE, 0);
                this.selectedMinute = sharedPreferences.getInt(ModifyActivity.MINUTE_PREFERENCE, 0);

                ZipCodeWeather zipCodeWeather = this.weatherManager.GetWeather(this.zipCodeToAdd, this.selectedHour, this.selectedMinute);

                this.PopulateWeatherData();
            }
        }
    }

    public void GetZipCode(View view) {
        // Creates an new intent to allow the next activity to be started.
        String zipCode = "";

        Intent intent = new Intent(ModifyActivity.this, GetZipCodeActivity.class);

        // Using startActivityForResult so that the zipcode and time entered can be used.
        startActivityForResult(intent, ModifyActivity.ZIPCODE_REQUEST_CODE);
    }

    public void ShowForecastWebsite(View view) {
        MainActivity mainActivity = (MainActivity) ModifyActivity.activityWeakReference.get();

        mainActivity.ShowForecastWebsite(view);
    }
}
