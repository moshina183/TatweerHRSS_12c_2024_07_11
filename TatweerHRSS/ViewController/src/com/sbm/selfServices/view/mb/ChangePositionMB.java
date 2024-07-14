package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.ChangePositionViewRowImpl;
import com.sbm.selfServices.model.views.up.ExitReEntryVORowImpl;
import com.sbm.selfServices.model.views.up.GradesVORowImpl;
import com.sbm.selfServices.model.views.up.ManPowerRequestViewRowImpl;
import com.sbm.selfServices.model.views.up.PositionsVORowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;

import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStripApp.FilmStripBean;

import java.math.BigInteger;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.TimeUnit;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.JAXBElement;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;

public class ChangePositionMB {
    private FusionDataLoader fusionFileLoader;
    private RichInputText otherValue;

    public ChangePositionMB() {
    }

    public String newRequest() {
        // Add event code here...
        
        
        JSFUtils.setExpressionValue("#{pageFlowScope.cPPersonNummber}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.cPPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.currentPosition}",
                                    JSFUtils.resolveExpression("#{PersonInfo.position}"));


        BiReportAccess BIRA = new BiReportAccess();
        List<Map> list = null;


        try {
            list = BIRA.getPositionsData();

            DCIteratorBinding positionsIter =
                ADFUtils.findIterator("PositionsVO1Iterator");
            ViewObject positionsView = positionsIter.getViewObject();
            PositionsVORowImpl positionRow;
            if (list.size() > 0) {
                ADFUtils.findOperation("deletePositionsTableRows").execute();
                System.out.println("All Positions table rows deleted successfully");


                for (Map currentPosition : list) {


                    positionRow = (PositionsVORowImpl)positionsView.createRow();
                    positionRow.setId((Integer)currentPosition.get("ID"));
                    positionRow.setPositionId(currentPosition.get("POSITION_ID").toString());
                    positionRow.setName(currentPosition.get("NAME").toString());
                    positionsView.insertRow(positionRow);

                    ADFUtils.findOperation("Commit").execute();


                }

                positionsView.executeQuery();
                positionsIter.executeQuery();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        
        return "newChangePosition";
    }

    public String saveRequest() {
        // Add event code here...
        Object employeeNumber =
            JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        System.err.println("employeeNumber is >>>>  " + employeeNumber);
        if (employeeNumber == null) {
            JSFUtils.addFacesErrorMessage("You don't have Person Number, So you can't save the request");
            return null;
        }

        ADFUtils.setBoundAttributeValue("ActionTaken", "SAVED");
        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Change Position Title Request has been saved");
        ADFUtils.findOperation("Execute").execute();
        
        return "back";
    }

    public String submitRequest()  {
        // Add event code here...
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String ChangePositionTitleTypeName="ChangePositionTitle" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ChangePositionTitleTypeName", ChangePositionTitleTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        
        String managerNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

        Long lineManagerID =
            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        String stringLineManagerID = lineManagerID.toString();
        
//        if (stringLineManagerID == null) {
//            JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not submit the request");
//            return null;
//        }
//        ADFUtils.setBoundAttributeValue("Assignee", managerNumber);
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");

        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;

        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();


        String position = relationshipDetails.get(0).getPositionName();
        ADFUtils.setBoundAttributeValue("PersonPosition", position);
        String department = relationshipDetails.get(0).getDepartmentName();
        ADFUtils.setBoundAttributeValue("PersonDepartment", department);
        String job = relationshipDetails.get(0).getJobName().getValue();
        ADFUtils.setBoundAttributeValue("PersonJob", job);
        String location = relationshipDetails.get(0).getLocationName();
        ADFUtils.setBoundAttributeValue("PersonLocation", location);
        String gradeCode = relationshipDetails.get(0).getGradeCode();
        System.err.println("gradeCode is >>> " + gradeCode);
        ADFUtils.setBoundAttributeValue("PersonGrade", gradeCode);
        
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
        
        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));


//        System.out.println("Manager Number Is >>>> " + managerNumber);
//        UserDetails byManagerId =
//            userService.getUserDetailsByPersonId(lineManagerID);
//        String managerName =
//            byManagerId.getUserPersonDetails().get(0).getDisplayName().getValue();
//        System.out.println("Manager Name Is >>>> " + managerName);
//        ADFUtils.setBoundAttributeValue("AssigneeName", managerName);
//        String managerEmail =
//            byManagerId.getUserPersonDetails().get(0).getEmailAddress().getValue();
//        System.out.println("Manager Email Is >>>> " + managerEmail);
//        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
        
        ADFUtils.findOperation("Commit").execute();
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
                                                        "ChangePosition",
                                                          (Number)nextStep.getAttribute("NextStepId"),
                                                          stepid.longValue(),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          act, "N");
        Row changePositionRow =
            ADFUtils.findIterator("ChangePositionView1Iterator").getCurrentRow();
        JSFUtils.addFacesInformationMessage("Request has been submitted");
//        ADFUtils.findOperation("Execute").execute();
//        if (managerEmail == null) {
//            JSFUtils.addFacesErrorMessage("You don't have Line Manager Email, So mail can't be sent to manager");
//            return "back";
//        }
//        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//
//               sendEmailByEmail(managerEmail, changePositionRow);
//        }
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, changePositionRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, changePositionRow);    
            } 
        }
        return "back";
    }

    public String approveRequest() {
        // Add event code here...
        int updateInsert=0;
        String personMail = null;
        Row changePositionRow =
            ADFUtils.findIterator("ChangePositionView1Iterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ChangePositionTitleTypeName="ChangePositionTitle" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ChangePositionTitleTypeName", ChangePositionTitleTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        String personNo=
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        System.err.println("pervoius==="+nextStep.getAttribute("StepId"));
        System.err.println("Update===>"+stepid);
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        userDetails = userService.getUserDetailsByPersonNumber(personNo);
        
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
                   List<UserWorkRelationshipDetails> relationshipDetails =
                       userDetails.getUserWorkRelationshipDetails(); 
                   String department1 = relationshipDetails.get(0).getDepartmentName(); 
                   
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
                
            List<UserWorkRelationshipDetails> relationshipDetails =
                userDetails.getUserWorkRelationshipDetails();
            Long lineManagerID = relationshipDetails.get(0).getManagerId();
            UserDetails managerDetails =
                userService.getUserDetailsByPersonId(lineManagerID);
            String managerNumber = managerDetails.getPersonNumber();
            JAXBElement<String> aXBElement = null;
            if (managerDetails.getUserPersonDetails().get(0).getDisplayName() !=
                null) {
                aXBElement =
                    managerDetails.getUserPersonDetails().get(0).getDisplayName();
            }
            String managerEmail =
                managerDetails.getUserPersonDetails().get(0).getEmailAddress().getValue();
            
            assigneeName = aXBElement.getValue();
            assigneeNo = managerNumber;
            assigneeEmail = managerEmail;
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
        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
/*
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("Assignee",
                                        nextStep.getAttribute("NextAssignee"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
        if (nextStep.getAttribute("NextAssignee").equals("Department Manager")) {
            
            String department=ADFUtils.getBoundAttributeValue("PersonDepartment").toString();
                String managerOfDeptNum = null;
                String managerOfDeptName = null;
                String managerOfDeptEmail = null;
                if (department != null) {

                    BiReportAccess BIRA = new BiReportAccess();


                    try {
                        List<Map> managerOfDeptList =
                            BIRA.getManagerOfDepartmentData(department);
                        if (managerOfDeptList.size() > 0) {
                            managerOfDeptNum =
                                    managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                            managerOfDeptName =
                                    managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            managerOfDeptEmail =
                                    managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();

                        } else {
                            JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                            return null;

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (managerOfDeptNum != null)

                    {
                       

                        ADFUtils.setBoundAttributeValue("Assignee", managerOfDeptNum);
                        ADFUtils.setBoundAttributeValue("AssigneeName", managerOfDeptName);
                        
                    }


                }
                else
                {
                        JSFUtils.addFacesErrorMessage("There is no Department for Employee so you can't Approve the request");
                        return null;

                    
                    }
                ADFUtils.findOperation("Commit").execute();
                System.err.println("stepid"+nextStep.getAttribute("StepId")+"current stepid==>"+ ADFUtils.getBoundAttributeValue("StepId"));
//                String returnvalue=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
//                                                               "ChangePosition",
//                                                               (Number)nextStep.getAttribute("StepId"),
//                                                             stepid.longValue(),
//                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                             "APPROVE_ACT", "N");
//                updateInsert=1;
                
            
                if (managerOfDeptEmail != null) {
                    personMail = managerOfDeptEmail;
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    sendEmailByEmail(personMail, changePositionRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Manager of Department has no email");

                }
            
            }
        
        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {

            BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;

            try {
                personData = report.getPersonByPostionReport("HR and Admin Director");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (personData.size() > 0) {
                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
                    System.out.println("HR and Admin Director Number is " +
                                       personData.get(0).get("PERSON_NUMBER"));
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    System.out.println("HR and Admin Director Name is " +
                                       personData.get(0).get("DISPLAY_NAME"));

                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME"));
                }

                else {
                    JSFUtils.addFacesErrorMessage("You can't approve request as HR and Admin Director Name is empty");
                    return null;
                }
                ADFUtils.findOperation("Commit").execute();
                System.err.println("stepid"+nextStep.getAttribute("StepId")+"current stepid==>"+ stepid);
                String returnvalue=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
                                                               "ChangePosition",
                                                               (Number)nextStep.getAttribute("StepId"),
                                                             stepid.longValue(),
                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                             "APPROVE_ACT", "N");
                updateInsert=1;
               
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                     sendEmailByEmail(personMail, changePositionRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR and Admin Director dosn't has email");

                }
            }
        }
//        Director of IT & C
        if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")) {

            BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;

            try {
                personData =
                        report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (personData.size() > 0) {

                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    ADFUtils.setBoundAttributeValue("Assignee",
                                                    personData.get(0).get("PERSON_NUMBER").toString());
                    System.out.println("Executive Director, Shared Services Sector (Acting) Number is " +
                                       personData.get(0).get("PERSON_NUMBER"));
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    System.out.println("Executive Director, Shared Services Sector (Acting) Name is " +
                                       personData.get(0).get("DISPLAY_NAME"));

                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME"));
                }

                else {
                    JSFUtils.addFacesErrorMessage("You can't approve request as Executive Director, Shared Services Sector (Acting) Name is empty");
                    return null;
                }
                ADFUtils.findOperation("Commit").execute();
                System.err.println("stepid"+nextStep.getAttribute("StepId")+"current stepid==>"+ stepid);
                String returnvalue=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
                                                               "ChangePosition",
                                                               (Number)nextStep.getAttribute("StepId"),
                                                             stepid.longValue(),
                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                             "APPROVE_ACT", "N");
                updateInsert=1;
                ADFUtils.findOperation("Commit").execute();
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    sendEmailByEmail(personMail, changePositionRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");

                }
            }


        }
        
*/      
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            BiReportAccess BIRA = new BiReportAccess();
            String creationDate = changePositionRow.getAttribute("CreationDate").toString(); 
            creationDate = creationDate.replace('-', '/');
            
            String personNumber =
                (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
            System.out.println("the person number is ==============> " +
                               personNumber);
            ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
            
            
            List<Map> data=null;
            try {
                data = BIRA.getPositionWorkerData(personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            String PosCode=null;
            String PosName=null;
           String proposedPositionHr = null;
            if(changePositionRow.getAttribute("ProposedPositionHr").toString() == null){
                JSFUtils.addFacesErrorMessage("Please select the propsed position HR.");
                System.out.println("=======inside the check==>");
            }
            else{
                 proposedPositionHr = changePositionRow.getAttribute("ProposedPositionHr").toString();
                System.err.println("=========>"+proposedPositionHr);
            }
          
           if(proposedPositionHr.equalsIgnoreCase("Other"))
           {

                String other = changePositionRow.getAttribute("Other").toString();
                if(other!=null)
                {
                        HashMap<String, String> params = new HashMap<String, String>();
                        PosCode ="TATWEERHRSS_"+(new Date()).getTime();
                        PosName=other;
                        
                        params.put("PosCode", PosCode);
                        params.put("PosName", PosName);
                        params.put("DeptID", data.get(0).get("ORGANIZATION_ID").toString());
                        params.put("JobID", data.get(0).get("JOB_ID").toString());
                        params.put("LocationID", data.get(0).get("LOCATION_ID").toString());
                        params.put("GradeID", data.get(0).get("GRADE_ID").toString());

                        try {
                            fusionFileLoader = new FusionDataLoader();
                            
                            Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 5);
                            String value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("ChangePositionId").toString(), personNumber, "Approved", "Position", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                            System.out.println("TAT HDR Updated Value - Approved Position: "+value1);
                            //fusionFileLoader.sendFusionRequest(params, 5);
                        } catch (Exception e) {
                            ADFUtils.findOperation("Rollback").execute();
                            e.printStackTrace();
                        }
                        
                        try {
                            TimeUnit.SECONDS.sleep(70);
                        } catch (InterruptedException e) {
                            ADFUtils.findOperation("Rollback").execute();
                            e.printStackTrace();
                        }
                        
                        List<Map> data1=null;
                        try {
                            data1 = BIRA.getPositionId(PosName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String positionId=null;
                        
                        if(data1.get(0).get("POSITION_ID")!=null){
                        
                         positionId=data1.get(0).get("POSITION_ID").toString();
                        
                        }
                        
                        else{
                                try {
                                    TimeUnit.SECONDS.sleep(30);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            
                                try {
                                    data1 = BIRA.getPositionId(PosName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(data1.get(0).get("POSITION_ID")!=null){
                                
                                 positionId=data1.get(0).get("POSITION_ID").toString();
                                
                                }
                            }
                        
                        if(positionId!=null){
                        System.err.println("New Position Id is >>>>  "+positionId);
                        
                        
                            HashMap<String, String> params1 = new HashMap<String, String>();

                            params1.put("WorkTermAssignmentId", data.get(0).get("WORK_TERM_ASSIGNMENT_ID").toString());
                            params1.put("ActionCode", data.get(0).get("ACTION_CODE").toString());
                            params1.put("AssignmentId", data.get(0).get("ASSIGNMENT_ID").toString());
                            params1.put("WorkTermsNumber", data.get(0).get("WORK_TERM_NUMBER").toString());
                            params1.put("AssignmentType", data.get(0).get("ASSIGNMENT_TYPE").toString());
                            params1.put("EffectiveLatestChange", data.get(0).get("EFFECTIVE_LATEST_CHANGE").toString());
                            params1.put("EffectiveSequence", data.get(0).get("EFFECTIVE_SEQUENCE").toString());
                            params1.put("EffectiveStartDate", creationDate);
                            params1.put("PeriodOfServiceId", data.get(0).get("PERIOD_OF_SERVICE_ID").toString());
                            params1.put("PersonId", data.get(0).get("PERSON_ID").toString());
                            params1.put("PersonNumber", data.get(0).get("PERSON_NUMBER").toString());
                            params1.put("DateStart", data.get(0).get("DATE_START").toString());
                            params1.put("WorkerType", data.get(0).get("WORKER_TYPE").toString()); 
                            params1.put("PositionId", positionId);
                            params1.put("PrimaryWorkTermsFlag", data.get(0).get("PRIMARY_WORK_TERMS_FLAG").toString());
                            params1.put("AssignmentNumber", data.get(0).get("ASSIGNMENT_NUMBER").toString());
                            params1.put("PrimaryAssignmentFlag", data.get(0).get("PRIMARY_ASSIGNMENT_FLAG").toString());
                            

                            try {
                                fusionFileLoader = new FusionDataLoader();
                                Map<String, String> map =  fusionFileLoader.sendFusionRequest(params1, 6);
                                String value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("ChangePositionId").toString(), personNumber, "Approved", "WorkTerms", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                System.out.println("TAT HDR Updated Value - Approved WorkTerms: "+value1);
                                //fusionFileLoader.sendFusionRequest(params1, 6);
                            } catch (Exception e) {
                                ADFUtils.findOperation("Rollback").execute();
                                e.printStackTrace();
                            }
                        
                        
                        }
                        else
                        {
                            
                                JSFUtils.addFacesErrorMessage("You can't approve this request as the new position wasn't created successfully");
                            return null;
                            
                            }
                        
                    
                    }
                else
                {
                    
                        JSFUtils.addFacesErrorMessage("You can't approve this request as Other field must has a value");
                    return null;
                    
                    
                    }

            }else
             {
                 
                     List<Map> data1=null;
                     try {
                         data1 = BIRA.getPositionId(proposedPositionHr);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     
                     String positionId=data1.get(0).get("POSITION_ID").toString();
                     HashMap<String, String> params1 = new HashMap<String, String>();

                     params1.put("WorkTermAssignmentId", data.get(0).get("WORK_TERM_ASSIGNMENT_ID").toString());
                     params1.put("ActionCode", data.get(0).get("ACTION_CODE").toString());
                     params1.put("AssignmentId", data.get(0).get("ASSIGNMENT_ID").toString());
                     params1.put("WorkTermsNumber", data.get(0).get("WORK_TERM_NUMBER").toString());
                     params1.put("AssignmentType", data.get(0).get("ASSIGNMENT_TYPE").toString());
                     params1.put("EffectiveLatestChange", data.get(0).get("EFFECTIVE_LATEST_CHANGE").toString());
                     params1.put("EffectiveSequence", data.get(0).get("EFFECTIVE_SEQUENCE").toString());
                     params1.put("EffectiveStartDate", data.get(0).get("EFFECTIVE_START_DATE").toString());
                     params1.put("PeriodOfServiceId", data.get(0).get("PERIOD_OF_SERVICE_ID").toString());
                     params1.put("PersonId", data.get(0).get("PERSON_ID").toString());
                     params1.put("PersonNumber", data.get(0).get("PERSON_NUMBER").toString());
                     params1.put("DateStart", data.get(0).get("DATE_START").toString());
                     params1.put("WorkerType", data.get(0).get("WORKER_TYPE").toString()); 
                     params1.put("PositionId", positionId);
                     params1.put("PrimaryWorkTermsFlag", data.get(0).get("PRIMARY_WORK_TERMS_FLAG").toString());
                     params1.put("AssignmentNumber", data.get(0).get("ASSIGNMENT_NUMBER").toString());
                     params1.put("PrimaryAssignmentFlag", data.get(0).get("PRIMARY_ASSIGNMENT_FLAG").toString());
                     
                     

                     try {
                         fusionFileLoader = new FusionDataLoader();
                         Map<String, String> map = fusionFileLoader.sendFusionRequest(params1, 6);
                         String value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("ChangePositionId").toString(), personNumber, "Approved", "WorkTerms", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                         System.out.println("TAT HDR Updated Value - Approved WorkTerms: "+value1);
                         //fusionFileLoader.sendFusionRequest(params1, 6);
                     } catch (Exception e) {
                         e.printStackTrace();
                     } 
                    
                 
                 }
           
           
         
            
            String returnvalue=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
                                                           "ChangePosition",
                                                           (Number)nextStep.getAttribute("StepId"),
                                                         stepid.longValue(),
                                                         (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                         "APPROVE_ACT", "Y");
            updateInsert=1;   
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            
                sendEmailToPerson(personNumber, changePositionRow);
                sendEmailByEmail(fyiEmailAddress, changePositionRow);
                //sendEmailForChangePositionEmployee(fyiEmailAddress,changePositionRow);
            }

        }


        
      
        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
        ADFUtils.findOperation("Commit").execute();
        String finalapproval = null;
       
        if(nextStep.getAttribute("NextAssignee").equals("Finished"))
        {
            finalapproval="Y";
        }
        else{
            finalapproval="N";
        }
       
        if(updateInsert==0)
        {
           Integer stepid1 = (Integer)ADFUtils.getBoundAttributeValue("StepId");
           ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
                                                       "ChangePosition",
                                                       (Number)nextStep.getAttribute("StepId"),
                                                     stepid1.longValue(),
                                                     (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                     "APPROVE_ACT", finalapproval);
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if("POSITION".equals(stepType)){
                    System.err.println("Email to mail-id::"+assigneeEmail);
                    sendEmailByEmail(assigneeEmail, changePositionRow);    
                }else{
                    System.err.println("Email to user-id::"+assigneeNo);
                    sendEmail(assigneeNo, changePositionRow);    
                }   
            }
           
        }
         
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";
    }

    public String rejectRequest() {
        // Add event code here...
        String personNumber =
            JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}").toString();
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        Row changePositionRow =
            ADFUtils.findIterator("ChangePositionView1Iterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ChangePositionTitleTypeName="ChangePositionTitle" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ChangePositionTitleTypeName", ChangePositionTitleTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
//        UserServiceUtil userService = new UserServiceUtil();
//        UserDetails byPersonNumber =
//            userService.getUserDetailsByPersonNumber(personNumber);
       
        ADFUtils.setBoundAttributeValue("Assignee",
                                       personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ADFUtils.findOperation("Commit").execute();
        String value =
                       ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"),
                                                                 "ChangePosition",
                                                            (Number)nextStep.getAttribute("StepId"),
                                                             new Long(0),
                                                             null,
                                                             "REJECT_ACT", "");
        if(value!=null && value.equalsIgnoreCase("Success")) {
            System.err.println("rejected"+value);
            ADFUtils.findOperation("Commit").execute();
        }
        else {
            System.err.println("Issue");
        }
        JSFUtils.addFacesInformationMessage("Request has been Rejected");
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
          sendEmail(personNumber, changePositionRow);
        }
        return "back";
    }
    
    public String getEmail(String personNumber) {
        JAXBElement<String> aXBElement = null;
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);
        if (userDetails.getUserPersonDetails().get(0).getEmailAddress() !=
            null) {
            aXBElement =
                    userDetails.getUserPersonDetails().get(0).getEmailAddress();
        } else {
            JSFUtils.addFacesErrorMessage("Person Number (" + personNumber +
                                          ") has no mail");
            return null;
        }

        return aXBElement.getValue();
    }
    
    public void sendEmail(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendChangePositionTitleEmail("OFOQ.HR@TATWEER.SA", email,
                              (ChangePositionViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }
    
    
    public void sendEmailByEmail(String personEmail, Row subject) {

        sendChangePositionTitleEmail("OFOQ.HR@TATWEER.SA", personEmail,
                          (ChangePositionViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    public void sendChangePositionTitleEmail(String from, String to,
                                  ChangePositionViewRowImpl subject, String personFYI) {
        
        String status = subject.getRequestStatus();
        String edited = subject.getEditRequest();
        String requesterName = subject.getPersonName();
        String assigneeName = "Sir";
        if(personFYI!= null && "Y".equals(personFYI)){
            assigneeName = requesterName;
        }else{
            if(subject.getAssigneeName()!=null && ! "".equals(subject.getAssigneeName())){
                assigneeName = subject.getAssigneeName();
            }
        }
        
        String subj = "";
        String hdrMsg = "";
        
        if(edited!=null && edited.equals("Y") && "PENDING".equals(status)){
            subj = "Change Position Title Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Change Position Title Request, and below the details";
            
        }else{
            subj = "Change Position Title Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Change Position Title request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("APPROVED".equals(status)){
                subj = "Change Position Title Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Change Position Title request has been Approved";
            }
        }
//        JSFUtils.addFacesInformationMessage(subj);
//        JSFUtils.addFacesInformationMessage(hdrMsg);
        
            if (to == null) {
               // to = "heleraki@gmail.com";
               JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

            }
            
            //String toPart = "Dear Sir," + "<br/><br/>";
            String bodyPart =
                "<p align=\"left\" style=\"text-align:left\">\n" +
                "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
                "Dear " + assigneeName + "," + "\n" +
                "<br/>" +
                hdrMsg +
                "  </span></p>\n" +
                "  <p>&nbsp;</p>";
        String proPosition=subject.getProposedPositionEmp()!=null ?subject.getProposedPositionEmp():"";
        String proPositionByhR  = subject.getProposedPositionHr()!=null?subject.getProposedPositionHr():"";
        String proPositionOther   =subject.getOther()!=null?subject.getOther():"";
        String PersoneName=subject.getPersonName()!=null?subject.getPersonName():"";
                                 String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                 String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                 String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                 String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
         String PersonGrade=subject.getPersonGrade()!=null?subject.getPersonGrade():"";
         String details= subject.getRequestDetails()!=null? subject.getRequestDetails():"";
         String effStartDate= subject.getEffectiveStartDate()!=null? subject.getEffectiveStartDate().toString():"";
            String bodyPart10 =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                "    <tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                subject.getPersonNumber() + "</td></tr>\n" +
                "    <tr><th>&nbsp;Name</th>\n" +
                "      <td width=\"50%\">" + PersoneName + "</td>\n" +
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
                "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Grade\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + PersonGrade + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Department\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + PersonDepartment +
                "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Location\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" +PersonLocation +
                "</td>\n" +
                "    </tr>\n" +
                "  </table>";

            String bodyPart11 = " <p>&nbsp;</p>";
     String bodyRequest=" <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Request Details</h2></td></tr>" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Request Detail\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" + details + "</td>\n" +
        "    </tr>"+
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Effective Start Date\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" + effStartDate + "</td>\n" +
        "    </tr>"+
        " </table>";     
     
            String bodyPart12 =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Change Position Details</h2></td></tr>" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Current Position\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + subject.getCurrentPosition() + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Proposed Position By Employee\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + proPosition + " </td>\n" +
                "    </tr>" + "      <th>\n" +
                "        &nbsp;Proposed Position By HR Director\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + proPositionByhR +
                "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Other Position Title\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" +proPositionOther +
                "</td>\n" +
                "    </tr>"+  " </table>";
                


        String verticalSpace = " <p>&nbsp;</p>";
        
        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryChangePositionIterator");
            String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
            String signaturePart =
                "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
                "<b/>" + "<br/>";
            String text =
                bodyPart + bodyPart10 + bodyPart11 +bodyRequest+verticalSpace+ bodyPart12 +verticalSpace  +ApprovalPart1+thankYouPart+
                signaturePart + "</p>";
                  System.err.println("text===="+text);
            OperationBinding sendMail =
                ADFUtils.findOperation("callSendEmailStoredPL");
            sendMail.getParamsMap().put("sender", from);
            sendMail.getParamsMap().put("receiver",
                                        (to ));
            //        sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
            sendMail.getParamsMap().put("subject", subj);
//                                        "Change Position Title Request " + subject.getRequestStatus());
            sendMail.getParamsMap().put("e_body", text);
            sendMail.execute();
    }
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }
   

    public void onChangePropesedHR(ValueChangeEvent valueChangeEvent) {
        String proposedHR=(String)valueChangeEvent.getNewValue();
        
        if(proposedHR!=null && proposedHR.equalsIgnoreCase("Other")) {
            otherValue.setValue(null);
            otherValue.setVisible(true);
            otherValue.setRequired(true);
            
        }
        else {
            otherValue.setRequired(false);
            otherValue.setVisible(false);
            otherValue.setValue(null);
        }
        
    }

    public void setOtherValue(RichInputText otherValue) {
        this.otherValue = otherValue;
    }

    public RichInputText getOtherValue() {
        return otherValue;
    }

    public void sendEmailForChangePositionEmployee(String email,
                                                   Row subject) {
//        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendChangePositionTitleEmailForFyI("OFOQ.HR@TATWEER.SA", email,
                                               (ChangePositionViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendChangePositionTitleEmailForFyI(String from, String to,
                                                   ChangePositionViewRowImpl subject) {
        if (to == null) {
          //  to = "heleraki@gmail.com";
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String proPosition=subject.getProposedPositionEmp()!=null ?subject.getProposedPositionEmp():"";
                    String proPositionByhR  = subject.getProposedPositionHr()!=null?subject.getProposedPositionHr():"";
                    String proPositionOther   =subject.getOther()!=null?subject.getOther():"";
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Change Position Title request has been Approved" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

       
        String PersoneName=subject.getPersonName()!=null?subject.getPersonName():"";
                                 String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                 String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                 String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                 String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
         String PersonGrade=subject.getPersonGrade()!=null?subject.getPersonGrade():"";
         String details= subject.getRequestDetails()!=null? subject.getRequestDetails():"";
            String bodyPart10 =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                "    <tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                subject.getPersonNumber() + "</td></tr>\n" +
                "    <tr><th>&nbsp;Name</th>\n" +
                "      <td width=\"50%\">" + PersoneName + "</td>\n" +
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
                "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Grade\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + PersonGrade + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Department\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + PersonDepartment +
                "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Location\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" +PersonLocation +
                "</td>\n" +
                "    </tr>\n" +
                "  </table>";

            String bodyPart11 = " <p>&nbsp;</p>";
        String bodyRequest=" <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Request Details</h2></td></tr>" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Request Detail\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" + details + "</td>\n" +
        "    </tr>"+  "</table>";                
            String bodyPart12 =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Change Position Details</h2></td></tr>" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Current Position\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + subject.getCurrentPosition() + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Proposed Position By Employee\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + proPosition + " </td>\n" +
                "    </tr>" + "      <th>\n" +
                "        &nbsp;Proposed Position By HR Director\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + proPositionByhR +
                "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Other Position Title\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" +proPositionOther +
                "</td>\n" +
                "    </tr>"+  " </table>";
        String verticalSpace = " <p>&nbsp;</p>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryChangePositionIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String text =
            bodyPart + bodyPart10 + bodyPart11 +bodyRequest+verticalSpace+ bodyPart12 +verticalSpace  +ApprovalPart1+thankYouPart+
            signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", (to));
        //        sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Change Position Title Request " +
                                    subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
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

    public String onCilckEditRequest() {
        
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("ChangePositionView1Iterator").getViewObject();
        Row r = vo.getCurrentRow();
        if(r.getAttribute("RequestStatus") != null && 
                ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")))){
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }else{
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                 personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            String ChangePositionTitleTypeName="ChangePositionTitle" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ChangePositionTitleTypeName", ChangePositionTitleTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
        } 
        return "edit";
    }
    
    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("ChangePositionView1Iterator").getViewObject();
        Row currRow = reqVo.getCurrentRow(); 
        String personNo = currRow.getAttribute("PersonNumber").toString();
        
        currRow.setAttribute("StepId", "1"); 
        currRow.setAttribute("EditRequest", "Y");
        currRow.setAttribute("RequestStatus", "EDIT");
        currRow.setAttribute("ActionTaken", "EDIT");
        currRow.setAttribute("Assignee", personNo);
        currRow.setAttribute("AssigneeName", "");
        
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request enabled for edit and removed from the current approval !");
        return "edit";
    }
    
    public void sendEmailToPerson(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendChangePositionTitleEmail("OFOQ.HR@TATWEER.SA", email,
                                     (ChangePositionViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){
            
            ViewObject reqVo = ADFUtils.findIterator("ChangePositionView1Iterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String ChangePositionTitleTypeName="ChangePositionTitle" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ChangePositionTitleTypeName", ChangePositionTitleTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
           
            if(status!= null && "PENDING".equals(status)){
              
                currRow.setAttribute("StepId", totalStep);
                currRow.setAttribute("RequestStatus", "Withdrawn");
                currRow.setAttribute("ActionTaken", "Withdrawn");
                currRow.setAttribute("Assignee", personNo);
                currRow.setAttribute("AssigneeName", "");
                
                ADFUtils.findOperation("Commit").execute();
                Integer stepId = (Integer)ADFUtils.getBoundAttributeValue("StepId");
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ChangePositionId"), 
                                                                             "ChangePosition", (Number)nextStep.getAttribute("NextStepId"), 
                                                                                stepId.longValue(),
                                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                             "WITHDRAW_ACT", "");
                ADFUtils.findOperation("Execute").execute();
                JSFUtils.addFacesInformationMessage("Request Withdrawn Successfully !");
                
            }else{
                JSFUtils.addFacesErrorMessage("Please check, Request status is not PENDING");
            }
        }
    }
    
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("ChangePositionView1Iterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ChangePositionViewCriteria"));
            vo.executeQuery();
        }
    }
    
}
