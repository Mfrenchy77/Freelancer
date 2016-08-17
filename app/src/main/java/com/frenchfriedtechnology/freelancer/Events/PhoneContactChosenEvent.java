package com.frenchfriedtechnology.freelancer.Events;


/**
 * Created by matteo on 3/3/16.
 */
public class PhoneContactChosenEvent extends BaseUiEvent {
    private final String contactName;
    private final String contactName2;
    private final String contactEmail;
    private final String contactPhone;
    private final String contactAddress;
    private final String contactNotes;

    public PhoneContactChosenEvent(String contactName,
                                   String contactName2,
                                   String contactEmail,
                                   String contactPhone,
                                   String contactAddress,
                                   String contactNotes) {
        this.contactName = contactName;
        this.contactName2 = contactName2;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.contactAddress = contactAddress;
        this.contactNotes = contactNotes;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactName2() {
        return contactName2;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public String getContactNotes() {
        return contactNotes;
    }
}
