package me.bromen.podgo.activities.home.mvp.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.support.v7.widget.RxPopupMenu;
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.activities.home.mvp.contracts.HomeView;
import me.bromen.podgo.extras.structures.Feed;
import me.bromen.podgo.extras.utilities.DisplayUtils;
import me.bromen.podgo.fragments.mediaplayerbar.MediaplayerBarFragment;
import me.bromen.podgo.fragments.mediaplayerbar.mvp.contracts.MediaplayerBarView;

/**
 * Created by jeff on 6/20/17.
 */

@SuppressLint("ViewConstructor")
public class HomeViewImpl extends FrameLayout implements HomeView {

    private static final String FRAGMENT_TAG = "homeFragment";

    @BindView(R.id.recycler_main)
    RecyclerView feedView;

    @BindView(R.id.textview_main_nopodcasts)
    TextView noFeedsView;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.mediaplayer_bar_main)
    FrameLayout mediaplayerBar;

    private final Activity activity;
    private final Picasso picasso;
    private PodcastRecyclerAdapter adapter;
    private final ProgressDialog progressDialog = new ProgressDialog(getContext());
    private final PopupMenu optionsPopup;

    public HomeViewImpl(MainActivity activity, Picasso picasso) {
        super(activity);
        this.activity = activity;
        this.picasso = picasso;

        inflate(getContext(), R.layout.activity_main, this);

        ButterKnife.bind(this);

        initToolbar();
        initFeedView();

        optionsPopup = new PopupMenu(getContext(), toolbar);
        optionsPopup.inflate(R.menu.menu_feed_options);

        mediaplayerBar.setVisibility(View.VISIBLE);
    }

    private void initToolbar() {

        toolbar.setTitle("Podcasts");
        toolbar.inflateMenu(R.menu.menu_main);
    }

    private void initFeedView() {

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getContext(), DisplayUtils.calculateNoOfColumns(getContext(), 125, 3));
        feedView.setLayoutManager(layoutManager);
        feedView.setHasFixedSize(true);

        adapter = new PodcastRecyclerAdapter(picasso);
        feedView.setAdapter(adapter);
    }

    public void createMediaplayerBar() {
        MediaplayerBarFragment fragment = new MediaplayerBarFragment();
        FragmentManager fm = ((MainActivity) activity).getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.mediaplayer_bar_main, fragment, FRAGMENT_TAG)
                .commit();
    }

    public void destroyMediaplayerBar() {
        FragmentManager fm = ((MainActivity) activity).getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void showNoFeeds() {
        feedView.setVisibility(View.GONE);
        noFeedsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFeeds(List<Feed> feedList) {
        feedView.setVisibility(View.VISIBLE);
        noFeedsView.setVisibility(View.GONE);
        adapter.updateList(feedList);
    }

    @Override
    public void showLoading(boolean loading) {
        if (loading) {
            progressDialog.show();
            progressDialog.setMessage("Loading Feeds");
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), "Error Loading Feeds", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFeedOptions() {
        optionsPopup.show();
    }

    @Override
    public void showNewEpisodes(int newEps) {
        if (newEps > 0) {
            Toast.makeText(getContext(), Integer.toString(newEps) + " Episodes Added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Feed up to date", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMediaplayerBar(boolean show) {
        if (show) {
            mediaplayerBar.setVisibility(View.VISIBLE);
        } else {
            mediaplayerBar.setVisibility(View.GONE);
        }
    }

    @Override
    public Observable<Integer> observeMenuItemClick() {
        return RxToolbar.itemClicks(toolbar).map(MenuItem::getItemId);
    }

    @Override
    public Observable<Feed> observeFeedTileClick() {
        return adapter.getTileClickedObservable();
    }

    @Override
    public Observable<Feed> observeFeedOptionsClick() {
        return adapter.getOptionsClickedObservable();
    }

    @Override
    public Observable<Integer> observeFeedOptionMenuClick() {
        return RxPopupMenu.itemClicks(optionsPopup).map(MenuItem::getItemId);
    }
}
