package com.example.firdause.popularmovies.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.firdause.popularmovies.R;
import com.example.firdause.popularmovies.fragments.FragmentDetailActivity;

/**
 * Created by Firdause on 10/25/15.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(FragmentDetailActivity.getDetailMovie(),
                    getIntent().getParcelableExtra(FragmentDetailActivity.getDetailMovie()));

            FragmentDetailActivity fragment = new FragmentDetailActivity();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
