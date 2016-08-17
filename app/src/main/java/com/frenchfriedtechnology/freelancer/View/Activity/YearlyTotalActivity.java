package com.frenchfriedtechnology.freelancer.View.Activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.squareup.otto.Subscribe;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Activity displaying all of the Daily Log entries with a graph on top
 */
public class YearlyTotalActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_yearly_total;
    }

    @Bind(R.id.root)
    CoordinatorLayout root;
    @Bind(R.id.activity_yearly_recycler_view)
    FreelancerRealmRecyclerView recyclerView;
    @Bind(R.id.fab_down)
    FloatingActionButton fabDown;
    @Bind(R.id.fab_up)
    FloatingActionButton fabUp;

    private LinearLayoutManagerWithSmoothScroller llManager;
    private RealmResults<LogEntry> results;
    private AdapterTotals mAdapterTotals;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpData();
        setUpRecycler();
    }


    private void setUpData() {
        // get all Daily Log entries and sort

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .name(getResources()
                        .getString(R.string.app_name))
                .build();
        realm = Realm.getInstance(realmConfig);
        results = realm.where(LogEntry.class).findAll();
        results.sort("day", Sort.ASCENDING);
    }

    private void setUpRecycler() {

        llManager = new LinearLayoutManagerWithSmoothScroller(this);
        mAdapterTotals = new AdapterTotals(this, realm, results);
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {

                mAdapterTotals.updateResults(results);
                results.removeChangeListener(this);
            }
        });

        recyclerView.setLayoutManager(llManager);
        recyclerView.setAdapter(mAdapterTotals);

    }

    /**
     * Scroll the list up one entry or all the way to the top with long click
     */
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
        if (position > 0) {
            if (mAdapterTotals.getItemCount() > 5) {
                llManager.scrollToPosition(5);
            }
            onScrollRequest(0);
        }
        return true;
    }

    /**
     * Scroll the List Down one entry or all the way to the bottom with long click
     */
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
            if (mAdapterTotals.getItemCount() > 5) {
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
