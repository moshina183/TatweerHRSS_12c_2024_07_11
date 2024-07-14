package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.APConsumer;
import com.sbm.CodeCombinationConsumer;
import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;

import com.sbm.selfServices.model.views.up.LoanRequestViewRowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;


import com.sbm.selfServices.view.utils.UserServiceUtil;


import com.view.beans.filmStripApp.FilmStripBean;

import java.io.IOException;
import java.io.InputStream;

import java.sql.SQLException;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.GregorianCalendar;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;


import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;


import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;


import oracle.jbo.Row;


import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

import org.apache.commons.io.IOUtils;

public class LoanMB {
    private RichPopup requestPopup;
    private FusionDataLoader fusionFileLoader;


    public LoanMB() {
    }
    //    public void dummyLoadPersonData() {
    //        PersonInfo personInfo = (PersonInfo)JSFUtils.resolveExpression("#{PersonInfo}");
    //        personInfo.setPerosnNumber("1004");
    //                        personInfo.setEmployeeId(1004L);
    //        //                personInfo.setPersonName();
    //        //                personInfo.setFullName();
    //        //                personInfo.setEmail();
    //        //                personInfo.setPosition();
    //        //                personInfo.setDepartment();
    //        //                personInfo.setJob();
    //        //                personInfo.setLocation();
    //        //       -------------  get user grade :
    //        personInfo.setGradeCode("9");
    //    //        JSFUtils.setExpressionValue("#{pageFlowScope.grade}", "5");
    //
    //    //        personInfo.setAssignee("HeadOfCorporate");
    //        //                    personInfo.setAssignee("CFO");
    //                            personInfo.setAssignee("1004");
    //        //                    personInfo.setAssignee("Payroll Manager");
    //        //                    personInfo.setAssignee("HRManager");
    //        //                    personInfo.setAssignee("BudgetController");
    //    }

