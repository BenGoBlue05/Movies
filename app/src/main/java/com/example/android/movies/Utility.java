package com.example.android.movies;

import com.example.android.movies.data.MovieContract;

/**
 * Created by bplewis5 on 8/7/16.
 */
public class Utility {

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
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;


}
