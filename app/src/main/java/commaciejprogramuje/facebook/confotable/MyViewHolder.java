package commaciejprogramuje.facebook.confotable;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView summaryTV;
    TextView dtStartTv;
    TextView dtEndTv;
    TextView timeStartTv;
    TextView timeEndTv;

    MyViewHolder(View itemView) {
        super(itemView);
        summaryTV = itemView.findViewById(R.id.meeting_summary);
        dtStartTv = itemView.findViewById(R.id.meeting_dt_start);
        dtEndTv = itemView.findViewById(R.id.meeting_dt_end);
        timeStartTv = itemView.findViewById(R.id.meeting_time_start);
        timeEndTv = itemView.findViewById(R.id.meeting_time_end);
    }
}
