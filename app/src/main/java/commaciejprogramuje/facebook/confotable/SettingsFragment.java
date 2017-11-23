package commaciejprogramuje.facebook.confotable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SettingsFragment extends Fragment {
    public static final String URL_TO_FILE_SETTINGS_KEY = "urlToFile";

    EditText urlEditText;
    Button saveHiddenButton;
    Button resetHiddenButton;
    private String urlToFile;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(URL_TO_FILE_SETTINGS_KEY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        urlEditText = view.findViewById(R.id.url_edit_text);
        saveHiddenButton = view.findViewById(R.id.save_settings_button);
        resetHiddenButton = view.findViewById(R.id.reset_settings_button);

        if (getArguments() != null) {
            urlToFile = getArguments().getString(URL_TO_FILE_SETTINGS_KEY);
            urlEditText.setText(urlToFile);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveHiddenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlToFile = urlEditText.getText().toString();

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
