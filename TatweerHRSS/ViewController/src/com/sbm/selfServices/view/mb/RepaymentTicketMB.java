package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.APConsumer;
import com.sbm.CodeCombinationConsumer;
import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.RepaymentTicketsViewRowImpl;
import com.sbm.selfServices.model.views.up.SpecialNeedSupportViewRowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;
import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStripApp.FilmStripBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import org.apache.myfaces.trinidad.model.UploadedFile;

public class RepaymentTicketMB {
    private FusionDataLoader fusionFileLoader;
    private String attachmentFileName;
    private RichInputFile inputFile;
    private RichPopup attachmentPopup;
    public RepaymentTicketMB() {
    }
    List<Map> budgetlist = null;
    Number accnumberBudget = null;
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }

    public String newRequest() {
        // Add event code here...
        JSFUtils.setExpressionValue("#{pageFlowScope.rTPersonNummber}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.rTPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));


        return "newRepaymentTickets";
    }

    public String saveRequest() {
        // Add event code here...
        long count = ADFUtils.findIterator("RepaymentTicketsAttachmentsView1Iterator").getEstimatedRowCount();
        
        if (count < 1) {
            JSFUtils.addFacesErrorMessage("You must add the required attachments before saving the request");
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
        JSFUtils.addFacesInformationMessage("Repayment Tickets Request has been saved");
        ADFUtils.findOperation("Execute").execute();

        return "back";
    }

    public static java.util.Date convertDomainDateToUtilDate(oracle.jbo.domain.Date domainDate) {
        java.util.Date date = null;
        if (domainDate != null) {
            java.sql.Date sqldate = domainDate.dateValue();
            date = new java.util.Date(sqldate.getTime());
        }
        return date;
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

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
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

    public String submitRequest() {
        // Add event code here...
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String RepaymentTicketsName="RepaymentTickets" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("RepaymentTicketsName", RepaymentTicketsName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
       
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        long count = ADFUtils.findIterator("RepaymentTicketsAttachmentsView1Iterator").getEstimatedRowCount();
        
        if (count < 1) {
            JSFUtils.addFacesErrorMessage("You must add the required attachments before submitting the request");
            return null;
        }

        BiReportAccess report = new BiReportAccess();
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
       
        Row repaymentTicketRow =
            ADFUtils.findIterator("RepaymentTicketsView1Iterator").getCurrentRow();

        Date date = (Date)repaymentTicketRow.getAttribute("CreationDate");
        System.err.println(date);
       
        List<Map> personAccrualData = null;

        try {
            personAccrualData =
                    report.getPersonAccrualData(date.toString(), personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        if (personAccrualData == null) {

            JSFUtils.addFacesErrorMessage("You can't submit the request as you has no accrular balance");
            return null;

        } else {
            if (personAccrualData.get(0).get("END_BAL") != null) {
                Number balance = null;
                try {
                    balance =
                            new Number(personAccrualData.get(0).get("END_BAL"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (balance.compareTo(0) > 1) {

                    JSFUtils.addFacesErrorMessage("You can't submit the request as your has no ticket balance");
                    return null;

                } else {

                    ADFUtils.setBoundAttributeValue("EndBalance", balance);


                }

            } else {

                JSFUtils.addFacesErrorMessage("You can't submit the request as ticket balance is null");
                return null;

            }
        }
       
 
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;

        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        
        String managerNumber =
           (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

        Long lineManagerID =
           (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        String stringLineManagerID = lineManagerID.toString();


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

/*
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
        ADFUtils.setBoundAttributeValue("AssigneeName",
                                        personData.get(0).get("DISPLAY_NAME").toString());
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("Assignee",
                                        nextStep.getAttribute("NextAssignee"));
*/        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        // If step type is POSITION
        if("POSITION".equals(stepType)){
        System.err.println("Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
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
        
        ADFUtils.findOperation("Commit").execute();
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        String value=  ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RepaymentTicketsId"), 
                                                "RepaymentTicket", (Number)nextStep.getAttribute("NextStepId"), 
                                                new Long(ADFUtils.getBoundAttributeValue("StepId").toString()), 
                                                (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                 act, "");
        JSFUtils.addFacesInformationMessage("Request has been submitted");
//        ADFUtils.findOperation("Execute").execute();
//        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//         sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                        repaymentTicketRow);
//        }
      if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, repaymentTicketRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, repaymentTicketRow);    
            } 
        }
        return "back";
    }
    
    public void sendEmail(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendRepaymentTicketsEmail("OFOQ.HR@TATWEER.SA", email,
                                      (RepaymentTicketsViewRowImpl)subject, "");
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

        sendRepaymentTicketsEmail("OFOQ.HR@TATWEER.SA", personEmail,
                                  (RepaymentTicketsViewRowImpl)subject,  "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendRepaymentTicketsEmail(String from, String to,
                                          RepaymentTicketsViewRowImpl subject, String personFYI) {
        
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
            subj = "Repayment Tickets Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Repayment Tickets Request, and below the details";
            
        }else{
            subj = "Repayment Tickets Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Repayment Tickets request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("APPROVED".equals(status)){
                subj = "Repayment Tickets Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Repayment Tickets request has been Approved";
            }
        }
//                                JSFUtils.addFacesInformationMessage(subj);
//                                JSFUtils.addFacesInformationMessage(hdrMsg);
        
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
        String amount = subject.getAmount() != null ? subject.getAmount().toString() : "";

        String repaymentTicketsDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Repayment Tickets Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Amount\n" +
            "      </th>\n" +
        "      <td width=\"50%\">" + amount  + "</td>\n" +
            "    </tr>\n" +
            "  </table>";
        

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryRepaymentIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + repaymentTicketsDetails +verticalSpace+ApprovalPart1+
            thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver",
                                    to );
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject", subj);
//                                    "Repayment Tickets Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
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
                                              "110896", "00", "0000",
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
                                                                  "110896",
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



    public String approveRequest() {
        // Add event code here...
        String returnValue = null;
        BiReportAccess report = new BiReportAccess();
        String personMail = null;
        int updateInsert=0;
        Row repaymentTicketsdRow =
            ADFUtils.findIterator("RepaymentTicketsView1Iterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String RepaymentTicketsName="RepaymentTickets" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("RepaymentTicketsName", RepaymentTicketsName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        Number previousStepId = (Number)nextStep.getAttribute("StepId");
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
/*
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("Assignee",
                                        nextStep.getAttribute("NextAssignee"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
            Number compensationPrice =
                (Number)ADFUtils.getBoundAttributeValue("CompensationPrice");
            Number amount =
                (Number)ADFUtils.getBoundAttributeValue("Amount");
            if (compensationPrice == null || amount == null) {

                JSFUtils.addFacesErrorMessage("You can't approve the request as Compensation Price and Amount must have values");
                return null;

            }


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
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RepaymentTicketsId"),
                                                          "RepaymentTicket",
                                                          previousStepId,
                                                          new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "N");
                updateInsert=1;
                ADFUtils.findOperation("Commit").execute();
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                     sendEmailByEmail(personMail, repaymentTicketsdRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR and Admin Director dosn't has email");

                }
            }
        }

*/
        
                        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        String personNo = (String)ADFUtils.getBoundAttributeValue("PersonNumber");
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        userDetails = userService.getUserDetailsByPersonNumber(personNo);
        
        
        if("POSITION".equals(stepType)){
            System.err.println("Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
             
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
        
        
        
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            List<UserWorkRelationshipDetails> relationshipDetails =
                                   userDetails.getUserWorkRelationshipDetails();
            String userEmployeeType=relationshipDetails.get(0).getUserPersonType();
            String personNumber =
                (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
            System.out.println("the person number is ==============> " +
                               personNumber);

            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");

Number amountNo = (Number)ADFUtils.getBoundAttributeValue("Amount");
           amountNo = (Number)amountNo.round(2);
                
            String amount = amountNo.toString();


            Row repaymentTicketRow =
                ADFUtils.findIterator("RepaymentTicketsView1Iterator").getCurrentRow();

            Date date = (Date)repaymentTicketRow.getAttribute("CreationDate");
            System.err.println(date);

            List<Map> personAccrualData = null;

            try {
                personAccrualData =
                        report.getPersonAccrualData(date.toString(),
                                                    personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (personAccrualData.get(0).get("END_BAL") == null) {

                JSFUtils.addFacesErrorMessage("You can't approve the request as the employee has no accrual details");
                return null;

            }

            String codeCombinationIdLine = getCodeCombinationId(personNumber);
            String division =
                            (String)(ADFContext.getCurrent().getSessionScope().get("division") == "" ? null :
                                                    ADFContext.getCurrent().getSessionScope().get("division"));
            String lob =
                            (String)(ADFContext.getCurrent().getSessionScope().get("lob") == "" ? null :
                                                    ADFContext.getCurrent().getSessionScope().get("lob"));
            String costCenter =
                            (String)(ADFContext.getCurrent().getSessionScope().get("costCenter") == "" ? null :
                                                    ADFContext.getCurrent().getSessionScope().get("costCenter"));
            if (codeCombinationIdLine == null) {

                JSFUtils.addFacesErrorMessage("You can't approve the request before creating the mentioned Code combination so please refer to the financial consultant");
                return null;

            }
            String formattedDate = date.toString().replace('-', '/');
            HashMap<String, String> params = new HashMap<String, String>();

//            Number endBalance =
//                (Number)personAccrualData.get(0).get("END_BAL");
                        Number endBalance=null;
            try {
                endBalance = new Number(personAccrualData.get(0).get("END_BAL").toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Number minusEndBalance = endBalance.multiply(-1);

            params.put("Date", formattedDate);
            params.put("PersonNumber", personNumber);
            params.put("WorkTermsNumber",
                       personAccrualData.get(0).get("ASSIGNMENT_NUMBER").toString());
            params.put("EndBal", minusEndBalance.toString());
            params.put("PerAccrualEntryId",
                       personAccrualData.get(0).get("PER_ACCRUAL_ENTRY_ID").toString());

            try {
                fusionFileLoader = new FusionDataLoader();
                Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 8);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("RepaymentTicketsId").toString(), personNumber, "Approved", "Tickets Accrual", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Approved Tickets Accrual: "+value);
                //fusionFileLoader.sendFusionRequest(params, 8);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String creationDate = null;
              creationDate=  (String)ADFUtils.getBoundAttributeValue("CreationDate");
               DateFormat dateFormat =null;
               try{
                 dateFormat =  new SimpleDateFormat("dd-MM-yyyy");
                 java.util.Date date1 = dateFormat.parse(creationDate);
                 GregorianCalendar cal = new GregorianCalendar();
                 cal.setTime(date1);
                 dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 creationDate = dateFormat.format(date1);
               }
                   catch(Exception e){
                       JSFUtils.addFacesErrorMessage("Error");
                       return null;
                   }
               
            Long longCodeCombinationIdLine =
                Long.parseLong(codeCombinationIdLine);

            String requestId =
                ADFUtils.getBoundAttributeValue("RepaymentTicketsId").toString();
            String invoiceNumber =
                personNumber + "-" + requestId + "-RepaymentTickets";
            System.err.println("invoiceNumber >>>> " + invoiceNumber);
            String description = personNumber + " - " + personName;
          if(!userEmployeeType.equals("Employee")){ 
//            Map param = new HashMap();
//            param.put("vendorName",
//                      "TICKET PROVISION"); //select vendor_name  from poz_suppliers_v
//            param.put("vendorId",
//                      "300000007191937"); //select vendor_id from poz_suppliers_v
//            param.put("vendorSiteCode", "Riyadh");
//            param.put("ledgerId", "300000001778002");
//            param.put("orgId", "300000001642073");
//            param.put("invoiceNumber", invoiceNumber);
//            param.put("invoiceAmount", amount);
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
//            line1.put("lineAmount", amount); //var
//            line1.put("lineCurrencyCode", "SAR");
//            line1.put("codeCombinationId", longCodeCombinationIdLine);
//            line1.put("personNumber", personNumber);
//            line1.put("bankName", "");
//            line1.put("IBAN", "");
//            line1.put("SwiftCode", "");
//            line1.put("PaymentType", "None");
//
//            List<Map> lineList = new ArrayList<Map>();
//            lineList.add(line1);
//            
//            List<Map> attacheList = new ArrayList<Map>();
//            
//            APConsumer newAPInvoice = new APConsumer();
//            returnValue = newAPInvoice.createInvoice(param, lineList, attacheList);
                               byte[] bytes = null;
                               //bytes=this.getBusinessTripAttach();
                               BlobDomain blob =new BlobDomain(bytes);
                               OperationBinding sendMail =
                               ADFUtils.findOperation("callSendInvoiceEmailStoredPL");
                               sendMail.getParamsMap().put("p_request_type", "Repayment Ticket");
                               sendMail.getParamsMap().put("p_request_number", requestId);
                               sendMail.getParamsMap().put("p_attachment", blob);
                               sendMail.execute();
              returnValue = "success";
            invoiceNumber="";
        } //2023 Element Entry Change
         if (userEmployeeType.equals("Employee")) { //2023 Element Entry Change
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
                                           //EES - account number code added by Moshina
                                           OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                             Map paramMap = oper.getParamsMap();
                                             paramMap.put("elementName", "Repayment Ticket Earnings");
                                             Row elementAccountNo = (Row)oper.execute();
                                             String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                                             System.err.println("EES: AccountNumber is::"+accountNumber);
                                           //---------------------------------------------
                                          
                                           List<Map> datFileData = null;
                                           //----------------------------------------


                                           //            upload element on oracle fusion HCM
                                           try {
                                           datFileData =
                                                       report.getOvertimeDatFileData(personNumber);
                                            } catch (Exception e) {
                                            e.printStackTrace();
                                            }
                                            HashMap<String, String> params1 = new HashMap<String, String>();
                                            //            params.put("Date", creationDate.toString());
                                            if (datFileData.size() > 0) {
                                                params1.put("AssignmentNumber",
                                                datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                                params1.put("Amount",amount);
                                                params1.put("StartDate", formattedDate1);
                                                params1.put("Count",
                                                                   datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                                params1.put("InvoiceDate",creationDate);
                                                params1.put("Description",description);
                                                params1.put("Segment1","01");
                                                params1.put("Segment2",division);
                                                params1.put("Segment3",lob);
                                                params1.put("Segment4",costCenter);
                                                params1.put("Segment5",accountNumber);
                                                params1.put("Segment6","00");
                                                try {
                                                fusionFileLoader = new FusionDataLoader();
                                                System.err.println("Repayment Ticekt Fusion Data Loader Params is::"+params1);
                                                Map<String, String> map = fusionFileLoader.sendFusionRequest(params1, 21);
                                                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("RepaymentTicketsId").toString(), personNumber, "Approved", "Repayment Ticket Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                                System.out.println("TAT HDR Updated Value - Approved Repayment Ticket Earnings: "+value+" Repayment Ticekt Fusion Data Loaded Sucessfully");
                                                    
                                                //fusionFileLoader.sendFusionRequest(params1, 21);
                                                //System.err.println("Repayment Ticekt Fusion Data Loaded Sucessfully");
                                                } catch (Exception e) {
                                                e.printStackTrace();
                                                }
                                               
                                            }

                                            else {

                                            JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");

                                           }
                                          
                                               returnValue="success";
                                               invoiceNumber="";
                                           

           }// 2023 Element Entry Change
            

//            APConsumer newAPInvoice = new APConsumer();
//            newAPInvoice.createInvoice(param, lineList);
            if(returnValue.equalsIgnoreCase("success")){
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RepaymentTicketsId"),
                                                          "RepaymentTicket",
                                                          previousStepId,
                                                          new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "Y");
                updateInsert=1;
                ADFUtils.setBoundAttributeValue("InvoiceNumber", invoiceNumber);
                ADFUtils.findOperation("Commit").execute();
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    sendEmailToPerson(personNumber, repaymentTicketsdRow);
                    sendEmailByEmail(fyiEmailAddress,repaymentTicketsdRow);
                }
            }


        }

        if(!nextStep.getAttribute("NextAssignee").equals("Finished") || (nextStep.getAttribute("NextAssignee").equals("Finished") && returnValue.equalsIgnoreCase("success"))){
            ADFUtils.findOperation("Commit").execute();
        }
        else{
                ADFUtils.findOperation("Rollback").execute();
                JSFUtils.addFacesInformationMessage("Something went wrong! please contact HR Administrator!");
                 return null;
        }
        if(updateInsert==0)
        {
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RepaymentTicketsId"),
                                                  "RepaymentTicket",
                                                  previousStepId,
                                                  new Long(ADFUtils.getBoundAttributeValue("StepId").toString()),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                  "APPROVE_ACT", "N");
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                if("POSITION".equals(stepType)){
                    System.err.println("Email to mail-id::"+assigneeEmail);
                    sendEmailByEmail(assigneeEmail, repaymentTicketsdRow);    
                }else{
                    System.err.println("Email to user-id::"+assigneeNo);
                    sendEmail(assigneeNo, repaymentTicketsdRow);    
                }   
            }
            
        }
       
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";

    }


    public String rejectRequest() {
        // Add event code here...
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String RepaymentTicketsName="RepaymentTickets" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("RepaymentTicketsName", RepaymentTicketsName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
               Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        RepaymentTicketsViewRowImpl repaymentTicketRow =
            (RepaymentTicketsViewRowImpl)ADFUtils.findIterator("RepaymentTicketsView1Iterator").getCurrentRow();
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
         sendEmail(personNumber, repaymentTicketRow);
        }
        ADFUtils.findOperation("Commit").execute();
        String value =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RepaymentTicketsId"),
                                                      "RepaymentTicket",
                                                 (Number)nextStep.getAttribute("StepId"),
                                                  new Long(0),
                                                  null,
                                                  "REJECT_ACT", "");
        JSFUtils.addFacesInformationMessage("Repayment Tickets Request has been Rejected");

        return "back";
    }

    public void calcAmount(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        if (valueChangeEvent.getNewValue() != null) {
            Number compensationPrice = (Number)valueChangeEvent.getNewValue();
            String endBalance =
                ADFUtils.getBoundAttributeValue("EndBalance").toString();
            Number numberEndBalance = null;

            try {
                numberEndBalance = new Number(endBalance);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Number amount = numberEndBalance.multiply(compensationPrice);
            amount = (Number)amount.round(2);
            ADFUtils.setBoundAttributeValue("Amount", amount);
        } else {
            ADFUtils.setBoundAttributeValue("Amount", null);
            JSFUtils.addFacesErrorMessage("You must enter value for compensation Price");

        }
    }
//    public static void main(String[] args) {
//        Number n=null;
//
//        try {
//            n = new Number(899.94);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        
//      n = (Number)n.round(2);
//        System.err.println("n+++++ "+n);
//    }
     public void sendEmailForFYI(String email, Row subject) {
//        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendRepaymentTicketsEmailForEmp("OFOQ.HR@TATWEER.SA", email,
                                      (RepaymentTicketsViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }
    public void sendRepaymentTicketsEmailForEmp(String from, String to,
                                              RepaymentTicketsViewRowImpl subject) {
            if (to == null) {
              //  to = "heleraki@gmail.com";
              JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

            }

            String into = "<p align=\"center\" style=\"text-align:center\">\n" +
                "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
                "<br/>" +
                "Kindly be informed that the following Repayment Tickets request has been Approved" +
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

            String repaymentTicketsDetails =
            " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Repayment Tickets Details</h2></td></tr>"  +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Amount\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + subject.getAmount() + "</td>\n" +
                "    </tr>\n" +
                "  </table>";

            String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryRepaymentIterator");
            String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
            String signaturePart =
                "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
                "<b/>" + "<br/>";
            String emailcontent =
                into + personalInformation + verticalSpace + repaymentTicketsDetails +verticalSpace+ApprovalPart1+
                thankYouPart + signaturePart + "</p>";

            OperationBinding sendMail =
                ADFUtils.findOperation("callSendEmailStoredPL");
            sendMail.getParamsMap().put("sender", from);
            sendMail.getParamsMap().put("receiver",
                                        to );
            //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
            sendMail.getParamsMap().put("subject",
                                        "Repayment Tickets Request " + subject.getRequestStatus());
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

    public String onClickEdit() {
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("RepaymentTicketsView1Iterator").getViewObject();
        Row r = vo.getCurrentRow();
        if(r.getAttribute("RequestStatus") != null && 
                ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")))){
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }else{
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                 personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            String RepaymentTicketsName="RepaymentTickets" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("RepaymentTicketsName", RepaymentTicketsName);//2023-PSC change
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
        }
        return "edit";
    }
    
    
    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("RepaymentTicketsView1Iterator").getViewObject();
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
            sendRepaymentTicketsEmail("OFOQ.HR@TATWEER.SA", email,
                                     (RepaymentTicketsViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){
            
            ViewObject reqVo = ADFUtils.findIterator("RepaymentTicketsView1Iterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String RepaymentTicketsName="RepaymentTickets" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("RepaymentTicketsName", RepaymentTicketsName);//2023-PSC change
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
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RepaymentTicketsId"), 
                                                                             "RepaymentTicket", (Number)nextStep.getAttribute("NextStepId"), 
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
        
        ViewObject vo = ADFUtils.findIterator("RepaymentTicketsView1Iterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("RepaymentTicketsViewCriteria"));
            vo.executeQuery();
        }
    }
}
