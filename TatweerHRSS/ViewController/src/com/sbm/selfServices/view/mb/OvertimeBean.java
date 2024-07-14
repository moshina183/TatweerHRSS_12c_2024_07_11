package com.sbm.selfServices.view.mb;


import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.LoanRequestViewRowImpl;
import com.sbm.selfServices.model.views.up.OvertimeRequestViewRowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;
import com.sbm.selfServices.view.utils.PersonInfo;
import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStripApp.FilmStripBean;

import java.math.BigDecimal;

import java.math.RoundingMode;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.JAXBElement;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;
import oracle.jbo.jbotester.load.SimpleDateFormatter;


public class OvertimeBean {
    private RichInputText overtime;
    private RichOutputText transtotal;
    private RichInputDate overdatetime;
    private RichTable linetable;
    private RichInputText totalhoursbinding;

    public OvertimeBean() {
        super();
    }
    private FusionDataLoader fusionFileLoader;

    public void dummyLoadPersonData() {
        PersonInfo personInfo =
            (PersonInfo)JSFUtils.resolveExpression("#{PersonInfo}");
        personInfo.setPerosnNumber("1004");
        personInfo.setEmployeeId(1006L);
        personInfo.setGradeCode("19");
        //            personInfo.setLineManager(1005L);
        personInfo.setAssignee("1004");
        //                personInfo.setPersonName();
        //                personInfo.setFullName();
        //                personInfo.setEmail();
        //                personInfo.setPosition();
        //                personInfo.setDepartment();
        //                personInfo.setJob();
        //                personInfo.setLocation();
        //       -------------  get user grade :
        //        JSFUtils.setExpressionValue("#{pageFlowScope.grade}", "5");

        //        personInfo.setAssignee("HeadOfCorporate");
        //                    personInfo.setAssignee("CFO");
        //                            personInfo.setAssignee("CEO");
        //                    personInfo.setAssignee("PayrollManager");
        //                    personInfo.setAssignee("HRManager");
        //                    personInfo.setAssignee("BudgetController");
    }

    public String save_action() {
        Object employeeId =
            JSFUtils.resolveExpression("#{PersonInfo.employeeId}");
        
        if (employeeId == null) {
            JSFUtils.addFacesErrorMessage("You don't have Person ID, So you can't save the request");
            return null;
        }
        //        OperationBinding oper = ADFUtils.findOperation("getTotalhoursPerMonth");
        //        Map paramMap = oper.getParamsMap();
        //        System.err.println("Person Id Is >>>>  "+ADFUtils.getBoundAttributeValue("PersoneId"));
        //        paramMap.put("PersonId", ADFUtils.getBoundAttributeValue("PersoneId"));
        //        Number totaReminderlHoursPerMonth = (Number)oper.execute();
        //
        //        System.err.println("totalMonthsPerYear >>> "+totaReminderlHoursPerMonth);
        //        Number noOfHoursByUser = (Number)ADFUtils.getBoundAttributeValue("NumberOfHours");
        //
        //        System.err.println("noOfHoursByUser >>> "+noOfHoursByUser);
        //
        //
        //
        //        if((noOfHoursByUser.compareTo(totaReminderlHoursPerMonth)) > 0)
        //
        //        {
        //                JSFUtils.addFacesErrorMessage("Total hours per month Shouldn't Exceed 60 hours !!!  " +
        //                    " The available hours this month are  " + totaReminderlHoursPerMonth);
        //
        //                return null;
        //            }


        //        ADFUtils.setBoundAttributeValue("Assignee", employeeId);
        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been saved");
        ADFUtils.findOperation("Execute").execute();
        return "back";
    }
    
