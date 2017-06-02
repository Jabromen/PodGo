package me.bromen.podgo.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.bromen.podgo.R;
import me.bromen.podgo.activities.MainActivity;
import me.bromen.podgo.structures.ItunesPodcast;

/**
 * Created by jeff on 6/1/17.
 */

public class ItunesRecyclerAdapter extends RecyclerView.Adapter<ItunesRecyclerAdapter.ItunesViewHolder> {

    public interface OnClickCallbacks {
        void onItunesPodcastSelected(String feedUrl);
    }

    private Context context;
    private OnClickCallbacks mCallbacks;
    private List<ItunesPodcast> podcastList = new ArrayList<>();

    public ItunesRecyclerAdapter(List<ItunesPodcast> podcastList) {
        this.podcastList = podcastList;
    }

    @Override
    public ItunesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mCallbacks = (OnClickCallbacks) context;
        View view = LayoutInflater.from(context).inflate(R.layout.itunes_podcast_list_item, parent, false);

        return new ItunesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItunesViewHolder holder, final int position) {
        String title = podcastList.get(position).getTitle();
        String imageUrl = podcastList.get(position).getImageUrl();

        holder.titleView.setText(title);

        Glide.with(context)
                .load(Uri.parse(imageUrl))
                .asBitmap()
                .override(150, 150)
                .centerCrop()
                .into(holder.podcastImageView);

        holder.downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItunesPodcastSelected(podcastList.get(position).getFeedUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return podcastList.size();
    }

    public class ItunesViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView podcastImageView;
        ImageView downloadImageView;

        public ItunesViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.itunesPodcastTitle);
            podcastImageView = (ImageView) itemView.findViewById(R.id.itunesPodcastImage);
            downloadImageView = (ImageView) itemView.findViewById(R.id.itunesPodcastDownloadButton);
        }
    }

    public void updateList(List<ItunesPodcast> newList) {
        podcastList = new ArrayList<>();
        podcastList.addAll(newList);
        notifyDataSetChanged();
    }
}
