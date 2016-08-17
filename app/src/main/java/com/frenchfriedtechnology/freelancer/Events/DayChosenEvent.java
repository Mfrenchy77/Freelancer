package com.frenchfriedtechnology.freelancer.Events;

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
