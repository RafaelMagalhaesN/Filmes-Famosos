package rmagalhaes.com.br.filmesfamosos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import rmagalhaes.com.br.filmesfamosos.models.Movie;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;

public class DetailActivity extends AppCompatActivity {

    private ImageView mMovieBanner;
    private TextView mTitle;
    private TextView mYear;
    private TextView mOverview;
    private RatingBar mRating;
    private TextView mRatingText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieBanner = findViewById(R.id.movie_card);
        mTitle = findViewById(R.id.title);
        mYear = findViewById(R.id.year);
        mOverview = findViewById(R.id.overview);
        mRating = findViewById(R.id.ratingBar);
        mRatingText = findViewById(R.id.average);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_REFERRER)) {
            Movie movie = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
            setDetails(movie);
        }

    }

    private void setDetails(Movie movie) {
        String imageUrl = movie.getPosterPath();
        String title = movie.getOriginalTitle();
        String date = movie.getReleaseDate();
        String overview = movie.getOverview();
        float rating = movie.getVoteAverage();

        imageUrl = Constants.IMAGE_URL_BASE_W185 +""+imageUrl;
        Picasso.with(this).load(imageUrl).into(mMovieBanner);

        mTitle.setText(title);
        mYear.setText(date);
        mOverview.setText(overview);
        mRating.setRating(rating);
        mRatingText.setText(String.valueOf(rating));
    }
}
