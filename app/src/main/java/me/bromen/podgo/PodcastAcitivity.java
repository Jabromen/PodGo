package me.bromen.podgo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.icosillion.podengine.models.Podcast;

public class PodcastAcitivity extends AppCompatActivity {

    private RecyclerView episodeView;
    private RecyclerView.Adapter episodeListAdapter;

    private int filterOption;

    private Podcast podcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        String title = getIntent().getStringExtra("TITLE");
        getSupportActionBar().setTitle(title);

        Bitmap bitmap = PodcastSaver.getPodcastImage(this, title);

        if (bitmap != null) {
            ImageView imageView = (ImageView) findViewById(R.id.podcast_toolbar_image);
            imageView.setImageBitmap(bitmap);
        }

        podcast = PodcastSaver.loadPodcastFromFile(this, title);

        if (savedInstanceState != null) {
            filterOption = savedInstanceState.getInt("FILTER");
        }
        else {
            filterOption = 0;
        }

        setUpEpisodeList();
        setUpFilterSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_podcast_acitivity, menu);

        MenuItem item = menu.findItem(R.id.action_filter);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filter_episodes_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_filter) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("FILTER", filterOption);
    }

    private void setUpEpisodeList() {

        episodeView = (RecyclerView) findViewById(R.id.episodeListPodcastActivity);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        episodeView.setLayoutManager(layoutManager);
        episodeView.setHasFixedSize(true);

        episodeListAdapter = new EpisodeRecyclerAdapter(podcast.getEpisodes());
        episodeView.setAdapter(episodeListAdapter);
        filterEpisodes(filterOption);
    }

    private void setUpFilterSpinner() {

        Spinner spinner = (Spinner) findViewById(R.id.filter_episodes_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filter_episodes_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(filterOption);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterEpisodes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterEpisodes(int option) {

        filterOption = option;

        switch (option) {

            // Show All Episodes
            case 0:
                ((EpisodeRecyclerAdapter) episodeListAdapter).updateList(podcast.getEpisodes());
                break;

            // Show Most Recent 7 Episodes
            case 1:
                int index = podcast.getEpisodes().size() < 7 ? podcast.getEpisodes().size() : 7;
                ((EpisodeRecyclerAdapter) episodeListAdapter).updateList(podcast.getEpisodes().subList(0, index));
                break;

            // TODO: Add filters for downloaded and not downloaded episodes
            default:
                Toast.makeText(this, "Option Not Implemented Yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
