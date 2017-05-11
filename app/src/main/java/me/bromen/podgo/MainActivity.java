package me.bromen.podgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity
        implements NewPodcastFragment.OnDataPass, DownloadPodcastTaskFragment.TaskCallbacks,
        DownloadImageTaskFragment.TaskCallBacks, PodcastRecyclerAdapter.OnClickCallbacks {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private DownloadPodcastTaskFragment mTaskFragment;

    private static final String IMAGE_TASK_FRAGMENT = "image_task_fragment";
    private DownloadImageTaskFragment mImageTaskFragment;

    private PodcastList podcastList;
    private RecyclerView podcastListView;
    private RecyclerView.Adapter podcastListAdapter;

    private boolean isDownloadingXml;
    private boolean isDownloadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        // Load saved podcast info from files
        podcastList = new PodcastList();
        podcastList.loadPodcastInfo(this);

        // Find download task fragment if exists
        mTaskFragment = (DownloadPodcastTaskFragment)
                getFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);

        mImageTaskFragment = (DownloadImageTaskFragment)
                getFragmentManager().findFragmentByTag(IMAGE_TASK_FRAGMENT);

        isDownloadingXml = mTaskFragment != null;
        isDownloadingImage = mImageTaskFragment != null;

        setUpPodcastList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            // Bring up new podcast dialog fragment
            NewPodcastFragment fragment = new NewPodcastFragment();
            fragment.show(getFragmentManager(), "Dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpPodcastList() {
        podcastListView = (RecyclerView) findViewById(R.id.podcastListMain);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        podcastListView.setLayoutManager(layoutManager);
        podcastListView.setHasFixedSize(true);

        podcastListAdapter = new PodcastRecyclerAdapter(podcastList);
        podcastListView.setAdapter(podcastListAdapter);
    }

    // What to do when data is received from new podcast dialog fragment
    @Override
    public void onDataPass(String data) {
        addNewPodcast(data);
    }

    //
    public void addNewPodcast(String feed) {

        if (feed == null)
            return;

        // Find download task fragment if exists
        mTaskFragment = (DownloadPodcastTaskFragment)
                getFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);

        // If doesn't exist, start new download task fragment
        if (mTaskFragment == null) {

            mTaskFragment = new DownloadPodcastTaskFragment();

            // Pass feed URL to task
            Bundle bundle = new Bundle();
            bundle.putString("URL", feed);
            mTaskFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        else {
            Toast.makeText(this, "Other podcast info being downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPreExecuteXML() {
        isDownloadingXml = true;
        Toast.makeText(this, "Downloading Podcast Info", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelledXML(String message) {
        isDownloadingXml = false;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecuteXML(Podcast podcast) {
        isDownloadingXml = false;

        if (tryAddPodcastToList(podcast)) {
            try {
                downloadPodcastImage(podcast.getImageURL().toString(), podcast.getTitle());
            } catch (MalformedURLException | MalformedFeedException e) {
                e.printStackTrace();
            }
        }
    }

    boolean tryAddPodcastToList(Podcast podcast) {
        if (podcast != null) {
            try {
                if (!podcastList.contains(podcast.getTitle())) {
                    podcastList.add(podcast);
                    PodcastSaver.savePodcastInfo(getApplicationContext(), podcast);
                    ((PodcastRecyclerAdapter) podcastListAdapter).refreshList();
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

    void downloadPodcastImage(String url, String podcastTitle) {

        // Find download task fragment if exists
        mImageTaskFragment = (DownloadImageTaskFragment)
                getFragmentManager().findFragmentByTag(IMAGE_TASK_FRAGMENT);

        if (mImageTaskFragment == null) {
            mImageTaskFragment = new DownloadImageTaskFragment();

            Bundle bundle = new Bundle();
            bundle.putString("URL", url);
            bundle.putString("TITLE", podcastTitle);
            mImageTaskFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(mImageTaskFragment, IMAGE_TASK_FRAGMENT).commit();
        }
    }

    @Override
    public void onPreExecuteImage() {
        isDownloadingImage = true;
    }

    @Override
    public void onCancelledImage(String message) {
        isDownloadingImage = false;
    }

    @Override
    public void onPostExecuteImage(Bitmap bitmap, String title) {
        isDownloadingImage = false;
        PodcastSaver.savePodcastImage(this, title, bitmap, true);
        ((PodcastRecyclerAdapter) podcastListAdapter).refreshList();
    }

    @Override
    public void podcastSelected(String podcastTitle) {
        // Temp fix to avoid crashing when changing activity while download task is running
        if (isDownloadingXml || isDownloadingImage) {
            return;
        }

        Intent podActivity = new Intent(getApplicationContext(), PodcastAcitivity.class);
        podActivity.putExtra("TITLE", podcastTitle);

        startActivity(podActivity);
    }
}
