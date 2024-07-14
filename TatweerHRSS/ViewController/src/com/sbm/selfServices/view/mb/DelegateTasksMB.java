package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.sbm.selfServices.model.views.up.LoanSettlement_VORowImpl;
import com.sbm.selfServices.model.views.up.LocationsVORowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;

import com.view.uiutils.JSFUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

public class DelegateTasksMB {
    private RichPopup delegatePop;

    public DelegateTasksMB() {
        super();
    }

    public void onClickDelegate(ActionEvent actionEvent) throws Exception {
        
        ViewObject formVo = ADFUtils.findIterator("DelegateTaskForm_ROVOIterator").getViewObject();
        Row rw = formVo.getCurrentRow();
        String personNo = rw.getAttribute("PersonNumber")!=null?rw.getAttribute("PersonNumber").toString():"";
        String personName = rw.getAttribute("PersonName")!=null?rw.getAttribute("PersonName").toString():"";
        String personEmail = rw.getAttribute("PersonEmail")!=null?rw.getAttribute("PersonEmail").toString():"";
        String action = rw.getAttribute("Action")!=null?rw.getAttribute("Action").toString():"";
//        System.err.println("Selected values..:"+ personNo + " - "+personName+ " - "+action);   
        
        ViewObject vo = ADFUtils.findIterator("WaitingTasksToDelegate_ROVOIterator").getViewObject();
        RowSetIterator rs = vo.createRowSetIterator(null);
        while(rs.hasNext()){
            Row r = rs.next();
            if(r.getAttribute("SelectFlag")!=null && "true".equals(r.getAttribute("SelectFlag").toString())){
                String reqID = r.getAttribute("RequestId").toString();
                String reqName = r.getAttribute("RequestName").toString();
                String reqStatus = r.getAttribute("RequestStatus").toString();
                String stepId = r.getAttribute("StepId").toString();
                
                String returnVal = this.callDelegateProcess(reqID, stepId, reqName, personName, personNo, action, reqStatus);                
//                System.err.println("Return value is..."+ returnVal);
            }
        }
        this.callDelegateFYIEmail(personName, personEmail,action);
        
        if("Reassign".equals(action)){
            JSFUtils.addFacesInformationMessage("Selected Requests are Reassigned Sucessfully !");
        }else{
            JSFUtils.addFacesInformationMessage("Selected Requests are Delegated Sucessfully !");
        }
        vo.executeQuery();
        formVo.executeQuery();
    }
    
