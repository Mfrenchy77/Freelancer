package com.frenchfriedtechnology.freelancer.View.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Events.NamesChosenEvent;
import com.frenchfriedtechnology.freelancer.Freelancer;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.Client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

// TODO: 2/4/16 keep woking on the auto populate feature

/**
 * Dialog for selecting users clients from a list. Auto populate selection based on day
 */
public class DialogSelectClients extends DialogFragment {

    @StringDef({MULTI, SINGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Tags {}

    public static final String MULTI = "MULTI";
    public static final String SINGLE = "SINGLE";
    public static final String TAG = "TAG";
    public static final String DAY= "DAY";

    public static DialogSelectClients newInstance(@StringRes String tag, String day) {

        Bundle bundle = new Bundle();
        bundle.putString(TAG, tag);
        bundle.putString(DAY,day);
        DialogSelectClients fragment = new DialogSelectClients();
        fragment.setArguments(bundle);
        return fragment;
    }

    private ArrayList<String> clients = new ArrayList<>();
    private ListView clientNameList;
    private Realm realm;
    private String tag;
    private String day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        tag = getArguments().getString(TAG);
        day = getArguments().getString(DAY);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.update_log_title));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_frequency, null);
        builder.setView(view);

        clientNameList = (ListView) view.findViewById(R.id.dialog_frequency_list);
        clientNameList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(Freelancer.getContext()).name(getResources()
                .getString(R.string.app_name)).build();

        realm = Realm.getInstance(realmConfig);
        final RealmResults<Client> results = realm.where(Client.class).findAllSorted("name");

        ArrayList<String> recurrence = new ArrayList<>();
        for (Client c : results) {
            clients.add(c.getName() + "\n" + c.getName2() + "\n");
            recurrence.add(c.getRecurrence());

        }

        //set single or multiple choice adapter
        final ArrayAdapter<String> adapter = tag.equals(MULTI) ? new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                clients) : new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_single_choice,
                clients);

        clientNameList.setAdapter(adapter);

        //set the selection if it is saved already
        if (tag.equals(MULTI)) {

            for (int i = 0; i < clients.size(); i++) {
                if (recurrence.get(i).contains(day)) {
                    clientNameList.setItemChecked(i, true);
                }
            }

            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selected = "";
                    int cntChoice = clientNameList.getCount();
                    SparseBooleanArray sparseBooleanArray = clientNameList.getCheckedItemPositions();
                    for (int i = 0; i < cntChoice; i++) {
                        if (sparseBooleanArray.get(i)) {
                            selected += clientNameList.getItemAtPosition(i).toString() + " ";
                        }
                    }
                    Log.d(Logger.TAG, "selected = " + selected);
                    BusProvider.Instance.getBus().post(new NamesChosenEvent(selected, null));
                }
            });
        } else {

            clientNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BusProvider.Instance.getBus().post(new NamesChosenEvent(adapter.getItem(position), results.get(position).getRate()));
                    Log.d(Logger.TAG, "DialogSelectClients, onItemSelected: " + adapter.getItem(position));
                    dismiss();
                }
            });


        }
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
    }
}


