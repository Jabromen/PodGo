package me.bromen.podgo.activities.newfeed.mvp;

import android.annotation.SuppressLint;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import me.bromen.podgo.R;
import me.bromen.podgo.activities.newfeed.NewFeedActivity;
import me.bromen.podgo.activities.newfeed.mvp.contracts.NewFeedView;

/**
 * Created by jeff on 6/21/17.
 */

@SuppressLint("ViewConstructor")
public class NewFeedViewImpl extends FrameLayout implements NewFeedView {

    public static String TAG = "NewFeedViewImpl";

    @BindView(R.id.button_newfeed_itunes)
    Button itunesButton;

    @BindView(R.id.edittext_newfeed_manual)
    EditText manualEdittext;

    @BindView(R.id.button_newfeed_manual)
    Button manualButton;

    @BindView(R.id.toolbar_newfeed)
    Toolbar toolbar;

    public NewFeedViewImpl(NewFeedActivity activity) {
        super(activity);

        inflate(getContext(), R.layout.activity_newfeed, this);

        ButterKnife.bind(this);

        initToolbar();
    }

    private void initToolbar() {
        toolbar.setTitle("Add New Feed");
    }

    private String getEdittextUrl() {
        return manualEdittext.getText().toString();
    }

    @Override
    public void showDownloadSuccess() {
        Toast.makeText(getContext(), "Download Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDownloadError(String reason) {
        Log.d(TAG, "showDownloadError()");
        Toast.makeText(getContext(), "Download Error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSaveSuccess() {
        Toast.makeText(getContext(), "Save Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSaveError(String reason) {
        Toast.makeText(getContext(), "Save Error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<Object> observeItunesButton() {
        return RxView.clicks(itunesButton);
    }

    @Override
    public Observable<String> observeManualButton() {
        return RxView.clicks(manualButton).map(__ -> getEdittextUrl());
    }
}
