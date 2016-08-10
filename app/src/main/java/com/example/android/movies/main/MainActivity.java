package com.example.android.movies.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.movies.R;
import com.example.android.movies.Utility;
import com.example.android.movies.detail.DetailFragment;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    public static final String DETAILFRAGMENT_TAG = "DETTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            Utility.TWO_PANE = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            Utility.TWO_PANE = false;
        }
    }

}
