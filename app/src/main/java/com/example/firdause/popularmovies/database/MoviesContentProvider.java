package com.example.firdause.popularmovies.database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.ContentProvider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;


/**
 * Created by Firdause on 10/28/15.
 */
public class MoviesContentProvider extends ContentProvider {

    // Instance variables for MoviesContentProvider Class
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private MoviesDatabase mMoviesDatabase;

    static final int PROVIDER_MOVIE = 100;

    static UriMatcher buildUriMatcher() {
        final String authority = MoviesContract.MOVIE_AUTHORITY;
        final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(authority, MoviesContract.MOVIE_PATH, PROVIDER_MOVIE);

        return mUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMoviesDatabase = new MoviesDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor mCursor;
        switch (mUriMatcher.match(uri)) {
            case PROVIDER_MOVIE: {
                mCursor = mMoviesDatabase.getReadableDatabase().query(
                        MoviesContract.MoviesDetails.DB_TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri detected: " + uri);
        }

        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int matching = mUriMatcher.match(uri);

        switch (matching) {
            case PROVIDER_MOVIE:
                return MoviesContract.MoviesDetails.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Error uri detected: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mMoviesDatabase.getWritableDatabase();
        Uri uriReturn;

        switch (mUriMatcher.match(uri)) {
            case PROVIDER_MOVIE: {
                long _id = db.insert(MoviesContract.MoviesDetails.DB_TABLE, null, values);
                if (_id > 0) {
                    uriReturn = MoviesContract.MoviesDetails.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into this " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Error uri detected: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMoviesDatabase.getWritableDatabase();
        int deletedDbRows;

        if (null == selection) selection = "1";
        switch (mUriMatcher.match(uri)) {
            case PROVIDER_MOVIE:
                deletedDbRows = db.delete(
                        MoviesContract.MoviesDetails.DB_TABLE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Error uri: " + uri);
        }

        if (deletedDbRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedDbRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMoviesDatabase.getWritableDatabase();
        int updateDbRows;

        switch (mUriMatcher.match(uri)) {
            case PROVIDER_MOVIE:
                updateDbRows = db.update(MoviesContract.MoviesDetails.DB_TABLE, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updateDbRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateDbRows;
    }

}
