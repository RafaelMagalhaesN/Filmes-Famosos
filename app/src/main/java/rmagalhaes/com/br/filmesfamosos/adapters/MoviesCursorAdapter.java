package rmagalhaes.com.br.filmesfamosos.adapters;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import rmagalhaes.com.br.filmesfamosos.R;
import rmagalhaes.com.br.filmesfamosos.data.MoviesActions;
import rmagalhaes.com.br.filmesfamosos.data.MoviesContract;
import rmagalhaes.com.br.filmesfamosos.models.Movie;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;

/**
 * Created by Rafael Magalhães on 26/01/18.
 */

public class MoviesCursorAdapter extends RecyclerView.Adapter<MoviesCursorAdapter.MoviesViewHolder>{

    private Cursor mCursor;
    private final MoviesCursorAdapterClickListener mClickListener;


    public interface MoviesCursorAdapterClickListener {
        void onClick(Movie movie);
    }


    public MoviesCursorAdapter(MoviesCursorAdapterClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
       holder.setImage();
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return;
        }
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
    }


    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Class variables for the task description and priority TextViews
        final ImageView moviesView;
        final View view;
        final CardView cardImage;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            moviesView = itemView.findViewById(R.id.imgMovieCard);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardImage.setOnClickListener(this);
        }

        void setImage() {
            int imagePathIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH);
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String imageUrl = mCursor.getString(imagePathIndex);
            imageUrl = Constants.IMAGE_URL_BASE_W342 +""+imageUrl;
            Picasso.with(view.getContext()).load(imageUrl).into(moviesView);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = MoviesActions.cursorToMovieObject(mCursor, adapterPosition);
            mClickListener.onClick(movie);
        }
    }


}
