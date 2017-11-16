package commaciejprogramuje.facebook.confotable;

import android.app.ActivityManager;
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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String CONFERENCE_ROOM = "AKWARIUM";
    public static final String INPUT_FILE_URL = "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/";
    public static final long RESFRESH_TIME_MINUTES = 2;
    public static final String MEETINGS_KEY = "meetings";

    private RecyclerView recyclerView;
    private String getFilesDir;
    private ArrayList<OneMeeting> meetingsArr = new ArrayList<>();
    private RefreshFileReciever refreshFileReciever;

    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        disableStatusBar();

        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        // set screen full bright
        Utils.setScreenFullBright(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Conference room: " + CONFERENCE_ROOM);
        setSupportActionBar(toolbar);

        getFilesDir = getFilesDir().getAbsolutePath();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState != null) {
            meetingsArr = (ArrayList<OneMeeting>) savedInstanceState.getSerializable(MEETINGS_KEY);
        } else {
            meetingsArr.add(new OneMeeting());
        }
        recyclerView.setAdapter(new MyAdapter(meetingsArr, recyclerView));

        Log.w("UWAGA", "start MainActivity");

        setAlarm(this);

        // register refresh file RECEIVER
        refreshFileReciever = new RefreshFileReciever();
        IntentFilter filter2 = new IntentFilter("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFileReciever");
        this.registerReceiver(refreshFileReciever, filter2);
    }

    private void disableStatusBar() {
        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        CustomViewGroup view = new CustomViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

    private static void setAlarm(Context context) {
        Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFileReciever");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * RESFRESH_TIME_MINUTES, pendingIntent);

        Log.w("UWAGA", "wywołanie alarmu: " + alarmManager);
    }

    @Override
    public void onBackPressed() {
        // disable back button
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
    protected void onPause() {
        super.onPause();
        // disable recent apps button
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        unregisterReceiver(refreshFileReciever);

        super.onDestroy();
    }

    public void changeLauncherOnClick(View view) {
        Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFileReciever");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        Utils.resetPreferredLauncherAndOpenChooser(this);

        finish();
    }

    private class RefreshFileReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22 && calendar.get(Calendar.HOUR_OF_DAY) < 7) {
                Utils.setScreenHalfBright(MainActivity.this);
            } else {
                Utils.setScreenFullBright(MainActivity.this);
            }

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(MEETINGS_KEY, meetingsArr);
    }

}
