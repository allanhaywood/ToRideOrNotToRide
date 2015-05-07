package applications.haywood.torideornottoride;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;


public class DetailsActivity extends ActionBarActivity {

    private Cursor zipCodes;
    private WeatherDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        db = new WeatherDb(this);
        zipCodes = db.getZipCodes();


        ListAdapter adapter = new SimpleCursorAdapter(this,
            android.R.layout.simple_list_item_1,
            zipCodes,
            new String[] {"ZipCode"},
            new int[] {android.R.id.text1},
            0);


        // Load zipcodes into table.
        List<Integer> zipCodeList = new ArrayList<Integer>();

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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        zipCodes.close();
        db.close();
    }

}
