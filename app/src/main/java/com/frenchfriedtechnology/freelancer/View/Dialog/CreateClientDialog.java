package com.frenchfriedtechnology.freelancer.View.Dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Common.CircularAnimUtil;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Database.FreeLancerDatabaseAdapter;
import com.frenchfriedtechnology.freelancer.Events.DayChosenEvent;
import com.frenchfriedtechnology.freelancer.Events.PhoneContactChosenEvent;
import com.frenchfriedtechnology.freelancer.Events.PickContactFromPhoneEvent;
import com.frenchfriedtechnology.freelancer.Events.ReturnFromEditorEvent;
import com.frenchfriedtechnology.freelancer.Freelancer;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.Client;
import com.squareup.otto.Subscribe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Dialog to create a new client or to edit an existing client
 */

public class CreateClientDialog extends DialogFragment {
    @StringDef({NEW_CLIENT, EDIT_CLIENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Tags {

    }

    private static final int REQUEST_READ_CONTACTS = 0;

    public static final String EDIT_CLIENT = "EDIT_CLIENT";
    public static final String NEW_CLIENT = "NEW_CLIENT";
    private static final String CLIENT = "CLIENT";
    public static final String TAG = "TAG";

    private TextInputEditText clientName, clientNameTwo, clientRecurrence,
            clientEmail, clientPhone, clientAddress, clientRate, clientNotes;
    private LinearLayout createButton, cancelButton, root;
    private Button chooseFromContactsButton;
    /*
        private TextView clientRecurrence;
    */
    private ImageButton deleteClient;

    private boolean contactsAllowed = true;

    public static CreateClientDialog newInstance(String clientName, @StringRes String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(CLIENT, clientName);
        bundle.putString(TAG, tag);

        CreateClientDialog dialog = new CreateClientDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    private String editClientName;
    private String tag;
    private AlertDialog dialog;
    private Realm realm;
    private RealmConfiguration realmConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editClientName = getArguments().getString(CLIENT);
            tag = getArguments().getString(TAG);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(tag.equals(NEW_CLIENT) ? "New Client" : "Edit Client");
        final FrameLayout fl = new FrameLayout(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_create_client, fl, false);
        fl.addView(view);

        root = (LinearLayout) view.findViewById(R.id.create_client_root);
        chooseFromContactsButton = (Button) view.findViewById(R.id.choose_from_contacts_button);
        clientRecurrence = (TextInputEditText) view.findViewById(R.id.create_client_recurrence_button);
        clientAddress = (TextInputEditText) view.findViewById(R.id.client_location);
        clientNameTwo = (TextInputEditText) view.findViewById(R.id.client_name_2);
        cancelButton = (LinearLayout) view.findViewById(R.id.cancel_create_button);
        clientNotes = (TextInputEditText) view.findViewById(R.id.client_notes);
        clientName = (TextInputEditText) view.findViewById(R.id.client_name_1);
        clientEmail = (TextInputEditText) view.findViewById(R.id.client_email);
        clientPhone = (TextInputEditText) view.findViewById(R.id.client_phone);
        clientRate = (TextInputEditText) view.findViewById(R.id.client_rate);
/*
        clientRecurrence = (TextView) view.findViewById(R.id.client_recurrence);
*/
        createButton = (LinearLayout) view.findViewById(R.id.create_button);
        deleteClient = (ImageButton) view.findViewById(R.id.delete_client_button);
        clientName.requestFocus();
        checkForContactsPermission();

        //If editing client add the fields
        if (tag.equals(EDIT_CLIENT)) {
            setupData();
            deleteClient.setVisibility(View.VISIBLE);
            Log.d(Logger.TAG, "CreateClientDialog Edit Client: " + editClientName);
            RealmResults<Client> results = realm.where(Client.class)
                    .contains("name", editClientName)
                    .findAll();
            for (final Client client : results) {
                clientName.setText(client.getName());
                clientNameTwo.setText(client.getName2());
                clientEmail.setText(client.getEmail());
                clientPhone.setText(client.getPhone());
                clientAddress.setText(client.getAddress());
                clientRate.setText(client.getRate());
                clientRecurrence.setText(client.getRecurrence());
                clientNotes.setText(client.getNotes());
                deleteClient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), clientName.getText() + " deleted", Toast.LENGTH_SHORT).show();
                        realm.beginTransaction();
                        client.removeFromRealm();
                        realm.commitTransaction();
                        dismiss();
                    }
                });
            }
