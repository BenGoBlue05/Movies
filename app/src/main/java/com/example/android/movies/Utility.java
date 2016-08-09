package com.example.android.movies;

import android.net.Uri;
import android.util.Log;

import com.example.android.movies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by bplewis5 on 8/7/16.
 */
public final class Utility {

    static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
    };
    static final int COL_TABLE_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_VOTE_AVG = 5;
    static final int COL_SYNOPSIS = 6;
    private static final String LOG_TAG_UTILITY = Utility.class.getSimpleName();
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    public static ArrayList<Movie> getMoviesFromJsonStr(String jsonStr, int maxResults)
            throws JSONException {

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String MOVIE_ID = "id";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVG = "vote_average";
        final String OVERVIEW = "overview";

        ArrayList<Movie> movies = new ArrayList<>();
        int numMovies;
        try {
            JSONObject initJson = new JSONObject(jsonStr);
            JSONArray results = initJson.getJSONArray(RESULTS);

            if (results.length() < maxResults) {
                numMovies = results.length();
            } else {
                numMovies = maxResults;
            }

            for (int i = 0; i < numMovies; i++) {
                String posterPath;
                long movieId;
                String title;
                String releaseDate;
                double voteAverage;
                String synopsis;

                try {
                    JSONObject movie = results.getJSONObject(i);
                    posterPath = movie.getString(POSTER_PATH);
                    movieId = movie.getLong(MOVIE_ID);
                    title = movie.getString(TITLE);
                    releaseDate = movie.getString(RELEASE_DATE);
                    voteAverage = movie.getDouble(VOTE_AVG);
                    synopsis = movie.getString(OVERVIEW);
                    Log.i(LOG_TAG_UTILITY, "THIS IS THE MOVIE ID: " + movieId);

                    movies.add(new Movie(movieId, posterPath, releaseDate, title, voteAverage, synopsis));
                } catch (JSONException e) {
                    Log.e(LOG_TAG_UTILITY, "JSON exception: ", e);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG_UTILITY, "Missing field ", e);
        }
        return movies;
    }

    public static ArrayList<String> getReviewsFromJsonStr(String jsonStr) {

        int numReviews;
        final int maxResults = 3;
        ArrayList<String> reviews = new ArrayList<>();

        try {
            JSONObject initJSON = new JSONObject(jsonStr);
            JSONArray results = initJSON.getJSONArray("results");
            if (results.length() < maxResults) {
                numReviews = results.length();
            } else {
                numReviews = maxResults;
            }

            for (int i = 0; i < numReviews; i++) {
                JSONObject reviewJson = results.getJSONObject(i);
                String review = reviewJson.getString("content");
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public static String getTrailerKeyFromJson(String jsonStr){
        try{
            JSONObject trailerJson;

            JSONObject initJson = new JSONObject(jsonStr);
            JSONArray results = initJson.getJSONArray("results");

            if (results.length() > 0){
                trailerJson = results.getJSONObject(0);
                return trailerJson.getString("key");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Movie> getMovies(String searchQuery) {

        try {
            final String baseUrl = "http://api.themoviedb.org/3/movie/" + searchQuery + "?";
            URL url = createUrl(baseUrl);
            String jsonStr = makeHttpRequest(url);
            int MAX_RESULTS = 48;

            return getMoviesFromJsonStr(jsonStr, MAX_RESULTS);

        } catch (IOException e) {
            Log.e(LOG_TAG_UTILITY, "IOEXCEPTION " + e);
        } catch (JSONException e) {
            Log.e(LOG_TAG_UTILITY, "Error ", e);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG_UTILITY, "No Internet Connection", e);
        }
        return null;
    }


    public static ArrayList<String> getReviews(long movieId) {

        try {
            final String urlStr = "http://api.themoviedb.org/3/movie/" +
                    movieId + "/reviews?";
            URL url = createUrl(urlStr);
            String jsonStr = makeHttpRequest(url);
            Log.i(LOG_TAG_UTILITY, "JSON FROM GET_REVIEWS: " + jsonStr);
            return getReviewsFromJsonStr(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTrailerKey(long movieId) {

        try {
            final String urlStr = "http://api.themoviedb.org/3/movie/" +
                    movieId + "/videos?";
            URL url = createUrl(urlStr);
            String jsonStr = makeHttpRequest(url);
            String trailerKey = getTrailerKeyFromJson(jsonStr);
            Log.i(LOG_TAG_UTILITY, "TRAILER KEY: " + trailerKey);
            return trailerKey;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





    private static URL createUrl(String baseUrl){
        String API_PARAM = "api_key";

        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_PARAM, BuildConfig.API_KEY)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG_UTILITY, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG_UTILITY, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
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
