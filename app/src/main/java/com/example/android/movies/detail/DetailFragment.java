package com.example.android.movies.detail;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
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

import com.example.android.movies.Movie;
import com.example.android.movies.R;
import com.example.android.movies.Utility;
import com.example.android.movies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String mTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private double mVoteAverage;
    private String mSynopsis;
    private long mMovieId;
    private String mTrailerKey;

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

        Movie movie = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_key));

        if (movie == null) {
            try {
                movie = getArguments().getParcelable(getString(R.string.movie_key));
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "ARGS ARE NULL", e);
            }
        }

        if (movie == null) {
            rootview.setVisibility(View.GONE);
            return rootview;
        } else{
            rootview.setVisibility(View.VISIBLE);
        }

        mTitle = movie.getTitle();
        mReleaseDate = movie.getReleaseDate();
        mPosterPath = movie.getPosterUrlStr();
        mVoteAverage = movie.getVoteAvg();
        mSynopsis = movie.getSynopsis();
        mMovieId = movie.getMovieId();


        Log.i(LOG_TAG, "THIS IS THE CHOSEN MOVIE'S ID: " + mMovieId);
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
        new FetchDetailsTask().execute(mMovieId);
    }

    public ContentValues createValues(
            long movieId, String title, String releaseDate, String posterPath,
            double voteAverage, String synopsis) {

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, voteAverage);
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);

        return values;
    }

    public class FetchDetailsTask extends AsyncTask<Long, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Long... movieIds) {
            if (movieIds.length == 0) {
                return null;
            }

            ArrayList<String> reviews = Utility.getReviews(movieIds[0]);
            mTrailerKey = Utility.getTrailerKey(movieIds[0]);

            if (reviews != null) {
                try {
                    Log.i(LOG_TAG, "GOT REVIEW" + reviews.get(0));
                } catch (IndexOutOfBoundsException e) {
                    Log.e(LOG_TAG, "INDEX OUT OF BOUNDS ", e);
                }
            }
            return reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<String> reviews) {
            final String posterUrl = "http://image.tmdb.org/t/p/w500/" + mPosterPath;
            final String trailerUrl = "https://www.youtube.com/watch?v=" + mTrailerKey;
            final String trailerThumbnailUrl = String.format("http://img.youtube.com/vi/%1$s/0.jpg", mTrailerKey);

            TextView titleTextView = (TextView) getActivity().findViewById(R.id.detail_title_textview);
            TextView releaseDateTextView = (TextView) getActivity().findViewById(R.id.detail_release_date_textview);
            TextView voteAvgTextView = (TextView) getActivity().findViewById(R.id.detail_vote_avg_textview);
            TextView summaryTextView = (TextView) getActivity().findViewById(R.id.detail_synopsis_textview);
            TextView userReviewTextView = (TextView) getActivity().findViewById((R.id.detail_review_textview));
            String releaseDateStr = "Release Date: " + mReleaseDate;
            String voteAvgStr = "Average Rating: " + mVoteAverage;


            try {
                userReviewTextView.setText(reviews.get(0));
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "NULL POINTER", e);
            } catch (IndexOutOfBoundsException e) {
                Log.e(LOG_TAG, "INDEX OUT OF BOUNDS");
            }

            titleTextView.setText(mTitle);
            voteAvgTextView.setText(voteAvgStr);
            summaryTextView.setText(mSynopsis);
            releaseDateTextView.setText(releaseDateStr);

            ImageView poster = (ImageView) getActivity().findViewById(R.id.detail_poster_imageview);
            if (poster != null) {
                Picasso.with(getContext()).load(posterUrl).into(poster);
            }

            ImageView trailer = (ImageView) getActivity().findViewById(R.id.detail_trailer_imageview);
            if (trailer != null) {
                Picasso.with(getContext()).load(trailerThumbnailUrl).into(trailer);
                trailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                        startActivity(trailerIntent);
                    }
                });
            }


        }


    }
}



