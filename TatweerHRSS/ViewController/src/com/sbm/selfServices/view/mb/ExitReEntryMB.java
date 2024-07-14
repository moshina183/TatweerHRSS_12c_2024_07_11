package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.ExceptionalRewardViewRowImpl;
import com.sbm.selfServices.model.views.up.ExitReEntryVORowImpl;

import com.sbm.selfServices.model.views.up.ManPowerRequestViewRowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;
import com.sbm.selfServices.view.utils.PersonInfo;
import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStripApp.FilmStripBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;


import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.JAXBElement;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputFile;
import oracle.adf.view.rich.component.rich.input.RichInputNumberSpinbox;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;

import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.adf.view.rich.component.rich.output.RichOutputText;

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

public class ExitReEntryMB {


    private RichSelectBooleanCheckbox selfFlag;
    private RichSelectBooleanCheckbox spouseFlag;
    private RichSelectBooleanCheckbox childrenFlag;
    private RichInputNumberSpinbox noOfChildren;
    private RichSelectOneChoice exitType;
    private RichInputFile inputFile;
    private String attachmentFileName;
    private RichPopup attachmentPopup;

    public ExitReEntryMB() {
    }
    private FusionDataLoader fusionFileLoader;

    public String newRequest() {
        // Add event code here...
        PersonInfo personInfo =
            (PersonInfo)JSFUtils.resolveExpression("#{PersonInfo}");
        JSFUtils.setExpressionValue("#{pageFlowScope.ExitReEntryPersonNumber}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.ExitReEntryPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
        BiReportAccess BIRA = new BiReportAccess();
        try {
            List<Map> list =
                BIRA.getEmployeeChilds(JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}").toString());
            for (Map currentEmployeeChilds : list) {

                String sDate1 =
                    currentEmployeeChilds.get("HIRE_DATE").toString();
                Date d = new SimpleDateFormat("dd-MM-yyyy").parse(sDate1);
                personInfo.setNationality(currentEmployeeChilds.get("NATIONALITY").toString());
                personInfo.setChildsNo(currentEmployeeChilds.get("NUMBER_OF_CHILDS").toString());
                personInfo.setMaritalStatus(currentEmployeeChilds.get("MARITAL_STATUS").toString());
                personInfo.setHireDate(d);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "newRequest";
    }

    public String saveRequest() {
        // Add event code here...
        Object employeeId =
            JSFUtils.resolveExpression("#{PersonInfo.employeeId}");
        if (employeeId == null) {
            JSFUtils.addFacesErrorMessage("You don't have Person ID, So you can't save the request");
            return null;
        }
        ADFUtils.setBoundAttributeValue("ActionTaken", "SAVED");
        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been saved");
        ADFUtils.findOperation("Execute").execute();

        return "backToRequests";
    }


    public static java.util.Date convertDomainDateToUtilDate(oracle.jbo.domain.Date domainDate) {
        java.util.Date date = null;
        if (domainDate != null) {
            java.sql.Date sqldate = domainDate.dateValue();
            date = new Date(sqldate.getTime());
        }
        return date;
    }

    public String submitRequest() {
        // Add event code here...
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 

        oracle.jbo.domain.Date date =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("TravelDate");
        System.err.println("Req date1:"+date);
        if (date == null) {

            JSFUtils.addFacesErrorMessage("You should select Travel Date");
            return null;

        }
        Date travelDate = convertDomainDateToUtilDate(date);
        System.err.println("travelDate2:"+travelDate);

        OperationBinding requestsInSameTravelDate =
            ADFUtils.findOperation("getExitRequestsInSameTravelDate");
        requestsInSameTravelDate.getParamsMap().put("bindPersonNo",
                                                    personNumber);
//        requestsInSameTravelDate.getParamsMap().put("travelDateBind",
//                                                    travelDate);

        Number noOfRequestsInSameTravelDate =
            (Number)requestsInSameTravelDate.execute();
        System.out.println("noOfRequestsInSameTravelDate  >>> " +
                           noOfRequestsInSameTravelDate);
        if (noOfRequestsInSameTravelDate.compareTo(0) == 1) {

            JSFUtils.addFacesErrorMessage("You can't submit the request as there is another request on the same travel date");
            return null;

        }

        String typeOfExitReEntry =
            ADFUtils.getBoundAttributeValue("TypeOfExitReEntry").toString();
        if(typeOfExitReEntry.equalsIgnoreCase("Work Need"))
        {
            
                System.err.println("typeOfExitReEntry 1:"+typeOfExitReEntry);
                OperationBinding btInSameDateOper =
                    ADFUtils.findOperation("getExitReEntryBT");
                
        //                requestsInSameTravelDate.getParamsMap().put("bindDate",
        //                                                            travelDate);
                System.err.println("travelDate :"+travelDate);
                btInSameDateOper.getParamsMap().put("bindPersonNo",
                                                            personNumber);
                
        Number  count = (Number)btInSameDateOper.execute();
        System.err.println("Count :"+count);
        if(count.compareTo(0) == 0)
        {

            JSFUtils.addFacesErrorMessage("You can't submit the request as when you select 'Work Need' in Type of exit re-entry,This request must related to Business Trip");
            return null;  
            

        }
            
            }
        else
        {
            System.err.println("typeOfExitReEntry 2:"+typeOfExitReEntry);
            
            }


        String nationality =
            (String)JSFUtils.resolveExpression("#{PersonInfo.nationality}");
        String childsNo =
            (String)JSFUtils.resolveExpression("#{PersonInfo.childsNo}");
        Number childsNumber = null;
        try {
            childsNumber = new Number(childsNo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String maritalStatus =
            (String)JSFUtils.resolveExpression("#{PersonInfo.maritalStatus}");
        Date hireDate =
            (Date)JSFUtils.resolveExpression("#{PersonInfo.hireDate}");


        Number embTotalBalance = null;
        if (nationality.equalsIgnoreCase("Saudi")) {

            //            embTotalBalance = new Number(200);k
            JSFUtils.addFacesErrorMessage("You can't submit the request as user nationality shouldn't be Saudi");
            return null;

        } else if (maritalStatus.equalsIgnoreCase("S")) {

            embTotalBalance = new Number(200);
        } else {
            if (childsNumber.equals(0)) {

                embTotalBalance = new Number(400);

            } else if (childsNumber.equals(1)) {

                embTotalBalance = new Number(600);

            } else if (childsNumber.equals(2)) {

                embTotalBalance = new Number(800);

            } else {

                embTotalBalance = new Number(1000);

            }


        }
        Number xFactor = null;
        String noOfMonths =
            ADFUtils.getBoundAttributeValue("FamilyPeriod").toString();
        if (noOfMonths.equalsIgnoreCase("2 Months")) {
            xFactor = new Number(200);

        } else if (noOfMonths.equalsIgnoreCase("3 Months")) {
            xFactor = new Number(300);

        } else if (noOfMonths.equalsIgnoreCase("4 Months")) {
            xFactor = new Number(400);

        } else if (noOfMonths.equalsIgnoreCase("5 Months")) {
            xFactor = new Number(500);

        } else if (noOfMonths.equalsIgnoreCase("6 Months")) {
            xFactor = new Number(600);

        } else if (noOfMonths.equalsIgnoreCase("7 Months")) {
            xFactor = new Number(700);

        } else if (noOfMonths.equalsIgnoreCase("8 Months")) {
            xFactor = new Number(800);

        } else if (noOfMonths.equalsIgnoreCase("9 Months")) {
            xFactor = new Number(900);

        } else if (noOfMonths.equalsIgnoreCase("10 Months")) {
            xFactor = new Number(1000);

        } else if (noOfMonths.equalsIgnoreCase("11 Months")) {
            xFactor = new Number(1100);

        } else if (noOfMonths.equalsIgnoreCase("12 Months")) {
            xFactor = new Number(1200);

        }


        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(hireDate);
        int dayOfHire = cal.get(Calendar.DAY_OF_MONTH);
        int monthOfHire = cal.get(Calendar.MONTH);
        java.util.Date currentDate = new Date();
        cal.setTime(currentDate);
        int currentYear = cal.get(Calendar.YEAR);
        cal2.set(Calendar.YEAR, currentYear);
        cal2.set(Calendar.MONTH, monthOfHire);
        cal2.set(Calendar.DAY_OF_MONTH, dayOfHire);
        Date dateToCheck = cal2.getTime();
        Date startOfYearDate = null;
        Date endOfYearDate = null;
        if (dateToCheck.compareTo(currentDate) > 0) {
            Calendar cal3 = Calendar.getInstance();
            cal3.set(Calendar.YEAR, (currentYear - 1));
            cal3.set(Calendar.MONTH, monthOfHire);
            cal3.set(Calendar.DAY_OF_MONTH, dayOfHire);
            Calendar cal4 = Calendar.getInstance();
            cal4.set(Calendar.YEAR, currentYear);
            cal4.set(Calendar.MONTH, monthOfHire);
            cal4.set(Calendar.DAY_OF_MONTH, dayOfHire - 1);

            startOfYearDate = cal3.getTime();
            endOfYearDate = cal4.getTime();
            System.out.println("startOfYearDate   " + startOfYearDate);
            System.out.println("endOfYearDate   " + endOfYearDate);
        } else {

            Calendar cal3 = Calendar.getInstance();
            cal3.set(Calendar.YEAR, currentYear);
            cal3.set(Calendar.MONTH, monthOfHire);
            cal3.set(Calendar.DAY_OF_MONTH, dayOfHire);
            Calendar cal4 = Calendar.getInstance();
            cal4.set(Calendar.YEAR, currentYear + 1);
            cal4.set(Calendar.MONTH, monthOfHire);
            cal4.set(Calendar.DAY_OF_MONTH, dayOfHire - 1);
            startOfYearDate = cal3.getTime();
            endOfYearDate = cal4.getTime();

            System.out.println("startOfYearDate   " + startOfYearDate);
            System.out.println("endOfYearDate   " + endOfYearDate);

        }

        OperationBinding totalConsumedPerYear =
            ADFUtils.findOperation("getTotalConsumedAmountPerYear");
        totalConsumedPerYear.getParamsMap().put("bindPersonNo", personNumber);
        totalConsumedPerYear.getParamsMap().put("bindStartOfYear",
                                                startOfYearDate);
        totalConsumedPerYear.getParamsMap().put("bindEndOfYear",
                                                endOfYearDate);
        Number totalConsumedThisYear = (Number)totalConsumedPerYear.execute();
        System.out.println("totalConsumedThisYear  >>> " +
                           totalConsumedThisYear);
        String visaInfo =
            ADFUtils.getBoundAttributeValue("VisaInformation1").toString();
        Number wantedAmount = new Number(0);

        if (maritalStatus.equalsIgnoreCase("S")) {

            if (visaInfo.equalsIgnoreCase("Individual Exit Re-Entry")) {

                if (exitType.getValue().toString().equalsIgnoreCase("Personal")) {

                    if ((totalConsumedThisYear.compareTo(0)) == 1) {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(0));
                    } else {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(200));

                    }
                }


                else if (exitType.getValue().toString().equalsIgnoreCase("Work Need")) {

                    ADFUtils.setBoundAttributeValue("Amount", xFactor);

                }


            } else if (visaInfo.equalsIgnoreCase("Multi Exit Re-Entry")) {

                if (exitType.getValue().toString().equalsIgnoreCase("Personal")) {

                    if ((totalConsumedThisYear.compareTo(0)) == 1) {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(0));
                    } else {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(200));

                    }
                } else if (exitType.getValue().toString().equalsIgnoreCase("Work Need")) {

                    ADFUtils.setBoundAttributeValue("Amount", xFactor);

                }


            }

        } else { // If Married


            if (visaInfo.equalsIgnoreCase("Individual Exit Re-Entry")) {

                if (exitType.getValue().toString().equalsIgnoreCase("Personal")) {

                    if ((totalConsumedThisYear.compareTo(embTotalBalance)) ==
                        0) {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(0));
                        JSFUtils.addFacesInformationMessage("Your remaining balance this year is Zero");
                    } else if ((totalConsumedThisYear.compareTo(embTotalBalance)) ==
                               -1) {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(200));

                    }

                }


                else if (exitType.getValue().toString().equalsIgnoreCase("Work Need")) {


                    ADFUtils.setBoundAttributeValue("Amount", xFactor);


                }


            } else if ((visaInfo.equalsIgnoreCase("Multi Exit Re-Entry"))) {


                if (exitType.getValue().toString().equalsIgnoreCase("Personal")) {

                    if ((totalConsumedThisYear.compareTo(embTotalBalance)) ==
                        0) {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        new Number(0));
                        JSFUtils.addFacesInformationMessage("Your remaining balance this year is Zero");
                    } else if ((totalConsumedThisYear.compareTo(embTotalBalance)) ==
                               -1) {
                        Number remainingThisYear =
                            (Number)embTotalBalance.minus(totalConsumedThisYear);

                        if (selfFlag.isSelected() && spouseFlag.isSelected()) {
                            if (childrenFlag.isSelected()) {
                                Number children =
                                    (Number)noOfChildren.getValue();
                                children = children.add(2);
                                wantedAmount = xFactor.multiply(children);

                            } else {
                                wantedAmount = xFactor.multiply(2);
                            }
                        } else if (selfFlag.isSelected() &&
                                   childrenFlag.isSelected() &&
                                   (!(spouseFlag.isSelected()))) {
                            Number children = (Number)noOfChildren.getValue();
                            children = children.add(1);
                            wantedAmount = xFactor.multiply(children);

                        } else if (spouseFlag.isSelected() &&
                                   childrenFlag.isSelected() &&
                                   (!(selfFlag.isSelected()))) {
                            Number children = (Number)noOfChildren.getValue();
                            children = children.add(1);
                            wantedAmount = xFactor.multiply(children);

                        }


                        if ((remainingThisYear.compareTo(wantedAmount) == 1) ||
                            (remainingThisYear.compareTo(wantedAmount) == 0)) {

                            ADFUtils.setBoundAttributeValue("Amount",
                                                            wantedAmount);

                        } else {
                            ADFUtils.setBoundAttributeValue("Amount",
                                                            remainingThisYear);
                            JSFUtils.addFacesInformationMessage("Your remaining balance this year is : " +
                                                                remainingThisYear);

                        }


                    }
                }


                else if (exitType.getValue().toString().equalsIgnoreCase("Work Need")) {


                    ADFUtils.setBoundAttributeValue("Amount", xFactor);


                }


            }


