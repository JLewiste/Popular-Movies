package com.example.firdause.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.firdause.popularmovies.fragments.FragmentMainActivity;

/**
 * Created by Firdause on 10/27/15.
 */
public class Movies implements Parcelable {

    // Instance variables for Movies Class
    private int id;
    private String date;
    private int rating;
    private String title;
    private String posterA;
    private String posterB;
    private String overview;

    public Movies() {

    }

    private Movies(Parcel in) {
        id = in.readInt();
        date = in.readString();
        rating = in.readInt();
        title = in.readString();
        posterA = in.readString();
        posterB = in.readString();
        overview = in.readString();
    }

    public Movies(JSONObject movies) throws JSONException {
        this.id = movies.getInt("id");
        this.date = movies.getString("release_date");
        this.rating = movies.getInt("vote_average");
        this.title = movies.getString("original_title");
        this.posterA = movies.getString("poster_path");
        this.posterB = movies.getString("backdrop_path");
        this.overview = movies.getString("overview");
    }

    public Movies(Cursor cursor) {
        this.id = cursor.getInt(FragmentMainActivity.MOVIE_ID);
        this.date = cursor.getString(FragmentMainActivity.MOVIE_DATE);
        this.rating = cursor.getInt(FragmentMainActivity.MOVIE_RATING);
        this.title = cursor.getString(FragmentMainActivity.MOVIE_TITLE);
        this.posterA = cursor.getString(FragmentMainActivity.MOVIE_POSTER_A);
        this.posterB = cursor.getString(FragmentMainActivity.MOVIE_POSTER_B);
        this.overview = cursor.getString(FragmentMainActivity.MOVIE_OVERVIEW);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterA() {
        return posterA;
    }

    public String getPosterB() {
        return posterB;
    }

    public String getOverview() {
        return overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movies> CREATOR =
            new Parcelable.Creator<Movies>() {
                public Movies createFromParcel(Parcel in) {
                    return new Movies(in);
                }

                public Movies[] newArray(int size) {
                    return new Movies[size];
                }
            };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(date);
        parcel.writeInt(rating);
        parcel.writeString(title);
        parcel.writeString(posterA);
        parcel.writeString(posterB);
        parcel.writeString(overview);
    }

}
