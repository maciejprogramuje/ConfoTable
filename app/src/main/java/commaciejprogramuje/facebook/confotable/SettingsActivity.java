package commaciejprogramuje.facebook.confotable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    public static final String URL_STRING_KEY = "urlString";
    //private static String url = "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/";

    Button saveSettingsButton;
    EditText pathEditText;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveSettingsButton = findViewById(R.id.save_settings_button);
        pathEditText = findViewById(R.id.url_edit_text);


    }

    public void saveSettingsOnClick(View view) {
        Log.w("UWAGA", "path: " + path);

        if(path != null) {
            path = pathEditText.getText().toString();
        } else {
            path = "error";
        }



        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra(URL_STRING_KEY, path);
        startActivity(intent);
    }
}