    public String validate_overlap_date(){
        ViewObject vo =
            ADFUtils.findIterator("OverTimeLineVOIterator").getViewObject();
        Row[] rows=vo.getAllRowsInRange();
        Row row;
        for(int i=0;i<rows.length;i++){
            row = rows[i];
            oracle.jbo.domain.Date overtimeLine = (oracle.jbo.domain.Date)row.getAttribute("OverTimeDate");
            ViewObject overTimeROVO =
                ADFUtils.findIterator("CheckOverlapROVOIterator").getViewObject();
            overTimeROVO.setNamedWhereClauseParam("p_person_number",
                                        ADFUtils.getBoundAttributeValue("PersoneId"));
            overTimeROVO.setNamedWhereClauseParam("p_over_time", overtimeLine);
            overTimeROVO.executeQuery();
            if (overTimeROVO.getEstimatedRowCount() > 0) {
                oracle.jbo.domain.Date lapdates =
                    (oracle.jbo.domain.Date)overTimeROVO.first().getAttribute("OverTimeDate");
//                if (lapdates.equals(overtimedate)) {
                    JSFUtils.addFacesInformationMessage("You Cannot Submit the Requet,Since there is Overlap in Date "+lapdates);
                  
//                }
            return "failed";
            }
        }
        return "sucess";
    }

