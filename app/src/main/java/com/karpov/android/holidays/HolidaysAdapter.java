package com.karpov.android.holidays;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karpov.android.holidays.utilities.HolidaysUtils;

/**
 * {@link HolidaysAdapter} exposes a list of holidays
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}.
 */
public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.HolidaysAdapterViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    /*
     * Store instance of a class that has implemented interface below
     * to call the onClick method whenever an item is clicked in the list.
     */
    private final HolidaysAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface HolidaysAdapterOnClickHandler {
        void onClick(String holidayName);
    }

    /**
     * Creates a HolidaysAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public HolidaysAdapter(@NonNull Context context, HolidaysAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If RecyclerView has more than one type of item use this
     *                  viewType integer to provide a different layout.
     *
     * @return A new HolidayAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public HolidaysAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.holiday_list_item, viewGroup, false);

        return new HolidaysAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the holiday
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holidaysAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HolidaysAdapterViewHolder holidaysAdapterViewHolder, int position) {

        mCursor.moveToPosition(position);

        String country = mCursor.getString(HolidaysActivity.INDEX_HOLIDAY_COUNTRY);
        String name = mCursor.getString(HolidaysActivity.INDEX_HOLIDAY_NAME);
        String date = mCursor.getString(HolidaysActivity.INDEX_HOLIDAY_DATE);

        /* Get flag resource id and set image icon */
        int flagImageId = HolidaysUtils.getFlagResourceId(mContext, country);
        holidaysAdapterViewHolder.flagImageView.setImageResource(flagImageId);

        /* Get human readable date string using our utility method and set this */
        String friendlyDateString = HolidaysUtils.getFriendlyDateString(date);
        holidaysAdapterViewHolder.dateView.setText(friendlyDateString);

        /* Set holiday`s name */
        holidaysAdapterViewHolder.nameView.setText(name);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * This method is called by MainActivity after a load has finished, as well as when the Loader
     * responsible for loading the holidays data is reset. When this method is called, we assume we
     * have a completely new set of data, so we call notifyDataSetChanged to tell the RecyclerView
     * to update.
     *
     * @param newCursor the new cursor to use as HolidaysAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * A ViewHolder for cache of the child views for a holiday item. Set an OnClickListener
     */
    public class HolidaysAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        final ImageView flagImageView;
        final TextView nameView;
        final TextView dateView;

        HolidaysAdapterViewHolder(View view) {
            super(view);
            flagImageView = view.findViewById(R.id.flagImageView);
            nameView = view.findViewById(R.id.nameHolidayTextView);
            dateView = view.findViewById(R.id.dateHolidayTextView);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String holidayName = mCursor.getString(HolidaysActivity.INDEX_HOLIDAY_NAME);
            mClickHandler.onClick(holidayName);
        }
    }

}
