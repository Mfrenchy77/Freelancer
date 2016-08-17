package com.frenchfriedtechnology.freelancer.View.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.Events.DayChosenEvent;
import com.frenchfriedtechnology.freelancer.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Dialog to pick Days of the Week
 */
public class DialogFrequencyPicker extends DialogFragment {

    @StringDef({CHOSE_CLIENT_RECURRENCE, CHOSE_DAYS_FOR_NOTIFICATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Tags {
    }

    public static final String CHOSE_DAYS_FOR_NOTIFICATION = "CHOSE_DAYS_FOR_NOTIFICATION";
    public static final String CHOSE_CLIENT_RECURRENCE = "CHOSE_CLIENT_RECURRENCE";
    public static final String RECURRENCE_FOR_CLIENT = "RECURRENCE_FOR_CLIENT";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_TAG = "EXTRA_TAG";

    private ListView daysList;

    public static DialogFrequencyPicker newInstance(
            @StringRes int title, @StringRes String tag, String recurrenceForClient) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_TITLE, title);
        args.putString(RECURRENCE_FOR_CLIENT, recurrenceForClient);
        args.putString(EXTRA_TAG, tag);

        DialogFrequencyPicker dialog = new DialogFrequencyPicker();
        dialog.setArguments(args);

        return dialog;
    }

    private
    @StringRes
    int title;
    private String tag;
    private String recurrence;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            title = getArguments().getInt(EXTRA_TITLE);
            tag = getArguments().getString(EXTRA_TAG);
            recurrence = getArguments().getString(RECURRENCE_FOR_CLIENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Dialog_Theme);
        builder.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_frequency, null);
        builder.setView(view);

        daysList = (ListView) view.findViewById(R.id.dialog_frequency_list);
        daysList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] days = getResources().getStringArray(R.array.days_of_week);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity()
                , android.R.layout.simple_list_item_multiple_choice, days);
        daysList.setAdapter(adapter);

        // set the selection if it is saved already
        if (tag.equals(CHOSE_DAYS_FOR_NOTIFICATION)) {

            for (int i = 0; i < days.length; i++) {
                if (new UserPrefs().getNotifyFrequency() != null &&
                        new UserPrefs().getNotifyFrequency()
                                .contains(daysList.getItemAtPosition(i).toString())) {

                    daysList.setItemChecked(i, true);
                }
            }
            new UserPrefs().clearNotifyFrequency();
        }
        if (recurrence != null) {

            for (int i = 0; i < days.length; i++) {
                if (recurrence.contains(daysList.getItemAtPosition(i).toString())) {

                    daysList.setItemChecked(i, true);
                }
            }
        }

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //save days to user prefs

                String selected = "";
                int cntChoice = daysList.getCount();
                SparseBooleanArray sparseBooleanArray = daysList.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
                        selected += daysList.getItemAtPosition(i).toString() + " ";
                        if (tag.equals(CHOSE_DAYS_FOR_NOTIFICATION)) {
                            new UserPrefs().setNotifyFrequency(daysList.getItemAtPosition(i).toString());
                        }
                    }
                }

                Log.d(Logger.TAG, "selected = " + selected);
                BusProvider.Instance.getBus().post(new DayChosenEvent(selected, tag));
            }
        });
        return builder.create();
    }
}
