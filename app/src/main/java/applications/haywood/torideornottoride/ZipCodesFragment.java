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
        WeatherDb weatherDb = WeatherDb.getSingleInstance(this.getActivity().getApplicationContext());
        return weatherDb.GetZipCodesStrings();
    }
}
