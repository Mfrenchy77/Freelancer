package com.frenchfriedtechnology.freelancer.View.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;

import com.frenchfriedtechnology.freelancer.Adapter.AdapterTotals;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Events.DeleteLogEvent;
import com.frenchfriedtechnology.freelancer.Events.EditLogEvent;
import com.frenchfriedtechnology.freelancer.Events.LogUpdatedEvent;
import com.frenchfriedtechnology.freelancer.LinearLayoutManagerWithSmoothScroller;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.LogEntry;
import com.frenchfriedtechnology.freelancer.View.Dialog.EditLogDialog;
import com.frenchfriedtechnology.freelancer.View.Widgets.FreelancerRealmRecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by matteo on 3/24/16.
 */
public class YearlyTotalActivity extends BaseActivity {
    private BarChart mChart;
    private LinearLayoutManagerWithSmoothScroller llManager;
    private Realm realm;
    private RealmConfiguration realmConfig;
    private RealmResults<LogEntry> results;
    private AdapterTotals mAdapterTotals;
    @Bind(R.id.root)
    CoordinatorLayout root;
    @Bind(R.id.activity_yearly_recycler_view)
    FreelancerRealmRecyclerView recyclerView;
    @Bind(R.id.fab_up)
    FloatingActionButton fabUp;
    @Bind(R.id.fab_down)
    FloatingActionButton fabDown;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_yearly_total;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpData();
        setUpRecycler();
/*
        setUpBarChart();
*/
    }

    // fixme this is being created in the Adapter Erase this when ready
    private void setUpBarChart() {
        mChart = (BarChart) findViewById(R.id.header_bar_chart);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setLogEnabled(true);

        mChart.setDescription("");
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(6, false);
        leftAxis.setAxisMinValue(-2.5f);
        leftAxis.setAxisMaxValue(2.5f);

/*
        mChart = new BarChart(this);
*/
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<String> daysEntered = new ArrayList<>();
        int daysIndex = 0;
        int maxDay = 366;
        int index = 0;
        String format = "MM/dd/yy";
        String date = "01/01/2016";//// FIXME: 3/29/16 change this to dynamically set the beginning of the year
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        DateFormat fmtDateAndTime = DateFormat.getDateInstance(DateFormat.SHORT);
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < results.size(); i++) {
            daysEntered.add(results.get(i).getDay());
        }// FIXME: 4/9/16 Error in the date somewhere near here
        for (int co = 0; co < maxDay; co++) {
            if (daysEntered.contains(sdf.format(c.getTime()))) {
                float cash = Float.parseFloat(results.get(daysIndex).getCashReceived());
                float check = Float.parseFloat(results.get(daysIndex).getChecksReceived());
                float totalMonies = cash + check;
                String formattedDate = sdf.format(c.getTime());
                Log.d(Logger.TAG, "BarEntry Index = " + index);
                Log.d(Logger.TAG, "Result Index = " + daysIndex);
                labels.add(index, formattedDate);
                daysIndex++;
                index++;

                Log.d(Logger.TAG, "Total Monies: " + totalMonies);
                entries.add(new BarEntry(totalMonies, index, formattedDate));
            } else {
                entries.add(new BarEntry(0, index, sdf.format(c.getTime())));

                Log.d(Logger.TAG, "Empty Index = " + index);
            }

            Log.d(Logger.TAG, "Day = " + sdf.format(c.getTime()));
            c.add(Calendar.DATE, 1);
        }
        Log.d(Logger.TAG, "Entries: " + entries);
        Log.d(Logger.TAG, "Labels: " + labels);
      /*
        dataSet.setBarSpacePercent(40f);
        dataSet.setColor(Color.rgb(240, 120, 124));

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        mChart = new BarChart(this);
        mChart.setMaxVisibleValueCount(30);
        mChart.setPinchZoom(false);
        mChart.setScaleEnabled(true);
        mChart.setDoubleTapToZoomEnabled(true);

        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(android.R.color.primary_text_light);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);*/
        BarDataSet dataSet = new BarDataSet(entries, "Date");
        BarData data = new BarData(labels, dataSet);

        mChart.setData(data);
        mChart.setDescription("Monies per Day");
        mChart.invalidate();
    }

    private void setUpData() {
        realmConfig = new RealmConfiguration.Builder(this)
                .name(getResources()
                        .getString(R.string.app_name))
                .build();
        realm = Realm.getInstance(realmConfig);
        results = realm.where(LogEntry.class).findAll();
        results.sort("day", Sort.ASCENDING);

    }

    private void setUpRecycler() {
        llManager = new LinearLayoutManagerWithSmoothScroller(this);
        recyclerView.setLayoutManager(llManager);
//        TotalsRealmAdapter totalsRealmAdapter = new TotalsRealmAdapter(this, results, true, true);
        mAdapterTotals = new AdapterTotals(this, realm, results);
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapterTotals.updateResults(results);
                results.removeChangeListener(this);
            }
        });
        recyclerView.setAdapter(mAdapterTotals);

    }

    @OnClick(R.id.fab_up)
    void onClickFabUp() {
        int position = llManager.findFirstVisibleItemPosition();
        if (position > 0) {
            onScrollRequest(position - 1);
        }
    }
    @OnLongClick(R.id.fab_up)
    boolean longClickUp() {
        fabUp.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        int position = llManager.findFirstVisibleItemPosition();
        if (position>0) {
            if (mAdapterTotals.getItemCount()>5){
                llManager.scrollToPosition(5);
            }
            onScrollRequest(0);
        }
        return true;
    }
    @OnClick(R.id.fab_down)
    void onClickFabDown() {
        int position = llManager.findFirstVisibleItemPosition();
        if (position != mAdapterTotals.getItemCount()) {
            onScrollRequest(position + 1);
        }
    }

    @OnLongClick(R.id.fab_down)
    boolean longClickedDown() {
        fabDown.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        int position = llManager.findFirstVisibleItemPosition();
        if (position != mAdapterTotals.getItemCount()) {
            if (mAdapterTotals.getItemCount()>5) {
                llManager.scrollToPosition(mAdapterTotals.getItemCount() - 5);
            }
            onScrollRequest(mAdapterTotals.getItemCount());
        }
        return true;
    }

    private void onScrollRequest(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }

    @Subscribe
    public void onEditLogEvent(EditLogEvent event) {
        //Open dialog to edit Log Entry
        EditLogDialog.newInstance(event.getLogDayToEdit(), event.getPosition()).show(getSupportFragmentManager(), null);

    }

    @Subscribe
    public void onLogUpdatedEvent(LogUpdatedEvent event) {
        Log.d(Logger.TAG, "YearlyTotalActivity, onLogUpdatedEvent");
        mAdapterTotals.updateResults(results);
    }

    @Subscribe
    public void onLogDeletedEvent(DeleteLogEvent event) {
        mAdapterTotals.notifyItemRemoved(event.getPosition());
    }
}
