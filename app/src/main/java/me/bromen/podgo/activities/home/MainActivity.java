package me.bromen.podgo.activities.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import me.bromen.podgo.activities.home.dagger.DaggerHomeComponent;
import me.bromen.podgo.activities.home.dagger.HomeModule;
import me.bromen.podgo.activities.home.mvp.HomePresenter;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.activities.home.mvp.view.HomeViewImpl;
import me.bromen.podgo.app.PodGoApplication;
//import me.bromen.podgo.fragments.AddNewFeedFragment;


//import static me.bromen.podgo.activities.home.MainActivity.MainFragments.EpisodeList;

public class MainActivity extends AppCompatActivity {

    @Inject
    HomeView view;

    @Inject
    HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerHomeComponent.builder()
                .appComponent(PodGoApplication.get(this).component())
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);

        setContentView((HomeViewImpl) view);
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

//public class MainActivity extends AppCompatActivity
//        implements NewPodcastDialogFragment.OnDataPass, PodcastRecyclerAdapter.OnClickCallbacks,
//        PodcastOptionDialogFragment.OnDataPass, FeedResultReceiver.Receiver,
//        EpisodeRecyclerAdapter.OnClickCallbacks, ItunesRecyclerAdapter.OnClickCallbacks {
//
//    public enum MainFragments {
//        PodcastList,
//        EpisodeList,
//        AddNewFeed,
//        ItunesSearch
//    }
//
//    private long selectedPodcastId;
//    private FeedList feedList;
//
//    private PodcastDbHelper dbHelper;
//
//    private EpisodeDownloads episodeDownloads;
//
//    public FeedResultReceiver xmlReceiver;
//    private BroadcastReceiver downloadReceiver;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        dbHelper = new PodcastDbHelper(getApplicationContext());
//
//        if (savedInstanceState != null) {
//            feedList = (FeedList) savedInstanceState.getSerializable("FEEDLIST");
//            xmlReceiver = savedInstanceState.getParcelable("RECEIVER");
//            selectedPodcastId = savedInstanceState.getLong("SELECTEDPODCASTID");
//            episodeDownloads = (EpisodeDownloads) savedInstanceState.getSerializable("EPISODEDOWNLOADS");
//            episodeDownloads.validateDownloads(this);
//        }
//        else {
//            setUpPodcastList();
//            setUpXmlReceiver();
//
//            episodeDownloads = new EpisodeDownloads();
//
//            PodcastListFragment fragment = new PodcastListFragment();
//
//            getFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fragment_main, fragment, PodcastListFragment.TAG)
//                    .commit();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        xmlReceiver.setReceiver(this);
//        setUpDownloadReceiver();
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        xmlReceiver.setReceiver(null);
//        unregisterReceiver(downloadReceiver);
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        dbHelper.close();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putSerializable("FEEDLIST", feedList);
//        outState.putParcelable("RECEIVER", xmlReceiver);
//        outState.putLong("SELECTEDPODCASTID", selectedPodcastId);
//        outState.putSerializable("EPISODEDOWNLOADS", episodeDownloads);
//    }
//
//    @Override
//    public void onBackPressed() {
//        FragmentManager fm = getFragmentManager();
//
//        if (fm.getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStackImmediate();
//        }
//        else {
//            super.onBackPressed();
//        }
//    }
//
//    // Initial setup for podcastList
//    public void setUpPodcastList() {
//        new LoadFeedListTask().execute(PodcastDbContract.ORDER_ALPHA_ASC);
//    }
//
//    public class LoadFeedListTask extends AsyncTask<Integer, Void, FeedList> {
//
//        protected FeedList doInBackground(Integer... options) {
//            return dbHelper.loadAllFeeds(options[0]);
//        }
//
//        @Override
//        protected void onPostExecute(FeedList list) {
//            feedList = list;
//            updatePodcastView();
//        }
//    }
//
//    public class SaveFeedDbTask extends AsyncTask<Feed, Void, Void> {
//
//        protected Void doInBackground(Feed... feed) {
//            feed[0].setId(dbHelper.saveFeed(feed[0]));
//            feed[0].setFeedItems(null);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            updatePodcastView();
//        }
//    }
//
//    public class RefreshFeedDbTask extends AsyncTask<Feed, Void, Integer> {
//
//        protected Integer doInBackground(Feed... feed) {
//            return dbHelper.updateFeed(feed[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Integer newItems) {
//            Toast.makeText(getApplicationContext(), newItems + " Episodes Added", Toast.LENGTH_SHORT).show();
//            updatePodcastView();
//        }
//    }
//
//    public class DeleteFeedTask extends AsyncTask<Long, Void, Void> {
//
//        protected Void doInBackground(Long... id) {
//            dbHelper.deleteFeed(id[0]);
//            return null;
//        }
//    }
//
//    // Initial setup for FeedResultReceiver
//    public void setUpXmlReceiver() {
//        xmlReceiver = new FeedResultReceiver(new Handler());
//        xmlReceiver.setReceiver(this);
//    }
//
//    public void setUpDownloadReceiver() {
//        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//
//        downloadReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//
//                episodeDownloads.completeDownload(reference);
//
//                Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
//
//                refreshEpisodeView();
//            }
//        };
//
//        registerReceiver(downloadReceiver, intentFilter);
//    }
//
//    // What to do when data is received from new podcast dialog fragment
//    @Override
//    public void onPassUrl(String data) {
//        downloadPodcastXml(data, false);
//    }
//
//    // User selects a podcast
//    @Override
//    public void onPodcastSelected(long podcastId) {
//
//        Bundle args = new Bundle();
//        args.putLong("ID", podcastId);
//        args.putString("TITLE", feedList.getFromId(podcastId).getTitle());
//
//        createNewFragment(EpisodeList, args);
//    }
//
//    public void createNewFragment(MainFragments type, Bundle arguments) {
//        Fragment fragment;
//        String tag;
//        switch (type) {
//            case PodcastList:
//                fragment = new PodcastListFragment();
//                tag = PodcastListFragment.TAG;
//                break;
//
//            case EpisodeList:
//                fragment = new EpisodeListFragment();
//                tag = EpisodeListFragment.TAG;
//                break;
//
//            case AddNewFeed:
//                fragment = new AddNewFeedFragment();
//                tag = AddNewFeedFragment.TAG;
//                break;
//
//            case ItunesSearch:
//                fragment = new ItunesSearchFragment();
//                tag = ItunesSearchFragment.TAG;
//                break;
//
//            default:
//                return;
//        }
//        if (arguments != null) {
//            fragment.setArguments(arguments);
//        }
//        getFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_main, fragment, tag)
//                .addToBackStack(null)
//                .commit();
//    }
//
//    @Override
//    public void onEpisodeSelected(String podcastTitle, String episodeTitle) {
//        Toast.makeText(this, episodeTitle, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onDownloadPlaySelected(String podcastTitle, String episodeTitle, String episodeUrl) {
//        if (episodeUrl == null) {
//            Toast.makeText(this, "Missing Download URL", Toast.LENGTH_SHORT).show();
//        }
//        else if (isDownloading(podcastTitle, episodeTitle)) {
//            episodeDownloads.cancelDownload(this, podcastTitle, episodeTitle);
//        }
//        else if (PodcastFileUtils.isEpisodeDownloaded(this, podcastTitle, episodeTitle)) {
//            Toast.makeText(this, "Play Episode", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            episodeDownloads.startDownload(this, Uri.parse(episodeUrl), podcastTitle, episodeTitle);
//            refreshEpisodeView();
//        }
//    }
//
//    @Override
//    public void onFilterEpisodes(int position) {
//        EpisodeListFragment episodeListFragment =
//                (EpisodeListFragment) getFragmentManager().findFragmentByTag(EpisodeListFragment.TAG);
//
//        if (episodeListFragment != null) {
//            episodeListFragment.filterEpisodes(position);
//        }
//    }
//
//    // Display podcast options menu
//    @Override
//    public void onOptionsSelected(long podcastId) {
//        Bundle bundle = new Bundle();
//        bundle.putLong("ID", podcastId);
//
//        PodcastOptionDialogFragment dialog = new PodcastOptionDialogFragment();
//        dialog.setArguments(bundle);
//        dialog.show(getFragmentManager(), "podcast_options_dialog");
//    }
//
//    // What to do when a specific podcast option is selected
//    @Override
//    public void onPassPodcastOption(PodcastOptionDialogFragment.OptionSelected option, long podcastId) {
//        switch (option) {
//            case OPTION_REFRESH:
//                Log.d("ID: ", ""+podcastId);
//                downloadPodcastXml(feedList.getFromId(podcastId).getFeedUrl(), true);
//                Toast.makeText(this, "Refreshing " + feedList.getFromId(podcastId).getTitle(), Toast.LENGTH_SHORT).show();
//                break;
//
//            case OPTION_DELETE:
//                deletePodcast(podcastId);
//                break;
//        }
//    }
//
//    // What to do when a result is received from FeedRequestService
//    @Override
//    public void onReceiveResult(int resultCode, Bundle resultData) {
//
//        if (resultCode == FeedResultReceiver.SUCCESS) {
//
//            boolean refresh = resultData.getBoolean("REFRESH");
//            Feed feed = (Feed) resultData.getSerializable("FEED");
//
//            if (!refresh) {
//                if (tryAddPodcastToList(feed)) {
//                    new SaveFeedDbTask().execute(feed);
//                }
//            }
//            else {
//                new RefreshFeedDbTask().execute(feed);
//            }
//        }
//        else if (resultCode == FeedResultReceiver.FAILURE) {
//            Toast.makeText(this, "Failed to retrieve feed", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    public void onItunesPodcastSelected(String feedUrl) {
//        downloadPodcastXml(feedUrl, false);
//    }
//
//    // Add a new podcast from URL
//    public void downloadPodcastXml(String feed, boolean refresh) {
//
//        Intent xmlIntent = new Intent(MainActivity.this, FeedRequestService.class);
//        xmlIntent.putExtra("URL", feed);
//        xmlIntent.putExtra("RECEIVER", xmlReceiver);
//        xmlIntent.putExtra("REFRESH", refresh);
//        startService(xmlIntent);
//
//    }
//
//    // Try to add a podcast to the list, returns true if added, false if not
//    boolean tryAddPodcastToList(Feed feed) {
//        if (feed != null) {
//            if (!feedList.contains(feed.getTitle())) {
//                feedList.add(feed);
//                Toast.makeText(this, "Added " + feed.getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
//            } else {
//                Toast.makeText(this, feed.getTitle() + " Already Saved", Toast.LENGTH_SHORT).show();
//            }
//        }
//        return false;
//    }
//
//    void refreshPodcastInList(Feed feed) {
//        if (feed != null) {
//
//            int index = feedList.indexOf(feed.getTitle());
//
//            feedList.remove(index);
//            feedList.add(index, feed);
//
//            new RefreshFeedDbTask().execute(feed);
//        }
//    }
//
//    // Getter for podcast list for use in fragment
//    public FeedList getFeedList() {
//        return feedList;
//    }
//
//    public PodcastDbHelper getDbHelper() {
//        return dbHelper;
//    }
//
//    // Getter for selected podcast for use in fragment
//    public long getSelectedPodcastId() {
//        return selectedPodcastId;
//    }
//
//    // Updates the podcast view with the current state of the podcastList
//    public void updatePodcastView() {
//        PodcastListFragment podcastListFragment =
//                (PodcastListFragment) getFragmentManager().findFragmentByTag(PodcastListFragment.TAG);
//
//        if (podcastListFragment != null) {
//            podcastListFragment.updatePodcastView(feedList);
//        }
//    }
//
//    // Removes a podcast from the list, deletes its local files, and updates the podcast view
//    public void deletePodcast(long podcastId) {
//        String title = feedList.getFromId(podcastId).getTitle();
//        if (feedList.removeFromId(podcastId) != null) {
//            new DeleteFeedTask().execute(podcastId);
//            PodcastFileUtils.deletePodcast(this, title);
//            updatePodcastView();
//        }
//    }
//
//    public void refreshEpisodeView() {
//        EpisodeListFragment episodeListFragment =
//                (EpisodeListFragment) getFragmentManager().findFragmentByTag(EpisodeListFragment.TAG);
//
//        if (episodeListFragment != null) {
//            episodeListFragment.refreshEpisodes();
//        }
//    }
//
//    public boolean isDownloading(String podcastTitle, String episodeTitle) {
//        return episodeDownloads.isDownloading(this, podcastTitle, episodeTitle);
//    }
//}
