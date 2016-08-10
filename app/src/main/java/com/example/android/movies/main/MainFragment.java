package com.example.android.movies.main;

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

import com.example.android.movies.Movie;
import com.example.android.movies.MovieAdapter;
import com.example.android.movies.R;
import com.example.android.movies.Utility;
import com.example.android.movies.detail.DetailActivity;
import com.example.android.movies.detail.DetailFragment;

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
                Utility.sSortBy = getString(R.string.favorites_query_param);
                break;
            default:
                Log.i(LOG_TAG, "NO MENU ITEM RECOGNIZED");
        }

        Log.i(LOG_TAG, "PREFERENCE: " + Utility.sSortBy);
        new FetchMoviesTask().execute(Utility.sSortBy);
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
                        .putExtra(getString(R.string.movie_key), movie);
                if (!Utility.TWO_PANE) {
                    startActivity(intent);
                } else {
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.movie_key), movie);
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.detail_container, detailFragment, MainActivity.DETAILFRAGMENT_TAG)
                            .commit();
                }
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
                new FetchMoviesTask().execute(Utility.sSortBy);
                return;
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "mPREF IS NULL: " + Utility.sSortBy);
        }
        Utility.sSortBy = getString(R.string.default_query_param);
        Log.i(LOG_TAG, "PREFERENCE MAIN FRAG: " + Utility.sSortBy);
        new FetchMoviesTask().execute(Utility.sSortBy);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        //exampleApiKey = "http://api.themoviedb.org/3/movie/popular?api_key=";
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            Log.i(LOG_TAG, "PARAMS[0]: " + params[0]);

            if (Utility.sSortBy.equals(getString(R.string.rating_query_param)) ||
                    Utility.sSortBy.equals(getString(R.string.popularity_query_param))) {
                return Utility.getMovies(params[0]);
            }

            if (Utility.sSortBy.equals(getString(R.string.favorites_query_param))) {
                return Utility.getFavoriteMovies(getContext());
            }

            return null;
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