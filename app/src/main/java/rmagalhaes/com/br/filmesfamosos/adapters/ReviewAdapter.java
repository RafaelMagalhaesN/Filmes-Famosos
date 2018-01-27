package rmagalhaes.com.br.filmesfamosos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import rmagalhaes.com.br.filmesfamosos.R;
import rmagalhaes.com.br.filmesfamosos.models.Review;

/**
 * Created by Rafael Magalhães on 27/01/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{


    private ArrayList<Review> mReviews = new ArrayList<>();

    public ReviewAdapter() {
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_conversation;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.setAuthorAndContent();
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void setVideos(ArrayList<Review> reviews) {
        this.mReviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView author;
        final TextView content;

        ReviewViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            content = itemView.findViewById(R.id.content);
        }

        void setAuthorAndContent() {
            int adapterPosition = getAdapterPosition();
            Review review = mReviews.get(adapterPosition);
            String author = review.getAuthor();
            String content = review.getContent();

            this.author.setText(author);
            this.content.setText(content);
        }

    }
}
