package com.example.android.movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by bplewis5 on 8/6/16.
 */
public class MovieProvider extends ContentProvider{

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_TABLE_ID = 101;
    static final int MOVIE_ID = 200;

    static final String sMovieTableIdSelection =
            MovieContract.MovieEntry._ID + " = ?";
    static final String sMovieIdSelection =
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_TABLE_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#/#", MOVIE_ID);
        return uriMatcher;
    }

    private Cursor getMovieByTableId(Uri uri, String[] projection, String sortOrder){
        long id = MovieContract.MovieEntry.getIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                sMovieTableIdSelection,
                new String[] {Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_TABLE_ID:
                retCursor = getMovieByTableId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TABLE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                 long id = mOpenHelper.getWritableDatabase().insert(
                         MovieContract.MovieEntry.TABLE_NAME, null, contentValues
                );
                Log.i(LOG_TAG, "HERE IS THE ID: " + id);
                Log.i(LOG_TAG, "THIS IS NEW");
                if (id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(id);
                else
                    throw new SQLException("FAILED TO INSERT ROW INTO " + uri);
                break;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_TABLE_ID:
                long id = MovieContract.MovieEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, sMovieTableIdSelection, new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rowsUpdated;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case MOVIE_TABLE_ID:
                long id = MovieContract.MovieEntry.getIdFromUri(uri);
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues, sMovieTableIdSelection, new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
