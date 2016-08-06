package com.example.android.movies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private ImageView mImageView;
    private Movie mMovie;
    private String mPosterPathStr;
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
        mPosterPathStr = mMovie.getPosterUrlStr();
        mImageView = (ImageView) getActivity().findViewById(R.id.detail_poster_imageview);

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    private void updateMovie() {
        new FetchDetailsTask().execute(mPosterPathStr);
    }

    public class FetchDetailsTask extends AsyncTask<String, Void, Bitmap> {

        private Bitmap downloadBitmap(String urlStr) {
            HttpURLConnection urlConnection = null;

            try {
                URL uri = new URL(urlStr);
                urlConnection = (HttpURLConnection) uri.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    return BitmapFactory.decodeStream(inputStream);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error downloading image from " + urlStr, e);
            } catch (NullPointerException e){
                Log.e(LOG_TAG, "Error", e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            final String BASE_URL = "http://image.tmdb.org/t/p/original/";
            String posterUrlStr = BASE_URL + params[0];
            return downloadBitmap(posterUrlStr);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            TextView titleTextView = (TextView) getActivity().findViewById(R.id.detail_title_textview);
            TextView releaseDateTextView = (TextView) getActivity().findViewById(R.id.detail_release_date_textview);
            TextView voteAvgTextView = (TextView) getActivity().findViewById(R.id.detail_vote_avg_textview);
            TextView summaryTextView  = (TextView) getActivity().findViewById(R.id.detail_synopsis_textview);

            String releaseDateStr = "Release Date: " + mMovie.getReleaseDate();
            String voteAvgStr = "Average Rating: " + mMovie.getVoteAvg();

            titleTextView.setText(mMovie.getTitle());
            voteAvgTextView.setText(voteAvgStr);
            summaryTextView.setText(mMovie.getSynopsis());
            releaseDateTextView.setText(releaseDateStr);
            mImageView = (ImageView) getActivity().findViewById(R.id.detail_poster_imageview);
            if (mImageView != null){
                if (bitmap != null){
                    mImageView.setImageBitmap(bitmap);
                }
            }
        }
    }


}
