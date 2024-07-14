package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.views.up.DepartmentsVORowImpl;
import com.sbm.selfServices.model.views.up.DeptEmployeesVORowImpl;
import com.sbm.selfServices.model.views.up.GradesVORowImpl;
import com.sbm.selfServices.model.views.up.JobsVORowImpl;
import com.sbm.selfServices.model.views.up.ManPowerRequestViewRowImpl;

import com.sbm.selfServices.model.views.up.OvertimeRequestViewRowImpl;
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
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import javax.faces.model.SelectItem;

import javax.xml.bind.JAXBElement;

import oracle.adf.controller.ControllerContext;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.adf.view.rich.context.AdfFacesContext;

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

public class ManpowerMB {

    private String attachmentFileName;
    private RichInputFile inputFile;
    private RichPopup attachmentPopup;
    private RichSelectOneChoice gradebinding;
    private RichInputText basicsalary;
    public ArrayList<SelectItem> vacantList = new ArrayList<SelectItem>();

    public ManpowerMB() {
    }

    public String newManpower() {
        // Add event code here...
String personNumber=JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}").toString();
        JSFUtils.setExpressionValue("#{pageFlowScope.mPPersonNummber}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.mPPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.location}",
                                    JSFUtils.resolveExpression("#{PersonInfo.location}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.department}",
                                    JSFUtils.resolveExpression("#{PersonInfo.department}"));

        UserServiceUtil userService = new UserServiceUtil();
     UserDetails   userDetails =
                userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        String department = relationshipDetails.get(0).getDepartmentName();
        
        BiReportAccess BIRA = new BiReportAccess();
        
        
        
        List<Map> list1=null;
        try {
            list1 = BIRA.getDeptEmployees(department);
            System.err.println("DeptEmployees  is : " + list1);
            
            DCIteratorBinding depEmployeestIter =
                            ADFUtils.findIterator("DeptEmployeesVO1Iterator");
                        ViewObject deptEmployeesView = depEmployeestIter.getViewObject();
            DeptEmployeesVORowImpl deptEmployeesRow=null;
            
            if (list1.size() > 0) {
                            ADFUtils.findOperation("deleteDeptEmployeesTableRows").execute();
                            System.out.println("All Department Employees table rows deleted successfully");
                            for (Map currentDeptEmployee : list1) {
                                deptEmployeesRow = (DeptEmployeesVORowImpl)deptEmployeesView.createRow();
                                deptEmployeesRow.setPersonId(currentDeptEmployee.get("PERSON_ID").toString());
                                deptEmployeesRow.setPersonNumber(currentDeptEmployee.get("PERSON_NUMBER").toString());
                                deptEmployeesRow.setDisplayName(currentDeptEmployee.get("DISPLAY_NAME").toString());
    
                            }   
                deptEmployeesView.insertRow(deptEmployeesRow);
                
                ADFUtils.findOperation("Commit").execute();
            }
            
            deptEmployeesView.executeQuery();
            depEmployeestIter.executeQuery();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
//        DeptEmployeesVO1Iterator
        
//        try {
//            List<Map> list1 = BIRA.getDepartmentsData();
//
//            DCIteratorBinding deptIter =
//                ADFUtils.findIterator("DepartmentsVO1Iterator");
//            ViewObject deptView = deptIter.getViewObject();
//            DepartmentsVORowImpl deptRow;
//            if (list1.size() > 0) {
//                ADFUtils.findOperation("deleteDeptTableRows").execute();
//                System.out.println("All Departments table rows deleted successfully");
//                for (Map currentDepartment : list1) {
//                    deptRow = (DepartmentsVORowImpl)deptView.createRow();
//                    deptRow.setDeptId(new Number(currentDepartment.get("ORGANIZATION_ID").toString()));
//                    deptRow.setDeptName(currentDepartment.get("NAME").toString());
//                    if (currentDepartment.get("SEGMENT4") != null) {
//                        deptRow.setSegment4((currentDepartment.get("SEGMENT4").toString()));
//                    } else {
//                        deptRow.setSegment4("");
//                    }
//
//                    deptView.insertRow(deptRow);
//
//                    ADFUtils.findOperation("Commit").execute();
//
//                }
//
//                deptView.executeQuery();
//                deptIter.executeQuery();
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

     
        List<Map> list2 = null;


        try {
            list2 = BIRA.getGradesData();

            DCIteratorBinding gradesIter =
                ADFUtils.findIterator("GradesVO1Iterator");
            ViewObject gradesView = gradesIter.getViewObject();
            GradesVORowImpl gradesRow;
            if (list2.size() > 0) {
                ADFUtils.findOperation("deleteGradesTableRows").execute();
                System.out.println("All Grades table rows deleted successfully");


                for (Map currentGrade : list2) {


                    gradesRow = (GradesVORowImpl)gradesView.createRow();
                    gradesRow.setGradeId(currentGrade.get("GRADE_ID").toString());
                    gradesRow.setGradeName(currentGrade.get("NAME").toString());
                    gradesView.insertRow(gradesRow);

                    ADFUtils.findOperation("Commit").execute();


                }

                gradesView.executeQuery();
                gradesIter.executeQuery();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        List<Map> list3 = null;


        try {
            list3 = BIRA.getJobsData();

            DCIteratorBinding jobsIter =
                ADFUtils.findIterator("JobsVO1Iterator");
            ViewObject jobsView = jobsIter.getViewObject();
            JobsVORowImpl jobsRow;
            if (list3.size() > 0) {
                ADFUtils.findOperation("deleteJobsTableRows").execute();
                System.out.println("All Jobs table rows deleted successfully");


                for (Map currentJob : list3) {
                    jobsRow = (JobsVORowImpl)jobsView.createRow();
                    jobsRow.setJobId(currentJob.get("JOB_ID").toString());
                    jobsRow.setJobName(currentJob.get("NAME").toString());
                    jobsView.insertRow(jobsRow);

                    ADFUtils.findOperation("Commit").execute();


                }

                jobsView.executeQuery();
                jobsIter.executeQuery();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "newManPower";
    }


    public String saveRequest() {
        // Add event code here...
        if( ADFUtils.getBoundAttributeValue("Justifications")==null)
        {
                JSFUtils.addFacesErrorMessage("Justifications is Mandatory");
                return null;
                
            }
        
        if( ADFUtils.getBoundAttributeValue("PositionCategory")==null)
        {
               JSFUtils.addFacesErrorMessage("You must select a position category");
               return null;
           
           }
        
        if( ADFUtils.getBoundAttributeValue("RequisitionTitle")==null)
        {
           JSFUtils.addFacesErrorMessage("You must select a Requisition Title");
           return null; 
        }
        if( ADFUtils.getBoundAttributeValue("Type")==null)
        {
           JSFUtils.addFacesErrorMessage("You must select a Type");
           return null; 
        }
        String manPowerType = ADFUtils.getBoundAttributeValue("Type").toString();
        
        if(!"Full Time Job".equals(manPowerType) && !"Tamheer".equals(manPowerType) && !"Volunteer".equals(manPowerType) &&  !"Coop Training".equals(manPowerType)){
            
            if( ADFUtils.getBoundAttributeValue("WorkType")==null)
            {
               JSFUtils.addFacesErrorMessage("You must select a Work Type");
               return null; 
            }
        }
        
        long count = ADFUtils.findIterator("ManpowerAttachmentsEOView1Iterator").getEstimatedRowCount();
        
        if (count < 1) {
            JSFUtils.addFacesErrorMessage("You must add the required job description attachments before saving the request");
            return null;
        }

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
        JSFUtils.addFacesInformationMessage("Manpower Requisition Request has been saved");
        ADFUtils.findOperation("Execute").execute();

        return "backToManPowerReq";
    }


    public String submitRequest() {
        // Add event code here...
        String manPowerType = ADFUtils.getBoundAttributeValue("Type")!=null ? (String)ADFUtils.getBoundAttributeValue("Type") :"";
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String type = (String)ADFUtils.getBoundAttributeValue("Type");
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        
        
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        
        if( ADFUtils.getBoundAttributeValue("Justifications")==null)
        {
                JSFUtils.addFacesErrorMessage("Justifications is Mandatory");
                return null;
                
            }
        if( ADFUtils.getBoundAttributeValue("RequisitionTitle")==null)
        {
           JSFUtils.addFacesErrorMessage("You must select a Requisition Title");
           return null; 
        }
        
        if( ADFUtils.getBoundAttributeValue("PositionCategory")==null)
        {
               JSFUtils.addFacesErrorMessage("You must select a position category");
               return null;
           
           }
        System.err.println("type======"+type);
        if(type!=null) {
            if(type.equalsIgnoreCase("Coop Training")) {

                JSFUtils.setExpressionValue("#{bindings.Grade.inputValue}",  "");
            }
            if(type.equalsIgnoreCase("Tamheer")) {
          
//                JSFUtils.setExpressionValue("#{bindings.BasicSalary.inputValue}",  new );
                ADFUtils.setBoundAttributeValue("BasicSalary", new Number(0));
                JSFUtils.setExpressionValue("#{bindings.Grade.inputValue}",  "");
//                AdfFacesContext.getCurrentInstance().addPartialTarget(basicsalary);
               
            }
        }
        long count = ADFUtils.findIterator("ManpowerAttachmentsEOView1Iterator").getEstimatedRowCount();
        
        if (count < 1) {
            JSFUtils.addFacesErrorMessage("You must add the required job description attachments before submitting the request");
            return null;
        }
        ADFUtils.findOperation("Commit").execute();
        String managerNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

        Long lineManagerID =
            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        String stringLineManagerID = null;
        stringLineManagerID = lineManagerID.toString();
//        if (lineManagerID == null) {
//            JSFUtils.addFacesErrorMessage("You donot have Line Manager, So you can not submit the request");
//            return null;
//        }
//        else{
//            stringLineManagerID = lineManagerID.toString();
//        }
        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");

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

//        String managerOfDeptNum = null;
//        String managerOfDeptName = null;
//        String managerOfDeptEmail = null;
//        if (department != null) {
//
//            BiReportAccess BIRA = new BiReportAccess();
//
//
//            try {
//                List<Map> managerOfDeptList =
//                    BIRA.getManagerOfDepartmentData(department);
//                if (managerOfDeptList.size() > 0) {
//                    managerOfDeptNum =
//                            managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
//                    managerOfDeptName =
//                            managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
//                    managerOfDeptEmail =
//                            managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
//
//                } else {
//                    JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
//                    return null;
//
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (managerOfDeptNum != null)
//
//            {
//
//                ADFUtils.setBoundAttributeValue("Assignee", managerOfDeptNum);
//
//            }
//
//
//        }

        //        System.out.println("Manager Number Is >>>> " + managerNumber);
        //        UserDetails byManagerId =
        //            userService.getUserDetailsByPersonId(lineManagerID);
        //        String managerName =
        //            byManagerId.getUserPersonDetails().get(0).getDisplayName().getValue();
        //        System.out.println("Manager Name Is >>>> " + managerName);
//        ADFUtils.setBoundAttributeValue("AssigneeName", managerOfDeptName);
        //        String managerEmail =
        //            byManagerId.getUserPersonDetails().get(0).getEmailAddress().getValue();
        //        System.out.println("Manager Email Is >>>> " + managerEmail);
//        ADFUtils.findOperation("Commit").execute();
//        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        
                
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        boolean elseCase = false;
        boolean tamheerOrFulltime = false;
        
//        if("POSITION".equals(stepType)){
//            if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")){
//                if(manPowerType.equalsIgnoreCase("Tamheer") || manPowerType.equalsIgnoreCase("Full Time Job")){
//                    tamheerOrFulltime = true;
//                    ADFUtils.setBoundAttributeValue("StepId",
//                                                    nextStep.getAttribute("NextStepId"));
//                    nextStep = (Row)nextOpr.execute();
//                    stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
//                }else{
//                    elseCase = true;
//                }
//           }else if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")){
//                       if(!"Full Time Job".equals(manPowerType) && !"Tamheer".equals(manPowerType) && !"Coop Training".equals(manPowerType)){
//                       tamheerOrFulltime = true;
//                       ADFUtils.setBoundAttributeValue("StepId",
//                                                       nextStep.getAttribute("NextStepId"));
//                       nextStep = (Row)nextOpr.execute();
//                       stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
//                   }else{
//                       elseCase = true;
//                   }
//               } 
//            else{
//                elseCase = true;
//            }
//            if(elseCase){
//                System.err.println("Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
//                BiReportAccess report = new BiReportAccess(); 
//                List<Map> personData2 = null;
//                try {
//                    personData2 = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (personData2.get(0).get("DISPLAY_NAME") != null) {
//                    assigneeName = personData2.get(0).get("DISPLAY_NAME").toString();
//                }
//                if (personData2.get(0).get("PERSON_NUMBER") != null) {
//                    assigneeNo = personData2.get(0).get("PERSON_NUMBER").toString();
//                }
//                if (personData2.get(0).get("EMAIL_ADDRESS") != null) {
//                    assigneeEmail = personData2.get(0).get("EMAIL_ADDRESS").toString();
//                }
//            }
//        }
        
        // If step type is POSITION
        if("POSITION".equals(stepType)){
            System.err.println("...Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
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
        
        // 
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.findOperation("Commit").execute();
        
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        
//        if(tamheerOrFulltime){
//            Number stepId = (Number)nextStep.getAttribute("StepId");
//            stepId = stepId.subtract(1);   
//            String value=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"), 
//                                                    "ManPower", stepId, 
//                                                    (Long)ADFUtils.getBoundAttributeValue("StepId"), 
//                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
//                                                    "SUMBIT_ACT", "");
//        }else{
            String value=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"), 
                                                    "ManPower", (Number)nextStep.getAttribute("NextStepId"), 
                                                    (Long)ADFUtils.getBoundAttributeValue("StepId"), 
                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                    act, "");
//        }
        
        
        
        ManPowerRequestViewRowImpl manpowerRow =
            (ManPowerRequestViewRowImpl)ADFUtils.findIterator("ManPowerRequestViewIterator").getCurrentRow();
      
        JSFUtils.addFacesInformationMessage("Request has been submitted");
        ADFUtils.findOperation("Execute").execute();
//        if (managerOfDeptEmail == null) {
//
//            JSFUtils.addFacesErrorMessage("You don't have Department Manager Email, So mail can't be sent to manager");
//            return "backToManPowerReq";
//        }
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, manpowerRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, manpowerRow);    
            }    
        }
        
      

        return "backToManPowerReq";
    }
    
    public String approveRequest() { 
           if(checkMandatory()){
               return "";
           }else{ 
           // Add event code here...
          int updateInsert=0;
           String personMail = null;
           ManPowerRequestViewRowImpl manpowerRow =
               (ManPowerRequestViewRowImpl)ADFUtils.findIterator("ManPowerRequestViewIterator").getCurrentRow();
           String manPowerType = ADFUtils.getBoundAttributeValue("Type")!=null ? (String)ADFUtils.getBoundAttributeValue("Type") :""; 
           String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
           String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
           ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
           OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
           Row nextStep = (Row)nextOpr.execute();
           Number previousStepId = (Number)nextStep.getAttribute("StepId");
           String emailNotification = (String)nextStep.getAttribute("EmailNotification");            
           String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
           String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
           String personNumber =
               ADFUtils.getBoundAttributeValue("PersonNumber").toString();
               
           //Dynamic Approval
           String assigneeName = "";
           String assigneeNo = "";
           String assigneeEmail = "";
           boolean elseCase = false;
           boolean tamheerOrFulltime = false;
           UserServiceUtil userService = new UserServiceUtil();
           UserDetails userDetails = null;
           userDetails = userService.getUserDetailsByPersonNumber(personNumber);
               
//           if("POSITION".equals(stepType)){
//               if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")){
//                   if(manPowerType.equalsIgnoreCase("Tamheer") || manPowerType.equalsIgnoreCase("Full Time Job")){
//                       tamheerOrFulltime = true;
//                       ADFUtils.setBoundAttributeValue("StepId",
//                                                       nextStep.getAttribute("NextStepId"));
//                       nextStep = (Row)nextOpr.execute();
//                       stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
//                   }else{
//                       elseCase = true;
//                   }
//               }else if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")){
//                       if(!"Full Time Job".equals(manPowerType) && !"Tamheer".equals(manPowerType) && !"Coop Training".equals(manPowerType)){
//                       tamheerOrFulltime = true;
//                       ADFUtils.setBoundAttributeValue("StepId",
//                                                       nextStep.getAttribute("NextStepId"));
//                       nextStep = (Row)nextOpr.execute();
//                       stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
//                   }else{
//                       elseCase = true;
//                   }
//               } 
//               else{
//                   elseCase = true;
//               }
//               if(elseCase){
//                   System.err.println("Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
//                   BiReportAccess report = new BiReportAccess(); 
//                   List<Map> personData2 = null;
//                   try {
//                       personData2 = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
//                   } catch (Exception e) {
//                       e.printStackTrace();
//                   }
//                   if (personData2.get(0).get("DISPLAY_NAME") != null) {
//                       assigneeName = personData2.get(0).get("DISPLAY_NAME").toString();
//                   }
//                   if (personData2.get(0).get("PERSON_NUMBER") != null) {
//                       assigneeNo = personData2.get(0).get("PERSON_NUMBER").toString();
//                   }
//                   if (personData2.get(0).get("EMAIL_ADDRESS") != null) {
//                       assigneeEmail = personData2.get(0).get("EMAIL_ADDRESS").toString();
//                   }
//               }
//               
////                                              
//              
//           }
           if("POSITION".equals(stepType)){ 
               
               System.err.println("...Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
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
              
           //Exist 
           System.out.println("next step is " +
                              nextStep.getAttribute("NextStepId"));

//           ADFUtils.setBoundAttributeValue("StepId",
//                                           nextStep.getAttribute("NextStepId"));
//           ADFUtils.setBoundAttributeValue("Assignee",
//                                           nextStep.getAttribute("NextAssignee"));
//           ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//           ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
//          
//
//           if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")) {
//               
//                   BiReportAccess report = new BiReportAccess();
//                   List<Map> personData = null;
//
//                   try {
//                       personData =
//                               report.getPersonByPostionReport("Financial Planning & Analysis Manager");
//                       
//                   } catch (Exception e) {
//                       e.printStackTrace();
//                   }
//                   
//                   if(manPowerType.equalsIgnoreCase("Tamheer") || manPowerType.equalsIgnoreCase("Full Time Job")) {
//                       if (personData.size() > 0) {
//                           if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                               String fyipersonNumber = (String)personData.get(0).get("PERSON_NUMBER");
//                               nextStep = (Row)nextOpr.execute();
//                               try
//                               {
//                               personData =
//                                       report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
//                                   if (personData.size() > 0) {
//                                   
//                                       if (personData.get(0).get("PERSON_NUMBER") != null) {
//                   //                                    ADFUtils.setBoundAttributeValue("Assignee",
//                   //                                                                    personData.get(0).get("PERSON_NUMBER").toString());
//                                           System.out.println("nextStep"+
//                                                              personData.get(0).get("PERSON_NUMBER"));
//                                       }
//                                       if (personData.get(0).get("DISPLAY_NAME") != null) {
//                                           System.out.println("nextStep" +
//                                                              personData.get(0).get("DISPLAY_NAME"));
//                                           ADFUtils.setBoundAttributeValue("StepId",
//                                                                           nextStep.getAttribute("NextStepId"));
//                                           ADFUtils.setBoundAttributeValue("Assignee",
//                                                                           nextStep.getAttribute("NextAssignee"));
//                                           ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                                           personData.get(0).get("DISPLAY_NAME"));
//                                       }
//                                   
//                                       else {
//                                           JSFUtils.addFacesErrorMessage("You can't approve request as Financial Planning & Analysis Manager Name is empty");
//                                           return null;
//                                       }
////                                       String value =
////                                           ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
////                                                                                 "ManPower",
////                                                                                 (Number)nextStep.getAttribute("StepId"),
////                                                                                 (Long)ADFUtils.getBoundAttributeValue("StepId"),
////                                                                                 (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
////                                                                                 "APPROVE_ACT", "N");
////                                       updateInsert=1;
//                                       ADFUtils.findOperation("Commit").execute();
//                                       if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                                           personMail =
//                                                   personData.get(0).get("EMAIL_ADDRESS").toString();
//                                           if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                                           sendEmailByEmail(personMail, manpowerRow);
//                                          // sendFYIEmail(fyipersonNumber, manpowerRow);
//                                           }
//                                       } else {
//                                           JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Financial Planning & Analysis Manager dosn't has email");
//                                   
//                                       }
//                                   }
//                               }
//                               catch(Exception e) {
//                                   e.printStackTrace();
//                               }
//                           }
//                       }
//                   }
//                   else{
//                       if (personData.size() > 0) {
//                   
//                           if (personData.get(0).get("PERSON_NUMBER") != null) {
//                               
//                               System.out.println("Financial Planning & Analysis Manager Number is " +
//                                                  personData.get(0).get("PERSON_NUMBER"));
//                           }
//                           if (personData.get(0).get("DISPLAY_NAME") != null) {
//                               System.out.println("Financial Planning & Analysis Manager Name is " +
//                                                  personData.get(0).get("DISPLAY_NAME"));
//                   
//                               ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                               personData.get(0).get("DISPLAY_NAME"));
//                           }
//                   
//                           else {
//                               JSFUtils.addFacesErrorMessage("You can't approve request as Financial Planning & Analysis Manager Name is empty");
//                               return null;
//                           }
//                           String value =
//                               ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
//                                                                     "ManPower",
//                                                                     (Number)nextStep.getAttribute("StepId"),
//                                                                     (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                                     (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                                     "APPROVE_ACT", "N");
//                           updateInsert=1;
//                           ADFUtils.findOperation("Commit").execute();
//                           if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                               personMail =
//                                       personData.get(0).get("EMAIL_ADDRESS").toString();
//                               if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                               sendEmailByEmail(personMail, manpowerRow);
//                               }
//                           } else {
//                               JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Financial Planning & Analysis Manager dosn't has email");
//                   
//                           }
//                       }
//                   } 
//
//           }

           //    -------------------------------------------------------------------------HR Payroll and benefits Supervisor
                          
//                 if (nextStep.getAttribute("NextAssignee").equals("HR Payroll and benefits Supervisor")) {
//                     
//                      BiReportAccess report = new BiReportAccess();
//                      List<Map> personData = null;
//                      try {
//                          personData = report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
//                      } catch (Exception e) {
//                          e.printStackTrace();
//                      }
//                      if (personData.size() > 0) {
//                          if (personData.get(0).get("PERSON_NUMBER") != null) {
//                              ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
//                              System.out.println("HR Payroll and benefits Supervisor Number is " +
//                                                 personData.get(0).get("PERSON_NUMBER"));
//                          }
//                          if (personData.get(0).get("DISPLAY_NAME") != null) {
//                              System.out.println("HR Payroll and benefits Supervisor Name is " +
//                                                 personData.get(0).get("DISPLAY_NAME"));
//                              ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                              personData.get(0).get("DISPLAY_NAME"));
//
//                              System.err.println("HR Payroll and benefits Supervisor--"+ ADFUtils.getBoundAttributeValue("AssigneeName"));
//                          }
//                          else {
//                              JSFUtils.addFacesErrorMessage("You can't approve request as HR Payroll and benefits Supervisor Name is empty");
//                              return null;
//                          }
//                          
//                          if(manPowerType.equalsIgnoreCase("Tamheer") || manPowerType.equalsIgnoreCase("Full Time Job")) {
//                              String value =
//                                  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
//                                                                        "ManPower",
//                                                                        new Number(2),
//                                                                        (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                                        "APPROVE_ACT", "N");
//                          }else{
//                              String value =
//                                  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
//                                                                        "ManPower",
//                                                                        (Number)nextStep.getAttribute("StepId"),
//                                                                        (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                                        "APPROVE_ACT", "N");
//                          }
//                          
//                          ADFUtils.findOperation("Commit").execute();
//                          updateInsert=1;
//                          if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                              personMail =
//                                      personData.get(0).get("EMAIL_ADDRESS").toString();
//                              if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                              sendEmailByEmail(personMail, manpowerRow);
//                              }
//                          } else {
//                              JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR Payroll and benefits Supervisor dosn't has email");
//                          }
//                      }
//                      }
                          
           //    -------------------------------------------------------------------------
           
//           if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
//               
//
//           BiReportAccess report = new BiReportAccess();
//           List<Map> personData = null;
//
//           try {
//               personData = report.getPersonByPostionReport("HR and Admin Director");
//           } catch (Exception e) {
//               e.printStackTrace();
//           }
//           
//           if (personData.size() > 0) {
//               if (personData.get(0).get("PERSON_NUMBER") != null) {
//                   ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
//                   System.out.println("HR and Admin Director Number is " +
//                                      personData.get(0).get("PERSON_NUMBER"));
//               }
//               if (personData.get(0).get("DISPLAY_NAME") != null) {
//                   System.out.println("HR and Admin Director Name is " +
//                                      personData.get(0).get("DISPLAY_NAME"));
//
//                   ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                   personData.get(0).get("DISPLAY_NAME"));
//
//                   System.err.println("HR--"+ ADFUtils.getBoundAttributeValue("AssigneeName"));
//               }
//
//               else {
//                   JSFUtils.addFacesErrorMessage("You can't approve request as HR and Admin Director Name is empty");
//                   return null;
//               }
//               String value =
//                   ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
//                                                         "ManPower",
//                                                         (Number)nextStep.getAttribute("StepId"),
//                                                         (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                         (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                         "APPROVE_ACT", "N");
//               ADFUtils.findOperation("Commit").execute();
//               updateInsert=1;
//               if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                   personMail =
//                           personData.get(0).get("EMAIL_ADDRESS").toString();
//                   if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                   sendEmailByEmail(personMail, manpowerRow);
//                   }
//               } else {
//                   JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR and Admin Director dosn't has email");
//
//               }
//           }
//           }
               
 /*

           if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")) {
               
               if(!"Full Time Job".equals(manPowerType) && !"Tamheer".equals(manPowerType) && !"Coop Training".equals(manPowerType)){
                   System.err.println("****IN TRUE");
                   ADFUtils.setBoundAttributeValue("StepId", "5");
                   ADFUtils.findOperation("Commit").execute();
                   nextStep = (Row)nextOpr.execute();
                   
               }else{
                   System.err.println("-------------In Executive Director, Shared Services Sector (Acting)");
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
                       String value =
                           ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                                 "ManPower",
                                                                 (Number)nextStep.getAttribute("StepId"),
                                                                 (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                 (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                                 "APPROVE_ACT", "N");
                       updateInsert=1;
                       ADFUtils.findOperation("Commit").execute();
                       if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                           personMail =
                                   personData.get(0).get("EMAIL_ADDRESS").toString();
                           if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                           sendEmailByEmail(personMail, manpowerRow);
                           }
                       } else {
                           JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");

                       }
                   }
                   
               } 
           }
*/

    //        //HR Specialist
    //
    //
    //        if (nextStep.getAttribute("NextAssignee").equals("HR Specialist")) {
    //
    ////            BiReportAccess report = new BiReportAccess();
    ////            List<Map> personData = null;
    ////
    ////            try {
    ////                personData = report.getPersonByPostionReport("HR Specialist");
    ////            } catch (Exception e) {
    ////                e.printStackTrace();
    ////            }
    ////
    ////            if (personData.size() > 0) {
    ////
    ////                if (personData.get(0).get("PERSON_NUMBER") != null) {
    ////                    System.out.println("HR Specialist Number is " +
    ////                                       personData.get(0).get("PERSON_NUMBER"));
    ////                }
    ////                if (personData.get(0).get("DISPLAY_NAME") != null) {
    ////                    System.out.println("HR Specialist Name is " +
    ////                                       personData.get(0).get("DISPLAY_NAME"));
    //        ADFUtils.setBoundAttributeValue("Assignee",
    //                                        "1197");
    //                    ADFUtils.setBoundAttributeValue("AssigneeName","Haifa AlBarrak");
    ////                                                    personData.get(0).get("DISPLAY_NAME"));
    ////                }
    ////
    ////                else {
    ////                    JSFUtils.addFacesErrorMessage("You can't approve request as HR Specialist Name is empty");
    ////                    return null;
    ////                }
    //
    //                ADFUtils.findOperation("Commit").execute();
    ////                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
    ////                    personMail =
    ////                            personData.get(0).get("EMAIL_ADDRESS").toString();
    ////
    //                    sendEmail("1197", manpowerRow);
    ////                } else {
    ////                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR Specialist dosn't has email");
    ////
    ////                }
    ////            }
    //
    //
    //        }


           if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
               
               System.err.println("****IN FINSHED ");

//               String personNumber =
//                   (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
               String personName =
                   (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
              

               ADFUtils.setBoundAttributeValue("Assignee", personNumber);
               ADFUtils.setBoundAttributeValue("AssigneeName", personName);
               ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
               String value =
                   ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                         "ManPower",
                                                         (Number)nextStep.getAttribute("StepId"),
                                                         (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                         (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                         "APPROVE_ACT", "Y");
               updateInsert=1;
               ADFUtils.findOperation("Commit").execute();
              
               if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                sendEmail(personNumber, manpowerRow);
                sendEmailByEmail(fyiEmailAddress, manpowerRow);
//                 sendFYIEmail(fyiEmailAddress, manpowerRow);
//               sendFYIEmail("1620", manpowerRow);
//                sendFYIEmailByPersonNumber("1197", manpowerRow);
               }
           }
           if(updateInsert==0) {
               String value ="";
//               if(tamheerOrFulltime){
//                   Number stepId = (Number)nextStep.getAttribute("StepId");
//                   stepId = stepId.subtract(1);
//                   value =
//                       ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
//                                                             "ManPower",stepId,
////                                                             (Number)nextStep.getAttribute("StepId"),
//                                                             (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                             "APPROVE_ACT", "N");  
//               }else{
                   value =
                       ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                             "ManPower",
                                                             (Number)nextStep.getAttribute("StepId"),
                                                             (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                             "APPROVE_ACT", "N");  
//               }
               
               if (value.equalsIgnoreCase("SUCCESS")) {
                   ADFUtils.findOperation("Commit").execute();
                   
                   if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                       if("POSITION".equals(stepType)){
                           System.err.println("Email to mail-id::"+assigneeEmail);
                           sendEmailByEmail(assigneeEmail, manpowerRow);    
                       }else{
                           System.err.println("Email to user-id::"+assigneeNo);
                           sendEmail(assigneeNo, manpowerRow);    
                       }   
                   }
                   
               } else {
                   ADFUtils.findOperation("Rollback").execute();
               }
           }
          
          
    //        ADFUtils.findOperation("Commit").execute();
         
           JSFUtils.addFacesInformationMessage("Request has been approved");
           return "backToManPowerReq";
       }}

    public String rejectRequest() {
        // Add event code here...
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String manPowerType = ADFUtils.getBoundAttributeValue("Type")!=null ? (String)ADFUtils.getBoundAttributeValue("Type") :"";
        String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        Number nextstepid = (Number)nextStep.getAttribute("NextStepId");
        String assignee =
            ADFUtils.getBoundAttributeValue("Assignee").toString();
        //        String budgetFlag = ADFUtils.getBoundAttributeValue("BudgetFlag").toString();
//        if ((assignee.equals("Financial Planning & Analysis Manager")))
//
//        {
//            String personMail = null;
//            ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
//            ADFUtils.setBoundAttributeValue("Assignee", "CEO");
//
//            
//            JSFUtils.addFacesInformationMessage("Request has been Rejected and sent to the CEO to take the decision");
//            BiReportAccess report = new BiReportAccess();
//            List<Map> personData = null;
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("Chief Executive Officer");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            String cEOName = null;
//            if (personData.size() > 0) {
//
//                    if (personData.get(0).get("DISPLAY_NAME") != null) {
//                        cEOName = (String)personData.get(0).get("DISPLAY_NAME");
//                        System.out.println("Chief Executive Officer Name is " +
//                                           personData.get(0).get("DISPLAY_NAME"));
//
//                        ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                        cEOName);
//                        
//                    }else
//                    {
//                            ADFUtils.setBoundAttributeValue("AssigneeName","");
//                        
//                        }
//                String value =
//                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
//                                                          "ManPower",
//                                                           (Number)nextStep.getAttribute("StepId"),
//                                                          new Long(nextStep.getAttribute("StepId").toString()),
//                                                          cEOName,
//                                                          "REJECT_ACT", "");
//                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                    personMail =
//                            personData.get(0).get("EMAIL_ADDRESS").toString();
//                    ManPowerRequestViewRowImpl manpowerRow =
//                        (ManPowerRequestViewRowImpl)ADFUtils.findIterator("ManPowerRequestViewIterator").getCurrentRow();
//                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                    sendEmailByEmailToCEO(personMail, manpowerRow,cEOName);
//                    }
//                } else {
//                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Chief Executive Officer has no email");
//
//                } 
//                    
//            }
//            ADFUtils.findOperation("Commit").execute();
//           
//        }
        //else{--2023 All Rejections should assigned to Employee only Change

        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ManPowerRequestViewRowImpl manpowerRow =
            (ManPowerRequestViewRowImpl)ADFUtils.findIterator("ManPowerRequestViewIterator").getCurrentRow();
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
             sendEmail(personNumber, manpowerRow);
            }
        
            ADFUtils.findOperation("Commit").execute();
//            System.err.println("status===="+ADFUtils.getBoundAttributeValue("RequestStatus"));
//            System.out.println("--->"+ADFUtils.getBoundAttributeValue("ManPowerReqId"));
//            System.out.println("--->"+nextStep.getAttribute("StepId"));
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                          "ManPower",
                                                     (Number)nextStep.getAttribute("StepId"),
                                                      new Long(0),
                                                      null,
                                                      "REJECT_ACT", "");
            if (value.equalsIgnoreCase("SUCCESS")) {
                ADFUtils.findOperation("Commit").execute();
            } else {
                ADFUtils.findOperation("Rollback").execute();
            }  
        JSFUtils.addFacesInformationMessage("Manpower Requisition Request has been Rejected");
       // }
        return "backToManPowerReq";

        }
    
    public void sendEmailByEmailToCEO(String personEmail, Row subject,String cEOName) {

        sendManpowerEmailToCEO("OFOQ.HR@TATWEER.SA", personEmail,
                          (ManPowerRequestViewRowImpl)subject,cEOName);
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    public void sendManpowerEmailToCEO(String from, String to,
                                  ManPowerRequestViewRowImpl subject,String cEOName) {
        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }          
        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear Mr " + cEOName + "," + "\n" +
            "<br/>" +
            "Kindly we need your decision as this request rejected as no budget" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String returnmailManpower=ApprovelLine.getManPowerEmail(into,from,to,subject, "");
        if(returnmailManpower!=null && returnmailManpower.equalsIgnoreCase("failed"))
        {
            System.out.println("Exception Caught");
        }
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
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendManpowerEmail("OFOQ.HR@TATWEER.SA", email,
                              (ManPowerRequestViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }
    
    public void sendFYIEmail(String email, Row subject) {
//        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendFYIManpowerEmail("OFOQ.HR@TATWEER.SA", email,
                              (ManPowerRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }
    
    public void sendFYIEmailByPersonNumber(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendFYIManpowerEmail("OFOQ.HR@TATWEER.SA", email,
                              (ManPowerRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendManpowerEmail("OFOQ.HR@TATWEER.SA", personEmail,
                          (ManPowerRequestViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    public void sendFYIManpowerEmail(String from, String to,
                                  ManPowerRequestViewRowImpl subject) {
        if (to == null) {
          //  to = "vf.khayal@gmail.com";
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Manpower Requisition Request has been Approved" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";
        String returnmailManpower=ApprovelLine.getManPowerEmail(into,from,to,subject, "");
        if(returnmailManpower!=null && returnmailManpower.equalsIgnoreCase("failed"))
        {
            System.err.println("Exception Caught");
        }
    }

    public void sendManpowerEmail(String from, String to,
                                  ManPowerRequestViewRowImpl subject, String personFYI) {
        
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
            subj = "Manpower Requisition Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Manpower Requisition Request, and below the details";
            
        }else if("Waiting Withdraw Approval".equals(status)){
            subj = "Manpower Requisition Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Manpower Requisition Request, and below the details";
            
        }else if("Withdrawn Rejected".equals(status)){
            subj = "Manpower Requisition Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of Manpower Requisition request as below";
            
        }else if(edited!=null && edited.equals("Y") && "PENDING".equals(status)){
            subj = "Manpower Requisition Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Manpower Requisition Request, and below the details";
            
        }else{
            subj = "Manpower Requisition Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Manpower Requisition request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("Withdrawn".equals(status)){
                subj = "Manpower Requisition Request has been withdraw";
                hdrMsg= "Kindly be informed you that the following Manpower Requisition request has been withdraw";
            }
            if("APPROVED".equals(status)){
                subj = "Manpower Requisition Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Manpower Requisition request has been Approved";
            }
           
        }
//                        JSFUtils.addFacesInformationMessage(subj);
//                        JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
          //  to = "vf.khayal@gmail.com";
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String into = "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" +
            hdrMsg+
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";
        String returnmailManpower=ApprovelLine.getManPowerEmail(into,from,to,subject, subj);
        if(returnmailManpower!=null && returnmailManpower.equalsIgnoreCase("failed"))
        {
            System.out.println("Exception Caught");
        }
       
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

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
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
        //        ADFUtils.findOperation("Execute1").execute();

        return null;
    }

    public String cancelBtnAction() {
        // Add event code here...
        attachmentPopup.hide();
        return null;
    }

    public void setInputFile(RichInputFile inputFile) {
        this.inputFile = inputFile;
    }

    public RichInputFile getInputFile() {
        return inputFile;
    }

    public void setAttachmentPopup(RichPopup attachmentPopup) {
        this.attachmentPopup = attachmentPopup;
    }

    public RichPopup getAttachmentPopup() {
        return attachmentPopup;
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

    public String showAttachmentPopup() {
        // Add event code here...

        inputFile.resetValue();
        inputFile.setValid(true);
        attachmentPopup.show(new RichPopup.PopupHints());

        return null;
    }

    public BlobDomain getBlob(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;
        try {
            in = file.getInputStream();
            blobDomain = new BlobDomain();
            out = blobDomain.getBinaryOutputStream();
            IOUtils.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return blobDomain;
    }
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }

    public void setGradebinding(RichSelectOneChoice gradebinding) {
        this.gradebinding = gradebinding;
    }

    public RichSelectOneChoice getGradebinding() {
        return gradebinding;
    }

    public void setBasicsalary(RichInputText basicsalary) {
        this.basicsalary = basicsalary;
    }

    public RichInputText getBasicsalary() {
        return basicsalary;
    }

    public ArrayList<SelectItem> getVacantList() throws Exception {
        if(vacantList.size()==0){
            
            BiReportAccess BIRA = new BiReportAccess();
            ArrayList<String> list = new ArrayList<String>();
//            list.add("HR Specialist");
            List<Map> map=null;
            
            String department = (String)JSFUtils.resolveExpression("#{bindings.PersonDepartment.inputValue}");
            String personNo = (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
            String location = (String)JSFUtils.resolveExpression("#{bindings.PersonLocation.inputValue}");

            List<Map> privilegeMRMap = BIRA.getPrivilegesToManpowerData(location, department);
            System.err.println("privilegeMRMap===>>>"+privilegeMRMap); 
            if(privilegeMRMap.size()>0){
                System.err.println("In privilegeMRMap"); 
                for(int i = 0; i < privilegeMRMap.size(); i++){
                    if(privilegeMRMap.get(i).get("POSITION") != null){
                        list.add(privilegeMRMap.get(i).get("POSITION").toString());
                    }
                }
                
            }else{
                System.err.println("In privilegeMRMap - ELSE"); 
                map = BIRA.getVacantPositionsData(location, personNo); 
                for(int i = 0; i < map.size(); i++){
                    if(map.get(i).get("POSITION") != null){
                        list.add(map.get(i).get("POSITION").toString());
                    }
                } 
            }  
            
            System.err.println("LIST===>>>"+list); 
            for(String value : list){
                vacantList.add(new SelectItem(value));
            } 
        } 
        return vacantList;
    }

    public void setVacantList(ArrayList<SelectItem> vacantList) {
        this.vacantList = vacantList;
    }
    
    private boolean checkMandatory() {
           String manPowerType = (String)ADFUtils.getBoundAttributeValue("Type"); 
           String stepId =ADFUtils.getBoundAttributeValue("StepId").toString();
           String edit = JSFUtils.resolveExpression("#{PersonInfo.fullName eq bindings.AssigneeName.inputValue and pageFlowScope.SpecialEdit eq 'Y'}").toString();
           String budget = ADFUtils.getBoundAttributeValue("AvailableBudgect").toString();
//           if(stepId!=null && "3".equals(stepId)){
           System.err.println("Special edit: "+edit);
           if("true".equals(edit)){
               if(!"Full Time Job".equals(manPowerType) && !"Tamheer".equals(manPowerType) && !"Volunteer".equals(manPowerType) && !"Coop Training".equals(manPowerType)){
               
               System.err.println("AvailableBudgect=>"+ADFUtils.getBoundAttributeValue("AvailableBudgect"));
               
                   if("false".equals(budget))
                   {
                           JSFUtils.addFacesErrorMessage("Available Budgect is Mandatory");
                           return true; 
                   }else{
                           return false;
                   } 

               
               }else{
                   return false;
               } 
           }else{
               return false;
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

    public String onEditInSearch() { 
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("ManPowerRequestViewIterator").getViewObject();
        Row r = vo.getCurrentRow();
        if(r.getAttribute("RequestStatus") != null && 
           ("APPROVED".equals(r.getAttribute("RequestStatus"))|| "Withdrawn".equals(r.getAttribute("RequestStatus")) || "Withdrawn Rejected".equals(r.getAttribute("RequestStatus")))){
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }else{
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                 personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            String manPowerType = r.getAttribute("Type").toString()!=null ? r.getAttribute("Type").toString() :"";
            String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
        } 
        
        return "edit";
    }
    
    
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){

            ViewObject reqVo = ADFUtils.findIterator("ManPowerRequestViewIterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            
            currRow.setAttribute("StepId", "1");
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String manPowerType = (String)currRow.getAttribute("Type")!=null ? (String)currRow.getAttribute("Type") :"";
            String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
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
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"), 
                                                                             "ManPower", (Number)nextStep.getAttribute("NextStepId"), 
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
                String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"), 
                                                                             "ManPower", (Number)nextStep.getAttribute("NextStepId"), 
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
        ViewObject reqVo = ADFUtils.findIterator("ManPowerRequestViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personNo = ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String manPowerType = ADFUtils.getBoundAttributeValue("Type")!=null ? (String)ADFUtils.getBoundAttributeValue("Type") :"";
        String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
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
            ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                      "ManPower",
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
        
        return "backToManPowerReq";
    }
    
    public String approve_withdraw() {
        
        ViewObject reqVo = ADFUtils.findIterator("ManPowerRequestViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String manPowerType = ADFUtils.getBoundAttributeValue("Type")!=null ? (String)ADFUtils.getBoundAttributeValue("Type") :"";
        String ManPowerTypeName="Manpower"+'-'+manPowerType +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ManPowerTypeName", ManPowerTypeName);
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
    
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                      "ManPower",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                     sendEmailToPerson(personNumber, currRow);
                     sendEmailByEmail(fyiEmailAddress, currRow);
            } 
        }else{
            
            String value =
                ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ManPowerReqId"),
                                                      "ManPower",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
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
        return "backToManPowerReq";
        }

    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("ManPowerRequestViewIterator").getViewObject();
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
            sendManpowerEmail("OFOQ.HR@TATWEER.SA", email,
                                     (ManPowerRequestViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("ManPowerRequestViewIterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ManPowerRequestViewCriteria"));
            vo.executeQuery();
        }
    }
}
