package commaciejprogramuje.facebook.confotable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;
import static commaciejprogramuje.facebook.confotable.MainActivity.RESFRESH_TIME_MINUTES;


public class MeetingsFragment extends Fragment {
    public static final String URL_TO_FILE_MEETINGS_KEY = "urlToFile";
    public static final String ROOM_NAME_MEETINGS_KEY = "roomName";
    public static final String START_HOUR_MEETINGS_KEY = "startHour";
    public static final String END_HOUR_MEETINGS_KEY = "endHour";
    private RecyclerView recyclerView;
    private ArrayList<OneMeeting> meetingsArr = new ArrayList<>();
    private RefreshFileReciever refreshFileReciever;

    private String inputFileUrl;
    private String roomName;
    private String startHour;
    private String endHour;

    public MeetingsFragment() {
        // Required empty public constructor
    }

    public static MeetingsFragment newInstance(String param1, String param2, String param3, String param4) {
        MeetingsFragment fragment = new MeetingsFragment();
        Bundle args = new Bundle();
        args.putString(URL_TO_FILE_MEETINGS_KEY, param1);
        args.putString(ROOM_NAME_MEETINGS_KEY, param2);
        args.putString(START_HOUR_MEETINGS_KEY, param3);
        args.putString(END_HOUR_MEETINGS_KEY, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meetings, container, false);

        // register refresh file RECEIVER
        refreshFileReciever = new RefreshFileReciever();
        IntentFilter filter2 = new IntentFilter("commaciejprogramuje.facebook.confotable.MeetingsFragment$RefreshFileReciever");
        getActivity().registerReceiver(refreshFileReciever, filter2);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (getArguments() != null) {
            inputFileUrl = getArguments().getString(URL_TO_FILE_MEETINGS_KEY);
            roomName = getArguments().getString(ROOM_NAME_MEETINGS_KEY);
            startHour = getArguments().getString(START_HOUR_MEETINGS_KEY);
            endHour = getArguments().getString(END_HOUR_MEETINGS_KEY);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meetingsArr.add(new OneMeeting("Launching...\nBe sure to set proper settings!"));
        recyclerView.setAdapter(new MyAdapter(meetingsArr, recyclerView));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Boardroom: " + roomName);
        setAlarm(getContext());
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(refreshFileReciever);

        super.onDetach();
    }

    private class RefreshFileReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("UWAGA", "wywołanie RefreshFileReciever");

            int endHourInt = Integer.valueOf(endHour);
            int startHourInt = Integer.valueOf(startHour);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            if (calendar.get(Calendar.HOUR_OF_DAY) > startHourInt && calendar.get(Calendar.HOUR_OF_DAY) < endHourInt) {
                Utils.setScreenHalfBright(getActivity());
            } else {
                Utils.setScreenFullBright(getActivity());
            }

            Log.w("UWAGA", "calHour="+calendar.get(Calendar.HOUR_OF_DAY)+", endStr="+endHour+", endInt="+endHourInt+", startStr="+startHour+", startIng="+startHourInt);

            /*if (calendar.get(Calendar.MINUTE) >= 0 && calendar.get(Calendar.MINUTE) < 15) {
                Utils.setScreenFullBright(getActivity());
            } else if (calendar.get(Calendar.MINUTE) >= 15 && calendar.get(Calendar.MINUTE) < 30) {
                Utils.setScreenHalfBright(getActivity());
            } else if (calendar.get(Calendar.MINUTE) >= 30 && calendar.get(Calendar.MINUTE) < 45) {
                Utils.setScreenFullBright(getActivity());
            } else {
                Utils.setScreenHalfBright(getActivity());
            }*/


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

    private static void setAlarm(Context context) {
        Intent alarmIntent = new Intent("commaciejprogramuje.facebook.confotable.MeetingsFragment$RefreshFileReciever");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * RESFRESH_TIME_MINUTES, pendingIntent);

        Log.w("UWAGA", "wywołanie alarmu: " + alarmManager);
    }
}