    public String newLoan() {
        // Add event code here...

        //        ADFUtils.findOperation("ExecuteClevelWithParams").execute();
        //        DCIteratorBinding csecretaryIterator =
        //            ADFUtils.findIterator("CsecretaryROViewIterator");
        //        if (csecretaryIterator.getEstimatedRowCount() > 0) {
        //            requestPopup.show(new RichPopup.PopupHints());
        //            return null;
        //        } else {
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        JSFUtils.setExpressionValue("#{pageFlowScope.loanPersonId}",
                                    personNumber);
        JSFUtils.setExpressionValue("#{pageFlowScope.loanPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));

        JSFUtils.setExpressionValue("#{pageFlowScope.empSalary}",
                                    JSFUtils.resolveExpression("#{PersonInfo.salary}"));
        String returnValue = loanValidation(personNumber,"new");
        if(returnValue.equalsIgnoreCase("success")){
            return "newLoan"; 
        }
        else{
            return null;
        }
        
    }
    
    public String loanValidation(String personNumber,String type){
        BiReportAccess BIRA = new BiReportAccess();
        try {
            ViewObject vo = ADFUtils.findIterator("PendingLoanROVO1Iterator").getViewObject();
            System.out.println("Person Number:"+personNumber);
            vo.setNamedWhereClauseParam("person_id", personNumber);
            vo.setRangeSize(-1);
            vo.executeQuery();
            System.out.println("row count:"+vo.getEstimatedRowCount());
            if(vo.getEstimatedRowCount()>0){
                if(type.equalsIgnoreCase("new")){
                   // JSFUtils.addFacesErrorMessage("Already Loan Request is pending for approval with "+vo.first().getAttribute("AssigneeName")+", Once Approved or Rejected you can try raising new request");
                    FilmStripBean.showPopupMessage("Already Loan Request is pending for approval with "+vo.first().getAttribute("AssigneeName")+", Once Approved or Rejected you can try raising new request");
                }
                else if(type.equalsIgnoreCase("submit")){
                    FilmStripBean.showPopupMessage("Already Loan Request is pending for approval with "+vo.first().getAttribute("AssigneeName")+", Once Approved or Rejected you can try submiting the request");
                 //   JSFUtils.addFacesErrorMessage("Already Loan Request is pending for approval with "+vo.first().getAttribute("AssigneeName")+", Once Approved or Rejected you can try submiting the request");
                }
                return "alreadyPending";
            }
            else{
                List<Map> LoanRemainingList = BIRA.getLoanRemaining(personNumber);
                if(LoanRemainingList.size()>0){
                    String LoanRemainingString = LoanRemainingList.get(0).get("LOANREMAINING")!=null ? LoanRemainingList.get(0).get("LOANREMAINING").toString() : "0";
                    Double LoanRemaining = Double.parseDouble(LoanRemainingString);
                    if(LoanRemaining>0){
                       // JSFUtils.addFacesErrorMessage("Already there is a existing loan for you, so you can't Request a new Loan");
                        FilmStripBean.showPopupMessage("Already there is a existing loan for you, so you can't Request a new Loan");
                        return "saasError";
                    }
                    else{
                        return "success";
                    }
                }
                else{
                  //  JSFUtils.addFacesErrorMessage("Already there is a existing loan for you, so you can't Request a new Loan");
                    FilmStripBean.showPopupMessage("Already there is a existing loan for you, so you can't Request a new Loan");
                    return "saasError";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    public void setRequestPopup(RichPopup requestPopup) {
        this.requestPopup = requestPopup;
    }

    public RichPopup getRequestPopup() {
        return requestPopup;
    }
    /* Modified by Moshina Dt: 2024/02/28
    * Instead of position number, Position code is passing as parameter for the below THCPositionReport.xdo
    */
    public String okPopupLoanAction() {
        // Add event code here...

        String reqType =
            (String)JSFUtils.resolveExpression("#{pageFlowScope.reqType}");
        if (reqType.equals("clevel")) {
        String clevelPosition =
            (String)JSFUtils.resolveExpression("#{bindings.CsecretaryROView.inputValue}");
        System.out.println("clevel positon: "+clevelPosition);
        UserDetails userDetails = null;
        UserServiceUtil userService = new UserServiceUtil();
        userDetails =
                userService.getUserDetailsByPersonNumber(clevelPosition);
        System.out.println("userDetails.getPersonNumber(): "+userDetails.getPersonNumber());
        System.out.println("userDetails: "+userDetails);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        int length = userDetails.getUserWorkRelationshipDetails().size();
        System.out.println("length: "+length);

        String position = relationshipDetails.get(0).getPositionName();
        String positionCode = relationshipDetails.get(0).getPositionCode();
        System.err.println("Position is BT: " + position);
        System.err.println("Position Code is BT: " + positionCode);
        
        BiReportAccess report = new BiReportAccess();
        //            ReportsUtils report = new ReportsUtils();
        List<Map> personData = null;
        try {
            personData = report.getPersonByPostionReport(positionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("person is " +
                           personData.get(0).get("PERSON_NUMBER"));
        System.out.println("person is " +
                           personData.get(0).get("DISPLAY_NAME"));
        JSFUtils.setExpressionValue("#{pageFlowScope.loanPersonId}",
                                    personData.get(0).get("PERSON_NUMBER"));
        JSFUtils.setExpressionValue("#{pageFlowScope.loanPersonName}",
                                    personData.get(0).get("DISPLAY_NAME"));


        return "newLoan";
        } else {
        JSFUtils.setExpressionValue("#{pageFlowScope.loanPersonId}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.loanPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
        return "newLoan";

        }
    }

    public String sendsave_action() {
        // Add event code here...
        //        DCIteratorBinding loanIterator = ADFUtils.findIterator("LoanRequestViewIterator");
        //        ViewObject loanRequestVO = loanIterator.getViewObject();
        //        LoanRequestViewRowImpl loanReqCurrentRow = (LoanRequestViewRowImpl)loanRequestVO.getCurrentRow();
        //        String currentPersonId = loanReqCurrentRow.getPersonId();
        //        String currentPersonName = loanReqCurrentRow.getPersonName();


        Object employeeId =
            JSFUtils.resolveExpression("#{PersonInfo.employeeId}");
        System.err.println("employeeId is >>>>  " + employeeId);
        if (employeeId == null) {
            JSFUtils.addFacesErrorMessage("You don't have Person ID, So you can't save the request");
            return null;
        }
        //        getTotalMonthsInYear


        OperationBinding oper = ADFUtils.findOperation("getTotalMonthsInYear");
        Map paramMap = oper.getParamsMap();
        System.err.println("Person Id Is >>>>  " +
                           ADFUtils.getBoundAttributeValue("PersonId"));
        paramMap.put("PersonId", ADFUtils.getBoundAttributeValue("PersonId"));
        Number totalMonthsInYear = (Number)oper.execute();

        System.err.println("totalMonthsPerYear >>> " + totalMonthsInYear);
        Number noOfMonthsByUser =
            (Number)ADFUtils.getBoundAttributeValue("NoOfMonths");

        System.err.println("noOfMonthsByUser >>> " + noOfMonthsByUser);

        if (totalMonthsInYear != null) {

            Number totalMonths = noOfMonthsByUser.add(totalMonthsInYear);
            Number reminderMonthsThisYear =
                (Number)new Number(3).minus(totalMonthsInYear);

//            if ((totalMonths.compareTo(3)) > 0)
//
//            {
//                JSFUtils.addFacesErrorMessage("Total Loan Amount Shouldn't Exceed 3 Months Salary !!!  " +
//                                              "  The available loan amount is  ( " +
//                                              reminderMonthsThisYear +
//                                              " ) month(s) salary");
//                return null;
//            }
        }

        if (totalMonthsInYear.equals(0)) {


            ADFUtils.setBoundAttributeValue("YearEnd",
                                            ADFUtils.getBoundAttributeValue("CreationDatePlusYear"));

        } else {
            OperationBinding operLoanTotalsRow =
                ADFUtils.findOperation("getLoanTotalsRow");
            Map paramMap2 = operLoanTotalsRow.getParamsMap();
            paramMap2.put("PersonId",
                          ADFUtils.getBoundAttributeValue("PersonId"));
            Row row = (Row)operLoanTotalsRow.execute();
            Date yearEnd = (Date)row.getAttribute("YearEnd");
            ADFUtils.setBoundAttributeValue("YearEnd", yearEnd);


        }

        //        OperationBinding oper = ADFUtils.findOperation("getTotalMonthsPerYear");
        //        Map paramMap = oper.getParamsMap();
        //        System.err.println("Person Id Is >>>>  "+ADFUtils.getBoundAttributeValue("PersonId"));
        //        paramMap.put("PersonId", ADFUtils.getBoundAttributeValue("PersonId"));
        //        Number totalMonthsPerYear = (Number)oper.execute();
        //
        //        System.err.println("totalMonthsPerYear >>> "+totalMonthsPerYear);
        //        Number noOfMonthsByUser = (Number)ADFUtils.getBoundAttributeValue("NoOfMonths");
        //
        //        System.err.println("noOfMonthsByUser >>> "+noOfMonthsByUser);
        //
        //        if(totalMonthsPerYear!=null){
        //
        //        Number totalMonths = noOfMonthsByUser.add(totalMonthsPerYear);
        //        Number reminderMonthsThisYear = (Number)new Number(3).minus(totalMonthsPerYear);
        //
        //        if((totalMonths.compareTo(3)) > 0)
        //
        //        {
        //         JSFUtils.addFacesErrorMessage("Total Loan Amount Shouldn't Exceed 3 Months Salary !!!  " +
        //             "  The available loan amount is  ( " + reminderMonthsThisYear +" ) month(s) salary");
        //         return null;
        //        }
        //        }


        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Loan Request has been saved");

        return "backToLoanRequest";
    }

    public String submit_action() {
        // Add event code here...
        String personNumberValidation = (String)ADFUtils.getBoundAttributeValue("PersonId");
        String returnValue = loanValidation(personNumberValidation,"submit");
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        if(returnValue.equalsIgnoreCase("success"))
        {
            OperationBinding oper = ADFUtils.findOperation("getTotalMonthsInYear");
            Map paramMap = oper.getParamsMap();
            System.err.println("Person Id Is >>>>  " +
                               ADFUtils.getBoundAttributeValue("PersonId"));
            paramMap.put("PersonId", ADFUtils.getBoundAttributeValue("PersonId"));
            Number totalMonthsInYear = (Number)oper.execute();
    
            System.err.println("totalMonthsPerYear >>> " + totalMonthsInYear);
            Number noOfMonthsByUser =
                (Number)ADFUtils.getBoundAttributeValue("NoOfMonths");
    
            System.err.println("noOfMonthsByUser >>> " + noOfMonthsByUser);
    
            if (totalMonthsInYear != null) {
    
                Number totalMonths = noOfMonthsByUser.add(totalMonthsInYear);
                Number reminderMonthsThisYear =
                    (Number)new Number(3).minus(totalMonthsInYear);
    
    //            if ((totalMonths.compareTo(3)) > 0)
    //
    //            {
    //                JSFUtils.addFacesErrorMessage("Total Loan Amount Shouldn't Exceed 3 Months Salary !!!  " +
    //                                              "  The available loan amount is  ( " +
    //                                              reminderMonthsThisYear +
    //                                              " ) month(s) salary");
    //                return null;
    //            }
            }
    
            if (totalMonthsInYear.equals(0)) {
    
    
                ADFUtils.setBoundAttributeValue("YearEnd",
                                                ADFUtils.getBoundAttributeValue("CreationDatePlusYear"));
    
            } else {
                OperationBinding operLoanTotalsRow =
                    ADFUtils.findOperation("getLoanTotalsRow");
                Map paramMap2 = operLoanTotalsRow.getParamsMap();
                paramMap2.put("PersonId",
                              ADFUtils.getBoundAttributeValue("PersonId"));
                Row row = (Row)operLoanTotalsRow.execute();
                Date yearEnd = (Date)row.getAttribute("YearEnd");
                ADFUtils.setBoundAttributeValue("YearEnd", yearEnd);
    
    
            }
    
            //        DCIteratorBinding loanIterator = ADFUtils.findIterator("LoanRequestViewIterator");
            //        ViewObject loanRequestVO = loanIterator.getViewObject();
            //        LoanRequestViewRowImpl loanReqCurrentRow = (LoanRequestViewRowImpl)loanRequestVO.getCurrentRow();
            //        String currentPersonId = loanReqCurrentRow.getPersonId();
            //        String currentPersonName = loanReqCurrentRow.getPersonName();
    
            //                OperationBinding oper = ADFUtils.findOperation("getTotalMonthsPerYear");
            //        Map paramMap = oper.getParamsMap();
            //        System.err.println("Person Id Is >>>>  "+ADFUtils.getBoundAttributeValue("PersonId"));
            //        paramMap.put("PersonId", ADFUtils.getBoundAttributeValue("PersonId"));
            //        Number totalMonthsPerYear = (Number)oper.execute();
            //
            //        System.err.println("totalMonthsPerYear >>> "+totalMonthsPerYear);
            //        Number noOfMonthsByUser = (Number)ADFUtils.getBoundAttributeValue("NoOfMonths");
            //
            //        System.err.println("noOfMonthsByUser >>> "+noOfMonthsByUser);
            //
            //        if(totalMonthsPerYear!=null){
            //
            //         Number totalMonths = noOfMonthsByUser.add(totalMonthsPerYear);
            //        Number reminderMonthsThisYear = (Number)new Number(3).minus(totalMonthsPerYear);
            //
            //         if((totalMonths.compareTo(3)) > 0)
            //
            //         {
            //                 JSFUtils.addFacesErrorMessage("Total Loan Amount Shouldn't Exceed 3 Months Salary !!!  " +
            //                     "  The available loan amount is  ( " + reminderMonthsThisYear +" ) month(s) salary");
            //                 return null;
            //             }
            //        }
            String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            String LoanTypeName="Loan" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("LoanTypeName", LoanTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
            System.out.println("next step is " +
                               nextStep.getAttribute("NextStepId"));
            String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            System.err.println("Submitted: stepType is::"+stepType);
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId"));
    
          
    
            String personNumber =
                ADFUtils.getBoundAttributeValue("PersonId").toString();
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
            ADFUtils.setBoundAttributeValue("PersonGrade", gradeCode);
            // Based step type
            // Usual Submit processs
            
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
            ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
            ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId"));
            
            ADFUtils.findOperation("Commit").execute();
            
            Row loanRow =
                ADFUtils.findIterator("LoanRequestViewIterator").getCurrentRow();
            String act = "SUMBIT_ACT";
            if(currStatus != null && "EDIT".equals(currStatus)){
                act = "UPDATE_ACT";
            }
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"), 
                                                                        "Loan", (Number)nextStep.getAttribute("NextStepId"), 
                                                                       (Long)ADFUtils.getBoundAttributeValue("StepId"), 
                                                                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                        act, "");
            
            if("POSITION".equals(stepType)){
                System.err.println(stepType + "-Mail to--email :"+assigneeEmail);
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    if (assigneeEmail == null) {
                        JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver");
                        return "backToLoanRequest";
                    }else{
                      sendEmailByEmail(assigneeEmail, loanRow);  
                    } 
                }
            }
            if(! "POSITION".equals(stepType)){
                System.err.println(stepType + "-Mail to--person :"+assigneeNo);
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    if (assigneeNo == null) {
                        JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver..");
                        return "backToLoanRequest";
                    }else{
                      sendEmail(assigneeNo, loanRow);  
                    } 
                }
            }
            
            /*
            BiReportAccess report = new BiReportAccess();
    
            //                    ReportsUtils report = new ReportsUtils();
            List<Map> personData = null;
    
            try {
                personData =
                        report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.err.println("HR Payroll and benefits Supervisor Number is " +
                               personData.get(0).get("PERSON_NUMBER"));
            System.err.println("HR Payroll and benefits Supervisor Name is " +
                               personData.get(0).get("DISPLAY_NAME"));
            System.err.println("HR Payroll and benefits Supervisor Email " +
                               personData.get(0).get("EMAIL_ADDRESS"));
            //        System.out.println("person Email " + personData.get(2));
            ADFUtils.setBoundAttributeValue("AssigneeName",
                                            personData.get(0).get("DISPLAY_NAME").toString());
            //        report.getDepartmentsReport();
            //        UserServiceUtil userService = new UserServiceUtil();
            //        UserDetails byPayrollManagerNumber = userService.getUserDetailsByPersonNumber(personData.get(0));
            //        JAXBElement<String> aXBElement = byPayrollManagerNumber.getUsername();
            //        String payrollManagerUserName=aXBElement.getValue();
            //
            //        System.out.println(">>>>>>>>>>>>>>>>><<<<<<<<<<<<<<");
            //        System.out.println("payrollManagerUserName is" +payrollManagerUserName);
            //        System.out.println(">>>>>>>>>>>>>>>>><<<<<<<<<<<<<<");
            Row loanRow =
                ADFUtils.findIterator("LoanRequestViewIterator").getCurrentRow();
            ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
            //        ADFUtils.setBoundAttributeValue("PersonId", currentPersonId);
            //        ADFUtils.setBoundAttributeValue("PersonName", currentPersonName);
       
           
            ADFUtils.findOperation("Commit").execute();
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"), 
                                                                        "Loan", (Number)nextStep.getAttribute("NextStepId"), 
                                                                       (Long)ADFUtils.getBoundAttributeValue("StepId"), 
                                                                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                        "SUMBIT_ACT", "");
            //       sendEmail((Long)managerId, loanRow);
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
                             loanRow);
            }*/
            JSFUtils.addFacesInformationMessage("Loan Request has been submitted");
            //            Long perId = (Long)JSFUtils.resolveExpression("#{PersonInfo.employeeId}");
            //        UserServiceUtil userService = new UserServiceUtil();
            //        UserDetails userDetails =
            //            userService.getUserDetailsByPersonNumber("1064");
            //
            //        String mail = userDetails.getUserPersonDetails().get(0).getEmailAddress().getValue();
            //        System.err.println("//////////-------------////////////");
            //        System.err.println("Payroll Manager Email from service by perNo is "+mail);
            //        System.err.println("//////////-------------////////////");
    
            return "backToLoanRequest";
        }
        else{
            return null;
        }
    }


    public String approve_action() {
        String returnValue = null;
        String personMail = null;
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String LoanTypeName="Loan" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanTypeName", LoanTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonId.inputValue}");
        LoanRequestViewRowImpl loanRow =
            (LoanRequestViewRowImpl)ADFUtils.findIterator("LoanRequestViewIterator").getCurrentRow();
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        
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
        String userEmployeeType=relationshipDetails1.get(0).getUserPersonType();
          
        //Dynamic Approval
        
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
        
        
        
        
        
//        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
//            BiReportAccess report = new BiReportAccess();
//
//            //                    ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//
//            try {
//                personData = report.getPersonByPostionReport("HR and Admin Director");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("HR and Admin Director Number is " +
//                               personData.get(0).get("PERSON_NUMBER"));
//            System.out.println("HR and Admin Director Name is " +
//                               personData.get(0).get("DISPLAY_NAME"));
//            ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
//            ADFUtils.setBoundAttributeValue("AssigneeName",
//                                            personData.get(0).get("DISPLAY_NAME"));
//
//            personMail = personData.get(0).get("EMAIL_ADDRESS").toString();
//            ADFUtils.findOperation("Commit").execute();
//            
//        }


        //        if (nextStep.getAttribute("NextAssignee").equals("CFO")){
        //                LoanRequestViewRowImpl loanRow =
        //                (LoanRequestViewRowImpl)ADFUtils.findIterator("LoanRequestViewIterator").getCurrentRow();
        //                ReportsUtils report = new ReportsUtils();
        //                ArrayList<String> personData =
        //                report.getPersonByPostionReport("CFO");
        //
        //                System.out.println("CFO Number is " + personData.get(0));
        //                System.out.println("CFO Name is " + personData.get(1));
        //
        //                ADFUtils.setBoundAttributeValue("AssigneeName", personData.get(1));
        //                personMail= personData.get(2);
        //                ADFUtils.findOperation("Commit").execute();
        ////                sendEmailByEmail(personMail, loanRow);
        //            }


        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            //            UserServiceUtil userService = new UserServiceUtil();
            
            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
            //            UserDetails byPersonNumber =
            //                userService.getUserDetailsByPersonNumber(personNumber);

            


            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", personName);
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");

            

         String creationDate = null;
          creationDate=  (String)ADFUtils.getBoundAttributeValue("CreationDate");
          
            DateFormat dateFormat =null;
            try{
              dateFormat =  new SimpleDateFormat("dd-MM-yyyy");
              java.util.Date date = dateFormat.parse(creationDate);
              GregorianCalendar cal = new GregorianCalendar();
              cal.setTime(date);
              dateFormat = new SimpleDateFormat("yyyy-MM-dd");
              creationDate = dateFormat.format(date);
            }
                catch(Exception e){
                    JSFUtils.addFacesErrorMessage("Error");
                    return null;
                }
            //Send Email to employee
//            LoanRequestViewRowImpl loanRow =
//                (LoanRequestViewRowImpl)ADFUtils.findIterator("LoanRequestViewIterator").getCurrentRow();
            Number noOfMonths = loanRow.getNoOfMonths();
            Number division = loanRow.getDivision();
            String salary=loanRow.getEmpBasicSalary();
            Number embSalary=null;

            try {
                embSalary = new Number(salary);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            Number loanTotalAmount=noOfMonths.multiply(embSalary);
 
            String dtatString = loanRow.getCreationDate().toString();
            
            String formattedDate = dtatString.replace('-', '/');
            System.out.println("Date is >>> " + formattedDate);

            String id = loanRow.getPersonId();


            String E = "E";
            String empNoToUpload = E + id;
            System.err.println("Emp number to upload is  >>> " +
                               empNoToUpload);
            

            
            
            UserServiceUtil userService = new UserServiceUtil();
            UserDetails userDetails = null;

            userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);
            List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();

            System.err.println("PersonType:'"+relationshipDetails.get(0).getUserPersonType()+"'");
            
            if(relationshipDetails.get(0).getUserPersonType().equalsIgnoreCase("Employee")){
            
//            String codeCombinationIdLine = getCodeCombinationId(personNumber);
//            String division1 =
//                                (String)(ADFContext.getCurrent().getSessionScope().get("division") == "" ? null :
//                                                        ADFContext.getCurrent().getSessionScope().get("division"));
//            String lob =
//                                (String)(ADFContext.getCurrent().getSessionScope().get("lob") == "" ? null :
//                                                        ADFContext.getCurrent().getSessionScope().get("lob"));
//            String costCenter =
//                                (String)(ADFContext.getCurrent().getSessionScope().get("costCenter") == "" ? null :
//                                                        ADFContext.getCurrent().getSessionScope().get("costCenter"));       
//            if (codeCombinationIdLine == null) {
//
//                JSFUtils.addFacesErrorMessage("You can't approve the request");
//                return null;
//
//            }
            
            BiReportAccess report = new BiReportAccess();
            List<Map> datFileData = null;

            try {
                datFileData = report.getLoanDatFileData(id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String assignmentNumber =
                datFileData.get(0).get("ASSIGNMENT_NUMBER").toString();
            HashMap<String, String> params = new HashMap<String, String>();


            params.put("Date", formattedDate);
            params.put("EmployeeNumber", assignmentNumber);
            params.put("NoOfMonths", noOfMonths.toString());
            params.put("Division", division.toString());
            params.put("Amount", loanTotalAmount.toString());
          


            try {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 1);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LoanId").toString(), personNumber, "Approved", "Loan Deduction", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Approved Loan Deduction: "+value);
                //fusionFileLoader.sendFusionRequest(params, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // edit basic salary
            returnValue = "success";
            }
           if(!userEmployeeType.equals("Employee")){ 
//            String totalAmount =
//                (String)ADFUtils.getBoundAttributeValue("TotalAmount");


//            Long longCodeCombinationIdLine =
//                Long.parseLong(codeCombinationIdLine);
//            String pattern = "yyyy-MM-dd";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//            String creationDate = simpleDateFormat.format(ADFUtils.getBoundAttributeValue("CreationDate").toString());
//            System.out.println("Creation date:"+creationDate);
            String requestId =
                ADFUtils.getBoundAttributeValue("LoanId").toString();
//            String invoiceNumber = personNumber + "-" + requestId + "-Loan";
//            System.err.println("invoiceNumber >>>> " + invoiceNumber);
//            String description=personNumber+" - "+personName;
//            Map param = new HashMap();
//            param.put("vendorName",
//                      "Loan to Employee"); //select vendor_name  from poz_suppliers_v
//            param.put("vendorId",
//                      "300000006296880"); //select vendor_id from poz_suppliers_v
//            param.put("vendorSiteCode", "Riyadh");
//            param.put("ledgerId", "300000001778002");
//            param.put("orgId", "300000001642073");
//            param.put("invoiceNumber", invoiceNumber);
//            param.put("invoiceAmount", totalAmount);
//            param.put("paymentCurrencyCode", "SAR");
//            param.put("invoiceTypeLookupCode", "STANDARD");
//            param.put("termsName", "Immediate");
//            param.put("paymentMethod", "WIRE");
//            param.put("Description", description);
//            param.put("CreationDate",creationDate);
//            
//            Map line1 = new HashMap();
//            line1.put("lineNumber", "1");
//            line1.put("lineType", "ITEM");
//            line1.put("lineAmount", totalAmount); //var
//            line1.put("lineCurrencyCode", "SAR");
////            line1.put("codeCombinationId", longCodeCombinationIdLine);
//            line1.put("personNumber", personNumber);
//            line1.put("bankName", "");
//            line1.put("IBAN", "");
//            line1.put("SwiftCode", "");
//            line1.put("PaymentType", "");
//
//            List<Map> lineList = new ArrayList<Map>();
//            lineList.add(line1);
//            
//            List<Map> attacheList = new ArrayList<Map>();
//            
//            APConsumer newAPInvoice = new APConsumer();
//            returnValue = newAPInvoice.createInvoice(param, lineList, attacheList);
                                          System.err.println("Inside Invoice Mail");
                               byte[] bytes = null;
                               //bytes=this.getBusinessTripAttach();
                               BlobDomain blob =new BlobDomain(bytes);
                               OperationBinding sendMail =
                               ADFUtils.findOperation("callSendInvoiceEmailStoredPL");
                               sendMail.getParamsMap().put("p_request_type", "Loan");
                               sendMail.getParamsMap().put("p_request_number", requestId);
                               sendMail.getParamsMap().put("p_attachment", blob);
                               sendMail.execute();
           returnValue = "success";
          }
            if (userEmployeeType.equals("Employee")) {
                String codeCombinationIdLine = getCodeCombinationId(personNumber);
                String division1 =
                                    (String)(ADFContext.getCurrent().getSessionScope().get("division") == "" ? null :
                                                            ADFContext.getCurrent().getSessionScope().get("division"));
                String lob =
                                    (String)(ADFContext.getCurrent().getSessionScope().get("lob") == "" ? null :
                                                            ADFContext.getCurrent().getSessionScope().get("lob"));
                String costCenter =
                                    (String)(ADFContext.getCurrent().getSessionScope().get("costCenter") == "" ? null :
                                                            ADFContext.getCurrent().getSessionScope().get("costCenter")); 
                String totalAmount =
                    (String)ADFUtils.getBoundAttributeValue("TotalAmount");
                 String dateString =creationDate;
               
                 String formattedDate1 = dateString.replace('-', '/');
                                            //-----------------------------------------

                 //                            SimpleDateFormat oldDateFormat =
                 //                                            new SimpleDateFormat("dd/MM/yyyy");
                 //                            Date myDate = null;
                 //                            try {
                 //                            myDate = oldDateFormat.parse(formattedDate);
                 //                            } catch (ParseException e) {
                 //                            e.printStackTrace();
                 //                            }
                 //                            oldDateFormat.applyPattern("yyyy/MM/dd");
                 //                            formattedDate = oldDateFormat.format(myDate);
                                           System.out.println("myDateString  >>> " + formattedDate1);
                                           OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                           Map paramMap = oper.getParamsMap();
                                           paramMap.put("elementName", "Loan Earnings");
                                           Row elementAccountNo = (Row)oper.execute();
                                           String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                                           System.err.println("EES: AccountNumber is::"+accountNumber);
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
                                                 params.put("Amount",totalAmount.toString());
                                                 params.put("StartDate", formattedDate1);
                                                 params.put("Count",
                                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                                 params.put("Segment1","01");
                                                 params.put("Segment2",division1);
                                                 params.put("Segment3",lob);
                                                 params.put("Segment4",costCenter);
                                                 params.put("Segment5",accountNumber);
                                                 params.put("Segment6","00");
                                                 try {
                                                 fusionFileLoader = new FusionDataLoader();
                                                 System.err.println("Loan Fusion Data Loader Params is::"+params);
                                                 //EES code added by Moshina
                                                 Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 23);
                                                 String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LoanId").toString(), personNumber, "Approved", "Loan Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                                 System.out.println("TAT HDR Updated Value - Approved Loan Earnings: "+value);
                                                 //fusionFileLoader.sendFusionRequest(params, 23);
                                                 System.err.println("Loan Fusion Data Loaded Sucessfully");
                                                 } catch (Exception e) {
                                                 e.printStackTrace();
                                                 }
                                                
                                                
                                             }

                                             else {

                                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");

                                            }
                                           
                                                returnValue="success";
                                               
                                            
           }
//            APConsumer newAPInvoice = new APConsumer();
//            newAPInvoice.createInvoice(param, lineList);

        
           
//            ADFUtils.findOperation("Commit").execute();
            
        }

        if(!nextStep.getAttribute("NextAssignee").equals("Finished") || (nextStep.getAttribute("NextAssignee").equals("Finished") && returnValue.equalsIgnoreCase("success"))){
            String finalapproval = null;
            if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
                finalapproval = "Y";
            } else {
                finalapproval = "N";
            }
            System.err.println("finalapproval====" + finalapproval);
            
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"),
                                                      "Loan",(Number)nextStep.getAttribute("StepId"),(Long)ADFUtils.getBoundAttributeValue("StepId"),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT",finalapproval);
            if(nextStep.getAttribute("NextAssignee").equals("Finished") && returnValue.equalsIgnoreCase("success")){
               if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                sendEmail(personNumber, loanRow);
                sendEmailLoanForEmp(fyiEmailAddress,loanRow);
//                //               send FYI mail to Accountant
//            
//                sendEmailByEmailToAmr("Amr.Yahia@tatweer.sa", loanRow,"Amr Jad");
//                sendEmailByEmailToAmr("saud.muraibadh@tatweer.sa", loanRow,"Saud Al-muraibadh");
                    }
                }
            else {
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    if("POSITION".equals(stepType)){
                        System.err.println("Email to mail-id::"+assigneeEmail);
                        sendEmailByEmail(assigneeEmail, loanRow);    
                    }else{
                        System.err.println("Email to user-id::"+assigneeNo);
                        sendEmail(assigneeNo, loanRow);    
                    }   
                }
            }
            if (value.equalsIgnoreCase("SUCCESS")) {
                ADFUtils.findOperation("Commit").execute();
            } else {
            //            ADFUtils.findOperation("Rollback").execute();
                System.err.println("Issues");
            }
//            ADFUtils.findOperation("Commit").execute();
            
           
            JSFUtils.addFacesInformationMessage("Request has been approved");
    
            return "backToLoanRequest";
        }
        
       
        else{
            ADFUtils.findOperation("Rollback").execute();
            JSFUtils.addFacesInformationMessage("Something went wrong! please contact HR Administrator!");
            return null;
        }
    }


    public String getCodeCombinationId(String emp_number) {
        BiReportAccess BIRA = new BiReportAccess();
        String division = null;
        String lob = null;
        String costCenter = null;
        String codeCombinationId = null;


        try {
            List<Map> hcmSegmentsList = BIRA.getHCMSegments(emp_number);
            if (hcmSegmentsList.size() > 0) {
                Map currentHCMSegments = hcmSegmentsList.get(0);
                    if((currentHCMSegments.get("SEGMENT2")!=null) || (currentHCMSegments.get("SEGMENT3")!=null) || (currentHCMSegments.get("SEGMENT4")!=null)){
                division = (String)currentHCMSegments.get("SEGMENT2");
                lob = (String)currentHCMSegments.get("SEGMENT3");
                costCenter = (String)currentHCMSegments.get("SEGMENT4");
                    }
                    else
                    {
                            JSFUtils.addFacesErrorMessage("This Employee has no costing values( Division & lob & Cost Center ) !!!!");
                            return null;
                        
                        }
            }

            else {

                JSFUtils.addFacesErrorMessage("This Employee has no costing values( Division & lob & Cost Center ) !!!!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map> combinationIdList = null;


        try {
            combinationIdList =
                    BIRA.getCodeCombinationId("01", division, lob, costCenter,
                                              "1103010102", "00", "0000",
                                              "0000");
            com.view.uiutils.JSFUtils.setExpressionValue("#{sessionScope.division}", division);
            com.view.uiutils.JSFUtils.setExpressionValue("#{sessionScope.lob}", lob);
            com.view.uiutils.JSFUtils.setExpressionValue("#{sessionScope.costCenter}", costCenter);

            //            BIRA.getCodeCombinationId("01","03","00","2010004","5102010003","00","0000","0000");


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (combinationIdList.get(0).get("CODE_COMBINATION_ID")!=null) {
            codeCombinationId =
                    (String)combinationIdList.get(0).get("CODE_COMBINATION_ID");
            System.err.println("CODE_COMBINATION_ID 1 >>>> " +
                               combinationIdList.get(0).get("CODE_COMBINATION_ID"));


            return codeCombinationId;

        }

        else

        {
            CodeCombinationConsumer codeCombinationConsumer =
                new CodeCombinationConsumer();
            codeCombinationId =
                    codeCombinationConsumer.createCodeCombination("THC Ledger SA",
                                                                  "01",
                                                                  division,
                                                                  lob,
                                                                  costCenter,
                                                                  "1103010102",
                                                                  "00", "0000",
                                                                  "0000",
                                                                  "PaaS.Self Service@tatweer.sa",
                                                                  "PAASTatweer@2020",
                                                                  "https://egwo-dev1.fa.em2.oraclecloud.com/fscmService/AccountCombinationService");


            return codeCombinationId;
            //        String codeComb1 =
            //            "01-" + division + "-" + lob + "-" + costCenter +
            //            "-1103010102" + "-00" + "-0000" + "-0000";
            //        JSFUtils.addFacesErrorMessage("You can't create AP Invoice as this code combination (" +
            //                                      codeComb1 +
            //                                      ") isn't created before");
            //    return null;
        }

    }


    public String reject_action() {
        // Add event code here...
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String LoanTypeName="Loan" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("LoanTypeName", LoanTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonId.inputValue}");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        //Get requstor ID by Number
        //        UserServiceUtil userService = new UserServiceUtil();
        //        UserDetails byPersonNumber =
        //            userService.getUserDetailsByPersonNumber(personNumber);
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        //Send Email to employee
        Row loanRow =
            ADFUtils.findIterator("LoanRequestViewIterator").getCurrentRow();
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"),
                                                      "Loan",
                                                 (Number)nextStep.getAttribute("StepId"),
                                                  new Long(0),
                                                  null,
                                                  "REJECT_ACT", "");
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
        sendEmail(personNumber, loanRow);
        }
        ADFUtils.findOperation("Commit").execute();
        
        JSFUtils.addFacesInformationMessage("Loan Request has been Rejected");

        return "backToLoanRequest";
    }
//--
    public void sendLoanEmail(String from, String to,
                              LoanRequestViewRowImpl subject, String personFYI) {
        
        String status = subject.getRequestStatus();
        String edited = subject.getEditRequest();
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
            subj = "Loan Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Loan Request, and below the details";
            
        }else if("Waiting Withdraw Approval".equals(status)){
            subj = "Loan Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Loan Request, and below the details";
            
        }else if("Withdrawn Rejected".equals(status)){
            subj = "Loan Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of Loan request as below";
            
        }else if(edited!=null && edited.equals("Y") && "PENDING".equals(status)){
            subj = "Loan Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Loan Request, and below the details";
            
        }else{
            subj = "Loan Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Loan request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("Withdrawn".equals(status)){
                subj = "Loan Request has been withdraw";
                hdrMsg= "Kindly be informed you that the following Loan request has been withdraw";
            }
            if("APPROVED".equals(status)){
                subj = "Loan Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Loan request has been Approved";
            }
           
        }
//                JSFUtils.addFacesInformationMessage(subj);
//                JSFUtils.addFacesInformationMessage(hdrMsg); 
        
        if (to == null) {
           // to = "vf.khayal@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
               "Dear " + assigneeName + "," + "\n" +
            "<br/>" + hdrMsg + 
//            "Kindly find below the details of Loan request as below" +
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
            subject.getPersonId() + "</td></tr>\n" +
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
            "      <td width=\"50%\">" + subject.getNoOfMonths() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Basic Salary\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getEmpBasicSalary() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Total Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTotalAmount() + "</td>\n" +
            "    </tr>"+
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Division\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDivision() + " </td>\n" +
            "    </tr>" + "      <th>\n" +
            "        &nbsp;Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCreationDate() +
            "</td>\n" +
            "    </tr>" + "  </table>";
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryLoanIterator"); 
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
        //        sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject", subj );
//                                    "Loan Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
    }

    public void sendLoanEmailToAmrJad(String from, String to,
                              LoanRequestViewRowImpl subject,String name) {
        if (to == null) {
          //  to = "vf.khayal@gmail.com";
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear Mr "+  name + "," + "\n" +
            "<br/>" +
            "Kindly find below the details of Loan request as below" +
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
            subject.getPersonId() + "</td></tr>\n" +
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
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Loan Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number of Months\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNoOfMonths() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Basic Salary\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getEmpBasicSalary() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Total Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTotalAmount() + "</td>\n" +
            "    </tr>"+
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Division\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDivision() + " </td>\n" +
            "    </tr>" + "      <th>\n" +
            "        &nbsp;Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCreationDate() +
            "</td>\n" +
            "    </tr>" + "  </table>";
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryLoanIterator");
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
        //        sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Loan Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
    }


    public void sendEmail(String personNumber, Row subject) {
        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendLoanEmail("OFOQ.HR@TATWEER.SA", email,
                          (LoanRequestViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendLoanEmail("OFOQ.HR@TATWEER.SA", personEmail,
                      (LoanRequestViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    public void sendEmailByEmailToAmr(String personEmail, Row subject,String name) {

        sendLoanEmailToAmrJad("OFOQ.HR@TATWEER.SA", personEmail,
                      (LoanRequestViewRowImpl)subject,name);
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    

    public String getEmail(String personNumber) {
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);

        JAXBElement<String> aXBElement =
            userDetails.getUserPersonDetails().get(0).getEmailAddress();
        return aXBElement.getValue();
    }


    public void calcTotalLoanAmount(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        Number numberOfMonths = (Number)valueChangeEvent.getNewValue();
        String basicSalary =
            (String)ADFUtils.getBoundAttributeValue("EmpBasicSalary");
        Number totalLoanAmount = null;


        try {
            totalLoanAmount = numberOfMonths.multiply(new Number(basicSalary));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ADFUtils.setBoundAttributeValue("TotalAmount",
                                        totalLoanAmount.toString());

    }
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
           // JSFUtils.addFacesErrorMessage("Session Expired! Please open the application through SAAS!");
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }
    public void sendLoanInformationEmployee(String from, String to,
                              LoanRequestViewRowImpl subject) {
        if (to == null) {
          //  to = "vf.khayal@gmail.com";
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Loan request has been Approved" +
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
            subject.getPersonId() + "</td></tr>\n" +
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
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Loan Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number of Months\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNoOfMonths() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Basic Salary\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getEmpBasicSalary() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Total Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTotalAmount() + "</td>\n" +
            "    </tr>"+
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Division\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDivision() + " </td>\n" +
            "    </tr>" + "      <th>\n" +
            "        &nbsp;Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCreationDate() +
            "</td>\n" +
            "    </tr>" + "  </table>";
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryLoanIterator");
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
        //        sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Loan Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
    }
    public void sendEmailLoanForEmp(String email, Row subject) {
//            String email = getEmail(personNumber);
            System.out.println("Manager Email is " + email);
            if (null == email) {
                JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
            } else {
                sendLoanInformationEmployee("OFOQ.HR@TATWEER.SA", email,
                              (LoanRequestViewRowImpl)subject);
                JSFUtils.addFacesInformationMessage("Mail has been sent");
            }
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

               ViewObject reqVo = ADFUtils.findIterator("LoanRequestViewIterator").getViewObject();
               Row currRow = reqVo.getCurrentRow();
               String status = currRow.getAttribute("RequestStatus").toString();
               String personNo = currRow.getAttribute("PersonId").toString();
               
               currRow.setAttribute("StepId", "1");
               String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
               String LoanTypeName="Loan" +'-'+ personLocation;
               ADFContext.getCurrent().getPageFlowScope().put("LoanTypeName", LoanTypeName);
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
                   Long stepid = (Long)ADFUtils.getBoundAttributeValue("StepId");
                   String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"), 
                                                                                "Loan", (Number)nextStep.getAttribute("NextStepId"), 
                                                                                stepid, 
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
                   
                   Long stepid = (Long)ADFUtils.getBoundAttributeValue("StepId");
                   String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"), 
                                                                                "Loan", (Number)nextStep.getAttribute("NextStepId"), 
                                                                                stepid, 
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
           ViewObject reqVo = ADFUtils.findIterator("LoanRequestViewIterator").getViewObject();
           Row currRow = reqVo.getCurrentRow();
           String personNo = ADFUtils.getBoundAttributeValue("PersonId").toString();
           String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
           String LoanTypeName="Loan" +'-'+ personLocation;
           ADFContext.getCurrent().getPageFlowScope().put("LoanTypeName", LoanTypeName);
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
               ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"),
                                                         "Loan",
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
           
           return "backToLoanRequest";
       }
       
       public String approve_withdraw() {
           
           ViewObject reqVo = ADFUtils.findIterator("LoanRequestViewIterator").getViewObject();
           Row currRow = reqVo.getCurrentRow();
           String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
           String LoanTypeName="Loan" +'-'+ personLocation;
           ADFContext.getCurrent().getPageFlowScope().put("LoanTypeName", LoanTypeName);
           OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
           Row nextStep = (Row)nextOpr.execute();
           String emailNotification = (String)nextStep.getAttribute("EmailNotification");
           String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
           String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";   
           
           String personNumber = ADFUtils.getBoundAttributeValue("PersonId").toString();
            
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
              String dateString =
                   ADFUtils.getBoundAttributeValue("CreationDate").toString(); 
              
               List<Map> datFileData = null;
               try {
                   datFileData = report.getLoanWithdrawDatfileInfo(personNumber, changeFormatOfDate("dd-MM-yyyy", "MM-dd-yyyy", dateString));
               } catch (Exception e) {
                   e.printStackTrace();
               } 
               
               String assignmentNumber =
                   datFileData.get(0).get("ASSIGNMENT_NUMBER")==null?"":
                   datFileData.get(0).get("ASSIGNMENT_NUMBER").toString();
               String entryCount =
                   datFileData.get(0).get("MULTIPLEENTRYCOUNT")==null?"":
                   datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString(); 
               String sDate =
                   datFileData.get(0).get("EFFECTIVE_START_DATE")==null?"":
                   datFileData.get(0).get("EFFECTIVE_START_DATE").toString();
               String eDate =
                   datFileData.get(0).get("EFFECTIVE_END_DATE")==null?"":
                   datFileData.get(0).get("EFFECTIVE_END_DATE").toString();
               String noOfMonths =
                   datFileData.get(0).get("NO_OF_MONTHS")==null?"":
                   datFileData.get(0).get("NO_OF_MONTHS").toString();
               String div =
                   datFileData.get(0).get("DIVISION")==null?"":
                   datFileData.get(0).get("DIVISION").toString();
               String totalAmt =
                   datFileData.get(0).get("TOTAL_AMOUNT")==null?"":
                   datFileData.get(0).get("TOTAL_AMOUNT").toString();
               String loanCreateDate =
                   datFileData.get(0).get("LOAN_CREATION_DATE")==null?"":
                   datFileData.get(0).get("LOAN_CREATION_DATE").toString();
//               
               HashMap<String, String> params = new HashMap<String, String>(); 
               params.put("AssignmentNo", assignmentNumber);
               params.put("Count", entryCount);
               params.put("EffStartDate", sDate);
               params.put("EffEndDate", eDate);
               params.put("LoanCreationDate", loanCreateDate);
               params.put("NoOfMonths", noOfMonths);
               params.put("Division", div);
               params.put("TotalAmount", totalAmt);
               System.err.println("LOAN WITHDRAW : Fusion upload calling with values : "+params);
//               
               try {
                   fusionFileLoader = new FusionDataLoader();
                   //EES code added by Moshina
                   Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 13);
                   String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LoanId").toString(), personNumber, "Withdrawn", "Loan Deduction", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                   System.out.println("TAT HDR Updated Value - Withdrawn Loan Deduction: "+value);
                   //fusionFileLoader.sendFusionRequest(params, 13);
                   
               } catch (Exception e) {
                   e.printStackTrace();
               } 
               
               String value =
                   ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"),
                                                         "Loan",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                       (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
               
               if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                        sendEmailToPerson(personNumber, currRow);
                        sendEmailByEmail(fyiEmailAddress, currRow);
               } 
           }else{
               
               String value =
                   ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LoanId"),
                                                         "Loan",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
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
           return "backToLoanRequest";
           }

       public String editPendingRequest() {
           ViewObject reqVo = ADFUtils.findIterator("LoanRequestViewIterator").getViewObject();
           Row currRow = reqVo.getCurrentRow(); 
           String personNo = currRow.getAttribute("PersonId").toString();
           
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

       public void sendEmailToPerson(String personNumber, Row subject) {
           String email = getEmail(personNumber);

           if (null == email) {
               JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
           } else {
               sendLoanEmail("OFOQ.HR@TATWEER.SA", email,
                                        (LoanRequestViewRowImpl)subject, "Y");
               JSFUtils.addFacesInformationMessage("Mail has been sent");
           }
       } 
       
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("LoanRequestViewIterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("LoanRequestViewCriteria"));
            vo.executeQuery();
        }
    }
}
