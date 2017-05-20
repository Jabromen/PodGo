package me.bromen.podgo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by jeff on 5/13/17.
 */

public class PodcastOptionDialogFragment extends DialogFragment {

    CharSequence options[] = new CharSequence[] {"Refresh Feed", "Delete Podcast"};

    enum OptionSelected {
        OPTION_REFRESH,
        OPTION_DELETE
    }

    interface OnDataPass {
        void onPassPodcastOption(OptionSelected option, String title);
    }

    OnDataPass mCallbacks;
    private String podcastTitle;

    public void passOption(OptionSelected option, String title) {
        if (mCallbacks != null) {
            mCallbacks.onPassPodcastOption(option, title);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mCallbacks = (OnDataPass) getActivity();

        podcastTitle = getArguments().getString("TITLE");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (OptionSelected.values()[which] == OptionSelected.OPTION_DELETE) {
                    confirmDelete();
                }
                else {
                    passOption(OptionSelected.values()[which], podcastTitle);
                }
            }
        });

        builder.setCancelable(true);

        return builder.create();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(getActivity())
                .setMessage("Delete Podcast?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passOption(OptionSelected.OPTION_DELETE, podcastTitle);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
