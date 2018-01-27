package rmagalhaes.com.br.filmesfamosos.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import rmagalhaes.com.br.filmesfamosos.R;
import rmagalhaes.com.br.filmesfamosos.models.Video;
import rmagalhaes.com.br.filmesfamosos.utils.Constants;

/**
 * Created by Rafael Magalhães on 27/01/18.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private ArrayList<Video> mVideos = new ArrayList<>();

    public VideoAdapter() {
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_video;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.setTrailerTitle();
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public void setVideos(ArrayList<Video> videos) {
        this.mVideos = videos;
        notifyDataSetChanged();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        final CardView cardImage;
        final TextView trailerTitle;

        VideoViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardVideo);
            trailerTitle = itemView.findViewById(R.id.trailerTitle);
            cardImage.setOnClickListener(this);
        }

        void setTrailerTitle() {
            int adapterPosition = getAdapterPosition();
            Video video = mVideos.get(adapterPosition);
            String trailerTitle = video.getName();
            this.trailerTitle.setText(trailerTitle);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Video video = mVideos.get(adapterPosition);
            String url = Constants.YOUTUBE_URL + video.getKey();
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            view.getContext().startActivity(intent);
        }
    }
}
