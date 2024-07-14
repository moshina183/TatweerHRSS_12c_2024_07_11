package com.sbm.selfServices.view.mb;

import java.util.Map;
import java.util.Date;
import java.util.List;
import oracle.jbo.Row;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;
import oracle.jbo.ViewObject;
import oracle.jbo.ViewCriteria;
import oracle.jbo.domain.Number;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBElement;
import oracle.jbo.ViewCriteriaRow;
import oracle.adf.share.ADFContext;
import javax.faces.event.ActionEvent;
import oracle.binding.OperationBinding;
import javax.faces.context.FacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;
import com.view.beans.filmStripApp.FilmStripBean;
import com.mivors.model.bi.integration.BiReportAccess;
import com.sbm.selfServices.view.utils.UserServiceUtil;
import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.MobileAllowanceRequestUPViewRowImpl;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

public class MobileFrag 
{
    private FusionDataLoader fusionFileLoader;
    private BiReportAccess report = new BiReportAccess();

    public MobileFrag() 
    {
        
    }

    public String save_action() 
    {
        Object employeeId = JSFUtils.resolveExpression("#{PersonInfo.employeeId}");
        
        if (employeeId == null) 
        {
            JSFUtils.addFacesErrorMessage("You donot have Person ID, So you can not save the request");
            return null;
        }
        
        
//        if( ADFUtils.getBoundAttributeValue("NumberOfMonths")==null)
//        {
//                JSFUtils.addFacesErrorMessage("You must select number of months");
//                return null;
//                
//            }
//        
//        if( ADFUtils.getBoundAttributeValue("RequestReason")==null)
//        {
//               JSFUtils.addFacesErrorMessage("You must add request reason");
//               return null;
//           
//           }01, 

		/*
         * Mobile report - Getting mobile allowance from report
         */
		String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String mobAllowance = "0";
        List<Map> allowance = null;
        try {
            allowance = report.getMobileAllowanceAmount(personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (allowance.get(0).get("AMOUNT") != null) {
            mobAllowance = allowance.get(0).get("AMOUNT").toString();
        }
        System.out.println("SAVE: mobAllowance is >>> " + mobAllowance);
        ADFUtils.setBoundAttributeValue("Amount", "400");
       
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int remainingMonths=12-month; 
        Number selectedMonths = (Number)ADFUtils.getBoundAttributeValue("NumberOfMonths");
        
        if(cal.get(Calendar.YEAR) == 2024) 
        {
            if(selectedMonths.compareTo(7) >= 0) 
            {
                JSFUtils.addFacesErrorMessage("Selection of number of months should be from current month of submission onwards.");
                return null;
            }
        }
        else 
        {
            if((selectedMonths.compareTo(remainingMonths))>0)
            {
                JSFUtils.addFacesErrorMessage("Selection of number of months should be from current month of submission onwards.");
                return null;
            }
        }
        
        OperationBinding oper = ADFUtils.findOperation("getMobileRequestsThisMonth");
        Map paramMap = oper.getParamsMap();
        paramMap.put("PersonNumber", ADFUtils.getBoundAttributeValue("PersoneNumber"));
        oracle.jbo.domain.Number totaRequestsThisMonth = (oracle.jbo.domain.Number)oper.execute();
        
        if (totaRequestsThisMonth.compareTo(0) > 0) 
        {
            JSFUtils.addFacesErrorMessage("You can't submit more than one request per month");
            return null;
        }
        
        ADFUtils.setBoundAttributeValue("ActionTaken", "SAVED");
        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
       // ADFUtils.setBoundAttributeValue("Amount", "400");
       /*
        * Mobile report - Getting mobile allowance from report
        */
        report = new BiReportAccess();
        List<Map> costCenterData = null;
        List<Map> hireTypeData = null;
         
        try 
        {
            costCenterData = report.getHCMSegments(ADFUtils.getBoundAttributeValue("PersoneNumber").toString());
            hireTypeData = report.getPositionWorkerData(ADFUtils.getBoundAttributeValue("PersoneNumber").toString());
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        String division = costCenterData.get(0).get("SEGMENT2")!=null? costCenterData.get(0).get("SEGMENT2").toString():"";
        String departmentNumber = costCenterData.get(0).get("SEGMENT3")!=null? costCenterData.get(0).get("SEGMENT3").toString():"";
        String costCenterNumber = costCenterData.get(0).get("SEGMENT4")!=null? costCenterData.get(0).get("SEGMENT4").toString():"";
        String hireType = hireTypeData.get(0).get("WORKER_TYPE")!=null? hireTypeData.get(0).get("WORKER_TYPE").toString():"";
        
        ADFUtils.setBoundAttributeValue("Division", division);
        ADFUtils.setBoundAttributeValue("DepartmentNumber", departmentNumber);
        ADFUtils.setBoundAttributeValue("CostCenterNumber", costCenterNumber);
        ADFUtils.setBoundAttributeValue("HireType", hireType);
        OperationBinding op = ADFUtils.findOperation("callGetDepartment");  
        String costCenterName = null;
        op.getParamsMap().put("cost_center_number", costCenterNumber);       
        costCenterName = (String)op.execute();
        ADFUtils.setBoundAttributeValue("CostCenterName", costCenterName);
        
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been saved");
        
        return "back";
    }

    public String submit_action() 
    {
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int remainingMonths=12-month; 
        System.err.println("Remaining Months :"+remainingMonths);
        Number selectedMonths = (Number)ADFUtils.getBoundAttributeValue("NumberOfMonths");
        
        if(cal.get(Calendar.YEAR) == 2024) 
        {
            if(selectedMonths.compareTo(7) >= 0) 
            {
                JSFUtils.addFacesErrorMessage("Selection of number of months should be from current month of submission onwards.");
                return null;
            }
        }
        else 
        {
            if((selectedMonths.compareTo(remainingMonths))>0)
            {
                JSFUtils.addFacesErrorMessage("Selection of number of months should be from current month of submission onwards.");
                return null;
            }
        }
        
        OperationBinding oper = ADFUtils.findOperation("getMobileRequestsThisMonth");
        Map paramMap = oper.getParamsMap();
        System.err.println("Person Id Is >>>>  " + ADFUtils.getBoundAttributeValue("PersoneNumber"));
        paramMap.put("PersonNumber", ADFUtils.getBoundAttributeValue("PersoneNumber"));
        oracle.jbo.domain.Number totaRequestsThisMonth = (oracle.jbo.domain.Number)oper.execute();

        if (totaRequestsThisMonth.compareTo(0) > 0) 
        {
            JSFUtils.addFacesErrorMessage("You can't submit more than one request per month");
            return null;
        }
        
        report = new BiReportAccess();
        List<Map> reportvalues = null;
        
        try 
        {
            reportvalues = report.getmobileDatFileData(ADFUtils.getBoundAttributeValue("PersoneNumber").toString());   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        if(reportvalues.get(0).get("ASSIGNMENT_NUMBER")==null) 
        {
            JSFUtils.addFacesInformationMessage("You Cannot Submit the request Since there is a  No Assignment number assigned to you");
            return null;
        }
        
        if(reportvalues.get(0).get("EFF_START_DATE")!=null && reportvalues.get(0).get("EFF_END_DATE")!=null) 
        {
            String eff_start_date = reportvalues.get(0).get("EFF_START_DATE")!=null?reportvalues.get(0).get("EFF_START_DATE").toString().substring(0, 10):"";
            String eff_to_date = reportvalues.get(0).get("EFF_END_DATE")!=null?reportvalues.get(0).get("EFF_END_DATE").toString().substring(0, 10):"";
            String returnvalue= validateMobileAllowance(eff_start_date,eff_to_date);
            
            if(returnvalue!=null&&returnvalue.equalsIgnoreCase("failed")) 
            {
                JSFUtils.addFacesInformationMessage("You Cannot Submit the request Since there is a  Request in the Current Period");
                return null;
            }
        } 
        
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String MobileTypeName="Mobile" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));

//        if( ADFUtils.getBoundAttributeValue("NumberOfMonths")==null)
//        {
//                JSFUtils.addFacesErrorMessage("You must select number of months");
//                return null;
//                
//            }
//        
//        if( ADFUtils.getBoundAttributeValue("RequestReason")==null)
//        {
//                JSFUtils.addFacesErrorMessage("You must add request reason");
//                return null;
//            
//            }
        
        
        
        //For all step type
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
        String personNumber = ADFUtils.getBoundAttributeValue("PersoneNumber").toString();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;

        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();

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
        //ADFUtils.setBoundAttributeValue("Amount", "400");
        /*
         * Mobile report - Getting mobile allowance from report
         */
        String mobAllowance = "0";
        List<Map> allowance = null;
        try {
            allowance = report.getMobileAllowanceAmount(personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (allowance.get(0).get("AMOUNT") != null) {
            mobAllowance = allowance.get(0).get("AMOUNT").toString();
        }
        System.out.println("mobAllowance is >>> " + mobAllowance);
        ADFUtils.setBoundAttributeValue("Amount", mobAllowance);
        
        report = new BiReportAccess();
        List<Map> costCenterData = null;
        List<Map> hireTypeData = null;
                 
        try 
        {
            costCenterData = report.getHCMSegments(personNumber);
            hireTypeData = report.getPositionWorkerData(personNumber);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        
        String division = costCenterData.get(0).get("SEGMENT2")!=null? costCenterData.get(0).get("SEGMENT2").toString():"";
        String departmentNumber = costCenterData.get(0).get("SEGMENT3")!=null? costCenterData.get(0).get("SEGMENT3").toString():"";
        String costCenterNumber = costCenterData.get(0).get("SEGMENT4")!=null? costCenterData.get(0).get("SEGMENT4").toString():"";
        String hireType = hireTypeData.get(0).get("WORKER_TYPE")!=null? hireTypeData.get(0).get("WORKER_TYPE").toString():"";
        
        ADFUtils.setBoundAttributeValue("Division", division);
        ADFUtils.setBoundAttributeValue("DepartmentNumber", departmentNumber);
        ADFUtils.setBoundAttributeValue("CostCenterNumber", costCenterNumber);

        ADFUtils.setBoundAttributeValue("HireType", hireType);
        OperationBinding op = ADFUtils.findOperation("callGetDepartment");  
        String costCenterName = null;
        op.getParamsMap().put("cost_center_number", costCenterNumber);       
        costCenterName = (String)op.execute();
        ADFUtils.setBoundAttributeValue("CostCenterName", costCenterName);
        
      
        // Based step type
        // Usual Submit processs
        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        // If step type is POSITION
        if("POSITION".equals(stepType))
        {
            System.err.println("Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
            
            report = new BiReportAccess();
            List<Map> personData = null;
            
            try 
            {
                personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
            if (personData.get(0).get("DISPLAY_NAME") != null) 
            {
                assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
            }
            
            if (personData.get(0).get("PERSON_NUMBER") != null) 
            {
                assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
            }
            
            if (personData.get(0).get("EMAIL_ADDRESS") != null) 
            {
                assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
            }
        }
        
        // If step type is DEPARTMENT MANAGER
        if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType))
        {
            System.err.println("Submitted : DEPT_MANAGER :"+department);
            
            report = new BiReportAccess();
            
            try 
            {
                List<Map> managerOfDeptList = report.getManagerOfDepartmentData(department);
                
                if (managerOfDeptList.size() > 0) 
                {
                    if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null)
                    {
                        assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                    }
                    
                    if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null)
                    {
                        assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                    }
                    
                    if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) 
                    {
                        assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                    } 
                } 
                else 
                {
                    JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                    return null;
                }
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType))
        {
            System.err.println("Submitted : LINE_MANAGER ");
            
            String managerNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");
            Long lineManagerID = (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
            String stringLineManagerID = lineManagerID.toString();
            
            if (stringLineManagerID == null) 
            {
                JSFUtils.addFacesErrorMessage("You don't have Line Manager, So you can not submit the request");
                return null;
            }  
                
            UserDetails byManagerId = userService.getUserDetailsByPersonId(lineManagerID);
            String managerName = byManagerId.getUserPersonDetails().get(0).getDisplayName().getValue(); 
            String managerEmail = byManagerId.getUserPersonDetails().get(0).getEmailAddress().getValue();
             
            assigneeName = managerName;
            assigneeNo = managerNumber;
            assigneeEmail = managerEmail;
        }
        
        // If step type is USER
        if("USER".equals(stepType))
        {
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
        
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
        
        ADFUtils.findOperation("Commit").execute();
        Long stepid = (Long)ADFUtils.getBoundAttributeValue("StepId");
        String act = "SUMBIT_ACT";
        
        if(currStatus != null && "EDIT".equals(currStatus))
        {
            act = "UPDATE_ACT";
        }

       String value = ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"), 
                                                           "MobileAllowance", (Number)nextStep.getAttribute("NextStepId"), stepid, 
                                                           (String)ADFUtils.getBoundAttributeValue("AssigneeName"), act, "");
       
       System.err.println("values======"+value);
       
        Row mobileAllowanceRow = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getCurrentRow();
        JSFUtils.addFacesInformationMessage("Request has been submitted");
        
        if("POSITION".equals(stepType))
        {
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
            {
                if (assigneeEmail == null) 
                {
                    JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver");
                    return null;
                }
                else
                {
                  sendEmailByEmail(assigneeEmail, mobileAllowanceRow);  
                } 
            }
        }
        
        if(! "POSITION".equals(stepType))
        {
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
            {
                if (assigneeNo == null) 
                {
                    JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver..");
                    return null;
                }
                else
                {
                  sendEmail(assigneeNo, mobileAllowanceRow);  
                } 
            }
        }
        return "back";
    }


    public void sendEmailByEmail(String personEmail, Row subject) 
    {
        sendMobileAllowanceEmail("OFOQ.HR@TATWEER.SA", personEmail, (MobileAllowanceRequestUPViewRowImpl)subject , "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");
    }

    public void sendMobileAllowanceEmail(String from, String to, MobileAllowanceRequestUPViewRowImpl subject, String personFYI) 
    {
        String status = subject.getRequestStatus();
        String edited = subject.getEditRequest();
        String requesterName = subject.getPersoneName();
        String assigneeName = "Sir";
        
        if(personFYI!= null && "Y".equals(personFYI))
        {
            assigneeName = requesterName;
        }
        else
        {
            assigneeName = subject.getAssigneeName()!=null ? subject.getAssigneeName() : "Sir";
        }
        
        String subj = "";
        String hdrMsg = "";
        
        if("Withdrawn".equals(status))
        {
            subj = "Mobile Allowance Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Mobile Allowance Request, and below the details";
            
        }
        else if("Waiting Withdraw Approval".equals(status))
        {
            subj = "Mobile Allowance Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw Mobile Allowance Request, and below the details";
            
        }
        else if("Withdrawn Rejected".equals(status))
        {
            subj = "Mobile Allowance Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of Mobile Allowance request as below";
            
        }
        else if(edited!=null && edited.equals("Y") && "PENDING".equals(status))
        {
            subj = "Mobile Allowance Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Mobile Allowance Request, and below the details";
            
        }
        else
        {
            subj = "Mobile Allowance Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Mobile Allowance request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI))
        {
            if("Withdrawn".equals(status))
            {
                subj = "Mobile Allowance Request has been withdraw";
                hdrMsg= "Kindly be informed you that the following Mobile Allowance request has been withdraw";
            }
            
            if("APPROVED".equals(status))
            {
                subj = "Mobile Allowance Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Mobile Allowance request has been Approved";
            }
        }

        System.err.println("Mail subject is - "+subj);
        System.err.println("Mail Header is - "+hdrMsg);
        
        if (to == null) 
        {
          JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");
        }

        String into = "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" + hdrMsg + 
//            "Kindly find below the details of Mobile Allowance request as below" +
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
            "      <td width=\"50%\">" + PersoneName + "</td>\n" +
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
            "      <td width=\"50%\">" + PersonJob + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Grade\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +PersonGrade +
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

        String MobileAllowanceDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Mobile Allowance Details</h2></td></tr>"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Request Reason\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getRequestReason() +
            "</td>\n" +
            "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp; No Of Months\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" + subject.getNumberOfMonths().toString() +
        "</td>\n" +
        "    </tr>\n"+
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryMobileIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo-test.fs.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        
        String emailcontent = into + personalInformation + verticalSpace + MobileAllowanceDetails +verticalSpace+
            ApprovalPart1+thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail = ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to );
        sendMail.getParamsMap().put("subject", subj); 
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }

    public void sendMobileAllowanceEmailToPayrollMgr(String from, String to, MobileAllowanceRequestUPViewRowImpl subject, String payrollMgrName) 
    {
        if (to == null) 
        {
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");
        }

        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear " + payrollMgrName + "," + "\n" +
            "<br/>" +
            "Kindly find below the details of Mobile Allowance request as below" +
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

        String MobileAllowanceDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Mobile Allowance Details</h2></td></tr>"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Request Reason\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getRequestReason() +
            "</td>\n" +
            "    </tr>\n" +
        "    <tr>\n" +
              "      <th>\n" +
              "        &nbsp; No Of Months\n" +
              "      </th>\n" +
              "      <td width=\"50%\">" + subject.getNumberOfMonths().toString() +
              "</td>\n" +
              "    </tr>\n"+
            "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryMobileIterator");
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        
        String emailcontent = into + personalInformation + verticalSpace + MobileAllowanceDetails +
                    ApprovalPart1+thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail = ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to );
        sendMail.getParamsMap().put("subject", "Mobile Allowance Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }

    public String approve_action() 
    {
        String personMail = null;
        List<Map> personData = null;
        int managerOFDept=0;
        int approvalDone = 0;
        
        Row mobileAllowanceRow = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String MobileTypeName="Mobile" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("Assignee", nextStep.getAttribute("NextAssignee"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
        
        if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType))
        {
            approvalDone = 2;
            String department = (String)ADFUtils.getBoundAttributeValue("PersonDepartment");
            String managerOfDeptNum = null;
            String managerOfDeptName = null;
            String managerOfDeptEmail = null;
            
            if (department != null) 
            {
                report = new BiReportAccess();

                try 
                {
                    List<Map> managerOfDeptList = report.getManagerOfDepartmentData(department);
                    
                    if (managerOfDeptList.size() > 0) 
                    {
                        managerOfDeptNum = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                        managerOfDeptName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                        
                        if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) 
                        {
                            managerOfDeptEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                        }
                        
                    } 
                    else 
                    {
                        JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                        return "back";
                    }

                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }

                if (managerOfDeptNum != null)
                {
                    ADFUtils.setBoundAttributeValue("Assignee", managerOfDeptNum);
                }
                
                if (managerOfDeptName != null)
                {
                    ADFUtils.setBoundAttributeValue("AssigneeName", managerOfDeptName);
                }
            }
            
            String value = ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                                 "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                 (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
            {
                sendEmailByEmail(managerOfDeptEmail, mobileAllowanceRow);
            }
        }
        
        //Dynamic Approval
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        if("POSITION".equals(stepType))
        {
            approvalDone = 1;
            System.err.println("Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
            
            report = new BiReportAccess(); 
            List<Map> personData2 = null;
            
            try 
            {
                personData2 = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
            if (personData2.get(0).get("DISPLAY_NAME") != null) 
            {
                assigneeName = personData2.get(0).get("DISPLAY_NAME").toString();
            }
            
            if (personData2.get(0).get("PERSON_NUMBER") != null) 
            {
                assigneeNo = personData2.get(0).get("PERSON_NUMBER").toString();
            }
            
            if (personData2.get(0).get("EMAIL_ADDRESS") != null) 
            {
                assigneeEmail = personData2.get(0).get("EMAIL_ADDRESS").toString();
            }
        }
        
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType))
        {
            approvalDone = 1;
            System.err.println("Approved, Assigning to : : LINE_MANAGER ");      
            UserServiceUtil userService = new UserServiceUtil();
            UserDetails userDetails = null;
            String personNumberCheck = ADFUtils.getBoundAttributeValue("PersoneNumber").toString();
            userDetails = userService.getUserDetailsByPersonNumber(personNumberCheck);
            List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
            Long lineManagerID = relationshipDetails.get(0).getManagerId();
            UserDetails managerDetails = userService.getUserDetailsByPersonId(lineManagerID);
            String managerNumber = managerDetails.getPersonNumber();
            JAXBElement<String> aXBElement = null;
            
            if (managerDetails.getUserPersonDetails().get(0).getDisplayName() != null) 
            {
                aXBElement = managerDetails.getUserPersonDetails().get(0).getDisplayName();
            }
                
            assigneeName = aXBElement.getValue();
            assigneeNo = managerNumber;
        } 
        
        // If step type is USER
        if("USER".equals(stepType))
        {
            approvalDone = 1;
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
        
        if(approvalDone == 1)
        {
            ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
            ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
            ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));  
            
            if ( ! nextStep.getAttribute("NextAssignee").equals("Finished"))
            {
                String value = ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                                     "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                     (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
                
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
                {
                    if("POSITION".equals(stepType))
                    {
                        sendEmailByEmail(assigneeEmail, mobileAllowanceRow);    
                    }
                    else
                    {
                        sendEmail(assigneeNo, mobileAllowanceRow);    
                    }   
                }
            } 
        }
        
        //----------------------------------------------
        
//        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
//           
//            BiReportAccess report = new BiReportAccess();
//          
//
//            try {
//                personData = report.getPersonByPostionReport("HR and Admin Director");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.err.println("Display Name==>"+ personData.get(0).get("DISPLAY_NAME"));
//            if (personData.size() > 0) {
//                if (personData.get(0).get("PERSON_NUMBER") != null) {
//                    ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
//                    System.out.println("HR and Admin Director Number is " +
//                                       personData.get(0).get("PERSON_NUMBER"));
//                }
//                if (personData.get(0).get("DISPLAY_NAME") != null) {
//                    System.err.println("Display Name==>"+ personData.get(0).get("DISPLAY_NAME"));
//                    System.out.println("HR and Admin Director Name is " +
//                                       personData.get(0).get("DISPLAY_NAME"));
//
//                    ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                    personData.get(0).get("DISPLAY_NAME"));
//                }
//                String value =
//                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
//                                                          "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//                ADFUtils.findOperation("Commit").execute();
//                managerOFDept=1;
//                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                sendEmailByEmail(personData.get(0).get("PERSON_NUMBER").toString(), mobileAllowanceRow);
//                }
//                else {
//                    JSFUtils.addFacesErrorMessage("You can't approve request as HR and Admin Director Name is empty");
//                    return null;
//                }   
//               
//            }
//        }
//
//
//        if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")) {
//
//            BiReportAccess report = new BiReportAccess();
//           
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (personData.size() > 0) {
//
//                if (personData.get(0).get("PERSON_NUMBER") != null) {
//                    ADFUtils.setBoundAttributeValue("Assignee",
//                                                    personData.get(0).get("PERSON_NUMBER").toString());
//                    System.out.println("Executive Director, Shared Services Sector (Acting) Number is " +
//                                       personData.get(0).get("PERSON_NUMBER"));
//                }
//                if (personData.get(0).get("DISPLAY_NAME") != null) {
//                    System.out.println("Executive Director, Shared Services Sector (Acting) Name is " +
//                                       personData.get(0).get("DISPLAY_NAME"));
//
//                    ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                    personData.get(0).get("DISPLAY_NAME"));
//                }
//                
//                else {
//                    JSFUtils.addFacesErrorMessage("You can't approve request as Executive Director, Shared Services Sector (Acting) Name is empty");
//                    return null;
//                }
//
//                String value =
//                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
//                                                          "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//                ADFUtils.findOperation("Commit").execute();
//                managerOFDept=1;
//                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                    personMail =
//                            personData.get(0).get("EMAIL_ADDRESS").toString();
//                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                    sendEmailByEmail(personMail, mobileAllowanceRow);
//                    }
//                } else {
//                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");
//
//                }
//            }
//
//
//        }
//        if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")) {
//
//            BiReportAccess report = new BiReportAccess();
//          
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("Financial Planning & Analysis Manager");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (personData.size() > 0) {
//
//                if (personData.get(0).get("PERSON_NUMBER") != null) {
//                    ADFUtils.setBoundAttributeValue("Assignee",
//                                                    personData.get(0).get("PERSON_NUMBER").toString());
//                    System.out.println("Financial Planning & Analysis Manager " +
//                                       personData.get(0).get("PERSON_NUMBER"));
//                }
//                if (personData.get(0).get("DISPLAY_NAME") != null) {
//                    System.out.println("Financial Planning & Analysis Manager " +
//                                       personData.get(0).get("DISPLAY_NAME"));
//
//                    ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                    personData.get(0).get("DISPLAY_NAME"));
//                }
//
//                else {
//                    JSFUtils.addFacesErrorMessage("You can't approve request as Financial Planning & Analysis Manager Name is empty");
//                    return null;
//                }
//                String value =
//                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
//                                                          "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//                ADFUtils.findOperation("Commit").execute();
//                managerOFDept=1;
//                ADFUtils.findOperation("Commit").execute();
//                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                    personMail =
//                            personData.get(0).get("EMAIL_ADDRESS").toString();
//                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                    sendEmailByEmail(personMail, mobileAllowanceRow);
//                    
//                    }
//                } else {
//                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Financial Planning & Analysis Manager dosn't has email");
//
//                }
//            }
//
//
//        }
//
//
//        if (nextStep.getAttribute("NextAssignee").equals("CEO")) {
//
//            BiReportAccess report = new BiReportAccess();
//            
//
//            try {
//                personData =
//                        report.getPersonByPostionReport("Chief Executive Officer");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (personData.size() > 0) {
//
//                if (personData.get(0).get("PERSON_NUMBER") != null) {
//                    System.out.println("Chief Executive Officer Number is " +
//                                       personData.get(0).get("PERSON_NUMBER"));
//                }
//                if (personData.get(0).get("DISPLAY_NAME") != null) {
//                    System.out.println("Chief Executive Officer Name is " +
//                                       personData.get(0).get("DISPLAY_NAME"));
//
//                    ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                    personData.get(0).get("DISPLAY_NAME"));
//                }
//
//                else {
//                    JSFUtils.addFacesErrorMessage("You can't approve request as Chief Executive Officer Name is empty");
//                    return null;
//                }
//
//                String value =
//                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
//                                                          "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                        (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//                ADFUtils.findOperation("Commit").execute();
//                managerOFDept=1;
//                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                    personMail =
//                            personData.get(0).get("EMAIL_ADDRESS").toString();
//                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//                    sendEmailByEmail(personMail, mobileAllowanceRow);
//                    }
//                } else {
//                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Chief Executive Officer dosn't has email");
//
//                }
//            }
//
//
//        }
        
        String personNumber=null;
        
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) 
        {
            UserServiceUtil userService = new UserServiceUtil();
            personNumber = JSFUtils.resolveExpression("#{bindings.PersoneNumber.inputValue}").toString();
            UserDetails byPersonNumber = userService.getUserDetailsByPersonNumber(personNumber);
            ADFUtils.setBoundAttributeValue("Assignee", byPersonNumber.getPersonNumber());
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
            String dateString = ADFUtils.getBoundAttributeValue("CreationDate").toString();
            String formattedDate = dateString.replace('-', '/');
            //-----------------------------------------

            SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date myDate = null;
            
            try 
            {
                myDate = oldDateFormat.parse(formattedDate);
            } 
            catch (ParseException e) 
            {
                e.printStackTrace();
            }
            
            oldDateFormat.applyPattern("yyyy/MM/dd");
            formattedDate = oldDateFormat.format(myDate);
            System.out.println("myDateString  >>> " + formattedDate);
            
            /*
             * Added by MSF on 2024.06.11
             * If request is approved after 13 then we will set effective start date as first day of the next month
             */
            Calendar cal = Calendar.getInstance();
            
            if (cal.get(Calendar.DAY_OF_MONTH) > 13)
            {
                cal.set(cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1), 1);     
                Date date = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");         
                formattedDate = sdf.format(date);              
            }
            
            report = new BiReportAccess();
            List<Map> datFileData = null;

            try 
            {
                datFileData = report.getmobileDatFileData(personNumber);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
            String assignmentNumber = datFileData.get(0).get("ASSIGNMENT_NUMBER")!=null? datFileData.get(0).get("ASSIGNMENT_NUMBER").toString():"";
            String MultipleEntryCount = datFileData.get(0).get("MULIPLE_ENTRY")!=null? datFileData.get(0).get("MULIPLE_ENTRY").toString():"";   
            String returndate=null; 
            
            //returndate = changeEffectiveEndDate();
            returndate = getEffectiveEndDate(formattedDate);
            
            ADFUtils.setBoundAttributeValue("EffectiveStartDate", convertStringToDate(formattedDate));
            ADFUtils.setBoundAttributeValue("EffectiveEndDate", convertStringToDate(returndate));
            
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("Date", formattedDate);
            params.put("AssignmentNumber", assignmentNumber);
            params.put("StartDate", formattedDate);
            params.put("EndDate", returndate);
            params.put("MultipleEntry", MultipleEntryCount);
            
            /*Added by MSF on 2024.06.11
            Now we are going to hardcode amount as 400 for every request, later we will implement the table structure for getting the amount with reference of their grade
            */
            //params.put("Amount", "400");
            /*
             * Mobile reports - amount got from binding
             */
            params.put("Amount", ADFUtils.getBoundAttributeValue("Amount").toString());
            
            try 
            {
                FusionDataLoader fusionFileLoader = new FusionDataLoader();
                
                //EES - code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 3);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("RequestId").toString(), personNumber, "Approved", "Mobile Allowance", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("Mobile allowance TAT HDR Updated Value: "+value);
                
                //fusionFileLoader.sendFusionRequest(params, 3);
                
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
            String value = ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                                 "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                 (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            
            ADFUtils.findOperation("Commit").execute();
            managerOFDept=1;
        }
      
        String finalapproval = null;
       
        if(nextStep.getAttribute("NextAssignee").equals("Finished"))
        {
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
            {
                sendEmailToPerson(personNumber, mobileAllowanceRow);
                sendEmailByEmail(fyiEmailAddress, mobileAllowanceRow);
            }
        }
        
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been approved");
        
        return "back";
    }


    public void sendEmail(String personNumber, Row subject) 
    {
        String email = getEmail(personNumber);

        if (null == email) 
        {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } 
        else 
        {
            sendMobileAllowanceEmail("OFOQ.HR@TATWEER.SA", email, (MobileAllowanceRequestUPViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }


    public String getEmail(String personNumber) 
    {
        JAXBElement<String> aXBElement = null;
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        
        if (userDetails.getUserPersonDetails().get(0).getEmailAddress() != null) 
        {
            aXBElement = userDetails.getUserPersonDetails().get(0).getEmailAddress();
        } 
        else 
        {
            JSFUtils.addFacesErrorMessage("Person Number (" + personNumber + ") has no mail");
            return null;
        }

        return aXBElement.getValue();
    }


    public void sendEmailToPayrollMgrFinalApproval(Row subject) 
    {
        report = new BiReportAccess();
        List<Map> personData = null;
        
        try 
        {
            personData = report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        System.err.println("HR Payroll and benefits Supervisor Name is " + personData.get(0).get("DISPLAY_NAME"));
        System.err.println("PHR Payroll and benefits Supervisor Email " + personData.get(0).get("EMAIL_ADDRESS"));

        if (null == (personData.get(0).get("EMAIL_ADDRESS"))) 
        {
            JSFUtils.addFacesInformationMessage("Mail has not been sent to HR Payroll and benefits Supervisor as he has no email");
        } 
        else 
        {
            sendMobileAllowanceEmailToPayrollMgr("OFOQ.HR@TATWEER.SA", personData.get(0).get("EMAIL_ADDRESS").toString(),
                                                 (MobileAllowanceRequestUPViewRowImpl)subject, personData.get(0).get("DISPLAY_NAME").toString());
            JSFUtils.addFacesInformationMessage("FYI Mail has been sent to HR Payroll and benefits Supervisor");
        }
    }

    public String reject_action() 
    {
        String personNumber = JSFUtils.resolveExpression("#{bindings.PersoneNumber.inputValue}").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String MobileTypeName="Mobile" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        Row mobileAllowanceRow = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getCurrentRow();

        UserServiceUtil userService = new UserServiceUtil();
        UserDetails byPersonNumber = userService.getUserDetailsByPersonNumber(personNumber);
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        ADFUtils.findOperation("Commit").execute();
        String value = ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                             "MobileAllowance", (Number)nextStep.getAttribute("StepId"), new Long(0), null, "REJECT_ACT", "");
        
        JSFUtils.addFacesInformationMessage("Request has been Rejected");
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
        {
            sendEmail(personNumber, mobileAllowanceRow);
        }
        
        return "back";
    }
    
    public String checkSession()
    {
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        
        if(personNumber==null || assignee==null)
        {
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        
        return "success";
    }
    
    public void sendEmailForMobileAllowanceEmployee(String email, Row subject) 
    {
        if (null == email) 
        {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } 
        else 
        {
            sendMobileAllowanceEmailForEmployee("OFOQ.HR@TATWEER.SA", email, (MobileAllowanceRequestUPViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }
    
    public void sendMobileAllowanceEmailForEmployee(String from, String to, MobileAllowanceRequestUPViewRowImpl subject) 
    {
        if (to == null) 
        {
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");
        }

        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Mobile Allowance request is Approved" +
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

        String MobileAllowanceDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
         "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Mobile Allowance Details</h2></td></tr>"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Request Reason\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getRequestReason() +
            "</td>\n" +
            "    </tr>\n" +
        "    <tr>\n" +
              "      <th>\n" +
              "        &nbsp; No Of Months\n" +
              "      </th>\n" +
              "      <td width=\"50%\">" + subject.getNumberOfMonths().toString() +
              "</td>\n" +
              "    </tr>\n"+
            "  </table>";


        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo-test.fs.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        
        String emailcontent = into + personalInformation + verticalSpace + MobileAllowanceDetails + thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail = ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to );
        sendMail.getParamsMap().put("subject", "Mobile Allowance Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }

    public String validateMobileAllowance(String start_date,String end_date) 
    {
        String returnvalue = "";
        ViewObject vo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        oracle.jbo.domain.Date date = (oracle.jbo.domain.Date)vo.getCurrentRow().getAttribute("CreationDate");
        
        try 
        {
            oracle.jbo.domain.Date tooracledate1 = new oracle.jbo.domain.Date(start_date);
            oracle.jbo.domain.Date tooracledate2 = new oracle.jbo.domain.Date(end_date);  
            System.err.println(tooracledate1+""+tooracledate2);
            
            if(tooracledate1.compareTo(date) * date.compareTo(tooracledate2)>0) 
            {
                returnvalue=  "failed";
            }
            else 
            {
                long value=tooracledate1.compareTo(date) * date.compareTo(tooracledate2);
                returnvalue=  "success";
            }
                
        }
         catch (Exception e)
        {
            e.printStackTrace();
            returnvalue = "exception";
        }

        return returnvalue;
    }

    public String changeEffectiveEndDate() 
    {
        oracle.jbo.domain.Date jboDate = null;
        ViewObject vo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        Number countofentry = (Number)ADFUtils.getBoundAttributeValue("NumberOfMonths");
        oracle.jbo.domain.Date date = (oracle.jbo.domain.Date)vo.getCurrentRow().getAttribute("CreationDate");   
        date.addMonths(countofentry.intValue()-1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.getValue());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        java.util.Date utilDate = calendar.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        jboDate = new oracle.jbo.domain.Date(sqlDate);
        String Value=ApprovelLine.convertDatePattern(jboDate.toString(),"yyyy-MM-dd","yyyy/MM/dd");
        
        return Value;
    }
    
    public String getEffectiveEndDate(String startDate) 
    {
        ViewObject vo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        Number countofentry = (Number)ADFUtils.getBoundAttributeValue("NumberOfMonths"); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String endDate = null;
            
        try 
        {
            Date date = formatter.parse(startDate);
            System.out.println("Original date: " + formatter.format(date));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, countofentry.intValue()-1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date updatedDate = calendar.getTime();
            endDate = formatter.format(updatedDate);
        } 
        catch (ParseException e) 
        {
            e.printStackTrace();
        }
        
        return endDate;
    }

    public ArrayList<String> getPersonDetails(String personNumber) 
    {
        ArrayList<String> personList = new ArrayList<String>();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = userService.getUserDetailsByPersonNumber(personNumber);

        JAXBElement<String> nameElement = userDetails.getUserPersonDetails().get(0).getDisplayName();
        String name = nameElement.getValue();
        
        JAXBElement<String> emailElement = userDetails.getUserPersonDetails().get(0).getEmailAddress();
        String email = emailElement.getValue();
        
        //Get person name by get(0)
        if(name!=null)
        {
            personList.add(name);    
        }
        else
        {
            personList.add("");
        }
        
        //Get person email by get(1)
        if(email!=null)
        {
            personList.add(email);    
        }
        else
        {
            personList.add("");
        }
        
        return personList;
    }
    
    public String onClickEdit() 
    {
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        Row r = vo.getCurrentRow();
        
        if(r.getAttribute("RequestStatus") != null && ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")) || "Withdrawn Rejected".equals(r.getAttribute("RequestStatus"))))
        {
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }
        else
        {
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus"))))
            {
                personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            
            String MobileTypeName="Mobile" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);
        }
        
        return "edit";
    }


    public void withdrawRequest(DialogEvent dialogEvent) 
    {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok))
        {

            ViewObject reqVo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersoneNumber").toString();
            
            currRow.setAttribute("StepId", "1");
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String MobileTypeName="Mobile" +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String emailNotification = (String)nextStep.getAttribute("EmailNotification");
            String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
            
            UserServiceUtil userService = new UserServiceUtil();
            UserDetails userDetails = null;

            userDetails = userService.getUserDetailsByPersonNumber(personNo);
            List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
            String department = relationshipDetails.get(0).getDepartmentName(); 
            
            if("PENDING".equals(status))
            {
                currRow.setAttribute("StepId", totalStep);
                currRow.setAttribute("RequestStatus", "Withdrawn");
                currRow.setAttribute("ActionTaken", "Withdrawn");
                currRow.setAttribute("Assignee", personNo);
                currRow.setAttribute("AssigneeName", "");
                
                ADFUtils.findOperation("Commit").execute();
                Long stepid = (Long)ADFUtils.getBoundAttributeValue("StepId");
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"), 
                                                                    "MobileAllowance", (Number)nextStep.getAttribute("NextStepId"), stepid,
                                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), "WITHDRAW_ACT", "");
                
                JSFUtils.addFacesInformationMessage("Request Withdrawn Successfully !");
                
            }
            else
            {
                String assigneeName = "";
                String assigneeNo = "";
                String assigneeEmail = "";
                
                // If step type is POSITION
                if("POSITION".equals(stepType))
                {
                    System.err.println("Submitted withdraw : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
                    report = new BiReportAccess();
                    List<Map> personData = null;
                    
                    try 
                    {
                        personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                    } 
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }
                    
                    if (personData.get(0).get("DISPLAY_NAME") != null) 
                    {
                        assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
                    }
                    
                    if (personData.get(0).get("PERSON_NUMBER") != null) 
                    {
                        assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
                    }
                    
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) 
                    {
                        assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
                    }
                } 
                
                // If step type is DEPARTMENT MANAGER
                if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType))
                {
                    System.err.println("Submitted withdraw : DEPT_MANAGER :"+department);
                    report = new BiReportAccess();
                    
                    try 
                    {
                        List<Map> managerOfDeptList = report.getManagerOfDepartmentData(department);
                        
                        if (managerOfDeptList.size() > 0) 
                        {
                            if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null)
                            {
                                assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                            }
                            
                            if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null)
                            {
                                assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            }
                            
                            if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) 
                            {
                                assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                            } 
                        } 
                        else 
                        {
                            JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                        }
                    } 
                    catch (Exception e) 
                    {
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
                String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"), 
                                                                             "MobileAllowance", (Number)nextStep.getAttribute("NextStepId"), stepid, 
                                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"), "SUMBIT_ACT", "");
                
                if("POSITION".equals(stepType))
                {
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
                    {
                        if (assigneeEmail == null) 
                        {
                            JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver");
                        }
                        else
                        {
                          sendEmailByEmail(assigneeEmail, currRow);
                        } 
                    }
                }
                
                if(! "POSITION".equals(stepType))
                {
                    if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
                    {
                        if (assigneeNo == null) 
                        {
                            JSFUtils.addFacesErrorMessage("You donot have Approver Email, So mail can't be sent to Approver..");
                        }
                        else
                        {
                          sendEmail(assigneeNo, currRow);
                        } 
                    }
                }
            }
            
            ADFUtils.findOperation("Execute").execute();
        }
    }
 
    public String reject_withdraw() 
    {
        ViewObject reqVo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personNo = ADFUtils.getBoundAttributeValue("PersoneNumber").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String MobileTypeName="Mobile" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change
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
        String value = ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                                     "MobileAllowance", (Number)nextStep.getAttribute("StepId"), new Long(0), null, "REJECT_ACT", "");
        
        JSFUtils.addFacesInformationMessage("Withdrawn Request Rejected !");
        
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
        {
            if (personNo == null) 
            {
                JSFUtils.addFacesErrorMessage("You donot have Person Email, So mail can't be sent to the person..");
            }
            else
            {
              sendEmail(personNo, currRow);
            } 
        }
        
        return "back";
    }
    
    public String approve_withdraw() 
    {
        
        ViewObject reqVo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String MobileTypeName="Mobile" +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("MobileTypeName", MobileTypeName);//2023-PSC change  
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";   
        
        String personNumber = ADFUtils.getBoundAttributeValue("PersoneNumber").toString();
         
        UserServiceUtil userService1 = new UserServiceUtil();
        UserDetails userDetails1 = null;
        userDetails1 = userService1.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails1 = userDetails1.getUserWorkRelationshipDetails();
        Long lineManagerID1 = relationshipDetails1.get(0).getManagerId();
        UserDetails managerDetails1 = userService1.getUserDetailsByPersonId(lineManagerID1);
        String managerNumber1 = managerDetails1.getPersonNumber();
        String department1 = relationshipDetails1.get(0).getDepartmentName();
          
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        if("POSITION".equals(stepType))
        {
            System.err.println("Approved withdraw, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
            
            report = new BiReportAccess(); 
            List<Map> personData2 = null;
            
            try 
            {
                personData2 = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
            if (personData2.get(0).get("DISPLAY_NAME") != null) 
            {
                assigneeName = personData2.get(0).get("DISPLAY_NAME").toString();
            }
            
            if (personData2.get(0).get("PERSON_NUMBER") != null) 
            {
                assigneeNo = personData2.get(0).get("PERSON_NUMBER").toString();
            }
            
            if (personData2.get(0).get("EMAIL_ADDRESS") != null) 
            {
                assigneeEmail = personData2.get(0).get("EMAIL_ADDRESS").toString();
            }
        }
        
        // If step type is DEPARTMENT MANAGER
        if("DEPT_MANAGER".equals(stepType) || "COST_CENTER_MANAGER".equals(stepType))
        {
            System.err.println("Approved withdraw, Assigning to : : DEPT_MANAGER :"+ department1);
            
            report = new BiReportAccess();
            
            try 
            {
                List<Map> managerOfDeptList = report.getManagerOfDepartmentData(department1);
                
                if (managerOfDeptList.size() > 0) 
                {
                    if (managerOfDeptList.get(0).get("PERSON_NUMBER") != null) 
                    {
                        assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                    }
                    
                    if (managerOfDeptList.get(0).get("DISPLAY_NAME") != null) 
                    {
                        assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                    }
                    if (managerOfDeptList.get(0).get("EMAIL_ADDRESS") != null) 
                    {
                        assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                    }
                } 
                else 
                {
                    JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                    return null;
                }
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
        // If step type is LINE MANAGER
        if("LINE_MANAGER".equals(stepType))
        {
            System.err.println("Approved withdraw, Assigning to : : LINE_MANAGER ");     
            JAXBElement<String> aXBElement = null;
            
            if (managerDetails1.getUserPersonDetails().get(0).getDisplayName() != null) 
            {
                aXBElement = managerDetails1.getUserPersonDetails().get(0).getDisplayName();
            }
                
            assigneeName = aXBElement.getValue();
            assigneeNo = managerNumber1;
        } 
        
        // If step type is USER
        if("USER".equals(stepType))
        {
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
        
        ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "Waiting Withdraw Approval");
        ADFUtils.setBoundAttributeValue("ActionTaken", "Waiting Withdraw Approval"); 
        ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
        ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        
        if (nextStep.getAttribute("NextAssignee").equals("Finished"))
        {
            ADFUtils.setBoundAttributeValue("RequestStatus", "Withdrawn");
            ADFUtils.setBoundAttributeValue("ActionTaken", "Withdrawn");
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            
            report = new BiReportAccess();
            String dateString = ADFUtils.getBoundAttributeValue("CreationDate").toString(); 
            List<Map> datFileData = null;
            
            try 
            {
                datFileData = report.getMobileAllowanceDatfileInfo(personNumber, changeFormatOfDate("dd-MM-yyyy", "MM-dd-yyyy", dateString));
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            } 
            
            String assignmentNumber = datFileData.get(0).get("ASSIGNMENT_NUMBER")==null?"": datFileData.get(0).get("ASSIGNMENT_NUMBER").toString();
            String entryCount = datFileData.get(0).get("MULTIPLEENTRYCOUNT")==null?"": datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString(); 
            String sDate = datFileData.get(0).get("EFFECTIVE_START_DATE")==null?"": datFileData.get(0).get("EFFECTIVE_START_DATE").toString();
            String eDate = datFileData.get(0).get("EFFECTIVE_END_DATE")==null?"": datFileData.get(0).get("EFFECTIVE_END_DATE").toString();
            
            HashMap<String, String> params = new HashMap<String, String>(); 
            params.put("AssignmentNo", assignmentNumber);
            params.put("Count", entryCount);
            params.put("EffStartDate", sDate);
            params.put("EffEndDate", eDate);
            
            /*Added by MSF on 2024.06.11
            Now we are going to hardcode amount as 400 for every request, later we will implement the table structure for getting the amount with reference of their grade
            */
            params.put("Amount", "400");
            
            System.err.println("Fusion upload calling with values : "+params);
            
            try 
            {
                fusionFileLoader = new FusionDataLoader();
                //EES code added by Moshina
                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 12);
                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("RequestId").toString(), personNumber, "Withdrawn", "Mobile Allowance", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Withdrawn mobile allowance: "+value);
                //fusionFileLoader.sendFusionRequest(params, 12);
                
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            } 
            
            String value = ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                                         "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                         (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
            {
                     sendEmailToPerson(personNumber, currRow);
                     sendEmailByEmail(fyiEmailAddress, currRow);
            } 
        }
        else
        {
            String value = ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("RequestId"),
                                                                         "MobileAllowance",(Number)nextStep.getAttribute("StepId"), (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                         (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
            
            if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y"))
            {
                if("POSITION".equals(stepType))
                {
                    sendEmailByEmail(assigneeEmail, currRow);    
                }
                else
                {
                    sendEmail(assigneeNo, currRow);    
                }   
            } 
        }
      
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Withdrawn Request Approved !");
        
        return "back";
    }

    public String editPendingRequest()
    {
        ViewObject reqVo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow(); 
        String personNo = currRow.getAttribute("PersoneNumber").toString();
        
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
    
    private String changeFormatOfDate(String fromFormat, String toFormat, String strDate) throws ParseException 
    {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        java.util.Date date = formatter.parse(strDate);
        SimpleDateFormat ft = new SimpleDateFormat(toFormat);
        
        return ft.format(date);
     }

    public void sendEmailToPerson(String personNumber, Row subject) 
    {
        String email = getEmail(personNumber);

        if (null == email) 
        {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } 
        else 
        {
            sendMobileAllowanceEmail("OFOQ.HR@TATWEER.SA", email, (MobileAllowanceRequestUPViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public void onClickStatusCount(ActionEvent actionEvent) 
    {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
        String tripType = ADFContext.getCurrent().getSessionScope().get("tripType") == null ? "" : ADFContext.getCurrent().getSessionScope().get("tripType").toString();
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("MobileAllowanceRequestUPViewIterator").getViewObject();
        vo.applyViewCriteria(null);
        vo.executeQuery();
        
        if ("MyTasks".equals(statusType)) 
        {
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("PersoneName", person);
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }
        else if("PendingTasks".equals(statusType))
        {
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("AssigneeName", person);
            vc.addRow(vcRow);
            vcRow.setAttribute("RequestStatus", "IN ('PENDING','Waiting Withdraw Approval')");
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }
        else
        {
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("MobileAllowanceRequestUPViewCriteriaByAssignee"));
            vo.executeQuery();
        }
    }
    
    public Date convertStringToDate(String dateString) 
    {
        Date convertedDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        
        try 
        {             
            convertedDate = formatter.parse(dateString);             
        } 
        catch (ParseException e) 
        { 
            e.printStackTrace(); 
        }
        
        return convertedDate;
    }
	
	public String onClickStartNewTask() {
        /*
         * Mobile report - Getting mobile allowance from report
         */
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String mobAllowance = "0";
        List<Map> allowance = null;
        try {
            allowance = report.getMobileAllowanceAmount(personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (allowance.get(0).get("AMOUNT") != null) {
            mobAllowance = allowance.get(0).get("AMOUNT").toString();
        }
        System.out.println("mobAllowance is >>> " + mobAllowance);
        ADFContext.getCurrent().getPageFlowScope().put("mobAllowance", mobAllowance);
        return "addNew";
    }
}