package commaciejprogramuje.facebook.confotable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static commaciejprogramuje.facebook.confotable.MainActivity.INPUT_FILE_URL_KEY;

public class SettingsActivity extends AppCompatActivity {
    public static final String URL_STRING_KEY = "urlString";

    EditText pathEditText;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.w("UWAGA", "tworzenie SettingsActivity");

        pathEditText = findViewById(R.id.url_edit_text);
        pathEditText.setText(getIntent().getStringExtra(INPUT_FILE_URL_KEY));
    }

    public void saveSettingsOnClick(View view) {
        path = pathEditText.getText().toString();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra(URL_STRING_KEY, path);
        startActivity(intent);
    }

    public void resetSettingsOnClick(View view) {
        Utils.resetPreferredLauncherAndOpenChooser(view.getContext());
        finish();
    }
}
