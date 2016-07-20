package com.frenchfriedtechnology.freelancer.View.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.Adapter.ClientListAdapter;
import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Events.ClientPhoneClickedEvent;
import com.frenchfriedtechnology.freelancer.Events.EditClientClickedEvent;
import com.frenchfriedtechnology.freelancer.Events.PhoneContactChosenEvent;
import com.frenchfriedtechnology.freelancer.Events.PickContactFromPhoneEvent;
import com.frenchfriedtechnology.freelancer.Events.ReturnFromEditorEvent;
import com.frenchfriedtechnology.freelancer.Freelancer;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.Client;
import com.frenchfriedtechnology.freelancer.View.Dialog.CreateClientDialog;
import com.frenchfriedtechnology.freelancer.View.Widgets.FreelancerRealmRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ClientList extends BaseActivity {
    private static final String STATE_CLIENT_LIST = "STATE_CLIENT_LIST";
    private static final String IS_NEW_CLIENT = "IS_NEW_CLIENT";

    static final int PICK_CONTACT_REQUEST = 69;
    private String contactID;

    @Bind(R.id.client_recyclerView)
    FreelancerRealmRecyclerView clientRecyclerView;
    @Bind(R.id.activity_client_list_fab)
    FloatingActionButton newClientFab;

    /*
        private RealmRecyclerView realmRecyclerView;

        private ClientListRealmAdapter mRealmAdapter;
        */
    private ArrayList<Client> listClient = new ArrayList<>();

    //    private ArrayList<CLObject> listClient = new ArrayList<>();
    private ClientListAdapter clientListAdapter;
    private Realm realm;
    private RealmConfiguration realmConfig;
    RealmResults<Client> results;
    private StaggeredGridLayoutManager layoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_client_list;
    }

    public static Intent startClientListNewClient(Context context, boolean isNewClient) {
        Intent intent = new Intent(context, ClientList.class);
        intent.putExtra(IS_NEW_CLIENT, isNewClient);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra(IS_NEW_CLIENT, false)) {
            //start CreateClientDialog for new Client
            CreateClientDialog.newInstance(null, CreateClientDialog.NEW_CLIENT).show(getSupportFragmentManager(), null);

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupData();
        setUpRecycler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Logger.TAG, "ClientList onDestroy()");
        realm.close();
        realm = null;
    }

    private void setupData() {

        realmConfig = new RealmConfiguration.Builder(this)
                .name(getResources()
                        .getString(R.string.app_name))
                .build();
        realm = Realm.getInstance(realmConfig);
    }

    private void setUpRecycler() {
        //initialize recycler
        // Get a Realm instance for this thread
/*
        realmRecyclerView = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);

        resetRealm();
        Realm.setDefaultConfiguration(getRealmConfig());
            realm = Realm.getDefaultInstance();*/

        results = realm.where(Client.class).findAllSorted("name");
//        FreeLancerDatabaseAdapter freeLancerDatabaseAdapter = new FreeLancerDatabaseAdapter(this);
     /*    mRealmAdapter = new ClientListRealmAdapter(this, results, true, true);
        realmRecyclerView.setAdapter(mRealmAdapter);*/
        clientRecyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        clientRecyclerView.setLayoutManager(layoutManager);
        clientListAdapter = new ClientListAdapter(this, realm, results);
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                clientListAdapter.updateResults(results);
                results.removeChangeListener(this);
            }
        });
  /*      ClientListRealmAdapter clientListRealmAdapter = new ClientListRealmAdapter(this, results, true, true);
        clientRecyclerView.setAdapter(clientListRealmAdapter);


        listClient = freeLancerDatabaseAdapter.getClientList();


        for (Client c : results) {
            listClient.add(c);
        }
        //Open Create Client Dialog if DataBase is Empty
        if (listClient.isEmpty()) {
            CreateClientDialog.newInstance(null, CreateClientDialog.NEW_CLIENT)
                    .show(getSupportFragmentManager(), null);
        }
        clientListAdapter.setList(listClient);*/
        clientRecyclerView.setAdapter(clientListAdapter);

    }

    @Override
    protected void onStart() {

        Log.d(Logger.TAG, "ClientList onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(Logger.TAG, "ClientList onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
/*
        outState.putParcelable(STATE_CLIENT_LIST, clientRecyclerView.getLayoutManager().onSaveInstanceState());
*/
        clientRecyclerView.setSaveEnabled(true);
        Log.d(Logger.TAG, "ClientList onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Parcelable state = savedInstanceState.getParcelable(STATE_CLIENT_LIST);
/*
        clientRecyclerView.getLayoutManager().onRestoreInstanceState(state);
*/
        Log.d(Logger.TAG, "ClientList onRestoreInstanceState");
    }

    @Subscribe
    public void onEditClientClickedEvent(EditClientClickedEvent event) {
        CreateClientDialog.newInstance(event.getEditClientName(), CreateClientDialog.EDIT_CLIENT)
                .show(getSupportFragmentManager(), null);
/*
        realm.close();
*/
    }

    @Subscribe
    public void onReturnFromEditorEvent(ReturnFromEditorEvent event) {
/*
        clientListAdapter.notifyDataSetChanged();
*/
        clientListAdapter.updateResults(results);
        Log.d(Logger.TAG, "ClientList onReturnFromEditorEvent");/*
        setupData();
        setUpRecycler();*/
    }

    private RealmConfiguration getRealmConfig() {
        return new RealmConfiguration
                .Builder(this)
                .setModules(Realm.getDefaultModule())
                .name(getResources().getString(R.string.app_name)).build();
    }

    private void resetRealm() {
        Realm.deleteRealm(getRealmConfig());
    }

    @OnClick(R.id.activity_client_list_fab)
    public void newClient() {
        CreateClientDialog.newInstance(null, CreateClientDialog.NEW_CLIENT)
                .show(getSupportFragmentManager(), null);
 /*   realm.close();
*/
        //ttry start activity for result

    }

    @Subscribe
    public void pickContactFromPhone(PickContactFromPhoneEvent event) {
        Log.d(Logger.TAG, "ClientList pickContactFromPhoneEvent");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Logger.TAG, "ClientList onActivityResult " + requestCode);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                retrieveContactInfo(contactUri);
            }
        }
    }

    @Subscribe
    public void clientPhoneClickedEvent(final ClientPhoneClickedEvent event) {
        //open dialog to choose phone or sms
        Log.d(Logger.TAG, "ClientList, clientPhoneClickedEvent() for: " + event.getClientName() + ", " + event.getPhoneNumber());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.freelacer_no_bg));
        builder.setTitle("Phone or Text")
                .setMessage(getString(R.string.dialog_message_phone) + " " + event.getClientName())
                .setPositiveButton("Phone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Open Phone

                        if (ActivityCompat.checkSelfPermission(Freelancer.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            ActivityCompat.requestPermissions(ClientList.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    201);
                        } else {
                            Toast.makeText(Freelancer.getContext(), "Call " + event.getClientName(), Toast.LENGTH_SHORT).show();

                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + event.getPhoneNumber()));
                            startActivity(callIntent);
                        }
                    }
                }).setNegativeButton("Text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Open Sms
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + event.getPhoneNumber()));
                startActivity(sendIntent);
