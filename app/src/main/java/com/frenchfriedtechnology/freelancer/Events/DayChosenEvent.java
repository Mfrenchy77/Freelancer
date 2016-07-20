package com.frenchfriedtechnology.freelancer.Events;

import java.security.PublicKey;

/**
 * Created by matteo on 2/4/16.
 */
public class DayChosenEvent extends BaseUiEvent {

    private final String text;
    private final String tag;

    public DayChosenEvent(String text, String tag) {
        this.text = text;
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }
}
