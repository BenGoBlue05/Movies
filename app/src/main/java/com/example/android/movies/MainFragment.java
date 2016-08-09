package com.example.android.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragmentmain, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.most_popular_menu_fragmentmain:
                Utility.sSortBy = getString(R.string.popularity_query_param);
                break;
            case R.id.top_rated_menu_fragmentmain:
                Utility.sSortBy = getString(R.string.rating_query_param);
                break;
            case R.id.favorites_menu_fragmentmain:
                Log.i(LOG_TAG, "FAVORITES SELECTED");
                break;
            default:
                Log.i(LOG_TAG, "NO MENU ITEM RECOGNIZED");
        }

        Log.i(LOG_TAG, "PREFERENCE: " + Utility.sSortBy);
        new FetchMovieTask().execute(Utility.sSortBy);
        return true;
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
        try {
            if (!Utility.sSortBy.isEmpty()) {
                Log.i(LOG_TAG, "mPREF IS NOT NULL: " + Utility.sSortBy);
                new FetchMovieTask().execute(Utility.sSortBy);
                return;
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "mPREF IS NULL: " + Utility.sSortBy);
        }
        Utility.sSortBy = getString(R.string.pref_default_value);
        Log.i(LOG_TAG, "PREFERENCE MAIN FRAG: " + Utility.sSortBy);
        new FetchMovieTask().execute(Utility.sSortBy);

    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        //exampleApiKey = "http://api.themoviedb.org/3/movie/popular?api_key=";
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            Log.i(LOG_TAG, "PARAMS[0]: " + params[0]);
            return Utility.getMovies(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }
            super.onPostExecute(movies);
        }

    }
}