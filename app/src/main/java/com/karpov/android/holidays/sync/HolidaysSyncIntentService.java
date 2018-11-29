package com.karpov.android.holidays.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class HolidaysSyncIntentService extends IntentService {

    public HolidaysSyncIntentService() {
        super("HolidaysSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HolidaysSyncTask.syncHolidays(this);
    }
}