package com.karpov.android.holidays.utilities;

import android.content.ContentValues;

import com.karpov.android.holidays.data.HolidayContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class HolidaysJsonUtils {

    private static final String DATA_HOLIDAYS = "holidays";
    private static final String DATA_NAME = "name";
    private static final String DATA_DATE = "date";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing holidays in country.
     *
     * @param holidaysJsonStr JSON response from server
     *
     * @param country Country which use for request
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getHolidaysContentValuesFromJson(String holidaysJsonStr, String country)
            throws JSONException {

        JSONObject holidaysJson = new JSONObject(holidaysJsonStr);

        JSONObject holidaysJsonObject = holidaysJson.optJSONObject(DATA_HOLIDAYS);

        JSONArray holidaysDatesArray = holidaysJsonObject.names();

        ContentValues[] holidayContentValues = new ContentValues[100];//ne znau kak eto fiksit`

        int indexContentValues = 0;
        for (int i = 0; i < holidaysDatesArray.length(); i++) {

            JSONArray dayHolidaysArray = holidaysJsonObject.getJSONArray(holidaysDatesArray.getString(i));

            for (int j = 0; j < dayHolidaysArray.length(); j++) {

                JSONObject holidayObject = dayHolidaysArray.getJSONObject(j);

                String name = holidayObject.getString(DATA_NAME);
                String date = holidayObject.getString(DATA_DATE);

                ContentValues holidayValues = new ContentValues();
                holidayValues.put(HolidayContract.HolidayEntry.COLUMN_COUNTRY, country);
                holidayValues.put(HolidayContract.HolidayEntry.COLUMN_NAME, name);
                holidayValues.put(HolidayContract.HolidayEntry.COLUMN_DATE, date);

                holidayContentValues[indexContentValues] = holidayValues;
                indexContentValues++;
            }

        }
        return holidayContentValues;
    }
}
