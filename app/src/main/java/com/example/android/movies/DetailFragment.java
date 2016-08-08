package com.example.android.movies;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.data.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private double mVoteAverage;
    private String mSynopsis;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        Movie mMovie = getActivity().getIntent().getParcelableExtra("EXTRA_MOVIE");

        long mMovieId = mMovie.getMovieId();
        Log.i(LOG_TAG, "THIS IS THE CHOSEN MOVIE'S ID: " + mMovieId);
        mTitle = mMovie.getTitle();
        mReleaseDate = mMovie.getReleaseDate();
        mPosterPath = mMovie.getPosterUrlStr();
        mVoteAverage = mMovie.getVoteAvg();
        mSynopsis = mMovie.getSynopsis();

        final ContentValues values =
                createValues(mMovieId, mTitle, mReleaseDate, mPosterPath, mVoteAverage, mSynopsis);

        Button addFavoriteButton = (Button) rootview.findViewById(R.id.detail_add_favorites_button);
        addFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().getContentResolver()
                        .insert(MovieContract.MovieEntry.CONTENT_URI, values);
            }
        });

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    private void updateMovie() {
        new FetchDetailsTask().execute();
    }

    public ContentValues createValues(
            long movieId, String title, String releaseDate, String posterPath,
            double voteAverage, String synopsis) {

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        values.put( MovieContract.MovieEntry.COLUMN_TITLE, title);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, voteAverage);
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);

        return values;
    }


    public class FetchDetailsTask extends AsyncTask<String, Void, RequestCreator> {

        @Override
        protected RequestCreator doInBackground(String... params) {
            final String BASE_URL = "http://image.tmdb.org/t/p/w500/";
            return Picasso.with(getContext()).load(BASE_URL + mPosterPath);
        }

        @Override
        protected void onPostExecute(RequestCreator requestCreator) {
            TextView titleTextView = (TextView) getActivity().findViewById(R.id.detail_title_textview);
            TextView releaseDateTextView = (TextView) getActivity().findViewById(R.id.detail_release_date_textview);
            TextView voteAvgTextView = (TextView) getActivity().findViewById(R.id.detail_vote_avg_textview);
            TextView summaryTextView = (TextView) getActivity().findViewById(R.id.detail_synopsis_textview);

            String releaseDateStr = "Release Date: " + mReleaseDate;
            String voteAvgStr = "Average Rating: " + mVoteAverage;

            titleTextView.setText(mTitle);
            voteAvgTextView.setText(voteAvgStr);
            summaryTextView.setText(mSynopsis);
            releaseDateTextView.setText(releaseDateStr);
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.detail_poster_imageview);
            if (imageView != null) {
                if (requestCreator != null) {
                    requestCreator.into(imageView);
                }
            }
        }
    }


}
