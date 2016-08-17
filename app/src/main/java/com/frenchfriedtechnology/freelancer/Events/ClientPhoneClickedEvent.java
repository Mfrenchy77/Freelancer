package com.frenchfriedtechnology.freelancer.Events;

public class ClientPhoneClickedEvent {
    private String phoneNumber;
    private String clientName;

    public ClientPhoneClickedEvent(String phoneNumber, String clientName) {
        this.phoneNumber = phoneNumber;
        this.clientName = clientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getClientName() {
        return clientName;
    }
}
