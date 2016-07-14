package com.example.android.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieFragment extends Fragment {
    private final String LOG_TAG = "MovieFragment";
    private final int MAX_RESULTS = 48;
    private MovieAdapter mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMovieAdapter.getItem(i);
                Intent intent = new Intent(getContext(), DetailActivity.class)
                        .putExtra("EXTRA_MOVIE", movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMovies();
    }

    private void updateMovies(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String searchWord = sharedPreferences.getString(
                getString(R.string.pref_search_key),
                getString(R.string.pref_default_value)
        );
        new FetchMovieTask().execute(searchWord);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private ArrayList<Movie> getMoviesFromJsonStr(String jsonStr, int maxResults)
                throws JSONException {

            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
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
                    String title;
                    String releaseDate;
                    double voteAverage;
                    String synopsis;

                    try {
                        JSONObject movie = results.getJSONObject(i);
                        posterPath = movie.getString(POSTER_PATH);
                        title = movie.getString(TITLE);
                        releaseDate = movie.getString(RELEASE_DATE);
                        voteAverage = movie.getDouble(VOTE_AVG);
                        synopsis = movie.getString(OVERVIEW);

                        movies.add(new Movie(posterPath, releaseDate, title, voteAverage, synopsis));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON exception: ", e);
                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Missing field ", e);
            }
            return movies;
        }
        //exampleApiKey = "http://api.themoviedb.org/3/movie/popular?api_key=";
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            String searchQuery = params[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String jsonStr = null;

            try{

                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + searchQuery + "?";
                final String API_PARAM = "api_key";

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.API_KEY)
                        .build();
                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = bufferedReader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return null;
                }
                jsonStr = buffer.toString();
            }
            catch (IOException e){
                Log.e(LOG_TAG, "Error: ", e);
            }
            finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null){
                    try{
                        bufferedReader.close();
                    }
                    catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing Stream", e);
                    }
                }
            }

            try{
                return getMoviesFromJsonStr(jsonStr, MAX_RESULTS);
            }catch (JSONException e){
                Log.e(LOG_TAG, "Error ", e);
            }
            catch (NullPointerException e){
                Log.e(LOG_TAG, "No Internet Connection", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null){
                mMovieAdapter.clear();
                for (Movie movie : movies){
                    mMovieAdapter.add(movie);
                }
            }
            super.onPostExecute(movies);
        }
    }

}
