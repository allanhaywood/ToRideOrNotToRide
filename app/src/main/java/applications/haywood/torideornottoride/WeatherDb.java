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
import java.util.Calendar;
import java.util.List;

// TODO: Consolidate db open/close so it isn't repeated unecessarily.
// db when last use calls close.
// All DB abstraction has to be here, there should be no Cursor uses outside of this class, there
// should be no references to columns, or any other db specific entities outside of this class.
// this allows the DB schema to be completely transparent to the users.
public class WeatherDb extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "weatherdb";
    private static final int DATABASE_VERSION = 13;
    private static final String WEATHER_TABLE_NAME = "weather";
    private static final String ZIPCODES_TABLE_NAME = "zipcodes";
    private static final String TAG = "WeatherDb";
    // Column indexes.
    // Weather table
    private static final int WEATHER_ID = 0;
    private static final int WEATHER_ZIPCODE = 1;
    private static final int WEATHER_JSON = 2;
    private static final int WEATHER_LASTUPDATE = 3;
    private static final int WEATHER_HOUR = 4;
    private static final int WEATHER_MINUTE = 5;
    private static final String WEATHER_ZIPCODE_COLUMN = "ZipCode";
    private static final String WEATHER_ORDERBY = "_id";
    private static final String WEATHER_ID_COLUMN = "_id";
    // Zipcode table
    private static final int ZIPCODE_ZIPCODE = 0;
    private static final int ZIPCODE_LATITUDE = 1;
    private static final int ZIPCODE_LONGITUDE = 2;
    private static final int ZIPCODE_CITY = 3;
    private static WeatherDb singleInstance;
    private Gson gson;

    // Constructor is private to enforce using getSingleInstance instead, to guarantee only one accessor of the DB.
    private WeatherDb(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // This needs to be before any db access, and should only be used if any data in the phones older db can be lost.
        setForcedUpgrade();

        this.gson = new Gson();
    }

    public static synchronized WeatherDb getSingleInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (singleInstance == null) {
            singleInstance = new WeatherDb(context.getApplicationContext());
        }
        return singleInstance;
    }

    private Cursor GetZipCodesTable(SQLiteDatabase sqLiteDatabaseWritable)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "Latitude", "Longitude", "City"};
        String sqlTables = WeatherDb.ZIPCODES_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(sqLiteDatabaseWritable, sqlSelect, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    public List<String> GetZipCodesStrings() {
        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        Cursor zipCodesTable = this.GetZipCodesTable(sqLiteDatabaseWritable);
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
            zipCode = String.format("%05d", zipCodesTable.getInt(ZIPCODE_ZIPCODE));
            latitude = zipCodesTable.getFloat(ZIPCODE_LATITUDE);
            longitude = zipCodesTable.getFloat(ZIPCODE_LONGITUDE);
            city = zipCodesTable.getString(ZIPCODE_CITY);

            // If latitude or longitude is set to an invalid value or null, it is unknown.
            latitudeString = (Math.abs(latitude) > 90 || latitude == null) ? "Unknown" : Float.toString(latitude);
            longitudeString = (Math.abs(longitude) > 180 || longitude == null) ? "Unknown" : Float.toString(longitude);

            zipCodeStrings.add(String.format("%s %s %s %s", zipCode, city, latitudeString, longitudeString));

            zipCodesTable.moveToNext();
        }

        zipCodesTable.close();

        // Closes any update database objects.
        super.close();

        return zipCodeStrings;
    }

    private Cursor GetWeatherTable(SQLiteDatabase sqLiteDatabaseWritable) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlProjection = {"_id", "ZipCode", "WeatherJSON", "LastUpdate", "Hour", "Minute"};
        String weatherTable = WeatherDb.WEATHER_TABLE_NAME;

        queryBuilder.setTables(weatherTable);
        Cursor cursor = queryBuilder.query(
                sqLiteDatabaseWritable,
                sqlProjection,
                null, null, null, null,
                WeatherDb.WEATHER_ORDERBY); // Order by id

        cursor.moveToFirst();
        return cursor;
    }

    public ZipCodeWeather GetWeatherForecast(int zipCode, int hour, int minute) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        ZipCodeWeather zipCodeWeather = new ZipCodeWeather();

        String[] sqlProjection = {"_id", "ZipCode", "WeatherJSON", "LastUpdate", "Hour", "Minute"};
        String sqlSelection = String.format("ZipCode = %s AND Hour = %s AND Minute = %s", zipCode, hour, minute);
        String sqlTables = WeatherDb.WEATHER_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        Cursor cursor = queryBuilder.query(sqLiteDatabaseWritable, sqlProjection, sqlSelection, null, null, null, null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            zipCodeWeather = this.CreateZipCodeWeatherFromRecord(
                    cursor.getString(WeatherDb.WEATHER_JSON),
                    cursor.getInt(WeatherDb.WEATHER_HOUR),
                    cursor.getInt(WeatherDb.WEATHER_MINUTE),
                    cursor.getInt(WeatherDb.WEATHER_LASTUPDATE),
                    cursor.getInt(WeatherDb.WEATHER_ZIPCODE));
        }

        cursor.close();

        // Closes any update database objects.
        super.close();

        return zipCodeWeather;
    }

    public List<ZipCodeWeather> GetAllWeatherForecast() {
        List<ZipCodeWeather> zipCodeWeatherList = new ArrayList<ZipCodeWeather>();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        ZipCodeWeather zipCodeWeather = new ZipCodeWeather();

        String[] sqlProjection = {"_id", "ZipCode", "WeatherJSON", "LastUpdate", "Hour", "Minute"};
        String sqlTables = WeatherDb.WEATHER_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        Cursor cursor = queryBuilder.query(sqLiteDatabaseWritable, sqlProjection, null, null, null, null, null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                zipCodeWeather = this.CreateZipCodeWeatherFromRecord(
                        cursor.getString(WeatherDb.WEATHER_JSON),
                        cursor.getInt(WeatherDb.WEATHER_HOUR),
                        cursor.getInt(WeatherDb.WEATHER_MINUTE),
                        cursor.getInt(WeatherDb.WEATHER_LASTUPDATE),
                        cursor.getInt(WeatherDb.WEATHER_ZIPCODE));

                zipCodeWeatherList.add(zipCodeWeather);

                cursor.moveToNext();
            }
        }

        cursor.close();

        // Closes any update database objects.
        super.close();

        return zipCodeWeatherList;
    }

    private ZipCodeWeather CreateZipCodeWeatherFromRecord(String json, int hour, int minute, int lastUpdate, int zipCode) {
        ZipCodeWeather zipCodeWeather;
        zipCodeWeather = this.gson.fromJson(json, ZipCodeWeather.class);
        zipCodeWeather.setHour(hour);
        zipCodeWeather.setMinute(minute);
        zipCodeWeather.setLastUpdate(lastUpdate);
        zipCodeWeather.setZipCode(zipCode);

        return zipCodeWeather;
    }

    // If the entry already exists, it will update the json string and lastupdate value.
    // If the entry doesn't already exist, it will create a new one.
    public synchronized void UpdateWeatherForecast(int zipCode, String weatherJson, int hour, int minute) {
        Log.d(WeatherDb.TAG, "Receving new weather forecast");
        Log.d(WeatherDb.TAG, "zipcode: " + zipCode);
        Log.d(WeatherDb.TAG, "weatherJson: " + weatherJson);
        Log.d(WeatherDb.TAG, "hour: " + hour);
        Log.d(WeatherDb.TAG, "minute: " + minute);

        int returnValue;

        ContentValues contentValues = new ContentValues();

        Calendar calendar = Calendar.getInstance();
        int currentEpochTimeInSeconds = (int) (calendar.getTime().getTime() / 1000);

        String id = this.GetIdForWeatherRecord(zipCode, hour, minute);
        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        // If the value is null, then there is no record
        if (id.contentEquals("-1")) {
            Log.d(WeatherDb.TAG, "No record found, inserting new record");
            contentValues.put("ZipCode", zipCode);
            contentValues.put("WeatherJSON", weatherJson);
            contentValues.put("LastUpdate", currentEpochTimeInSeconds);
            contentValues.put("Hour", hour);
            contentValues.put("Minute", minute);

            sqLiteDatabaseWritable.beginTransaction();
            returnValue = (int) sqLiteDatabaseWritable.insert(WeatherDb.WEATHER_TABLE_NAME, null, contentValues);
            sqLiteDatabaseWritable.setTransactionSuccessful();
            sqLiteDatabaseWritable.endTransaction();
        } else {
            Log.d(WeatherDb.TAG, "Record found, updating current record");

            contentValues.put("WeatherJSON", weatherJson);
            contentValues.put("LastUpdate", currentEpochTimeInSeconds);

            sqLiteDatabaseWritable.beginTransaction();
            returnValue = sqLiteDatabaseWritable.update(
                    WEATHER_TABLE_NAME,
                    contentValues,
                    WeatherDb.WEATHER_ID_COLUMN + "=" + id,
                    null);
            sqLiteDatabaseWritable.setTransactionSuccessful();
            sqLiteDatabaseWritable.endTransaction();
        }

        // Closes any update database objects.
        super.close();

        // Validate change
        this.ValidateRecord(zipCode, weatherJson, hour, minute, currentEpochTimeInSeconds);
    }

    private void ValidateRecord(int zipCode, String weatherJson, int hour, int minute, int currentEpochTimeInSeconds) {
        Log.d(WeatherDb.TAG, "Validating Record...");
        ZipCodeWeather zipCodeWeather = this.GetWeatherForecast(zipCode, hour, minute);
        Log.d(WeatherDb.TAG, "ZipCode Correct: " + (zipCodeWeather.getZipCode() == zipCode));
        Log.d(WeatherDb.TAG, "Hour Correct: " + (zipCodeWeather.getHour() == hour));
        Log.d(WeatherDb.TAG, "Minute Correct: " + (zipCodeWeather.getMinute() == minute));
        Log.d(WeatherDb.TAG, "LastUpdate Correct: " + (zipCodeWeather.getLastUpdate() == currentEpochTimeInSeconds));
    }

    private Cursor GetZipCodeRecord(SQLiteDatabase sqLiteDatabaseWritable, int zipCode) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlProjection = {"_id", "Latitude", "Longitude", "City"};
        String sqlSelection = String.format("_id = %s", zipCode);
        String sqlTables = WeatherDb.ZIPCODES_TABLE_NAME;

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(sqLiteDatabaseWritable, sqlProjection, sqlSelection, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    // Returns the coordinates of the zipcode in the format "latitude,longitude"
    public String GetZipCodeCoordinates(int zipCode) {
        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        Cursor cursor = this.GetZipCodeRecord(sqLiteDatabaseWritable, zipCode);

        String coordinates = null;
        if (cursor.getCount() != 0) {
            Log.d(WeatherDb.TAG, "Getting Coordinates for zipcode:" + cursor.getInt(WeatherDb.ZIPCODE_ZIPCODE));
            Log.d(WeatherDb.TAG, "Getting Coordinates for city:" + cursor.getString(WeatherDb.ZIPCODE_CITY));
            Log.d(WeatherDb.TAG, "Latitude for city:" + cursor.getFloat(WeatherDb.ZIPCODE_LATITUDE));
            Log.d(WeatherDb.TAG, "Longitude for city:" + cursor.getFloat(WeatherDb.ZIPCODE_LONGITUDE));

            coordinates = String.format("%s,%s",
                    cursor.getFloat(WeatherDb.ZIPCODE_LATITUDE),
                    cursor.getFloat(WeatherDb.ZIPCODE_LONGITUDE));

            Log.d(WeatherDb.TAG, "Coordinates for city:" + coordinates);

            cursor.close();

            // Closes any update database objects.
            super.close();
        }

        return coordinates;
    }

    public List<String> GetWeatherStrings() {
        List<String> zipCodeWeatherStrings = new ArrayList<String>();

        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        Cursor weatherTable = this.GetWeatherTable(sqLiteDatabaseWritable);

        // Prepare variables to store column data
        String zipCode = "";
        String time = "";
        String weather = "";
        int lastUpdate = 0;
        String summary;
        float chanceOfPrecipitation;


        ZipCodeWeather zipCodeWeather;

        // Load zipcodes into table.
        while (!weatherTable.isAfterLast()) {
            zipCode = String.format("%05d", weatherTable.getInt(WEATHER_ZIPCODE));
            weather = weatherTable.getString(WEATHER_JSON);
            time = this.Convert24HourToAmPm(weatherTable.getInt(WEATHER_HOUR), weatherTable.getInt(WEATHER_MINUTE));

            zipCodeWeather = gson.fromJson(weather, ZipCodeWeather.class);
            summary = zipCodeWeather.getCurrently().getSummary();
            chanceOfPrecipitation = zipCodeWeather.getCurrently().getPrecipProbability().floatValue();

            Log.d(WeatherDb.TAG, "zipcode: " + zipCode);
            Log.d(WeatherDb.TAG, "time: " + time);
            Log.d(WeatherDb.TAG, "summary: " + summary);
            Log.d(WeatherDb.TAG, "POP: " + chanceOfPrecipitation);

            zipCodeWeatherStrings.add(String.format("%s %s %s %s%%",
                    zipCode,
                    time,
                    zipCodeWeather.getCurrently().getSummary(),
                    zipCodeWeather.getCurrently().getPrecipProbability().floatValue() * 100));

            weatherTable.moveToNext();
        }

        weatherTable.close();

        // Closes any update database objects.
        super.close();

        return zipCodeWeatherStrings;
    }

    private String Convert24HourToAmPm(int hour, int minute) {
        String time = "";
        String ampm = "AM";
        if (hour >= 12) {
            ampm = "PM";
        }

        if (hour > 12) {
            hour = hour - 12;
        }

        return String.format(
                "%s:%s%s",
                String.format("%02d", hour), // Make sure hour is padded to two digits.
                String.format("%02d", minute), // Make sure minute is padded to two digits.
                ampm);
    }

    // Removes the specified items, using the same strings that WeatherDb provides as identification.
    // TODO: Investigate better method
    public void RemoveWeatherItems(List<String> itemsToRemove) {
        String[] stringArray;
        String idValue;
        Log.d(WeatherDb.TAG, "Starting to remove items from weather table.");
        for (int i = 0; i < itemsToRemove.size(); i++) {
            stringArray = itemsToRemove.get(i).split(" ");

            Log.d(WeatherDb.TAG, "Removing: " + itemsToRemove.get(i));

            idValue = this.GetIdForWeatherRecord(
                    Integer.parseInt(stringArray[0]),
                    Integer.parseInt(this.Extract24HourFromAmPm(stringArray[1])),
                    Integer.parseInt(this.ExtractMinuteFromAmPm(stringArray[1])));

            Log.d(WeatherDb.TAG, "Id to be deleted: " + idValue);

            SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
            sqLiteDatabaseWritable.delete(
                    WeatherDb.WEATHER_TABLE_NAME,
                    WeatherDb.WEATHER_ID_COLUMN + "=?",
                    new String[]{idValue});

            // Closes any update database objects.
            super.close();
        }
    }

    private String GetIdForWeatherRecord(int zipCode, int hour, int minute) {
        String result = "-1";
        String[] sqlProjection = {"_id", "ZipCode", "WeatherJSON", "LastUpdate", "Hour", "Minute"};
        String sqlSelection;
        String sqlTables = WeatherDb.WEATHER_TABLE_NAME;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(sqlTables);
        Cursor cursor;

        // Get ID of item to delete
        sqlSelection = String.format(
                "ZipCode = %s AND Hour = %s AND Minute = %s",
                zipCode,
                hour,
                minute);

        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        cursor = queryBuilder.query(sqLiteDatabaseWritable, sqlProjection, sqlSelection, null, null, null, null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            Long id = cursor.getLong(WeatherDb.WEATHER_ID);
            result = id.toString();
        }

        cursor.close();

        // Closes any update database objects.
        super.close();

        return result;
    }

    private String ExtractMinuteFromAmPm(String ampm) {
        // The item after : is the minutes appended with either AM or PM.
        // Once the portion after the first : is obtained, get the first two digits
        // which coorespond to the minute.
        String minute = ampm.split(":")[1].substring(0, 2);
        Log.d(WeatherDb.TAG, "Extracted minute: " + minute);

        return minute;
    }

    private String Extract24HourFromAmPm(String ampm) {
        // The ampm when split on : the first item is the hour.
        Integer hour = Integer.parseInt(ampm.split(":")[0]);

        // If this is PM time, the hour needs to be converted to the 24 hour time.
        if (ampm.contains("PM")) {
            if (hour < 12) {
                hour = hour + 12;
            }
        }

        return hour.toString();
    }
}