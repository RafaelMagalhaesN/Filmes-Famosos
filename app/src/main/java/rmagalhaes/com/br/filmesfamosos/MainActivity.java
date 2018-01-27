package rmagalhaes.com.br.filmesfamosos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;

import rmagalhaes.com.br.filmesfamosos.adapters.MoviesAdapter;
import rmagalhaes.com.br.filmesfamosos.adapters.MoviesCursorAdapter;
import rmagalhaes.com.br.filmesfamosos.data.MoviesContract;
import rmagalhaes.com.br.filmesfamosos.databinding.ActivityMainBinding;
import rmagalhaes.com.br.filmesfamosos.models.Movie;
import rmagalhaes.com.br.filmesfamosos.utils.NetworkUtils;
import rmagalhaes.com.br.filmesfamosos.api.ApiTypes;
import rmagalhaes.com.br.filmesfamosos.models.Movies;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;

import static rmagalhaes.com.br.filmesfamosos.api.ApiTypes.*;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterClickListener, MoviesCursorAdapter.MoviesCursorAdapterClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {


    private MoviesAdapter mMoviesAdapter;
    private MoviesCursorAdapter mMoviesCursorAdapter;

    private ActivityMainBinding mBinding;

    private static ApiTypes mCurrentApiType = LOCAL;
    private ArrayList<Movie> mMovies = new ArrayList<>();
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int mCurrentPage = 1;
    private static final int TASK_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        int spanCount = getResources().getInteger(R.integer.grid_columns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);

        mBinding.recyclerView.setLayoutManager(gridLayoutManager);
        mBinding.recyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mMoviesCursorAdapter = new MoviesCursorAdapter(this);

        if (mCurrentApiType == LOCAL) {
            mBinding.recyclerView.setAdapter(mMoviesCursorAdapter);
        } else {
            mBinding.recyclerView.setAdapter(mMoviesAdapter);
        }

        initEndlessRecyclerViewScroll(gridLayoutManager);

        if (savedInstanceState == null) {
            if (mCurrentApiType != LOCAL) {
                new FetchMovies().execute(mCurrentApiType);
            } else {
                getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentApiType == LOCAL) getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    private void initEndlessRecyclerViewScroll(GridLayoutManager gridLayoutManager) {
        if (gridLayoutManager == null) return;

        mScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        mBinding.recyclerView.addOnScrollListener(mScrollListener);
    }

    private void loadNextDataFromApi(int page) {
        mCurrentPage = ++page;
        if (mCurrentApiType != LOCAL) new FetchMovies().execute(mCurrentApiType);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.MOVIES, mMovies);
        outState.putParcelable(Constants.RV_STATE, mBinding.recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt(Constants.PAGE_STATE, mCurrentPage);
        outState.putParcelable(Constants.SCROLL_STATE, mScrollListener.mLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.RV_STATE)) {
            Parcelable recyclerViewState = savedInstanceState.getParcelable(Constants.RV_STATE);
            mBinding.recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIES)) {
            mMovies = savedInstanceState.getParcelableArrayList(Constants.MOVIES);
            mMoviesAdapter.setMoviesData(mMovies);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.PAGE_STATE)) {
            mCurrentPage = savedInstanceState.getInt(Constants.PAGE_STATE);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.SCROLL_STATE)) {
            Parcelable scrollViewState = savedInstanceState.getParcelable(Constants.SCROLL_STATE);
            mScrollListener.mLayoutManager.onRestoreInstanceState(scrollViewState);
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
        mMovies.clear();
        mCurrentPage = 1;

        if (id == R.id.rating) {
            mCurrentApiType = TOP_RATED;
            mBinding.recyclerView.setAdapter(mMoviesAdapter);
            new FetchMovies().execute(mCurrentApiType);
        } else if (id == R.id.popularity) {
            mCurrentApiType = POPULARITY;
            mBinding.recyclerView.setAdapter(mMoviesAdapter);
            new FetchMovies().execute(mCurrentApiType);
        } else if (id == R.id.favorites) {
            mBinding.recyclerView.setAdapter(mMoviesCursorAdapter);
            mCurrentApiType = LOCAL;
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_REFERRER, movie);
        startActivity(intent);
    }

    private void setErrorVisible(boolean isFavorite) {
        if (isFavorite) {
            mBinding.error.setText(getResources().getString(R.string.error_favorite));
        } else {
            mBinding.error.setText(getResources().getString(R.string.error));
        }
        mBinding.recyclerView.setVisibility(View.INVISIBLE);
        mBinding.error.setVisibility(View.VISIBLE);
    }

    private void setRecyclerViewVisible() {
        mBinding.recyclerView.setVisibility(View.VISIBLE);
        mBinding.error.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mMoviesData = null;

            @Override
            protected void onStartLoading() {
                if (mMoviesData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMoviesData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMoviesData = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            setRecyclerViewVisible();
            mMoviesCursorAdapter.swapCursor(data);
        } else {
            setErrorVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesCursorAdapter.swapCursor(null);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchMovies extends AsyncTask<ApiTypes, Void, Movies> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
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
                            moviesRequestUrl = NetworkUtils.buildURL(Constants.MOVIES_NOW_PLAYING, mCurrentPage);
                        break;

                        case TOP_RATED:
                            moviesRequestUrl = NetworkUtils.buildURL(Constants.MOVIES_TOP_RATED, mCurrentPage);
                        break;

                        case POPULARITY:
                            moviesRequestUrl = NetworkUtils.buildURL(Constants.MOVIES_POPULAR, mCurrentPage);
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
            mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movies != null && movies.getMovies().size() > 0) {
                setRecyclerViewVisible();
                if (mMovies.isEmpty()) {
                    mMovies = movies.getMovies();
                    mMoviesAdapter.setMoviesData(mMovies);
                    mMoviesAdapter.notifyDataSetChanged();
                } else {
                    int positionStart = mMoviesAdapter.getItemCount();
                    mMovies.addAll(movies.getMovies());
                    int itemCount = mMovies.size() - 1;
                    mMoviesAdapter.notifyItemRangeInserted(positionStart, itemCount);
                }
            } else {
                setErrorVisible(false);
            }
        }

        @Override
        protected void onCancelled(Movies movies) {
            super.onCancelled(movies);
            mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
