package com.frenchfriedtechnology.freelancer.Events;

/**
 * Created by matteo on 3/24/16.
 */
public class EditLogEvent {
    private String logDayToEdit;
    private int position;

    public EditLogEvent(String logDayToEdit, int position) {
        this.logDayToEdit = logDayToEdit;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getLogDayToEdit() {
        return logDayToEdit;
    }
}
