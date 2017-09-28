package com.example.oegod.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by oegod on 22.08.2017.
 */

public class CrimeListFragment extends Fragment {
    private static final java.lang.String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private Button mAddCrimeButton;
    private TextView mTextViewCrimesNotFound;
    private boolean mSubtitleVisible;
    private static final int REQUEST_CRIME = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list_new, container, false);
        mCrimeRecyclerView = v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        mAddCrimeButton = v.findViewById(R.id.addCrimeOnNotFound);
        mTextViewCrimesNotFound = v.findViewById(R.id.crimesNotFoundLabel);
        mAddCrimeButton.setOnClickListener((view) -> createNewCrime());
//        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createNewCrime();
//            }
//        });

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimeLab.invalidateCrimes();
        CrimesMap crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes, this);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        if (crimes.size() == 0) {
            mCrimeRecyclerView.setVisibility(View.INVISIBLE);
            mAddCrimeButton.setVisibility(View.VISIBLE);
            mTextViewCrimesNotFound.setVisibility(View.VISIBLE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mAddCrimeButton.setVisibility(View.INVISIBLE);
            mTextViewCrimesNotFound.setVisibility(View.INVISIBLE);
        }
        showSubitle();
    }

    public void crimeClicked(Crime crime) {
        startActivity(CrimePagerActivity.newIntent(getActivity(), crime.getId()));
//        startActivityForResult(CrimePagerActivity.newIntent(getActivity(),crime.getId()), REQUEST_CRIME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", "REQUEST " + requestCode);
        if (requestCode == REQUEST_CRIME) {
            UUID crimeId = (UUID) data.getSerializableExtra("crime_id");
            Crime crime = CrimeLab.get(getContext()).getCrime(crimeId);
            mAdapter.notifyItemChanged(crime.getPosition());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                createNewCrime();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                showSubitle();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewCrime() {
//        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity());
        startActivity(intent);
    }

    private void showSubitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_format, crimeCount, crimeCount);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private CrimesMap mCrimes;

        private Fragment mFragment;

        public CrimeAdapter(CrimesMap crimes, Fragment fragment) {
            mCrimes = crimes;
            mFragment = fragment;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType) {
                case 1:
                    return new SeveralCrimeHolder(layoutInflater, parent, getActivity(), mFragment);
                default:
                    return new CrimeHolder(layoutInflater, parent, getActivity(), mFragment);
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.getCrimeByPosition(position);
            switch (holder.getItemViewType()) {
                case 0:
                    CrimeHolder crimeHolder = (CrimeHolder) holder;
                    crimeHolder.bind(crime);
                    break;
                case 1:
                    SeveralCrimeHolder severalCrimeHolder = (SeveralCrimeHolder) holder;
                    severalCrimeHolder.bind(crime);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.getCrimeByPosition(position);
            if (crime == null) {
                return 0;
            }
            return crime.isNeedPolice() ? 1 : 0;
        }

        public void setCrimes(CrimesMap crimes) {
            mCrimes = crimes;
        }
    }
}
