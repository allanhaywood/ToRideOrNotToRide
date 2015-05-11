package applications.haywood.torideornottoride;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;

/**
 * Created by ahaywood on 5/10/2015.
 */
public class WeatherManager {

    // NOTE: Find a way to not push the actual API key to github, and only fill it in at build time.
    private static final String SERVER_URL = "https://api.forecast.io/forecast/";
    private static final String SERVER_OPTIONS = "?exclude=[minutely,hourly,daily,alerts,flags]";

    private static final String TAG = "WeatherManager";

    private ZipCodeWeather zipCodeWeatherForecast;
    private Cursor zipCodeTable;
    private WeatherDb weatherDb;
    private Resources resources;

    public WeatherManager(Context context) {
        resources = context.getResources();
        weatherDb = new WeatherDb(MainActivity.GetMyContext());
    }

    // Will first check the local database for a matching weather forecast.
    // If a match is found, and recent, it will simply return that.
    // If a match is found, but stale, it will pull the latest forecast data, update the db and return that.
    // If a match is not found, it will pull the latest forecast data, add it to the db, and return that.
    public ZipCodeWeather GetWeather(int zipCode, int hour, int minute) {
        ZipCodeWeather zipCodeWeather;

        zipCodeWeatherForecast = weatherDb.GetWeatherForecast(zipCode, hour, minute);

        if (zipCodeWeatherForecast.getCurrently() == null) {
            weatherDb.AddWeatherForecast(zipCode,
                    this.GetWeatherForecast(zipCode, hour, minute),
                    hour,
                    minute);

            zipCodeWeatherForecast = weatherDb.GetWeatherForecast(zipCode, hour, minute);
        }

        return zipCodeWeatherForecast;
    }

    // Will pull the latest JSON string from forecast.io for the specified zipCode, hour, and minute.
    // If the specified hour and minute has not past for the day yet, it will pull today's forecast for that time.
    // If the specified hour and minute has already past for the day, it will pull tomorrows forecast for that time.
    // TODO: Change this to an asynchronous call.
    private String GetWeatherForecast(int zipCode, int hour, int minute) {
        String jsonString = "";

        try {
            // Create an HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(this.BuildUrl(zipCode, hour, minute));

            HttpResponse response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity httpEntity = response.getEntity();
                InputStream inputStream = httpEntity.getContent();

                try {
                    // Read teh server resposne and attempt to parse it as JSON
                    Reader reader = new InputStreamReader(inputStream);
                    jsonString = reader.toString();
                    inputStream.close();


                } catch (Exception exception) {
                    Log.e(TAG, "Failed get JSON due to: " + exception);
                }
            }
        } catch (java.io.IOException exception) {
            Log.e(WeatherManager.TAG, "Failed to send HTTP POST request due to: " + exception);
        }

        return jsonString;
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
