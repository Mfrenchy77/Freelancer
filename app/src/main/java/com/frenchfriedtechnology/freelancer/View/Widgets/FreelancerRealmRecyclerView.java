package com.frenchfriedtechnology.freelancer.View.Widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.frenchfriedtechnology.freelancer.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Created by matteo on 3/29/16.
 */
public class FreelancerRealmRecyclerView extends RecyclerView {
    /**
     * The View to display when the RecyclerView has no items at all
     */
    private List<View> mNonEmptyViews = Collections.emptyList();
    private List<View> mEmptyViews = Collections.emptyList();
    private AdapterDataObserver mObserver = new AdapterDataObserver() {

        /**
         * Whenever this method is triggered, if the RecyclerView has some items to display, show it and hide the empty view otherwise do the reverse
         */
        @Override
        public void onChanged() {
            hideIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            hideIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            hideIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            hideIfEmpty();
        }
    };



    public FreelancerRealmRecyclerView(Context context) {
        super(context);
    }

    public FreelancerRealmRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreelancerRealmRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void hideIfEmpty() {
        if (getAdapter() != null && !mNonEmptyViews.isEmpty() && !mEmptyViews.isEmpty()) {
            if (getAdapter().getItemCount() == 0) {
                Utils.showViews(mEmptyViews);
                FreelancerRealmRecyclerView.this.setVisibility(View.GONE);
                Utils.hideViews(mNonEmptyViews);


            } else {
                Utils.hideViews(mEmptyViews);
                FreelancerRealmRecyclerView.this.setVisibility(View.VISIBLE);
                Utils.showViews(mNonEmptyViews);
            }
        }
    }


    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            //Register an AdapterDataSetObserver to monitor the number of items in the RecyclerView whenever items are being added, removed
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    public void setViewsToHideWhenEmpty(View... views) {
        mNonEmptyViews = Arrays.asList(views);
    }

    public void setViewsToShowWhenEmpty(View... views) {
        mEmptyViews = Arrays.asList(views);
    }
}