package com.frenchfriedtechnology.freelancer.Events;

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
