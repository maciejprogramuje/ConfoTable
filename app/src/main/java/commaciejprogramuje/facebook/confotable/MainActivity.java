package commaciejprogramuje.facebook.confotable;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // ------------------------------------------------------------
    // ------------------------------------------------------------
    // --------------------*** user options ***--------------------
    // ------------------------------------------------------------
    // ------------------------------------------------------------
    public static final String CONFERENCE_ROOM = "AKWARIUM";
    // "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/";
    public static final long RESFRESH_TIME_MINUTES = 2;
    public static final String ADMIN_CODE = "0000";
    // ----------------------- from 0 to 255 ----------------------
    public static final int FULL_BRIGHT_LEVEL = 180;
    public static final int HALF_BRIGHT_LEVEL = 7;
    // ------------------------------------------------------------
    // ------------------------------------------------------------

    public static final String MEETINGS_KEY = "meetings";

    private RecyclerView recyclerView;
    private ArrayList<OneMeeting> meetingsArr = new ArrayList<>();
    private RefreshFileReciever refreshFileReciever;
    private Button hiddenButton;
    protected PowerManager.WakeLock mWakeLock;
    private String inputFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        hiddenButton = findViewById(R.id.change_launcher_button);
        hiddenButton.setBackgroundColor(Color.TRANSPARENT);

        Intent incomingIntent = getIntent();

        if (incomingIntent != null) {
            inputFileUrl = incomingIntent.getStringExtra(SettingsActivity.URL_STRING_KEY);
        } else if (savedInstanceState != null) {
            inputFileUrl = savedInstanceState.getString(MEETINGS_KEY);
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        meetingsArr.add(new OneMeeting("Launching..."));
        recyclerView.setAdapter(new MyAdapter(meetingsArr, recyclerView));

        Log.w("UWAGA", "start MainActivity");
        Log.w("UWAGA", "inputFileUrl: "+inputFileUrl);

        // register refresh file RECEIVER
        refreshFileReciever = new RefreshFileReciever();
        IntentFilter filter2 = new IntentFilter("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFileReciever");
        this.registerReceiver(refreshFileReciever, filter2);

        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        if (inputFileUrl != null) {
            disableStatusBar();

            // set screen full bright
            Utils.setScreenFullBright(this);

            Log.w("UWAGA", "summary: "+meetingsArr.get(0).getSummary());

            if(!meetingsArr.get(0).getSummary().equals(ParsePage.WRONG_URL_MESSAGE)) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle("Conference room: " + CONFERENCE_ROOM);
                setSupportActionBar(toolbar);

                setAlarm(this);
            }
        }
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

        if(inputFileUrl != null) {
            // disable recent apps button
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        unregisterReceiver(refreshFileReciever);

        super.onDestroy();
    }

    public void hiddenButtonOnClick(View view) {
        final Context context = view.getContext();
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.admin_code, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.code_input_edit_text);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (userInput.getText().toString().equals(ADMIN_CODE)) {
                                    Log.w("UWAGA", "nowe ustawienia");
                                    MainActivity.this.finish();
                                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                    startActivity(intent);

                                    /*Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFileReciever");
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, alarmIntent, 0);
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager.cancel(pendingIntent);

                                    Utils.resetPreferredLauncherAndOpenChooser(context);

                                    finish();*/
                                } else {
                                    Toast.makeText(context, "Access denied!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class RefreshFileReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            /*if (calendar.get(Calendar.HOUR_OF_DAY) > 19 && calendar.get(Calendar.HOUR_OF_DAY) < 7) {
                Utils.setScreenHalfBright(MainActivity.this);
            } else {
                Utils.setScreenFullBright(MainActivity.this);
            }*/

            if (calendar.get(Calendar.MINUTE) >= 0 && calendar.get(Calendar.MINUTE) < 15) {
                Utils.setScreenFullBright(MainActivity.this);
            } else if (calendar.get(Calendar.MINUTE) >= 15 && calendar.get(Calendar.MINUTE) < 30) {
                Utils.setScreenHalfBright(MainActivity.this);
            } else if (calendar.get(Calendar.MINUTE) >= 30 && calendar.get(Calendar.MINUTE) < 45) {
                Utils.setScreenFullBright(MainActivity.this);
            } else {
                Utils.setScreenHalfBright(MainActivity.this);
            }

            ParsePage refreshParsingPage = new ParsePage(new ParsePage.OnTaskCompletedListener() {
                @Override
                public void onTaskCompletedListener(ArrayList<OneMeeting> parsingResultArr) {
                    meetingsArr = parsingResultArr;
                    recyclerView.setAdapter(new MyAdapter(meetingsArr, recyclerView));
                }
            });
            refreshParsingPage.execute(inputFileUrl);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(MEETINGS_KEY, inputFileUrl);
    }

}
