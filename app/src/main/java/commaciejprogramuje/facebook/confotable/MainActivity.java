package commaciejprogramuje.facebook.confotable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String INPUT_FILE_URL = "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/";
    public static final long RESFRESH_TIME_MINUTES = 2;
    public static final String MEETINGS_KEY = "meetings";

    private RecyclerView recyclerView;
    private String getFilesDir;
    private ArrayList<OneMeeting> meetingsArr = new ArrayList<>();
    private RefreshFile refreshFile;

    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFilesDir = getFilesDir().getAbsolutePath();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if(savedInstanceState != null) {
            meetingsArr = (ArrayList<OneMeeting>) savedInstanceState.getSerializable(MEETINGS_KEY);
        } else {
            meetingsArr.add(new OneMeeting("Launching...", "", "", "", "", ""));
        }
        recyclerView.setAdapter(new MyAdapter(meetingsArr, recyclerView));

        Log.w("UWAGA", "start MainActivity");

        setAlarm(this);
    }

    private static void setAlarm(Context context) {
        Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFile");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * RESFRESH_TIME_MINUTES, pendingIntent);

        Log.w("UWAGA", "wywołanie alarmu: " + alarmManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFile = new RefreshFile();
        IntentFilter filter = new IntentFilter("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFile");
        this.registerReceiver(refreshFile, filter);
    }

    @Override
    public void onBackPressed() {
        // nothing to do here
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }


    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        unregisterReceiver(refreshFile);

        super.onDestroy();
    }

    private class RefreshFile extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ParsePage refreshParsingPage = new ParsePage(new ParsePage.OnTaskCompletedListener() {
                @Override
                public void onTaskCompletedListener(ArrayList<OneMeeting> parsingResultArr) {
                    meetingsArr = parsingResultArr;
                    recyclerView.setAdapter(new MyAdapter(meetingsArr, recyclerView));
                }
            });
            refreshParsingPage.execute(getFilesDir);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_close) {
            Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFile");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, alarmIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            Utils.resetPreferredLauncherAndOpenChooser(this);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(MEETINGS_KEY, meetingsArr);
    }
}