/*
            realm.close();
*/
        } else setupData();
        clientRate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                clientRecurrence.callOnClick();
                return true;
            }
        });
        clientRecurrence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick a day of the week
                DialogFrequencyPicker.newInstance(R.string.create_client_recurrence_title
                        , DialogFrequencyPicker.CHOSE_CLIENT_RECURRENCE,
                        tag.equals(NEW_CLIENT) ? null : clientRecurrence.getText().toString())
                        .show(getFragmentManager(), null);
            }
        });
        clientNotes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                createClient();
                return true;
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClient();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        chooseFromContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.Instance.getBus().post(new PickContactFromPhoneEvent());
               /*  Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);

               Log.d(Logger.TAG, "Contacts Allowed? " + contactsAllowed);
                if (contactsAllowed) { //open contacts list and populate fields on selection
                    Toast.makeText(getActivity(), "Open Contacts", Toast.LENGTH_SHORT).show();
                    new ContactFromPhoneDialog().show(getFragmentManager(), null);
                } else checkForContactsPermission();*/
            }
        });

        //set the contact autocomplete        clientName.setAdapter(new );
        builder.setView(fl);
        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                CircularAnimUtil.dialogReveal(view, true, null);
            }
        });

        BusProvider.Instance.getBus().register(this);
        ButterKnife.bind(getActivity());

        dialog.show();

        return dialog;

    }

    private void setupData() {

        realmConfig = new RealmConfiguration.Builder(Freelancer.getContext()).name(getResources()
                .getString(R.string.app_name)).build();

        realm = Realm.getInstance(realmConfig);

    }

    private void checkForContactsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Subscribe
    public void daysChosenEvent(DayChosenEvent event) {
        if (DialogFrequencyPicker.CHOSE_CLIENT_RECURRENCE.equals(event.getTag())) {
            Log.d(Logger.TAG, "BUS FOR - " + event.getTag() + "\n" + event.getText());
            clientRecurrence.setText(event.getText());
            clientNotes.requestFocus();
        }
    }

    @Subscribe
    public void contactFromPhoneEvent(PhoneContactChosenEvent event) {
        Log.d(Logger.TAG, "CreateClientDialog contactFromPhoneEvent for: " + event.getContactName());
        clientName.setText(event.getContactName());
        clientNameTwo.setText(event.getContactName2());
        clientEmail.setText(event.getContactEmail());
        clientPhone.setText(event.getContactPhone());
        clientAddress.setText(event.getContactAddress());
        clientNotes.setText(event.getContactNotes());
    }

    @OnEditorAction(R.id.client_rate)
    boolean onSelectRecurrence() {
        DialogFrequencyPicker.newInstance(R.string.create_client_recurrence_title
                , DialogFrequencyPicker.CHOSE_CLIENT_RECURRENCE,
                tag.equals(NEW_CLIENT) ? null : clientRecurrence.getText().toString())
                .show(getFragmentManager(), null);
        return true;
    }

    @OnEditorAction(R.id.client_notes)
    boolean onFinished() {
        createClient();
        return true;
    }

    public void createClient() {
        setupData();
        String name = clientName.getText().toString();
        String name2 = clientNameTwo.getText().toString();
        String email = clientEmail.getText().toString();
        String phone = clientPhone.getText().toString();
        String address = clientAddress.getText().toString();
        String rate = clientRate.getText().toString();
        String recurrence = clientRecurrence.getText().toString();

        //put empty text in place of hint Text
        if (recurrence.equals(getResources().getString(R.string.create_client_list_recurrence))) {
            recurrence = "";
        }
        String notes = clientNotes.getText().toString();
        //take info and store it in DataBase
        if (!TextUtils.isEmpty(name)) {

            Client client = new Client();
            client.setName(name);
            client.setName2(name2);
            client.setEmail(email);
            client.setPhone(phone);
            client.setAddress(address);
            client.setRate(rate);
            client.setRecurrence(recurrence);
            client.setNotes(notes);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(client);
            realm.commitTransaction();

/*
            realm.close();
*/
            if (tag.equals(EDIT_CLIENT)) {

                Toast.makeText(getActivity(), "Edited " + clientName.getText(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "New Client " + clientName.getText() + " saved", Toast.LENGTH_SHORT).show();
            }


//          Switched to Realm for Database, but keeping original code to show how SQL works

//            long id = freeLancerDatabaseAdapter
//                    .insertDataClientList(name, name2, email, phone, address, rate, recurrence, notes);
//            if (id < 0) {
//                //Error
//                Toast.makeText(Freelancer.getContext(), "Error Creating New Client", Toast.LENGTH_SHORT).show();
//            } else {
//                //success
//            }

            dismiss();
        } else {
            Toast.makeText(getActivity(), "No Name Entered", Toast.LENGTH_SHORT).show();
            clientName.requestFocus();

        }
    }

    public void cancel() {
        //close Dialog
        dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {

        Log.d(Logger.TAG, "Permission Results: " + requestCode + " " + permissions + " " + grantResults);
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFromContactsButton.setVisibility(View.VISIBLE);
                    chooseFromContactsButton.setClickable(true);
                    contactsAllowed = true;
                    Log.d(Logger.TAG, "Permission? " + contactsAllowed);

                } else {
                    Log.d(Logger.TAG, "Permission? " + contactsAllowed);
                    contactsAllowed = false;
                    chooseFromContactsButton.setClickable(false);
                    chooseFromContactsButton.setVisibility(View.GONE);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }/*

    public void setContact(Uri contactLookupUri) {
        mContactUri = contactLookupUri;


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/

    @Override
    public void dismiss() {
        super.dismiss();
        BusProvider.Instance.getBus().post(new ReturnFromEditorEvent());
    }
}