            else if ((visaInfo.equalsIgnoreCase("Individual Exit Re-Entry or Multi for Family"))) {

                if ((totalConsumedThisYear.compareTo(embTotalBalance)) == 0) {

                    ADFUtils.setBoundAttributeValue("Amount", new Number(0));
                    JSFUtils.addFacesInformationMessage("Your remaining balance this year is Zero");
                } else if ((totalConsumedThisYear.compareTo(embTotalBalance)) ==
                           -1) {
                    Number remainingThisYear =
                        (Number)embTotalBalance.minus(totalConsumedThisYear);

                    if (selfFlag.isSelected() && spouseFlag.isSelected()) {
                        if (childrenFlag.isSelected()) {
                            Number children = (Number)noOfChildren.getValue();
                            children = children.add(2);
                            wantedAmount = xFactor.multiply(children);

                        } else {
                            wantedAmount = xFactor.multiply(2);
                        }
                    } else if (selfFlag.isSelected() &&
                               childrenFlag.isSelected() &&
                               (!(spouseFlag.isSelected()))) {
                        Number children = (Number)noOfChildren.getValue();
                        children = children.add(1);
                        wantedAmount = xFactor.multiply(children);

                    } else if (spouseFlag.isSelected() &&
                               childrenFlag.isSelected() &&
                               (!(selfFlag.isSelected()))) {
                        Number children = (Number)noOfChildren.getValue();
                        children = children.add(1);
                        wantedAmount = xFactor.multiply(children);

                    }


                    if ((remainingThisYear.compareTo(wantedAmount) == 1) ||
                        (remainingThisYear.compareTo(wantedAmount) == 0)) {

                        ADFUtils.setBoundAttributeValue("Amount",
                                                        wantedAmount);

                    } else {
                        ADFUtils.setBoundAttributeValue("Amount",
                                                        remainingThisYear);
                        JSFUtils.addFacesInformationMessage("Your remaining balance this year is : " +
                                                            remainingThisYear);

                    }


                }
            }

        }
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId")); 
        
        String managerNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

        Long lineManagerID =
            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        String stringLineManagerID = lineManagerID.toString();
        
