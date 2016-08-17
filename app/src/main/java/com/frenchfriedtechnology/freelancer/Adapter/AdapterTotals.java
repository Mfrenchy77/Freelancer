package com.frenchfriedtechnology.freelancer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Events.EditLogEvent;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.LogEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.realm.implementation.RealmBarData;
import com.github.mikephil.charting.data.realm.implementation.RealmBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * RecyclerView Adapter for listing all the Log Updates and a Chart to show Money earner per day
 */
public class AdapterTotals extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int NO_ITEM = 2;
    public static final int FOOTER = 3;
    private LayoutInflater mInflater;
    private Context mContext;
    private Realm mRealm;
    private RealmResults<LogEntry> mResults;
    private int mSort;

    public AdapterTotals(Context context, Realm realm, RealmResults<LogEntry> results) {
        mContext = context;
        mRealm = realm;
        mResults = results;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private LogEntry getItem(int position) {
        return mResults.get(position);
    }

    public void updateResults(RealmResults<LogEntry> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mResults.size() + 1;

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return HEADER;
        }
        return ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            //graph
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_graph, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_yearly_total, parent, false);
            return new ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
//setup Line Chart
//            holder.lineChart.setData();

            //setup BarChart
            holder.barChart.setDrawBarShadow(false);
            holder.barChart.setDrawValueAboveBar(true);
/*
            holder.barChart.setLogEnabled(true);
*/
            holder.barChart.setPinchZoom(true);
            holder.barChart.setDrawBorders(false);
            holder.barChart.setDragEnabled(true);
            XAxis xAxis = holder.barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelRotationAngle(45f);
            xAxis.setEnabled(true);


            YAxis leftAxis = holder.barChart.getAxisLeft();
  /*         leftAxis.setSpaceBottom(5f);

            xAxis.setAxisLineColor(R.color.colorAccent);
            holder.barChart.setDescription("");

            leftAxis.setEnabled(true);
            leftAxis.setAxisLineColor(R.color.colorAccent);

          leftAxis.setLabelCount(6, false);
            leftAxis.setAxisMinValue(-2.5f);
            leftAxis.setAxisMaxValue(2.5f);
*/
            //Logic for iterating the year and adding the Entered dates
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();
            ArrayList<String> daysEntered = new ArrayList<>();

            int daysIndex = 0;
            int maxDay = 366;
            int index = 0;

            Calendar c = Calendar.getInstance();
            String format = "MM/dd/yy";
            String date = "01/01/" + c.get(Calendar.YEAR);
            Log.d(Logger.TAG, "Beginning of Year: " + date);
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
/*
c.set(Calendar.YEAR,Calendar.JANUARY,01);
*/
            //sets date to Jan 1st in appropriate format
            try {
                c.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //enters Realm results day into list
            for (int i = 0; i < mResults.size(); i++) {
                daysEntered.add(mResults.get(i).getDay());
            }

            //while co is less than max day
            for (int co = 0; co < maxDay; co++) {
                String formattedDate = sdf.format(c.getTime());
                if (daysEntered.contains(sdf.format(c.getTime()))) {

                    //Realm list has a day that matches current iteration
                    float cash = Float.parseFloat(mResults.get(daysIndex).getCashReceived());
                    float check = Float.parseFloat(mResults.get(daysIndex).getChecksReceived());
                    float totalMonies = cash + check;
                    Log.d(Logger.TAG, "BarEntry Index = " + index);
                    Log.d(Logger.TAG, "Result Index = " + daysIndex);
                    Log.d(Logger.TAG, "formattedDate: " + formattedDate);
                    labels.add(index, formattedDate);
                    Log.d(Logger.TAG, "Total Monies: " + totalMonies);
                    entries.add(new BarEntry(totalMonies, index, formattedDate));
                    daysIndex++;
                } else {
                  /* Log.d(Logger.TAG, "Empty Index = " + index);*/
                }
                //add empty day and move index up and date up
                labels.add(index, formattedDate);
                index++;
                c.add(Calendar.DATE, 1);
            }
            BarDataSet dataSet = new BarDataSet(entries, "Total Earned per Day");
            BarData data = new BarData(labels, dataSet);

            holder.barChart.setData(data);
            holder.barChart.setDescription("Monies per Day");
            holder.barChart.invalidate();

        } else if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            final LogEntry logEntry = getItem(position - 1);
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
                    BusProvider.Instance.getBus().post(new EditLogEvent(logEntry.getDay(), position));
                }
            });

        }
    }

    public class HeaderViewHolder extends RealmViewHolder {
        private BarChart barChart;
        private LineChart lineChart;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.barChart = (BarChart) itemView.findViewById(R.id.header_bar_chart);
/*
            this.lineChart = (LineChart) itemView.findViewById(R.id.header_line_chart);
*/
        }
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
