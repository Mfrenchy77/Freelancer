package com.frenchfriedtechnology.freelancer.View.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Events.DeleteLogEvent;
import com.frenchfriedtechnology.freelancer.Events.LogUpdatedEvent;
import com.frenchfriedtechnology.freelancer.Events.NamesChosenEvent;
import com.frenchfriedtechnology.freelancer.Freelancer;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.LogEntry;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Dialog to Change the Daily Log entry
 */
public class EditLogDialog extends DialogFragment {

    private static final String LOG_DAY = "LOG_DAY";
    private static final String LOG_POSITION = "LOG_POSITION";

    private FloatingActionButton deleteFab;

    private EditText textCash, textCheck, textMileage, textExpenses, textNotes;
    private String date, clients, cash, check, mileage, expenses, notes;
    private TextView textClients, textDate;
    private CoordinatorLayout root;

    public static EditLogDialog newInstance(String logDateToEdit, int position) {

        Bundle args = new Bundle();
        args.putString(LOG_DAY, logDateToEdit);
        args.putInt(LOG_POSITION, position);
        EditLogDialog fragment = new EditLogDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private String editLogDay;
    private Realm realm;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editLogDay = getArguments().getString(LOG_DAY);
        position = getArguments().getInt(LOG_POSITION);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Log Entry");
        FrameLayout fl = new FrameLayout(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.content_update_log, fl, false);
        fl.addView(view);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(Freelancer.getContext()).name(getResources()
                .getString(R.string.app_name)).build();
        realm = Realm.getInstance(realmConfig);

        root = (CoordinatorLayout) view.findViewById(R.id.root);
        deleteFab = (FloatingActionButton) view.findViewById(R.id.delete_log_fab);
        textClients = (TextView) view.findViewById(R.id.log_select_clients);
        textCheck = (EditText) view.findViewById(R.id.log_check_received);
        textCash = (EditText) view.findViewById(R.id.log_cash_received);
        textExpenses = (EditText) view.findViewById(R.id.log_expenses);
        textDate = (TextView) view.findViewById(R.id.log_current_date);
        textMileage = (EditText) view.findViewById(R.id.log_mileage);
        textNotes = (EditText) view.findViewById(R.id.log_notes);
        deleteFab.setVisibility(View.VISIBLE);
        textClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogSelectClients.newInstance(DialogSelectClients.MULTI, null).show(getFragmentManager(), null);
            }
        });

        LinearLayout upDateLog = (LinearLayout) view.findViewById(R.id.button_update_log);
        upDateLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateLog();
            }
        });
        LinearLayout closeLog = (LinearLayout) view.findViewById(R.id.button_close_log);
        closeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        setUpDisplay();
        builder.setView(fl);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        BusProvider.Instance.getBus().register(this);
        ButterKnife.bind(getActivity());

        return dialog;
    }

    private void setUpDisplay() {

        RealmResults<LogEntry> results = realm.where(LogEntry.class)
                .contains("day", editLogDay)
                .findAll();

        for (final LogEntry logEntry : results) {
            textDate.setText(logEntry.getDay());
            textCash.setText(logEntry.getCashReceived());
            textCheck.setText(logEntry.getChecksReceived());
            textExpenses.setText(logEntry.getExpenses());
            textMileage.setText(logEntry.getMileage());
            textClients.setText(logEntry.getClientsForDay());
            textNotes.setText(logEntry.getNotes());
            deleteFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete Log Entry
                    BusProvider.Instance.getBus().post(new DeleteLogEvent(logEntry.getDay(), position));
                    realm.beginTransaction();
                    logEntry.removeFromRealm();
                    realm.commitTransaction();
                    dismiss();
                }
            });
        }

    }

    /**
     * Send to Daily Log table upon confirmation from the dialog
     */
    public void updateLog() {

        if (TextUtils.isEmpty(textDate.getText())) {

            textDate.requestFocus();
            Snackbar.make(root, "Date can not be empty", Snackbar.LENGTH_SHORT).show();
        } else {
            date = textDate.getText().toString();
            clients = TextUtils.isEmpty(textClients.getText()) ? "" : textClients.getText().toString();
            cash = TextUtils.isEmpty(textCash.getText()) ? "0" : textCash.getText().toString();
            check = TextUtils.isEmpty(textCheck.getText()) ? "0" : textCheck.getText().toString();
            mileage = TextUtils.isEmpty(textMileage.getText()) ? "0" : textMileage.getText().toString();
            expenses = TextUtils.isEmpty(textExpenses.getText()) ? "0" : textExpenses.getText().toString();
            notes = TextUtils.isEmpty(textNotes.getText()) ? "" : textNotes.getText().toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.update_log_confirmation_title))
                .setMessage("Date - " + date + "\nClients - " + clients +
                        "\nCash - " + cash + "\nChecks - " + check +
                        "\nMileage - " + mileage + "\nExpenses - " +
                        expenses + "\nNotes - " + notes)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sendToDB();
                    }
                }).show();
    }

    /**
     * Send data to Daily Log T
     */
    public void sendToDB() {

        LogEntry logEntry = new LogEntry();
        logEntry.setDay(date);
        logEntry.setClientsForDay(clients);
        logEntry.setCashReceived(cash);
        logEntry.setChecksReceived(check);
        logEntry.setMileage(mileage);
        logEntry.setExpenses(expenses);
        logEntry.setNotes(notes);


        realm.beginTransaction();
        realm.copyToRealmOrUpdate(logEntry);
        realm.commitTransaction();
        dismiss();
        BusProvider.Instance.getBus().post(new LogUpdatedEvent());
    }

    @Subscribe
    public void onNamesChosenEvent(NamesChosenEvent event) {

        textClients.setText(event.getNames());
    }

    @Override
    public void dismiss() {
        super.dismiss();

        realm.close();
    }
}
