package com.sbm.selfServices.view.mb;

import com.view.beans.filmStripApp.FilmStripPhaseListener;
import com.view.uiutils.ADFUtils;
import com.view.uiutils.JSFUtils;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

public class EmailNotificationSetupBB {
    public EmailNotificationSetupBB() {
        super();
    }
    
    public void onClickSave(ActionEvent actionEvent) {
        ADFUtils.findOperation("Commit").execute();
        ViewObject vo=ADFUtils.findIterator("RequestTypesEmailNotifyVOIterator").getViewObject();
        vo.executeQuery();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}", "Changes Saved Successfully !!");
        RichPopup msgPop = (RichPopup) JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
        FilmStripPhaseListener.updateWithdrawAndEdit(vo);
        
    }    
    
    public void onClickRevert(ActionEvent actionEvent) {
        ADFUtils.findOperation("Rollback").execute();
        ViewObject vo=ADFUtils.findIterator("RequestTypesEmailNotifyVOIterator").getViewObject();
        vo.executeQuery();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}", "Changes Reverted !!");
        RichPopup msgPop = (RichPopup) JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
    }
}
