package com.frenchfriedtechnology.freelancer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Events.EditClientClickedEvent;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.Client;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by matteo on 3/11/16.
 */

public class ClientListRealmAdapter extends RealmBasedRecyclerViewAdapter<Client,
        ClientListRealmAdapter.ViewHolder> {

    public ClientListRealmAdapter(
            Context context,
            RealmResults<Client> realmResults,
            boolean automaticUpdate,
            boolean animateIdType) {
        super(context, realmResults, automaticUpdate, animateIdType);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View v =inflater.inflate(R.layout.list_item_client,viewGroup,false);
        ViewHolder vh = new ViewHolder((LinearLayout)v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder holder, int position) {
final Client currentClient = realmResults.get(position);

        final String name = currentClient.getName();
        holder.clientName.setText(name);

        holder.clientName2.setVisibility(!currentClient.getName2().equals("") ? View.VISIBLE : View.GONE);
        holder.clientName2.setText(currentClient.getName2());


        holder.clientEmail.setVisibility(!currentClient.getEmail().equals("") ? View.VISIBLE : View.GONE);
        holder.clientEmail.setText(currentClient.getEmail());
        holder.clientEmail.setLinksClickable(true);


        holder.clientPhone.setVisibility(!currentClient.getPhone().equals("") ? View.VISIBLE : View.GONE);
        holder.clientPhone.setText(currentClient.getPhone());

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
        });    }

    public class ViewHolder extends RealmViewHolder {
        private final TextView clientName, clientName2, clientEmail,
                clientPhone, clientAddress, clientRate, clientRecurrence, clientNotes;
        private final LinearLayout clientEdit;

        public ViewHolder(LinearLayout container) {
            super(container);
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

