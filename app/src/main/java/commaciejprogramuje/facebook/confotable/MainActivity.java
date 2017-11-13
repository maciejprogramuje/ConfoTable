package commaciejprogramuje.facebook.confotable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String INPUT_FILE_URL = "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/";
    public static final long RESFRESH_TIME_MINUTES = 2;

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

        meetingsArr.add(new OneMeeting("Launching...", "", "", "", "", ""));
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
        // … really
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
