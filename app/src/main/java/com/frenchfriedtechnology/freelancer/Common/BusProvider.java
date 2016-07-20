package com.frenchfriedtechnology.freelancer.Common;

/**
 * Created by matteo on 2/4/16.
 */

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
