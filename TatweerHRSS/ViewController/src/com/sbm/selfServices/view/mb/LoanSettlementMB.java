package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.LoanSettlement_VORowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;

import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStripApp.FilmStripBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.JAXBElement;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import oracle.jbo.domain.Number;

import org.apache.commons.io.IOUtils;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class LoanSettlementMB {
    
    private FusionDataLoader fusionFileLoader;
    public LoanSettlementMB() {
        super();
    }
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }
    
    public String createRequest(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        BiReportAccess BIRA = new BiReportAccess();
        List<Map> LoanRemainingList;
                                                       
        OperationBinding loanDtls = ADFUtils.findOperation("getLastLoanDetails");
        Row loanRow = (Row)loanDtls.execute();
    
        try{
            LoanRemainingList = BIRA.getLoanRemaining(personNumber);
            if(LoanRemainingList.size()>0){
                String LoanRemainingString = LoanRemainingList.get(0).get("LOANREMAINING")!=null ? LoanRemainingList.get(0).get("LOANREMAINING").toString() : "0";
                Double LoanRemaining = Double.parseDouble(LoanRemainingString);
                System.err.println("Remaining amount::"+LoanRemaining);
                if(LoanRemaining>0){ 
                if(loanRow!=null){
                    this.createNewRequest(loanRow, LoanRemaining);
                    return "new";
                }else{
                    FilmStripBean.showPopupMessage("There is no loan available !");
                }
                }else{
                    FilmStripBean.showPopupMessage("There is no remaining amount for settlement !");
                }
            }else{
                FilmStripBean.showPopupMessage("There is no remaining amount for settlement !");
            }
            } catch (Exception e) {
                FilmStripBean.showPopupMessage("Error : "+ e.toString());
            }
        return "";
    }

    private void createNewRequest(Row loanRow, Double loanRemaining ) {
        ViewObject settleVo = ADFUtils.findIterator("LoanSettlement_VOIterator").getViewObject();
        
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String personPosition = (String)JSFUtils.resolveExpression("#{PersonInfo.position}");
        String personName = (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}");
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}");
        String personJob = (String)JSFUtils.resolveExpression("#{PersonInfo.job}");
        String personGrade = (String)JSFUtils.resolveExpression("#{PersonInfo.gradeCode}");
        String personDepartment = (String)JSFUtils.resolveExpression("#{PersonInfo.department}");
        
        Row newRow = settleVo.createRow();
        newRow.setAttribute("LoanPreclosureId", null);
        newRow.setAttribute("LoanId", loanRow.getAttribute("LoanId"));
        newRow.setAttribute("LoanTotalAmount", loanRow.getAttribute("TotalAmount"));
        newRow.setAttribute("LoanDivision", loanRow.getAttribute("Division"));
        newRow.setAttribute("LoanNoOfMonths", loanRow.getAttribute("NoOfMonths"));
        newRow.setAttribute("LoanCreationDate", loanRow.getAttribute("CreationDate"));
        newRow.setAttribute("LoanRemainingAmount", loanRemaining);
        
        newRow.setAttribute("PersonNumber", personNumber);
        newRow.setAttribute("PersonPosition", personPosition);
        newRow.setAttribute("PersonName", personName);
        newRow.setAttribute("PersonLocation", personLocation);
        newRow.setAttribute("PersonJob", personJob);
        newRow.setAttribute("PersonGrade", personGrade);
        newRow.setAttribute("PersonDepartment", personDepartment);
        
        settleVo.insertRow(newRow);
    }

    public String onClickSave() {
        ViewObject settleVo = ADFUtils.findIterator("LoanSettlement_VOIterator").getViewObject();
        settleVo.getCurrentRow().setAttribute("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute(); 
        JSFUtils.addFacesInformationMessage("Request Saved !");
        return "back";  
    }
    
    public void onSelectAttach(ValueChangeEvent vcs) { 
        
        if(vcs.getNewValue()!=null){
            UploadedFile file = (UploadedFile)vcs.getNewValue();
            String fileName = file.getFilename();
            String contentType = file.getContentType();
            if (fileName.length() > 50) {
                JSFUtils.addFacesErrorMessage("File Name cannot be more than 50 character including extension. Please try with smaller file name!"); 
            } else { 
                BlobDomain blob = createBlobDomain(file);
                System.out.println("File name is " + fileName);
                
                ADFUtils.setBoundAttributeValue("AttachmentFile", blob);
                ADFUtils.setBoundAttributeValue("FileName", fileName);
                ADFUtils.setBoundAttributeValue("FileType", contentType);
            } 
        }      
    }
    
    private BlobDomain createBlobDomain(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;

        try {
            in = file.getInputStream();

            blobDomain = new BlobDomain();
            out = blobDomain.getBinaryOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead = 0;

            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }

        return blobDomain;
    }
    public void downloadFile(FacesContext facesContext,
                             OutputStream outputStream) {
        BlobDomain blob =
            (BlobDomain)ADFUtils.getBoundAttributeValue("AttachmentFile");
        try {
            IOUtils.copy(blob.getInputStream(), outputStream);
            blob.closeInputStream();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String seonClickSubmit() {
        
        ADFUtils.findOperation("Commit").execute(); 
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String LoanSettlementTypeName="Loan-Settlement" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanSettlementTypeName", LoanSettlementTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        
        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        String department = relationshipDetails.get(0).getDepartmentName();
        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        // If step type is POSITION
        if("POSITION".equals(stepType)){
            System.err.println("Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
            BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;
            try {
                personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (personData.get(0).get("DISPLAY_NAME") != null) {
                assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
            }
            if (personData.get(0).get("PERSON_NUMBER") != null) {
                assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
            }
            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
            }
        } 
        
        // If step type is DEPARTMENT MANAGER
        if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType)){
            System.err.println("Submitted : DEPT_MANAGER :"+department);
            BiReportAccess BIRA = new BiReportAccess();
            try {
                List<Map> managerOfDeptList =
                    BIRA.getManagerOfDepartmentData(department);
                if (managerOfDeptList.size() > 0) {
                    if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                        assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                    }
                    if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                        assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                    }
                    if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                        assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                    } 
                } else {
                    JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType)){
            System.err.println("Submitted : LINE_MANAGER ");
                String managerNumber =
                    (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

                Long lineManagerID =
                    (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
                String stringLineManagerID = lineManagerID.toString();
                if (stringLineManagerID == null) {
                    JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not submit the request");
                    return null;
                }  
                UserDetails byManagerId =
                    userService.getUserDetailsByPersonId(lineManagerID);
                String managerName =
                    byManagerId.getUserPersonDetails().get(0).getDisplayName().getValue(); 
                String managerEmail =
                    byManagerId.getUserPersonDetails().get(0).getEmailAddress().getValue();
             
            assigneeName = managerName;
            assigneeNo = managerNumber;
            assigneeEmail = managerEmail;
        }
        // If step type is USER
        if("USER".equals(stepType)){
            System.err.println("Submitted : USER ");
            ArrayList<String> personDetails = new ArrayList<String>();
            personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
            assigneeName = personDetails.get(0).toString();
            assigneeEmail = personDetails.get(1).toString();
            assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
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
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        
        ADFUtils.findOperation("Commit").execute();
        
        Row loanSettlRow =
            ADFUtils.findIterator("LoanSettlement_VOIterator").getCurrentRow(); 
        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"), 
                                                                    "Loan-Settlement", (Number)nextStep.getAttribute("NextStepId"), 
                                                                    (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                    "SUMBIT_ACT", "");
        
        if("POSITION".equals(stepType)){
            System.err.println(stepType + "-Mail to--email :"+assigneeEmail);
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if (assigneeEmail == null) {
                    JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver");
                    return "back";
                }else{
                  sendEmailByEmail(assigneeEmail, loanSettlRow);  
                } 
            }
        }
        if(! "POSITION".equals(stepType)){
            System.err.println(stepType + "-Mail to--person :"+assigneeNo);
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if (assigneeNo == null) {
                    JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver..");
                    return "back";
                }else{
                  sendEmail(assigneeNo, loanSettlRow);  
                } 
            }
        }
        JSFUtils.addFacesInformationMessage("Loan Settlement Request has been submitted");
        return "back";
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
        return personList;
    }

    public String onClickApprove() {
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String LoanSettlementTypeName="Loan-Settlement" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanSettlementTypeName", LoanSettlementTypeName);
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
        
        if("POSITION".equals(stepType)){
            System.err.println("Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
            BiReportAccess report = new BiReportAccess(); 
            List<Map> personData2 = null;
            try {
                personData2 = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (personData2.get(0).get("DISPLAY_NAME") != null) {
                assigneeName = personData2.get(0).get("DISPLAY_NAME").toString();
            }
            if (personData2.get(0).get("PERSON_NUMBER") != null) {
                assigneeNo = personData2.get(0).get("PERSON_NUMBER").toString();
            }
            if (personData2.get(0).get("EMAIL_ADDRESS") != null) {
                assigneeEmail = personData2.get(0).get("EMAIL_ADDRESS").toString();
            }
        }
        // If step type is DEPARTMENT MANAGER
            if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType)){
                    System.err.println("Approved, Assigning to : : DEPT_MANAGER :"+ department1);
                    BiReportAccess BIRA = new BiReportAccess();
                    try {
                        List<Map> managerOfDeptList =
                        BIRA.getManagerOfDepartmentData(department1);
                        if (managerOfDeptList.size() > 0) {
                            if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                            }
                            if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            }
                            if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                            }
                        }
                        else
                        {
                            JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                            return null;
                        }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
               }
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType)){
                System.err.println("Approved, Assigning to : : LINE_MANAGER ");     
                JAXBElement<String> aXBElement = null;
                if (managerDetails1.getUserPersonDetails().get(0).getDisplayName() !=
                    null) {
                    aXBElement =
                        managerDetails1.getUserPersonDetails().get(0).getDisplayName();
            }
                assigneeName = aXBElement.getValue();
                assigneeNo = managerNumber1;
        } 
        // If step type is USER
        if("USER".equals(stepType)){
            System.err.println("Approved, Assigning to : : USER ");
            ArrayList<String> personDetails = new ArrayList<String>();
            personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
            assigneeName = personDetails.get(0).toString();
            assigneeEmail = personDetails.get(1).toString();
            assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
        } 
        System.err.println("Assigning to (name) ::"+assigneeName);
        System.err.println("Assigning to (no) ::"+assigneeNo);
        System.err.println("Assigning to (email) ::"+assigneeEmail);
        
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        
        if (nextStep.getAttribute("NextAssignee").equals("Finished")){
            
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
            ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
            String remainingAmt =
                ADFUtils.getBoundAttributeValue("LoanRemainingAmount").toString();
            String createdOn =
                ADFUtils.getBoundAttributeValue("CreatedOn").toString();
            //EES substring added.
            String formattedCreatedOn = createdOn.replace('-', '/').substring(0, 10);
            System.out.println("LOAN SETTLE: FORMATTED DATE: "+formattedCreatedOn); 
            //String formattedCreatedOn= createdOn.replace('-', '/');
            
            BiReportAccess report = new BiReportAccess();
            List<Map> datFileData = null;
            try {
                datFileData = report.getAdjustLoanBalance(personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String assignmentNumber =
                datFileData.get(0).get("ASSIGNMENT_NUMBER").toString();
            String entryCount =
                datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString(); 
            
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("Date", formattedCreatedOn);
            params.put("AssignmentNo", assignmentNumber);
            params.put("Count", entryCount);
            params.put("RemainingAmount", remainingAmt);
            System.err.println("Fusion upload calling with values : "+params);
            
            try {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 11);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LoanId").toString(), personNumber, "Approved", "Adjust Loan Balance Deductions", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Approved Adjust Loan Balance Deductions: "+value);
                //fusionFileLoader.sendFusionRequest(params, 11);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        
        
        String finalapproval = null;
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            finalapproval = "Y";
        } else {
            finalapproval = "N";
        }
        Row loanSettlRow =
            ADFUtils.findIterator("LoanSettlement_VOIterator").getCurrentRow();
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"),
                                                  "Loan-Settlement",(Number)nextStep.getAttribute("StepId"),(Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT",finalapproval);
        
        if(nextStep.getAttribute("NextAssignee").equals("Finished")){
        
            if(value.equalsIgnoreCase("success")){
                ADFUtils.findOperation("Commit").execute();
                JSFUtils.addFacesInformationMessage("Request has been approved");
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                            sendEmail(personNumber, loanSettlRow);
                            sendEmailByEmail(fyiEmailAddress,loanSettlRow);
                }
                return "back";
            }else{
                ADFUtils.findOperation("Rollback").execute();
                JSFUtils.addFacesInformationMessage("Something went wrong! please contact HR Administrator!");
                return null;
            }
            }
        else {
            ADFUtils.findOperation("Commit").execute();
            JSFUtils.addFacesInformationMessage("Request has been approved");
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if("POSITION".equals(stepType)){
                    System.err.println("Email to mail-id::"+assigneeEmail);
                    sendEmailByEmail(assigneeEmail, loanSettlRow);    
                }else{
                    System.err.println("Email to user-id::"+assigneeNo);
                    sendEmail(assigneeNo, loanSettlRow);    
                }   
            }
            return "back";
        }
    }
    
    public void sendLoanSettlementEmail(String from, String to,
                              LoanSettlement_VORowImpl subject, String personFYI) {
        
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
        if("Withdrawn".equals(status)){
            subj = "Loan Settlement Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Loan Settlement Request, and below the details";
            
        }else if("Waiting Withdraw Approval".equals(status)){
            subj = "Loan Settlement Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Loan Settlement Request, and below the details";
            
        }else if("Withdrawn Rejected".equals(status)){
            subj = "Loan Settlement Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of Loan Settlement request as below";
            
        }else{
            subj = "Loan Settlement Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Loan Settlement request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("Withdrawn".equals(status)){
                subj = "Loan Settlement Request has been withdraw";
                hdrMsg= "Kindly be informed you that the following Loan Settlement request has been withdraw";
            }
            if("APPROVED".equals(status)){
                subj = "Loan Settlement Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Loan Settlement request has been Approved";
            }
           
        }
