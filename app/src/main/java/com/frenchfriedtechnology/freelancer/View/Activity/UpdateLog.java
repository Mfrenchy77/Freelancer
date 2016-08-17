package com.frenchfriedtechnology.freelancer.View.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Events.NamesChosenEvent;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.LogEntry;
import com.frenchfriedtechnology.freelancer.View.Dialog.DialogSelectClients;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * This activity updates the Daily Log Table, Opens from the notification and auto suggest clients
 * from user's Client List Table
 */
public class UpdateLog extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_log;
    }

    @Bind(R.id.root)
    CoordinatorLayout root;
    @Bind(R.id.log_current_date)
    TextView textDate;
    @Bind(R.id.log_cash_received)
    EditText textCash;
    @Bind(R.id.log_check_received)
    EditText textCheck;
    @Bind(R.id.log_mileage)
    EditText textMileage;
    @Bind(R.id.log_expenses)
    EditText textExpenses;
    @Bind(R.id.log_notes)
    EditText textNotes;
    @Bind(R.id.log_select_clients)
    TextView textClients;

    private String format = "MM/dd/yy";
    private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
    private Calendar currentDate = Calendar.getInstance();

    private String date, clients, cash, check, mileage, expenses, notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textDate.setText(sdf.format(currentDate.getTime()));
    }

    /**
     * Image Button to update Daily Log
     */
    public void upDateButton(View view) {

        updateLog();
    }

    /**
     * This is set on Linear Layout for the TextView, it opens the MULTI-choice selector
     * and the sets the names into the TextView
     */
    public void selectClients(View view) {
        try {
            Date date = sdf.parse(textDate.getText().toString());
            currentDate.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.US);
        String day = simpleDateFormat.format(currentDate.getTime());
        Log.d(Logger.TAG, "UpdateLog selectClients() day: " + textDate.getText().toString() +" "+day);
        DialogSelectClients.newInstance(DialogSelectClients.MULTI, day).show(getSupportFragmentManager(), null);
    }

    /**
     * Opens Date picker Dialog to choose date and clears selected clients
     */
    @OnClick(R.id.log_current_date)
    void onChooseDate() {

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                textDate.setText(sdf.format(newDate.getTime()));
                textClients.setText("");
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * If user moves to end of update form and presses finished on the keyboard, it will initiate
     * the confirmation dialog
     */
    @OnEditorAction(R.id.log_notes)
    boolean onNotesAction() {

        updateLog();
        return true;
    }

    /**
     * Bus event receiving the selections from the dialog and inserting into TextView
     */
    @Subscribe
    public void onReceiveSelectedClients(NamesChosenEvent event) {

        textClients.setText(event.getNames());
    }

    /**
     * Method that takes all the Text fields and sends to Daily Log table upon confirmation from the
     * dialog
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

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Dialog_Theme));
        builder.setTitle(getResources().getString(R.string.update_log_confirmation_title))
                .setMessage("Date - " + date + "\nClients - " + clients +
                        "\nCash - " + cash + "\nChecks - " + check +
                        "\nMileage - " + mileage + "\nExpenses - " +
                        expenses + "\nNotes - " + notes)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveDailyLog();
                    }
                }).show();
    }

    /**
     * Save Daily Log in Realm Database
     */
    public void saveDailyLog() {
        // Get a Realm instance for this thread

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).name(getResources()
                .getString(R.string.app_name)).build();
        Realm realm = Realm.getInstance(realmConfig);

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
        realm.close();

        Toast.makeText(this, "Updated Daily Log for " + date, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    /**
     * Image button to close the Update Log activity
     */
    public void closeLog(View view) {
        onBackPressed();
    }

}
