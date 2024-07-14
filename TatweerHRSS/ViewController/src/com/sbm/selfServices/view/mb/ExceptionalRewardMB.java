package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.ChangePositionViewRowImpl;
import com.sbm.selfServices.model.views.up.DeptEmployeesVORowImpl;
import com.sbm.selfServices.model.views.up.ExceptionalRewardViewRowImpl;
import com.sbm.selfServices.model.views.up.ExitReEntryVORowImpl;
import com.sbm.selfServices.model.views.up.ManagerEmployeesVORowImpl;
import com.sbm.selfServices.model.views.up.SpecialNeedSupportViewRowImpl;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.JAXBElement;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.adf.view.rich.component.rich.input.RichInputText;

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

public class ExceptionalRewardMB {
    private String attachmentFileName;
    private RichInputFile inputFile;
    private RichPopup attachmentPopup;
    private FusionDataLoader fusionFileLoader;


    public ExceptionalRewardMB() {
    }

    public String newRequest() {
        // Add event code here...
        JSFUtils.setExpressionValue("#{pageFlowScope.eRPersonNummber}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.eRPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));

        BiReportAccess BIRA = new BiReportAccess();

        String mngrNumber =
            JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}").toString();


        List<Map> list1 = null;
        try {
            list1 = BIRA.getMngrEmployees(mngrNumber);

            DCIteratorBinding mngrEmployeestIter =
                ADFUtils.findIterator("ManagerEmployeesVO1Iterator");
            ViewObject mngrEmployeesView = mngrEmployeestIter.getViewObject();
            ManagerEmployeesVORowImpl mngrEmployeesRow = null;

            if (list1.size() > 0) {

                if (list1.size() == 1) {
                    if (list1.get(0).get("PERSON_NUMBER") == null) {
                        ADFUtils.findOperation("deleteMngrEmpsTableRows").execute();
                        System.out.println("All Manager Employees table rows deleted successfully");

                    } else {

                        ADFUtils.findOperation("deleteMngrEmpsTableRows").execute();
                        System.out.println("All Manager Employees table rows deleted successfully");
                        mngrEmployeesRow =
                                (ManagerEmployeesVORowImpl)mngrEmployeesView.createRow();
                        mngrEmployeesRow.setEmployeeNumber(list1.get(0).get("PERSON_NUMBER").toString());
                        mngrEmployeesRow.setEmployeeName(list1.get(0).get("DISPLAY_NAME").toString());
                        mngrEmployeesRow.setSalaryAmount(list1.get(0).get("SALARY_AMOUNT").toString());
                        mngrEmployeesRow.setManagerNumber(list1.get(0).get("MANAGER_NUMBER").toString());

                    }

                } else {
                    ADFUtils.findOperation("deleteMngrEmpsTableRows").execute();
                    System.out.println("All Manager Employees table rows deleted successfully");
                    for (Map currentDeptEmployee : list1) {
                        mngrEmployeesRow =
                                (ManagerEmployeesVORowImpl)mngrEmployeesView.createRow();
                        mngrEmployeesRow.setEmployeeNumber(currentDeptEmployee.get("PERSON_NUMBER").toString());
                        mngrEmployeesRow.setEmployeeName(currentDeptEmployee.get("DISPLAY_NAME").toString());
                        mngrEmployeesRow.setSalaryAmount(currentDeptEmployee.get("SALARY_AMOUNT").toString());
                        mngrEmployeesRow.setManagerNumber(currentDeptEmployee.get("MANAGER_NUMBER").toString());

                    }
                    mngrEmployeesView.insertRow(mngrEmployeesRow);

                    ADFUtils.findOperation("Commit").execute();

                }

            }

            else {

                ADFUtils.findOperation("deleteMngrEmpsTableRows").execute();
                System.out.println("All Manager Employees table rows deleted successfully");

            }

            mngrEmployeesView.executeQuery();
            mngrEmployeestIter.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "newExceptionalReward";
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

    public void setInputFile(RichInputFile inputFile) {
        this.inputFile = inputFile;
    }

    public RichInputFile getInputFile() {
        return inputFile;
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

    public String cancelBtnAction() {
        // Add event code here...
        attachmentPopup.hide();
        return null;
    }

    public void setAttachmentPopup(RichPopup attachmentPopup) {
        this.attachmentPopup = attachmentPopup;
    }

    public RichPopup getAttachmentPopup() {
        return attachmentPopup;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public String submitRequest() throws SQLException {
        // Add event code here...
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String ExceptionalRewardTypeName="ExceptionalReward" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExceptionalRewardTypeName", ExceptionalRewardTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        if (ADFUtils.getBoundAttributeValue("PersonNumber") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as Person Number is empty");
            return null;


        }
        if (ADFUtils.getBoundAttributeValue("PersonName") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as Person Name is empty");
            return null;


        }

        if (ADFUtils.getBoundAttributeValue("CreatedByNo") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as you must select employee");
            return null;


        }

        if (ADFUtils.getBoundAttributeValue("CreatedByName") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as you must select employee");
            return null;


        }


        if (ADFUtils.getBoundAttributeValue("EmpBasicSalary") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as Employee has no Salary");
            return null;


        }

//        ADFUtils.setBoundAttributeValue("StepId",
//                                        nextStep.getAttribute("NextStepId"));
//        ADFUtils.setBoundAttributeValue("Assignee",
//                                        nextStep.getAttribute("NextAssignee"));
        
        //        String managerNumber =
        //            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");
        //
        //        Long lineManagerID =
        //            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        //        String stringLineManagerID = lineManagerID.toString();
        //        if (stringLineManagerID == null) {
        //            JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not submit the request");
        //            return null;
        //        }
        //        ADFUtils.setBoundAttributeValue("Assignee", managerNumber);
        
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
         String managerNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

        Long lineManagerID =
            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        String stringLineManagerID = lineManagerID!=null ?lineManagerID.toString(): null;
        
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
        ADFUtils.setBoundAttributeValue("PersonGrade", gradeCode);
        System.err.println("gradeCode is >>> " + gradeCode);


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
        
        

//        BiReportAccess report = new BiReportAccess();
//        List<Map> personData = null;
//
//        try {
//            personData =
//                    report.getPersonByPostionReport("HR and Admin Director");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (personData.get(0).get("PERSON_NUMBER") != null) {
//            System.err.println("HR and Admin Director Number is " +
//                               personData.get(0).get("PERSON_NUMBER"));
//        }
//        if (personData.get(0).get("DISPLAY_NAME") != null) {
//            System.err.println("HR and Admin Director Name is " +
//                               personData.get(0).get("DISPLAY_NAME"));
//
//            ADFUtils.setBoundAttributeValue("AssigneeName",
//                                            personData.get(0).get("DISPLAY_NAME").toString());
//        }
        ADFUtils.findOperation("Commit").execute();
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        String value=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"), 
                                                "ExceptionalReward", new Number(nextStep.getAttribute("NextStepId").toString()),
                                                new Long(ADFUtils.getBoundAttributeValue("StepId").toString()), 
                                                (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                act, "");
        Row exceptionalRewardRow =
            ADFUtils.findIterator("ExceptionalRewardView1Iterator").getCurrentRow();
        JSFUtils.addFacesInformationMessage("Request has been submitted");
//        ADFUtils.findOperation("Execute").execute();
        //        if (managerEmail == null) {
        //            JSFUtils.addFacesErrorMessage("You don't have Line Manager Email, So mail can't be sent to manager");
        //            return "back";
        //        }


//        if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//            System.err.println("HR and Admin Director Email " +
//                               personData.get(0).get("EMAIL_ADDRESS"));
//            if (emailNotification != null &&
//                emailNotification.equalsIgnoreCase("Y")) {
//                sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                                 exceptionalRewardRow);
//            }
//        } else {
//            JSFUtils.addFacesErrorMessage("HR and Admin Director don't have Email, So mail can't be sent");
//            return "back";
//
//        }
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, exceptionalRewardRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, exceptionalRewardRow);    
            } 
        }

