package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;

import javax.faces.event.ActionEvent;

public class Setup {
    public Setup() {
    }

    public void save_action(ActionEvent actionEvent) {
        // Add event code here...
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Data has been saved");
    }

    public void cancel_action(ActionEvent actionEvent) {
        // Add event code here...
        ADFUtils.findOperation("Rollback").execute();
        JSFUtils.addFacesInformationMessage("Data has been canceled");
    }
}
