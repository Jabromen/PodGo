package me.bromen.podgo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.AlertDialog;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by jeff on 5/5/17.
 */

public class NewPodcastFragment extends DialogFragment {

    // Interface used to pass data back to the calling activity
    interface OnDataPass {
        void onDataPass(String data);
    }

    OnDataPass dataPasser;

    // Pass data back to the calling activity
    public void passData(String data) {
        dataPasser.onDataPass(data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the new podcast prompt
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View promptView = inflater.inflate(R.layout.prompt_new_podcast, null, false);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setView(promptView);

        final EditText userInput = (EditText) promptView.findViewById(R.id.rssFeedUserInput);

        userInput.setText("http://");
        Selection.setSelection(userInput.getText(), userInput.getText().length());

        dialog
                .setCancelable(false)
                .setPositiveButton(R.string.new_podcast_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                passData(userInput.getText().toString());
                            }
                        })
                .setNegativeButton(R.string.new_podcast_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        return dialog.create();
    }
}
