package me.bromen.podgo.adapters;

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

import java.util.ArrayList;
import java.util.List;

import me.bromen.podgo.structures.FeedItem;
import me.bromen.podgo.utilities.PodcastFileUtils;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.home.MainActivity;

/**
 * Created by jeff on 5/9/17.
 */

public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public interface OnClickCallbacks {
        void onEpisodeSelected(String podcastTitle, String episodeTitle);
        void onDownloadPlaySelected(String podcastTitle, String episodeTitle, String episodeUrl);
        void onFilterEpisodes(int position);
    }

    private int startingFilter;
    private String podcastTitle;
    private List<FeedItem> episodeList = new ArrayList<>();

    private Context context;
    private OnClickCallbacks mCallbacks;

    public EpisodeRecyclerAdapter(List<FeedItem> episodeList, String podcastTitle, int startingFilter) {
        this.episodeList = episodeList;
        this.podcastTitle = podcastTitle;
        this.startingFilter = startingFilter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        mCallbacks = (EpisodeRecyclerAdapter.OnClickCallbacks) context;

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_list_header, parent, false);
            return new EpisodeViewHolderHeader(view);
        }
        else if (viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_list_item, parent, false);
            return new EpisodeViewHolderItem(view);
        }

        throw new RuntimeException("Invalid viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof EpisodeViewHolderHeader) {
            ((EpisodeViewHolderHeader) holder).titleView.setText(podcastTitle);

            Glide.with(context)
                    .load(PodcastFileUtils.getPodcastImageFile(context, podcastTitle))
                    .asBitmap()
                    .centerCrop()
                    .into(((EpisodeViewHolderHeader) holder).podcastImageView);
        }
        else if (holder instanceof EpisodeViewHolderItem) {
            String title = "";
            String url = "";
            try {
                title = episodeList.get(position - 1).getTitle();
                url = episodeList.get(position - 1).getEnclosure().getUrl();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            ((EpisodeViewHolderItem) holder).titleView.setText(title);

            Glide.with(context)
                    .load(PodcastFileUtils.getPodcastImageFile(context, podcastTitle))
                    .asBitmap()
                    .override(50, 50)
                    .centerCrop()
                    .into(((EpisodeViewHolderItem) holder).episodeImageView);

            int img_id;

            if (url == null) {
                img_id = R.mipmap.ic_error;
            }
            else if (true/*((MainActivity) context).isDownloading(podcastTitle, title)*/) {
                img_id = R.mipmap.ic_cancel;
            }
            else if (PodcastFileUtils.isEpisodeDownloaded(context, podcastTitle, title)) {
                img_id = R.mipmap.ic_play;
            }
            else {
                img_id = R.mipmap.ic_download;
            }

            Glide.with(context)
                    .load(img_id)
                    .asBitmap()
                    .into(((EpisodeViewHolderItem) holder).downloadPlayButton);

            final String finalTitle = title;
            final String finalUrl = url;
            ((EpisodeViewHolderItem) holder).downloadPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCallbacks.onDownloadPlaySelected(podcastTitle, finalTitle, finalUrl);

                }
            });
            ((EpisodeViewHolderItem) holder).selectEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mCallbacks.onEpisodeSelected(podcastTitle, titleView.getText().toString());
                    mCallbacks.onEpisodeSelected(podcastTitle, episodeList.get(position-1).getId()+"");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return episodeList.size() + 1;
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

        public EpisodeViewHolderItem(final View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.episodeTitle);
            episodeImageView = (ImageView) itemView.findViewById(R.id.episodeImage);
            selectEpisode = (RelativeLayout) itemView.findViewById(R.id.selectEpisode);
            downloadPlayButton = (ImageView) itemView.findViewById(R.id.episodeDownloadPlayButton);
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

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.filter_episodes_options,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            filterEpisodes.setAdapter(adapter);
            filterEpisodes.setSelection(startingFilter);

            filterEpisodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mCallbacks.onFilterEpisodes(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void updateList(List<FeedItem> newList) {
        episodeList = new ArrayList<>();

        episodeList.addAll(newList);

        notifyDataSetChanged();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }
}