    public String submit_action() {


        //        OperationBinding oper = ADFUtils.findOperation("getTotalhoursPerMonth");
        //        Map paramMap = oper.getParamsMap();
        //        System.err.println("Person Id Is >>>>  "+ADFUtils.getBoundAttributeValue("PersoneId"));
        //        paramMap.put("PersonId", ADFUtils.getBoundAttributeValue("PersoneId"));
        //        Number totaReminderlHoursPerMonth = (Number)oper.execute();
        //
        //        System.err.println("totalMonthsPerYear >>> "+totaReminderlHoursPerMonth);
        //        Number noOfHoursByUser = (Number)ADFUtils.getBoundAttributeValue("NumberOfHours");
        //
        //        System.err.println("noOfHoursByUser >>> "+noOfHoursByUser);
        //
        //
        //
        //        if((noOfHoursByUser.compareTo(totaReminderlHoursPerMonth)) > 0)
        //
        //        {
        //                JSFUtils.addFacesErrorMessage("Total hours per month Shouldn't Exceed 60 hours !!!  " +
        //                    " The available hours this month are  " + totaReminderlHoursPerMonth);
        //
        //                return null;
        //            }
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String returnvalue=validate_overlap_date();
        if(returnvalue!=null && returnvalue.equalsIgnoreCase("failed")) {
            return null;
        }
        String requestedHoursString = (String)ADFUtils.getBoundAttributeValue("NumberOfHours");
        if(requestedHoursString==null){
            requestedHoursString = "0";
        }
        Number requestedHours;
        try {
            requestedHours = new Number(requestedHoursString);
        } catch (SQLException e) {
            JSFUtils.addFacesErrorMessage("Issue Occured");
            return null;
        }
        ViewObject totalVo=ADFUtils.findIterator("TotalHourPerMonVO1Iterator").getViewObject();
        totalVo.setNamedWhereClauseParam("PERSON_ID_BIND", ADFUtils.getBoundAttributeValue("PersoneId"));
        totalVo.executeQuery();
        
        if(totalVo.getEstimatedRowCount()>0) {
          Number balanceHoursPerMonth = (Number)totalVo.first().getAttribute("Totalhours");
             if(requestedHours.compareTo(balanceHoursPerMonth)>0)  {
                JSFUtils.addFacesErrorMessage("Number of Overtime You entered exceeded the official limit (60 hours per month)");
                return null;
            }
        }
       
                String overtimeTypename="Overtime" +'-'+ personLocation;               
                ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
       
        
        // Based step type
        // Usual Submit processs
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersoneId").toString();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        userDetails =
                userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        Long lineManagerID = relationshipDetails.get(0).getManagerId();
        String stringLineManagerID = lineManagerID.toString();
        UserDetails managerDetails =
            userService.getUserDetailsByPersonId(lineManagerID);
        String managerNumber = managerDetails.getPersonNumber();
        String department1 =
            relationshipDetails.get(0).getDepartmentName();
          
        
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
            System.err.println("Submitted : DEPT_MANAGER :"+department1);
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
        
        // 
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        
       //Exist...

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

        ADFUtils.findOperation("Commit").execute();
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"), 
                                                                    "OverTime", (Number)nextStep.getAttribute("NextStepId"), 
                                                                    stepid.longValue(), 
                                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                    act, "");
        Row overtimeRow =
            ADFUtils.findIterator("OvertimeRequestViewIterator").getCurrentRow();
        JSFUtils.addFacesInformationMessage("Request has been submitted");
        ADFUtils.findOperation("Execute").execute();

        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, overtimeRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, overtimeRow);    
            }   
        }
        return "back";
    }

    public String approve_action() {
        // Add event code here...
        String personMail = null;
        int updateInsert=0;
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String overtimeTypename="Overtime" +'-'+ personLocation;               
                       ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersoneId").toString();
//        ADFUtils.setBoundAttributeValue("StepId",
//                                        nextStep.getAttribute("NextStepId"));
//        ADFUtils.setBoundAttributeValue("Assignee",
//                                        nextStep.getAttribute("NextAssignee"));
//        //         JSFUtils.resolveExpression("#{PersonInfo.nextAssignee}"));
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");


//        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
//            Row overtimeRow =
//                ADFUtils.findIterator("OvertimeRequestViewIterator").getCurrentRow();
//
//
//            BiReportAccess report = new BiReportAccess();
//            //
//            //                           ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("HR and Admin Director");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//            System.err.println("HR and Admin Director Number is " +
//                               personData.get(0).get("PERSON_NUMBER"));
//            System.err.println("HR and Admin Director Name is " +
//                               personData.get(0).get("DISPLAY_NAME"));
//            System.err.println("HR and Admin Director Email " +
//                               personData.get(0).get("EMAIL_ADDRESS"));
//            ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
//            ADFUtils.setBoundAttributeValue("AssigneeName",
//                                            personData.get(0).get("DISPLAY_NAME").toString());
//            Integer stepid1 = (Integer)ADFUtils.getBoundAttributeValue("StepId");
//            ADFUtils.findOperation("Commit").execute();
//            String value =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
//                                                      "OverTime",(Number)nextStep.getAttribute("StepId"),stepid1.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//              sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                               overtimeRow);
//            }
//            updateInsert=1;
//        }


//        if (nextStep.getAttribute("NextAssignee").equals("HR Payroll and benefits Supervisor")) {
//            Row overtimeRow =
//                ADFUtils.findIterator("OvertimeRequestViewIterator").getCurrentRow();
//
//
//            BiReportAccess report = new BiReportAccess();
//            //
//            //                           ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//            System.err.println("HR Payroll and benefits Supervisor Number is " +
//                               personData.get(0).get("PERSON_NUMBER"));
//            System.err.println("HR Payroll and benefits Supervisor Name is " +
//                               personData.get(0).get("DISPLAY_NAME"));
//            System.err.println("HR Payroll and benefits Supervisor Email " +
//                               personData.get(0).get("EMAIL_ADDRESS"));
//            ADFUtils.setBoundAttributeValue("AssigneeName",
//                                            personData.get(0).get("DISPLAY_NAME").toString());
//            Integer stepid1 = (Integer)ADFUtils.getBoundAttributeValue("StepId");
//            ADFUtils.findOperation("Commit").execute();
//            String value =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
//                                                      "OverTime",(Number)nextStep.getAttribute("StepId"),stepid1.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//          
//    
//            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//              sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                               overtimeRow);
//            }
//            updateInsert=1;
//        }
        
                //Dynamic Approval
                 
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        
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
        
        Row overtimeRow =
            ADFUtils.findIterator("OvertimeRequestViewIterator").getCurrentRow();
        


        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            //            UserServiceUtil userService = new UserServiceUtil();
            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersoneName.inputValue}");
            System.out.println("the person number is ==============> " +
                               personNumber);

            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", personName);
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
            //            Send Email to employee
            //            String managerId = (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");
            
         String dateString =
                ADFUtils.getBoundAttributeValue("ReqDate").toString();
              

            String formattedDate = dateString.replace('-', '/');
            //-----------------------------------------

            SimpleDateFormat oldDateFormat =
                new SimpleDateFormat("dd/MM/yyyy");
            Date myDate = null;
            try {
                myDate = oldDateFormat.parse(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            oldDateFormat.applyPattern("yyyy/MM/dd");
            formattedDate = oldDateFormat.format(myDate);
            System.out.println("myDateString  >>> " + formattedDate);

            //---------------------------------------------
            BiReportAccess report = new BiReportAccess();
            List<Map> datFileData = null;
            //----------------------------------------


            //            upload element on oracle fusion HCM
            try {
                datFileData =
                        report.getOvertimeDatFileData(personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HashMap<String, String> params = new HashMap<String, String>();
            //            params.put("Date", creationDate.toString());
            if (datFileData.size() > 0) {
                params.put("AssignmentNumber",
                           datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                params.put("HoursValue",
                           ADFUtils.getBoundAttributeValue("NumberOfHours").toString());
                params.put("StartDate", formattedDate);
                params.put("Count",
                           datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
            }

            else {

                JSFUtils.addFacesErrorMessage("You can't create Overtime element as the Assignment number or Multiple Entry Count is null");

            }
            try {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 2);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("OvertimeRequestId").toString(), personNumber, "Approved", "Overtime Allowance", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Approved Overtime Allowance: "+value);
                //fusionFileLoader.sendFusionRequest(params, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Integer stepid1 = (Integer)ADFUtils.getBoundAttributeValue("StepId");
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
                                                      "OverTime",(Number)nextStep.getAttribute("StepId"),stepid1.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            ADFUtils.findOperation("Commit").execute();
            updateInsert=1;
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            sendEmail(personNumber, overtimeRow);
            sendEmailByEmail(fyiEmailAddress, overtimeRow);
            }
            
        }

        ADFUtils.findOperation("Commit").execute();
       
        String finalapproval = null;
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            finalapproval = "Y";
        } else {
            finalapproval = "N";
        }
        System.err.println("finalapproval====" + finalapproval);
        if(updateInsert==0) {
            
            Integer stepid1 = (Integer)ADFUtils.getBoundAttributeValue("StepId");
            
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
                                                  "OverTime",(Number)nextStep.getAttribute("StepId"),stepid1.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT",finalapproval);
        
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if("POSITION".equals(stepType)){
                    System.err.println("Email to mail-id::"+assigneeEmail);
                    sendEmailByEmail(assigneeEmail, overtimeRow);    
                }else{
                    System.err.println("Email to user-id::"+assigneeNo);
                    sendEmail(assigneeNo, overtimeRow);    
                }   
            }
            
        }

        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";
    }

    public String reject_action() {
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersoneId.inputValue}");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String overtimeTypename="Overtime" +'-'+ personLocation;               
        ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        //Send Email to employee
        //        Object managerId =  JSFUtils.resolveExpression("#{PersonInfo.lineManager}");
        Row overtimeRow =
            ADFUtils.findIterator("OvertimeRequestViewIterator").getCurrentRow();

        //Get requstor ID by Number
        //        UserServiceUtil userService = new UserServiceUtil();
        //        UserDetails byPersonNumber =
        //            userService.getUserDetailsByPersonNumber(personNumber.stringValue());
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.findOperation("Commit").execute();
        String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
                                                              "OverTime",
                                                         (Number)nextStep.getAttribute("StepId"),
                                                          new Long(0),
                                                          null,
                                                          "REJECT_ACT", "");
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
        sendEmail(personNumber, overtimeRow);
        }
        JSFUtils.addFacesInformationMessage("Request has been Rejected");
        return "back";
    }
    //      Sending email area

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
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendOvertimeEmail("OFOQ.HR@TATWEER.SA", email,
                              (OvertimeRequestViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }


    public void sendEmailByEmail(String personEmail, Row subject) {

        sendOvertimeEmail("OFOQ.HR@TATWEER.SA", personEmail,
                          (OvertimeRequestViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendOvertimeEmail(String from, String to,
                                  OvertimeRequestViewRowImpl subject, String personFYI) {
        String status = subject.getRequestStatus();
        String edited = subject.getEditRequest();
        String requesterName = subject.getPersoneName();
        String assigneeName = "Sir";
        if(personFYI!= null && "Y".equals(personFYI)){
            assigneeName = requesterName;
        }else{
            assigneeName = subject.getAssigneeName()!=null ? subject.getAssigneeName() : "Sir";
        }
        
        String subj = "";
        String hdrMsg = "";
        if("Withdrawn".equals(status)){
            subj = "Overtime Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Overtime Request, and below the details";
            
        }else if("Waiting Withdraw Approval".equals(status)){
            subj = "Overtime Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Overtime Request, and below the details";
            
        }else if("Withdrawn Rejected".equals(status)){
            subj = "Overtime Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of Overtime request as below";
            
        }else if(edited!=null && edited.equals("Y") && "PENDING".equals(status)){
            subj = "Overtime Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Overtime Request, and below the details";
            
        }else{
            subj = "Overtime Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Overtime request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("Withdrawn".equals(status)){
                subj = "Overtime Request has been withdraw";
                hdrMsg= "Kindly be informed you that the following Overtime request has been withdraw";
            }
            if("APPROVED".equals(status)){
                subj = "Overtime Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Overtime request has been Approved";
            }
           
        }
//            JSFUtils.addFacesInformationMessage(subj);
//            JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
           // to = "vf.khayal@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String assignee = subject.getAssigneeName()!=null ? subject.getAssigneeName():"";
        String into = "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" +
             hdrMsg +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";
        String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
                                String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
        String PersonGrade=subject.getPersonGrade()!=null?subject.getPersonGrade():"";
        String personalInformation =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
            " <tr><th>&nbsp;Requester Name</th>\n" +
            "      <td width=\"50%\">" + PersoneName+ "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Position\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + PersonPosition +
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
            "      <td width=\"50%\">" + PersonGrade +
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
            "      <td width=\"50%\">" + PersonLocation +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String verticalSpace = " <p>&nbsp;</p>";
       
       
        String overtimeHours =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">OverTime Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number of Hours\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNumberOfHours() +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryOverTimeIterator");
        String OverTimeLine= ApprovelLine.overTimeWithLine("OverTimeLineVOIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + overtimeHours +verticalSpace+OverTimeLine+ApprovalPart1+
            thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject", subj);
//                                    "Overtime Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }
    //            public static void main(String[] args) {
    //            //
    //                BiReportAccess BIRA=new BiReportAccess();
    //
    //            String dateString="02-11-2017";
    //                                SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-mm-yyyy");
    //        Date myDate=null;
    //        try {
    //            myDate = oldDateFormat.parse(dateString);
    //        } catch (ParseException e) {
    //            e.printStackTrace();
    //        }
    //        System.err.println("myDate   "+myDate);
    //
    //        String format = oldDateFormat.format(myDate);
    //        System.out.println(oldDateFormat.format(myDate));
    //
    ////        oldDateFormat.applyPattern("yyyy/MM/dd");
    ////        dateString = oldDateFormat.format(myDate);
    //            List<Map> datFileData=null;
    //            try {
    //                datFileData = BIRA.getOvertimeDatFileData("1134", myDate);
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //
    //
    //        }
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }

    public void sendEmailForOverTimeEmp(String email, Row subject) {
//        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendOvertimeEmailForOverTimeFYI("OFOQ.HR@TATWEER.SA", email,
                                            (OvertimeRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendOvertimeEmailForOverTimeFYI(String from, String to,
                                                OvertimeRequestViewRowImpl subject) {
        if (to == null) {
           // to = "vf.khayal@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following overtime request has been Approved" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
                                String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
        String PersonGrade=subject.getPersonGrade()!=null?subject.getPersonGrade():"";
        String personalInformation =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
            " <tr><th>&nbsp;Requester Name</th>\n" +
            "      <td width=\"50%\">" + PersoneName+ "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Position\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + PersonPosition +
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
            "      <td width=\"50%\">" + PersonGrade +
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
            "      <td width=\"50%\">" + PersonLocation +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String verticalSpace = " <p>&nbsp;</p>";

        String overtimeDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">OverTime Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number of Hours\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNumberOfHours() +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryOverTimeIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + overtimeDetails +ApprovalPart1+
            thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Overtime Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }

    public void onClickAdd(ActionEvent actionEvent) {
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String overtimeTypename="Overtime" +'-'+ personLocation;               
        ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
    
      ADFUtils.findOperation("CreateInsert").execute();  
     
     
    }

    public void onClickDelete(ActionEvent actionEvent) {
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String overtimeTypename="Overtime" +'-'+ personLocation;               
        ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
       ViewObject vo=ADFUtils.findIterator("OverTimeLineVOIterator").getViewObject();
        if(vo.getEstimatedRowCount()>1){ 
            ADFUtils.findOperation("Delete").execute();
            calcEntryTime();
            AdfFacesContext.getCurrentInstance().addPartialTarget(totalhoursbinding);
        }
        else{
            JSFUtils.addFacesErrorMessage("You can't delete the over time entry! Since atleast one row of over time is needed"); 
        }
    }
   

    public void setOvertime(RichInputText overtime) {
        this.overtime = overtime;
    }

    public RichInputText getOvertime() {
        return overtime;
    }

    public void setTranstotal(RichOutputText transtotal) {
        this.transtotal = transtotal;
    }

    public RichOutputText getTranstotal() {
        return transtotal;
    }

    public void onChangeHours(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        ViewObject vo=ADFUtils.findIterator("OverTimeLineVOIterator").getViewObject();
        Row row = vo.getCurrentRow();
        BigDecimal enteredHours = new BigDecimal(0);
                /* Added by Moshina - Mar'2024
                 * Entered hours validation done BigDecimal enteredHours=new BigDecimal(enteredHoursString);
                 * */
        if(valueChangeEvent.getNewValue()!=null) {
            int flag=0;
            String enteredHoursString = (String)valueChangeEvent.getNewValue();

            try {
                enteredHours = new BigDecimal(enteredHoursString);
            } catch (NumberFormatException e) {
                JSFUtils.addFacesErrorMessage("Enter the Valid Hours E.g.: 5, 5.5 format only");
            }
            if((enteredHours.doubleValue()-Math.floor(enteredHours.doubleValue())>0)) {
                BigDecimal minutesEntered=new BigDecimal(enteredHours.doubleValue() - Math.floor( enteredHours.doubleValue()));
                if(minutesEntered.compareTo(new BigDecimal(0.59))>0) {
                    JSFUtils.addFacesErrorMessage("You Cannot Enter More than 59 Minutes, since more than 60 mins is an hour add it in the hours accordingly");
                    row.setAttribute("Hours", valueChangeEvent.getOldValue());
                    AdfFacesContext.getCurrentInstance().addPartialTarget(valueChangeEvent.getComponent());
                    flag=1;
                }
            }
            if(flag==0 && !calcEntryTime()){
                row.setAttribute("Hours", valueChangeEvent.getOldValue());
                AdfFacesContext.getCurrentInstance().addPartialTarget(valueChangeEvent.getComponent());
            }
        }
    }
    public boolean calcEntryTime() {
        ViewObject vo=ADFUtils.findIterator("OverTimeLineVOIterator").getViewObject();
        RowSetIterator rs = vo.createRowSetIterator(null); 
        String TotalHours=null;
        Number hours=new Number(0);
        Number minutes=new Number(0);
        try{
            while (rs.hasNext()) {
                Row r = rs.next();
                String NoofHoursString = r.getAttribute("Hours")!=null ? r.getAttribute("Hours").toString() : "0";
                if(NoofHoursString!=null){
                    String[] NoofHoursStringArray=NoofHoursString.split("\\.");
                    hours = hours.add(new Number(NoofHoursStringArray[0]));
                    if(NoofHoursStringArray.length==2){
                        minutes = minutes.add(new Number(NoofHoursStringArray[1]));
                    }
                }
            }
                 rs.closeRowSetIterator();
                 if(minutes.doubleValue()>0){
                     double hoursInMinutes = minutes.doubleValue()/60;
                     hoursInMinutes = Math.floor(hoursInMinutes);
                     hours = hours.add(hoursInMinutes);
                     minutes = minutes.subtract(hoursInMinutes*60);
                 }
                String finalTotalHours =  hours.toString() + "." + minutes.toString();
                TotalHours = finalTotalHours;
        
                if(new Number(TotalHours).doubleValue()>0){
                    ADFUtils.setBoundAttributeValue("NumberOfHours", TotalHours);
                }
                else{
                    JSFUtils.addFacesErrorMessage("Total hours should be greater than zero");
                    return false;
                }
        }
        catch(Exception e){
            e.printStackTrace();
            JSFUtils.addFacesErrorMessage("Issue in calculation");
            return false;
        }
                System.err.println("NumberOfHours======="+ ADFUtils.getBoundAttributeValue("NumberOfHours"));
            return true;
    }
    

    public void onChangeDate(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        oracle.jbo.domain.Date overtimedate =
            (oracle.jbo.domain.Date)valueChangeEvent.getNewValue();

        if (valueChangeEvent.getNewValue() != null) {
            java.sql.Date javaSqlDate2 = overtimedate.dateValue();
            long javaMilliSeconds2 = javaSqlDate2.getTime();
            java.util.Date overtimeUtildate = new java.util.Date(javaMilliSeconds2);
            Calendar cal = Calendar.getInstance();
            java.util.Date date=cal.getTime();
            if(overtimeUtildate.compareTo(date)<0)
            {
            ViewObject vo =
                ADFUtils.findIterator("CheckOverlapROVOIterator").getViewObject();
            vo.setNamedWhereClauseParam("p_person_number",
                                        ADFUtils.getBoundAttributeValue("PersoneId"));
            vo.setNamedWhereClauseParam("p_over_time", overtimedate);
            vo.executeQuery();
            if (vo.getEstimatedRowCount() > 0) {
                oracle.jbo.domain.Date lapdates =
                    (oracle.jbo.domain.Date)vo.first().getAttribute("OverTimeDate");
                if (lapdates.equals(overtimedate)) {
                    JSFUtils.addFacesInformationMessage("Date OverLap,Choose Some Other Date");
                    overdatetime.setValue("");
                    AdfFacesContext.getCurrentInstance().addPartialTarget(overdatetime);
                }

            }
            }
            else {
                overdatetime.setValue("");
                AdfFacesContext.getCurrentInstance().addPartialTarget(overdatetime);
                JSFUtils.addFacesInformationMessage("Date cannot be greater than Current Date");
            }
        }
    }

    public void setOverdatetime(RichInputDate overdatetime) {
        this.overdatetime = overdatetime;
    }

    public RichInputDate getOverdatetime() {
        return overdatetime;
    }

    public void setLinetable(RichTable linetable) {
        this.linetable = linetable;
    }

    public RichTable getLinetable() {
        return linetable;
    }

  

    public void setTotalhoursbinding(RichInputText totalhoursbinding) {
        this.totalhoursbinding = totalhoursbinding;
    }

    public RichInputText getTotalhoursbinding() {
        return totalhoursbinding;
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
    
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){

            ViewObject reqVo = ADFUtils.findIterator("OvertimeRequestViewIterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersoneId").toString();
            
            currRow.setAttribute("StepId", "1");
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String overtimeTypename="Overtime" +'-'+ personLocation;               
            ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
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
                Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"), 
                                                                             "OverTime", (Number)nextStep.getAttribute("NextStepId"), 
                                                                             stepid.longValue(), 
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
                
                Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
                String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"), 
                                                                             "OverTime", (Number)nextStep.getAttribute("NextStepId"), 
                                                                             stepid.longValue(), 
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
        ViewObject reqVo = ADFUtils.findIterator("OvertimeRequestViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personNo = ADFUtils.getBoundAttributeValue("PersoneId").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String overtimeTypename="Overtime" +'-'+ personLocation;               
        ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
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
            ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
                                                      "OverTime",
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
    
    public String approve_withdraw() throws ParseException {
        
        ViewObject reqVo = ADFUtils.findIterator("OvertimeRequestViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
            String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
            String overtimeTypename="Overtime" +'-'+ personLocation;               
            ADFContext.getCurrent().getPageFlowScope().put("OvertimeTypeName", overtimeTypename);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";   
        
        String personNumber = ADFUtils.getBoundAttributeValue("PersoneId").toString();
         
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
            
            BiReportAccess report = new BiReportAccess();
            String reqCreatedDate =
                ADFUtils.getBoundAttributeValue("ReqDate").toString(); 
            String noOfHours =
                ADFUtils.getBoundAttributeValue("NumberOfHours").toString();
            
            List<Map> datFileData = null;
            try {
                datFileData = report.getOvertimeDatWithdrawFileData(personNumber, changeFormatOfDate("dd-MM-yyyy", "MM-dd-yyyy", reqCreatedDate));
            } catch (Exception e) {
                e.printStackTrace();
            } 
            
            String assignmentNumber =
                datFileData.get(0).get("ASSIGNMENT_NUMBER")==null?"":
                datFileData.get(0).get("ASSIGNMENT_NUMBER").toString();
            String entryCount =
                datFileData.get(0).get("MULTIPLEENTRYCOUNT")==null?"":
                datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString();  
            
            String effStartDate =
                datFileData.get(0).get("EFFECTIVE_START_DATE")==null?"":
                datFileData.get(0).get("EFFECTIVE_START_DATE").toString();
            String effEndDate =
                datFileData.get(0).get("EFFECTIVE_END_DATE")==null?"":
                datFileData.get(0).get("EFFECTIVE_END_DATE").toString();  
            
//            String effStartDate = this.changeFormatOfDate("dd-MM-yyyy", "yyyy/MM/dd", reqCreatedDate);
//            String effEndDate = this.getLastDayOfMonth("dd-MM-yyyy", "yyyy/MM/dd", reqCreatedDate); 
            
            
            HashMap<String, String> params = new HashMap<String, String>(); 
            params.put("AssignmentNo", assignmentNumber);
            params.put("Count", entryCount);
            params.put("EffStartDate", effStartDate);
            params.put("EffEndDate", effEndDate); 
            params.put("NumberOfHours", noOfHours); 
            System.err.println("OVERTIME WITHDRAW : Fusion upload calling with values : "+params);

            try {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 14);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("OvertimeRequestId").toString(), personNumber, "Withdrawn", "Overtime Allowance", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Withdrawn Overtime Allowance: "+value);
                //fusionFileLoader.sendFusionRequest(params, 14);
                
            } catch (Exception e) {
                e.printStackTrace();
            } 
            
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
                                                      "OverTime",(Number)nextStep.getAttribute("StepId"), ((Integer)ADFUtils.getBoundAttributeValue("StepId")).longValue(),
                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                     sendEmailToPerson(personNumber, currRow);
                     sendEmailByEmail(fyiEmailAddress, currRow);
            } 
        }else{
            
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("OvertimeRequestId"),
                                                      "OverTime",(Number)nextStep.getAttribute("StepId"), ((Integer)ADFUtils.getBoundAttributeValue("StepId")).longValue(),
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

    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("OvertimeRequestViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow(); 
        String personNo = currRow.getAttribute("PersoneId").toString();
        
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
    
    private String changeFormatOfDate(String fromFormat, String toFormat, String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        java.util.Date date = formatter.parse(strDate);
        SimpleDateFormat ft = new SimpleDateFormat(toFormat);
        return ft.format(date);
     }
    
    private String getLastDayOfMonth(String fromFormat, String toFormat, String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        java.util.Date date = formatter.parse(strDate);
        
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         calendar.add(Calendar.MONTH, 1);
         calendar.set(Calendar.DAY_OF_MONTH, 1);
         calendar.add(Calendar.DATE, -1); 
         Date lastDayOfMonth = calendar.getTime();
         
         DateFormat sdf = new SimpleDateFormat(toFormat);
         return sdf.format(lastDayOfMonth);
     }

    public void sendEmailToPerson(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendOvertimeEmail("OFOQ.HR@TATWEER.SA", email,
                                     (OvertimeRequestViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("OvertimeRequestViewIterator").getViewObject();
        vo.applyViewCriteria(null);
        vo.executeQuery();
        
        if ("MyTasks".equals(statusType)) {
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("PersoneName", person);
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }else 
        if("PendingTasks".equals(statusType)){
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("AssigneeName", person);
            vc.addRow(vcRow);
            vcRow.setAttribute("RequestStatus",
                               "IN ('PENDING','Waiting Withdraw Approval')");
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }else{
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("OvertimeRequestViewCriteria"));
            vo.executeQuery();
        }
    }

}


//note: if you want to implement alighn to the table hsere is a sample
//https://www.w3schools.com/tags/att_td_align.asp
