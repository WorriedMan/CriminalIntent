package com.example.oegod.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.oegodf.crime.CrimesMap;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID =
            "com.example.oegodf.criminalintent.crime_id";

    @BindView(R.id.crime_view_pager)
    ViewPager mViewPager;

    Menu mMenu;

    private CrimesMap<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        ButterKnife.bind(this);
        mViewPager.setPageMargin(convertDip2Pixels(this, 5));
        FragmentManager fragmentManager = getSupportFragmentManager();

        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);
        mCrimes = CrimeLab.get(this).getCrimes(crimeId == null);
        mViewPager.setAdapter(new CrimePagerAdapter(fragmentManager));

        mViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        setMenuItemsState(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }
        );

        if (crimeId != null) {
            Crime crime = CrimeLab.get(this).getCrime(crimeId);
            if (crime != null) {
                int crimePos = crime.getPosition();
                if (crimePos >= 0) { // crimePos = -1 тут остановился
                    mViewPager.setCurrentItem(crimePos);
                }
            }
        } else {
            mViewPager.setCurrentItem(mCrimes.size());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crime_menu, menu);
        mMenu = menu;
        setMenuItemsState(mViewPager.getCurrentItem());
        return true;
    }

    private void setMenuItemsState(int position) {
        if (mMenu != null) {
            if (position == 0) {
                mMenu.findItem(R.id.action_to_begin).setEnabled(false);
            } else {
                mMenu.findItem(R.id.action_to_begin).setEnabled(true);
            }
            if (position == mCrimes.size() - 1) {
                mMenu.findItem(R.id.action_to_end).setEnabled(false);
            } else {
                mMenu.findItem(R.id.action_to_end).setEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_to_begin:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.action_to_end:
                mViewPager.setCurrentItem(mCrimes.size());
                return true;
            case R.id.action_delete:
                deleteCurrentCrime(mViewPager.getCurrentItem());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteCurrentCrime(int itemId) {
        Crime crime = (Crime) mCrimes.getCrimeByPosition(itemId);
        CrimeLab crimeLab = CrimeLab.get(getApplicationContext());
        crimeLab.deleteCrime(crime);
        crimeLab.recountCrimes();
        mCrimes = CrimeLab.get(getApplicationContext()).getCrimes();
        Log.d("DEBUG", "itemId " + itemId);
        Log.d("DEBUG", "mCrimes.size() " + mCrimes.size());
        if (mCrimes.size() == 0) {
            finish();
        } else {
            if (itemId > mCrimes.size()) {
                mViewPager.setCurrentItem(mCrimes.size());
            } else {
                mViewPager.setAdapter(new CrimePagerAdapter(getSupportFragmentManager()));
                mViewPager.setCurrentItem(itemId);
            }
        }
//        finish();
    }


    public static int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }


    static Intent newIntent(Context context) {
        return new Intent(context, CrimePagerActivity.class);
    }

    static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }


    private class CrimePagerAdapter extends FragmentStatePagerAdapter {
        public CrimePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Crime crime = (Crime) mCrimes.getCrimeByPosition(position);
            return CrimeFragment.newInstance(crime.getId());
        }

        @Override
        public int getCount() {
            return mCrimes.size();
        }
    }
}
