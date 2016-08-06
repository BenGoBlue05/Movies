package com.example.android.movies.data;

import android.provider.BaseColumns;

/**
 * Created by bplewis5 on 8/6/16.
 */
public class MovieContract {

    public static abstract class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_USER_REVIEW = "user_review";
    }

}
