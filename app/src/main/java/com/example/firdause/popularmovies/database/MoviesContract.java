package com.example.firdause.popularmovies.database;

import android.content.ContentResolver;
import android.content.ContentUris;

import android.net.Uri;

import android.provider.BaseColumns;

/**
 * Created by Firdause on 10/28/15.
 */
public class MoviesContract {

    // Instance variables for MoviesContract Class
    public static final String MOVIE_AUTHORITY = "com.example.firdause.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + MOVIE_AUTHORITY);

    public static final String MOVIE_PATH = "movie";


    public static final class MoviesDetails implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MOVIE_AUTHORITY + "/" + MOVIE_PATH;

        public static final String DB_TABLE = "movie";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_DATE = "date";
        public static final String MOVIE_RATING = "rating";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_POSTER_A = "posterA";
        public static final String MOVIE_POSTER_B = "posterB";
        public static final String MOVIE_OVERVIEW = "overview";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}