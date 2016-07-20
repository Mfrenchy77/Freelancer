package com.frenchfriedtechnology.freelancer.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by matteo on 3/10/16.
 */
public class LogEntry extends RealmObject {

    @PrimaryKey
    private String day;
    private String clientsForDay;
    private String cashReceived;
    private String checksReceived;
    private String mileage;
    private String expenses;
    private String notes;

    public LogEntry() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getClientsForDay() {
        return clientsForDay;
    }

    public void setClientsForDay(String clientsForDay) {
        this.clientsForDay = clientsForDay;
    }

    public String getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(String cashReceived) {
        this.cashReceived = cashReceived;
    }

    public String getChecksReceived() {
        return checksReceived;
    }

    public void setChecksReceived(String checksReceived) {
        this.checksReceived = checksReceived;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
