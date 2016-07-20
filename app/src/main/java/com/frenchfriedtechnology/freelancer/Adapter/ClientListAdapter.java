package com.frenchfriedtechnology.freelancer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Events.ClientPhoneClickedEvent;
import com.frenchfriedtechnology.freelancer.Events.EditClientClickedEvent;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.Client;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by matteo on 2/3/16.
 */

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ClientViewHolder> {
    //    private ArrayList<CLObject> listClient = new ArrayList<>();
    private ArrayList<Client> listClient = new ArrayList<>();

    private Context mContext;
    private Realm mRealm;
    private RealmResults<Client> mResults;
    private LayoutInflater layoutInflater;


    public ClientListAdapter(Context context, Realm realm, RealmResults<Client> results) {
        mContext = context;
        mRealm = realm;
        mResults = results;
    }
    public void updateResults(RealmResults<Client> results) {
        mResults = results;
        notifyDataSetChanged();
    }



    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ClientViewHolder holder, int position) {
        final Client currentClient = mResults.get(position);
        final String name = currentClient.getName();
        holder.clientName.setText(name);

        holder.clientName2.setVisibility(!currentClient.getName2().equals("") ? View.VISIBLE : View.GONE);
        holder.clientName2.setText(currentClient.getName2());


        holder.clientEmail.setVisibility(!currentClient.getEmail().equals("") ? View.VISIBLE : View.GONE);
        holder.clientEmail.setText(currentClient.getEmail());
        holder.clientEmail.setLinksClickable(true);


        holder.clientPhone.setVisibility(!currentClient.getPhone().equals("") ? View.VISIBLE : View.GONE);
        holder.clientPhone.setText(currentClient.getPhone());
        holder.clientPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.Instance.getBus().post(new ClientPhoneClickedEvent(currentClient.getPhone(), currentClient.getName()));
            }
        });

        holder.clientAddress.setVisibility(!currentClient.getAddress().equals("") ? View.VISIBLE : View.GONE);
        holder.clientAddress.setText(currentClient.getAddress());

        holder.clientRate.setVisibility(!currentClient.getRate().equals("") ? View.VISIBLE : View.GONE);
        holder.clientRate.setText(currentClient.getRate());

        holder.clientRecurrence.setVisibility(!currentClient.getRecurrence().equals("") ? View.VISIBLE : View.GONE);
        holder.clientRecurrence.setText(currentClient.getRecurrence());

        holder.clientNotes.setVisibility(!currentClient.getNotes().equals("") ? View.VISIBLE : View.GONE);
        holder.clientNotes.setText(currentClient.getNotes());

        holder.clientEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open edit Dialog
                BusProvider.Instance.getBus().post(new EditClientClickedEvent(name));
            }
        });
    }

    @Override
    public int getItemCount() {
        //number of clients
        return mResults.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        private final TextView clientName, clientName2, clientEmail,
                clientPhone, clientAddress, clientRate, clientRecurrence, clientNotes;
        private final LinearLayout clientEdit;

        public ClientViewHolder(View itemView) {
            super(itemView);
            this.clientEdit = (LinearLayout) itemView.findViewById(R.id.list_item_edit_client_container);
            this.clientName = (TextView) itemView.findViewById(R.id.list_client_name);
            this.clientName2 = (TextView) itemView.findViewById(R.id.list_client_name2);
            this.clientEmail = (TextView) itemView.findViewById(R.id.list_client_email);
            this.clientPhone = (TextView) itemView.findViewById(R.id.list_client_phone);
            this.clientAddress = (TextView) itemView.findViewById(R.id.list_client_address);
            this.clientRate = (TextView) itemView.findViewById(R.id.list_client_rate);
            this.clientRecurrence = (TextView) itemView.findViewById(R.id.list_client_recurrence);
            this.clientNotes = (TextView) itemView.findViewById(R.id.list_client_notes);


        }
    }
}
