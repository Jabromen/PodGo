package me.bromen.podgo.activities.home.mvp.view;

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
import me.bromen.podgo.ext.structures.Feed;
import me.bromen.podgo.ext.structures.FeedList;
import me.bromen.podgo.R;

/**
 * Created by jeff on 5/11/17.
 */

public class PodcastRecyclerAdapter extends RecyclerView.Adapter<PodcastRecyclerAdapter.PodcastViewHolder> {

    private Context context;
    private final Picasso picasso;

    private List<Feed> feedList = new ArrayList<>();

    private PublishSubject<Feed> tileClickSubject = PublishSubject.create();
    private PublishSubject<Feed> optionsClickSubject = PublishSubject.create();

    public Observable<Feed> getTileClickedObservable() {
        return tileClickSubject;
    }

    public Observable<Feed> getOptionsClickedObservable() {
        return optionsClickSubject;
    }

    public PodcastRecyclerAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    public PodcastRecyclerAdapter(Picasso picasso, List<Feed> feeds) {
        this(picasso);
        this.feedList = feeds;
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.podcast_list_item, parent, false);
        PodcastViewHolder viewHolder = new PodcastViewHolder(view);

        RxView.clicks(viewHolder.imageView)
                .takeUntil(RxView.detaches(parent))
                .map(__ -> viewHolder.getCurrentFeed())
                .subscribe(tileClickSubject);

        RxView.clicks(viewHolder.optionsView)
                .takeUntil(RxView.detaches(parent))
                .map(__ -> viewHolder.getCurrentFeed())
                .subscribe(optionsClickSubject);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, final int position) {

        holder.setCurrentFeed(feedList.get(position));
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class PodcastViewHolder extends RecyclerView.ViewHolder {

        Feed feed;

        TextView titleView;
        ImageView imageView;
        ImageView optionsView;

        public PodcastViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.podcastTitleListItem);
            imageView = (ImageView) itemView.findViewById(R.id.podcastImageListItem);
            optionsView = (ImageView) itemView.findViewById(R.id.podcastOptionListItem);
        }

        public void setCurrentFeed(Feed feed) {
            this.feed = feed;
            setViews();
        }

        public Feed getCurrentFeed() {
            return feed;
        }

        private void setViews() {
            picasso.with(context)
                    .load(feed.getImageUrl())
                    .error(R.drawable.checkerboard)
                    .resize(150, 150)
                    .centerCrop()
                    .into(imageView);

            titleView.setText(feed.getTitle());
        }
    }

    public void updateList(FeedList newList) {
        feedList.clear();
        feedList.addAll(newList);
        refreshList();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }
}
