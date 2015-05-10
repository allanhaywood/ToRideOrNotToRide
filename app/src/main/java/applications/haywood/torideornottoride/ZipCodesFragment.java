package applications.haywood.torideornottoride;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ZipCodesFragment extends Fragment {

    List<String> zipCodeStrings = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Cursor zipCodesTable;
    private WeatherDb weatherDb;

    public ZipCodesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                zipCodeStrings);

        View view = inflater.inflate(R.layout.fragment_zip_codes, container, false);

        final ListView listView = (ListView) view.findViewById(
                R.id.zipCodesView);

        listView.setAdapter(this.adapter);
        return view;
    }

    public void RefreshView()
    {
        this.zipCodeStrings = this.GetZipCodeStrings();
        //this.adapter.clear();
        this.adapter.addAll(this.zipCodeStrings);
        this.adapter.notifyDataSetChanged();
    }

    private List<String> GetZipCodeStrings()
    {
        weatherDb = new WeatherDb(this.getActivity().getApplicationContext());
        zipCodesTable = weatherDb.getZipCodes();

        // Prepare variables to store column data
        String zipCode = "";
        Float latitude = 0.0F; // Using Float as it is nullable and this value is from a database.
        Float longitude = 0.0F; // Using Float as it is nullable and this value is from a database.
        String city = "";

        String latitudeString = "";
        String longitudeString = "";

        // Load zipcodes into table.
        Integer count = 0;
        while (!zipCodesTable.isAfterLast())
        {
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        zipCodesTable.close();
        weatherDb.close();
    }
}
