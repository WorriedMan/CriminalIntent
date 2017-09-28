package com.example.oegod.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by oegod on 27.08.2017.
 */

class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Crime mCrime;
    private CrimeListFragment mFragment;

    private TextView mTitleTextView;
    private TextView mDateTextView;
    private Activity mActivity;
    private ImageView mCrimeSolvedImageView;

    CrimeHolder(LayoutInflater inflater, ViewGroup parent, Activity activity, Fragment fragment) {
        super(inflater.inflate(R.layout.list_item_crime, parent, false));

        mTitleTextView = itemView.findViewById(R.id.crime_title);
        mDateTextView = itemView.findViewById(R.id.crime_date);
        mCrimeSolvedImageView = itemView.findViewById(R.id.crime_solved);

        itemView.setOnClickListener(this);
        mActivity = activity;
        mFragment = (CrimeListFragment) fragment;
    }

    void bind(Crime crime) {
        mCrime = crime;
        mTitleTextView.setText(mCrime.getTitle());
        Context context = mActivity.getApplicationContext();
        mDateTextView.setText(mCrime.getDateString(context)+" "+mCrime.getTimeString(context));
        mCrimeSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        mFragment.crimeClicked(mCrime);
    }
}