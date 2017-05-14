package me.bromen.podgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 5/11/17.
 */

public class PodcastRecyclerAdapter extends RecyclerView.Adapter<PodcastRecyclerAdapter.PodcastViewHolder> {

    interface OnClickCallbacks {
        void onPodcastSelected(String podcastTitle);
        void onOptionsSelected(String podcastTitle);
    }

    private Context context;
    private OnClickCallbacks mCallbacks;
    private List<Podcast> podcastList = new ArrayList<>();

    PodcastRecyclerAdapter(List<Podcast> podcastList) {
        this.podcastList = podcastList;
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mCallbacks = (OnClickCallbacks) context;
        View view = LayoutInflater.from(context).inflate(R.layout.podcast_list_item, parent, false);

        return new PodcastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        String title = "";
        try {
            title = podcastList.get(position).getTitle();
        } catch (MalformedFeedException e) {
            e.printStackTrace();
            holder.titleView.setText("ERROR: Malformed Feed");
        }

        holder.titleView.setText(title);

        Bitmap image = PodcastFileUtils.getPodcastImage(context, title);
        if (image != null) {
            holder.imageView.setImageBitmap(image);
        }

    }

    @Override
    public int getItemCount() {
        return podcastList.size();
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

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onPodcastSelected(titleView.getText().toString());
                }
            });

            optionsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onOptionsSelected(titleView.getText().toString());
                }
            });
        }
    }

    public void updateList(PodcastList newList) {
        podcastList = new ArrayList<>();
        podcastList.addAll(newList);
        refreshList();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }
}
