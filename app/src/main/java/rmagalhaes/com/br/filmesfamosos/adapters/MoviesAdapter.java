package rmagalhaes.com.br.filmesfamosos.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rmagalhaes.com.br.filmesfamosos.R;
import rmagalhaes.com.br.filmesfamosos.models.Movie;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{


    private ArrayList<Movie> movies = new ArrayList<>();
    private final MoviesAdapterClickListener mClickListener;

    public MoviesAdapter(MoviesAdapterClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface MoviesAdapterClickListener {
        void onClick(Movie movie);
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MoviesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        holder.setImage();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMoviesData(ArrayList<Movie> movies) {
        this.movies = movies;
    }


    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView moviesView;
        final CardView cardImage;
        final View view;

        MoviesViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            moviesView = itemView.findViewById(R.id.imgMovieCard);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardImage.setOnClickListener(this);
        }

        void setImage() {
            int adapterPosition = getAdapterPosition();
            Movie movie = movies.get(adapterPosition);
            String imageUrl = movie.getPosterPath();
            imageUrl = Constants.IMAGE_URL_BASE_W342 +""+imageUrl;
            Picasso.with(view.getContext()).load(imageUrl).into(moviesView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movies.get(adapterPosition);
            mClickListener.onClick(movie);
        }
    }
}
