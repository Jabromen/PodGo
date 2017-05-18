package me.bromen.podgo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;

import java.util.List;

/**
 * Created by jeff on 5/17/17.
 */

public class EpisodeListFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView episodeView;
    private RecyclerView.Adapter episodeAdapter;

    private int filterOption;
    private String podcastTitle;
    private List<Episode> episodeList;

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
        mToolbar.setTitle(podcastTitle);

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

        podcastTitle = ((MainActivity) getActivity()).getSelectedPodcast();
        episodeList = PodcastFileUtils.loadPodcastFromFile(getActivity(), podcastTitle).getEpisodes();

        if (mToolbar != null) {
            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
            mToolbar.setTitle(podcastTitle);
        }

        setUpEpisodeView();
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

    private void setUpEpisodeView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        episodeView.setLayoutManager(layoutManager);
        episodeView.setHasFixedSize(true);

        episodeAdapter = new EpisodeRecyclerAdapter(episodeList, podcastTitle, filterOption);
        episodeView.setAdapter(episodeAdapter);
    }

    public void filterEpisodes(int option) {

        filterOption = option;

        switch (option) {

            // Show All Episodes
            case 0:
                ((EpisodeRecyclerAdapter) episodeAdapter).updateList(episodeList);
                break;

            // Show Most Recent 7 Episodes
            case 1:
                int index = episodeList.size() < 7 ? episodeList.size() : 7;
                ((EpisodeRecyclerAdapter) episodeAdapter).updateList(episodeList.subList(0, index));
                break;

            // TODO: Add filters for downloaded and not downloaded episodes
            default:
                Toast.makeText(getActivity(), "Option Not Implemented Yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
