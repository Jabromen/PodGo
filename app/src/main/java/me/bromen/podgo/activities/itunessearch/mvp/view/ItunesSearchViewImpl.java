package me.bromen.podgo.activities.itunessearch.mvp.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.itunessearch.ItunesSearchActivity;
import me.bromen.podgo.activities.itunessearch.mvp.contracts.ItunesSearchView;
import me.bromen.podgo.ext.structures.ItunesPodcast;

/**
 * Created by jeff on 6/22/17.
 */

@SuppressLint("ViewConstructor")
public class ItunesSearchViewImpl extends FrameLayout implements ItunesSearchView {

    private final Picasso picasso;

    @BindView(R.id.recycler_itunessearch)
    RecyclerView searchResultsView;

    @BindView(R.id.textview_itunessearch_error)
    TextView noResultsView;

    @BindView(R.id.toolbar_itunessearch)
    Toolbar toolbar;

    private SearchView searchView;
    private ItunesRecyclerAdapter searchAdapter;
    private ProgressDialog loadingDialog = new ProgressDialog(getContext());

    public ItunesSearchViewImpl(ItunesSearchActivity activity, Picasso picasso) {
        super(activity);
        this.picasso = picasso;

        inflate(getContext(), R.layout.activity_itunessearch, this);

        ButterKnife.bind(this);

        initToolbar();
        initSearchRecycler();
    }

    private void initToolbar() {
        toolbar.setTitle("Itunes Search");
        toolbar.inflateMenu(R.menu.menu_itunessearch);

        searchView = (SearchView) toolbar.getMenu()
                .findItem(R.id.action_itunes_search)
                .getActionView();
    }

    private void initSearchRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        searchResultsView.setLayoutManager(layoutManager);
        searchResultsView.setHasFixedSize(true);

        searchAdapter = new ItunesRecyclerAdapter(picasso);
        searchResultsView.setAdapter(searchAdapter);
    }

    @Override
    public void showSearchResults(List<ItunesPodcast> results) {
        searchResultsView.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);

        searchAdapter.updateList(results);
    }

    @Override
    public void showNoSearchResults() {
        searchResultsView.setVisibility(View.GONE);
        noResultsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingResults(boolean loading) {
        showLoading(loading, "Loading Search Results");
    }

    @Override
    public void showLoadingFeed(boolean loading) {
        showLoading(loading, "Downloading Feed");
    }

    private void showLoading(boolean loading, String message) {
        if (loading) {
            loadingDialog.setMessage(message);
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showDownloadSuccess() {
        Toast.makeText(getContext(), "Download Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDownloadError(String reason) {
        Toast.makeText(getContext(), "Save Error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<String> observeSearchQuery() {
        return RxSearchView.queryTextChangeEvents(searchView)
                .filter(SearchViewQueryTextEvent::isSubmitted)
                .map(searchViewQueryTextEvent -> searchViewQueryTextEvent.queryText().toString());
    }

    @Override
    public Observable<String> observeDownloadFeedClick() {
        return searchAdapter.getDownloadClickedObservable().map(ItunesPodcast::getFeedUrl);
    }
}
