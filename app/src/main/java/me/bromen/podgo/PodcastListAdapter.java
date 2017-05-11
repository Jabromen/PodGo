package me.bromen.podgo;

import android.content.Context;
import android.graphics.Bitmap;
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

        final Podcast podcast = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.podcastTitleListItem);

        ImageView imageView = (ImageView) view.findViewById(R.id.podcastImageListItem);

        try {
            textView.setText(podcast.getTitle());
            Bitmap image = PodcastSaver.getPodcastImage(getContext(), podcast.getTitle());

            if (image != null) {
                imageView.setImageBitmap(image);
            }

        } catch (MalformedFeedException | NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }
}
