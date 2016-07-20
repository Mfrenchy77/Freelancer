package com.frenchfriedtechnology.freelancer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Events.EditLogEvent;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.LogEntry;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by matteo on 3/24/16.
 */
public class TotalsRealmAdapter extends RealmBasedRecyclerViewAdapter<LogEntry, TotalsRealmAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public TotalsRealmAdapter(Context context, RealmResults<LogEntry> realmResults, boolean automaticUpdate,
                              boolean animatedIdType) {
        super(context, realmResults, automaticUpdate, animatedIdType);
    }

    @Override
    public int getItemCount() {
        return realmResults.size() +1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        if (i == TYPE_HEADER) {
            //graph
            View v = inflater.inflate(R.layout.list_item_graph, viewGroup, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
        } else if (i == TYPE_ITEM) {
            View v = inflater.inflate(R.layout.list_item_yearly_total, viewGroup, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
            return vh;
        }
        return null;
    }

    @Override
    public void onBindRealmViewHolder(final ViewHolder holder, final int position) {
        final LogEntry logEntry = realmResults.get(position);
        holder.date.setText(logEntry.getDay());

        holder.cash.setText("Cash: " + logEntry.getCashReceived());
        holder.cash.setVisibility(!logEntry.getCashReceived().equals("") ? View.VISIBLE : View.GONE);

        if (logEntry.getChecksReceived() != null) {
            holder.check.setVisibility(!logEntry.getChecksReceived().equals("") ? View.VISIBLE : View.GONE);
            holder.check.setText("Check: " + logEntry.getChecksReceived());
        }

        holder.expenses.setText("Expenses: " + logEntry.getExpenses());
        holder.expenses.setVisibility(!logEntry.getExpenses().equals("") ? View.VISIBLE : View.GONE);

        holder.miles.setText("Mileage: " + logEntry.getMileage());
        holder.miles.setVisibility(!logEntry.getMileage().equals("") ? View.VISIBLE : View.GONE);

        holder.clients.setText("Clients: \n" + logEntry.getClientsForDay());
        holder.clients.setVisibility(!logEntry.getClientsForDay().equals("") ? View.VISIBLE : View.GONE);

        holder.notes.setText(String.format("Notes: \n%s", logEntry.getNotes()));
        holder.notes.setVisibility(!logEntry.getNotes().equals("") ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.Instance.getBus().post(new EditLogEvent(logEntry.getDay(),position));
            }
        });

    }

    public class ViewHolder extends RealmViewHolder {
        private final TextView date, cash, check, expenses, miles, clients, notes;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.date = (TextView) itemView.findViewById(R.id.item_yearly_date);
            this.cash = (TextView) itemView.findViewById(R.id.item_yearly_money_cash);
            this.check = (TextView) itemView.findViewById(R.id.item_yearly_money_check);
            this.expenses = (TextView) itemView.findViewById(R.id.item_yearly_expenses);
            this.miles = (TextView) itemView.findViewById(R.id.item_yearly_miles);
            this.clients = (TextView) itemView.findViewById(R.id.item_yearly_clients);
            this.notes = (TextView) itemView.findViewById(R.id.item_yearly_notes);
        }
    }
}
