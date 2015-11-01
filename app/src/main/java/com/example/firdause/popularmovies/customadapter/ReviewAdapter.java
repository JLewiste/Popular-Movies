package com.example.firdause.popularmovies.customadapter;

import android.content.Context;

import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firdause.popularmovies.R;
import com.example.firdause.popularmovies.model.Review;

import java.util.List;

/**
 * Created by Firdause on 10/28/15.
 */

public class ReviewAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final Review mReview = new Review();

    private List<Review> reviewObjects;

    public ReviewAdapter(Context context, List<Review> customObjects) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reviewObjects = customObjects;
    }

    public void addObjects(Review customObjects) {
        synchronized (mReview) {
            reviewObjects.add(customObjects);
        }
        notifyDataSetChanged();
    }

    public void eraseObjects() {
        synchronized (mReview) {
            reviewObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reviewObjects.size();
    }

    @Override
    public Review getItem(int position) {
        return reviewObjects.get(position);
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
            view = mLayoutInflater.inflate(R.layout.gridview_review, parent, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        }

        final Review review = getItem(position);

        mViewHolder = (ViewHolder) view.getTag();
        mViewHolder.reviewAuthor.setText(review.getAuthor());
        mViewHolder.reviewContent.setText(Html.fromHtml(review.getContent()));

        return view;
    }

    // ViewHolder pattern ensures smooth scrolling when using GridView
    public static class ViewHolder {
        public final TextView reviewAuthor;
        public final TextView reviewContent;

        public ViewHolder(View view) {
            reviewAuthor = (TextView) view.findViewById(R.id.author_review);
            reviewContent = (TextView) view.findViewById(R.id.content_review);
        }
    }

}
 