package com.example.android.flickbuzz;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailFragment extends Fragment {

    View rootView;

    public DetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            FetchMovieDetails fetchMovieDetails = new FetchMovieDetails();
            fetchMovieDetails.execute(movieId);
        }
        return rootView;
    }

    public void populateMovieDetails(long movieId) {

    }

    public class FetchMovieDetails extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;

            String moviesDetails = null;
            String api_key = getString(R.string.api_key_val);
            String movieId = params[0];
            try {
                final String MOVIE_DETAILS_BASE_URL = getString(R.string.movie_details_base_url);
                final String MOVIE_ID_PARAM = getString(R.string.tmdb_id_param);
                final String API_KEY_PARAM = getString(R.string.tmdb_api_param);

                Uri builtUri = Uri.parse(MOVIE_DETAILS_BASE_URL+movieId).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();

                URL url = new URL(builtUri.toString());

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

                moviesDetails = buffer.toString();
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

            return moviesDetails;
        }

        @Override
        protected void onPostExecute(String movieJsonDetails) {
            final String TMDB_TITLE = getString(R.string.tmdb_title);
            final String TMDB_RELEASE = getString(R.string.tmdb_release_date);
            final String TMDB_DURATION = getString(R.string.tmdb_runtime);
            final String TMDB_RATING = getString(R.string.tmdb_rating);
            final String TMDB_SYNOPSIS = getString(R.string.tmdb_synopsis);

            TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
            TextView releaseYear = (TextView) rootView.findViewById(R.id.text_release_date);
            TextView movieDuration = (TextView) rootView.findViewById(R.id.text_duration);
            TextView movieRating = (TextView) rootView.findViewById(R.id.text_rating);
            TextView movieSynopsis = (TextView) rootView.findViewById(R.id.textView_movieSynopsis);
            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.image_poster);

            try {
                JSONObject movieDetails = new JSONObject(movieJsonDetails);
                String movieTitleStr = movieDetails.getString(TMDB_TITLE);
                movieTitle.setText(movieTitleStr);
                releaseYear.setText(movieDetails.getString(TMDB_RELEASE));
                movieDuration.setText(movieDetails.getString(TMDB_DURATION));
                movieRating.setText(movieDetails.getString(TMDB_RATING));
                movieSynopsis.setText(movieDetails.getString(TMDB_SYNOPSIS));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
}
