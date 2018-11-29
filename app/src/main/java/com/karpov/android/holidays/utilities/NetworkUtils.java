package com.karpov.android.holidays.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String HOLIDAYS_BASE_URL =
            "https://holidayapi.com/v1/holidays";

    /* Api key what we use for request */
    private static final String apiKey = "8c0ea604-fe39-46f9-a8cd-000689b914aa";
    /* The format we want our API to return */
    private static final String format = "json";
    /* Year what we use for request */
    private static final int year = 2017;

    /* The key parameter allows us to provide a personal API key to the API */
    private final static String KEY_PARAM = "key";
    /* The format parameter allows us to designate format response from our API */
    private final static String FORMAT_PARAM = "format";
    /* The country parameter allows us to designate country we want */
    private final static String COUNTRY_PARAM = "country";
    /* The year parameter allows us to designate year we want */
    private final static String YEAR_PARAM = "year";

    /**
     * Builds the URL used to talk to the holidays server using a country name.
     *
     * @param countryQuery The country name that will be queried for.
     * @return The URL to use to query the holiday server.
     */
    public static URL buildUrl(String countryQuery) {
        Uri builtUri = Uri.parse(HOLIDAYS_BASE_URL).buildUpon()
                .appendQueryParameter(KEY_PARAM, apiKey)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(COUNTRY_PARAM, countryQuery)
                .appendQueryParameter(YEAR_PARAM, Integer.toString(year))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        String jsonResponse = "";

        /* If the URL is null, then return early. */
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(10000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /*
             * If the request was successful (response code 200),
             * then read the input stream and parse the response.
             */
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the holiday JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Helper method for convert the InputStream into a String
     * @param inputStream InputStream for convert.
     * @return String which contains the whole JSON response from the server.
     * @throws IOException Related to network and stream reading
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
