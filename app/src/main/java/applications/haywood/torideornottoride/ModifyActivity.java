package applications.haywood.torideornottoride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

    private static WeakReference<Activity> mainActivityWeakReference;
    private WeatherManager weatherManager;
    private WeatherFragment weatherFragment;

    private int zipCodeToAdd;
    private int selectedHour;
    private int selectedMinute;

    public static void UpdateMainActivity(Activity activity) {
        mainActivityWeakReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        this.PopulateWeatherData();
        WeatherManager.UpdateModifyActivity(this);
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

    public void PopulateWeatherData() {
        this.weatherFragment = (WeatherFragment)
                getFragmentManager().findFragmentById(
                        R.id.weatherFragment);

        if (this.weatherFragment != null) {
            this.weatherFragment.RefreshView();
        }
    }

    // This method is automatically called when the GetZipCode activity completes.
    // This facilitates getting and updating the title with the username entered.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ModifyActivity.ZIPCODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Retrieve the zipcode stored in the shared preferences.
                SharedPreferences sharedPreferences = getSharedPreferences(ModifyActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                this.zipCodeToAdd = Integer.parseInt(sharedPreferences.getString(ModifyActivity.ZIPCODE_PREFERENCE, null));
                this.selectedHour = sharedPreferences.getInt(ModifyActivity.HOUR_PREFERENCE, 0);
                this.selectedMinute = sharedPreferences.getInt(ModifyActivity.MINUTE_PREFERENCE, 0);

                this.weatherManager.GetWeather(this.zipCodeToAdd, this.selectedHour, this.selectedMinute);

                //this.PopulateWeatherData();
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
        MainActivity mainActivity = (MainActivity) ModifyActivity.mainActivityWeakReference.get();

        mainActivity.ShowForecastWebsite(view);
    }

    public void RemoveSelectedZipCodes(View view) {
        List<String> itemsToRemove = new ArrayList<String>();

        WeatherDb weatherDb = new WeatherDb(this);

        ListView listView = this.weatherFragment.GetListView();
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();

        // Return the same strings that the WeatherDb provides, so it knowns which items to delete.
        // TODO: Investigate more robust method.
        for (int i = 0; i < listView.getCount(); i++) {
            if (checkedItems.get(i)) {
                itemsToRemove.add((String) listView.getItemAtPosition(i));
            }
        }

        weatherDb.RemoveWeatherItems(itemsToRemove);

        if (this.weatherFragment != null) {
            this.weatherFragment.RefreshView();
        }
    }
}
