package applications.haywood.torideornottoride;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends Fragment {

    private Gson gson = new Gson();

    private List<String> zipCodeWeathersStrings = new ArrayList<String>();
    private List<ZipCodeWeather> zipCodeWeathers = new ArrayList<ZipCodeWeather>();
    private ArrayAdapter<String> adapter;

    private Cursor weatherTable;
    private WeatherDb weatherDb;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                zipCodeWeathersStrings);

        View view = inflater.inflate(R.layout.fragment_zip_codes, container, false);

        final ListView listView = (ListView) view.findViewById(
                R.id.zipCodesView);

        listView.setAdapter(this.adapter);
        return view;
    }

    public void RefreshView() {
        this.zipCodeWeathersStrings = this.GetWeatherStrings();
        //this.adapter.clear();
        this.adapter.addAll(this.zipCodeWeathersStrings);
        this.adapter.notifyDataSetChanged();
    }

    private List<String> GetWeatherStrings() {
        weatherDb = new WeatherDb(this.getActivity().getApplicationContext());
        return weatherDb.GetWeatherStrings();
    }

    public List<ZipCodeWeather> getZipCodeWeathers() {
        return this.zipCodeWeathers;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weatherTable.close();
        weatherDb.close();
    }
}
