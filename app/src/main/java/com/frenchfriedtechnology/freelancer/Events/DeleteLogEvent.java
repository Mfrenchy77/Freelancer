package com.frenchfriedtechnology.freelancer.Events;

/**
 * Created by matteo on 3/29/16.
 */
public class DeleteLogEvent {
    private String logDate;
    private int position;

    public DeleteLogEvent(String logDate, int position) {
        this.logDate = logDate;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getLogDate() {
        return logDate;
    }
}
