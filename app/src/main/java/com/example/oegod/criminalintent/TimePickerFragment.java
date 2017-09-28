package com.example.oegod.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by oegod on 01.09.2017.
 */

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "criminalintent.time";

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment datePickerFragment = new TimePickerFragment();
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        Date date = (Date) getArguments().getSerializable(ARG_TIME);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        final TimePicker timePicker = v.findViewById(R.id.dialog_time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
        timePicker.setIs24HourView(DateFormat.is24HourFormat(getActivity()));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour;
                        int minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            minute = timePicker.getMinute();
                        } else {
                            hour = timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();
                        }
                        calendar.set(Calendar.HOUR_OF_DAY,hour);
                        calendar.set(Calendar.MINUTE,minute);
                        putDateResult(Activity.RESULT_OK, calendar.getTime());
                    }
                })
                .create();
    }

    private void putDateResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
