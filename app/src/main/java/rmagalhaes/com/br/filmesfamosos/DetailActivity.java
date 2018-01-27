package rmagalhaes.com.br.filmesfamosos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import rmagalhaes.com.br.filmesfamosos.adapters.ReviewAdapter;
import rmagalhaes.com.br.filmesfamosos.adapters.VideoAdapter;
import rmagalhaes.com.br.filmesfamosos.data.MoviesActions;
import rmagalhaes.com.br.filmesfamosos.databinding.ActivityDetailBinding;
import rmagalhaes.com.br.filmesfamosos.models.Movie;
import rmagalhaes.com.br.filmesfamosos.models.Review;
import rmagalhaes.com.br.filmesfamosos.models.Reviews;
import rmagalhaes.com.br.filmesfamosos.models.Video;
import rmagalhaes.com.br.filmesfamosos.models.Videos;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;
import rmagalhaes.com.br.filmesfamosos.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity{

    private ActivityDetailBinding mBinding;
    private Movie mMovie;
    private ReviewAdapter mReviewAdapter;
    private VideoAdapter mVideoAdapter;
    private final ArrayList<Video> mVideos = new ArrayList<>();
    private final ArrayList<Review> mReviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        LinearLayoutManager lmReviewsManager = new LinearLayoutManager(this);
        LinearLayoutManager lmTrailersManager = new LinearLayoutManager(this);
        mBinding.rvReviews.setLayoutManager(lmReviewsManager);
        mBinding.rvTrailers.setLayoutManager(lmTrailersManager);

        mBinding.rvReviews.setHasFixedSize(true);
        mBinding.rvTrailers.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();
        mVideoAdapter = new VideoAdapter();

        mBinding.rvReviews.setAdapter(mReviewAdapter);
        mBinding.rvTrailers.setAdapter(mVideoAdapter);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_REFERRER)) {
            Movie movie = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
            setDetails(movie);
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.MOVIES, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIES)) {
            mMovie = savedInstanceState.getParcelable(Constants.MOVIES);
            setDetails(mMovie);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setDetails(final Movie movie) {
        this.mMovie = movie;


        String imageUrl = movie.getPosterPath();
        String title = movie.getOriginalTitle();
        String date = movie.getReleaseDate();
        String overview = movie.getOverview();
        String id = Long.toString(movie.getId());
        float rating = movie.getVoteAverage();

        imageUrl = Constants.IMAGE_URL_BASE_W185 +""+imageUrl;
        Picasso.with(this).load(imageUrl).into(mBinding.imgMovieCard);

        mBinding.title.setText(title);
        mBinding.year.setText(date);
        mBinding.overview.setText(overview);
        mBinding.ratingBar.setRating(rating);
        String ratingText = String.valueOf(rating) + getString(R.string.rating_constant);
        mBinding.average.setText(ratingText);
        setButtonStyle(movie.isInStorage());

        mBinding.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.isInStorage()) {
                    onMovieDisfavor();
                } else {
                    onMovieFavorite();
                }
            }
        });

        new FetchVideos().execute(id);
        new FetchReviews().execute(id);
    }

    private void onMovieFavorite() {
        MoviesActions.onFavorite(mMovie, this);
        mMovie.setIsInStorage(true);
        setButtonStyle(true);
    }

    private void onMovieDisfavor() {
        MoviesActions.onDisfavor(mMovie, this);
        mMovie.setIsInStorage(false);
        setButtonStyle(false);
    }


    private void setButtonStyle(boolean isInStorage) {
        if (isInStorage) {
            mBinding.buttonFavorite.setCardBackgroundColor(getResources().getColor(R.color.colorUnselected));
            mBinding.txtFav.setText(getResources().getString(R.string.disfavor));
            mBinding.icStar.setImageResource(R.drawable.ic_star_selected);
            mBinding.txtFav.setTextColor(getResources().getColor(R.color.colorBlack));
        } else {
            mBinding.buttonFavorite.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            mBinding.txtFav.setText(getResources().getString(R.string.favorite));
            mBinding.icStar.setImageResource(R.drawable.ic_star);
            mBinding.txtFav.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchVideos extends AsyncTask<String, Void, Videos> {

        URL videosRequestUrl = null;
        String jsonStringify;
        Videos videos;

        @Override
        protected Videos doInBackground(String... strings) {
            try {
                String id = "0";
                if (strings != null && strings.length > 0 && strings[0] != null) id = strings[0];
                videosRequestUrl = NetworkUtils.buildVideoURL(id);
                jsonStringify = NetworkUtils.getResponseFromHttpUrl(videosRequestUrl);
                videos = new Gson().fromJson(jsonStringify, Videos.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return videos;
        }

        @Override
        protected void onPostExecute(Videos videos) {
            super.onPostExecute(videos);

            if (videos != null && videos.getVideos().size() > 0) {
                mVideos.clear();
                mVideos.addAll(videos.getVideos());
                mVideoAdapter.setVideos(mVideos);
                mBinding.rvTrailers.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchReviews extends AsyncTask<String, Void, Reviews> {

        URL reviewsRequestUrl = null;
        String jsonStringify;
        Reviews reviews;

        @Override
        protected Reviews doInBackground(String... strings) {
            try {
                String id = "0";
                if (strings != null && strings.length > 0 && strings[0] != null) id = strings[0];
                reviewsRequestUrl = NetworkUtils.buildReviewURL(id);
                jsonStringify = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
                reviews = new Gson().fromJson(jsonStringify, Reviews.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return reviews;
        }

        @Override
        protected void onPostExecute(Reviews reviews) {
            super.onPostExecute(reviews);

            if (reviews != null && reviews.getReviews().size() > 0) {
                mReviews.clear();
                mReviews.addAll(reviews.getReviews());
                mReviewAdapter.setVideos(mReviews);
                mBinding.separator.setVisibility(View.VISIBLE);
                mBinding.rvReviews.setVisibility(View.VISIBLE);
            }
        }
    }
}
