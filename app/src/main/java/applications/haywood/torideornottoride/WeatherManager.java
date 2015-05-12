package applications.haywood.torideornottoride;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ahaywood on 5/10/2015.
 */
public class WeatherManager {

    private static final String SERVER_URL = "http://api.forecast.io/forecast/";
    private static final String SERVER_OPTIONS = "?exclude=[minutely,hourly,daily,alerts,flags]";

    // If a weather record is older than this many seconds, it will be updated, instaed of pulled from the db only.
    private static final long AGE_TO_UPDATE = 60;
    private static final String TAG = "WeatherManager";
    //Keep track of how many fetches are pending.
    private static int numberOfFetches = 0;
    private static WeakReference<Activity> modifyActivityWeakReference;
    private ZipCodeWeather zipCodeWeatherForecast;
    private Cursor zipCodeTable;
    private WeatherDb weatherDb;
    private Resources resources;
    private String jsonString;
    private int zipCode;
    private int hour;
    private int minute;

    public WeatherManager(Context context) {
        resources = context.getResources();
        weatherDb = new WeatherDb(MainActivity.GetMyContext());
    }

    public static void UpdateModifyActivity(Activity activity) {
        modifyActivityWeakReference = new WeakReference<Activity>(activity);
    }

    // Will first check the local database for a matching weather forecast.
    // If a match is found, and recent, it will simply return that.
    // If a match is found, but stale, it will pull the latest forecast data, update the db.
    // If a match is not found, it will pull the latest forecast data, add it to the db.
    public void GetWeather(int zipCode, int hour, int minute) {

        this.zipCode = zipCode;
        this.hour = hour;
        this.minute = minute;

        zipCodeWeatherForecast = weatherDb.GetWeatherForecast(zipCode, hour, minute);

        Log.d(WeatherManager.TAG, "Checking database for forecast data for: " + zipCode + " " + hour + minute);

        if (zipCodeWeatherForecast.getCurrently() == null) {
            Log.d(WeatherManager.TAG, "No existing forecast found");

            ForecastFetcher forecastFetcher = new ForecastFetcher();
            forecastFetcher.SetForecastParameters(zipCode, hour, minute);
            forecastFetcher.execute();
        } else {
            if (this.LastUpdateIsOld(zipCodeWeatherForecast.getLastUpdate())) {
                Log.d(WeatherManager.TAG, "old weather found, updating.");

                ForecastFetcher forecastFetcher = new ForecastFetcher();
                forecastFetcher.SetForecastParameters(zipCode, hour, minute);
                forecastFetcher.execute();
            }
        }
    }

    private boolean LastUpdateIsOld(int epochInSeconds) {
        Calendar timeNow = Calendar.getInstance();
        long nowEpochInSeconds = (timeNow.getTime().getTime() / 1000);
        long timeDifference = (nowEpochInSeconds - (long) epochInSeconds);
        Log.d(WeatherManager.TAG, "Time Difference is: " + timeDifference);

        // If time is older than the desired amount, return true.
        return (timeDifference > WeatherManager.AGE_TO_UPDATE);
    }

