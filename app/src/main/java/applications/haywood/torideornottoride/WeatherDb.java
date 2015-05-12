package applications.haywood.torideornottoride;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.google.gson.Gson;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

// TODO: Consolidate db open/close so it isn't repeated unecessarily.
// TODO: Investigate implemnting as a singleton, will need to keep track of # of references, and only close
// db when last use calls close.
// All DB abstraction has to be here, there should be no Cursor uses outside of this class, there
// should be no references to columns, or any other db specific entities outside of this class.
// this allows the DB schema to be completely transparent to the users.
public class WeatherDb extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "weatherdb";
    private static final int DATABASE_VERSION = 7;

    private static final String WEATHER_TABLE_NAME = "weather";
    private static final String ZIPCODES_TABLE_NAME = "zipcodes";

    private static final String TAG = "WeatherDb";

    // Column indexes.
    // Weather table
    private static final int WEATHER_ZIPCODE = 0;
    private static final int WEATHER_JSON = 1;
    private static final int WEATHER_LASTUPDATE = 2;
    private static final int WEATHER_HOUR = 3;
    private static final int WEATHER_MINUTE = 4;

    // Zipcode table
    private static final int ZIPCODE_ZIPCODE = 0;
    private static final int ZIPCODE_LATITUDE = 1;
    private static final int ZIPCODE_LONGITUDE = 2;
    private static final int ZIPCODE_CITY = 3;


    private SQLiteDatabase sqLiteDatabaseReadable;
    private SQLiteDatabase sqLiteDatabaseWritable;
    private Gson gson;

    public WeatherDb(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // This needs to be before any db access, and should only be used if any data in the phones older db can be lost.
        setForcedUpgrade();

        // Open as writeable first so it can be upgraded if needed.
        this.sqLiteDatabaseWritable = getWritableDatabase();
        this.sqLiteDatabaseReadable = getReadableDatabase();

        this.gson = new Gson();
    }

    private Cursor GetZipCodesTable()
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "Latitude", "Longitude", "City"};
        String sqlTables = WeatherDb.ZIPCODES_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(this.sqLiteDatabaseReadable, sqlSelect, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    public List<String> GetZipCodesStrings() {
        Cursor zipCodesTable = this.GetZipCodesTable();
        List<String> zipCodeStrings = new ArrayList<String>();

        // Prepare variables to store column data
        String zipCode = "";
        Float latitude = 0.0F; // Using Float as it is nullable and this value is from a database.
        Float longitude = 0.0F; // Using Float as it is nullable and this value is from a database.
        String city = "";

        String latitudeString = "";
        String longitudeString = "";

        // Load zipcodes into table.
        while (!zipCodesTable.isAfterLast()) {
            zipCode = String.format("%05d", zipCodesTable.getInt(0));
            latitude = zipCodesTable.getFloat(1);
            longitude = zipCodesTable.getFloat(2);
            city = zipCodesTable.getString(3);

            // If latitude or longitude is set to an invalid value or null, it is unknown.
            latitudeString = (Math.abs(latitude) > 90 || latitude == null) ? "Unknown" : Float.toString(latitude);
            longitudeString = (Math.abs(longitude) > 180 || longitude == null) ? "Unknown" : Float.toString(longitude);

            zipCodeStrings.add(String.format("%s %s %s %s", zipCode, city, latitudeString, longitudeString));

            zipCodesTable.moveToNext();
        }

        return zipCodeStrings;
    }

    private Cursor GetWeatherTable() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlProjection = {"_id", "WeatherJSON", "LastUpdate", "Hour", "Minute"};
        String sqlTables = WeatherDb.WEATHER_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(this.sqLiteDatabaseReadable, sqlProjection, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    public ZipCodeWeather GetWeatherForecast(int zipCode, int hour, int minute) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        ZipCodeWeather zipCodeWeather = new ZipCodeWeather();

        String[] sqlProjection = {"_id", "WeatherJSON", "LastUpdate", "Hour", "Minute"};
        String sqlSelection = String.format("_id = %s AND Hour = %s AND Minute = %s", zipCode, hour, minute);
        String sqlTables = WeatherDb.WEATHER_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(this.sqLiteDatabaseReadable, sqlProjection, sqlSelection, null, null, null, null);

        if (cursor.getCount() != 0) {

            cursor.moveToFirst();

            zipCodeWeather = this.gson.fromJson(cursor.getString(WeatherDb.WEATHER_JSON), ZipCodeWeather.class);
            zipCodeWeather.setHour(cursor.getInt(WeatherDb.WEATHER_HOUR));
            zipCodeWeather.setMinute(cursor.getInt(WeatherDb.WEATHER_MINUTE));
            zipCodeWeather.setLastUpdate(cursor.getInt(WeatherDb.WEATHER_LASTUPDATE));
        }

        return zipCodeWeather;
    }

    public long AddWeatherForecast(int zipCode, String weatherJson, int hour, int minute) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", zipCode);
        contentValues.put("WeatherJSON", weatherJson);
        contentValues.put("LastUpdate", (System.currentTimeMillis() / 1000)); // Convert milliseconds to seconds.
        contentValues.put("Hour", hour);
        contentValues.put("Minute", minute);

        return this.sqLiteDatabaseWritable.insert(WeatherDb.WEATHER_TABLE_NAME, null, contentValues);
    }

    private Cursor GetZipCodeRecord(int zipCode) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlProjection = {"_id", "Latitude", "Longitude", "City"};
        String sqlSelection = String.format("_id = %s", zipCode);
        String sqlTables = WeatherDb.ZIPCODES_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(this.sqLiteDatabaseReadable, sqlProjection, sqlSelection, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    // Returns the coordinates of the zipcode in the format "latitude,longitude"
    public String GetZipCodeCoordinates(int zipCode) {
        Cursor cursor = this.GetZipCodeRecord(zipCode);
        Log.d(WeatherDb.TAG, "Getting Coordinates for zipcode:" + cursor.getInt(WeatherDb.ZIPCODE_ZIPCODE));
        Log.d(WeatherDb.TAG, "Getting Coordinates for city:" + cursor.getString(WeatherDb.ZIPCODE_CITY));
        Log.d(WeatherDb.TAG, "Latitude for city:" + cursor.getFloat(WeatherDb.ZIPCODE_LATITUDE));
        Log.d(WeatherDb.TAG, "Longitude for city:" + cursor.getFloat(WeatherDb.ZIPCODE_LONGITUDE));

        String coordinates = String.format("%s,%s",
                cursor.getFloat(WeatherDb.ZIPCODE_LATITUDE),
                cursor.getFloat(WeatherDb.ZIPCODE_LONGITUDE));

        Log.d(WeatherDb.TAG, "Coordinates for city:" + coordinates);

        return coordinates;
    }

    public List<String> GetWeatherStrings() {
        List<String> zipCodeWeatherStrings = new ArrayList<String>();

        Cursor weatherTable = this.GetWeatherTable();

        // Prepare variables to store column data
        String zipCode = "";
        String weather = "";
        int lastUpdate = 0;

        ZipCodeWeather zipCodeWeather;

        // Load zipcodes into table.
        while (!weatherTable.isAfterLast()) {
            zipCode = String.format("%05d", weatherTable.getInt(WEATHER_ZIPCODE));
            weather = weatherTable.getString(WEATHER_JSON);
            lastUpdate = weatherTable.getInt(WEATHER_LASTUPDATE);

            zipCodeWeather = gson.fromJson(weather, ZipCodeWeather.class);
            zipCodeWeatherStrings.add(String.format("%s %s %s%%",
                    zipCode,
                    zipCodeWeather.getCurrently().getSummary(),
                    zipCodeWeather.getCurrently().getPrecipProbability()));

            weatherTable.moveToNext();
        }

        return zipCodeWeatherStrings;
    }
}