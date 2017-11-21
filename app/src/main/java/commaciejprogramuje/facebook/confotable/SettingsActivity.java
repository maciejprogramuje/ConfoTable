package commaciejprogramuje.facebook.confotable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    public static final String URL_STRING_KEY = "urlString";

    private static String url = "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void saveSettingsOnClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra(URL_STRING_KEY, url);
        startActivity(intent);
    }
}
