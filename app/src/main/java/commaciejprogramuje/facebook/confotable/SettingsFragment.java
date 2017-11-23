package commaciejprogramuje.facebook.confotable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import static commaciejprogramuje.facebook.confotable.MainActivity.SHARED_PREF_KEY;


public class SettingsFragment extends Fragment {
    public static final String URL_TO_FILE_SETTINGS_KEY = "urlToFile";

    EditText urlEditText;
    Button saveHiddenButton;
    Button resetHiddenButton;
    private String urlToFile;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        urlEditText = view.findViewById(R.id.url_edit_text);
        saveHiddenButton = view.findViewById(R.id.save_settings_button);
        resetHiddenButton = view.findViewById(R.id.reset_settings_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(SHARED_PREF_KEY)) {
            urlToFile = sharedPref.getString(SHARED_PREF_KEY, "error");
            urlEditText.setText(urlToFile);
            Log.w("UWAGA", "urlToFile from sharedPref in SettingsFragment: " + urlToFile);
        }

        saveHiddenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlToFile = urlEditText.getText().toString();

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SHARED_PREF_KEY, urlToFile);
                editor.commit();
                Log.w("UWAGA", "zapisuję w SharedPreferences: " + urlToFile);


                MeetingsFragment meetingsFragment = MeetingsFragment.newInstance(urlToFile);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, meetingsFragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        resetHiddenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.resetPreferredLauncherAndOpenChooser(view.getContext());
                getActivity().finish();
            }
        });
    }
}
