package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.views.up.TraineesExitForm_VORowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;

import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.uiutils.JSFUtils;

import java.io.UnsupportedEncodingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.xml.bind.JAXBElement;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Number;
import oracle.jbo.domain.Timestamp;

public class TraineesExitFormMB {
    
    private RichPopup createReqPop;
    private RichPopup jsfMsgPop;
    
    public TraineesExitFormMB() {
        super();
    }
    
    public String  onNewRequest() throws Exception {
        String personNo = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String personName = (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}");
        
        ViewObject vo = ADFUtils.findIterator("AllPersonsInHR_ROVOIterator").getViewObject();
        ViewCriteria vc = vo.createViewCriteria();
        ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
        vcRow.setAttribute("PersonNumber", personNo);
        vc.addRow(vcRow);
        vcRow.setAttribute("Active", "Y");
        vc.addRow(vcRow);
        vo.applyViewCriteria(vc);
        vo.executeQuery();
        long count = vo.getEstimatedRowCount();
        if(count > 0){
            JSFUtils.setExpressionValue("#{bindings.RequestForTrans.inputValue}", "Self");
            JSFUtils.setExpressionValue("#{bindings.PersonNameTrans.inputValue}", "");
            JSFUtils.setExpressionValue("#{bindings.PersonNumberTrans.inputValue}", "");
            JSFUtils.showPopup(createReqPop);    
        }else{
            return this.createRequestNew("Self", personNo, personName);  
        }
        return null;
        
    }

    public void setCreateReqPop(RichPopup createReqPop) {
        this.createReqPop = createReqPop;
    }

    public RichPopup getCreateReqPop() {
        return createReqPop;
    }
    
    private boolean checkPendingRequest(String person) throws ParseException {
        
        ViewObject refVo = ADFUtils.findIterator("TraineesExitForm_VORefIterator").getViewObject();
        ViewCriteria vc = refVo.createViewCriteria();
        ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
        vcRow.setAttribute("PersonName", person);
        vc.addRow(vcRow);
        vcRow.setAttribute("RequestStatus", "IN ('PENDING','SAVED')");
        vc.addRow(vcRow);
        refVo.applyViewCriteria(vc);
        refVo.executeQuery();
        long count = refVo.getEstimatedRowCount();
//        vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("SaerchVC"));
//        vo.executeQuery();
        System.err.println("Pending records="+count);
        if(count != 0){
            JSFUtils.addFacesErrorMessage("You Can't Submit a new Request of Clerance Form in Case if You already Submitted Exit and Clerance Form");
            return false;
        }
        
        //Check Approved if any
        ViewCriteria vc1 = refVo.createViewCriteria();
        ViewCriteriaRow vcRow1 = vc1.createViewCriteriaRow();
        vcRow1.setAttribute("PersonName", person);
        vc1.addRow(vcRow1);
        vcRow1.setAttribute("RequestStatus", "APPROVED");
        vc1.addRow(vcRow1);
        refVo.applyViewCriteria(vc1);
        refVo.executeQuery();
        long approvedCount = refVo.getEstimatedRowCount();
        if(approvedCount > 0){
            Row first = refVo.first();
            String termination = first.getAttribute("LastWorkingDate") != null ? first.getAttribute("LastWorkingDate").toString() : "";
                
            Date terminationDate = new Date();
            Date sysdate = new Date();
            
            if(!"".equals(termination)){
                terminationDate = new SimpleDateFormat("yyyy-MM-dd").parse(termination);    
            }
            System.err.println("Date compare ="+sysdate.compareTo(terminationDate));
            if(sysdate.compareTo(terminationDate) < 0){
                JSFUtils.addFacesErrorMessage("You Can't Submit a new Request of Clerance Form in Case if You already Submitted Exit and Clerance Form");
                return false;
            }
        }
            
        return true;
    }
    
