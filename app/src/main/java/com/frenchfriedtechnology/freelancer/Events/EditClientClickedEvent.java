package com.frenchfriedtechnology.freelancer.Events;

/**
 * Created by matteo on 3/10/16.
 */
public class EditClientClickedEvent extends BaseUiEvent {
    private String editClientName;

    public EditClientClickedEvent(String editClientName) {
        this.editClientName = editClientName;
    }

    public String getEditClientName() {
        return editClientName;
    }

}
