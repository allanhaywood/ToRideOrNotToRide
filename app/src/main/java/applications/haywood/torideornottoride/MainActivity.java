package applications.haywood.torideornottoride;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