    public String createRequestNew(String type, String personNumber, String personName) throws Exception {
        
        if(!this.checkPendingRequest(personName)){
            return "";
        } 
        String sessionUser = (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}");
        ADFContext.getCurrent().getPageFlowScope().put("formName", "Exit");
        ViewObject settleVo = ADFUtils.findIterator("TraineesExitForm_VOIterator").getViewObject();
        BiReportAccess report = new BiReportAccess();
        System.err.println("personName==-"+personName);
        System.err.println("personNumber==-"+personNumber);
        
        String personPosition = (String)JSFUtils.resolveExpression("#{PersonInfo.position}");
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}");
        String personJob = (String)JSFUtils.resolveExpression("#{PersonInfo.job}");
        String personGrade = (String)JSFUtils.resolveExpression("#{PersonInfo.gradeCode}");
        String personDepartment = (String)JSFUtils.resolveExpression("#{PersonInfo.department}");
        
        if(! "Self".equals(type)){
            ArrayList<String> list = this.getPersonDetails(personNumber);
            System.err.println("list~~"+list); 
            personPosition = list.get(2);
            personDepartment = list.get(3);
            personLocation = list.get(4);
            personGrade = list.get(5);
            personJob = list.get(6);
        }
        
        List<Map> empsMap = report.getTerminatedEmployees(personNumber); 
//        List<Map> empsMap = report.getTerminatedEmployees("D0023");
        System.err.println("EmpsMap : "+empsMap);
        String jobName = empsMap.get(0).get("JOB_NAME")!=null ? empsMap.get(0).get("JOB_NAME").toString() : "";
        if("Trainee".equals(jobName)){
            
            String lastWorkDate = empsMap.get(0).get("ACTUAL_TERMINATION_DATE")!=null ? empsMap.get(0).get("ACTUAL_TERMINATION_DATE").toString() : "";
            String hireDate = empsMap.get(0).get("HIRE_DATE")!=null ? empsMap.get(0).get("HIRE_DATE").toString() : "";
            String reason = empsMap.get(0).get("ACTION_REASON")!=null ? empsMap.get(0).get("ACTION_REASON").toString() : "";
            String totYrs = empsMap.get(0).get("TOT_YEARS")!=null ? empsMap.get(0).get("TOT_YEARS").toString() : "";
            String totMonths = empsMap.get(0).get("TOT_MONTHS")!=null ? empsMap.get(0).get("TOT_MONTHS").toString() : "";
            String totDays= empsMap.get(0).get("TOT_DAYS")!=null ? empsMap.get(0).get("TOT_DAYS").toString() : "";
            String depart = empsMap.get(0).get("DEPARTMENT_AR")!=null ? empsMap.get(0).get("DEPARTMENT_AR").toString() : "";
            String manager = empsMap.get(0).get("MANAGER_NAME_AR")!=null ? empsMap.get(0).get("MANAGER_NAME_AR").toString() : "";
            String personNameArabic = empsMap.get(0).get("FULL_NAME_AR")!=null ? empsMap.get(0).get("FULL_NAME_AR").toString() : "";
            String reasonEng = empsMap.get(0).get("ACTION_REASON_EN")!=null ? empsMap.get(0).get("ACTION_REASON_EN").toString() : "";
            String actionType = empsMap.get(0).get("ACTION")!=null ? empsMap.get(0).get("ACTION").toString() : "";
            String managerEng = empsMap.get(0).get("MANAGER_NAME")!=null ? empsMap.get(0).get("MANAGER_NAME").toString() : "";
            String trainingTitle = empsMap.get(0).get("POSITION_NAME_AR")!=null ? empsMap.get(0).get("POSITION_NAME_AR").toString() : "";
            
            String lengthOfService = totYrs + " Years, "+ totMonths +" Months, "+ totDays +" Days ";
            Date hiredDate = new Date();
            Date lastWorkingDate = new Date();
            
            if(!"".equals(hireDate)){
                hiredDate = new SimpleDateFormat("dd-MMM-yyyy").parse(hireDate);    
            }
            if(!"".equals(lastWorkDate)){
                lastWorkingDate = new SimpleDateFormat("dd-MMM-yyyy").parse(lastWorkDate);    
            }
            
            Row newRow = settleVo.createRow();
            newRow.setAttribute("RequestId", null); 
            newRow.setAttribute("StepId", "1"); 
            
            newRow.setAttribute("PersonNumber", personNumber);
            newRow.setAttribute("PersonName", personName);
            newRow.setAttribute("PersonLocation", personLocation);
            newRow.setAttribute("PersonPosition", personPosition);
            newRow.setAttribute("PersonJob", personJob);
            newRow.setAttribute("PersonGrade", personGrade);
            newRow.setAttribute("PersonDepartment", personDepartment);
            newRow.setAttribute("PersonNameAr", personNameArabic);
            newRow.setAttribute("TrainingTitle", trainingTitle);
            
            newRow.setAttribute("DepartmentAr", depart);
            newRow.setAttribute("LengthOfService", lengthOfService);
            newRow.setAttribute("ManagerName", managerEng);
            newRow.setAttribute("ManagerNameAr", manager); 
            newRow.setAttribute("ReasonForExit", reasonEng);
            newRow.setAttribute("ReasonAr", reason);
            newRow.setAttribute("DateOfJoining", hiredDate);
            newRow.setAttribute("LastWorkingDate", lastWorkingDate); 
            newRow.setAttribute("CreatedBy", sessionUser); 
            newRow.setAttribute("RequestType", type);
            newRow.setAttribute("ActionType", actionType); 

            settleVo.insertRow(newRow);
            this.setExitStepType();
            
        }else{
            JSFUtils.addFacesErrorMessage("You are not allowed to create request, as trainee details not available for "+personNumber);
            return null;
        } 
        return "edit";
    }
    
    public ArrayList<String> getPersonDetails(String personNumber) {
        ArrayList<String> personList = new ArrayList<String>();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);

        JAXBElement<String> nameElement =
            userDetails.getUserPersonDetails().get(0).getDisplayName();
        String name = nameElement.getValue();
        
        JAXBElement<String> emailElement =
            userDetails.getUserPersonDetails().get(0).getEmailAddress();
        String email = emailElement.getValue();
        List<UserWorkRelationshipDetails> relationshipDetails =
                    userDetails.getUserWorkRelationshipDetails();
        String position = relationshipDetails.get(0).getPositionName();
        String department = relationshipDetails.get(0).getDepartmentName();
        String location = relationshipDetails.get(0).getLocationName();
        String gradeCode = relationshipDetails.get(0).getGradeCode();
        String job = relationshipDetails.get(0).getJobName().getValue();
        //Get person name by get(0)
        if(name!=null){
            personList.add(name);    
        }else{
            personList.add("");
        }
        //Get person email by get(1)
        if(email!=null){
            personList.add(email);    
        }else{
            personList.add("");
        }
        //Get person position by get(2)
        if(position!=null){
            personList.add(position);    
        }else{
            personList.add("");
        }
        //Get person department by get(3)
        if(department!=null){
            personList.add(department);    
        }else{
            personList.add("");
        }
        //Get person location by get(4)
        if(location!=null){
            personList.add(location);    
        }else{
            personList.add("");
        }
        //Get person gradeCode by get(5)
        if(gradeCode!=null){
            personList.add(gradeCode);    
        }else{
            personList.add("");
        }
        //Get person job by get(6)
        if(job!=null){
            personList.add(job);    
        }else{
            personList.add("");
        }

        return personList;
    }
    
    public String onCreateFromPop() throws ParseException, Exception {
        String type = ADFUtils.getBoundAttributeValue("RequestForTrans") != null ? ADFUtils.getBoundAttributeValue("RequestForTrans").toString() : "Self";
        String pName = ADFUtils.getBoundAttributeValue("PersonNameTrans") != null ? ADFUtils.getBoundAttributeValue("PersonNameTrans").toString() : "";
        String pNo = ADFUtils.getBoundAttributeValue("PersonNumberTrans") != null ? ADFUtils.getBoundAttributeValue("PersonNumberTrans").toString() : "";

        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String personName = (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}"); 
        
        if("Self".equals(type)){ 
            return this.createRequestNew(type, personNumber, personName);
        }else{
            return this.createRequestNew(type, pNo, pName);
        }
    }
    
    public String onSave() throws Exception {
        ViewObject vo = ADFUtils.findIterator("TraineesExitForm_VOIterator").getViewObject();
        vo.getCurrentRow().setAttribute("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request Saved !");
        return "back";
    }
    
    public String onClickEdit() {
        ViewObject vo = ADFUtils.findIterator("TraineesExitForm_VOIterator").getViewObject();
        Row currRow = vo.getCurrentRow();
        String position = currRow.getAttribute("AssigneePosition")!=null ?currRow.getAttribute("AssigneePosition").toString():"";
        String personName = (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}");
        String createdName = currRow.getAttribute("PersonName")!=null ?currRow.getAttribute("PersonName").toString():"";
        String createdBy = currRow.getAttribute("CreatedBy")!=null ?currRow.getAttribute("CreatedBy").toString():"";
        
        if(personName.equals(createdName) || personName.equals(createdBy)){
            ADFContext.getCurrent().getPageFlowScope().put("formName", "Exit");  
            ADFContext.getCurrent().getPageFlowScope().put("AssigneeIsHR", "TRUE");
        }else{
            if("HR_OPER".equals(position) || "THC_HR_OPER".equals(position) || "HR_DEPART".equals(position)){

                ADFContext.getCurrent().getPageFlowScope().put("formName", "Exit");  
                ADFContext.getCurrent().getPageFlowScope().put("AssigneeIsHR", "TRUE");    
            }else{
                ADFContext.getCurrent().getPageFlowScope().put("formName", "Clearance");  
                ADFContext.getCurrent().getPageFlowScope().put("AssigneeIsHR", "FALSE"); 
            } 
        }  
        this.setExitStepType();
        return "edit";
    }
    
    public String onClickSumbit() throws Exception {
        
        ADFUtils.findOperation("Commit").execute(); 
        
        Row exitRow = ADFUtils.findIterator("TraineesExitForm_VOIterator").getCurrentRow(); 
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted Exit-Trainees : stepType is::"+stepType);
        
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        
        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        Long lineManagerID1 = relationshipDetails.get(0).getManagerId();
        UserDetails managerDetails1 =
            userService.getUserDetailsByPersonId(lineManagerID1);
        String managerNumber1 = managerDetails1.getPersonNumber();
        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType)){
            System.err.println("Submitted : LINE_MANAGER ");
            JAXBElement<String> aXBElement = null;
            if (managerDetails1.getUserPersonDetails().get(0).getDisplayName() !=
                null) {
                aXBElement =
                    managerDetails1.getUserPersonDetails().get(0).getDisplayName();
            }else{
                JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not approve the request");
                return null;
            }
            assigneeName = aXBElement.getValue();
            assigneeNo = managerNumber1;                  
        }else{
            
            System.err.println("Submitted : USER ");
            ArrayList<String> personDetails = new ArrayList<String>();
            personDetails = getPersonDetailsFromReport(nextStep.getAttribute("NextAssignee").toString());
            assigneeName = personDetails.get(0).toString();
            assigneeEmail = personDetails.get(1).toString();
            assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
            if("".equals(assigneeName) || "".equals(assigneeEmail)){
                JSFUtils.addFacesErrorMessage("You can not submit the request as "+stepType+" details not available. ");
                return null;
            }
        }
        
        System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
        System.err.println("Assigning to (name) ::"+assigneeName);
        System.err.println("Assigning to (no) ::"+assigneeNo);
        System.err.println("Assigning to (email) ::"+assigneeEmail);
        
        //
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("ActionTaken", "SUBMITTED");
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        ADFUtils.setBoundAttributeValue("AssigneePosition", stepType);
        ADFUtils.setBoundAttributeValue("LastAssignedTime", new Timestamp(new Date()));
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        this.setUsageDetails(stepType , assigneeName);
        
        //Store certificate in DB
        BiReportAccess report = new BiReportAccess();
        String personPosition = ADFUtils.getBoundAttributeValue("PersonPosition").toString();
        byte[] attach;
        
        if("Tamheer Trainee".equals(personPosition)){
            attach = report.getTrainingCertificateForTamheer(personNumber);
        }else{
            attach = report.getTrainingCertificate(personNumber);
        }
        
        BlobDomain blob = new BlobDomain(attach);
        exitRow.setAttribute("ExpCertification", blob);
        
        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"), 
                                                                    "TraineesExitForm", (Number)nextStep.getAttribute("NextStepId"), 
                                                                    (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                    "SUMBIT_ACT", "");
        ADFUtils.findOperation("Commit").execute(); 
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//             sendEmail(assigneeNo, exitRow);
             sendEmailByEmail(assigneeEmail, exitRow);
        }
        JSFUtils.addFacesInformationMessage("Exit and Clearance Request has been submitted");
        return "back";
    }
    
    public ArrayList<String> getPersonDetailsFromReport(String personNumber){
        ArrayList<String> personList = new ArrayList<String>();
        String name = "";
        String email = "";
        ViewObject vo = ADFUtils.findIterator("AllPersons_VOIterator").getViewObject();
        ViewCriteria vc = vo.createViewCriteria();
        ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
        vcRow.setAttribute("PersonNumber", personNumber);
        vc.addRow(vcRow);
        vo.applyViewCriteria(vc);
        vo.executeQuery();
        Row r = vo.first();
        name = r.getAttribute("PersonName")!=null?r.getAttribute("PersonName").toString():"";
        email = r.getAttribute("PersonEmail")!=null?r.getAttribute("PersonEmail").toString():"";
        personList.add(name);   //Get person name by get(0)
        personList.add(email);    //Get person email by get(1)
        vo.applyViewCriteria(null);
        vo.executeQuery();
        System.err.println("personList--"+personList);
        return personList;
    }

    private void setUsageDetails(String stepType, String assigneeName) {
        oracle.jbo.domain.Date sysdate = new oracle.jbo.domain.Date(new oracle.jbo.domain.Date().getCurrentDate());
        String type = ADFContext.getCurrent().getPageFlowScope().get("TraineesExitFromType").toString();
        
        if("LINE_MANAGER".equals(stepType)){
            ADFUtils.setBoundAttributeValue("DhDate", sysdate);
            ADFUtils.setBoundAttributeValue("DhName", assigneeName);
        }
        if("ITC".equals(stepType)){
            ADFUtils.setBoundAttributeValue("ItcDate", sysdate);
            ADFUtils.setBoundAttributeValue("ItcName", assigneeName);
        }
        if("FACILITY_MNG".equals(stepType)){
            ADFUtils.setBoundAttributeValue("FmuDate", sysdate);
            ADFUtils.setBoundAttributeValue("FmuName", assigneeName);
        } 
        if("HR_OPER".equals(stepType)){
            ADFUtils.setBoundAttributeValue("Hro2Date", sysdate);
            ADFUtils.setBoundAttributeValue("Hro2Name", assigneeName);
        }
        if("THC_HR_OPER".equals(stepType)){
            ADFUtils.setBoundAttributeValue("HroDate", sysdate);
            ADFUtils.setBoundAttributeValue("HroName", assigneeName);
        }
        if("HR_DEPART".equals(stepType)){
            ADFUtils.setBoundAttributeValue("HrdDate", sysdate);
            ADFUtils.setBoundAttributeValue("HrdName", assigneeName);
        }
    }
    
    public String onClickApprove() throws Exception {
        if(! this.checkRequired()){
            return ""; 
        }else{
            System.err.println("Values available !");
        }
        ViewObject reqVo = ADFUtils.findIterator("TraineesExitForm_VOIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String expCertFlag = currRow.getAttribute("ExpCertificationFlag")!=null?currRow.getAttribute("ExpCertificationFlag").toString():"";

        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():""; 
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        String personName =
            ADFUtils.getBoundAttributeValue("PersonName").toString();
        
        
        UserServiceUtil userService1 = new UserServiceUtil();
        UserDetails userDetails1 = null;
        userDetails1 =
                userService1.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails1 =
            userDetails1.getUserWorkRelationshipDetails();
        Long lineManagerID1 = relationshipDetails1.get(0).getManagerId();
        UserDetails managerDetails1 =
            userService1.getUserDetailsByPersonId(lineManagerID1);
        String managerNumber1 = managerDetails1.getPersonNumber();
        String department1 =
            relationshipDetails1.get(0).getDepartmentName();
          
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType)){
                System.err.println("Approved Exit, Assigning to : : LINE_MANAGER ");     
                JAXBElement<String> aXBElement = null;
                if (managerDetails1.getUserPersonDetails().get(0).getDisplayName() !=
                    null) {
                    aXBElement =
                        managerDetails1.getUserPersonDetails().get(0).getDisplayName();
                }else{
                    JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not approve the request");
                    return null;
                }
                assigneeName = aXBElement.getValue();
                assigneeNo = managerNumber1;
        }
        else if(!nextStep.getAttribute("NextAssignee").equals("Finished")){
             
            System.err.println("Approved Exit, Assigning to : : USER ");
            ArrayList<String> personDetails = new ArrayList<String>();
            personDetails = getPersonDetailsFromReport(nextStep.getAttribute("NextAssignee").toString());
            assigneeName = personDetails.get(0).toString();
            assigneeEmail = personDetails.get(1).toString();
            assigneeNo = nextStep.getAttribute("NextAssignee").toString();
            
            if("".equals(assigneeName) || "".equals(assigneeEmail)){
                JSFUtils.addFacesErrorMessage("You can not approve the request as "+stepType+" details not available. ");
                return null;
            }
        } 
        System.err.println("Assigning to (name) ::"+assigneeName);
        System.err.println("Assigning to (no) ::"+assigneeNo);
        System.err.println("Assigning to (email) ::"+assigneeEmail);
        
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        ADFUtils.setBoundAttributeValue("AssigneePosition", stepType);
        ADFUtils.setBoundAttributeValue("LastAssignedTime", new Timestamp(new Date()));
        this.setUsageDetails(stepType , assigneeName);
        
        if (nextStep.getAttribute("NextAssignee").equals("Finished")){
            
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
            ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("Assignee", "");
            ADFUtils.setBoundAttributeValue("AssigneePosition", "");
            ADFUtils.setBoundAttributeValue("LastAssignedTime", new Timestamp(new Date()));
        }
        
        String finalapproval = null;
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            finalapproval = "Y";
        } else {
            finalapproval = "N";
        } 
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                  "TraineesExitForm",(Number)nextStep.getAttribute("StepId"),
                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                  "APPROVE_ACT",finalapproval);
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
                sendEmailToPerson(personNumber, currRow);
                sendEmailByEmail(fyiEmailAddress, currRow);
                if("true".equals(expCertFlag)){
                    this.sendExperienceCertificate();  
                } 
            } else {
                sendEmailByEmail(assigneeEmail, currRow);
            } 
        }
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";
    }
    
    public String onClickReject() {
        Row exitRow = ADFUtils.findIterator("TraineesExitForm_VOIterator").getCurrentRow(); 
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", "");
        ADFUtils.setBoundAttributeValue("AssigneeName", ""); 
        ADFUtils.setBoundAttributeValue("AssigneePosition", "");
        
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                  "TraineesExitForm",(Number)nextStep.getAttribute("StepId"), new Long(0), null,
                                                  "REJECT_ACT",""); 
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            sendEmailToPerson(personNumber, exitRow);
        }
        ADFUtils.findOperation("Commit").execute(); 
        JSFUtils.addFacesInformationMessage("Exit and Clearance Request has been Rejected");

        return "back";
    }
    
    private void setExitStepType() {
        
        ViewObject vo = ADFUtils.findIterator("TraineesExitForm_VOIterator").getViewObject();
        Row currRow = vo.getCurrentRow();
        String personLocation = currRow.getAttribute("PersonLocation")!=null ?currRow.getAttribute("PersonLocation").toString():"";
        String exitTypename = "TraineesExitForm-THC";
        
        if("Special Education City - Makkah".equals(personLocation)){
            exitTypename = "TraineesExitForm-Makka";
        }
        if("Prince Sultan Center".equals(personLocation)){
            exitTypename = "TraineesExitForm-Sultan";
        }
        if("Special Education Center - Alkubar".equals(personLocation)){
            exitTypename = "TraineesExitForm-Alkhobar";
        }
        if("Special Education Center - Buraidah".equals(personLocation)){
            exitTypename = "TraineesExitForm-Qassim";
        }
        System.err.println("Person Locations is :"+personLocation);
        System.err.println("Location Exit Type is :"+exitTypename);
        
        ADFContext.getCurrent().getPageFlowScope().put("TraineesExitFromType", exitTypename);
    }
    
    public String getEmail(String personNumber) {
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);

        JAXBElement<String> aXBElement =
            userDetails.getUserPersonDetails().get(0).getEmailAddress();
        return aXBElement.getValue();
    }
    
    public void sendEmail(String personNumber, Row subject) {
        String email = this.getEmail(personNumber);
        System.out.println("- Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendTraineeExitEmail("OFOQ.HR@TATWEER.SA",email,
                          (TraineesExitForm_VORowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendTraineeExitEmail("OFOQ.HR@TATWEER.SA", personEmail,
                      (TraineesExitForm_VORowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    public void sendEmailToPerson(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendTraineeExitEmail("OFOQ.HR@TATWEER.SA", email,
                                     (TraineesExitForm_VORowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public void sendTraineeExitEmail(String from, String to,
                              TraineesExitForm_VORowImpl subject, String personFYI) {
        ADFUtils.findOperation("ExecuteWithParams").execute();
        String status = subject.getRequestStatus();
        String requesterName = subject.getPersonName();
        String assigneeName = "Sir";
        if(personFYI!= null && "Y".equals(personFYI)){
            assigneeName = requesterName;
        }else{
            assigneeName = subject.getAssigneeName()!=null ? subject.getAssigneeName() : "Sir";
        }
        
        String subj = "";
        String hdrMsg = ""; 
        subj = "Trainee Exit Request for Mr./Mrs."+requesterName+ " is " + status;  
        hdrMsg= "Kindly find below the details of Trainee Exit request as below"; 
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("APPROVED".equals(status)){
                assigneeName = requesterName;
                subj = "Trainee Exit Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Trainee Exit request has been Approved";
            }
            if("REJECTED".equals(status)){
                assigneeName = requesterName;
                subj = "Trainee Exit Request has been REJECTED";
                hdrMsg= "Kindly be informed you that the following Trainee Exit request has been Rejected";
            }
           
        }
//                JSFUtils.addFacesInformationMessage(subj);
//                JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
          //  to = "vf.khayal@gmail.com";
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");
        }
    
        String bodyPart =
            "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" +
            hdrMsg +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";
        String PersoneName=subject.getPersonName()!=null?subject.getPersonName():"";
        String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
        String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
        String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
        String bodyPart10 =
        "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>"  +
            "    <tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
            subject.getPersonNumber() + "</td></tr>\n" +
            "    <tr><th>&nbsp;Name</th>\n" +
            "      <td width=\"50%\">" + PersoneName+ "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Position\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +PersonPosition +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Department\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +PersonDepartment +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Location\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + PersonLocation+
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String bodyPart11 = " <p>&nbsp;</p>";

        String bodyPart12 =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Exit and Clearance Details</h2></td></tr>"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Hire Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDateOfJoining() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Last Working Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getLastWorkingDate() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Length Of Service\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getLengthOfService() + "</td>\n" +
            "    </tr>"+
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Reason for Exit\n" +
            "      </th>\n" +
        "      <td width=\"50%\">" + (subject.getReasonForExit()!=null?subject.getReasonForExit():"-") + " </td>\n" +
            "    </tr>" + "<tr><th>\n" +
            "        &nbsp;Manager Name\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getManagerName() +
            "</td>\n" +
            "    </tr>" + 
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Action\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getActionType() + " </td>\n" +
            "    </tr>" +
            "  </table>";
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryVO3Iterator"); 
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String text =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 +verticalSpace+ApprovalPart1+ thankYouPart +
            signaturePart + "</p>";
        
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        sendMail.getParamsMap().put("subject", subj);
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
    }
    
    public void onUpdatePersonalEmail(ActionEvent actionEvent) {
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Personal Email Address Updated !");
    }
    
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("TraineesExitForm_VOIterator").getViewObject();
        vo.applyViewCriteria(null);
        vo.executeQuery();
        
        if ("MyTasks".equals(statusType)) {
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("SearchMyTaskOnly"));
            vo.executeQuery();
        }else 
        if("PendingTasks".equals(statusType)){
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("AssigneeName", person);
            vc.addRow(vcRow);
            vcRow.setAttribute("RequestStatus", "IN ('PENDING','Waiting Withdraw Approval')");
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }else{
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("SaerchVC"));
            vo.executeQuery();
        }
    }
    
    public boolean checkRequired(){
        Row exitRow = ADFUtils.findIterator("TraineesExitForm_VOIterator").getCurrentRow();
        String stepType = exitRow.getAttribute("AssigneePosition")!=null ?exitRow.getAttribute("AssigneePosition").toString() : "";
        
        if("LINE_MANAGER".equals(stepType)){
            if(exitRow.getAttribute("DhQues1")==null || "false".equals(exitRow.getAttribute("DhQues1").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "DhQues1");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
            if(exitRow.getAttribute("DhQues3")==null || "false".equals(exitRow.getAttribute("DhQues3").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "DhQues3");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
        }
        
        if("ITC".equals(stepType)){ 
            if(exitRow.getAttribute("ItcQues1")==null || "false".equals(exitRow.getAttribute("ItcQues1").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "ItcQues1");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
            if(exitRow.getAttribute("ItcQues2")==null || "false".equals(exitRow.getAttribute("ItcQues2").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "ItcQues2");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
            if(exitRow.getAttribute("ItcQues3")==null || "false".equals(exitRow.getAttribute("ItcQues3").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "ItcQues3");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
        }  
        if("FACILITY_MNG".equals(stepType)){
            
            if(exitRow.getAttribute("FmuQues2")==null || "false".equals(exitRow.getAttribute("FmuQues2").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "FmuQues2");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
        } 
        if("THC_HR_OPER".equals(stepType)){
            if(exitRow.getAttribute("HroQues1")==null || "false".equals(exitRow.getAttribute("HroQues1").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "HroQues1");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
            if(exitRow.getAttribute("HroQues2")==null || "false".equals(exitRow.getAttribute("HroQues2").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "HroQues2");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
            if(exitRow.getAttribute("HroQues3")==null || "false".equals(exitRow.getAttribute("HroQues3").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "HroQues3");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
        }
        if("HR_OPER".equals(stepType)){ 
                if(exitRow.getAttribute("Hro2Ques1")==null || "false".equals(exitRow.getAttribute("Hro2Ques1").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "Hro2Ques1");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
        }
        if("HR_DEPART".equals(stepType)){
            if(exitRow.getAttribute("HrdQues1")==null || "false".equals(exitRow.getAttribute("HrdQues1").toString())){
                ADFContext.getCurrent().getPageFlowScope().put("jsfErrorMessage", "HrdQues1");
                JSFUtils.showPopup(jsfMsgPop);
                return false;
            }
        }
        
        return true;
    }

    public void setJsfMsgPop(RichPopup jsfMsgPop) {
        this.jsfMsgPop = jsfMsgPop;
    }

    public RichPopup getJsfMsgPop() {
        return jsfMsgPop;
    }
    
    public void sendExperienceCertificate() throws Exception {
        Row exitRow = ADFUtils.findIterator("TraineesExitForm_VOIterator").getCurrentRow();
        String requestId = ADFUtils.getBoundAttributeValue("RequestId").toString();
        String to = exitRow.getAttribute("PersonalEmail")!=null?exitRow.getAttribute("PersonalEmail").toString():"";
        if(!"".equals(to)){
            OperationBinding sendMail = ADFUtils.findOperation("sendTrainingCertificate");
            sendMail.getParamsMap().put("requestId", Integer.parseInt(requestId));
            sendMail.execute();
            JSFUtils.addFacesInformationMessage("Training Certificate has been sent.");
        }
    }
}