//        if (stringLineManagerID == null) {
//            JSFUtils.addFacesErrorMessage("You donot have Line Manager, So you can not submit the request");
//            return null;
//        }
//        ADFUtils.setBoundAttributeValue("Assignee", managerNumber);
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
 
       UserServiceUtil userService = new UserServiceUtil();
       UserDetails userDetails = null;

       userDetails = userService.getUserDetailsByPersonNumber(personNumber);
       List<UserWorkRelationshipDetails> relationshipDetails =
           userDetails.getUserWorkRelationshipDetails();

       String department = relationshipDetails.get(0).getDepartmentName();
       String position = relationshipDetails.get(0).getPositionName();
       
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
       
       
       //Exist
       
       
        ADFUtils.setBoundAttributeValue("PersonPosition", position);
        ADFUtils.setBoundAttributeValue("PersonDepartment", department);
        String job = relationshipDetails.get(0).getJobName().getValue();
        ADFUtils.setBoundAttributeValue("PersonJob", job);
        String location = relationshipDetails.get(0).getLocationName();
        ADFUtils.setBoundAttributeValue("PersonLocation", location);
        String gradeCode = relationshipDetails.get(0).getGradeCode();
        System.err.println("gradeCode is >>> " + gradeCode);
        ADFUtils.setBoundAttributeValue("PersonGrade", gradeCode);
        //        ADFUtils.setBoundAttributeValue("Amount", );


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
        
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        
        ADFUtils.findOperation("Commit").execute();
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        String value=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"), 
                                                "ExitReEntry", (Number)nextStep.getAttribute("NextStepId"), 
                                                stepid.longValue(), 
                                                (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                act, "");
        Row exitReEntryRow =
            ADFUtils.findIterator("ExitReEntryVO1Iterator").getCurrentRow();
        JSFUtils.addFacesInformationMessage("Request has been submitted");
