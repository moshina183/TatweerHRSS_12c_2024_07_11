package com.sbm.selfServices.view.mb;

import com.view.uiutils.ADFUtils;
import com.view.uiutils.JSFUtils;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class ApproverSetupCEO {
    public ApproverSetupCEO() {
        super();
    }
    
    public void onClickSave(ActionEvent actionEvent) {
        ADFUtils.findOperation("Commit").execute();
        ViewObject vo=ADFUtils.findIterator("ApproverStepsCEO_VO1Iterator").getViewObject();
        vo.executeQuery();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}", "Changes Saved !!");
        RichPopup msgPop = (RichPopup) JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
        
    }    
    
    public void onClickRevert(ActionEvent actionEvent) {
        ADFUtils.findOperation("Rollback").execute();
        ViewObject vo=ADFUtils.findIterator("ApproverStepsCEO_VO1Iterator").getViewObject();
        vo.executeQuery();
        JSFUtils.setExpressionValue("#{pageFlowScope.message}", "Changes Reverted !!");
        RichPopup msgPop = (RichPopup) JSFUtils.findComponentInRoot("p1");
        JSFUtils.showPopup(msgPop);
    }

    public void onChangeStepType(ValueChangeEvent vce) {
        vce.getComponent().processUpdates(FacesContext.getCurrentInstance());
        ViewObject vo = ADFUtils.findIterator("ApproverStepsCEO_VO1Iterator").getViewObject();
        Row r = vo.getCurrentRow();
        String stepType = r.getAttribute("StepType")!=null?r.getAttribute("StepType").toString():"POSITION";
        if(vce.getNewValue() != null){
            if("LINE_MANAGER".equals(stepType)){
                r.setAttribute("StepName", "Line Manager");
            }
            if("DEPT_MANAGER".equals(stepType)){
                r.setAttribute("StepName", "Department Manager");
            }
            if("COST_CENTER_MANAGER".equals(stepType)){
                r.setAttribute("StepName", "Cost Center Manager");
            }
            if("POSITION".equals(stepType) || "USER".equals(stepType)){
                r.setAttribute("StepName", "");
            }
        } 
    }

    public void onCreateStep(ActionEvent actionEvent) {
        ViewObject vo = ADFUtils.findIterator("ApproverStepsCEO_VO1Iterator").getViewObject();
        long count = vo.getEstimatedRowCount();
        long nxtStep = count + 1; 
        long finished = 0;
        if(vo.first() != null){
            RowSetIterator itr = vo.createRowSetIterator(null);  
            while (itr.hasNext()){
                Row r = itr.next();
                if(r.getAttribute("StepName")!=null && "Finished".equals(r.getAttribute("StepName"))){
                    finished = Long.parseLong(r.getAttribute("StepId").toString()); 
                    r.setAttribute("StepId", finished+1); 
                }
            }   
            Row createRow = vo.createRow();
            createRow.setAttribute("RequestTypeId", vo.first().getAttribute("RequestTypeId"));
            createRow.setAttribute("StepId", count);
            createRow.setAttribute("NextStepId", nxtStep);
            vo.insertRow(createRow); 
            
//            ADFUtils.findOperation("Commit").execute();
//            vo.executeQuery();
//            JSFUtils.setExpressionValue("#{pageFlowScope.message}", "New step created and saved!!");
//            RichPopup msgPop = (RichPopup) JSFUtils.findComponentInRoot("p1");
//            JSFUtils.showPopup(msgPop);
        
        
            
        }
    }
    
    public void onChangeIds(ValueChangeEvent vce) {
        vce.getComponent().processUpdates(FacesContext.getCurrentInstance());
    }

    public void onCheckSpecialEdit(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if(valueChangeEvent.getNewValue() != null){
            System.out.println("valueChangeEvent.getNewValue().toString()"+valueChangeEvent.getNewValue().toString());
            ADFUtils.setBoundAttributeValue("SpecialEdit", valueChangeEvent.getNewValue().toString());
        }
    }
}

