package com.example.firdause.popularmovies.database;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Firdause on 10/28/15.
 */
public class MoviesDatabase extends SQLiteOpenHelper {

    // Instance variables for MoviesDatabase Class
    private static final int DB_VERSION = 1;

    static final String DB_NAME = "movie.db";

    public MoviesDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MoviesDetails.DB_TABLE + " (" +

                MoviesContract.MoviesDetails._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesDetails.MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.MoviesDetails.MOVIE_DATE + " TEXT, " +
                MoviesContract.MoviesDetails.MOVIE_RATING + " INTEGER, " +
                MoviesContract.MoviesDetails.MOVIE_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesDetails.MOVIE_POSTER_A + " TEXT, " +
                MoviesContract.MoviesDetails.MOVIE_POSTER_B + " TEXT, " +
                MoviesContract.MoviesDetails.MOVIE_OVERVIEW + " TEXT);";

        database.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int originalVersion, int upgradedVersion) {

        database.execSQL("DROP DB_TABLE IF EXISTS " + MoviesContract.MoviesDetails.DB_TABLE);
        onCreate(database);
    }

}
