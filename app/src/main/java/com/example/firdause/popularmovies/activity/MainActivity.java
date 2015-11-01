package com.example.firdause.popularmovies.activity;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.firdause.popularmovies.R;
import com.example.firdause.popularmovies.fragments.FragmentDetailActivity;
import com.example.firdause.popularmovies.fragments.FragmentMainActivity;
import com.example.firdause.popularmovies.model.Movies;

/**
 * Created by Firdause on 10/25/15.
 */
public class MainActivity extends AppCompatActivity implements FragmentMainActivity.Callback{

    // Check for Tablet or Phone layout
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new FragmentDetailActivity(),
                                FragmentDetailActivity.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(Movies movies) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(FragmentDetailActivity.getDetailMovie(), movies);

            FragmentDetailActivity fragment = new FragmentDetailActivity();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, FragmentDetailActivity.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(FragmentDetailActivity.getDetailMovie(), movies);
            startActivity(intent);
        }
    }
}
