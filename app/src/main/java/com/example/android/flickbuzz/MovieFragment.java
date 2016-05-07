package com.example.android.flickbuzz;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by XCEED on 03-05-2016.
 */
public class MovieFragment extends Fragment {
    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.movie_grid, container, false);

        GridView gridview = (GridView) rootview.findViewById(R.id.movie_grid_view);


        return rootview;
    }

}
