package me.bromen.podgo.activities.feeddetail.mvp.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.bromen.podgo.ext.structures.Feed;
import me.bromen.podgo.ext.structures.FeedItem;
import me.bromen.podgo.ext.utilities.PodcastFileUtils;
import me.bromen.podgo.R;

/**
 * Created by jeff on 5/9/17.
 */

public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private Picasso picasso;

    private Feed feed = new Feed();

    private PublishSubject<FeedItem> tileClickSubject = PublishSubject.create();
    private PublishSubject<FeedItem> downloadClickSubject = PublishSubject.create();

    public Observable<FeedItem> getTileClickedObservable() {
        return tileClickSubject;
    }

    public Observable<FeedItem> getDownloadClickedObservable() {
        return downloadClickSubject;
    }

    public EpisodeRecyclerAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    public EpisodeRecyclerAdapter(Picasso picasso, Feed feed) {
        this(picasso);
        this.feed = feed;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_list_header, parent, false);
            return new EpisodeViewHolderHeader(view);
        }
        else if (viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_list_item, parent, false);
            EpisodeViewHolderItem viewHolder = new EpisodeViewHolderItem(view);

            RxView.clicks(viewHolder.selectEpisode)
                    .takeUntil(RxView.detaches(parent))
                    .map(__ -> viewHolder.getCurrentItem())
                    .subscribe(tileClickSubject);

            RxView.clicks(viewHolder.downloadPlayButton)
                    .takeUntil(RxView.detaches(parent))
                    .map(__ -> viewHolder.getCurrentItem())
                    .subscribe(downloadClickSubject);

            return viewHolder;
        }

        throw new RuntimeException("Invalid viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof EpisodeViewHolderHeader) {
            ((EpisodeViewHolderHeader) holder).setViews();
        }
        else if (holder instanceof EpisodeViewHolderItem) {
            ((EpisodeViewHolderItem) holder).setCurrentItem(feed.getFeedItems().get(position-1));
        }
    }

    @Override
    public int getItemCount() {
        return feed.getFeedItems().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public class EpisodeViewHolderItem extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView episodeImageView;
        RelativeLayout selectEpisode;
        ImageView downloadPlayButton;

        private FeedItem item;

        public EpisodeViewHolderItem(final View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.episodeTitle);
            episodeImageView = (ImageView) itemView.findViewById(R.id.episodeImage);
            selectEpisode = (RelativeLayout) itemView.findViewById(R.id.selectEpisode);
            downloadPlayButton = (ImageView) itemView.findViewById(R.id.episodeDownloadPlayButton);
        }

        public FeedItem getCurrentItem() {
            return item;
        }

        public void setCurrentItem(FeedItem item) {
            this.item = item;
            setViews();
        }

        private void setViews() {
            titleView.setText(item.getTitle());

            int img_id;
            if (item.isDownloading()) {
                img_id = R.mipmap.ic_cancel;
            } else if (item.isDownloaded()) {
                img_id = R.mipmap.ic_play;
            } else {
                img_id = R.mipmap.ic_download;
            }

            picasso.with(context)
                    .load(img_id)
                    .into(downloadPlayButton);

            picasso.with(context)
                    .load(feed.getImageUrl())
                    .error(R.drawable.checkerboard)
                    .resize(50, 50)
                    .centerCrop()
                    .into(episodeImageView);
        }
    }

    public class EpisodeViewHolderHeader extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView podcastImageView;
        Spinner filterEpisodes;

        public EpisodeViewHolderHeader(final View headerView) {
            super(headerView);

            titleView = (TextView) itemView.findViewById(R.id.headerPodcastTitle);
            podcastImageView = (ImageView) itemView.findViewById(R.id.headerPodcastImage);
            filterEpisodes = (Spinner) itemView.findViewById(R.id.episodeListFilterSpinner);
        }

        public void setViews() {
            titleView.setText(feed.getTitle());

            picasso.with(context)
                    .load(feed.getImageUrl())
                    .error(R.drawable.checkerboard)
                    .resize(200, 200)
                    .centerCrop()
                    .into(podcastImageView);
        }
    }

    public void updateList(Feed feed) {
        this.feed = feed;

        notifyDataSetChanged();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }
}
