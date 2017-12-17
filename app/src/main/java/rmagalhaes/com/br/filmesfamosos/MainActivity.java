package rmagalhaes.com.br.filmesfamosos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;

import rmagalhaes.com.br.filmesfamosos.adapters.MoviesAdapter;
import rmagalhaes.com.br.filmesfamosos.models.Movie;
import rmagalhaes.com.br.filmesfamosos.utils.NetworkUtils;
import rmagalhaes.com.br.filmesfamosos.api.ApiTypes;
import rmagalhaes.com.br.filmesfamosos.models.Movies;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;

import static rmagalhaes.com.br.filmesfamosos.api.ApiTypes.*;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterClickListener {


    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorText;
    private ProgressBar mProgress;
    private ArrayList<Movie> mMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mErrorText = findViewById(R.id.error);
        mProgress = findViewById(R.id.pb_loading_indicator);

        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        if(savedInstanceState == null) {
            new FetchMovies().execute(NOW_PLAYING);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.MOVIES, mMovies);
        outState.putParcelable(Constants.RV_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.RV_STATE)) {
            Parcelable recyclerViewState = savedInstanceState.getParcelable(Constants.RV_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIES)) {
            mMovies = savedInstanceState.getParcelableArrayList(Constants.MOVIES);
            mMoviesAdapter.setMoviesData(mMovies);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rating) {
            new FetchMovies().execute(TOP_RATED);
        } else if (id == R.id.popularity) {
            new FetchMovies().execute(POPULARITY);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_REFERRER, movie);
        startActivity(intent);
    }

    private void setErrorVisible() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
    }

    private void setRecyclerViewVisible() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchMovies extends AsyncTask<ApiTypes, Void, Movies> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movies doInBackground(ApiTypes... types) {
            ApiTypes type = null;
            Movies movies = null;
            URL moviesRequestUrl = null;
            String jsonStringify;

            if (types != null && types.length > 0 && types[0] != null) type = types[0];

            try {
                if (type != null) {
                    switch (type) {
                        case NOW_PLAYING:
                            moviesRequestUrl = NetworkUtils.buildURL(Constants.MOVIES_NOW_PLAYING);
                        break;

                        case TOP_RATED:
                            moviesRequestUrl = NetworkUtils.buildURL(Constants.MOVIES_TOP_RATED);
                        break;

                        case POPULARITY:
                            moviesRequestUrl = NetworkUtils.buildURL(Constants.MOVIES_POPULAR);
                        break;

                        default:
                        break;
                    }
                    jsonStringify = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                    movies = new Gson().fromJson(jsonStringify, Movies.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(Movies movies) {
            super.onPostExecute(movies);
            mProgress.setVisibility(View.INVISIBLE);

            if (movies != null && movies.getMovies().size() > 0) {
                setRecyclerViewVisible();
                mMovies = movies.getMovies();
                mMoviesAdapter.setMoviesData(mMovies);
            } else {
                setErrorVisible();
            }
        }

        @Override
        protected void onCancelled(Movies movies) {
            super.onCancelled(movies);
            mProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mProgress.setVisibility(View.INVISIBLE);
        }
    }
}
