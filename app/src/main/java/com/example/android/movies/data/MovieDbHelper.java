package com.example.android.movies.data;

/**
 * Created by bplewis5 on 8/6/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.movies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie.db";

    private static final String TEXT_TYPE = " TEXT NOT NULL";
    private static final String INTEGER_TYPE = " INTEGER NOT NULL";
    private static final String REAL_TYPE = " REAL NOT NULL";
    private static final String BLOB_TYPE = " BLOB NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                    MovieEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_RELEASE_DATE + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_VOTE_AVG + REAL_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_SYNOPSIS + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_TRAILER + BLOB_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_USER_REVIEW + TEXT_TYPE + ");";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}