//        ADFUtils.findOperation("Execute").execute();
//        if (managerEmail == null) {
//            JSFUtils.addFacesErrorMessage("You donot have Line Manager Email, So mail can't be sent to manager");
//            return "backToRequests";
//        }
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, exitReEntryRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, exitReEntryRow);    
            } 
        }
        return "backToRequests";

    }

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendExitReEntryEmail("OFOQ.HR@TATWEER.SA", personEmail,
                             (ExitReEntryVORowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendExitReEntryEmail(String from, String to,
                                     ExitReEntryVORowImpl subject, String personFYI) {
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
        if("Withdrawn".equals(status)){
            subj = "Exit Re-Entry Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Exit Re-Entry Request, and below the details";
            
        }else if("Waiting Withdraw Approval".equals(status)){
            subj = "Exit Re-Entry Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Exit Re-Entry Request, and below the details";
            
        }else if("Withdrawn Rejected".equals(status)){
            subj = "Exit Re-Entry Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of Exit Re-Entry request as below";
            
        }else if(edited!=null && edited.equals("Y") && "PENDING".equals(status)){
            subj = "Exit Re-Entry Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Exit Re-Entry Request, and below the details";
            
        }else{
            subj = "Exit Re-Entry Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Exit Re-Entry request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("Withdrawn".equals(status)){
                subj = "Exit Re-Entry Request has been withdraw";
                hdrMsg= "Kindly be informed you that Exit Re- Entry Request has been withdraw, Please find below the details";
            }
            if("APPROVED".equals(status)){
                subj = "Exit Re-Entry Request has been APPROVED";
                hdrMsg= "Kindly be informed you that Exit Re- Entry Request has been approved, Please find below the details";
            }
           
        }
//                                JSFUtils.addFacesInformationMessage(subj);
//                                JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
           // to = "heleraki@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        String into = "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" +
            hdrMsg +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String personalInformation =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
            " <tr><th>&nbsp;Requester Name</th>\n" +
            "      <td width=\"50%\">" + subject.getPersonName() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Position\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonPosition() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Job\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonJob() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Grade\n" +
            "      </th>\n" +
        "      <td width=\"50%\">" + (subject.getPersonGrade()!=null?subject.getPersonGrade():" ") +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Department\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonDepartment() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Location\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonLocation() +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String verticalSpace = " <p>&nbsp;</p>";

        String overtimeDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Exit Re-Entry Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Visa Information\n" +
            "      </th>\n" +
        "      <td width=\"50%\">" +( subject.getVisaInformation()!=null?subject.getVisaInformation():" ") +
            "</td>\n" +
            "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Travel Date\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +( subject.getTravelDate()!=null?subject.getTravelDate():" ") +
        "</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Type Of Exit Re-Entry\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +( subject.getTypeOfExitReEntry()!=null?subject.getTypeOfExitReEntry():" ") +
        "</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Onlin Payment\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +( subject.getOnlinPayment()!=null?subject.getOnlinPayment():" ") +
        "</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Request Reason\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +( subject.getRequestReason()!=null?subject.getRequestReason():" ") +
        "</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Comments\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +( subject.getComments()!=null?subject.getComments():" ") +
        "</td>\n" +
        "    </tr>\n" +
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryExitIterator");
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
        sendMail.getParamsMap().put("receiver",
                                    to );
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject", subj);
//                                    "Exit Re-Entry Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }

    public String approveRequest() {
        // Add event code here...
        String personMail = null;
        int updateInsert=0;
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        String personNo=
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
//        System.out.println("next step is " +
//                           nextStep.getAttribute("NextStepId"));
//        ADFUtils.setBoundAttributeValue("StepId",
//                                        nextStep.getAttribute("NextStepId"));
//        ADFUtils.setBoundAttributeValue("Assignee",
//                                        nextStep.getAttribute("NextAssignee"));
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
        
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

//        if (nextStep.getAttribute("NextAssignee").equals("Admin Services Supervisor")) {
//            Row exitReEntryRow =
//                ADFUtils.findIterator("ExitReEntryVO1Iterator").getCurrentRow();
//
//
//            BiReportAccess report = new BiReportAccess();
//
//            List<Map> personData = null;
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("Admin Services Supervisor");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.err.println("Admin Services Supervisor Number is " +
//                               personData.get(0).get("PERSON_NUMBER"));
//            System.err.println("Admin Services Supervisor Name is " +
//                               personData.get(0).get("DISPLAY_NAME"));
//            System.err.println("Admin Services Supervisor Email " +
//                               personData.get(0).get("EMAIL_ADDRESS"));
//            ADFUtils.setBoundAttributeValue("AssigneeName",
//                                            personData.get(0).get("DISPLAY_NAME").toString());
//            String value =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
//                                                      "ExitReEntry",
//                                                      (Number)nextStep.getAttribute("StepId"),
//                                                      stepid.longValue(),
//                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                      "APPROVE_ACT", "N");
//            ADFUtils.findOperation("Commit").execute();
//            updateInsert=1;
//            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                             exitReEntryRow);
//            }
//        }
        Row exitReEntryRow =
                ADFUtils.findIterator("ExitReEntryVO1Iterator").getCurrentRow();
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            //            UserServiceUtil userService = new UserServiceUtil();
            String personNumber =
                (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
            System.out.println("the person number is ==============> " +
                               personNumber);

            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");

            Number amount = (Number)ADFUtils.getBoundAttributeValue("Amount");
            if (amount.compareTo(0) == 1) {
                String dateString =
                    ADFUtils.getBoundAttributeValue("CreationDate").toString();
                 

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
                            report.getExitReEntryDatFileData(personNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HashMap<String, String> params = new HashMap<String, String>();

                if (datFileData.size() > 0) {
                    params.put("AssignmentNumber",
                               datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                    params.put("Amount",
                               ADFUtils.getBoundAttributeValue("Amount").toString());
                    params.put("Date", formattedDate);
                    params.put("Count",
                               datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                }

                else {

                    JSFUtils.addFacesErrorMessage("You can't create Exit Re-Entry element as the Assignment number or Multiple Entry Count is null");

                }
                try {
                    fusionFileLoader = new FusionDataLoader();
                    //EES code added by Moshina
                    Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 4);
                    String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("ExitReEntryId").toString(), personNumber, "Approved", "Exit Re Entry Visa", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                    System.out.println("TAT HDR Updated Value - Approved Exit Re Entry Visa: "+value);
                    //fusionFileLoader.sendFusionRequest(params, 4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSFUtils.addFacesInformationMessage("Exit Re-Entry element is created in HCM");
               
            }
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
                                                      "ExitReEntry",
                                                      (Number)nextStep.getAttribute("StepId"),
                                                      stepid.longValue(),
                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                      "APPROVE_ACT", "Y");
            updateInsert=1;
            ADFUtils.findOperation("Commit").execute();
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            sendEmailToPerson(personNumber, exitReEntryRow);
            sendEmailByEmail(fyiEmailAddress, exitReEntryRow);
            //            sendEmailForExitEntryFYI(fyiEmailAddress,exitReEntryRow);
            }
           
        }

        ADFUtils.findOperation("Commit").execute();
        String finalapproval=null;
       
        if(nextStep.getAttribute("NextAssignee").equals("Finished"))
        {
            finalapproval="Y";
        }
        else{
            finalapproval="N";
        }
        System.err.println("finalapproval===="+finalapproval);
        if(updateInsert==0)
        {
        stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
                                                  "ExitReEntry",
                                                  (Number)nextStep.getAttribute("StepId"),
                                                  stepid.longValue(),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                  "APPROVE_ACT", finalapproval);
        
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if("POSITION".equals(stepType)){
                    System.err.println("Email to mail-id::"+assigneeEmail);
                    sendEmailByEmail(assigneeEmail, exitReEntryRow);    
                }else{
                    System.err.println("Email to user-id::"+assigneeNo);
                    sendEmail(assigneeNo, exitReEntryRow);    
                }   
            }
        }
        
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "backToRequests";

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
            sendExitReEntryEmail("OFOQ.HR@TATWEER.SA", email,
                                 (ExitReEntryVORowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public String rejectRequest() {
        // Add event code here...
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ExitReEntryVORowImpl exitReEntryRow =
            (ExitReEntryVORowImpl)ADFUtils.findIterator("ExitReEntryVO1Iterator").getCurrentRow();
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
        sendEmail(personNumber, exitReEntryRow);
        }
        ADFUtils.findOperation("Commit").execute();
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
                                                      "ExitReEntry",
                                                 (Number)nextStep.getAttribute("StepId"),
                                                  new Long(0),
                                                  null,
                                                  "REJECT_ACT", "");
        JSFUtils.addFacesInformationMessage("Exit Re-Entry Request has been Rejected");

        return "backToRequests";

    }

    //    public static void main(String[] args) {
    //        java.util.Date date = new Date();
    //        Calendar cal = Calendar.getInstance();
    //        cal.setTime(date);
    //        int year = cal.get(Calendar.YEAR);
    //        int month = cal.get(Calendar.MONTH);
    //        //        month=month+2;
    //        int day = cal.get(Calendar.DAY_OF_MONTH);
    //        System.err.println("month>>>" + month);
    //        System.err.println("day>>>" + day);
    //        System.err.println("year>>>" + year);
    //        cal.set(Calendar.YEAR, year);
    //        cal.set(Calendar.MONTH, month);
    //        cal.set(Calendar.DATE, day);
    //        java.util.Date utilDate = cal.getTime();
    //        System.out.println(utilDate);
    //
    //    }


    public void showHidePersonsFlags(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String visaInformation = (String)valueChangeEvent.getNewValue();
        if (visaInformation.equalsIgnoreCase("Individual Exit Re-Entry")) {
            selfFlag.setValue(true);
            selfFlag.setVisible(true);
            selfFlag.setDisabled(true);
            spouseFlag.setValue(false);
            spouseFlag.setVisible(false);
            childrenFlag.setValue(false);
            childrenFlag.setVisible(false);
            noOfChildren.setValue(new Number(0));


        } else if (visaInformation.equalsIgnoreCase("Multi Exit Re-Entry")) {
            selfFlag.setValue(false);
            selfFlag.setVisible(true);
            selfFlag.setDisabled(false);
            spouseFlag.setValue(false);
            spouseFlag.setVisible(true);
            childrenFlag.setValue(false);
            childrenFlag.setVisible(true);
            noOfChildren.setValue(new Number(0));


        } else if (visaInformation.equalsIgnoreCase("Individual Exit Re-Entry or Multi for Family")) {
            selfFlag.setValue(false);
            selfFlag.setVisible(true);
            selfFlag.setDisabled(false);
            spouseFlag.setValue(false);
            spouseFlag.setVisible(true);
            childrenFlag.setValue(false);
            childrenFlag.setVisible(true);
            noOfChildren.setValue(new Number(0));


        }

    }

    public void setSelfFlag(RichSelectBooleanCheckbox selfFlag) {
        this.selfFlag = selfFlag;
    }

    public RichSelectBooleanCheckbox getSelfFlag() {
        return selfFlag;
    }

    public void setSpouseFlag(RichSelectBooleanCheckbox spouseFlag) {
        this.spouseFlag = spouseFlag;
    }

    public RichSelectBooleanCheckbox getSpouseFlag() {
        return spouseFlag;
    }

    public void setChildrenFlag(RichSelectBooleanCheckbox childrenFlag) {
        this.childrenFlag = childrenFlag;
    }

    public RichSelectBooleanCheckbox getChildrenFlag() {
        return childrenFlag;
    }

    public void setNoOfChildren(RichInputNumberSpinbox noOfChildren) {
        this.noOfChildren = noOfChildren;
    }

    public RichInputNumberSpinbox getNoOfChildren() {
        return noOfChildren;
    }


    public void setExitType(RichSelectOneChoice exitType) {
        this.exitType = exitType;
    }

    public RichSelectOneChoice getExitType() {
        return exitType;
    }

    public void showHideIfWorkNeed(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String exitReEntryType = (String)valueChangeEvent.getNewValue();
        if (exitReEntryType.equalsIgnoreCase("Work Need")) {
            selfFlag.setValue(true);
            selfFlag.setVisible(true);
            selfFlag.setDisabled(true);
            spouseFlag.setValue(false);
            spouseFlag.setVisible(false);
            childrenFlag.setValue(false);
            childrenFlag.setVisible(false);
            noOfChildren.setValue(new Number(0));

        } else {

            selfFlag.setValue(false);
            selfFlag.setVisible(true);
            selfFlag.setDisabled(false);
            spouseFlag.setValue(false);
            spouseFlag.setVisible(true);
            childrenFlag.setValue(false);
            childrenFlag.setVisible(true);
            noOfChildren.setValue(new Number(0));

        }
    }


    public void setInputFile(RichInputFile inputFile) {
        this.inputFile = inputFile;
    }

    public RichInputFile getInputFile() {
        return inputFile;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentPopup(RichPopup attachmentPopup) {
        this.attachmentPopup = attachmentPopup;
    }

    public RichPopup getAttachmentPopup() {
        return attachmentPopup;
    }

    public void setFusionFileLoader(FusionDataLoader fusionFileLoader) {
        this.fusionFileLoader = fusionFileLoader;
    }

    public FusionDataLoader getFusionFileLoader() {
        return fusionFileLoader;
    }
    public String cancelBtnAction() {
        // Add event code here...
        attachmentPopup.hide();
        return null;
    }
    public String saveBtnAction() {
        // Add event code here...
        UploadedFile file = (UploadedFile)inputFile.getValue();
        // Get the file name
        String fileName = file.getFilename();
        // get the mime type
        String contentType = file.getContentType();
        // get blob
        String uploadedBy =
            (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}");
        BlobDomain blob = createBlobDomain(file);
        System.out.println("file name is " + fileName);
        ADFUtils.findOperation("CreateInsert").execute();
        //        if (getAttachmentFileName() != null &&
        //            getAttachmentFileName().isEmpty()) {
        //            fileName = getAttachmentFileName();
        //        }
        ADFUtils.setBoundAttributeValue("AttachmentFile", blob);
        ADFUtils.setBoundAttributeValue("FileName", fileName);
        ADFUtils.setBoundAttributeValue("FileType", contentType);
        ADFUtils.setBoundAttributeValue("UploadedBy", uploadedBy);
        //        AdfFacesContext.getCurrentInstance().addPartialTarget(attachmentsTable);
        attachmentFileName = null;
        //        attachmentFileDesc = null ;
        attachmentPopup.hide();
        return null;
    }
    public String showAttachmentPopup() {
        // Add event code here...
        inputFile.resetValue();
        inputFile.setValid(true);
        attachmentPopup.show(new RichPopup.PopupHints());

        return null;
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
        // Add event code here...
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
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }
    public void sendEmailForExitEntryFYI(String email, Row subject) {
//        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendExitReEntryEmailForEmployee("OFOQ.HR@TATWEER.SA", email,
                                            (ExitReEntryVORowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }


    public void sendExitReEntryEmailForEmployee(String from, String to,
                                                ExitReEntryVORowImpl subject) {
        if (to == null) {
           // to = "heleraki@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Exit Re-Entry request has been Approved" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String personalInformation =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
            " <tr><th>&nbsp;Requester Name</th>\n" +
            "      <td width=\"50%\">" + subject.getPersonName() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Position\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonPosition() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Job\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonJob() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Grade\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonGrade() +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Department\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonDepartment() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Location\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPersonLocation() +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String verticalSpace = " <p>&nbsp;</p>";

        String overtimeDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Exit Re-Entry Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Visa Information\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getVisaInformation() +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryExitIterator");
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
                                    "Exit Re-Entry Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
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
    
    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("ExitReEntryVO1Iterator").getViewObject();
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
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){

            ViewObject reqVo = ADFUtils.findIterator("ExitReEntryVO1Iterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            
            currRow.setAttribute("StepId", "1");
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
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
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"), 
                                                                             "ExitReEntry", (Number)nextStep.getAttribute("NextStepId"), 
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
                String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"), 
                                                                             "ExitReEntry", (Number)nextStep.getAttribute("NextStepId"), 
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
        ViewObject reqVo = ADFUtils.findIterator("ExitReEntryVO1Iterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personNo = ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
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
            ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
                                                      "ExitReEntry",
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
        
        return "backToRequests";
    }
    
    public String approve_withdraw() {
        
        ViewObject reqVo = ADFUtils.findIterator("ExitReEntryVO1Iterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
            String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
            String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
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
            
            BiReportAccess report = new BiReportAccess();
            String reqCreatedDate =
                ADFUtils.getBoundAttributeValue("CreationDate").toString(); 
            String amount =
                ADFUtils.getBoundAttributeValue("Amount").toString();
            
            List<Map> datFileData = null;
            try {
                datFileData = report.getExitReEntryWithdrawDatInfo(personNumber, changeFormatOfDate("dd-MM-yyyy", "MM-dd-yyyy", reqCreatedDate));
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
           
            HashMap<String, String> params = new HashMap<String, String>(); 
            params.put("AssignmentNo", assignmentNumber);
            params.put("Count", entryCount);
            params.put("EffStartDate", effStartDate);
            params.put("EffEndDate", effEndDate); 
            params.put("Amount", amount); 
            System.err.println("EXIT-RE-ENTRY WITHDRAW : Fusion upload calling with values : "+params);

            try {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 15);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("ExitReEntryId").toString(), personNumber, "Withdrawn", "Exit Re Entry Visa", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Withdrawn Exit Re Entry Visa: "+value);
                //fusionFileLoader.sendFusionRequest(params, 15);
                
            } catch (Exception e) {
                e.printStackTrace();
            } 
            
            Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
    
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
                                                      "ExitReEntry",(Number)nextStep.getAttribute("StepId"),
                                                       stepid.longValue(),
                                                       (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                     sendEmailToPerson(personNumber, currRow);
                     sendEmailByEmail(fyiEmailAddress, currRow);
            } 
        }else{
            Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExitReEntryId"),
                                                      "ExitReEntry",(Number)nextStep.getAttribute("StepId"), stepid.longValue(),
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
        return "backToRequests";
        }
  
    public void sendEmailToPerson(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendExitReEntryEmail("OFOQ.HR@TATWEER.SA", email,
                                     (ExitReEntryVORowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    private String changeFormatOfDate(String fromFormat, String toFormat, String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        java.util.Date date = formatter.parse(strDate);
        SimpleDateFormat ft = new SimpleDateFormat(toFormat);
        return ft.format(date);
     }

    public String onEditInSearch() {
        
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("ExitReEntryVO1Iterator").getViewObject();
        Row r = vo.getCurrentRow();
        if(r.getAttribute("RequestStatus") != null &&
                ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")) || "Withdrawn Rejected".equals(r.getAttribute("RequestStatus")))){
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }else{
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                 personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            String ExitReEntryTypeName="ExitReEntry" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ExitReEntryTypeName", ExitReEntryTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
        }
        return "edit";
    }
    
    
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("ExitReEntryVO1Iterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ExitReEntryVOCriteria"));
            vo.executeQuery();
        }
    }
    
}
