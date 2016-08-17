package com.frenchfriedtechnology.freelancer;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * LinearLayoutManager to Smooth scrolling
 */
public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

    private static final float MILLISECONDS_PER_INCH = 60f;

    public LinearLayoutManagerWithSmoothScroller(Context context) {
        super(context, VERTICAL, false);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(Freelancer.getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START;
                    }

                    //This controls the direction in which smoothScroll looks
                    //for your view
                    @Override
                    public PointF computeScrollVectorForPosition
                    (int targetPosition) {
                        return LinearLayoutManagerWithSmoothScroller.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    //This returns the milliseconds it takes to
                    //scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel
                    (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}