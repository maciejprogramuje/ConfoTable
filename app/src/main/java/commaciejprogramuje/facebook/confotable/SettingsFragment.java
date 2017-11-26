package commaciejprogramuje.facebook.confotable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static commaciejprogramuje.facebook.confotable.MainActivity.SHARED_PREF_END_HOUR_KEY;
import static commaciejprogramuje.facebook.confotable.MainActivity.SHARED_PREF_ROOM_NAME_KEY;
import static commaciejprogramuje.facebook.confotable.MainActivity.SHARED_PREF_START_HOUR_KEY;
import static commaciejprogramuje.facebook.confotable.MainActivity.SHARED_PREF_URL_TO_FILE_KEY;


public class SettingsFragment extends Fragment {
    @BindView(R.id.url_edit_text)
    EditText urlEditText;
    @BindView(R.id.room_name_edit_text)
    EditText roomNameEditText;
    @BindView(R.id.start_hour_subtract)
    Button startHourSubtract;
    @BindView(R.id.start_hour_text_view)
    TextView startHourTextView;
    @BindView(R.id.start_hour_add)
    Button startHourAdd;
    @BindView(R.id.end_hour_subtract)
    Button endHourSubtract;
    @BindView(R.id.end_hour_text_view)
    TextView endHourTextView;
    @BindView(R.id.end_hour_add)
    Button endHourAdd;
    @BindView(R.id.reset_settings_button)
    Button resetSettingsButton;
    @BindView(R.id.save_settings_button)
    Button saveSettingsButton;
    @BindView(R.id.not_save_settings_button)
    Button notSaveSettingsButton;
    Unbinder unbinder;

    private String urlToFile;
    private String roomName;
    private String startHour;
    private String endHour;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        urlToFile = sharedPref.getString(SHARED_PREF_URL_TO_FILE_KEY, "https://");
        roomName = sharedPref.getString(SHARED_PREF_ROOM_NAME_KEY, "Main Conference Room");
        startHour = sharedPref.getString(SHARED_PREF_START_HOUR_KEY, "7");
        endHour = sharedPref.getString(SHARED_PREF_END_HOUR_KEY, "20");

        urlEditText.setText(urlToFile);
        roomNameEditText.setText(roomName);
        startHourTextView.setText(startHour);
        endHourTextView.setText(endHour);

        Log.w("UWAGA", "urlToFile from sharedPref in SettingsFragment: " + urlToFile);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlToFile = urlEditText.getText().toString();
                roomName = roomNameEditText.getText().toString();

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SHARED_PREF_URL_TO_FILE_KEY, urlToFile);
                editor.putString(SHARED_PREF_ROOM_NAME_KEY, roomName);
                editor.putString(SHARED_PREF_START_HOUR_KEY, startHour);
                editor.putString(SHARED_PREF_END_HOUR_KEY, endHour);
                editor.commit();
                Log.w("UWAGA", "zapisuję w SharedPreferences: " + urlToFile);

                showMeetingsFragment();
            }
        });

        startHourSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHour = changeByOneHour(startHour, startHourTextView, "subtract");
            }
        });

        startHourAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHour = changeByOneHour(startHour, startHourTextView, "add");
            }
        });

        endHourSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endHour = changeByOneHour(endHour, endHourTextView, "subtract");
            }
        });

        endHourAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endHour = changeByOneHour(endHour, endHourTextView, "add");
            }
        });

        notSaveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMeetingsFragment();
            }
        });

        resetSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.resetPreferredLauncherAndOpenChooser(view.getContext());
                getActivity().finish();
            }
        });
    }

    private String changeByOneHour(String hour, TextView hourTextView, String operation) {
        int hourInt = Integer.valueOf(hour);
        if (operation.equals("subtract")) {
            hourInt--;
            if (hourInt < 0) hourInt = 23;
        } else {
            hourInt++;
            if (hourInt > 23) hourInt = 0;
        }
        String hourStr = String.valueOf(hourInt);
        hourTextView.setText(hourStr);
        return hourStr;
    }

    private void showMeetingsFragment() {
        MeetingsFragment meetingsFragment = MeetingsFragment.newInstance(urlToFile, roomName, startHour, endHour);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, meetingsFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.url_edit_text, R.id.room_name_edit_text, R.id.start_hour_subtract, R.id.start_hour_text_view, R.id.start_hour_add, R.id.end_hour_subtract, R.id.end_hour_text_view, R.id.end_hour_add, R.id.reset_settings_button, R.id.save_settings_button, R.id.not_save_settings_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.url_edit_text:
                break;
            case R.id.room_name_edit_text:
                break;
            case R.id.start_hour_subtract:
                break;
            case R.id.start_hour_text_view:
                break;
            case R.id.start_hour_add:
                break;
            case R.id.end_hour_subtract:
                break;
            case R.id.end_hour_text_view:
                break;
            case R.id.end_hour_add:
                break;
            case R.id.reset_settings_button:
                break;
            case R.id.save_settings_button:
                break;
            case R.id.not_save_settings_button:
                break;
        }
    }
}
