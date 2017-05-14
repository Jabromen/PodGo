package me.bromen.podgo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 5/9/17.
 */

public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<EpisodeRecyclerAdapter.EpisodeViewHolder> {

    interface OnClickCallbacks {
        void onEpisodeSelected();
        void onPlayDownloadSelected();
    }

    private List<Episode> episodeList = new ArrayList<>();
    OnClickCallbacks mCallbacks;

    EpisodeRecyclerAdapter(List<Episode> episodeList) {
        this.episodeList = episodeList;

    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_list_item, parent, false);

        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        try {
            holder.titleView.setText(episodeList.get(position).getTitle().replace("\n", " "));
        } catch (MalformedFeedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView playDownloadView;
        ProgressBar progressBar;

        public EpisodeViewHolder(final View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.episodeTitleListItem);
            playDownloadView = (ImageView) itemView.findViewById(R.id.episodePlayDownload);
            progressBar = (ProgressBar) itemView.findViewById(R.id.episodeDownloadProgress);

            playDownloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void updateList(List<Episode> newList) {
        episodeList = new ArrayList<>();

        episodeList.addAll(newList);

        notifyDataSetChanged();
    }
}
