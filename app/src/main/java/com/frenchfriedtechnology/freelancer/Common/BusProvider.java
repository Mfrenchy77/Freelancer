package com.frenchfriedtechnology.freelancer.Common;

import com.squareup.otto.Bus;

/**
 * Provides a single instance of an Event Bus which is used to relay messages throughout the app
 */
public enum BusProvider {

    Instance();

    private final Bus bus;

    BusProvider() {
        bus = new Bus();

    }

    /**
     * @return the Otto Event Bus instance
     */
    public Bus getBus() {
        return bus;
    }

}
