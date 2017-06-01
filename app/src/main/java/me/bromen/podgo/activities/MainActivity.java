package me.bromen.podgo.activities;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import me.bromen.podgo.downloads.EpisodeDownloads;
import me.bromen.podgo.adapters.EpisodeRecyclerAdapter;
import me.bromen.podgo.fragments.NewPodcastDialogFragment;
import me.bromen.podgo.storage.PodcastDbContract;
import me.bromen.podgo.storage.PodcastDbHelper;
import me.bromen.podgo.structures.Feed;
import me.bromen.podgo.structures.FeedList;
import me.bromen.podgo.utilities.PodcastFileUtils;
import me.bromen.podgo.fragments.PodcastListFragment;
import me.bromen.podgo.fragments.PodcastOptionDialogFragment;
import me.bromen.podgo.adapters.PodcastRecyclerAdapter;
import me.bromen.podgo.R;
import me.bromen.podgo.downloads.FeedRequestService;
import me.bromen.podgo.downloads.FeedResultReceiver;
import me.bromen.podgo.fragments.EpisodeListFragment;

public class MainActivity extends AppCompatActivity
        implements NewPodcastDialogFragment.OnDataPass, PodcastRecyclerAdapter.OnClickCallbacks,
        PodcastOptionDialogFragment.OnDataPass, FeedResultReceiver.Receiver,
        EpisodeRecyclerAdapter.OnClickCallbacks {

    private static final String PODCAST_LIST_TAG = "podcast_list_fragment";
    private static final String EPISODE_LIST_TAG = "episode_list_fragment";

    private long selectedPodcastId;
    private FeedList feedList;

    private PodcastDbHelper dbHelper;

    private EpisodeDownloads episodeDownloads;

    public FeedResultReceiver xmlReceiver;
    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new PodcastDbHelper(getApplicationContext());

        if (savedInstanceState != null) {
            feedList = (FeedList) savedInstanceState.getSerializable("FEEDLIST");
            xmlReceiver = savedInstanceState.getParcelable("RECEIVER");
            selectedPodcastId = savedInstanceState.getLong("SELECTEDPODCASTID");
            episodeDownloads = (EpisodeDownloads) savedInstanceState.getSerializable("EPISODEDOWNLOADS");
            episodeDownloads.validateDownloads(this);
        }
        else {
            setUpPodcastList();
            setUpXmlReceiver();

            episodeDownloads = new EpisodeDownloads();

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
        setUpDownloadReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        xmlReceiver.setReceiver(null);
        unregisterReceiver(downloadReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("FEEDLIST", feedList);
        outState.putParcelable("RECEIVER", xmlReceiver);
        outState.putLong("SELECTEDPODCASTID", selectedPodcastId);
        outState.putSerializable("EPISODEDOWNLOADS", episodeDownloads);
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
        new LoadFeedListTask().execute(PodcastDbContract.ORDER_ALPHA_ASC);
    }

    public class LoadFeedListTask extends AsyncTask<Integer, Void, FeedList> {

        protected FeedList doInBackground(Integer... options) {
            return dbHelper.loadAllFeeds(options[0]);
        }

        @Override
        protected void onPostExecute(FeedList list) {
            feedList = list;
            updatePodcastView();
        }
    }

    public class SaveFeedDbTask extends AsyncTask<Feed, Void, Void> {

        protected Void doInBackground(Feed... feed) {
            feed[0].setId(dbHelper.saveFeed(feed[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updatePodcastView();
        }
    }

    public class RefreshFeedDbTask extends AsyncTask<Feed, Void, Integer> {

        protected Integer doInBackground(Feed... feed) {
            return dbHelper.updateFeed(feed[0]);
        }

        @Override
        protected void onPostExecute(Integer newItems) {
            Toast.makeText(getApplicationContext(), newItems + " Episodes Added", Toast.LENGTH_SHORT).show();
            updatePodcastView();
        }
    }

    public class DeleteFeedTask extends AsyncTask<Long, Void, Void> {

        protected Void doInBackground(Long... id) {
            dbHelper.deleteFeed(id[0]);
            return null;
        }
    }

    // Initial setup for FeedResultReceiver
    public void setUpXmlReceiver() {
        xmlReceiver = new FeedResultReceiver(new Handler());
        xmlReceiver.setReceiver(this);
    }

    public void setUpDownloadReceiver() {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                episodeDownloads.completeDownload(reference);

                Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();

                refreshEpisodeView();
            }
        };

        registerReceiver(downloadReceiver, intentFilter);
    }

    // What to do when data is received from new podcast dialog fragment
    @Override
    public void onPassUrl(String data) {
        downloadPodcastXml(data, false);
    }

    // User selects a podcast
    @Override
    public void onPodcastSelected(long podcastId) {

        Log.d("ID", ""+podcastId);

        selectedPodcastId = podcastId;

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
    public void onDownloadPlaySelected(String podcastTitle, String episodeTitle, String episodeUrl) {
        if (episodeUrl.equals("")) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
        }
        else if (isDownloading(podcastTitle, episodeTitle)) {
            episodeDownloads.cancelDownload(this, podcastTitle, episodeTitle);
        }
        else if (PodcastFileUtils.isEpisodeDownloaded(this, podcastTitle, episodeTitle)) {
            Toast.makeText(this, "Play Episode", Toast.LENGTH_SHORT).show();
        }
        else {
            episodeDownloads.startDownload(this, Uri.parse(episodeUrl), podcastTitle, episodeTitle);
            refreshEpisodeView();
        }
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
    public void onOptionsSelected(long podcastId) {
        Bundle bundle = new Bundle();
        bundle.putLong("ID", podcastId);

        PodcastOptionDialogFragment dialog = new PodcastOptionDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "podcast_options_dialog");
    }

    // What to do when a specific podcast option is selected
    @Override
    public void onPassPodcastOption(PodcastOptionDialogFragment.OptionSelected option, long podcastId) {
        switch (option) {
            case OPTION_REFRESH:
                Log.d("ID: ", ""+podcastId);
                downloadPodcastXml(feedList.getFromId(podcastId).getFeedUrl(), true);
                Toast.makeText(this, "Refreshing " + feedList.getFromId(podcastId).getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case OPTION_DELETE:
                deletePodcast(podcastId);
                break;
        }
    }

    // What to do when a result is received from FeedRequestService
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        if (resultCode == FeedResultReceiver.SUCCESS) {

            boolean refresh = resultData.getBoolean("REFRESH");
            Feed feed = (Feed) resultData.getSerializable("FEED");

            if (!refresh) {
                if (tryAddPodcastToList(feed)) {
                    new SaveFeedDbTask().execute(feed);
                }
            }
            else {
                new RefreshFeedDbTask().execute(feed);
            }
        }
        else if (resultCode == FeedResultReceiver.FAILURE) {
            Toast.makeText(this, "Failed to retrieve feed", Toast.LENGTH_SHORT).show();
        }

    }

    // Add a new podcast from URL
    public void downloadPodcastXml(String feed, boolean refresh) {

        Intent xmlIntent = new Intent(MainActivity.this, FeedRequestService.class);
        xmlIntent.putExtra("URL", feed);
        xmlIntent.putExtra("RECEIVER", xmlReceiver);
        xmlIntent.putExtra("REFRESH", refresh);
        startService(xmlIntent);

    }

    // Try to add a podcast to the list, returns true if added, false if not
    boolean tryAddPodcastToList(Feed feed) {
        if (feed != null) {
            if (!feedList.contains(feed.getTitle())) {
                feedList.add(feed);
                Toast.makeText(this, "Added " + feed.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, feed.getTitle() + " Already Saved", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    void refreshPodcastInList(Feed feed) {
        if (feed != null) {

            int index = feedList.indexOf(feed.getTitle());

            feedList.remove(index);
            feedList.add(index, feed);

            new RefreshFeedDbTask().execute(feed);
        }
    }

    // Getter for podcast list for use in fragment
    public FeedList getFeedList() {
        return feedList;
    }

    public PodcastDbHelper getDbHelper() {
        return dbHelper;
    }

    // Getter for selected podcast for use in fragment
    public long getSelectedPodcastId() {
        return selectedPodcastId;
    }

    // Updates the podcast view with the current state of the podcastList
    public void updatePodcastView() {
        PodcastListFragment podcastListFragment =
                (PodcastListFragment) getFragmentManager().findFragmentByTag(PODCAST_LIST_TAG);

        if (podcastListFragment != null) {
            podcastListFragment.updatePodcastView(feedList);
        }
    }

    // Removes a podcast from the list, deletes its local files, and updates the podcast view
    public void deletePodcast(long podcastId) {
        String title = feedList.getFromId(podcastId).getTitle();
        if (feedList.removeFromId(podcastId) != null) {
            new DeleteFeedTask().execute(podcastId);
            PodcastFileUtils.deletePodcast(this, title);
            updatePodcastView();
        }
    }

    public void refreshEpisodeView() {
        EpisodeListFragment episodeListFragment =
                (EpisodeListFragment) getFragmentManager().findFragmentByTag(EPISODE_LIST_TAG);

        if (episodeListFragment != null) {
            episodeListFragment.refreshEpisodes();
        }
    }

    public boolean isDownloading(String podcastTitle, String episodeTitle) {
        return episodeDownloads.isDownloading(this, podcastTitle, episodeTitle);
    }
}
