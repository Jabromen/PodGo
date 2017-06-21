package me.bromen.podgo.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.bromen.podgo.adapters.EpisodeRecyclerAdapter;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.structures.FeedItem;
import me.bromen.podgo.utilities.PodcastFileUtils;
import me.bromen.podgo.R;

/**
 * Created by jeff on 5/17/17.
 */

public class EpisodeListFragment extends Fragment {

    public static final String TAG = "episode_list_fragment";

    private Toolbar mToolbar;
    private RecyclerView episodeView;
    private RecyclerView.Adapter episodeAdapter;

    private int filterOption;
    private long id;
    private String podcastTitle;
    private List<FeedItem> itemList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_episode_list, container, false);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        mToolbar.setTitle("Episodes");

        episodeView = (RecyclerView) rootView.findViewById(R.id.episodeListMain);

        if (savedInstanceState != null) {
            filterOption = savedInstanceState.getInt("FILTER");
        }
        else {
            filterOption = 0;
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        id = getArguments().getLong("ID");
        podcastTitle = getArguments().getString("TITLE");

        setUpEpisodeView();

        new LoadEpisodesTask().execute(id);

        if (mToolbar != null) {
            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
            mToolbar.setTitle(podcastTitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("FILTER", filterOption);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_podcast_acitivity, menu);
    }

    public class LoadEpisodesTask extends AsyncTask<Long, Void, List<FeedItem>> {

        @Override
        protected List<FeedItem> doInBackground(Long... id) {
//            return ((MainActivity) getActivity()).getDbHelper().loadFeedItems(id[0]);
            return null;
        }

        @Override
        protected void onPostExecute(List<FeedItem> feedItems) {
            itemList = feedItems;
            setUpEpisodeView();
        }
    }

    private void setUpEpisodeView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        episodeView.setLayoutManager(layoutManager);
        episodeView.setHasFixedSize(true);

        episodeAdapter = new EpisodeRecyclerAdapter(itemList, podcastTitle, filterOption);
        episodeView.setAdapter(episodeAdapter);
    }

    public void filterEpisodes(int option) {

        filterOption = option;

        switch (option) {

            // Show All Episodes
            case 0:
                ((EpisodeRecyclerAdapter) episodeAdapter).updateList(itemList);
                break;

            // Show Most Recent 7 Episodes
            case 1:
                int index = itemList.size() < 7 ? itemList.size() : 7;
                ((EpisodeRecyclerAdapter) episodeAdapter).updateList(itemList.subList(0, index));
                break;

            // Show All Downloaded Episodes
            case 2:
                List<FeedItem> downloaded = new ArrayList<>();

                for (FeedItem ep : itemList) {
                    if (isEpisodeDownloaded(ep)) {
                        downloaded.add(ep);
                    }
                }
                ((EpisodeRecyclerAdapter) episodeAdapter).updateList(downloaded);
                break;

            // Show All Not Downloaded Episodes
            case 3:
                List<FeedItem> notDownloaded = new ArrayList<>();

                for (FeedItem ep : itemList) {
                    if (!isEpisodeDownloaded(ep)) {
                        notDownloaded.add(ep);
                    }
                }
                ((EpisodeRecyclerAdapter) episodeAdapter).updateList(notDownloaded);
                break;

            default:
                Toast.makeText(getActivity(), "Option Not Implemented Yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean isEpisodeDownloaded(FeedItem ep) {
        return PodcastFileUtils.isEpisodeDownloaded(getActivity(), podcastTitle, ep.getTitle());
    }

    public void refreshEpisodes() {
        ((EpisodeRecyclerAdapter) episodeAdapter).refreshList();
    }
}
