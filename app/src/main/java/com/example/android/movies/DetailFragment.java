package com.example.android.movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;

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
        mMovie = getActivity().getIntent().getParcelableExtra("EXTRA_MOVIE");

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

    public class FetchDetailsTask extends AsyncTask<String, Void, RequestCreator> {

        @Override
        protected RequestCreator doInBackground(String... params) {
            final String BASE_URL = "http://image.tmdb.org/t/p/w500/";
            return Picasso.with(getContext()).load(BASE_URL + mMovie.getPosterUrlStr());
        }

        @Override
        protected void onPostExecute(RequestCreator requestCreator) {
            TextView titleTextView = (TextView) getActivity().findViewById(R.id.detail_title_textview);
            TextView releaseDateTextView = (TextView) getActivity().findViewById(R.id.detail_release_date_textview);
            TextView voteAvgTextView = (TextView) getActivity().findViewById(R.id.detail_vote_avg_textview);
            TextView summaryTextView = (TextView) getActivity().findViewById(R.id.detail_synopsis_textview);

            String releaseDateStr = "Release Date: " + mMovie.getReleaseDate();
            String voteAvgStr = "Average Rating: " + mMovie.getVoteAvg();

            titleTextView.setText(mMovie.getTitle());
            voteAvgTextView.setText(voteAvgStr);
            summaryTextView.setText(mMovie.getSynopsis());
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
