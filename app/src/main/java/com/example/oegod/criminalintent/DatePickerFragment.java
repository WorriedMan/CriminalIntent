package com.example.oegod.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by oegod on 01.09.2017.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "criminalintent.date";
    private Button mSaveButton;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        Intent intent = getActivity().getIntent();
        Date date = (Date) intent.getSerializableExtra(EXTRA_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePicker datePicker = v.findViewById(R.id.dialog_date_picker);
        datePicker.init(year, month, day, null);

        mSaveButton = v.findViewById(R.id.buton_save_date);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year, month, day)
                        .getTime();
                putDateResult(Activity.RESULT_OK, date);
            }
        });
        return v;
    }

    private void putDateResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }
}
