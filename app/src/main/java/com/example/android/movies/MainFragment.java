package com.example.android.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
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

    private void updateMovies() {
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

        //exampleApiKey = "http://api.themoviedb.org/3/movie/popular?api_key=";
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            return Utility.getMovies(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null){
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }
            super.onPostExecute(movies);
        }

    }
}