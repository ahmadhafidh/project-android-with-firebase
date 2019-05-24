package com.ahmad.reminderme;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toolbar;

import java.util.Calendar;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.Holder> {

    private List<Activity> activities;
    private Context context;

    public ActivityAdapter(List<Activity> activities, Context context) {
        this.activities = activities;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jadwal, parent, false);
        return new Holder(itemView);
    }

    public Activity getItem(int position) {
        return activities.get(position);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Activity activity = activities.get(position);

        holder.cb_status.setChecked(activity.isEnabled());

        if (activity.isEnabled()) {
            holder.cb_status.setText("enabled");
        } else {
            holder.cb_status.setText("disabled");
        }

        holder.cb_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.setEnabled(isChecked);
                if (activity.isEnabled()) {
                    holder.cb_status.setText("enabled");
                } else {
                    holder.cb_status.setText("disabled");
                }
            }
        });

        holder.tv_activity.setText(activity.getActivity());
        holder.tv_time.setText(String.format("%02d:%02d", activity.getTime()/60, activity.getTime()%60));

        holder.tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                TimePickerDialog picker = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                activity.setTime((sHour * 60) + sMinute);
                                holder.tv_time.setText(String.format("%02d:%02d", sHour, sMinute));
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv_activity, tv_time;
        CheckBox cb_status;

        Holder(View view) {
            super(view);
            tv_activity = view.findViewById(R.id.tv_activity);
            tv_time = view.findViewById(R.id.tv_time);
            cb_status = view.findViewById(R.id.cb_status);
        }
    }
}
