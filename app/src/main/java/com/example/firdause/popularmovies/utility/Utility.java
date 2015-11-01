package com.example.firdause.popularmovies.utility;

import android.content.Context;

import android.database.Cursor;

import com.example.firdause.popularmovies.database.MoviesContract;

/**
 * Created by Firdause on 10/31/15.
 */
public class Utility {

    public static int favoritedMovies(Context context, int id) {

        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MoviesDetails.CONTENT_URI, null,
                MoviesContract.MoviesDetails.MOVIE_ID + " = ?",
                new String[]{Integer.toString(id)},
                null
        );

        int numberOfRows = cursor.getCount();
        cursor.close();

        return numberOfRows;
    }

    public static String moviePosterUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }

}