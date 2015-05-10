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
    private static final int DATABASE_VERSION = 1;

    public WeatherDb(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getZipCodes()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "Latitude", "Longitude", "City"};
        String sqlTables = "zipcodes";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();
        return c;
    }
}