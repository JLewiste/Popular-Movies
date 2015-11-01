package com.example.firdause.popularmovies.fragments;

import android.content.Context;

import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.GridView;

import com.example.firdause.popularmovies.R;
import com.example.firdause.popularmovies.customadapter.MoviesAdapter;
import com.example.firdause.popularmovies.database.MoviesContract;
import com.example.firdause.popularmovies.model.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.net.Uri;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

/**
 * Created by Firdause on 10/25/15.
 */
public class FragmentMainActivity extends Fragment {

    private GridView mGridView;

    private MoviesAdapter mMoviesAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String POPULARITY_DESC = "popularity.desc";
    private static final String RATING_DESC = "vote_average.desc";
    private static final String FAVORITE = "favorite";
    private static final String MOVIES_KEY = "movies";

    public static final int _ID = 0;
    public static final int MOVIE_ID = 1;
    public static final int MOVIE_DATE = 2;
    public static final int MOVIE_RATING = 3;
    public static final int MOVIE_TITLE = 4;
    public static final int MOVIE_POSTER_A = 5;
    public static final int MOVIE_POSTER_B = 6;
    public static final int MOVIE_OVERVIEW = 7;

    private String mSortMoviesBy = POPULARITY_DESC;

    private ArrayList<Movies> mMovies = null;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesDetails._ID,
            MoviesContract.MoviesDetails.MOVIE_ID,
            MoviesContract.MoviesDetails.MOVIE_DATE,
            MoviesContract.MoviesDetails.MOVIE_RATING,
            MoviesContract.MoviesDetails.MOVIE_TITLE,
            MoviesContract.MoviesDetails.MOVIE_POSTER_A,
            MoviesContract.MoviesDetails.MOVIE_POSTER_B,
            MoviesContract.MoviesDetails.MOVIE_OVERVIEW
    };

    public FragmentMainActivity() {
    }

    // Fragment interface method
    public interface Callback {
        void onItemSelected(Movies movie);
    }

    // Handle menu events
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main_menu, menu);

        MenuItem sortPopularity = menu.findItem(R.id.menu_bar_popularity);
        MenuItem sortRating = menu.findItem(R.id.menu_bar_rating);
        MenuItem sortFavorite = menu.findItem(R.id.menu_bar_favorite);

        if (mSortMoviesBy.contentEquals(POPULARITY_DESC)) {
            if (!sortPopularity.isChecked()) {
                sortPopularity.setChecked(true);
            }
        } else if (mSortMoviesBy.contentEquals(RATING_DESC)) {
            if (!sortRating.isChecked()) {
                sortRating.setChecked(true);
            }
        } else if (mSortMoviesBy.contentEquals(FAVORITE)) {
            if (!sortPopularity.isChecked()) {
                sortFavorite.setChecked(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.menu_bar_popularity:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                mSortMoviesBy = POPULARITY_DESC;
                updateMovies(mSortMoviesBy);

                return true;

            case R.id.menu_bar_rating:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                mSortMoviesBy = RATING_DESC;
                updateMovies(mSortMoviesBy);

                return true;

            case R.id.menu_bar_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                mSortMoviesBy = FAVORITE;
                updateMovies(mSortMoviesBy);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity_main, container, false);

        mGridView = (GridView) view.findViewById(R.id.primary_grid_view);
        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movies>());

        mGridView.setAdapter(mMoviesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movie = mMoviesAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                mSortMoviesBy = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mMoviesAdapter.insertData(mMovies);
            } else {
                updateMovies(mSortMoviesBy);
            }
        } else {
            updateMovies(mSortMoviesBy);
        }

        return view;
    }

    private void updateMovies(String sort_by) {

        if (!sort_by.contentEquals(FAVORITE)) {
            new LoadMovies().execute(sort_by);
        } else {
            new LoadFavoriteMovies(getActivity()).execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mSortMoviesBy.contentEquals(POPULARITY_DESC)) {
            outState.putString(SORT_SETTING_KEY, mSortMoviesBy);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    public class LoadMovies extends AsyncTask<String, Void, List<Movies>> {

        private final String LOG_TAG = LoadMovies.class.getSimpleName();

        private List<Movies> getMoviesDataFromJson(String jsonStr) throws JSONException {

            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movies> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movies movieModel = new Movies(movie);
                results.add(movieModel);
            }

            return results;
        }

        @Override
        protected List<Movies> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String API_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(API_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, getString(R.string.my_api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            if (movies != null) {
                if (mMoviesAdapter != null) {
                    mMoviesAdapter.insertData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }

    public class LoadFavoriteMovies extends AsyncTask<Void, Void, List<Movies>> {

        private Context mContext;

        public LoadFavoriteMovies(Context context) {
            mContext = context;
        }

        private List<Movies> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<Movies> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movies movie = new Movies(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<Movies> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MoviesContract.MoviesDetails.CONTENT_URI, MOVIE_COLUMNS, null, null, null);

            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            if (movies != null) {
                if (mMoviesAdapter != null) {
                    mMoviesAdapter.insertData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }
}
