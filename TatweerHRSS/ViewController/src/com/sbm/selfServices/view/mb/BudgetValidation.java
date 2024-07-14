package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;

import com.view.beans.filmStripApp.FilmStripBean;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

public class BudgetValidation {

    private RichTable t1;

    public void save_action(ActionEvent actionEvent) {
        // Add event code here...
        ViewObject vo=ADFUtils.findIterator("BudgetValidationStepsVOIterator").getViewObject();
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                         "Data Saved Successfully!");
               vo.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(t1);
        ExtendedRenderKitService erkService =
            Service.getService(FacesContext.getCurrentInstance().getRenderKit(),
                               ExtendedRenderKitService.class);
        erkService.addScript(FacesContext.getCurrentInstance(),
                             "var hints = {autodismissNever:true}; " +
                             "AdfPage.PAGE.findComponent('p1').show(hints);");
              
           }

    public void cancel_action(ActionEvent actionEvent) {
        // Add event code here...
        ViewObject vo=ADFUtils.findIterator("BudgetValidationStepsVOIterator").getViewObject();
        ADFUtils.findOperation("Rollback").execute();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                         "Data Rolled back Successfully!");
        vo.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(t1);
        ExtendedRenderKitService erkService =
            Service.getService(FacesContext.getCurrentInstance().getRenderKit(),
                               ExtendedRenderKitService.class);
        erkService.addScript(FacesContext.getCurrentInstance(),
                             "var hints = {autodismissNever:true}; " +
                             "AdfPage.PAGE.findComponent('p1').show(hints);");
       
       
    }

    public void setT1(RichTable t1) {
        this.t1 = t1;
    }

    public RichTable getT1() {
        return t1;
    }
}
