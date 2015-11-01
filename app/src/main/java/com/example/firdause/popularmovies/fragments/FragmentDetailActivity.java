package com.example.firdause.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firdause.popularmovies.R;
import com.example.firdause.popularmovies.utility.Utility;
import com.example.firdause.popularmovies.customadapter.ReviewAdapter;
import com.example.firdause.popularmovies.customadapter.TrailerAdapter;
import com.example.firdause.popularmovies.database.MoviesContract;
import com.example.firdause.popularmovies.model.Movies;
import com.example.firdause.popularmovies.model.Review;
import com.example.firdause.popularmovies.model.Trailer;
import com.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Firdause on 10/25/15.
 */
public class FragmentDetailActivity extends Fragment {
    // For debugging purpose
    public static final String TAG = FragmentDetailActivity.class.getSimpleName();

    private static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    private Movies mMovies;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private ScrollView mContainerScroll;

    private Toast mToast;

    private LinearListView mMoviesTrailersLinear;
    private LinearListView mMoviesReviewsLinear;

    private CardView mMoviesReviewsCard;
    private CardView mMoviesTrailersCard;

    private ImageView mMoviesPosterImage;

    private TextView mMoviesTitleText;
    private TextView mMoviesOverviewText;
    private TextView mMoviesReleaseDateText;
    private TextView mMoviesVoteAverageText;

    public FragmentDetailActivity() {
    }

    public static String getDetailMovie() {
        return DETAIL_MOVIE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMovies != null) {
            inflater.inflate(R.menu.fragment_detail_menu, menu);

            final MenuItem bookmarkFavorite = menu.findItem(R.id.clickable_star_favorite);

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utility.favoritedMovies(getActivity(), mMovies.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorited) {
                    bookmarkFavorite.setIcon(isFavorited == 1 ?
                            R.drawable.abc_btn_rating_star_on_mtrl_alpha :
                            R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                }
            }.execute();

        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.clickable_star_favorite:
                if (mMovies != null) {
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utility.favoritedMovies(getActivity(), mMovies.getId());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavorited) {
                            // If the movie is in the favorite section
                            if (isFavorited == 1) {
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MoviesContract.MoviesDetails.CONTENT_URI,
                                                MoviesContract.MoviesDetails.MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(mMovies.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // If the movie is not in the favorite section
                            else {
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MoviesContract.MoviesDetails.MOVIE_ID, mMovies.getId());
                                        values.put(MoviesContract.MoviesDetails.MOVIE_DATE, mMovies.getDate());
                                        values.put(MoviesContract.MoviesDetails.MOVIE_RATING, mMovies.getRating());
                                        values.put(MoviesContract.MoviesDetails.MOVIE_TITLE, mMovies.getTitle());
                                        values.put(MoviesContract.MoviesDetails.MOVIE_POSTER_A, mMovies.getPosterA());
                                        values.put(MoviesContract.MoviesDetails.MOVIE_POSTER_B, mMovies.getPosterB());
                                        values.put(MoviesContract.MoviesDetails.MOVIE_OVERVIEW, mMovies.getOverview());

                                        return getActivity().getContentResolver().insert(MoviesContract.MoviesDetails.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovies = arguments.getParcelable(FragmentDetailActivity.DETAIL_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_activity_detail, container, false);

        mContainerScroll = (ScrollView) rootView.findViewById(R.id.detail_layout);

        if (mMovies != null) {
            mContainerScroll.setVisibility(View.VISIBLE);
        } else {
            mContainerScroll.setVisibility(View.INVISIBLE);
        }

        mMoviesPosterImage = (ImageView) rootView.findViewById(R.id.detail_movies_poster);

        mMoviesTrailersLinear = (LinearListView) rootView.findViewById(R.id.detail_movies_trailers);
        mMoviesReviewsLinear = (LinearListView) rootView.findViewById(R.id.detail_movies_reviews);

        mMoviesReviewsCard = (CardView) rootView.findViewById(R.id.detail_movies_reviews_cardview);
        mMoviesTrailersCard = (CardView) rootView.findViewById(R.id.detail_movies_trailers_cardview);

        mMoviesTitleText = (TextView) rootView.findViewById(R.id.detail_movies_title);
        mMoviesOverviewText = (TextView) rootView.findViewById(R.id.detail_movies_overview);
        mMoviesReleaseDateText = (TextView) rootView.findViewById(R.id.detail_movies_date);
        mMoviesVoteAverageText = (TextView) rootView.findViewById(R.id.detail_movies_vote_average);

        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
        mMoviesTrailersLinear.setAdapter(mTrailerAdapter);

        // Launch movie trailer via Intent
        mMoviesTrailersLinear.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view,
                                    int position, long id) {
                Trailer trailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
            }
        });

        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
        mMoviesReviewsLinear.setAdapter(mReviewAdapter);

        if (mMovies != null) {

            String poster_url = Utility.moviePosterUrl(342, mMovies.getPosterB());

            Glide.with(this).load(poster_url).into(mMoviesPosterImage);

            mMoviesTitleText.setText(mMovies.getTitle());
            mMoviesOverviewText.setText(mMovies.getOverview());

            String releaseDate = mMovies.getDate();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                String date = DateUtils.formatDateTime(getActivity(),
                        dateFormat.parse(releaseDate).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                mMoviesReleaseDateText.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mMoviesVoteAverageText.setText(Integer.toString(mMovies.getRating()));
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovies != null) {
            new LoadMoviesTrailers().execute(Integer.toString(mMovies.getId()));
            new LoadMoviesReviews().execute(Integer.toString(mMovies.getId()));
        }
    }


    public class LoadMoviesTrailers extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = LoadMoviesTrailers.class.getSimpleName();

        private List<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> results = new ArrayList<>();

            // List down trailers that are available in YouTube Only
            for(int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailer trailerModel = new Trailer(trailer);
                    results.add(trailerModel);
                }
            }

            return results;
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.my_api_key))
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
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null) {
                if (trailers.size() > 0) {
                    mMoviesTrailersCard.setVisibility(View.VISIBLE);
                    if (mTrailerAdapter != null) {
                        mTrailerAdapter.eraseObjects();
                        for (Trailer trailer : trailers) {
                            mTrailerAdapter.addObjects(trailer);
                        }
                    }
                }
            }
        }
    }

    public class LoadMoviesReviews extends AsyncTask<String, Void, List<Review>> {

        private final String LOG_TAG = LoadMoviesReviews.class.getSimpleName();

        private List<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Review> results = new ArrayList<>();

            for(int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Review(review));
            }

            return results;
        }

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.my_api_key))
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
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null) {
                if (reviews.size() > 0) {
                    mMoviesReviewsCard.setVisibility(View.VISIBLE);
                    if (mReviewAdapter != null) {
                        mReviewAdapter.eraseObjects();
                        for (Review review : reviews) {
                            mReviewAdapter.addObjects(review);
                        }
                    }
                }
            }
        }
    }
}
