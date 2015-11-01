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
import com.example.firdause.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by Firdause on 10/28/15.
 */

public class TrailerAdapter extends BaseAdapter {

    // Instance variables for TrailerAdapter Class
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final Trailer mTrailer = new Trailer();

    private List<Trailer> trailerObjects;

    public TrailerAdapter(Context context, List<Trailer> customObjects) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        trailerObjects = customObjects;
    }

    public Context getContext() {
        return mContext;
    }

    public void addObjects(Trailer customObjects) {
        synchronized (mTrailer) {
            trailerObjects.add(customObjects);
        }
        notifyDataSetChanged();
    }

    public void eraseObjects() {
        synchronized (mTrailer) {
            trailerObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trailerObjects.size();
    }

    @Override
    public Trailer getItem(int position) {
        return trailerObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder mViewHolder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.gridview_trailer, parent, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        }

        final Trailer trailer = getItem(position);

        mViewHolder = (ViewHolder) view.getTag();

        String trailerPosterUrl = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        Glide.with(getContext()).load(trailerPosterUrl).into(mViewHolder.trailerImage);

        mViewHolder.trailerTitle.setText(trailer.getName());

        return view;
    }

    // ViewHolder pattern ensures smooth scrolling when using GridView
    public static class ViewHolder {
        public final ImageView trailerImage;
        public final TextView trailerTitle;

        public ViewHolder(View view) {
            trailerImage = (ImageView) view.findViewById(R.id.trailer_image);
            trailerTitle = (TextView) view.findViewById(R.id.trailer_title);
        }
    }

}