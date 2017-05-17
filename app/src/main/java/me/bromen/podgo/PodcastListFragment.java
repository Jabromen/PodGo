package me.bromen.podgo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * UI Component Fragment for the list of saved podcasts
 */

public class PodcastListFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView podcastView;
    private RecyclerView.Adapter podcastAdapter;

    private PodcastList podcastList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_podcast_list, container, false);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        mToolbar.setTitle("Podcasts");

        podcastView = (RecyclerView) rootView.findViewById(R.id.podcastListMain);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mToolbar != null) {
            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        }

        podcastList = ((MainActivity) getActivity()).getPodcastList();

        setUpPodcastView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }
        else if (id == R.id.action_new_podcast) {
            // Bring up new podcast dialog fragment, results handled via callbacks in MainActivity
            NewPodcastDialogFragment fragment = new NewPodcastDialogFragment();
            fragment.show(getFragmentManager(), "new_podcast_dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpPodcastView() {

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getActivity(), DisplayUtils.calculateNoOfColumns(getActivity(), 125, 3));
        podcastView.setLayoutManager(layoutManager);
        podcastView.setHasFixedSize(true);

        podcastAdapter = new PodcastRecyclerAdapter(podcastList);
        podcastView.setAdapter(podcastAdapter);
    }

    public void updatePodcastView(PodcastList newList) {
        ((PodcastRecyclerAdapter) podcastAdapter).updateList(newList);
    }
}
