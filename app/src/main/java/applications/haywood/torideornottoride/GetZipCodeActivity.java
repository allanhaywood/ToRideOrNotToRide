package applications.haywood.torideornottoride;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class GetZipCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_zip_code);
    }

    // Sets zipcode and hour in sharedpreferences and current title when OK is clicked.
    public void SetZipCode(View view) {
        // Get the text in the zipCOdeEditText box.
        EditText zipCodeText = (EditText) findViewById(R.id.zipCodeEditText);
        String zipCode = zipCodeText.getText().toString();

        // Get the selected time from the time picker.
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        // Store the username in the sharedpreferences so other activities can access it.
        SharedPreferences sharedPreferences = getSharedPreferences(ModifyActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();

        editor.putString(ModifyActivity.ZIPCODE_PREFERENCE, zipCode);
        editor.putInt(ModifyActivity.HOUR_PREFERENCE, hour);
        editor.putInt(ModifyActivity.MINUTE_PREFERENCE, minute);
        editor.commit();

        // Indicate that activity completed successfully for parent activity using result.
        setResult(RESULT_OK, getIntent());

        finish();
    }

}
