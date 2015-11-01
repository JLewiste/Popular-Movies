package com.example.firdause.popularmovies.customadapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.firdause.popularmovies.R;
import com.example.firdause.popularmovies.model.Movies;

import java.util.List;

/**
 * Created by Firdause on 10/27/15.
 */
public class MoviesAdapter extends BaseAdapter {

    // Instance variables for MoviesAdapter Class
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    private final Movies mMovies = new Movies();

    private List<Movies> moviesObjects;

    public MoviesAdapter(Context context, List<Movies> customObjects) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        moviesObjects = customObjects;
    }

    public Context getContext() {
        return mContext;
    }

    public void addObjects(Movies customObjects) {
        synchronized (mMovies) {
            moviesObjects.add(customObjects);
        }
        notifyDataSetChanged();
    }

    public void eraseObjects() {
        synchronized (mMovies) {
            moviesObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void insertData(List<Movies> data) {
        eraseObjects();
        for(Movies movies : data) {
            addObjects(movies);
        }
    }

    @Override
    public int getCount() {
        return moviesObjects.size();
    }

    @Override
    public Movies getItem(int position) {
        return moviesObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder mViewHolder;

        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.gridview_movies, parent, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        }

        final Movies movies = getItem(position);

        String posterLink = "http://image.tmdb.org/t/p/w185" + movies.getPosterA();
        mViewHolder = (ViewHolder) view.getTag();

        Glide.with(getContext()).load(posterLink).into(mViewHolder.moviePoster);
        mViewHolder.movieTitle.setText(movies.getTitle());

        return view;
    }

    // ViewHolder pattern ensures smooth scrolling when using GridView
    public static class ViewHolder {
        ImageView moviePoster;
        TextView movieTitle;

        public ViewHolder(View view) {
            moviePoster = (ImageView) view.findViewById(R.id.movie_image);
            movieTitle = (TextView) view.findViewById(R.id.movie_title);
        }
    }

}
