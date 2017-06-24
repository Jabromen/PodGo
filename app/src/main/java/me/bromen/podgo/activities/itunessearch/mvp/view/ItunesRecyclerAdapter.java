package me.bromen.podgo.activities.itunessearch.mvp.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.bromen.podgo.R;
import me.bromen.podgo.extras.structures.ItunesPodcast;

/**
 * Created by jeff on 6/1/17.
 */

public class ItunesRecyclerAdapter extends RecyclerView.Adapter<ItunesRecyclerAdapter.ItunesViewHolder> {

    private Context context;
    private final Picasso picasso;
    private List<ItunesPodcast> podcastList = new ArrayList<>();

    private PublishSubject<ItunesPodcast> downloadFeedClickSubject = PublishSubject.create();

    public Observable<ItunesPodcast> getDownloadClickedObservable() {
        return downloadFeedClickSubject;
    }

    public ItunesRecyclerAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    public ItunesRecyclerAdapter(Picasso picasso, List<ItunesPodcast> podcastList) {
        this(picasso);
        this.podcastList = podcastList;
    }

    @Override
    public ItunesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.itunes_podcast_list_item, parent, false);
        ItunesViewHolder viewHolder = new ItunesViewHolder(view);

        RxView.clicks(viewHolder.downloadImageView)
                .takeUntil(RxView.detaches(parent))
                .map(__ -> viewHolder.getCurrentPodcast())
                .subscribe(downloadFeedClickSubject);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItunesViewHolder holder, final int position) {
        holder.setCurrentPodcast(podcastList.get(position));
    }

    @Override
    public int getItemCount() {
        return podcastList.size();
    }

    public class ItunesViewHolder extends RecyclerView.ViewHolder {

        ItunesPodcast podcast;

        TextView titleView;
        ImageView podcastImageView;
        ImageView downloadImageView;

        public ItunesViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.itunesPodcastTitle);
            podcastImageView = (ImageView) itemView.findViewById(R.id.itunesPodcastImage);
            downloadImageView = (ImageView) itemView.findViewById(R.id.itunesPodcastDownloadButton);
        }

        public void setCurrentPodcast(ItunesPodcast podcast) {
            this.podcast = podcast;
            setViews();
        }

        public ItunesPodcast getCurrentPodcast() {
            return podcast;
        }

        private void setViews() {
            titleView.setText(podcast.getTitle());

            picasso.with(context)
                    .load(podcast.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .resize(100, 100)
                    .centerCrop()
                    .into(podcastImageView);

            picasso.with(context)
                    .load(R.mipmap.ic_download)
                    .into(downloadImageView);
        }
    }

    public void updateList(List<ItunesPodcast> newList) {
        podcastList = newList;
        notifyDataSetChanged();
    }
}
