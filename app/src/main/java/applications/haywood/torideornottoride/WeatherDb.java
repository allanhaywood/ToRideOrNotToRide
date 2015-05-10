package applications.haywood.torideornottoride;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by ahaywood on 5/6/2015.
 */
public class WeatherDb extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "weatherdb";
    private static final int DATABASE_VERSION = 2;

    public WeatherDb(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    public Cursor getZipCodes()
    {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "Latitude", "Longitude", "City"};
        String sqlTables = "zipcodes";

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(database, sqlSelect, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getWeather() {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "WeatherJSON", "LastUpdate"};
        String sqlTables = "weather";

        queryBuilder.setTables(sqlTables);
        Cursor cursor = queryBuilder.query(database, sqlSelect, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }
}