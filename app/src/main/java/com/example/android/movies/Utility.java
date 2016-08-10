package com.example.android.movies;

import android.content.Context;
import android.database.Cursor;
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

    private static final String LOG_TAG_UTILITY = Utility.class.getSimpleName();
    public static String sSortBy;

    public static ArrayList<Movie> getMoviesFromJsonStr(String jsonStr)
            throws JSONException {

        final String RESULTS = "results";

        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject initJson = new JSONObject(jsonStr);
            JSONArray results = initJson.getJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                try {
                    JSONObject movieData = results.getJSONObject(i);
                    Movie movie = getMovieFromJsonObject(movieData);
                    movies.add(movie);
                } catch (JSONException e) {
                    Log.e(LOG_TAG_UTILITY, "JSON EXCEPTION: ", e);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG_UTILITY, "MISSING FIELD ", e);
        }
        return movies;
    }

    public static Movie getMovieFromJsonObject(JSONObject movieJSON) {
        try {

//            http://docs.themoviedb.apiary.io/#reference/configuration/configuration/get?console=1
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_MOVIE_ID = "id";
            final String TMDB_TITLE = "title";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_VOTE_AVG = "vote_average";
            final String TMBD_OVERVIEW = "overview";

            String posterPath = movieJSON.getString(TMDB_POSTER_PATH);
            long movieId = movieJSON.getLong(TMDB_MOVIE_ID);
            String title = movieJSON.getString(TMDB_TITLE);
            String releaseDate = movieJSON.getString(TMDB_RELEASE_DATE);
            double voteAverage = movieJSON.getDouble(TMDB_VOTE_AVG);
            String synopsis = movieJSON.getString(TMBD_OVERVIEW);
            return new Movie(movieId, posterPath, releaseDate, title, voteAverage, synopsis);
        } catch (JSONException e) {
            Log.e(LOG_TAG_UTILITY, "JSON exception: ", e);
        }
        return null;
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

    public static String getTrailerKeyFromJson(String jsonStr) {
        try {
            JSONObject trailerJson;

            JSONObject initJson = new JSONObject(jsonStr);
            JSONArray results = initJson.getJSONArray("results");

            if (results.length() > 0) {
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
            Log.i(LOG_TAG_UTILITY, "SEARCH QUERY TERM: " + searchQuery);

            return getMoviesFromJsonStr(jsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG_UTILITY, "IOEXCEPTION " + e);
        } catch (JSONException e) {
            Log.e(LOG_TAG_UTILITY, "Error ", e);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG_UTILITY, "No Internet Connection", e);
        }
        return null;
    }
    //http://api.themoviedb.org/3/movie/297761?api_key=fd6a31594405033bb36da6d8fba873c5


    public static Movie getFavoriteMovie(Long movieId) {

        try {
            final String urlStr = "http://api.themoviedb.org/3/movie/" +
                    movieId + "api_key=" + BuildConfig.API_KEY;
            URL url = createUrl(urlStr);
            String jsonStr = makeHttpRequest(url);
            Log.i(LOG_TAG_UTILITY, jsonStr);
            return getMovieFromJsonObject(new JSONObject(jsonStr));
        } catch (IOException e) {
            Log.e(LOG_TAG_UTILITY, "IOEXCEPTION ", e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Movie> getFavoriteMovies(Context context) {

        ArrayList<Movie> movies = new ArrayList<>();
        ArrayList<Long> movieIds = getFavoriteMoviesIds(context);

        for (Long movieId : movieIds) {
            movies.add(getFavoriteMovie(movieId));
        }

        return movies;
    }

    public static ArrayList<Long> getFavoriteMoviesIds(Context context) {

        ArrayList<Long> movieIds = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null);

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long _id = cursor.getLong(0);
                    Log.i(LOG_TAG_UTILITY, "THE MOVIE ID FROM CURSOR IS: " + _id);
                    movieIds.add(_id);
                }
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG_UTILITY, "NULL POINTER FOR CURSOR", e);
        } catch (IndexOutOfBoundsException e) {
            Log.e(LOG_TAG_UTILITY, "INDEX OUT OF BOUNDS FROM CURSOR", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return movieIds;
    }


//    public static ArrayList<Movie> getFavoriteMovies(ArrayList<Long> movie_ids){
//
//    }

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


    private static URL createUrl(String baseUrl) {
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
