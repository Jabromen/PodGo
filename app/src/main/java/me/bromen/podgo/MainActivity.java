package me.bromen.podgo;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

public class MainActivity extends AppCompatActivity
        implements NewPodcastDialogFragment.OnDataPass, DownloadPodcastTaskFragment.TaskCallbacks,
        PodcastRecyclerAdapter.OnClickCallbacks, PodcastOptionDialogFragment.OnDataPass {

    private static final String XML_TASK_FRAGMENT = "xml_task_fragment";
    private DownloadPodcastTaskFragment mXmlTaskFragment;

    private static final String PODCAST_LIST_TAG = "podcast_list_fragment";

    private PodcastList podcastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            podcastList = (PodcastList) savedInstanceState.getSerializable("PODCASTLIST");
        }
        else {
            setUpPodcastList();
        }
        setUpDownloadFragments();

        PodcastListFragment fragment = new PodcastListFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main, fragment, PODCAST_LIST_TAG);
        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("PODCASTLIST", podcastList);
    }

    public void setUpDownloadFragments() {
        // Find download task fragments
        mXmlTaskFragment = (DownloadPodcastTaskFragment)
                getFragmentManager().findFragmentByTag(XML_TASK_FRAGMENT);
    }

    public void setUpPodcastList() {
        // Load saved podcast info from files
        podcastList = new PodcastList();
        podcastList.loadPodcastInfo(this);
    }

    // What to do when data is received from new podcast dialog fragment
    @Override
    public void onPassUrl(String data) {
        addNewPodcast(data);
    }

    //
    public void addNewPodcast(String feed) {

        if (feed == null)
            return;

        // Find download task fragment if exists
        mXmlTaskFragment = (DownloadPodcastTaskFragment)
                getFragmentManager().findFragmentByTag(XML_TASK_FRAGMENT);

        // If doesn't exist, start new download task fragment
        if (mXmlTaskFragment == null) {

            mXmlTaskFragment = new DownloadPodcastTaskFragment();

            // Pass feed URL to task
            Bundle bundle = new Bundle();
            bundle.putString("URL", feed);
            mXmlTaskFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(mXmlTaskFragment, XML_TASK_FRAGMENT).commit();
        }
        else {
            Toast.makeText(this, "Other podcast info being downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPreExecuteXML() {
        Toast.makeText(this, "Downloading Podcast Info", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelledXML(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecuteXML(Podcast podcast) {

        if (tryAddPodcastToList(podcast)) {
            updatePodcastView();
        }
    }

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

    public PodcastList getPodcastList() {
        if (podcastList == null) {
            podcastList = new PodcastList();
            podcastList.loadPodcastInfo(this);
        }
        return podcastList;
    }

    public void updatePodcastView() {
        PodcastListFragment podcastListFragment =
                (PodcastListFragment) getFragmentManager().findFragmentByTag(PODCAST_LIST_TAG);

        if (podcastListFragment != null) {
            podcastListFragment.updatePodcastView(podcastList);
        }
    }

    public void deletePodcast(String podcastTitle) {
        if (podcastList.remove(podcastTitle)) {
            PodcastFileUtils.deletePodcast(this, podcastTitle);
            updatePodcastView();
        }
    }

    @Override
    public void onPodcastSelected(String podcastTitle) {
        Toast.makeText(this, podcastTitle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionsSelected(String podcastTitle) {
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", podcastTitle);

        PodcastOptionDialogFragment dialog = new PodcastOptionDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "podcast_options_dialog");
    }

    @Override
    public void onPassPodcastOption(PodcastOptionDialogFragment.OptionSelected option, String title) {
        switch (option) {
            case OPTION_DELETE:
                deletePodcast(title);
                break;
        }
    }
}
