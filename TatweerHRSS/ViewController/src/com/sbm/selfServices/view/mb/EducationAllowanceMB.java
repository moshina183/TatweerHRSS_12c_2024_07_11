package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.APConsumer;
import com.sbm.CodeCombinationConsumer;
import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.ChangePositionViewRowImpl;
import com.sbm.selfServices.model.views.up.EducationAllowanceViewRowImpl;
import com.sbm.selfServices.model.views.up.ExceptionalRewardViewRowImpl;
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
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.faces.model.SelectItem;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;

import oracle.adf.model.AttributeBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputFile;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;


import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Number;

import oracle.jbo.server.ViewObjectImpl;
import oracle.jbo.server.ApplicationModuleImpl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class EducationAllowanceMB {
    
    private RichInputText secondChild;
    private RichInputText thirdChild;
    private RichInputText fourthChild;
    private RichInputText fifthChild;
    private RichInputText sixthChild;
    private RichInputText seventhChild;
    private RichInputText eightthChild;
    private RichInputText ninthChild;
    private RichInputText tenthChild;
    private RichInputText empIBAN;
    private RichInputText schoolIBAN;
    private RichInputFile inputFile;
    private String attachmentFileName;
    private RichPopup attachmentPopup;
    private FusionDataLoader fusionFileLoader;
    public ArrayList<SelectItem> NameofChildList = new ArrayList<SelectItem>();
    public ArrayList<SelectItem> ChildDetailsList = new ArrayList<SelectItem>();    
    public HashMap<String,String> map = new HashMap<String,String>();

    public List<String> namelist = new ArrayList<String>();
    boolean isSocSelected = false;

    String oldValue = "temp";

    public EducationAllowanceMB() {
    }

    public String newRequest() {
        
        // Add event code here...
        JSFUtils.setExpressionValue("#{pageFlowScope.eAPersonNummber}",
                                    JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
        JSFUtils.setExpressionValue("#{pageFlowScope.eAPersonName}",
                                    JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
        //        JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",1);
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        JSFUtils.setExpressionValue("#{pageFlowScope.NameOfChild}",0);
                    BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;

            try {
                personData = report.getEduNoOfChildren(personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (personData.size() > 0) {
                if (personData.get(0).get("NUMBER_OF_CHILDS") != null) {
                    System.err.println("NUMBER_OF_CHILDS is " +
                                       personData.get(0).get("NUMBER_OF_CHILDS"));
                    Integer numberOfChilds =new Integer((String)personData.get(0).get("NUMBER_OF_CHILDS"));
                    if(numberOfChilds.compareTo(0)==0)
                    {
                           // JSFUtils.addFacesErrorMessage("You can't create Education Allowance Request as you have no children");
                            FilmStripBean.showPopupMessage("You can't create Education Allowance Request as you have no children");
                            return null;
                        
                        }
                    else if(numberOfChilds.compareTo(0)==1)
                    {
                            
                            JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",numberOfChilds);
                        
                        }
                }
                if (personData.get(0).get("NUMBER_OF_CHILDS") == null) {
                                   FilmStripBean.showPopupMessage("You can't create Education Allowance Request as you have no children");
                                   return null;
                               }

            }
        ArrayList<String> list = new ArrayList<String>();
        
        List<Map> ChildNameListMap=new ArrayList<Map>();
        try {
            ChildNameListMap = report.getChildDetails(personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(ChildNameListMap.size()>0){
                    System.err.println("In ChildNameListMap"); 
                    for(int i = 0; i < ChildNameListMap.size(); i++){
                        if(ChildNameListMap.get(i).get("CHILD_NAME") != null){
                            list.add(ChildNameListMap.get(i).get("CHILD_NAME").toString());
                        }                        
                        map.put(ChildNameListMap.get(i).get("CHILD_NAME").toString(),ChildNameListMap.get(i).get("CHILD_NATIONAL_ID").toString());
                    }                    
                }
        ChildDetailsList.clear();      
                for(String value : list){
                    ChildDetailsList.add(new SelectItem(value));                    
                }         
        
        return "newEducationAllowance";
    }

    public void showHideChildrenFields(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        Integer noOfChildren = (Integer)valueChangeEvent.getNewValue();
        Number n=new Number(0);
        if(noOfChildren.equals(1))
        {
            
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(false);
                secondChild.setValue(n);
//                thirdChild.setVisible(false);
                thirdChild.setValue(n);
//                fourthChild.setVisible(false);
                fourthChild.setValue(n);
//                fifthChild.setVisible(false);
                fifthChild.setValue(n);
//                sixthChild.setVisible(false);
                sixthChild.setValue(n);
//                seventhChild.setVisible(false);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            
            }
        else         if(noOfChildren.equals(2))
        {
            
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(false);
                thirdChild.setValue(n);
//                fourthChild.setVisible(false);
                fourthChild.setValue(n);
//                fifthChild.setVisible(false);
                fifthChild.setValue(n);
//                sixthChild.setVisible(false);
                sixthChild.setValue(n);
//                seventhChild.setVisible(false);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            }
        else         if(noOfChildren.equals(3))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(false);
                fourthChild.setValue(n);
//                fifthChild.setVisible(false);
                fifthChild.setValue(n);
//                sixthChild.setVisible(false);
                sixthChild.setValue(n);
//                seventhChild.setVisible(false);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            
            }
        else         if(noOfChildren.equals(4))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(false);
                fifthChild.setValue(n);
//                sixthChild.setVisible(false);
                sixthChild.setValue(n);
//                seventhChild.setVisible(false);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            
            }
        else         if(noOfChildren.equals(5))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(true);
                fifthChild.setValue(n);
//                sixthChild.setVisible(false);
                sixthChild.setValue(n);
//                seventhChild.setVisible(false);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            }
        else         if(noOfChildren.equals(6))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(true);
                fifthChild.setValue(n);
//                sixthChild.setVisible(true);
                sixthChild.setValue(n);
//                seventhChild.setVisible(false);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            }
        else         if(noOfChildren.equals(7))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(true);
                fifthChild.setValue(n);
//                sixthChild.setVisible(true);
                sixthChild.setValue(n);
//                seventhChild.setVisible(true);
                seventhChild.setValue(n);
//                eightthChild.setVisible(false);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            }
        else         if(noOfChildren.equals(8))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(true);
                fifthChild.setValue(n);
//                sixthChild.setVisible(true);
                sixthChild.setValue(n);
//                seventhChild.setVisible(true);
                seventhChild.setValue(n);
//                eightthChild.setVisible(true);
                eightthChild.setValue(n);
//                ninthChild.setVisible(false);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            }
        else         if(noOfChildren.equals(9))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(true);
                fifthChild.setValue(n);
//                sixthChild.setVisible(true);
                sixthChild.setValue(n);
//                seventhChild.setVisible(true);
                seventhChild.setValue(n);
//                eightthChild.setVisible(true);
                eightthChild.setValue(n);
//                ninthChild.setVisible(true);
                ninthChild.setValue(n);
//                tenthChild.setVisible(false);
                tenthChild.setValue(n);
            
            }
        else         if(noOfChildren.equals(10))
        {
                JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",noOfChildren);
//                secondChild.setVisible(true);
                secondChild.setValue(n);
//                thirdChild.setVisible(true);
                thirdChild.setValue(n);
//                fourthChild.setVisible(true);
                fourthChild.setValue(n);
//                fifthChild.setVisible(true);
                fifthChild.setValue(n);
//                sixthChild.setVisible(true);
                sixthChild.setValue(n);
//                seventhChild.setVisible(true);
                seventhChild.setValue(n);
//                eightthChild.setVisible(true);
                eightthChild.setValue(n);
//                ninthChild.setVisible(true);
                ninthChild.setValue(n);
//                tenthChild.setVisible(true);
                tenthChild.setValue(n);
            
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
                                              "5101030111", "00", "0000",
                                              "0000");
           // com.view.uiutils.JSFUtils.setExpressionValue("#{sessionScope.combinationIdList}", combinationIdList);
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
                                                                  "5101030111",
                                                                  "00", "0000",
                                                                  "0000",
                                                                  "PaaS.Self Service@tatweer.sa",
                                                                  "PAASTatweer@2020",
                                                                  "https://egwo.fa.em2.oraclecloud.com/fscmService/AccountCombinationService");


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
        String personMail = null;
        List<Map> personData = null;
        Row educationAllowanceRow =
            ADFUtils.findIterator("EducationAllowanceView1Iterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String EducationAllowanceName="EducationAllowance" +'-' + personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("EducationAllowanceName", EducationAllowanceName);//2023-PSC change       
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        
        //Sysout added by Moshina - 24-03-2024
        System.out.println("Approve action: "+personNumber);
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
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
        
        //Dynamic Approval
        
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        boolean approved = false;
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;
        userDetails = userService.getUserDetailsByPersonNumber(personNumber);
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();
        String userEmployeeType=relationshipDetails.get(0).getUserPersonType();
        
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
        
        if ( ! nextStep.getAttribute("NextAssignee").equals("Finished")){
            approved = true;
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId"));
            ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
            ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
            ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
            ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");
            ADFUtils.findOperation("Commit").execute();
            Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"),
                                                      "EducationAllowance",(Number)nextStep.getAttribute("StepId"),stepid.longValue(),
                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
            
                if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
                    if("POSITION".equals(stepType)){
                        System.err.println("Email to mail-id::"+assigneeEmail);
                        sendEmailByEmail(assigneeEmail, educationAllowanceRow);    
                    }else{
                        System.err.println("Email to user-id::"+assigneeNo);
                        sendEmail(assigneeNo, educationAllowanceRow);    
                    }   
                }
        }
        
       
        
/*        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {

            BiReportAccess report = new BiReportAccess();
           

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
                ADFUtils.setBoundAttributeValue("StepId",
                                                nextStep.getAttribute("NextStepId"));
               
                Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
                ADFUtils.findOperation("Commit").execute();
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"),
                                                          "EducationAllowance",(Number)nextStep.getAttribute("StepId"),stepid.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
                if (personData !=null && personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmail(personMail, educationAllowanceRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR and Admin Director dosn't has email");

                }
//               
              
            }
            JSFUtils.addFacesInformationMessage("Request has been approved");
            return "back"; 
        }   */
//        if (nextStep.getAttribute("NextAssignee").equals("Accountant")) {
//
//            
//            ADFUtils.setBoundAttributeValue("Assignee",
//                                            "2034");
//                    ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                    "Amr Jad");
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//            ADFUtils.findOperation("Commit").execute();  
//            Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
//            String value =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"),
//                                                      "EducationAllowance",(Number)nextStep.getAttribute("StepId"),stepid.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
//            JSFUtils.addFacesInformationMessage("Request has been approved");
//            return "back";     
//        }
 /*       
        if (nextStep.getAttribute("NextAssignee").equals("Cost Control Specialist")) {
              
            BiReportAccess report = new BiReportAccess();            
            try {
                personData = report.getPersonByPostionReport("Cost Control Specialist");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (personData.size() > 0) {
                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    ADFUtils.setBoundAttributeValue("Assignee",personData.get(0).get("PERSON_NUMBER"));
                    System.out.println("Cost Control Specialist Number is " +
                                       personData.get(0).get("PERSON_NUMBER"));
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    System.out.println("Cost Control Specialist Name is " +
                                       personData.get(0).get("DISPLAY_NAME"));

                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME"));
                }

                else {
                    JSFUtils.addFacesErrorMessage("You can't approve request as Cost Control Specialist Name is empty");
                    return null;
                }
                ADFUtils.setBoundAttributeValue("StepId",
                                                nextStep.getAttribute("NextStepId"));
               
                Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
                ADFUtils.findOperation("Commit").execute();
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"),
                                                          "EducationAllowance",(Number)nextStep.getAttribute("StepId"),stepid.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
                if (personData !=null && personData.get(0).get("EMAIL_ADDRESS") != null) {
                    personMail =
                            personData.get(0).get("EMAIL_ADDRESS").toString();
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmail(personMail, educationAllowanceRow);
                    }
                } else {
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Cost Control Specialist dosn't has email");
                } 
            }
            JSFUtils.addFacesInformationMessage("Request has been approved");
            return "back";  
        }
  */                
        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {

           
            String personName =
                (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
            System.out.println("the person number is ==============> " +
                               personNumber);
            String creationDate = null;
              creationDate=  (String)ADFUtils.getBoundAttributeValue("CreationDate");
              System.out.println("Creation Date:"+creationDate);
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
            System.out.println("After format:"+creationDate);
            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
            ADFUtils.findOperation("Commit").execute();
            
            
            String totalAmount =
                ADFUtils.getBoundAttributeValue("TotalAmount").toString();

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
          
            if (codeCombinationIdLine == null || codeCombinationIdLine.equals("-1")) {

                JSFUtils.addFacesErrorMessage("You can't approve the request before creating the mentioned Code combination so please refer to the financial consultant");
                return null;

            }
            Long longCodeCombinationIdLine =
                Long.parseLong(codeCombinationIdLine);
           
            String requestId =
                ADFUtils.getBoundAttributeValue("EducationAllowanceId").toString();
            String invoiceNumber = personNumber + "-" + requestId + "-EducationAllowance";
            System.err.println("invoiceNumber >>>> " + invoiceNumber);
            String description=personNumber+" - "+personName;
            
            
            BiReportAccess report = new BiReportAccess();
            
            List<Map> bankData = null;
            try {
                
                bankData =
                        report.getBankAccountData(personNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String bankName=null;
            String IBAN=null;
            String swiftCode=null;
            if(bankData.get(0).get("BANK_NAME")!=null){
                
                bankName=bankData.get(0).get("BANK_NAME").toString();
                
                
                }
            else
                
            {
                bankName="";
                
                }
            
            if(bankData.get(0).get("Employee_Bank_ID")!=null){
                
                swiftCode=bankData.get(0).get("Employee_Bank_ID").toString();
                
                
                }
            else
                
            {
                swiftCode="";
                
                }
            
            if(bankData.get(0).get("IBAN_NUMBER")!=null){
                
                IBAN=bankData.get(0).get("IBAN_NUMBER").toString();
                
                
                }
            else
                
            {
                IBAN="";
                
                }
            String    paymentMethod=null;
            if(ADFUtils.getBoundAttributeValue("PaymentMethod")!=null){
            
           paymentMethod = ADFUtils.getBoundAttributeValue("PaymentMethod").toString();
            }
            String vendorSiteCode = "Riyadh";
            if(paymentMethod.equalsIgnoreCase("School"))
            {
                    IBAN = "";
                    vendorSiteCode = "School Invoices";
                    if(ADFUtils.getBoundAttributeValue("IbanSchool")!=null){
                        IBAN=ADFUtils.getBoundAttributeValue("IbanSchool").toString();
                    }
                
                }
            if(!userEmployeeType.equals("Employee")){  
//            Map param = new HashMap();
//            param.put("vendorName",
//                      "Employees school Allownce"); //select vendor_name  from poz_suppliers_v
//            param.put("vendorId",
//                      "300000006593608"); //select vendor_id from poz_suppliers_v
//            param.put("vendorSiteCode", vendorSiteCode);
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
//            line1.put("codeCombinationId", longCodeCombinationIdLine);
//            line1.put("personNumber", personNumber);
//            line1.put("bankName", bankName);
//            line1.put("IBAN", IBAN);
//            line1.put("SwiftCode", swiftCode);
//            line1.put("PaymentType", paymentMethod);
//
//            List<Map> lineList = new ArrayList<Map>();
//            lineList.add(line1);
//            
//            List<Map> attacheList = new ArrayList<Map>(); 
//    try { 
//                ViewObject attachmentVO = ADFUtils.findIterator("EducationAllowanceAttachView1Iterator").getViewObject();
//                if(attachmentVO.getEstimatedRowCount()>0){
//                Row[] rows = attachmentVO.getAllRowsInRange();
//                Row row;
//                Map attachmentMap = null;
//                for(int i=0;i<rows.length;i++){
//                    attachmentMap = new HashMap();
//                    row = rows[i];
//                    attachmentMap.put("attachmentType", "FILE");
//                    attachmentMap.put("category", "To Payables");
//                    attachmentMap.put("fileTitle", row.getAttribute("FileName"));
//                    BlobDomain blob = (BlobDomain)row.getAttribute("AttachmentFile");
//                    InputStream inputStream = blob.getInputStream();
//                    byte[] bytes=null;
//                    try {
//                        bytes = IOUtils.toByteArray(inputStream);
//                    } catch (IOException e) {
//                        System.err.println("Exception : Exception in toByteArray --"+e.toString());
//                    }
//                    attachmentMap.put("fileContent", DatatypeConverter.printBase64Binary(bytes));
//                    attacheList.add(attachmentMap);
//                }
//                }
//            } catch (Exception e) {
//                System.err.println("Exception : Exception in attaching to file to invoice --"+e.toString());
//            } 
//    
//            APConsumer newAPInvoice = new APConsumer();
//            returnValue = newAPInvoice.createInvoice(param, lineList, attacheList);
                                                          System.err.println("Inside Invoice Mail");
                               byte[] bytes = null;
                               //bytes=this.getBusinessTripAttach();
                               BlobDomain blob =new BlobDomain(bytes);
                               OperationBinding sendMail =
                               ADFUtils.findOperation("callSendInvoiceEmailStoredPL");
                               sendMail.getParamsMap().put("p_request_type", "Educational Allowance");
                               sendMail.getParamsMap().put("p_request_number", requestId);
                               sendMail.getParamsMap().put("p_attachment", blob);
                               sendMail.execute();
            returnValue = "success";
              invoiceNumber="";
            //ADFUtils.setBoundAttributeValue("InvoiceNumber", invoiceNumber);
          }
           if (userEmployeeType.equals("Employee")) { //2023 Element Entry Change
                String dateString =creationDate;
               
                                         String formattedDate = dateString.replace('-', '/');
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
                                           System.out.println("myDateString  >>> " + formattedDate);
                                          OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                          Map paramMap = oper.getParamsMap();
                                          paramMap.put("elementName", "Education Allowance Earnings");
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
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            //            params.put("Date", creationDate.toString());
                                            if (datFileData.size() > 0) {
                                                params.put("AssignmentNumber",
                                                datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                                params.put("Amount",totalAmount.toString());
                                                params.put("StartDate", formattedDate);
                                                params.put("Count",
                                                                   datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                                params.put("InvoiceDate",creationDate);
                                                params.put("Description",description);
                                                params.put("Segment1","01");
                                                params.put("Segment2",division);
                                                params.put("Segment3",lob);
                                                params.put("Segment4",costCenter);
                                                params.put("Segment5",accountNumber);
                                                params.put("Segment6","00");
                                                try {
                                                fusionFileLoader = new FusionDataLoader();
                                                System.err.println("Educational Allowance Fusion Data Loader Params is::"+params);
                                                    
                                                    //EES code added by Moshina
                                                Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 20);
                                                String value = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("EducationAllowanceId").toString(), personNumber, "Approved", "Education Allowance", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                                System.out.println("TAT HDR Updated Value - Approved Education Allowance: "+value+ " Educational Allowance Fusion Data Loaded Sucessfully.");
                                                    
                                                /*fusionFileLoader.sendFusionRequest(params, 20);
                                                System.err.println("Educational Allowance Fusion Data Loaded Sucessfully");*/
                                                } catch (Exception e) {
                                                e.printStackTrace();
                                                }
                                               
                                               
                                            }

                                            else {

                                            JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");

                                           }
                                          
                                               returnValue="success";
                                               invoiceNumber="";
                                            ADFUtils.setBoundAttributeValue("InvoiceNumber", invoiceNumber);

                
          } //2023 Element Entry Change


//            APConsumer newAPInvoice = new APConsumer();
//            newAPInvoice.createInvoice(param, lineList);
            
            


        }
       
        if(nextStep.getAttribute("NextAssignee").equals("Finished") && returnValue.equalsIgnoreCase("success")){
            
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId"));
           
            Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
            String finalapproval = null;
            ADFUtils.findOperation("Commit").execute();
            if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
                finalapproval = "Y";
            } else {
                finalapproval = "N";
            }
            
            
            String value =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"),
                                                      "EducationAllowance",(Number)nextStep.getAttribute("StepId"),stepid.longValue(),(String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT",finalapproval);
           
            ADFUtils.findOperation("Commit").execute();
                if (emailNotification != null &&
                    emailNotification.equalsIgnoreCase("Y")) 
                { 
                    sendEmailToPerson(personNumber, educationAllowanceRow);
                /*Commented and added code by Moshina
                 * Mail SMTP issue
                 * sendEmailByEmail(fyiEmailAddress, educationAllowanceRow)
                 * group of mail addresses splited into single email address
                 */
                //sendEmailByEmail(fyiEmailAddress, educationAllowanceRow);
                if(fyiEmailAddress != null || fyiEmailAddress != ""){
                    String[] arrOfEmail = fyiEmailAddress.split(",");
                    for (String mail : arrOfEmail) {
                        System.out.println("single email address"+mail);
                        sendEmailByEmail(mail, educationAllowanceRow);
                    }
                }
//                    sendEmail(personNumber, educationAllowanceRow);
//                    sendEmailForEducationEmployee(fyiEmailAddress,
//                                                  educationAllowanceRow);
               
            }
          
           
            JSFUtils.addFacesInformationMessage("Request has been approved");
            return "back";
        }else if(approved){
            return "back";
        }
        else{
            ADFUtils.findOperation("Rollback").execute();
            JSFUtils.addFacesInformationMessage("Something went wrong! please contact HR Administrator!");
            return null;
        }
    }

    public void setSecondChild(RichInputText secondChild) {
        this.secondChild = secondChild;
    }

    public RichInputText getSecondChild() {
        return secondChild;
    }

    public void setThirdChild(RichInputText thirdChild) {
        this.thirdChild = thirdChild;
    }

    public RichInputText getThirdChild() {
        return thirdChild;
    }

    public void setFourthChild(RichInputText fourthChild) {
        this.fourthChild = fourthChild;
    }

    public RichInputText getFourthChild() {
        return fourthChild;
    }

    public void setFifthChild(RichInputText fifthChild) {
        this.fifthChild = fifthChild;
    }

    public RichInputText getFifthChild() {
        return fifthChild;
    }

    public void setSixthChild(RichInputText sixthChild) {
        this.sixthChild = sixthChild;
    }

    public RichInputText getSixthChild() {
        return sixthChild;
    }

    public void setSeventhChild(RichInputText seventhChild) {
        this.seventhChild = seventhChild;
    }

    public RichInputText getSeventhChild() {
        return seventhChild;
    }

    public void setEightthChild(RichInputText eightthChild) {
        this.eightthChild = eightthChild;
    }

    public RichInputText getEightthChild() {
        return eightthChild;
    }

    public void setNinthChild(RichInputText ninthChild) {
        this.ninthChild = ninthChild;
    }

    public RichInputText getNinthChild() {
        return ninthChild;
    }

    public void setTenthChild(RichInputText tenthChild) {
        this.tenthChild = tenthChild;
    }

    public RichInputText getTenthChild() {
        return tenthChild;
    }

    public void showHideIBAN(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        
        String paymentMethod = (String)valueChangeEvent.getNewValue();
        
            if(paymentMethod.equalsIgnoreCase("School"))
            {
                    schoolIBAN.setValue(null);
                    schoolIBAN.setVisible(true);
                    schoolIBAN.setDisabled(false);
                    schoolIBAN.setRequired(true);

                
                }
            else
            {
                
                    schoolIBAN.setRequired(false);
                    schoolIBAN.setVisible(false);
                    schoolIBAN.setDisabled(false);
                    schoolIBAN.setValue("");
                
                }
        
    }

    public void setEmpIBAN(RichInputText empIBAN) {
        this.empIBAN = empIBAN;
    }

    public RichInputText getEmpIBAN() {
        return empIBAN;
    }

    public void setSchoolIBAN(RichInputText schoolIBAN) {
        this.schoolIBAN = schoolIBAN;
    }

    public RichInputText getSchoolIBAN() {
        return schoolIBAN;
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

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public String showAttachmentPopup() {
        // Add event code here...
        inputFile.resetValue();
        inputFile.setValid(true);
        attachmentPopup.show(new RichPopup.PopupHints());
        return null;
    }

    public void setAttachmentPopup(RichPopup attachmentPopup) {
        this.attachmentPopup = attachmentPopup;
    }

    public RichPopup getAttachmentPopup() {
        return attachmentPopup;
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
    
    
    public void sendEmail(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
        } else {
            sendEducationAllowanceEmail("OFOQ.HR@TATWEER.SA", email,
                              (EducationAllowanceViewRowImpl)subject, "");
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

        sendEducationAllowanceEmail("OFOQ.HR@TATWEER.SA", personEmail,
                          (EducationAllowanceViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendEducationAllowanceEmail(String from, String to,
                                  EducationAllowanceViewRowImpl subject, String personFYI) {
        
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
            subj = "Educational Allowance Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated Educational Allowance Request, and below the details";
            
        }else{
            subj = "Educational Allowance Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of Educational Allowance request as below";
        }
        
        if(personFYI!= null && "Y".equals(personFYI)){
            if("APPROVED".equals(status)){
                subj = "Educational Allowance Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following Educational Allowance request has been Approved";
            }
        }
//                JSFUtils.addFacesInformationMessage(subj);
//                JSFUtils.addFacesInformationMessage(hdrMsg);
        
        
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
        String PersoneName=subject.getPersonName()!=null?subject.getPersonName():"";
                                             String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                             String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                             String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                             String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
                     String PersonGrade=subject.getPersonGrade()!=null?subject.getPersonGrade():"";
                     String NumberOfChildren =
            (String)(subject.getNumberOfChildren().toString() !=null?subject.getNumberOfChildren().toString():"");
                     
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

        String educationAllowanceDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Educational Allowance Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number Of Children\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + NumberOfChildren +
            "</td>\n" +
            "    </tr>\n";
        if(NumberOfChildren.compareTo("1") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;First Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getFirstChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("2") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Second Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getSecondChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("3") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Third Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getThirdChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("4") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Fourth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getFourthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("5") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Fifth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getFifthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("6") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Sixth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getSixthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("7") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Seventh Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getSeventhChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("8") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Eigth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getEightthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("9") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Ninth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNinthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(NumberOfChildren.compareTo("10") >=0){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Tenth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTenthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Total Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTotalAmount() +
            "</td>\n" +
            "    </tr>\n"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Payment Method\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPaymentMethod() +
            "</td>\n" +
            "    </tr>\n";
           educationAllowanceDetails = educationAllowanceDetails + "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryEducationIterator");    
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + educationAllowanceDetails +verticalSpace+ApprovalPart1+
            thankYouPart + signaturePart + "</p>";
         System.err.println("emailcontent==>"+emailcontent);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject", subj);
//                                    "Education Allowance Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", emailcontent);
        sendMail.execute();
    }

    public String submitRequest() {
        // Add event code here...
     namelist.clear();
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        long count = ADFUtils.findIterator("EducationAllowanceAttachView1Iterator").getEstimatedRowCount();
       // String MaxAmount =
           // ADFUtils.getBoundAttributeValue("LookupValueNameDisp").toString();
       // System.err.println("MaxAmount >>>>> "+MaxAmount);
        
        if (count < 1) {
            JSFUtils.addFacesErrorMessage("You must add the required invoices attachments before submitting the request");
            return null;
        }
        
        Number totalAmountTrans = (Number)ADFUtils.getBoundAttributeValue("TotalAmountTrans");
        System.err.println("totalAmountTrans >>>>> "+totalAmountTrans);
        
        String personNumber =
            ADFUtils.getBoundAttributeValue("PersonNumber").toString();
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String EducationAllowanceName="EducationAllowance" +'-' + personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("EducationAllowanceName", EducationAllowanceName);//2023-PSC change
        
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        
//        ADFUtils.setBoundAttributeValue("StepId",
//                                        nextStep.getAttribute("NextStepId"));
//        ADFUtils.setBoundAttributeValue("Assignee",
//                                        nextStep.getAttribute("NextAssignee"));
        
        Integer noOfChildren = (Integer)JSFUtils.resolveExpression("#{pageFlowScope.inv1}");
        
        ADFUtils.setBoundAttributeValue("NumberOfChildren1",noOfChildren);
        
        OperationBinding oper = ADFUtils.findOperation("getTotalAmountEducAllow");
        Map paramMap = oper.getParamsMap();
        
        paramMap.put("PersonNumber", personNumber);
        Number totalAmountThisYear = (Number)oper.execute();

        System.out.println("totalAmountThisYear >>> " + totalAmountThisYear);
        ViewObject LookUpValuesVO =
               ADFUtils.findIterator("LookUpValuesROVO1Iterator").getViewObject();
           ViewCriteria vc = LookUpValuesVO.createViewCriteria();
                   ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
                   vcRow.setAttribute("LookupValueId", 1001001);
                   vc.addRow(vcRow);
                   vcRow.setAttribute("LookupTypeId", 1000);
                   vc.addRow(vcRow);
                   LookUpValuesVO.applyViewCriteria(vc);
                   LookUpValuesVO.executeQuery();
                   long count1 = LookUpValuesVO.getEstimatedRowCount();
                   Row first = LookUpValuesVO.first();
                   String MaxCount=(String)first.getAttribute("LookupValueNameDisp");
                   Number MaxCountNum=(Number)first.getAttribute("LookupValueNameDispTl");
        

        if (totalAmountThisYear != null) {

            Number totalAmount = totalAmountTrans.add(totalAmountThisYear);
            Number reminderAmountThisYear =
                (Number)new Number(MaxCountNum).minus(totalAmountThisYear);

            if ((totalAmount.compareTo(MaxCountNum)) > 0)

            {
                JSFUtils.addFacesErrorMessage("Total Education Allowance Amount Shouldn't Exceed"+ MaxCount+ "SAR Per Year !!!  " +
                                              "  The remaining amount is  ( " +
                                              reminderAmountThisYear +
                                              " ) SAR");
                return null;
            }
   
        }
        else
        {
            
                if ((totalAmountTrans.compareTo(MaxCountNum)) > 0)
                    JSFUtils.addFacesErrorMessage("Total Education Allowance Amount Shouldn't Exceed" +MaxCount+ "SAR Per Year !!!");
                return null;
            
            }
        
        ADFUtils.setBoundAttributeValue("TotalAmount",totalAmountTrans);
        

//        ADFUtils.setBoundAttributeValue("ActionTaken", "PENDING");
//        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        

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
        BiReportAccess bankreport = new BiReportAccess();
        
        List<Map> bankData = null;
        String IBAN=null;
        try {
            
            bankData =
                    bankreport.getBankAccountData(personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bankData.get(0).get("IBAN_NUMBER")!=null){            
            IBAN=bankData.get(0).get("IBAN_NUMBER").toString();                        
            }
        else            
        {
            IBAN="";            
            }
        ADFUtils.setBoundAttributeValue("IbanEmployee", IBAN);
        String managerNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");

        Long lineManagerID =
            (Long)JSFUtils.resolveExpression("#{PersonInfo.lineManagerID}");
        String stringLineManagerID = lineManagerID != null ? lineManagerID.toString() : "";
        
        //Dynamic Approval 
        
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
//                    report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.err.println("HR Payroll and benefits Supervisor Number is " +
//                           personData.get(0).get("PERSON_NUMBER"));
//        System.err.println("HR Payroll and benefits Supervisor Name is " +
//                           personData.get(0).get("DISPLAY_NAME"));
//        System.err.println("HR Payroll and benefits Supervisor Email " +
//                           personData.get(0).get("EMAIL_ADDRESS"));
//        ADFUtils.setBoundAttributeValue("AssigneeName",
//                                        personData.get(0).get("DISPLAY_NAME").toString());

        ADFUtils.findOperation("Commit").execute();
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"), 
                                                                    "EducationAllowance", (Number)nextStep.getAttribute("NextStepId"), 
                                                                    stepid.longValue(), 
                                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                     act, "");
        
        JSFUtils.addFacesInformationMessage("Request has been submitted");
//        ADFUtils.findOperation("Execute").execute();
        Row educationAllowanceRow =
            ADFUtils.findIterator("EducationAllowanceView1Iterator").getCurrentRow();
        
//        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
//           sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                           educationAllowanceRow);
//        }
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if("POSITION".equals(stepType)){
                System.err.println("Email to mail-id::"+assigneeEmail);
                sendEmailByEmail(assigneeEmail, educationAllowanceRow);    
            }else{
                System.err.println("Email to user-id::"+assigneeNo);
                sendEmail(assigneeNo, educationAllowanceRow);    
            } 
        }
        return "back";
    }

    public String rejectRequest() throws SQLException {
        // Add event code here...
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String EducationAllowanceName="EducationAllowance" +'-' + personLocation;
                ADFContext.getCurrent().getPageFlowScope().put("EducationAllowanceName", EducationAllowanceName);//2023-PSC change
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification"); 
        ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
        EducationAllowanceViewRowImpl educationAllowanceRow =
            (EducationAllowanceViewRowImpl)ADFUtils.findIterator("EducationAllowanceView1Iterator").getCurrentRow();
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
         sendEmail(personNumber, educationAllowanceRow);
        }
        ADFUtils.findOperation("Commit").execute();
        Integer stepid = (Integer)ADFUtils.getBoundAttributeValue("StepId");
        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"), 
                                                                    "EducationAllowance",new Number(educationAllowanceRow.getAttribute("StepId").toString()), 
                                                                    stepid.longValue(), 
                                                                    (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                    "REJECT_ACT", "");
        JSFUtils.addFacesInformationMessage("Education Allowance Request has been Rejected");

        return "back";
    }

    public void setValueOfInv1(ActionEvent actionEvent) {
        // Add event code here...
        EducationAllowanceViewRowImpl educationAllowanceRow =
            (EducationAllowanceViewRowImpl)ADFUtils.findIterator("EducationAllowanceView1Iterator").getCurrentRow();
      Integer numberOfChildren = (Integer)educationAllowanceRow.getAttribute("NumberOfChildren");
        JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",numberOfChildren);
    }

    public String saveRequest() {
        // Add event code here...
       namelist.clear();
        System.out.println("save request namelist: "+namelist);
        Integer noOfChildren = (Integer)JSFUtils.resolveExpression("#{pageFlowScope.inv1}");
        ADFUtils.setBoundAttributeValue("NumberOfChildren1",noOfChildren);
//        if(ADFUtils.getBoundAttributeValue("NameOfChild")==null){
//            JSFUtils.addFacesErrorMessage("Name Of Child is Mandatory");
//            return null;
//        }
        long count = ADFUtils.findIterator("EducationAllowanceAttachView1Iterator").getEstimatedRowCount();
        
        if (count < 1) {
            JSFUtils.addFacesErrorMessage("You must add the required invoices attachments before saving the request");
            return null;
        }
        Object employeeNumber =
            JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        System.err.println("employeeNumber is >>>>  " + employeeNumber);
        if (employeeNumber == null) {
            JSFUtils.addFacesErrorMessage("You don't have Person Number, So you can't save the request");
            return null;
        }
        
        Number totalAmountTrans = (Number)ADFUtils.getBoundAttributeValue("TotalAmountTrans");
                System.err.println("totalAmountTrans >>>>> "+totalAmountTrans);
                        ADFUtils.setBoundAttributeValue("TotalAmount",totalAmountTrans);
     
        ADFUtils.setBoundAttributeValue("ActionTaken", "SAVED");
        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Education Allowance Request has been saved");
        
        ADFUtils.findOperation("Execute").execute();
        
        return "back";
    }

    public String directApprove() {
        // Add event code here...
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersonNumber.inputValue}");
        String personName =
            (String)JSFUtils.resolveExpression("#{bindings.PersonName.inputValue}");
        System.out.println("the person number is ==============> " +
                           personNumber);

        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
        ADFUtils.findOperation("Commit").execute();
        
        
        String totalAmount =
            ADFUtils.getBoundAttributeValue("TotalAmount").toString();

        String codeCombinationIdLine = getCodeCombinationId(personNumber);
        if (codeCombinationIdLine == null) {

            JSFUtils.addFacesErrorMessage("You can't approve the request before creating the mentioned Code combination so please refer to the financial consultant");
            return null;

        }
        Long longCodeCombinationIdLine =
            Long.parseLong(codeCombinationIdLine);

        String requestId =
            ADFUtils.getBoundAttributeValue("EducationAllowanceId").toString();
        String invoiceNumber = personNumber + "-" + requestId + "-EducationAllowance";
        System.err.println("invoiceNumber >>>> " + invoiceNumber);
        String description=personNumber+" - "+personName;
        
        
        BiReportAccess report = new BiReportAccess();
        
        List<Map> bankData = null;
        try {
            
            bankData =
                    report.getBankAccountData(personNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String bankName=null;
        String IBAN=null;
        String swiftCode=null;
        if(bankData.get(0).get("BANK_NAME")!=null){
            
            bankName=bankData.get(0).get("BANK_NAME").toString();
            
            
            }
        else
            
        {
            bankName="";
            
            }
        
        if(bankData.get(0).get("Employee_Bank_ID")!=null){
            
            swiftCode=bankData.get(0).get("Employee_Bank_ID").toString();
            
            
            }
        else
            
        {
            swiftCode="";
            
            }
        
        if(bankData.get(0).get("IBAN_NUMBER")!=null){
            
            IBAN=bankData.get(0).get("IBAN_NUMBER").toString();
            
            
            }
        else
            
        {
            IBAN="";
            
            }
        String    paymentMethod=null;
        if(ADFUtils.getBoundAttributeValue("PaymentMethod")!=null){
        
        paymentMethod = ADFUtils.getBoundAttributeValue("PaymentMethod").toString();
        }
        
        if(paymentMethod.equalsIgnoreCase("School"))
        {
                IBAN=ADFUtils.getBoundAttributeValue("IbanSchool").toString();
            
            }

        Map param = new HashMap();
        param.put("vendorName",
                  "Employees school Allownce"); //select vendor_name  from poz_suppliers_v
        param.put("vendorId",
                  "300000006593608"); //select vendor_id from poz_suppliers_v
        param.put("vendorSiteCode", "Riyadh");
        param.put("ledgerId", "300000001778002");
        param.put("orgId", "300000001642073");
        param.put("invoiceNumber", invoiceNumber);
        param.put("invoiceAmount", totalAmount);
        param.put("paymentCurrencyCode", "SAR");
        param.put("invoiceTypeLookupCode", "STANDARD");
        param.put("termsName", "Immediate");
        param.put("paymentMethod", "WIRE");
        param.put("Description", description);

        Map line1 = new HashMap();
        line1.put("lineNumber", "1");
        line1.put("lineType", "ITEM");
        line1.put("lineAmount", totalAmount); //var
        line1.put("lineCurrencyCode", "SAR");
        line1.put("codeCombinationId", longCodeCombinationIdLine);
        line1.put("personNumber", personNumber);
        line1.put("bankName", bankName);
        line1.put("IBAN", IBAN);
        line1.put("SwiftCode", swiftCode);
        line1.put("PaymentType", paymentMethod);

        List<Map> lineList = new ArrayList<Map>();
        lineList.add(line1);
        
        List<Map> attacheList = new ArrayList<Map>();
        
        ViewObject attachmentVO = ADFUtils.findIterator("EducationAllowanceAttachView1Iterator").getViewObject();
        if(attachmentVO.getEstimatedRowCount()>0){
            Row[] rows = attachmentVO.getAllRowsInRange();
            Row row;
            Map attachmentMap = null;
            for(int i=0;i<rows.length;i++){
                attachmentMap = new HashMap();
                row = rows[i];
                attachmentMap.put("attachmentType", "FILE");
                attachmentMap.put("category", "To Payables");
                attachmentMap.put("fileTitle", row.getAttribute("FileName"));
                BlobDomain blob = (BlobDomain)row.getAttribute("AttachmentFile");
                InputStream inputStream = blob.getInputStream();
                byte[] bytes=null;
                try {
                    bytes = IOUtils.toByteArray(inputStream);
                } catch (IOException e) {
                }
                attachmentMap.put("fileContent", DatatypeConverter.printBase64Binary(bytes));
                attacheList.add(attachmentMap);
            }
        }


        APConsumer newAPInvoice = new APConsumer();
        newAPInvoice.createInvoice(param, lineList, attacheList);
            
        
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";
    }
    
    public String checkSession(){
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee = (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if(personNumber==null || assignee==null){
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }
    public void sendEmailForEducationEmployee(String email, Row subject) {
//         String email = getEmail(personNumber);

         if (null == email) {
             JSFUtils.addFacesInformationMessage("Mail hasn't been sent because the employee has no email");
         } else {
             sendEducationAllowanceEmailForEmpFYI("OFOQ.HR@TATWEER.SA", email,
                               (EducationAllowanceViewRowImpl)subject);
             JSFUtils.addFacesInformationMessage("Mail has been sent");
         }
     }
    public void sendEducationAllowanceEmailForEmpFYI(String from, String to,
                                  EducationAllowanceViewRowImpl subject) {
        if (to == null) {
            //to = "heleraki@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }

        String into = "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "<br/>" +
            "Kindly be informed that the following Educational Allowance Request has been Approved" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String PersoneName=subject.getPersonName()!=null?subject.getPersonName():"";
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

        String educationAllowanceDetails =
        " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Educational Allowance Details</h2></td></tr>" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Number Of Children\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNumberOfChildren() +
            "</td>\n" +
            "    </tr>\n";
        if(subject.getNumberOfChildren()>=1){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;First Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getFirstChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=2){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Second Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getSecondChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=3){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Third Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getThirdChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=4){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Fourth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getFourthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=5){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Fifth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getFifthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=6){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Sixth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getSixthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=7){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Seventh Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getSeventhChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=8){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Eigth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getEightthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=9){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Ninth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getNinthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
        if(subject.getNumberOfChildren()>=10){
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Tenth Child's Invoice\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTenthChild() +
            "</td>\n" +
            "    </tr>\n";
        }
            educationAllowanceDetails = educationAllowanceDetails + "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Total Amount\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTotalAmount() +
            "</td>\n" +
            "    </tr>\n"+
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Payment Method\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getPaymentMethod() +
            "</td>\n" +
            "    </tr>\n";
           educationAllowanceDetails = educationAllowanceDetails + "  </table>";

        String ApprovalPart1= ApprovelLine.approvalLine("ApprovalHistoryEducationIterator");    
        String thankYouPart = "<br/><b>Thanks In Advance " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String emailcontent =
            into + personalInformation + verticalSpace + educationAllowanceDetails +verticalSpace+ApprovalPart1+
            thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //            sendMail.getParamsMap().put("receiver", "vf.khayal@gmail.com,ah.alkhayal@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Education Allowance Request " + subject.getRequestStatus());
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
        //2023 Number of Children Fix
        String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        
        JSFUtils.setExpressionValue("#{pageFlowScope.NameOfChild}",0);
                            BiReportAccess report = new BiReportAccess();
                    List<Map> personData = null;

                    try {
                        personData = report.getEduNoOfChildren(personNumber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (personData.size() > 0) {
                        if (personData.get(0).get("NUMBER_OF_CHILDS") != null) {
                            System.err.println("NUMBER_OF_CHILDS is " +
                                               personData.get(0).get("NUMBER_OF_CHILDS"));
                            Integer numberOfChilds =new Integer((String)personData.get(0).get("NUMBER_OF_CHILDS"));
                            if(numberOfChilds.compareTo(0)==0)
                            {
                                   // JSFUtils.addFacesErrorMessage("You can't create Education Allowance Request as you have no children");
                                    FilmStripBean.showPopupMessage("You can't create Education Allowance Request as you have no children");
                                    return null;
                                
                                }
                            else if(numberOfChilds.compareTo(0)==1)
                            {
                                    
                                    JSFUtils.setExpressionValue("#{pageFlowScope.inv1}",numberOfChilds);
                                
                                }
                        }
                        if (personData.get(0).get("NUMBER_OF_CHILDS") == null) {
                            FilmStripBean.showPopupMessage("You can't create Education Allowance Request as you have no children");
                            return null;
                        }
                        

                    }
        ArrayList<String> list = new ArrayList<String>();
                
                List<Map> ChildNameListMap=new ArrayList<Map>();
                try {
                    ChildNameListMap = report.getChildDetails(personNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if(ChildNameListMap.size()>0){
                            System.err.println("In ChildNameListMap"); 
                            for(int i = 0; i < ChildNameListMap.size(); i++){
                                if(ChildNameListMap.get(i).get("CHILD_NAME") != null){
                                    list.add(ChildNameListMap.get(i).get("CHILD_NAME").toString());
                                }                        
                                map.put(ChildNameListMap.get(i).get("CHILD_NAME").toString(),ChildNameListMap.get(i).get("CHILD_NATIONAL_ID").toString());
                            }                    
                        }
                ChildDetailsList.clear();      
                        for(String value : list){
                            ChildDetailsList.add(new SelectItem(value));                    
                        } 
        //2023 Number of Children Fix
        ViewObject reqVo = ADFUtils.findIterator("EducationAllowanceView1Iterator").getViewObject();
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
            sendEducationAllowanceEmail("OFOQ.HR@TATWEER.SA", email,
                                     (EducationAllowanceViewRowImpl)subject, "Y");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public void withdrawRequest(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)){
            
            ViewObject reqVo = ADFUtils.findIterator("EducationAllowanceView1Iterator").getViewObject();
            Row currRow = reqVo.getCurrentRow();
            String status = currRow.getAttribute("RequestStatus").toString();
            String personNo = currRow.getAttribute("PersonNumber").toString();
            String personLocation = (String)currRow.getAttribute("PersonLocation") !=null?(String)currRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String EducationAllowanceName="EducationAllowance" +'-' + personLocation;
                    ADFContext.getCurrent().getPageFlowScope().put("EducationAllowanceName", EducationAllowanceName);//2023-PSC change
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
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("EducationAllowanceId"), 
                                                                             "EducationAllowance", (Number)nextStep.getAttribute("NextStepId"), 
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
    
    /* Person number Code change done by Moshina - 24-03-2024
     * Education allowance request assgined to wrong person after final approval
     */
    public String editEducationalAllowance() {
            BiReportAccess report = new BiReportAccess();
            String personNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
            System.out.println("Approver Person Number: "+personNumber);
            ArrayList<String> list = new ArrayList<String>();
            List<Map> ChildNameListMap=new ArrayList<Map>();
            ViewObject vo1 = ADFUtils.findIterator("EducationAllowanceView1Iterator").getViewObject();
            Row r1 = vo1.getCurrentRow();
            String personNumber1 = r1.getAttribute("PersonNumber").toString();
            System.out.println("Person numb: "+personNumber1);
            try {
                ChildNameListMap = report.getChildDetails(personNumber1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(ChildNameListMap.size()>0){
                System.err.println("In ChildNameListMap");
                for(int i = 0; i < ChildNameListMap.size(); i++){
                    if(ChildNameListMap.get(i).get("CHILD_NAME") != null){
                        list.add(ChildNameListMap.get(i).get("CHILD_NAME").toString());
                    }
                    map.put(ChildNameListMap.get(i).get("CHILD_NAME").toString(),ChildNameListMap.get(i).get("CHILD_NATIONAL_ID").toString());
                }
            }
            ChildDetailsList.clear();
            for(String value : list){
                ChildDetailsList.add(new SelectItem(value));
            }
            System.out.println("ChildDetailsList"+ChildDetailsList);
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
            JSFUtils.setExpressionValue("#{pageFlowScope.NameOfChild}",0);
            ViewObject vo = ADFUtils.findIterator("EducationAllowanceView1Iterator").getViewObject();
            Row r = vo.getCurrentRow();
            if(r.getAttribute("RequestStatus") != null && ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn Rejected".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")))){
                JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
            }else{
                String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
                if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                     personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
                }
                
                                                        
                           String EducationAllowanceName="EducationAllowance" +'-' + personLocation;
                        ADFContext.getCurrent().getPageFlowScope().put("EducationAllowanceName", EducationAllowanceName);//2023-PSC change
                OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
                Row nextStep = (Row)nextOpr.execute();
                String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
                JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
            } 
            
           
            return "edit";
        }
    public void showNameofChildNID(ValueChangeEvent valueChangeEvent) {
        String NameofChild = "";
        int count;
        //valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        System.out.println("valueChangeEvent.getNewValue()" +valueChangeEvent.getNewValue().toString());
        System.out.println("valueChangeEvent.getOldValue()" +oldValue);
        count = namelist.size();
        if(valueChangeEvent.getNewValue()!= oldValue ){
            oldValue = valueChangeEvent.getNewValue().toString();
            System.out.println("inside oldValue comp: "+oldValue);
            NameofChild = (String)valueChangeEvent.getNewValue();
            if(namelist.contains(NameofChild)){
                System.out.println("inside if:"+namelist);
            //    JSFUtils.addFacesErrorMessage("Selected child already added.");
                //return null;
            }else{
                System.out.println("inside else:"+namelist);
                namelist.add(NameofChild);
                System.out.println(namelist);
                count = namelist.size();
                
                if(count == 1){
                    ADFUtils.setBoundAttributeValue("NameOfChildOne",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChild) );
                }
                else if(count == 2){
                    ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChild) );
                }
               else if(count == 3){
                    ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChild) );
                }
              else  if(count == 4){
                    ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChild) );
                }
                else if(count == 5){
                    ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChild) );
                }
               else if(count == 6){
                    ADFUtils.setBoundAttributeValue("NameOfChildSix",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildSix",map.get(NameofChild) );
                }
               else if(count == 7){
                    ADFUtils.setBoundAttributeValue("NameOfChildSeven",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildSeven",map.get(NameofChild) );
                }
               else if(count == 8){
                    ADFUtils.setBoundAttributeValue("NameOfChildEight",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildEight",map.get(NameofChild) );
                }
              else  if(count == 9){
                    ADFUtils.setBoundAttributeValue("NameOfChildNine",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildNine",map.get(NameofChild) );
                }
               else if(count == 10){
                    ADFUtils.setBoundAttributeValue("NameOfChildTen",NameofChild);
                    ADFUtils.setBoundAttributeValue("NidOfChildTen",map.get(NameofChild) );
                }
            }    
        }
        
        
//        Object listChild=valueChangeEvent.getNewValue();
//        System.out.println("Object List:" + listChild);  
//        if (listChild instanceof ArrayList<?>) {
//             NameofChildList = (ArrayList<SelectItem>) listChild;
//            System.out.println("Inside If"+NameofChildList);
//            
//        } else {
//            System.out.println("Inside Else");
//            // Handle the case where newValue is not an instance of ArrayList<SelectItem>
//        }
//          NameofChildList = (ArrayList<SelectItem>)valueChangeEvent.getNewValue();
//                System.out.println("Name of Child:" + NameofChildList);             
//         Object[] NameofChildListArray = null;             
//         NameofChildListArray= NameofChildList.toArray();
//        Object listChild=valueChangeEvent.getNewValue();
//        String s=listChild.toString();
//        System.out.println("String object" + s);
//List<?> resultList; 
//if (listChild instanceof Collection<?>) {
//    // If yourObject is a Collection (e.g., ArrayList, LinkedList, etc.)
//    resultList = new ArrayList((Collection<?>) listChild);
//    System.out.println("If List:" + resultList); 
//} else if (listChild.getClass().isArray()) {
//    // If yourObject is an array
//    resultList = Arrays.asList((Object[]) listChild);
//    System.out.println("Else If List:" + resultList); 
//} else {
//    // Handle other cases or throw an exception if necessary
//    throw new IllegalArgumentException("Cannot convert the object to a List");
//} 

//        Integer noOfChildren=NameofChildList.size();
//       // Integer noOfChildren =NameofChildListArray.length;
//         if (NameofChildList != null) {
//        
//       JSFUtils.setExpressionValue("#{pageFlowScope.NameOfChild}",NameofChildList.size());
//             System.out.println("length:" + NameofChildList.size());
//             for (int i = 0; i < NameofChildList.size(); i++) {
//                 System.out.println("---" + NameofChildList.get(i));
//             }
//            
//         if(noOfChildren.equals(1)){
//                     ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                        ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get( NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree","");
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive","");
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(2)){
//                     ADFUtils.setBoundAttributeValue("NameOfChildOne",  NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo", NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get( NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get( NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree","");
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive","");
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(3)){
//                     ADFUtils.setBoundAttributeValue("NameOfChildOne",  NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo", NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree", NameofChildList.get(2) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get( NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get( NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get( NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive","");
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(4)){
//                      ADFUtils.setBoundAttributeValue("NameOfChildOne",  NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo", NameofChildList.get(1));
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree", NameofChildList.get(2) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour", NameofChildList.get(3));
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive","");
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(5)){
//                     ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChildList.get(2) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChildList.get(3) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChildList.get(4) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChildList.get(4)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(6)){
//                     ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChildList.get(2));
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChildList.get(3) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChildList.get(4));
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix",NameofChildList.get(5) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChildList.get(4)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix",map.get(NameofChildList.get(5)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(7)){
//                      ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChildList.get(2));
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChildList.get(3) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChildList.get(4) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix",NameofChildList.get(5));
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", NameofChildList.get(6));
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight","" );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChildList.get(4)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix",map.get(NameofChildList.get(5)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", map.get(NameofChildList.get(6)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", "");
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(8)){
//                      ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChildList.get(2) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChildList.get(3));
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChildList.get(4) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix",NameofChildList.get(5) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven",NameofChildList.get(6));
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight",NameofChildList.get(7) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", "");
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChildList.get(4)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix",map.get(NameofChildList.get(5)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", map.get(NameofChildList.get(6)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight",map.get(NameofChildList.get(7)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine","" );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(9)){
//                     ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChildList.get(2));
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChildList.get(3) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChildList.get(4) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix",NameofChildList.get(5) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", NameofChildList.get(6));
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight",NameofChildList.get(7) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", NameofChildList.get(8));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", "");
//                                  ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(0)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChildList.get(4)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix",map.get(NameofChildList.get(5)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven",map.get(NameofChildList.get(6)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", map.get(NameofChildList.get(7)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine",map.get(NameofChildList.get(8)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen","");
//                  }
//                  if(noOfChildren.equals(10)){
//                      ADFUtils.setBoundAttributeValue("NameOfChildOne", NameofChildList.get(0));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTwo",NameofChildList.get(1) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildThree",NameofChildList.get(2) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFour",NameofChildList.get(3) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildFive",NameofChildList.get(4) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSix",NameofChildList.get(5) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildSeven", NameofChildList.get(6));
//                      ADFUtils.setBoundAttributeValue("NameOfChildEight",NameofChildList.get(7) );
//                      ADFUtils.setBoundAttributeValue("NameOfChildNine", NameofChildList.get(8));
//                      ADFUtils.setBoundAttributeValue("NameOfChildTen", NameofChildList.get(9));
//                      ADFUtils.setBoundAttributeValue("NidOfChildOne",map.get(NameofChildList.get(0)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTwo",map.get(NameofChildList.get(1)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildThree",map.get(NameofChildList.get(2)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildFour",map.get(NameofChildList.get(3)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildFive",map.get(NameofChildList.get(4)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildSix",map.get(NameofChildList.get(5)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildSeven", map.get(NameofChildList.get(6)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildEight", map.get(NameofChildList.get(7)));
//                      ADFUtils.setBoundAttributeValue("NidOfChildNine",map.get(NameofChildList.get(8)) );
//                      ADFUtils.setBoundAttributeValue("NidOfChildTen",map.get(NameofChildList.get(9)));
//                  }
//        
         
         
   //  }
        
    }
    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
         
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("EducationAllowanceView1Iterator").getViewObject();
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
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("EducationAllowanceViewCriteria"));
            vo.executeQuery();
        }
    }

    public void setNameofChildList(ArrayList<SelectItem> NameofChildList) {
        this.NameofChildList = NameofChildList;
    }

    public ArrayList<SelectItem> getNameofChildList() {
        return NameofChildList;
    }

    public void setChildDetailsList(ArrayList<SelectItem> ChildDetailsList) {
        this.ChildDetailsList = ChildDetailsList;
    }

    public ArrayList<SelectItem> getChildDetailsList() {
        return ChildDetailsList;
    }

   

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setNamelist(List<String> namelist) {
        this.namelist = namelist;
    }

    public List<String> getNamelist() {
        return namelist;
    }

    public void onSelectNameOfChild(ValueChangeEvent valueChangeEvent) {
        System.out.println("onSelectNameOfChild: ");
    }
}