    public void updateAllPerson(ActionEvent actionEvent) throws Exception {
        BiReportAccess bi = new BiReportAccess();
        List<Map> allPersonDetails = bi.getAllPersonDetails();
        System.err.println("AllPersonDetails : "+allPersonDetails);
        
        try{
              
            ViewObject vo = ADFUtils.getApplicationModuleForDataControl("SBMModuleDataControl").findViewObject("AllPersons_VO");
            RowSetIterator rsItr = vo.createRowSetIterator("persons");
            while (rsItr.hasNext()) {
                rsItr.next().remove();
            }
            com.view.uiutils.ADFUtils.findOperation("Commit").execute();
                           
                    for (Map current : allPersonDetails){                         
                        String personId = current.get("PERSON_NUMBER") !=null ? current.get("PERSON_NUMBER").toString() : "0";
                        String personName = current.get("NAME") !=null ? current.get("NAME").toString() : "";
                        String email = current.get("USERNAME") !=null ? current.get("USERNAME").toString() : "";
                        
                        Row newRow = vo.createRow();
                        newRow.setAttribute("PersonNumber", personId);
                        newRow.setAttribute("PersonName", personName);
                        newRow.setAttribute("PersonEmail", email); 
                        
                        vo.insertRow(newRow);
                        com.view.uiutils.ADFUtils.findOperation("Commit").execute();
                    }
                    vo.executeQuery(); 
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    private String callDelegateProcess(String req_id, String step_id, String req_name, String assigneeName, String assignee, String action, String aprType) {
        
        String actionType = "Delegate".equals(action) ? "DELEGATE" : "Reassign".equals(action) ? "REASSIGN" : "DELEGATE";
        String approvalType = "PENDING".equals(aprType) ? "Approval Request" : "Waiting Withdraw Approval".equals(aprType) ? "Withdraw Request" : "Approval Request";
        
        OperationBinding op =
        ADFUtils.findOperation("callDelegateProcess");  
        String returnValue=null;
        op.getParamsMap().put("req_id", req_id);
        op.getParamsMap().put("step_id", step_id);
        op.getParamsMap().put("req_name", req_name);        
        op.getParamsMap().put("assigneeName", assigneeName);
        op.getParamsMap().put("assignee", assignee);
        op.getParamsMap().put("actionType", actionType);
        op.getParamsMap().put("approvalType", approvalType);
        System.err.println("Call-Delegate-Process-Param : "+ op.getParamsMap());
        
        returnValue = (String)op.execute();
        return returnValue;
    }

    public void onDelegate(ActionEvent actionEvent) {
        int count = 0;
        ViewObject vo = ADFUtils.findIterator("WaitingTasksToDelegate_ROVOIterator").getViewObject();
        RowSetIterator rs = vo.createRowSetIterator(null);
        while(rs.hasNext()){
            Row r = rs.next();
            if(r.getAttribute("SelectFlag")!=null && "true".equals(r.getAttribute("SelectFlag").toString())){
                count++;
            }
        }
        if(count > 0){
            JSFUtils.showPopup(delegatePop);  
        }else{
            JSFUtils.addFacesErrorMessage("Select minimum one request to delegate !");
        }
    }

    public void setDelegatePop(RichPopup delegatePop) {
        this.delegatePop = delegatePop;
    }

    public RichPopup getDelegatePop() {
        return delegatePop;
    }

    public void onSelectAll(ActionEvent actionEvent) {
        ViewObject vo = ADFUtils.findIterator("WaitingTasksToDelegate_ROVOIterator").getViewObject();
        RowSetIterator rs = vo.createRowSetIterator(null);
            while(rs.hasNext()){
                Row r = rs.next();
                r.setAttribute("SelectFlag", Boolean.TRUE);
            }  
        ADFContext.getCurrent().getPageFlowScope().put("SelectAllFlag", "Y");
    }
    
    public void onUnSelectAll(ActionEvent actionEvent) {
        ViewObject vo = ADFUtils.findIterator("WaitingTasksToDelegate_ROVOIterator").getViewObject();
        RowSetIterator rs = vo.createRowSetIterator(null);
            while(rs.hasNext()){
                Row r = rs.next();
                r.setAttribute("SelectFlag", Boolean.FALSE);
            }
        ADFContext.getCurrent().getPageFlowScope().put("SelectAllFlag", "N");
    }
    
    private String callDelegateFYIEmail(String toPersonName, String toPersonEmail, String action) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
        String personSender = !"".equals(person) ?  "Mr."+person : "Admin team";
        String actionDone =  "Delegate".equals(action) ? "delegated" : "Reassign".equals(action) ? "reassigned" : "delegated or reassigned";
        String bodyPart =
            "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + toPersonName + "," + "\n" +
            "<br/>" +
            "<br/>" +
            "Kindly be informed you that "+personSender+" has been "+actionDone+" below requests, Please take action\n" + 
            "<br/>" +
            "\n" + 
            "</p>";
        String subj= "Delegated or Reassigned the waiting tasks";
        String from= "OFOQ.HR@TATWEER.SA";
        
        String details = this.getSelectedRequestsHtml();
        bodyPart = bodyPart + details;
        System.err.println(toPersonEmail+"=="+bodyPart );
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", toPersonEmail);
        sendMail.getParamsMap().put("subject", subj);
        sendMail.getParamsMap().put("e_body", bodyPart);
        sendMail.execute();
        JSFUtils.addFacesInformationMessage("Mail has been sent !");
        return null;   
    }
    
    public static String getSelectedRequestsHtml() {
        String tableRecords = "";
        String finalApprovalPart = "";
        String tableTitle =
            "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "<tr class=\"tableHeader\"><td colspan=\"6\" align=\"center\"><h2 style=\"color:white;\">Request Details</h2></td></tr>";

        String hReqName = "<th style=text-align:left>\n" +
            "        &nbsp;Request Name\n" +
            "      </th>\n";
        String hReqId = "<th style=text-align:left>\n" +
            "        &nbsp;Request ID\n" +
            "      </th>\n";
        String hPersonNo = "<th style=text-align:left>\n" +
            "        &nbsp;Person Number\n" +
            "      </th>\n";
        String hPersonName = "<th style=text-align:left>\n" +
            "        &nbsp;Person Name\n" +
            "      </th>\n";
        String hReqStatus = "<th style=text-align:left>\n" +
            "        &nbsp;Request Status\n" +
            "      </th>\n";
        String hCreationDate = "<th style=text-align:left>\n" +
            "        &nbsp;Creation Date\n" +
            "      </th>\n";
        
        String tableHeader = hReqName + hReqId + hPersonNo + hPersonName + hReqStatus + hCreationDate;
        
        try {

            ViewObject vo = ADFUtils.findIterator("WaitingTasksToDelegate_ROVOIterator").getViewObject();
            RowSetIterator rs = vo.createRowSetIterator(null);

            while(rs.hasNext()){
                Row r = rs.next();
                if(r.getAttribute("SelectFlag")!=null && "true".equals(r.getAttribute("SelectFlag").toString())){
                    String reqID = r.getAttribute("RequestId")!=null?r.getAttribute("RequestId").toString():"";
                    String reqName = r.getAttribute("RequestName")!=null?r.getAttribute("RequestName").toString():"";
                    String reqStatus = r.getAttribute("RequestStatus")!=null?r.getAttribute("RequestStatus").toString():"";
                    String personNo = r.getAttribute("PersonNumber")!=null?r.getAttribute("PersonNumber").toString():"";
                    String personName = r.getAttribute("PersonName")!=null?r.getAttribute("PersonName").toString():"";
                    String creationDate = r.getAttribute("CreationDate")!=null?r.getAttribute("CreationDate").toString():"";
               
                tableRecords = tableRecords + 
                    "<tr>\n" + "<td>" +
                    reqID +  "</td>\n" +
                    "<td>" +
                    reqName + "</td>\n" +
                    "<td>" +
                    personNo + "</td>\n" + 
                    "<td>" +
                    personName + "</td>\n" + 
                    "<td>" +
                    reqStatus + "</td>\n" + 
                    "<td>" +
                    creationDate + "</td>\n" +  "  </tr>"; 
                }
            }
            
            String verticalSpace = "<p>&nbsp;</p>";
            finalApprovalPart =
                    verticalSpace + tableTitle + tableHeader + tableRecords + " </table>" +
                    "<br/></br>";
        } catch (Exception e) {
            e.printStackTrace();

        }
        return finalApprovalPart;
    }
}