//        JSFUtils.addFacesInformationMessage(subj);
//        JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
           // to = "vf.khayal@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
//            "Dear " + (subject.getAssigneeName()!= null ? subject.getAssigneeName() : "Receiver") + "," + "\n" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" +
            hdrMsg +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";
        String PersoneName=subject.getPersonName()!=null?subject.getPersonName():"";
                                            String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                            String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                            String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                            String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
                    String PersonGrade=subject.getPersonGrade()!=null?subject.getPersonGrade():"";
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
            "        &nbsp;Job\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + PersonJob+ "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Grade\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + PersonGrade+ "</td>\n" +
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
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Loan Details</h2></td></tr>"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number of Months\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getLoanNoOfMonths() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Total Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getLoanTotalAmount() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Remaining Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getLoanRemainingAmount() + "</td>\n" +
            "    </tr>"+
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Division\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getLoanDivision() + " </td>\n" +
            "    </tr>" + "      <th>\n" +
            "        &nbsp;Settlement Requested Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCreatedOn() +
            "</td>\n" +
            "    </tr>" + "  </table>";
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryVO1Iterator"); 
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String text =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 +verticalSpace+ApprovalPart1+ thankYouPart +
            signaturePart + "</p>";
        
        System.err.println("MAIL:::"+text);

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        sendMail.getParamsMap().put("subject", subj);
//                                    "Loan Settlement Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
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
        String email = getEmail(personNumber);
        System.out.println("- Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendLoanSettlementEmail("OFOQ.HR@TATWEER.SA",email,
                          (LoanSettlement_VORowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendLoanSettlementEmail("OFOQ.HR@TATWEER.SA", personEmail,
                      (LoanSettlement_VORowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public String onClickReject() {
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String LoanSettlementTypeName="Loan-Settlement" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanSettlementTypeName", LoanSettlementTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", "");
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
         
        Row loanSettlRow =
            ADFUtils.findIterator("LoanSettlement_VOIterator").getCurrentRow();
        
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"),
                                                  "Loan-Settlement",(Number)nextStep.getAttribute("StepId"), new Long(0), null,
                                                  "REJECT_ACT",""); 
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            sendEmail(personNumber, loanSettlRow);
        }
        ADFUtils.findOperation("Commit").execute(); 
        JSFUtils.addFacesInformationMessage("Loan Request has been Rejected");

        return "back";
    }
    
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){

            ViewObject reqVo = ADFUtils.findIterator("LoanSettlement_VOIterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            
            currRow.setAttribute("StepId", "1");
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String LoanSettlementTypeName="Loan-Settlement" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("LoanSettlementTypeName", LoanSettlementTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String emailNotification = (String)nextStep.getAttribute("EmailNotification");
            String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
            
           
          
            UserServiceUtil userService = new UserServiceUtil();
            UserDetails userDetails = null;

            userDetails = userService.getUserDetailsByPersonNumber(personNo);
            List<UserWorkRelationshipDetails> relationshipDetails =
                userDetails.getUserWorkRelationshipDetails();
            String department = relationshipDetails.get(0).getDepartmentName(); 
            
            if("PENDING".equals(status)){
                currRow.setAttribute("StepId", totalStep);
                currRow.setAttribute("RequestStatus", "Withdrawn");
                currRow.setAttribute("ActionTaken", "Withdrawn");
                currRow.setAttribute("Assignee", personNo);
                currRow.setAttribute("AssigneeName", "");
                
                ADFUtils.findOperation("Commit").execute();
                 
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"), 
                                                                             "Loan-Settlement", (Number)nextStep.getAttribute("NextStepId"), 
                                                                             (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                             "WITHDRAW_ACT", "");
                ADFUtils.findOperation("Execute").execute();
                JSFUtils.addFacesInformationMessage("Request Withdrawn Successfully !");
                
            }else{
                
                String assigneeName = "";
                String assigneeNo = "";
                String assigneeEmail = "";
                
                // If step type is POSITION
                if("POSITION".equals(stepType)){
                    System.err.println("Submitted withdraw : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
                    BiReportAccess report = new BiReportAccess();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
                    }
                    if (personData.get(0).get("PERSON_NUMBER") != null) {
                        assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
                    }
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
                    }
                } 
                
                // If step type is DEPARTMENT MANAGER
                if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType)){
                    System.err.println("Submitted withdraw : DEPT_MANAGER :"+department);
                    BiReportAccess BIRA = new BiReportAccess();
                    try {
                        List<Map> managerOfDeptList =
                            BIRA.getManagerOfDepartmentData(department);
                        if (managerOfDeptList.size() > 0) {
                            if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                            }
                            if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            }
                            if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                            } 
                        } else {
                            JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                // If step type is LINE MANAGER
                if("LINE_MANAGER".equals(stepType)){
                    System.err.println("Submitted withdraw : LINE_MANAGER ");
                        String managerNumber =
                            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

                        Long lineManagerID =
                            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
                        String stringLineManagerID = lineManagerID.toString();
                        if (stringLineManagerID == null) {
                            JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not submit the request");
                        }  
                        UserDetails byManagerId =
                            userService.getUserDetailsByPersonId(lineManagerID);
                        String managerName =
                            byManagerId.getUserPersonDetails().get(0).getDisplayName().getValue(); 
                        String managerEmail =
                            byManagerId.getUserPersonDetails().get(0).getEmailAddress().getValue();
                     
                    assigneeName = managerName;
                    assigneeNo = managerNumber;
                    assigneeEmail = managerEmail;
                }
                // If step type is USER
                if("USER".equals(stepType)){
                    System.err.println("Submitted withdraw : USER ");
                    ArrayList<String> personDetails = new ArrayList<String>();
                    personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
                    assigneeName = personDetails.get(0).toString();
                    assigneeEmail = personDetails.get(1).toString();
                    assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
                } 
                
                System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
                System.err.println("withdraw Assigning to (name) ::"+assigneeName);
                System.err.println("withdraw Assigning to (no) ::"+assigneeNo);
                System.err.println("withdraw Assigning to (email) ::"+assigneeEmail);
                
                //  
                currRow.setAttribute("StepId", nextStep.getAttribute("NextStepId"));
                currRow.setAttribute("RequestStatus", "Waiting Withdraw Approval");
                currRow.setAttribute("ActionTaken", "Waiting Withdraw Approval");
                currRow.setAttribute("Assignee", assigneeNo);
                currRow.setAttribute("AssigneeName", assigneeName);
                
                ADFUtils.findOperation("Commit").execute();
                 
                String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"), 
                                                                             "Loan-Settlement", (Number)nextStep.getAttribute("NextStepId"), 
                                                                             (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                             "SUMBIT_ACT", "");
                
                if("POSITION".equals(stepType)){
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                        if (assigneeEmail == null) {
                            JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver");
                        }else{
                          sendEmailByEmail(assigneeEmail, currRow);
                        } 
                    }
                }
                if(! "POSITION".equals(stepType)){
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                        if (assigneeNo == null) {
                            JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver..");
                        }else{
                          sendEmail(assigneeNo, currRow);
                        } 
                    }
                }
            }
            ADFUtils.findOperation("Execute").execute();
        }
    }
    
    public String reject_withdraw() {
        ViewObject reqVo = ADFUtils.findIterator("LoanSettlement_VOIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personNo = ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String LoanSettlementTypeName="Loan-Settlement" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanSettlementTypeName", LoanSettlementTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        
        String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        currRow.setAttribute("StepId", totalStep);
        currRow.setAttribute("RequestStatus", "Withdrawn Rejected");
        currRow.setAttribute("ActionTaken", "Withdrawn Rejected");
        currRow.setAttribute("Assignee", personNo);
        currRow.setAttribute("AssigneeName", "");
        
        ADFUtils.findOperation("Commit").execute();
        String value =
            ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"),
                                                      "Loan-Settlement",
                                                 (Number)nextStep.getAttribute("StepId"),
                                                  new Long(0),
                                                  null,
                                                  "REJECT_ACT", "");
        
        JSFUtils.addFacesInformationMessage("Withdrawn Request Rejected !");
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if (personNo == null) {
                JSFUtils.addFacesErrorMessage("You donot have Person Email, So mail can't be sent to the person..");
            }else{
              sendEmail(personNo, currRow);
            } 
        }
        
        return "back";
    }
    
    public String approve_withdraw() {
        
        ViewObject reqVo = ADFUtils.findIterator("LoanSettlement_VOIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String LoanSettlementTypeName="Loan-Settlement" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanSettlementTypeName", LoanSettlementTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";   
        
        String personNumber = ADFUtils.getBoundAttributeValue("PersonNumber").toString();
         
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
        
        if("POSITION".equals(stepType)){
            System.err.println("Approved withdraw, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
            BiReportAccess report = new BiReportAccess(); 
            List<Map> personData2 = null;
            try {
                personData2 = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (personData2.get(0).get("DISPLAY_NAME") != null) {
                assigneeName = personData2.get(0).get("DISPLAY_NAME").toString();
            }
            if (personData2.get(0).get("PERSON_NUMBER") != null) {
                assigneeNo = personData2.get(0).get("PERSON_NUMBER").toString();
            }
            if (personData2.get(0).get("EMAIL_ADDRESS") != null) {
                assigneeEmail = personData2.get(0).get("EMAIL_ADDRESS").toString();
            }
        }
        // If step type is DEPARTMENT MANAGER
            if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType)){
                    System.err.println("Approved withdraw, Assigning to : : DEPT_MANAGER :"+ department1);
                    BiReportAccess BIRA = new BiReportAccess();
                    try {
                        List<Map> managerOfDeptList =
                        BIRA.getManagerOfDepartmentData(department1);
                        if (managerOfDeptList.size() > 0) {
                            if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                            }
                            if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            }
                            if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                            }
                        }
                        else
                        {
                            JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                            return null;
                        }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
               }
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType)){
                System.err.println("Approved withdraw, Assigning to : : LINE_MANAGER ");     
                JAXBElement<String> aXBElement = null;
                if (managerDetails1.getUserPersonDetails().get(0).getDisplayName() !=
                    null) {
                    aXBElement =
                        managerDetails1.getUserPersonDetails().get(0).getDisplayName();
            }
                assigneeName = aXBElement.getValue();
                assigneeNo = managerNumber1;
        } 
        // If step type is USER
        if("USER".equals(stepType)){
            System.err.println("Approved withdraw, Assigning to : : USER ");
            ArrayList<String> personDetails = new ArrayList<String>();
            personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
            assigneeName = personDetails.get(0).toString();
            assigneeEmail = personDetails.get(1).toString();
            assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
        } 
        System.err.println("Withdraw Assigning to (name) ::"+assigneeName);
        System.err.println("Withdraw Assigning to (no) ::"+assigneeNo);
        System.err.println("Withdraw Assigning to (email) ::"+assigneeEmail);
        
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "Waiting Withdraw Approval");
        ADFUtils.setBoundAttributeValue("ActionTaken", "Waiting Withdraw Approval"); 
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        
        if (nextStep.getAttribute("NextAssignee").equals("Finished")){
            
            ADFUtils.setBoundAttributeValue("RequestStatus", "Withdrawn");
            ADFUtils.setBoundAttributeValue("ActionTaken", "Withdrawn");
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            
            String remainingAmt =
                    ADFUtils.getBoundAttributeValue("LoanRemainingAmount").toString();
            String createdOn =
                    ADFUtils.getBoundAttributeValue("CreatedOn").toString();
            String formattedCreatedOn= createdOn.replace('-', '/');
                
            BiReportAccess report = new BiReportAccess();
            List<Map> datFileData = null;
            try {
                datFileData = report.getAdjustLoanBalance(personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String assignmentNumber =
                datFileData.get(0).get("ASSIGNMENT_NUMBER").toString();
            String entryCount =
                datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString(); 
            Integer mpCount = Integer.parseInt(entryCount) - 1;
            
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("Date", formattedCreatedOn);
            params.put("AssignmentNo", assignmentNumber);
            params.put("Count", mpCount.toString() );
            params.put("RemainingAmount", remainingAmt);
            System.err.println("Fusion upload calling for LoanSettlemenWITHDRAW : "+params);
            
            try {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 16);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LoanId").toString(), personNumber, "Withdrawn", "Adjust Loan Balance Deductions", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Withdrawn Adjust Loan Balance Deductions: "+value);
                //fusionFileLoader.sendFusionRequest(params, 16);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
             
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"),
                                                      "Loan-Settlement",(Number)nextStep.getAttribute("StepId"),
                                                       (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                       (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                     sendEmailToPerson(personNumber, currRow);
                     sendEmailByEmail(fyiEmailAddress, currRow);
            } 
        }else{ 
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanPreclosureId"),
                                                       "Loan-Settlement",(Number)nextStep.getAttribute("StepId"),
                                                       (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                       (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    if("POSITION".equals(stepType)){
                        sendEmailByEmail(assigneeEmail, currRow);    
                    }else{
                        sendEmail(assigneeNo, currRow);    
                    }   
            } 
        }
      
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Withdrawn Request Approved !");
        return "back";
        }
    
    public void sendEmailToPerson(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendLoanSettlementEmail("OFOQ.HR@TATWEER.SA", email,
                                     (LoanSettlement_VORowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    private String changeFormatOfDate(String fromFormat, String toFormat, String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        java.util.Date date = formatter.parse(strDate);
        SimpleDateFormat ft = new SimpleDateFormat(toFormat);
        return ft.format(date);
     }
    
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("LoanSettlement_VOIterator").getViewObject();
        vo.applyViewCriteria(null);
        vo.executeQuery();
        
        if ("MyTasks".equals(statusType)) {
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("PersonName", person);
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FilterSearch"));
            vo.executeQuery();
        }
    }

}
