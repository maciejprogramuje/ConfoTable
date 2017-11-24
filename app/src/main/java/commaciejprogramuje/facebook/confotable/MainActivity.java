package commaciejprogramuje.facebook.confotable;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    // "https://poczta.pb.pl/home/sala_akwarium@pb.pl/Calendar/"
    // ------------------------------------------------------------
    public static final String SHARED_PREF_URL_TO_FILE_KEY = "sharedPrefUrlToFile";
    public static final String SHARED_PREF_ROOM_NAME_KEY = "sharedPrefRoomName";
    public static final String SHARED_PREF_START_HOUR_KEY = "sharedPrefStartHour";
    public static final String SHARED_PREF_END_HOUR_KEY = "sharedPrefEndHour";

    protected PowerManager.WakeLock mWakeLock;

    private String inputFileUrl;
    private String roomName;
    private String startHour;
    private String endHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button hiddenButton = findViewById(R.id.change_launcher_button);
        hiddenButton.setBackgroundColor(Color.TRANSPARENT);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        inputFileUrl = sharedPref.getString(SHARED_PREF_URL_TO_FILE_KEY, "https://");
        roomName = sharedPref.getString(SHARED_PREF_ROOM_NAME_KEY, "Main Conference Room");
        startHour = sharedPref.getString(SHARED_PREF_START_HOUR_KEY, "7");
        endHour = sharedPref.getString(SHARED_PREF_END_HOUR_KEY, "20");

        Log.w("UWAGA", "start MainActivity, inputFileUrl from sharedPref: " + inputFileUrl);

        // Disable sleep mode
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // turn off sleep mode
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // turn on sleep mode

        // This code together with the one in onDestroy() Will make the screen be always on until this Activity gets destroyed.
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        disableStatusBar();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Conference room: " + roomName);

        setSupportActionBar(toolbar);

        MeetingsFragment meetingsFragment = MeetingsFragment.newInstance(inputFileUrl, startHour, endHour);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, meetingsFragment);
        fragmentTransaction.commitAllowingStateLoss();
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

                                Log.w("UWAGA", "click");

                                if (userInput.getText().toString().equals(ADMIN_CODE)) {
                                    Log.w("UWAGA", "nowe ustawienia");

                                    Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MainActivity$RefreshFileReciever");
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, alarmIntent, 0);
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    Log.w("UWAGA", "usunięcie alarmu alarmu: " + alarmManager);
                                    alarmManager.cancel(pendingIntent);

                                    SettingsFragment settingsFragment = SettingsFragment.newInstance();
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.main_container, settingsFragment);
                                    fragmentTransaction.commitAllowingStateLoss();
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
}
