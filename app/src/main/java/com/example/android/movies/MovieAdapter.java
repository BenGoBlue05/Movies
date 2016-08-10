package com.example.android.movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bplewis5 on 7/11/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    final String BASE_URL = "http://image.tmdb.org/t/p/w500/";
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item,
                    parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) gridItemView.findViewById(R.id.poster_imageview);
            gridItemView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) gridItemView.getTag();
        Movie movie = getItem(position);
        String finalImgUrlStr = null;
        try {
            finalImgUrlStr = BASE_URL + movie.getPosterUrlStr();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "NULL POINTER", e);
        }

        if (finalImgUrlStr != null) {
            Picasso.with(getContext()).load(finalImgUrlStr).into(holder.imageView);
        }

        return gridItemView;
    }

    static class ViewHolder {
        public ImageView imageView;
    }
}
