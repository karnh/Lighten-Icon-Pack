package com.webh.lightentheme;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Class for displaying dialog to allow user
 * to install Application Launcher
 */
public class MissingDialogFragment extends DialogFragment {

    // Interface to send selection to main activity
    public interface NoticeDialogListener {
        void onDialogPositiveClick(String marketLink);
        void onDialogNegativeClick(String marketLink);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get launcher name and play store package name for dialog
        Bundle mArgs = getArguments();
        String launcher = mArgs.getString("launcher");
        final String marketLink = mArgs.getString("link");

        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String messageText = String.format(getResources().getString(R.string.dialog_message),launcher);
        builder.setMessage(messageText)
                .setPositiveButton(R.string.dialog_install, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(marketLink);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(marketLink);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