        return "back";
    }

    public String approveRequest() {
        // Add event code here...
        int updateInsert=0;
        String personMail = null;
        Row exceptionalRewardRow =
            ADFUtils.findIterator("ExceptionalRewardView1Iterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ExceptionalRewardTypeName="ExceptionalReward" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExceptionalRewardTypeName", ExceptionalRewardTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();

        Number previousStepId = (Number)nextStep.getAttribute("StepId");
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));

//        ADFUtils.setBoundAttributeValue("StepId",
//                                        nextStep.getAttribute("NextStepId"));
//        ADFUtils.setBoundAttributeValue("Assignee",
//                                        nextStep.getAttribute("NextAssignee"));
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
        //        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
        //
        //            BiReportAccess report = new BiReportAccess();
        //            List<Map> personData = null;
        //
        //            try {
        //                personData = report.getPersonByPostionReport("HR and Admin Director");
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //            if (personData.size() > 0) {
        //                if (personData.get(0).get("PERSON_NUMBER") != null) {
        //                    System.out.println("HR and Admin Director Number is " +
        //                                       personData.get(0).get("PERSON_NUMBER"));
        //                }
        //                if (personData.get(0).get("DISPLAY_NAME") != null) {
        //                    System.out.println("HR and Admin Director Name is " +
        //                                       personData.get(0).get("DISPLAY_NAME"));
        //
        //                    ADFUtils.setBoundAttributeValue("AssigneeName",
        //                                                    personData.get(0).get("DISPLAY_NAME"));
        //                }
        //
        //                else {
        //                    JSFUtils.addFacesErrorMessage("You can't approve request as HR and Admin Director Name is empty");
        //                    return null;
        //                }
        //
        //                ADFUtils.findOperation("Commit").execute();
        //                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
        //                    personMail =
        //                            personData.get(0).get("EMAIL_ADDRESS").toString();
        //
        //                     sendEmailByEmail(personMail, exceptionalRewardRow);
        //                } else {
        //                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR and Admin Director dosn't has email");
        //
        //                }
        //            }
        //        }
        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        String personNo = (String)ADFUtils.getBoundAttributeValue("PersonNumber");
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
        if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")) {


            Number percentageFromBasicSal =
                (Number)ADFUtils.getBoundAttributeValue("PercentageFromBasicSal");
            Number hrRecomendations =
                (Number)ADFUtils.getBoundAttributeValue("HrRecomendations");
            if (percentageFromBasicSal == null || hrRecomendations == null) {

                JSFUtils.addFacesErrorMessage("You can't approve the request as Percentage From Salary and HR Recomendation must have values");
                ADFUtils.findOperation("Rollback").execute();
                return null;

            }

            BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;

            try {
                personData =
                        report.getPersonByPostionReport("Financial Planning & Analysis Manager");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (personData.size() > 0) {

                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    System.out.println("Financial Planning & Analysis Manager Number is " +
                                       personData.get(0).get("PERSON_NUMBER"));
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    System.out.println("Financial Planning & Analysis Manager Name is " +
                                       personData.get(0).get("DISPLAY_NAME"));

                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME"));
                }

                else {
                    JSFUtils.addFacesErrorMessage("You can't approve request as Financial Planning & Analysis Manager Name is empty");
                    return null;
                }
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"),
                                                          "ExceptionalReward",
                                                          previousStepId,
                                                          new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "N");
                updateInsert=1;
                ADFUtils.findOperation("Commit").execute();
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmail(personMail, exceptionalRewardRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Financial Planning & Analysis Manager dosn't has email");

                }
            }


        }

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
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"),
                                                          "ExceptionalReward",
                                                          previousStepId,
                                                        new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "N");
                updateInsert=1;
                ADFUtils.findOperation("Commit").execute();
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmail(personMail, exceptionalRewardRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");

                }
            }


        }

        if (nextStep.getAttribute("NextAssignee").equals("CEO")) {

            BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;

            try {
                personData =
                        report.getPersonByPostionReport("Chief Executive Officer");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (personData.size() > 0) {

                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    System.out.println("Chief Executive Officer Number is " +
                                       personData.get(0).get("PERSON_NUMBER"));
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    System.out.println("Chief Executive Officer Name is " +
                                       personData.get(0).get("DISPLAY_NAME"));

                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME"));
                }

                else {
                    JSFUtils.addFacesErrorMessage("You can't approve request as Chief Executive Officer Name is empty");
                    return null;
                }
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"),
                                                          "ExceptionalReward",
                                                          previousStepId,
                                                            new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "N");
                updateInsert=1;
                ADFUtils.findOperation("Commit").execute();
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmail(personMail, exceptionalRewardRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Chief Executive Officer dosn't has email");

                }
            }


        }
