package com.karpov.android.holidays;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.karpov.android.holidays.data.HolidayContract;
import com.karpov.android.holidays.sync.HolidaysSyncTask;
import com.karpov.android.holidays.sync.HolidaysSyncUtils;

public class HolidaysActivity extends AppCompatActivity implements
        HolidaysAdapter.HolidaysAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * holidays data.
     */
    public static final String[] MAIN_HOLIDAYS_PROJECTION = {
            HolidayContract.HolidayEntry.COLUMN_COUNTRY,
            HolidayContract.HolidayEntry.COLUMN_NAME,
            HolidayContract.HolidayEntry.COLUMN_DATE
    };

    /*
     * Store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings. Lol
     */
    public static final int INDEX_HOLIDAY_COUNTRY = 0;
    public static final int INDEX_HOLIDAY_NAME = 1;
    public static final int INDEX_HOLIDAY_DATE = 2;

    private HolidaysAdapter mHolidaysAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    private static final int HOLIDAY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holidays);

        /* Get a reference to our RecyclerView from xml. */
        mRecyclerView = findViewById(R.id.recyclerview_holiday);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);


        /* Create a LinearLayoutManager and set orientation is vertical. */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Setting to improve performance because we know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);


        /* The HolidaysAdapter is responsible for linking our Holidays data with the Views. */

        mHolidaysAdapter = new HolidaysAdapter(this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mHolidaysAdapter);

        showLoading();

        /* Ensures a loader is initialized and active. */
        getSupportLoaderManager().initLoader(HOLIDAY_LOADER_ID, null, this);

        HolidaysSyncUtils.initialize(this);
        Log.v("TAG", "taskaa create");
    }

    /**
     * When any preference changed we start sync for update our database and schedule
     * notifications.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (SettingsFragment.sPreferenceChanged) {
            showLoading();
            HolidaysSyncUtils.startImmediateSync(this);
            SettingsFragment.sPreferenceChanged = false;
        }
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param holidayName Name of holiday.
     */
    @Override
    public void onClick(String holidayName) {

        Intent openInWebIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        openInWebIntent.putExtra(SearchManager.QUERY, holidayName);
        startActivity(openInWebIntent);
    }

    /**
     * Called when a new Loader needs to be created.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case HOLIDAY_LOADER_ID:
                /* URI for all rows of holidays data in our table */
                Uri holidayQueryUri = HolidayContract.HolidayEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = HolidayContract.HolidayEntry.COLUMN_DATE + " ASC";

                return new CursorLoader(this,
                        holidayQueryUri,
                        MAIN_HOLIDAYS_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mHolidaysAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0 && HolidaysSyncTask.sLoadingIsComplete) showHolidaysDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mHolidaysAdapter.swapCursor(null);
    }


    /**
     * This method will make the View for the holidays data visible and hide loading indicator.
     */
    private void showHolidaysDataView() {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the holiday View.
     */
    private void showLoading() {

        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Inflate and set up the menu for this Activity.
     *
     * @param menu The options menu for items.
     *
     * @return Return true for the menu to be displayed;
     *         if return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.holidays, menu);
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
