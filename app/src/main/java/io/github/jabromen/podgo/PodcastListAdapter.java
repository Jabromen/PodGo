package io.github.jabromen.podgo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 5/6/17.
 */

public class PodcastListAdapter extends ArrayAdapter<Podcast> {
    public PodcastListAdapter(@NonNull Context context, @NonNull PodcastList podcasts) {
        super(context, R.layout.podcast_list_item, podcasts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View view = layoutInflater.inflate(R.layout.podcast_list_item, parent, false);

        Podcast podcast = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.podcastTitleListItem);

        try {
            textView.setText(podcast.getTitle());
        } catch (MalformedFeedException | NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }
}
