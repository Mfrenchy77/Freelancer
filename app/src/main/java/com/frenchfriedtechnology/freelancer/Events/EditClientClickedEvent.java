package com.frenchfriedtechnology.freelancer.Events;

public class EditClientClickedEvent extends BaseUiEvent {
    private String editClientName;

    public EditClientClickedEvent(String editClientName) {
        this.editClientName = editClientName;
    }

    public String getEditClientName() {
        return editClientName;
    }

}
