package com.frenchfriedtechnology.freelancer.Events;

import android.support.annotation.Nullable;

/**
 * Created by matteo on 2/4/16.
 */
public class NamesChosenEvent extends BaseUiEvent {
    private final String names;
    private final String rate;

    public NamesChosenEvent(String names, String rate) {
        this.names = names;
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public String getNames() {
        return names;
    }
}
