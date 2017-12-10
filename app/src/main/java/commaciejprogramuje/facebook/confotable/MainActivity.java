package commaciejprogramuje.facebook.confotable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
    public static final long RESFRESH_TIME_MINUTES = 2;
    public static final String ADMIN_CODE = "0000";
    // ------------------------------------------------------------
    // ------------------------------------------------------------
    public static final String SHARED_PREF_URL_TO_FILE_KEY = "sharedPrefUrlToFile";
    public static final String SHARED_PREF_ROOM_NAME_KEY = "sharedPrefRoomName";
    public static final String SHARED_PREF_START_HOUR_KEY = "sharedPrefStartHour";
    public static final String SHARED_PREF_END_HOUR_KEY = "sharedPrefEndHour";
    public static boolean isParsingComplette = true;
    public final static int REQUEST_CODE = 66;

    protected PowerManager.WakeLock mWakeLock;

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button hiddenButton = findViewById(R.id.change_launcher_button);
        hiddenButton.setBackgroundColor(Color.TRANSPARENT);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String inputFileUrl = sharedPref.getString(SHARED_PREF_URL_TO_FILE_KEY, "https://");
        String roomName = sharedPref.getString(SHARED_PREF_ROOM_NAME_KEY, "Main Conference Room");
        String startHour = sharedPref.getString(SHARED_PREF_START_HOUR_KEY, "20");
        String endHour = sharedPref.getString(SHARED_PREF_END_HOUR_KEY, "7");

        Log.w("UWAGA", "start MainActivity, inputFileUrl from sharedPref: " + inputFileUrl);

        // Disable sleep mode
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // turn off sleep mode
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // turn on sleep mode

        // This code together with the one in onDestroy() Will make the screen be always on until this Activity gets destroyed.
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else {
            disableStatusBar();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Conference room: " + roomName);
        setSupportActionBar(toolbar);

        MeetingsFragment meetingsFragment = MeetingsFragment.newInstance(inputFileUrl, roomName, startHour, endHour);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, meetingsFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            // ** if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                    disableStatusBar();
                }
            }
        }
    }

    private void disableStatusBar() {
        WindowManager manager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
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
        assert manager != null;
        manager.addView(view, localLayoutParams);
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    // disable volume buttons
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
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
        assert activityManager != null;
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
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.admin_code, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.code_input_edit_text);

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

                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    Log.w("UWAGA", "usunięcie alarmu alarmu: " + alarmManager);
                                    assert alarmManager != null;
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
