package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.model.views.up.RequestVisibleSetupVOImpl;

import com.view.uiutils.ADFUtils;
import com.view.uiutils.JSFUtils;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;

public class RequestVisibleSetupBB {
    public RequestVisibleSetupBB() {
        super();
    }

    public void onClickSave(ActionEvent actionEvent) {
        ADFUtils.findOperation("Commit").execute();
        ViewObject vo =
            ADFUtils.findIterator("RequestVisible_VOIterator").getViewObject();
        vo.executeQuery();

        ViewObject rovo =
            ADFUtils.findIterator("RequestVisibleROVOIterator").getViewObject();
        rovo.executeQuery();
        this.refresh();

    }

    public void onClickRevert(ActionEvent actionEvent) {
        ADFUtils.findOperation("Rollback").execute();
        ViewObject vo =
            ADFUtils.findIterator("RequestVisible_VOIterator").getViewObject();
        vo.executeQuery();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                    "Changes Reverted !!");
        RichPopup msgPop = (RichPopup)JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
    }

    public void refresh() {
        FacesContext context = FacesContext.getCurrentInstance();
        String currentView = context.getViewRoot().getViewId();
        ViewHandler vh = context.getApplication().getViewHandler();
        UIViewRoot x = vh.createView(context, currentView);
        x.setViewId(currentView);
        context.setViewRoot(x);
    }

    public void onClickSaveNew(ActionEvent actionEvent) {

        ADFUtils.findOperation("Commit").execute();
        ViewObject vo =
            ADFUtils.findIterator("RequestVisibleSetupVO1Iterator").getViewObject();
        vo.executeQuery();

        JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                    "Changes Saved !!");
        RichPopup msgPop = (RichPopup)JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
        ViewObject rovo =
            ADFUtils.findIterator("RequestVisibleROVOIterator").getViewObject();
        rovo.executeQuery();
        this.refresh();

    }

    public void onClickRevertNew(ActionEvent actionEvent) {
        ADFUtils.findOperation("Rollback").execute();
        ViewObject vo =
            ADFUtils.findIterator("RequestVisibleSetupVO1Iterator").getViewObject();
        vo.executeQuery();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                    "Changes Reverted !!");
        RichPopup msgPop = (RichPopup)JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
    }

    public void onImport(ActionEvent actionEvent) {
        ViewObject vo =
            ADFUtils.findIterator("RequestVisibleSetupVO1Iterator").getViewObject();
        ViewObject reqVo =
            ADFUtils.findIterator("RequestVisible_VOIterator").getViewObject();
        String locationId = "";
        String locationName = "";
        long count = vo.getEstimatedRowCount();

        if (vo.getNamedWhereClauseParam("BV_LOC_ID") != null) {
            locationId = vo.getNamedWhereClauseParam("BV_LOC_ID").toString();
            locationName = this.getlocationNameById(locationId);
            System.err.println("count--" + count);
            System.err.println("BV_LOC_ID : " + locationId);
            System.err.println("locationName : " + locationName);
            
            if (count == 0 && !"".equals(locationName)) {
                RowSetIterator reqItr = reqVo.createRowSetIterator(null);
                while (reqItr.hasNext()) {
                    System.err.println("**Loop");
                    Row r = reqItr.next();
                    Row createRow = vo.createRow();
                    createRow.setAttribute("RequestTypeId",
                                           r.getAttribute("RequestTypeId"));
                    createRow.setAttribute("LocationsId", locationId);
                    createRow.setAttribute("Location", locationName);
                    createRow.setAttribute("ReqVisibleFlag", "Y");
                    vo.insertRow(createRow);
                }
                ADFUtils.findOperation("Commit").execute();
                vo.executeQuery();
            }else{
                JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                            " Request details already ampped for selected location !!");
                RichPopup msgPop = (RichPopup)JSFUtils.findComponentInRoot("p1");
                JSFUtils.showPopup(msgPop);
            }
        } else {
            JSFUtils.setExpressionValue("#{pageFlowScope.message}",
                                        " Please select location !!");
            RichPopup msgPop = (RichPopup)JSFUtils.findComponentInRoot("p1");
            JSFUtils.showPopup(msgPop);
        }
    }

    private String getlocationNameById(String locationId) {
        ViewObject locVo = ADFUtils.findIterator("LocationsVORefIterator").getViewObject();
        ViewCriteria vc = locVo.createViewCriteria();
        ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
        vcRow.setAttribute("LocationsId", locationId);
        vc.addRow(vcRow);
        locVo.applyViewCriteria(vc);
        locVo.executeQuery();
        if (locVo.first() != null){
            Row r = locVo.first();
            String locName = r.getAttribute("LocationsName")!=null ? r.getAttribute("LocationsName").toString() : "";
            return locName;
        }
        locVo.applyViewCriteria(null);
        locVo.executeQuery();
        return "";
        }
}
