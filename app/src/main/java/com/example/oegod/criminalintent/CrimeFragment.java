package com.example.oegod.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.widget.CompoundButton.*;

/**
 * Created by oegod on 21.08.2017.
 */

public class CrimeFragment extends Fragment {

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private Crime mCurrentCrime;
    private Unbinder unbinder;
    private String mCrimeTempName;

    @BindView(R.id.editText)
    EditText mCrimeEditText;
    @BindView(R.id.button)
    Button mCrimeDate;
    @BindView(R.id.checkBox)
    CheckBox mSolvedCheck;
    @BindView(R.id.titleTextView)
    TextView mTitleTextView;
    @BindView(R.id.crimeTime)
    Button mCrimeTimeButton;
    @BindView(R.id.needPoliceCheckBox)
    CheckBox mNeedPoliceCheckBox;
    @BindView(R.id.chooseSuspectButton)
    CheckBox mSuspectButton;
    @BindView(R.id.sendReportButton)
    CheckBox mSendReportButton;

    private static final String ARG_CRIME_ID = "crime_id";

    static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        if (crimeId == null) {
            mCurrentCrime = new Crime(UUID.randomUUID());
        } else {
            mCurrentCrime = CrimeLab.get(getContext()).getCrime(crimeId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        unbinder = ButterKnife.bind(this, v);
        mCrimeEditText.addTextChangedListener(new TextLabelChanged());
        mCrimeEditText.setText(mCurrentCrime.getTitle());
        updateDate();
        mNeedPoliceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCurrentCrime.setNeedPolice(isChecked);
            mTitleTextView.setBackgroundColor(isChecked ? ContextCompat.getColor(getContext(), R.color.colorPolice) : Color.WHITE);
        });
        mNeedPoliceCheckBox.setChecked(mCurrentCrime.isNeedPolice());
        mSolvedCheck.setOnCheckedChangeListener(new CrimeSolvedListener());
        mSolvedCheck.setChecked(mCurrentCrime.isSolved());
        if (mCurrentCrime.isNeedPolice()) {
            mTitleTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPolice));
        } else {
            mTitleTextView.setBackgroundColor(Color.WHITE);
        }
        mCrimeTempName = mCurrentCrime.getTitle();
        Log.d("DEBUG", "mCrimeTempName " + mCrimeTempName);
        return v;
    }

    @OnClick(R.id.button)
    public void crimeDateClicked() {
//        FragmentManager fragmentManager = getFragmentManager();
//        DatePickerFragment dialog = DatePickerFragment.newInstance(mCurrentCrime.getDate());
//        dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
//        dialog.show(fragmentManager,DIALOG_DATE);
        Intent intent = new Intent(getContext(), DateFragmentActivity.class);
        intent.putExtra(DatePickerFragment.EXTRA_DATE, mCurrentCrime.getDate());
        startActivityForResult(intent, REQUEST_DATE);
    }

    @OnClick(R.id.crimeTime)
    public void crimeTimeClicked() {
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerFragment dialog = TimePickerFragment.newInstance(mCurrentCrime.getDate());
        dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
        dialog.show(fragmentManager, DIALOG_TIME);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCrimeTempName != null) {
            if (!mCrimeTempName.equals("")) {
                mCurrentCrime.setTitle(mCrimeTempName);
            }
            if (mCurrentCrime.isNewCrime()) {
                CrimeLab.get(getActivity()).addCrime(mCurrentCrime);
            } else {
                CrimeLab.get(getContext()).updateCrime(mCurrentCrime);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
//        if (!mCrimeTempName.equals("")) {
//            mCurrentCrime.setTitle(mCrimeTempName);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCurrentCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCurrentCrime.setDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        mCrimeDate.setText(mCurrentCrime.getDateString(getContext()));
        mCrimeTimeButton.setText(mCurrentCrime.getTimeString(getContext()));
    }

    public class TextLabelChanged implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            mCurrentCrime.setTitle(charSequence.toString());
            mCrimeTempName = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class CrimeSolvedListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mCurrentCrime.setSolved(b);
        }
    }
}