/*
                Intent eventIntentMessage = getPackageManager()
                        .getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(Freelancer.getContext()));

                eventIntentMessage.putExtra("address", event.getPhoneNumber());
                startActivity(eventIntentMessage);



                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", event.getPhoneNumber());
                startActivity(smsIntent);
*/

                Toast.makeText(Freelancer.getContext(), "Text " + event.getClientName(), Toast.LENGTH_SHORT).show();
            }
        }).setNeutralButton(android.R.string.cancel, null).show();
    }


    private void retrieveContactInfo(Uri contactUri) {
        String contactName = null;
        String contactName2 = null;
        String contactEmail = null;
        String contactNumber = null;
        String contactAddress = null;
        String contactNotes = null;

        // getting contacts ID

        Cursor cursorID = getContentResolver().query(contactUri,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(
                    cursorID.getColumnIndex(
                            ContactsContract.Contacts._ID));
        }
        cursorID.close();
        Log.d(Logger.TAG, "Contact ID: " + contactID);

        // get contact phone number

        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(
                    cursorPhone.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        Log.d(Logger.TAG, "Contact Phone Number: " + contactNumber);

        //get contact Nickname

        Cursor cursorNick = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Nickname.NAME},
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + "= ?",
                new String[]{contactID, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE},
                null);
        if (cursorNick.moveToNext()) {
            contactName2 = cursorNick.getString(
                    cursorNick.getColumnIndex(
                            ContactsContract.CommonDataKinds.Nickname.NAME));
        }
        cursorNick.close();
        Log.d(Logger.TAG, "Contact Name 2: " + contactName2);

        //get Notes

        Cursor cursorNotes = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Note.NOTE},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "= ?",
                new String[]{contactID, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE},
                null);
        if (cursorNotes.moveToNext()) {
            contactNotes = cursorNotes.getString(
                    cursorNotes.getColumnIndex(
                            ContactsContract.CommonDataKinds.Note.NOTE));
        }
        cursorNotes.close();
        Log.d(Logger.TAG, "Contact Note: " + contactNotes);

        //get address

        Cursor c = getContentResolver().query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS},
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?",
                new String[]{contactID}, null);
        if (c.moveToNext()) {
            contactAddress = c.getString(
                    c.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
        }
        c.close();
        Log.d(Logger.TAG, "Contact Address: " + contactAddress);

        //get email

        Cursor cursorEmail = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactID}, null);
        if (cursorEmail.moveToNext()) {
            contactEmail = cursorEmail.getString(
                    cursorEmail.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.DATA));
        }
        cursorEmail.close();
        Log.d(Logger.TAG, "Contact Email Address: " + contactEmail);

        // get Name

        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(
                    cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        Log.d(Logger.TAG, "Contact Name: " + contactName);
        BusProvider.Instance.getBus().post(new PhoneContactChosenEvent(contactName,
                contactName2,
                contactEmail,
                contactNumber,
                contactAddress,
                contactNotes));

    }

}