*/
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {

            String personNumber =
                (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");

            String selectedPerson =
                (String)JSFUtils.resolveExpression("#{bindings.CreatedByNo.inputValue}");

            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
            System.out.println("the person number is ==============> " +
                               personNumber);

            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");


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
                        report.getExceptionalRewardDatFileData(selectedPerson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HashMap<String, String> params = new HashMap<String, String>();
            //            params.put("Date", creationDate.toString());
            if (datFileData.size() > 0) {
                params.put("AssignmentNumber",
                           (datFileData.get(0).get("ASSIGNMENT_NUMBER")!=null?datFileData.get(0).get("ASSIGNMENT_NUMBER").toString():""));
                params.put("Amount",
                           ADFUtils.getBoundAttributeValue("HrRecomendations").toString());
                params.put("Date", formattedDate);
                params.put("Count",
                           (datFileData.get(0).get("MULTIPLEENTRYCOUNT")!=null?datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString():""));
            }

            else {

                JSFUtils.addFacesErrorMessage("You can't create Exceptional Reward element as the Assignment number or Multiple Entry Count is null");

            }
            try {
                fusionFileLoader = new FusionDataLoader();
                
                //EES code added by Moshina
                Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 7);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("ExceptionalRewardId").toString(), personNumber, "Approved", "Exceptional Reward Allowance", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Approved Exceptional Reward Allowance: "+value);
                //fusionFileLoader.sendFusionRequest(params, 7);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"),
                                                      "ExceptionalReward",
                                                      previousStepId,
                                                        new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                      "APPROVE_ACT", "Y");
            updateInsert=1;
            if (emailNotification != null &&
                emailNotification.equalsIgnoreCase("Y")) {
                sendEmailToPerson(personNumber, exceptionalRewardRow);
                sendEmailByEmail(fyiEmailAddress, exceptionalRewardRow);
//                sendEmailForExceptionalRewardForEmp(fyiEmailAddress,
//                                                    exceptionalRewardRow);
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
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"),
                                                  "ExceptionalReward",
                                                  previousStepId,
                                                  Long.valueOf(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                  "APPROVE_ACT", finalapproval);
        
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if("POSITION".equals(stepType)){
                    System.err.println("Email to mail-id::"+assigneeEmail);
                    sendEmailByEmail(assigneeEmail, exceptionalRewardRow);    
                }else{
                    System.err.println("Email to user-id::"+assigneeNo);
                    sendEmail(assigneeNo, exceptionalRewardRow);    
                }   
            }
        }
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";
    }

    public String saveRequest() {
        // Add event code here...
        if (ADFUtils.getBoundAttributeValue("PersonNumber") == null) {


            JSFUtils.addFacesErrorMessage("You can't Save the request as you must select Person");
            return null;


        }
        if (ADFUtils.getBoundAttributeValue("PersonName") == null) {


            JSFUtils.addFacesErrorMessage("You can't Save the request as Person Name is empty");
            return null;


        }

        if (ADFUtils.getBoundAttributeValue("CreatedByNo") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as you must select employee");
            return null;


        }

        if (ADFUtils.getBoundAttributeValue("CreatedByName") == null) {


            JSFUtils.addFacesErrorMessage("You can't submit the request as you must select employee");
            return null;


        }


        if (ADFUtils.getBoundAttributeValue("EmpBasicSalary") == null) {


            JSFUtils.addFacesErrorMessage("You can't Save the request as Employee has no Salary");
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
        JSFUtils.addFacesInformationMessage("Exceptional Reward Request has been saved");
        ADFUtils.findOperation("Execute").execute();

        return "back";
    }

    public void sendEmail(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendExceptionalRewardEmail("OFOQ.HR@TATWEER.SA", email,
                                       (ExceptionalRewardViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
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

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendExceptionalRewardEmail("OFOQ.HR@TATWEER.SA", personEmail,
                                   (ExceptionalRewardViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendExceptionalRewardEmail(String from, String to,
                                           ExceptionalRewardViewRowImpl subject, String personFYI) {
        
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
            subj = "Exceptional Reward Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Exceptional Reward Request, and below the details";
            
        }else{
            subj = "Exceptional Reward Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Exceptional Reward request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("APPROVED".equals(status)){
                subj = "Exceptional Reward Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Exceptional Reward request has been Approved";
            }
        }
//        JSFUtils.addFacesInformationMessage(subj);
//        JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
            //to = "heleraki@gmail.com";
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

        String exceptionalRewardDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Exceptional Reward Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Reward Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getRewardAmount() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Percentage From Basic Salary\n" +
            "      </th>\n" +
        "      <td width=\"50%\">" + (subject.getPercentageFromBasicSal()!=null?subject.getPercentageFromBasicSal():" ") +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Basic Salary\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +  (subject.getEmpBasicSalary()!=null?subject.getEmpBasicSalary():" ") + "</td>\n" +
        "    </tr><tr>\n" +
        "      <th>\n" +
        "        &nbsp;Requested For Employee\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +  (subject.getCreatedByNo()!=null?subject.getCreatedByNo():" ") + "</td>\n" +
        "    </tr><tr>\n" +
        "      <th>\n" +
        "        &nbsp;Requested For Name\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +  (subject.getCreatedByName()!=null?subject.getCreatedByName():" ") + "</td>\n" +
        "    </tr><tr>\n" +
        "      <th>\n" +
        "        &nbsp;Justification\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +  (subject.getJustification()!=null?subject.getJustification():" ") + "</td>\n" +
        "    </tr><tr>\n" +
        "      <th>\n" +
        "        &nbsp;Last Reward Date	\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" +  (subject.getLastRewardDate()!=null?subject.getLastRewardDate():" ") + "</td>\n" +
            "    </tr>\n" + 
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryRewardIterator");       
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + exceptionalRewardDetails +verticalSpace+ApprovalPart1+
            thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject", subj);
//                                    "Exceptional Reward Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }


    public String rejectRequest() {
        // Add event code here...
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String ExceptionalRewardTypeName="ExceptionalReward" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("ExceptionalRewardTypeName", ExceptionalRewardTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ExceptionalRewardViewRowImpl exceptionalRewardRow =
            (ExceptionalRewardViewRowImpl)ADFUtils.findIterator("ExceptionalRewardView1Iterator").getCurrentRow();
        if (emailNotification != null &&
            emailNotification.equalsIgnoreCase("Y")) {
            sendEmail(personNumber, exceptionalRewardRow);
        }
        ADFUtils.findOperation("Commit").execute();
        String value =
                 ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"),
                                                           "ExceptionalReward",
                                                      (Number)nextStep.getAttribute("StepId"),
                                                       new Long(0),
                                                       null,
                                                       "REJECT_ACT", "");
        JSFUtils.addFacesInformationMessage("Exceptional Reward Request has been Rejected");

        return "back";
    }

    public void calcHRRecommendation(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        if (valueChangeEvent.getNewValue() != null) {
            Number percentageFromSalary =
                (Number)valueChangeEvent.getNewValue();
            String salary =
                ADFUtils.getBoundAttributeValue("EmpBasicSalary").toString();
            Number numberSalary = null;

            try {
                numberSalary = new Number(salary);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Number result = (numberSalary.multiply(percentageFromSalary));
            Number hrRecomendations = result.divide(100);
            ADFUtils.setBoundAttributeValue("HrRecomendations",
                                            hrRecomendations);
        } else {
            ADFUtils.setBoundAttributeValue("HrRecomendations", null);
            JSFUtils.addFacesErrorMessage("You must enter value for percentage From Salary");

        }
    }


    public void setLastRewardDateValue(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        if (valueChangeEvent.getNewValue() != null) {
            System.err.println("per no 1:" + valueChangeEvent.getNewValue());
            String personNumber = (String)valueChangeEvent.getNewValue();
            OperationBinding lastExcepRewardDateOper =
                ADFUtils.findOperation("getLastExceptionalRewardDate");
            lastExcepRewardDateOper.getParamsMap().put("bindPersonNumber",
                                                       personNumber);

            String lastExcepRewardDate =
                (String)lastExcepRewardDateOper.execute();
            System.out.println("lastExcepRewardDate  >>> " +
                               lastExcepRewardDate);
            ADFUtils.setBoundAttributeValue("LastRewardDateString",
                                            lastExcepRewardDate);
            System.err.println("Ana 5");


        }
    }

    public String checkSession() {
        String personNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee =
            (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if (personNumber == null || assignee == null) {
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }

    public void sendEmailForExceptionalRewardForEmp(String email,
                                                    Row subject) {
//        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendExceptionalRewardEmailForFyi("OFOQ.HR@TATWEER.SA", email,
                                             (ExceptionalRewardViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendExceptionalRewardEmailForFyi(String from, String to,
                                                 ExceptionalRewardViewRowImpl subject) {
        if (to == null) {
           // to = "heleraki@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Exceptional Reward request has been Approved" +
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

        String exceptionalRewardDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Exceptional Reward Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Reward Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getRewardAmount() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Percentage From Basic Salary\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPercentageFromBasicSal() +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Basic Salary\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getBasicSalary() + "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryRewardIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + exceptionalRewardDetails +verticalSpace+ApprovalPart1+
            thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Exceptional Reward Request " + subject.getRequestStatus());
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

    public String onEditInSearch() {
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("ExceptionalRewardView1Iterator").getViewObject();
        Row r = vo.getCurrentRow();
        if(r.getAttribute("RequestStatus") != null &&
            ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")))){
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }else{
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                 personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            String ExceptionalRewardTypeName="ExceptionalReward" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ExceptionalRewardTypeName", ExceptionalRewardTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
        }
        return "edit";
    }
    
    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("ExceptionalRewardView1Iterator").getViewObject();
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
            sendExceptionalRewardEmail("OFOQ.HR@TATWEER.SA", email,
                                     (ExceptionalRewardViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){
            
            ViewObject reqVo = ADFUtils.findIterator("ExceptionalRewardView1Iterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String ExceptionalRewardTypeName="ExceptionalReward" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("ExceptionalRewardTypeName", ExceptionalRewardTypeName);
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
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("ExceptionalRewardId"), 
                                                                             "ExceptionalReward", (Number)nextStep.getAttribute("NextStepId"), 
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
        
        ViewObject vo = ADFUtils.findIterator("ExceptionalRewardView1Iterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ExceptionalRewardViewCriteria"));
            vo.executeQuery();
        }
    }
}