    private void handlePostsJson(String jsonString) {
        this.jsonString = jsonString;

        weatherDb.UpdateWeatherForecast(this.zipCode,
                this.jsonString,
                this.hour,
                this.minute);

        Log.e(TAG, "JSON Handled");

        // Update list on Modify screen, only call if ModifyActivity is available.
        if (WeatherManager.modifyActivityWeakReference != null) {
            final ModifyActivity modifyActivity = (ModifyActivity) WeatherManager.modifyActivityWeakReference.get();

            modifyActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    modifyActivity.PopulateWeatherData();
                }
            });
        }
    }

    public void UpdateAllWeather() {
        List<ZipCodeWeather> zipCodeWeatherList = this.weatherDb.GetAllWeatherForecast();

        for (int i = 0; i < zipCodeWeatherList.size(); i++) {
            this.GetWeather(
                    zipCodeWeatherList.get(i).getZipCode(),
                    zipCodeWeatherList.get(i).getHour(),
                    zipCodeWeatherList.get(i).getMinute());
        }
    }

    public int GetMaxChanceOfRain() {
        List<ZipCodeWeather> zipCodeWeatherList = this.weatherDb.GetAllWeatherForecast();
        ZipCodeWeather zipCodeWeather;

        float maxPercentChanceRain = 0;
        float checkingPercentChanceRain = 0;
        for (int i = 0; i < zipCodeWeatherList.size(); i++) {
            zipCodeWeather = zipCodeWeatherList.get(i);
            checkingPercentChanceRain = zipCodeWeather.getCurrently().getPrecipProbability().floatValue();
            Log.d(WeatherManager.TAG, "Checking against: " + checkingPercentChanceRain);
            Log.d(WeatherManager.TAG, "Checking max: " + maxPercentChanceRain);
            if (checkingPercentChanceRain > maxPercentChanceRain) {
                maxPercentChanceRain = checkingPercentChanceRain;
            }
        }

        Log.d(WeatherManager.TAG, "Resulting max: " + maxPercentChanceRain * 100);

        return (int) (maxPercentChanceRain * 100);
    }

    private class ForecastFetcher extends AsyncTask<Void, Void, String> {

        private static final String TAG = "ForecastFetcher";

        String jsonString = "";
        private int zipCode;
        private int hour;
        private int minute;

        public void SetForecastParameters(int zipCode, int hour, int minute) {
            this.zipCode = zipCode;
            this.hour = hour;
            this.minute = minute;
        }

        // TODO: Add automatic retry if http connection fails.
        // TODO: https was not working, switched to http, fix and use https again.
        @Override
        protected String doInBackground(Void... params) {
            try {
                WeatherManager.numberOfFetches++;

                // Queue up if there is more than one fetch being processed.
                while (WeatherManager.numberOfFetches > 1) {
                    Log.d(ForecastFetcher.TAG, "Waiting for other fetches to complete");

                    try {
                        Thread.currentThread().sleep(1000); // sleep for 1 second.
                    } catch (InterruptedException ie) {
                        Log.i(ForecastFetcher.TAG, "Thread interupted");
                    }
                }

                // Create an HTTP client
                HttpClient httpClient = new DefaultHttpClient();
                String url = this.BuildUrl(zipCode, hour, minute);
                HttpPost httpPost = new HttpPost(url);

                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity httpEntity = response.getEntity();
                    InputStream inputStream = httpEntity.getContent();

                    try {
                        // Read teh server resposne and attempt to parse it as JSON
                        Reader reader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        jsonString = bufferedReader.readLine();
                        inputStream.close();

                        Log.e(TAG, "Got JSON: " + jsonString);
                        handlePostsJson(jsonString);
                        WeatherManager.numberOfFetches--;

                    } catch (Exception exception) {
                        Log.e(TAG, "Failed get JSON due to: " + exception);
                        WeatherManager.numberOfFetches--;
                    }
                }
            } catch (java.io.IOException exception) {
                Log.e(WeatherManager.TAG, "Failed to send HTTP POST request due to: " + exception);
                WeatherManager.numberOfFetches--;
            }

            return null;
        }

        private String BuildUrl(int zipCode, int hour, int minute) {
            // Combine all values to create the relevant URL to obtain the weather forecast data.
            String apikey = resources.getString(R.string.forecast_apikey);
            String coordinates = weatherDb.GetZipCodeCoordinates(zipCode);
            String epochTime = this.GmtGetEpochTimeInSeconds(hour, minute);

            String url = String.format("%s%s/%s,%s%s",
                    WeatherManager.SERVER_URL,
                    apikey,
                    coordinates,
                    epochTime,
                    WeatherManager.SERVER_OPTIONS);

            return url;
        }

        // TODO: Write a unit test for this.
        // Returns string representing Epoch time in seconds.
        private String GmtGetEpochTimeInSeconds(int hour, int minute) {
            // Create a calendar object representing the current time.
            Calendar calendarNow = Calendar.getInstance();

            Log.d(WeatherManager.TAG, "Calendar Now: " + calendarNow.toString());

            // Create a calendar object representing the time input.
            Calendar calendarInput = Calendar.getInstance();
            calendarInput.set(Calendar.HOUR_OF_DAY, hour);
            calendarInput.set(Calendar.MINUTE, minute);

            Log.d(WeatherManager.TAG, "Calendar Input: " + calendarInput.toString());

            // If the time input has past get the time for tomorrow.
            if (calendarNow.after(calendarInput)) {
                calendarInput.roll(Calendar.DAY_OF_YEAR, 1);
                Log.d(WeatherManager.TAG, "Updated Calendar Input: " + calendarInput.toString());
            }

            long epochTimeInMilliseconds = calendarInput.getTime().getTime();
            long epochTimeInSeconds = epochTimeInMilliseconds / 1000;

            Log.d(WeatherManager.TAG, "Epoch time in milliseconds: " + epochTimeInMilliseconds);
            Log.d(WeatherManager.TAG, "Epoch time in seconds: " + epochTimeInSeconds);

            // epoch time in seconds.
            return String.valueOf(epochTimeInSeconds);
        }
    }

}
