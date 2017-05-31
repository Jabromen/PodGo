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

import me.bromen.podgo.downloads.FileTarget;
import me.bromen.podgo.structures.Feed;
import me.bromen.podgo.structures.FeedList;
import me.bromen.podgo.utilities.PodcastFileUtils;
import me.bromen.podgo.R;

/**
 * Created by jeff on 5/11/17.
 */

public class PodcastRecyclerAdapter extends RecyclerView.Adapter<PodcastRecyclerAdapter.PodcastViewHolder> {

    public interface OnClickCallbacks {
        void onPodcastSelected(long podcastId);
        void onOptionsSelected(long podcastId);
    }

    private Context context;
    private OnClickCallbacks mCallbacks;
    private FeedList feedList = new FeedList();

    public PodcastRecyclerAdapter(FeedList feedList) {
        this.feedList = feedList;
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mCallbacks = (OnClickCallbacks) context;
        View view = LayoutInflater.from(context).inflate(R.layout.podcast_list_item, parent, false);

        return new PodcastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, final int position) {

        String title = feedList.get(position).getTitle();
        String url = feedList.get(position).getImageUrl();

        holder.titleView.setText(title);

        setUpImageView(context, holder.imageView, title, url);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onPodcastSelected(feedList.get(position).getId());
            }
        });

        holder.optionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onOptionsSelected(feedList.get(position).getId());
            }
        });
    }

    private void setUpImageView(Context context, ImageView imageView, String title, String url) {
        if (PodcastFileUtils.imageFileIsSaved(context, title)) {
            Glide.with(context)
                    .load(PodcastFileUtils.getPodcastImageFile(context, title))
                    .asBitmap()
                    .override(150, 150)
                    .centerCrop()
                    .into(imageView);
        }
        else {
            Glide.with(context)
                    .load(Uri.parse(url))
                    .asBitmap()
                    .centerCrop()
                    .into(new FileTarget(PodcastFileUtils.getPodcastImageFile(context, title).getPath(),
                            300, 300, context, imageView, 150, 150));
        }
    }

    @Override
    public int getItemCount() {
        return feedList != null ? feedList.size() : 0;
    }

    public class PodcastViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView imageView;
        ImageView optionsView;

        public PodcastViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.podcastTitleListItem);
            imageView = (ImageView) itemView.findViewById(R.id.podcastImageListItem);
            optionsView = (ImageView) itemView.findViewById(R.id.podcastOptionListItem);
        }
    }

    public void updateList(FeedList newList) {
        feedList = new FeedList();
        feedList.addAll(newList);
        refreshList();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }
}
