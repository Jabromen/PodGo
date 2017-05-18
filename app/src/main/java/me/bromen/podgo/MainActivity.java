package me.bromen.podgo;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NewPodcastDialogFragment.OnDataPass, PodcastRecyclerAdapter.OnClickCallbacks,
        PodcastOptionDialogFragment.OnDataPass, XmlResultReceiver.Receiver,
        EpisodeRecyclerAdapter.OnClickCallbacks {

    private static final String PODCAST_LIST_TAG = "podcast_list_fragment";
    private static final String EPISODE_LIST_TAG = "episode_list_fragment";

    private String selectedPodcast;
    private PodcastList podcastList;

    public XmlResultReceiver xmlReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            podcastList = (PodcastList) savedInstanceState.getSerializable("PODCASTLIST");
            xmlReceiver = savedInstanceState.getParcelable("RECEIVER");
            selectedPodcast = savedInstanceState.getString("SELECTEDPODCAST");
        }
        else {
            setUpPodcastList();
            setUpXmlReceiver();

            PodcastListFragment fragment = new PodcastListFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_main, fragment, PODCAST_LIST_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        xmlReceiver.setReceiver(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        xmlReceiver.setReceiver(null);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("PODCASTLIST", podcastList);
        outState.putParcelable("RECEIVER", xmlReceiver);
        outState.putString("SELECTEDPODCAST", selectedPodcast);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        }
        else {
            super.onBackPressed();
        }
    }

    // Initial setup for podcastList
    public void setUpPodcastList() {
        // Load saved podcast info from files
        podcastList = new PodcastList();
        podcastList.loadPodcastInfo(this);
    }

    // Initial setup for XmlResultReceiver
    public void setUpXmlReceiver() {
        xmlReceiver = new XmlResultReceiver(new Handler());
        xmlReceiver.setReceiver(this);
    }

    // What to do when data is received from new podcast dialog fragment
    @Override
    public void onPassUrl(String data) {
        addNewPodcast(data);
    }

    // User selects a podcast
    @Override
    public void onPodcastSelected(String podcastTitle) {

        selectedPodcast = podcastTitle;

        EpisodeListFragment fragment = new EpisodeListFragment();

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.fragment_main, fragment, EPISODE_LIST_TAG);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onEpisodeSelected(String podcastTitle, String episodeTitle) {
        Toast.makeText(this, episodeTitle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFilterEpisodes(int position) {
        EpisodeListFragment episodeListFragment =
                (EpisodeListFragment) getFragmentManager().findFragmentByTag(EPISODE_LIST_TAG);

        if (episodeListFragment != null) {
            episodeListFragment.filterEpisodes(position);
        }
    }

    // Display podcast options menu
    @Override
    public void onOptionsSelected(String podcastTitle) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", podcastTitle);

        PodcastOptionDialogFragment dialog = new PodcastOptionDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "podcast_options_dialog");
    }

    // What to do when a specific podcast option is selected
    @Override
    public void onPassPodcastOption(PodcastOptionDialogFragment.OptionSelected option, String title) {
        switch (option) {
            case OPTION_DELETE:
                deletePodcast(title);
                break;
        }
    }

    // What to do when a result is received from XmlRequestService
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        if (resultCode == XmlResultReceiver.SUCCESS) {
            String feedUrl = resultData.getString("URL");
            String feedXml = resultData.getString("XML");

            try {
                if (tryAddPodcastToList(new Podcast(feedXml, new URL(feedUrl)))) {
                    updatePodcastView();
                }
            } catch (MalformedFeedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to parse feed", Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Malformed URL", Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode == XmlResultReceiver.FAILURE) {
            Toast.makeText(this, "Failed to retrieve feed", Toast.LENGTH_SHORT).show();
        }

    }

    // Add a new podcast from URL
    public void addNewPodcast(String feed) {

        Intent xmlIntent = new Intent(MainActivity.this, XmlRequestService.class);
        xmlIntent.putExtra("URL", feed);
        xmlIntent.putExtra("RECEIVER", xmlReceiver);
        startService(xmlIntent);

    }

    // Try to add a podcast to the list, returns true if added, false if not
    boolean tryAddPodcastToList(Podcast podcast) {
        if (podcast != null) {
            try {
                if (!podcastList.contains(podcast.getTitle())) {
                    podcastList.add(new PodcastShell(podcast));
                    PodcastFileUtils.savePodcastInfo(getApplicationContext(), podcast);
                    Toast.makeText(this, "Added " + podcast.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(this, podcast.getTitle() + " Already Saved", Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedFeedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: Malformed Feed", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    // Getter for podcast list for use in fragment
    public PodcastList getPodcastList() {
        if (podcastList == null) {
            podcastList = new PodcastList();
            podcastList.loadPodcastInfo(this);
        }
        return podcastList;
    }

    // Getter for selected podcast for use in fragment
    public String getSelectedPodcast() {
        return selectedPodcast;
    }

    // Updates the podcast view with the current state of the podcastList
    public void updatePodcastView() {
        PodcastListFragment podcastListFragment =
                (PodcastListFragment) getFragmentManager().findFragmentByTag(PODCAST_LIST_TAG);

        if (podcastListFragment != null) {
            podcastListFragment.updatePodcastView(podcastList);
        }
    }

    // Removes a podcast from the list, deletes its local files, and updates the podcast view
    public void deletePodcast(String podcastTitle) {
        if (podcastList.remove(podcastTitle)) {
            PodcastFileUtils.deletePodcast(this, podcastTitle);
            updatePodcastView();
        }
    }
}
