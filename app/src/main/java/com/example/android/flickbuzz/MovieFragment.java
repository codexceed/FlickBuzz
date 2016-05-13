package com.example.android.flickbuzz;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XCEED on 03-05-2016.
 */
public class MovieFragment extends Fragment {
    long[] movieIdArray;
    String[] posterPaths;
    View rootview;

    public MovieFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragmentmenu, menu);
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.movie_grid, container, false);
        refresh();
        GridView gridview = (GridView) rootview.findViewById(R.id.movie_grid_view);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieId = String.valueOf(movieIdArray[position]);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieId);
                startActivity(intent);
            }
        });
        return rootview;
    }

    public void refresh() {
        FetchMovieData movieData = new FetchMovieData();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = sharedPreferences.getString(getString(R.string.pref_key_sort_by), "popularity.desc");
        movieData.execute(sort_by);
    }


    public class ImageAdapter extends BaseAdapter {
        public Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            if(posterPaths != null)
            return posterPaths.length;
            else return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if(convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(1, 1, 1, 1);
                imageView.setLayoutParams(new GridView.LayoutParams(mContext.getResources().getInteger(R.integer.poster_width), mContext.getResources().getInteger(R.integer.poster_height)));
            }
            else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w342"+posterPaths[position])
                    .into(imageView);
            return imageView;
        }


    }


    public class FetchMovieData extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = MovieFragment.class.getSimpleName();

        private String[] getMovieIdsFromJsonStr(String moviesJsonStr)
        throws JSONException{
            final String TMDB_RESULTS = getString(R.string.tmdb_results_param);
            final String TMDB_ID = getString(R.string.tmdb_id_param);
            final String TMDB_POSTER = getString(R.string.tmdb_poster_param);

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            movieIdArray = new long[movieArray.length()];
            posterPaths = new String[movieArray.length()];
            for (int i = 0;i < movieArray.length();i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);
                movieIdArray[i] = movieObject.getLong(TMDB_ID);

                posterPaths[i] = movieObject.getString(TMDB_POSTER);
            }

            return posterPaths;
        }

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            String moviesData = null;
            String sort_by = params[0];
            String api_key = getString(R.string.api_key_val);

            try {
                final String MOVIE_DB_BASE_URL = getString(R.string.movie_db_base_url);
                final String SORT_PARAM = getString(R.string.tmdb_sort_param);
                final String API_KEY = getString(R.string.tmdb_api_param);


                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_by)
                        .appendQueryParameter(API_KEY, api_key)
                        .build();

                URL url =  new URL(builtUri.toString());

                Log.v(LOG_TAG, "BUILT URI: "+ builtUri.toString());


                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
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

                moviesData = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceHolderFragment", "Error ", e);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceHolderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieIdsFromJsonStr(moviesData);
            } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] posterPathArray) {
            GridView gridview = (GridView) rootview.findViewById(R.id.movie_grid_view);
            gridview.setAdapter(new ImageAdapter(getActivity()));
        }
    }

}


