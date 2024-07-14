package com.mivors.model.bi.integration;

import com.mivors.elaf.bi.ExternalReportWSSService;
import com.mivors.elaf.bi.ExternalReportWSSService_Service;

import com.mivors.elaf.bi.type.ArrayOfParamNameValue;
import com.mivors.elaf.bi.type.ArrayOfString;
import com.mivors.elaf.bi.type.ObjectFactory;

import com.mivors.elaf.bi.type.ParamNameValue;
import com.mivors.elaf.bi.type.ParamNameValues;
import com.mivors.elaf.bi.type.ReportRequest;
import com.mivors.elaf.bi.type.ReportResponse;


import com.mivors.model.bi.integration.type.PrivilegesToManpower;
import com.mivors.model.bi.integration.type.collection.ActionReasonCollection;
import com.mivors.model.bi.integration.type.collection.AdjustLoanBalanceCollection;
import com.mivors.model.bi.integration.type.collection.AllEmpsAndMngrsCollection;
import com.mivors.model.bi.integration.type.collection.AllPersonsCollection;
import com.mivors.model.bi.integration.type.collection.BankAccountDataCollection;
import com.mivors.model.bi.integration.type.collection.BudgetReportCollection;
import com.mivors.model.bi.integration.type.collection.BusinessTripWithdrawnCollection;
import com.mivors.model.bi.integration.type.collection.ChildDetailsCollection;
import com.mivors.model.bi.integration.type.collection.CodeCombinationIDCollection;
import com.mivors.model.bi.integration.type.collection.TatweerCollection;

import com.mivors.model.bi.integration.type.collection.DepartmentCollection;


import com.mivors.model.bi.integration.type.collection.DeptEmployeesCollection;
import com.mivors.model.bi.integration.type.collection.EducationNoOfChildrenCollection;
import com.mivors.model.bi.integration.type.collection.EmpIsMngrCollection;
import com.mivors.model.bi.integration.type.collection.EmpSalaryCollection;
import com.mivors.model.bi.integration.type.collection.EmployeeChildsCollection;
import com.mivors.model.bi.integration.type.collection.ExitReEntryDatFileDataCollection;
import com.mivors.model.bi.integration.type.collection.ExitReReentryWithdrawCollections;
import com.mivors.model.bi.integration.type.collection.GradesCollection;
import com.mivors.model.bi.integration.type.collection.HCMSegmentsCollection;
import com.mivors.model.bi.integration.type.collection.InvoiceStatusCollection;
import com.mivors.model.bi.integration.type.collection.JobsCollection;
import com.mivors.model.bi.integration.type.collection.LoanDatFileDataCollection;
import com.mivors.model.bi.integration.type.collection.LoanRemainingCollection;
import com.mivors.model.bi.integration.type.collection.LoanWithdrawDatCollection;
import com.mivors.model.bi.integration.type.collection.LocationsCollection;
import com.mivors.model.bi.integration.type.collection.ManagerEmployeesCollection;
import com.mivors.model.bi.integration.type.collection.ManagerOfDeptCollection;

import com.mivors.model.bi.integration.type.collection.MobileAllowanceDetailsCollection;
import com.mivors.model.bi.integration.type.collection.MobileDateCollection;
import com.mivors.model.bi.integration.type.collection.MobileDatfileCollection;
import com.mivors.model.bi.integration.type.collection.OvertimeDatFileDataCollection;
import com.mivors.model.bi.integration.type.collection.OvertimeDatWithdrawCollection;
import com.mivors.model.bi.integration.type.collection.PerDiemCollection;
import com.mivors.model.bi.integration.type.collection.PersonAccrualCollection;
import com.mivors.model.bi.integration.type.collection.PersonByPositionCollection;

import com.mivors.model.bi.integration.type.collection.PositionWorkerCollection;
import com.mivors.model.bi.integration.type.collection.PositionsCollection;

import com.mivors.model.bi.integration.type.collection.PrivilegesToManpowerCollection;
import com.mivors.model.bi.integration.type.collection.TerminatedEmpsCollection;
import com.mivors.model.bi.integration.type.collection.VacantPositionCollection;

import java.io.StringReader;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import javax.xml.bind.Unmarshaller;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class BiReportAccess {
    private ExternalReportWSSService_Service externalReportWSSService_Service;
    private ExternalReportWSSService externalReportWSSService;
    private SecurityPoliciesFeature securityFeatures;

    public static final String DEPARTMENTS_REPORT = "DepartmentsReport.xdo";
    public static final String MANAGER_OF_DEPT_REPORT = "ManagerOfDepartmentReport.xdo";
    public static final String CODE_COMBINATION_ID_REPORT = "CodeCombinationIDReport.xdo";
    public static final String HCM_SEGMENTS_REPORT = "HCMSegmentReport.xdo";
    public static final String EMPLOYEE_SALARY_REPORT = "EmployeeSalaryReport.xdo";
    public static final String ACTION_REASON_REPORT = "THC_EMP_ACTION_REASON_Report.xdo";
    public static final String PERSON_BY_POSITION_REPORT = "THCPositionReport.xdo";
    public static final String OVERTIME_DAT_FILE_REPORT = "OvertimeDatFileReport.xdo";
    public static final String LOAN_DAT_FILE_REPORT = "LoanDatFileReport.xdo";
    public static final String GRADES_REPORT = "GradesReport.xdo";
    public static final String JOBS_REPORT = "JobsReport.xdo";
    public static final String DEPT_EMPLOYEES_REPORT = "DepartmentsEmployeesReport.xdo";
    public static final String EMPLOYEE_CHILDS_REPORT = "EmployeeChildsReport.xdo";
    public static final String EXITREENTRY_DAT_FILE_REPORT = "exitReEntryReport.xdo";
    public static final String POSITIONS_REPORT = "PositionsReport.xdo";
    public static final String POSITION_WORKER_REPORT = "PositionWorkerReport.xdo";
    public static final String POSITION_ID_REPORT = "PositionIdReport.xdo";
    public static final String EXCEPTIONAL_REWARD_DAT_FILE_REPORT = "ExceptionalRewardDatReport.xdo";
    public static final String ADJUST_ACCRUAL_BALANCE_REPORT = "AdjustAccrualBalanceReport.xdo";
    public static final String MANAGER_EMPLOYEES_REPORT = "ManagerEmployeesReport.xdo";
    public static final String BANK_ACCOUNT_DATA_REPORT = "Bank_Account_Data_Report.xdo";
    public static final String EDUCATION_CHILDS_REPORT = "EducationNoOfChildrenReport.xdo";
    public static final String ALL_EMPS_AND_MNGR_REPORT = "AllEmployeesAndManagersReport.xdo";
    public static final String EMP_IS_MNGR_REPORT = "Emp_Is_Manager_Report.xdo";
    public static final String LOAN_VALIDATION_REPORT = "LoanSummary.xdo";
    public static final String BUSINESS_TRIP_PERDIEM_REPORT = "BusinessTripReport.xdo";
    private static final String BUDGET_AMOUNT_REPORT="THCSEPCostcenterBudgetBalanceReport.xdo";
    private static final String MOBILE_ALLOWANCE="MobileAllowanceLayout.xdo";
    private static final String BUSINESS_TRIP_ATTACHMENT="Business Trip Attachment.xdo";
    private static final String VACANT_POSITION="Vacant Position.xdo";
    private static final String INVOICE_STATUS="Invoice Status.xdo";
    private static final String BUSINESS_TRIP_WITHDRAWN="Business Trip Withdrawn Date File.xdo";
    private static final String LOCATIONS="Locations.xdo";
    private static final String ADJUST_LOAN_BALANCE="Adjust Loan Balance Deductions.xdo";
    private static final String MOBILE_WITHDRAW="Mobile Allowance Datfile.xdo";
    private static final String LOAN_WITHDRAW="Loan Filedat.xdo";
    public static final String OVERTIME_ALLOW_DAT_WITHDRAW = "Overtime Allowance Datfile.xdo";
    public static final String EXIT_RE_ENTRY_DAT_WITHDRAW = "Exit Re Reentry Datfile.xdo";
    public static final String PRIVILEGES_TO_MR = "Privileges to Manpower Requisition.xdo";
    public static final String ALL_PERSONS_REPORT = "Delegation and username.xdo";
    public static final String TERMINATED_EMPLOYEES = "Terminated Employees.xdo";
    public static final String EXP_CERTIFICATE = "Experience Certificate Layout.xdo";
    public static final String TRAINING_CERTIFICATE = "Training Certificate.xdo";
    public static final String TAMHEER_TRAINING_CERTIFICATE = "Tamheer Certificate.xdo";
    public static final String DEPENDENT_INFORMATION = "THC Children Information for Education Allowance.xdo";
    public static final String MOBILE_ALLOWANCE_AMOUNT = "THC Mobile Allowance Request Report.xdo";
        
    private String REPORT_BASE_PATH ="/Custom/";
    private String REPORT_PASS_BASE_PATH ="/Custom/PAAS/";
    private String REPORT_PASS_THC_CHILD_BASE_PATH ="/Custom/PAAS/THC Child Info/";
//    private String wsdlURL="https://egwo-test.bi.em2.oraclecloud.com/xmlpserver/services/ExternalReportWSSService"; 
      private String wsdlURL="https://egwo-dev1.fa.em2.oraclecloud.com/xmlpserver/services/ExternalReportWSSService"; 
   //private String wsdlURL="https://egwo.fa.em2.oraclecloud.com/xmlpserver/services/ExternalReportWSSService";    
        
//    private String userName="heleraki"; 
//     
//    private String password="Tatweer@123";

//    private String userName="HCM.TRACK"; 
//     
//    private String password="12345678";
    
    private String userName="PaaS.Self Service@tatweer.sa"; 
     
    private String password="PAASTatweer@2020";
   

    public BiReportAccess() {
        super();
        
//        this.wsdlURL=wsdlUrl;
//        this.userName=username;
//        this.password=password;
        
        securityFeatures =
                new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
        externalReportWSSService_Service =
                new ExternalReportWSSService_Service();
        externalReportWSSService =
                externalReportWSSService_Service.getExternalReportWSSService(securityFeatures);
        javax.xml.ws.BindingProvider  wsbp = (javax.xml.ws.BindingProvider )externalReportWSSService;
        Map<String, Object> requestContext = wsbp.getRequestContext();
        requestContext.put( javax.xml.ws.BindingProvider.USERNAME_PROPERTY,
                           this.userName); 
        requestContext.put( javax.xml.ws.BindingProvider.PASSWORD_PROPERTY,
                           this.password); 
        requestContext.put( javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                           this.wsdlURL);
        
        
    }
    
//    private int isValidParam(String paramName,ArrayOfParamNameValue params){
//        if(params==null)return -1;
//        int index=-1;
//        for(ParamNameValue paramV :params.getItem()){
//            index++;
//            if(paramV.getName().equalsIgnoreCase(paramName))return index;
//           
//        }
//        return -1;
//    }

    private byte[] getReportBytes(String report,boolean flattenXml) throws Exception {
        ObjectFactory of = new ObjectFactory();
        ReportRequest reportRequest = of.createReportRequest();
        ReportResponse reportResponse;
        reportRequest.setReportAbsolutePath(report);
        reportRequest.setSizeOfDataChunkDownload(-1);
        reportRequest.setAttributeFormat("xml");
        reportRequest.setFlattenXML(flattenXml);
    
        reportRequest.setByPassCache(true);        
//        ParamNameValues p = externalReportWSSService.getReportParameters(reportRequest);
        
     
        reportResponse =
                externalReportWSSService.runReport(reportRequest,null);
        String data = new String(reportResponse.getReportBytes());
        System.out.println(data);
        return reportResponse.getReportBytes();
    }
    
    private byte[] getReportBytesPDF(String report, Map<String,Object> params,boolean flattenXml) throws Exception {
        ObjectFactory of = new ObjectFactory();
        ReportRequest reportRequest = of.createReportRequest();
        ReportResponse reportResponse;
        reportRequest.setReportAbsolutePath(report);
        reportRequest.setSizeOfDataChunkDownload(-1);
        reportRequest.setAttributeFormat("pdf");
        reportRequest.setFlattenXML(flattenXml);
    
        reportRequest.setByPassCache(true);        
        ParamNameValues p = externalReportWSSService.getReportParameters(reportRequest);
        ArrayOfParamNameValue paramValues=p.getListOfParamNameValues();
        ArrayOfParamNameValue ourParamValues = new ArrayOfParamNameValue();
         if (params != null){
            for (String k : params.keySet()) {
                
                int index =isValidParam( k,paramValues);
                if(index>-1){
                    ParamNameValue currentParam = paramValues.getItem().get(index);
                    ArrayOfString valuesArr = new ArrayOfString();
                    valuesArr.getItem().add(params.get(k).toString());
                    currentParam.setValues(valuesArr);
                    currentParam.setLovLabels(valuesArr);
                    paramValues.getItem().remove(currentParam);
                    paramValues.getItem().add(currentParam);
                    ourParamValues.getItem().add(currentParam);
                }   
            }
    
         reportRequest.setParameterNameValues(ourParamValues);
         }
        reportResponse =
                externalReportWSSService.runReport(reportRequest,null);
        String data = new String(reportResponse.getReportBytes());
//        System.out.println(data);
        return reportResponse.getReportBytes();
    }
    
    private int isValidParam(String paramName,ArrayOfParamNameValue params){
        if(params==null)return -1;
        int index=-1;
        for(ParamNameValue paramV :params.getItem()){
            index++;
            if(paramV.getName().equalsIgnoreCase(paramName))return index;
           
        }
        return -1;
    }
    
    private byte[] getReportBytes(String report, Map<String,Object> params,boolean flattenXml) throws Exception {
        ObjectFactory of = new ObjectFactory();
        ReportRequest reportRequest = of.createReportRequest();
        ReportResponse reportResponse;
        reportRequest.setReportAbsolutePath(report);
        reportRequest.setSizeOfDataChunkDownload(-1);
        reportRequest.setAttributeFormat("xml");
        reportRequest.setFlattenXML(flattenXml);
    
        reportRequest.setByPassCache(true);        
        ParamNameValues p = externalReportWSSService.getReportParameters(reportRequest);
        ArrayOfParamNameValue paramValues=p.getListOfParamNameValues();
        ArrayOfParamNameValue ourParamValues = new ArrayOfParamNameValue();
         if (params != null){
            for (String k : params.keySet()) {
                
                int index =isValidParam( k,paramValues);
                if(index>-1){
                    ParamNameValue currentParam = paramValues.getItem().get(index);
                    ArrayOfString valuesArr = new ArrayOfString();
                    valuesArr.getItem().add(params.get(k).toString());
                    currentParam.setValues(valuesArr);
                    currentParam.setLovLabels(valuesArr);
                    paramValues.getItem().remove(currentParam);
                    paramValues.getItem().add(currentParam);
                    ourParamValues.getItem().add(currentParam);
                }   
            }
    
         reportRequest.setParameterNameValues(ourParamValues);
         }
        reportResponse =
                externalReportWSSService.runReport(reportRequest,null);
        String data = new String(reportResponse.getReportBytes());
        System.out.println(data);
        return reportResponse.getReportBytes();
    }
    
    
  
    
    private TatweerCollection parseXml(byte[] xml,Class type)throws Exception{
        
        String output = new String(xml);
        StringReader sr = new StringReader(output);
        
        JAXBContext jaxbContext = JAXBContext.newInstance(type);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();               
        TatweerCollection result = (TatweerCollection) jaxbUnmarshaller.unmarshal(sr);
        return result;
    }


    public List<Map> getAllEmpsAndMngrsData() throws Exception {
       
                
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+ALL_EMPS_AND_MNGR_REPORT,
                                true); 
        AllEmpsAndMngrsCollection allEmpsAndMngrs = (AllEmpsAndMngrsCollection) this.parseXml(results,  AllEmpsAndMngrsCollection.class);
        int size=allEmpsAndMngrs.getAllEmpsAndMngrs().size();
        System.err.println("Size is >> "+allEmpsAndMngrs.getAllEmpsAndMngrs().size());
        Map department ;
        List<Map> listOfEmpsAndMngrs=new ArrayList<Map>();
        if(allEmpsAndMngrs!=null && allEmpsAndMngrs.getAllEmpsAndMngrs().size()>0){
            for(int i =0;i<size;i++){
                    department = new HashMap();
                    department.put("PERSON_NUMBER",allEmpsAndMngrs.getAllEmpsAndMngrs().get(i).getPERSON_NUMBER());
                    department.put("EMPLOYEE_NAME",allEmpsAndMngrs.getAllEmpsAndMngrs().get(i).getEMPLOYEE_NAME());
                    department.put("MANAGER_NUMBER",allEmpsAndMngrs.getAllEmpsAndMngrs().get(i).getMANAGER_NUMBER());
                    department.put("MANAGER_NAME",allEmpsAndMngrs.getAllEmpsAndMngrs().get(i).getMANAGER_NAME());
                    listOfEmpsAndMngrs.add(department);
    
                
                
                }
            
        }
        return  listOfEmpsAndMngrs;
    }

    public List<Map> getEduNoOfChildren(String p_emp_no) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("p_emp_no", p_emp_no );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+EDUCATION_CHILDS_REPORT,params,
                                true); 
        EducationNoOfChildrenCollection EduNoOfChildsColl = (EducationNoOfChildrenCollection) this.parseXml(results,  EducationNoOfChildrenCollection.class);
        int size=EduNoOfChildsColl.getEduNoOfChilds().size();
        System.err.println("Size is >> "+EduNoOfChildsColl.getEduNoOfChilds().size());
        Map empChildData ;
        List<Map> listOfEduNoOfChilds=new ArrayList<Map>();
        if(EduNoOfChildsColl!=null && EduNoOfChildsColl.getEduNoOfChilds().size()>0){
            for(int i =0;i<size;i++){
                    empChildData = new HashMap();
                    empChildData.put("PERSON_NUMBER",EduNoOfChildsColl.getEduNoOfChilds().get(i).getPERSON_NUMBER());
                    empChildData.put("DISPLAY_NAME",EduNoOfChildsColl.getEduNoOfChilds().get(i).getDISPLAY_NAME());
                    empChildData.put("MARITAL_STATUS",EduNoOfChildsColl.getEduNoOfChilds().get(i).getMARITAL_STATUS());
                    empChildData.put("NUMBER_OF_CHILDS",EduNoOfChildsColl.getEduNoOfChilds().get(i).getNUMBER_OF_CHILDS());
                    

                    listOfEduNoOfChilds.add(empChildData);
    
                
                
                }
            
        }
        
        return  listOfEduNoOfChilds;
    } 
    public List<Map> getChildDetails(String personNo) throws Exception {   
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("PNUM", personNo); 
        
         byte[] results =
             this.getReportBytes(REPORT_PASS_THC_CHILD_BASE_PATH + DEPENDENT_INFORMATION , params ,true); 
         
         ChildDetailsCollection collections = (ChildDetailsCollection) this.parseXml(results,  ChildDetailsCollection.class);
         int size = collections.getChildDetails().size(); 
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("CHILD_NAME",collections.getChildDetails().get(i).getChildName());
                     tempMap.put("CHILD_NATIONAL_ID",collections.getChildDetails().get(i).getChildNationalId());
                    
                     listMap.add(tempMap);     
                }             
         }
         return  listMap;
     } 

    public List<Map> getDepartmentsData() throws Exception {
       
        	
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+DEPARTMENTS_REPORT,
                                true); 
        DepartmentCollection departments = (DepartmentCollection) this.parseXml(results,  DepartmentCollection.class);
        int size=departments.getDepartments().size();
        System.err.println("Size is >> "+departments.getDepartments().size());
        Map department ;
        List<Map> listOfDepartments=new ArrayList<Map>();
        if(departments!=null && departments.getDepartments().size()>0){
            for(int i =0;i<size;i++){
                    department = new HashMap();
                    department.put("ORGANIZATION_ID",departments.getDepartments().get(i).getORGANIZATION_ID());
                    department.put("NAME",departments.getDepartments().get(i).getNAME());
                    department.put("SEGMENT4",departments.getDepartments().get(i).getSEGMENT4());
                    department.put("SEGMENT2",departments.getDepartments().get(i).getSEGMENT2());
                    department.put("SEGMENT3",departments.getDepartments().get(i).getSEGMENT3());
                    listOfDepartments.add(department);
                }
            
        }
        return  listOfDepartments;
    }
    
    
    public List<Map> getManagerOfDepartmentData(String dept_name) throws Exception {
        
        System.err.println("===> getManagerOfDepartmentData called with==="+dept_name);
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dept_name", dept_name + "");
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+MANAGER_OF_DEPT_REPORT,params,
                                true); 
        ManagerOfDeptCollection managerCollection = (ManagerOfDeptCollection) this.parseXml(results,  ManagerOfDeptCollection.class);
        int size=managerCollection.getManagerOfDept().size();
        System.err.println("Size is >> "+managerCollection.getManagerOfDept().size());
        Map managerData ;
        List<Map> listOfManagers=new ArrayList<Map>();
        if(managerCollection!=null && managerCollection.getManagerOfDept().size()>0){
            for(int i =0;i<size;i++){
                    managerData = new HashMap();
                    managerData.put("ORGANIZATION_ID",managerCollection.getManagerOfDept().get(i).getORGANIZATION_ID());
                    managerData.put("NAME",managerCollection.getManagerOfDept().get(i).getNAME());
                    managerData.put("PERSON_NUMBER",managerCollection.getManagerOfDept().get(i).getPERSON_NUMBER());
                    managerData.put("PERSON_ID",managerCollection.getManagerOfDept().get(i).getPERSON_ID());
                    managerData.put("FULL_NAME",managerCollection.getManagerOfDept().get(i).getFULL_NAME());
                    managerData.put("DISPLAY_NAME",managerCollection.getManagerOfDept().get(i).getDISPLAY_NAME());
                    managerData.put("EMAIL_ADDRESS",managerCollection.getManagerOfDept().get(i).getEMAIL_ADDRESS());
                    
                
                    listOfManagers.add(managerData);
   
                
                
                }
            
        }
        
        return  listOfManagers;
    }
    
    public List<Map> getEmpIsMngrData(String bindPersonNumber) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bindPersonNumber", bindPersonNumber );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+EMP_IS_MNGR_REPORT,params,
                                true); 
        EmpIsMngrCollection empIsMngrCollection = (EmpIsMngrCollection) this.parseXml(results,  EmpIsMngrCollection.class);
        int size=empIsMngrCollection.getEmp().size();
        System.err.println("Size is >> "+empIsMngrCollection.getEmp().size());
        Map empIsMngrData ;
        List<Map> listOfEmpIsMngr=new ArrayList<Map>();
        if(empIsMngrCollection!=null && empIsMngrCollection.getEmp().size()>0){
            for(int i =0;i<size;i++){
                    empIsMngrData = new HashMap();
                    
                    
                    empIsMngrData.put("PERSON_NUMBER",empIsMngrCollection.getEmp().get(i).getPERSON_NUMBER());
                    empIsMngrData.put("MANAGER_FLAG",empIsMngrCollection.getEmp().get(i).getMANAGER_FLAG());
                    
                
                    listOfEmpIsMngr.add(empIsMngrData);
    
                
                
                }
            
        }
        
        return  listOfEmpIsMngr;
    }
    
    
    public List<Map> getPersonAccrualData(String p_date,String bindEmpNo) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("p_date", p_date );
        params.put("bindEmpNo", bindEmpNo );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+ADJUST_ACCRUAL_BALANCE_REPORT,params,
                                true); 
        PersonAccrualCollection personAccrual = (PersonAccrualCollection) this.parseXml(results,  PersonAccrualCollection.class);
        int size=personAccrual.getPersonAccrual().size();
        System.err.println("Size is >> "+personAccrual.getPersonAccrual().size());
        Map personAccrualData ;
        List<Map> listOfPersonsAccrual=new ArrayList<Map>();
        if(personAccrual!=null && personAccrual.getPersonAccrual().size()>0){
            for(int i =0;i<size;i++){
                    personAccrualData = new HashMap();
                    personAccrualData.put("PERSON_NUMBER",personAccrual.getPersonAccrual().get(i).getPERSON_NUMBER());
                    personAccrualData.put("ASSIGNMENT_NUMBER",personAccrual.getPersonAccrual().get(i).getASSIGNMENT_NUMBER());
                    personAccrualData.put("END_BAL",personAccrual.getPersonAccrual().get(i).getEND_BAL());
                    personAccrualData.put("PER_ACCRUAL_ENTRY_ID",personAccrual.getPersonAccrual().get(i).getPER_ACCRUAL_ENTRY_ID());
                    personAccrualData.put("ACCRUAL_PERIOD",personAccrual.getPersonAccrual().get(i).getACCRUAL_PERIOD());
                    
                    
                
                    listOfPersonsAccrual.add(personAccrualData);
    
                
                
                }
            
        }
        
        return  listOfPersonsAccrual;
    }
    
    public String callActionReasoneReport(String personNumber) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("P_EMP_NO", personNumber );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+ACTION_REASON_REPORT,params,
                                true); 
        ActionReasonCollection actionReason = (ActionReasonCollection) this.parseXml(results,  ActionReasonCollection.class);
        int size=actionReason.getActionReason().size();
        System.err.println("Size is >> "+actionReason.getActionReason().size());
        Map actionReasonData ;
        List<Map> listOfActionReason=new ArrayList<Map>();
        if(actionReason!=null && actionReason.getActionReason().size()>0){
            for(int i =0;i<size;i++){
                    actionReasonData = new HashMap();
                    actionReasonData.put("PERSON_ID",actionReason.getActionReason().get(i).getPERSON_ID());
                    actionReasonData.put("FULL_NAME",actionReason.getActionReason().get(i).getFULL_NAME());
                    actionReasonData.put("PERSON_NUMBER",actionReason.getActionReason().get(i).getPERSON_NUMBER());
                    actionReasonData.put("ACTION_REASON_CODE",actionReason.getActionReason().get(i).getACTION_REASON_CODE());
                    
                    
                
                    listOfActionReason.add(actionReasonData);
    
                
                
                }
            
        }
        return  actionReason.getActionReason().get(0).getACTION_REASON_CODE().toString();
    }
    
    
    public List<Map> getPersonByPostionReport(String postion) throws Exception {
        
        System.err.println("===> getPersonByPostionReport called with==="+postion);
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("position_name", postion );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+PERSON_BY_POSITION_REPORT,params,
                                true); 
        PersonByPositionCollection person = (PersonByPositionCollection) this.parseXml(results,  PersonByPositionCollection.class);
        int size=person.getPersonByPosition().size();
        System.err.println("Size is >> "+person.getPersonByPosition().size());
        Map personData ;
        List<Map> listOfPersons=new ArrayList<Map>();
        if(person!=null && person.getPersonByPosition().size()>0){
            for(int i =0;i<size;i++){
                    personData = new HashMap();
                    personData.put("PERSON_NUMBER",person.getPersonByPosition().get(i).getPERSON_NUMBER());
                    personData.put("DISPLAY_NAME",person.getPersonByPosition().get(i).getDISPLAY_NAME());
                    personData.put("POSITION_CODE",person.getPersonByPosition().get(i).getPOSITION_CODE());
                    personData.put("POSITION_NAME",person.getPersonByPosition().get(i).getPOSITION_NAME());
                    personData.put("EMAIL_ADDRESS",person.getPersonByPosition().get(i).getEMAIL_ADDRESS());
                    
                
                    listOfPersons.add(personData);
    
                
                
                }
            
        }
        return  listOfPersons;
    }
    
    
    
    public List<Map> getCodeCombinationId(String bindStatic1,String bindHR1,String bindHR2,String bindHR3,String bindFIN,String bindStatic2,String bindStatic3,String bindStatic4) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("bindStatic1", bindStatic1 + "");
        params.put("bindHR1", bindHR1 + "");
        params.put("bindHR2", bindHR2 + "");
        params.put("bindHR3", bindHR3 + "");
        params.put("bindFIN", bindFIN + "");
        params.put("bindStatic2", bindStatic2 + "");
        params.put("bindStatic3", bindStatic3 + "");
        params.put("bindStatic4", bindStatic4 + "");
        
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+CODE_COMBINATION_ID_REPORT,params,
                                true); 
        CodeCombinationIDCollection codeCombinationIdColl = (CodeCombinationIDCollection) this.parseXml(results,  CodeCombinationIDCollection.class);
        int size=codeCombinationIdColl.getCodeCombinationId().size();
        System.err.println("Size is >> "+codeCombinationIdColl.getCodeCombinationId().size());
        Map codeCompinationData ;
        List<Map> listOfCodeCombinId=new ArrayList<Map>();
        if(codeCombinationIdColl!=null && codeCombinationIdColl.getCodeCombinationId().size()>0){
            for(int i =0;i<size;i++){
                    codeCompinationData = new HashMap();
                    codeCompinationData.put("CODE_COMBINATION_ID",codeCombinationIdColl.getCodeCombinationId().get(i).getCODE_COMBINATION_ID());
                    
                    
                
                    listOfCodeCombinId.add(codeCompinationData);
    
                
                
                }
            
        }
        return  listOfCodeCombinId;
    }
    
    
    
    public List<Map> getHCMSegments(String bind_emp_number) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("bind_emp_number", bind_emp_number);
        
        
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+HCM_SEGMENTS_REPORT,params,
                                true); 
        HCMSegmentsCollection hcmSegmentsColl = (HCMSegmentsCollection) this.parseXml(results,  HCMSegmentsCollection.class);
        int size=hcmSegmentsColl.getHCMSegments().size();
        System.err.println("Size is >> "+hcmSegmentsColl.getHCMSegments().size());
        Map hcmSegmentsData ;
        List<Map> listOfHCMSegments=new ArrayList<Map>();
        if(hcmSegmentsColl!=null && hcmSegmentsColl.getHCMSegments().size()>0){
            for(int i =0;i<size;i++){
                    hcmSegmentsData = new HashMap();
                    hcmSegmentsData.put("SEGMENT2",hcmSegmentsColl.getHCMSegments().get(i).getSEGMENT2());
                    hcmSegmentsData.put("SEGMENT3",hcmSegmentsColl.getHCMSegments().get(i).getSEGMENT3());
                    hcmSegmentsData.put("SEGMENT4",hcmSegmentsColl.getHCMSegments().get(i).getSEGMENT4());
                    
                    
                
                    listOfHCMSegments.add(hcmSegmentsData);
    
                
                
                }
            
        }
        return  listOfHCMSegments;
    }
    
    
    public List<Map> getEmpSalary(String bindPersonNumber) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("bindPersonNumber", bindPersonNumber);
        System.err.println("Salary Person Number is >> "+bindPersonNumber);
        
        byte[] results =
            this.getReportBytes(REPORT_PASS_BASE_PATH+EMPLOYEE_SALARY_REPORT,params,
                                true); 
        EmpSalaryCollection empSalaryColl = (EmpSalaryCollection) this.parseXml(results,  EmpSalaryCollection.class);
        int size=empSalaryColl.getEmpSalaries().size();
        System.err.println("Size is >> "+empSalaryColl.getEmpSalaries().size());
        Map empSalaryData ;
        List<Map> listOfEmpSalary=new ArrayList<Map>();
        listOfEmpSalary.clear();
        if(empSalaryColl!=null && empSalaryColl.getEmpSalaries().size()>0){
            for(int i =0;i<size;i++){
                    empSalaryData = new HashMap();
                    empSalaryData.put("PERSON_NUMBER",empSalaryColl.getEmpSalaries().get(i).getPERSON_NUMBER());
                    empSalaryData.put("PERSON_NAME",empSalaryColl.getEmpSalaries().get(i).getPERSON_NAME());
                    empSalaryData.put("SALARY_AMOUNT",empSalaryColl.getEmpSalaries().get(i).getSALARY_AMOUNT());
                    System.err.println("Salary empSalaryData is >> "+empSalaryData.size());
                    
                
                    listOfEmpSalary.add(empSalaryData);
                   
    
                
                
                }
            
        }
        
        System.err.println("Salary listOfEmpSalary is >> "+listOfEmpSalary.size());
        return  listOfEmpSalary;
    }
    
    
    public List<Map> getOvertimeDatFileData(String p_emp_no) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("p_emp_no", p_emp_no);
//        params.put("p_effective_date", p_effective_date);
        
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+OVERTIME_DAT_FILE_REPORT,params,
                                true); 
        OvertimeDatFileDataCollection overtimeDatFileDataCollection = (OvertimeDatFileDataCollection) this.parseXml(results,  OvertimeDatFileDataCollection.class);
        int size=overtimeDatFileDataCollection.getOvertimeDatFileData().size();
        System.err.println("Size is >> "+overtimeDatFileDataCollection.getOvertimeDatFileData().size());
        Map overtimeDatFileData ;
        List<Map> listOfOvertimeDatFileData=new ArrayList<Map>();
        if(overtimeDatFileDataCollection!=null && overtimeDatFileDataCollection.getOvertimeDatFileData().size()>0){
            for(int i =0;i<size;i++){
                    overtimeDatFileData = new HashMap();
                    overtimeDatFileData.put("PERSON_NUMBER",overtimeDatFileDataCollection.getOvertimeDatFileData().get(i).getPerson_number());
                    overtimeDatFileData.put("ASSIGNMENT_NUMBER",overtimeDatFileDataCollection.getOvertimeDatFileData().get(i).getAssignment_number());
//                    overtimeDatFileData.put("ENTRYTYPE",overtimeDatFileDataCollection.getOvertimeDatFileData().get(i).getEntrytype());
                    overtimeDatFileData.put("MULTIPLEENTRYCOUNT",overtimeDatFileDataCollection.getOvertimeDatFileData().get(i).getMultipleEntryCount());
                    
                    
                
                    listOfOvertimeDatFileData.add(overtimeDatFileData);
    
                
                
                }
            
        }
        return  listOfOvertimeDatFileData;
    }
    
    public List<Map> getOvertimeDatWithdrawFileData(String p_emp_no, String p_effective_date) throws Exception {
        System.err.println("p_emp_no= "+p_emp_no+",  p_effective_date= "+p_effective_date);
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("p_emp_no", p_emp_no);
        params.put("p_effective_date", p_effective_date);
        
        byte[] results =
            this.getReportBytes(REPORT_PASS_BASE_PATH+OVERTIME_ALLOW_DAT_WITHDRAW,params,
                                true); 
        OvertimeDatWithdrawCollection overtimeDatFileDataCollection = (OvertimeDatWithdrawCollection) this.parseXml(results,  OvertimeDatWithdrawCollection.class);
        int size=overtimeDatFileDataCollection.getOvertimeDatWithdraw().size();
        System.err.println("Size is >> "+overtimeDatFileDataCollection.getOvertimeDatWithdraw().size());
        Map overtimeDatFileData ;
        List<Map> listOfOvertimeDatFileData=new ArrayList<Map>();
        if(overtimeDatFileDataCollection!=null && overtimeDatFileDataCollection.getOvertimeDatWithdraw().size()>0){
            for(int i =0;i<size;i++){
                    overtimeDatFileData = new HashMap();
                    overtimeDatFileData.put("PERSON_NUMBER",overtimeDatFileDataCollection.getOvertimeDatWithdraw().get(i).getPERSON_NUMBER());
                    overtimeDatFileData.put("ASSIGNMENT_NUMBER",overtimeDatFileDataCollection.getOvertimeDatWithdraw().get(i).getASSIGNMENT_NUMBER()); 
                    overtimeDatFileData.put("MULTIPLEENTRYCOUNT",overtimeDatFileDataCollection.getOvertimeDatWithdraw().get(i).getMULTIPLEENTRYCOUNT());
                    overtimeDatFileData.put("EFFECTIVE_START_DATE",overtimeDatFileDataCollection.getOvertimeDatWithdraw().get(i).getEFFECTIVE_START_DATE());
                    overtimeDatFileData.put("EFFECTIVE_END_DATE",overtimeDatFileDataCollection.getOvertimeDatWithdraw().get(i).getEFFECTIVE_END_DATE());
                    overtimeDatFileData.put("SCREENENTRYVALUE",overtimeDatFileDataCollection.getOvertimeDatWithdraw().get(i).getSCREENENTRYVALUE());
                     
                    listOfOvertimeDatFileData.add(overtimeDatFileData);
                     }
            
        }
        return  listOfOvertimeDatFileData;
    }
    
    
    public List<Map> getExceptionalRewardDatFileData(String p_emp_no) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("p_emp_no", p_emp_no);
    //        params.put("p_effective_date", p_effective_date);
        
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+EXCEPTIONAL_REWARD_DAT_FILE_REPORT,params,
                                true); 
        OvertimeDatFileDataCollection exceptionalRewardDatFileDataCollection = (OvertimeDatFileDataCollection) this.parseXml(results,  OvertimeDatFileDataCollection.class);
        int size=exceptionalRewardDatFileDataCollection.getOvertimeDatFileData().size();
        System.err.println("Size is >> "+exceptionalRewardDatFileDataCollection.getOvertimeDatFileData().size());
        Map exceptionalRewardDatFileData ;
        List<Map> listOfExceptionalRewardDatFileData=new ArrayList<Map>();
        if(exceptionalRewardDatFileDataCollection!=null && exceptionalRewardDatFileDataCollection.getOvertimeDatFileData().size()>0){
            for(int i =0;i<size;i++){
                    exceptionalRewardDatFileData = new HashMap();
                    exceptionalRewardDatFileData.put("PERSON_NUMBER",exceptionalRewardDatFileDataCollection.getOvertimeDatFileData().get(i).getPerson_number());
                    exceptionalRewardDatFileData.put("ASSIGNMENT_NUMBER",exceptionalRewardDatFileDataCollection.getOvertimeDatFileData().get(i).getAssignment_number());
    //                    overtimeDatFileData.put("ENTRYTYPE",overtimeDatFileDataCollection.getOvertimeDatFileData().get(i).getEntrytype());
                    exceptionalRewardDatFileData.put("MULTIPLEENTRYCOUNT",exceptionalRewardDatFileDataCollection.getOvertimeDatFileData().get(i).getMultipleEntryCount());
                    
                    
                
                    listOfExceptionalRewardDatFileData.add(exceptionalRewardDatFileData);
    
                
                
                }
            
        }
        return  listOfExceptionalRewardDatFileData;
    }
    
    public List<Map> getExitReEntryDatFileData(String p_emp_no) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("p_emp_no", p_emp_no);
        
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+EXITREENTRY_DAT_FILE_REPORT,params,
                                true); 
        ExitReEntryDatFileDataCollection exitReEntryDatFileDataCollection = (ExitReEntryDatFileDataCollection) this.parseXml(results,  ExitReEntryDatFileDataCollection.class);
        int size=exitReEntryDatFileDataCollection.getExitReEntryDatFileData().size();
        System.err.println("Size is >> "+exitReEntryDatFileDataCollection.getExitReEntryDatFileData().size());
        Map exitReEntryDatFileData ;
        List<Map> listOfExitReEntryDatFileData=new ArrayList<Map>();
        if(exitReEntryDatFileDataCollection!=null && exitReEntryDatFileDataCollection.getExitReEntryDatFileData().size()>0){
            for(int i =0;i<size;i++){
                    exitReEntryDatFileData = new HashMap();
                    exitReEntryDatFileData.put("PERSON_NUMBER",exitReEntryDatFileDataCollection.getExitReEntryDatFileData().get(i).getPerson_number());
                    exitReEntryDatFileData.put("ASSIGNMENT_NUMBER",exitReEntryDatFileDataCollection.getExitReEntryDatFileData().get(i).getAssignment_number());
                    exitReEntryDatFileData.put("MULTIPLEENTRYCOUNT",exitReEntryDatFileDataCollection.getExitReEntryDatFileData().get(i).getMultipleEntryCount());
                    
                    
                
                    listOfExitReEntryDatFileData.add(exitReEntryDatFileData);
    
                
                
                }
            
        }
        return  listOfExitReEntryDatFileData;
    }
    
    public List<Map> getLoanDatFileData(String p_emp_no) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("p_emp_no", p_emp_no);
        
        
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+LOAN_DAT_FILE_REPORT,params,
                                true); 
        LoanDatFileDataCollection loanDatFileDataCollection = (LoanDatFileDataCollection) this.parseXml(results,  LoanDatFileDataCollection.class);
        int size=loanDatFileDataCollection.getLoanDatFileData().size();
        System.err.println("Size is >> "+loanDatFileDataCollection.getLoanDatFileData().size());
        Map loanDatFileData ;
        List<Map> listOfLoanDatFileData=new ArrayList<Map>();
        if(loanDatFileDataCollection!=null && loanDatFileDataCollection.getLoanDatFileData().size()>0){
            for(int i =0;i<size;i++){
                    loanDatFileData = new HashMap();
                    loanDatFileData.put("PERSON_NUMBER",loanDatFileDataCollection.getLoanDatFileData().get(i).getPerson_number());
                    loanDatFileData.put("ASSIGNMENT_NUMBER",loanDatFileDataCollection.getLoanDatFileData().get(i).getAssignment_number());
                    
                    listOfLoanDatFileData.add(loanDatFileData);
    
                
                
                }
            
        }
        return  listOfLoanDatFileData;
    }
    
    
    public List<Map> getGradesData() throws Exception {
       
                
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+GRADES_REPORT,
                                true); 
        GradesCollection gradesColl = (GradesCollection) this.parseXml(results,  GradesCollection.class);
        int size=gradesColl.getGrades().size();
        System.err.println("Size is >> "+gradesColl.getGrades().size());
        Map grade ;
        List<Map> listOfGrades=new ArrayList<Map>();
        if(gradesColl!=null && gradesColl.getGrades().size()>0){
            for(int i =0;i<size;i++){
                    grade = new HashMap();
                    grade.put("GRADE_ID",gradesColl.getGrades().get(i).getGRADE_ID());
                    grade.put("NAME",gradesColl.getGrades().get(i).getNAME());
                    
                    listOfGrades.add(grade);
    
                
                
                }
            
        }
        return  listOfGrades;
    }
    
    
    
    public List<Map> getJobsData() throws Exception {
       
                
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+JOBS_REPORT,
                                true); 
        JobsCollection jobsCollection = (JobsCollection) this.parseXml(results,  JobsCollection.class);
        int size=jobsCollection.getJobs().size();
        System.err.println("Size is >> "+jobsCollection.getJobs().size());
        Map job ;
        List<Map> listOfJobs=new ArrayList<Map>();
        if(jobsCollection!=null && jobsCollection.getJobs().size()>0){
            for(int i =0;i<size;i++){
                    job = new HashMap();
                    job.put("JOB_ID",jobsCollection.getJobs().get(i).getJOB_ID());
                    job.put("NAME",jobsCollection.getJobs().get(i).getNAME());
                    
                    listOfJobs.add(job);
    
                
                
                }
            
        }
        return  listOfJobs;
    }
    
    
    
    public List<Map> getDeptEmployees(String p_org_name) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("p_org_name", p_org_name );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+DEPT_EMPLOYEES_REPORT,params,
                                true); 
        DeptEmployeesCollection deptEmployeesColl = (DeptEmployeesCollection) this.parseXml(results,  DeptEmployeesCollection.class);
        int size=deptEmployeesColl.getDeptEmployees().size();
        System.err.println("Size is >> "+deptEmployeesColl.getDeptEmployees().size());
        Map empData ;
        List<Map> listOfEmployees=new ArrayList<Map>();
        if(deptEmployeesColl!=null && deptEmployeesColl.getDeptEmployees().size()>0){
            for(int i =0;i<size;i++){
                    empData = new HashMap();
                    empData.put("PERSON_NUMBER",deptEmployeesColl.getDeptEmployees().get(i).getPERSON_NUMBER());
                    empData.put("DISPLAY_NAME",deptEmployeesColl.getDeptEmployees().get(i).getDISPLAY_NAME());
                    empData.put("PERSON_ID",deptEmployeesColl.getDeptEmployees().get(i).getPERSON_ID());

                    listOfEmployees.add(empData);
    
                
                
                }
            
        }
        
        return  listOfEmployees;
    }
    
    
    
    public List<Map> getMngrEmployees(String bindMngrNo) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bindMngrNo", bindMngrNo );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+MANAGER_EMPLOYEES_REPORT,params,
                                true); 
        ManagerEmployeesCollection mngrEmployeesColl = (ManagerEmployeesCollection) this.parseXml(results,  ManagerEmployeesCollection.class);
        int size=mngrEmployeesColl.getMngrEmployees().size();
        System.err.println("Size is >> "+mngrEmployeesColl.getMngrEmployees().size());
        Map empData ;
        List<Map> listOfEmployees=new ArrayList<Map>();
        if(mngrEmployeesColl!=null && mngrEmployeesColl.getMngrEmployees().size()>0){
            for(int i =0;i<size;i++){
                    empData = new HashMap();
                    empData.put("PERSON_NUMBER",mngrEmployeesColl.getMngrEmployees().get(i).getPERSON_NUMBER());
                    empData.put("DISPLAY_NAME",mngrEmployeesColl.getMngrEmployees().get(i).getDISPLAY_NAME());
                    empData.put("MANAGER_NUMBER",mngrEmployeesColl.getMngrEmployees().get(i).getMANAGER_NUMBER());
                    empData.put("SALARY_AMOUNT",mngrEmployeesColl.getMngrEmployees().get(i).getSALARY_AMOUNT());

                    listOfEmployees.add(empData);
    
                
                
                }
            
        }
        
        return  listOfEmployees;
    }
    
    public List<Map> getEmployeeChilds(String p_emp_no) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("p_emp_no", p_emp_no );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+EMPLOYEE_CHILDS_REPORT,params,
                                true); 
        EmployeeChildsCollection empChildsColl = (EmployeeChildsCollection) this.parseXml(results,  EmployeeChildsCollection.class);
        int size=empChildsColl.getEmpChilds().size();
        System.err.println("Size is >> "+empChildsColl.getEmpChilds().size());
        Map empChildData ;
        List<Map> listOfEmployeeChilds=new ArrayList<Map>();
        if(empChildsColl!=null && empChildsColl.getEmpChilds().size()>0){
            for(int i =0;i<size;i++){
                    empChildData = new HashMap();
                    empChildData.put("PERSON_NUMBER",empChildsColl.getEmpChilds().get(i).getPERSON_NUMBER());
                    empChildData.put("HIRE_DATE",empChildsColl.getEmpChilds().get(i).getHIRE_DATE());
                    empChildData.put("MARITAL_STATUS",empChildsColl.getEmpChilds().get(i).getMARITAL_STATUS());
                    empChildData.put("NUMBER_OF_CHILDS",empChildsColl.getEmpChilds().get(i).getNUMBER_OF_CHILDS());
                    empChildData.put("NATIONALITY",empChildsColl.getEmpChilds().get(i).getNATIONALITY());

                    listOfEmployeeChilds.add(empChildData);
    
                
                
                }
            
        }
        
        return  listOfEmployeeChilds;
    }
    

    public List<Map> getBankAccountData(String PersonNumber) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("PersonNumber", PersonNumber );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+BANK_ACCOUNT_DATA_REPORT,params,
                                true); 
        BankAccountDataCollection bankAccountDataColl = (BankAccountDataCollection) this.parseXml(results,  BankAccountDataCollection.class);
        int size=bankAccountDataColl.getListOfBankAccounts().size();
        System.err.println("Size is >> "+bankAccountDataColl.getListOfBankAccounts().size());
        Map bankAccountData ;
        List<Map> listOfBankAccountsData=new ArrayList<Map>();
        if(bankAccountDataColl!=null && bankAccountDataColl.getListOfBankAccounts().size()>0){
            for(int i =0;i<size;i++){
                    bankAccountData = new HashMap();
                    bankAccountData.put("PERSON_NUMBER",bankAccountDataColl.getListOfBankAccounts().get(i).getPERSON_NUMBER());
                    bankAccountData.put("FULL_NAME",bankAccountDataColl.getListOfBankAccounts().get(i).getFULL_NAME());
                    bankAccountData.put("ID",bankAccountDataColl.getListOfBankAccounts().get(i).getID());
                    bankAccountData.put("BANK_ACCOUNT_NUMBER",bankAccountDataColl.getListOfBankAccounts().get(i).getBANK_ACCOUNT_NUMBER());
                    bankAccountData.put("IBAN_NUMBER",bankAccountDataColl.getListOfBankAccounts().get(i).getIBAN_NUMBER());
                    bankAccountData.put("BANK_NAME",bankAccountDataColl.getListOfBankAccounts().get(i).getBANK_NAME());
                    bankAccountData.put("Employee_Bank_ID",bankAccountDataColl.getListOfBankAccounts().get(i).getEmployee_Bank_ID());

                    listOfBankAccountsData.add(bankAccountData);
    
                
                
                }
            
        }
        
        return  listOfBankAccountsData;
    }
    
    
    /*
     * Mobile report - Getting mobile allowance from report
     */
    public List<Map> getMobileAllowanceAmount(String personNo) throws Exception {  
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("PNum", personNo); 
         
         byte[] results = this.getReportBytes(REPORT_PASS_BASE_PATH+ MOBILE_ALLOWANCE_AMOUNT, params, true); 
         MobileAllowanceDetailsCollection collections = (MobileAllowanceDetailsCollection) this.parseXml(results,  MobileAllowanceDetailsCollection.class);
         int size = collections.getMobileAllowanceDetails().size();
         System.err.println("Size is.. >> "+ size);
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("PERSON_NUMBER",collections.getMobileAllowanceDetails().get(i).getPERSON_NUMBER());
                     tempMap.put("GRADE_CODE",collections.getMobileAllowanceDetails().get(i).getGRADE_CODE());
                     tempMap.put("AMOUNT",collections.getMobileAllowanceDetails().get(i).getAMOUNT());

                     listMap.add(tempMap);     
                }             
         }
         return  listMap;
    }
    
    public List<Map> getPositionWorkerData(String bindPersonNo) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bindPersonNo", bindPersonNo );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+POSITION_WORKER_REPORT,params,
                                true); 
        PositionWorkerCollection positionWorkerColl = (PositionWorkerCollection) this.parseXml(results,  PositionWorkerCollection.class);
        int size=positionWorkerColl.getPositionWorkers().size();
        System.err.println("Size is >> "+positionWorkerColl.getPositionWorkers().size());
        Map positionWorkerData ;
        List<Map> listOfpositionWorkers=new ArrayList<Map>();
        if(positionWorkerColl!=null && positionWorkerColl.getPositionWorkers().size()>0){
            for(int i =0;i<size;i++){
                    positionWorkerData = new HashMap();
                    positionWorkerData.put("PERSON_NUMBER",positionWorkerColl.getPositionWorkers().get(i).getPERSON_NUMBER());
                    positionWorkerData.put("WORKER_TYPE",positionWorkerColl.getPositionWorkers().get(i).getWORKER_TYPE());
                    positionWorkerData.put("WORK_TERM_NUMBER",positionWorkerColl.getPositionWorkers().get(i).getWORK_TERM_NUMBER());
                    positionWorkerData.put("ASSIGNMENT_ID",positionWorkerColl.getPositionWorkers().get(i).getASSIGNMENT_ID());
                    positionWorkerData.put("ASSIGNMENT_NUMBER",positionWorkerColl.getPositionWorkers().get(i).getASSIGNMENT_NUMBER());
                    positionWorkerData.put("ACTION_CODE",positionWorkerColl.getPositionWorkers().get(i).getACTION_CODE());
                    positionWorkerData.put("ASSIGNMENT_TYPE",positionWorkerColl.getPositionWorkers().get(i).getASSIGNMENT_TYPE());
                    positionWorkerData.put("EFFECTIVE_LATEST_CHANGE",positionWorkerColl.getPositionWorkers().get(i).getEFFECTIVE_LATEST_CHANGE());
                    positionWorkerData.put("GRADE_ID",positionWorkerColl.getPositionWorkers().get(i).getGRADE_ID());
                    positionWorkerData.put("JOB_ID",positionWorkerColl.getPositionWorkers().get(i).getJOB_ID());
                    positionWorkerData.put("LOCATION_ID",positionWorkerColl.getPositionWorkers().get(i).getLOCATION_ID());
                    positionWorkerData.put("ORGANIZATION_ID",positionWorkerColl.getPositionWorkers().get(i).getORGANIZATION_ID());
                    positionWorkerData.put("EFFECTIVE_SEQUENCE",positionWorkerColl.getPositionWorkers().get(i).getEFFECTIVE_SEQUENCE());
                    positionWorkerData.put("EFFECTIVE_START_DATE",positionWorkerColl.getPositionWorkers().get(i).getEFFECTIVE_START_DATE());
                    positionWorkerData.put("PERSON_ID",positionWorkerColl.getPositionWorkers().get(i).getPERSON_ID());
                    positionWorkerData.put("PERIOD_OF_SERVICE_ID",positionWorkerColl.getPositionWorkers().get(i).getPERIOD_OF_SERVICE_ID());
                    positionWorkerData.put("DATE_START",positionWorkerColl.getPositionWorkers().get(i).getDATE_START());
                    positionWorkerData.put("POSITION_ID",positionWorkerColl.getPositionWorkers().get(i).getPOSITION_ID());
                    positionWorkerData.put("PRIMARY_WORK_TERMS_FLAG",positionWorkerColl.getPositionWorkers().get(i).getPRIMARY_WORK_TERMS_FLAG());
                    positionWorkerData.put("PRIMARY_ASSIGNMENT_FLAG",positionWorkerColl.getPositionWorkers().get(i).getPRIMARY_ASSIGNMENT_FLAG());
                    positionWorkerData.put("WORK_TERM_ASSIGNMENT_ID",positionWorkerColl.getPositionWorkers().get(i).getWORK_TERM_ASSIGNMENT_ID());

                    listOfpositionWorkers.add(positionWorkerData);
    
                
                
                }
            
        }
        
        return  listOfpositionWorkers;
    }
    
    
    public List<Map> getPositionsData() throws Exception {
       
                
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+POSITIONS_REPORT,
                                true); 
        PositionsCollection positions = (PositionsCollection) this.parseXml(results,  PositionsCollection.class);
        int size=positions.getPositions().size();
        System.err.println("Size is >> "+positions.getPositions().size());
        Map position ;
        List<Map> listOfPositions=new ArrayList<Map>();
        if(positions!=null && positions.getPositions().size()>0){
            for(int i =0;i<size;i++){
                    position = new HashMap();
                    position.put("ID",i+1);
                    position.put("POSITION_ID",positions.getPositions().get(i).getPOSITION_ID());
                    position.put("NAME",positions.getPositions().get(i).getNAME());
                    
                    listOfPositions.add(position);
    
                
                
                }
            
        }
        return  listOfPositions;
    }
    
    public List<Map> getPositionId(String bindPositionName) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bindPositionName", bindPositionName );
        byte[] results =
            this.getReportBytes(REPORT_BASE_PATH+POSITION_ID_REPORT,params,
                                true); 
        PositionsCollection positions = (PositionsCollection) this.parseXml(results,  PositionsCollection.class);
        int size=positions.getPositions().size();
        System.err.println("Size is >> "+positions.getPositions().size());
        Map position ;
        List<Map> listOfPositions=new ArrayList<Map>();
        if(positions!=null && positions.getPositions().size()>0){
            for(int i =0;i<size;i++){
                    position = new HashMap();
                    
                    position.put("POSITION_ID",positions.getPositions().get(i).getPOSITION_ID());
                    position.put("NAME",positions.getPositions().get(i).getNAME());
                    
                    listOfPositions.add(position);
        
                
                
                }
            
        }
        return  listOfPositions;
        }
    
    public List<Map> getLoanRemaining(String PersonNumber) throws Exception {
       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("PersonNumber", PersonNumber );
        byte[] results =
            this.getReportBytes(REPORT_PASS_BASE_PATH+LOAN_VALIDATION_REPORT,params,
                                true); 
        LoanRemainingCollection loanRemaining = (LoanRemainingCollection) this.parseXml(results,  LoanRemainingCollection.class);
        int size=loanRemaining.getLoanRemaining().size();
        System.err.println("Size is >> "+loanRemaining.getLoanRemaining().size());
        Map loanRemainingMap ;
        List<Map> listOfLoanRemaining=new ArrayList<Map>();
        if(loanRemaining!=null && loanRemaining.getLoanRemaining().size()>0){
            for(int i =0;i<size;i++){
                    loanRemainingMap = new HashMap();
                    
                    loanRemainingMap.put("PERSON_NUMNER",loanRemaining.getLoanRemaining().get(i).getPERSON_NUMBER());
                    loanRemainingMap.put("EMPLOYEENAME",loanRemaining.getLoanRemaining().get(i).getEMPLOYEENAME());
                    loanRemainingMap.put("LOANREMAINING",loanRemaining.getLoanRemaining().get(i).getLOANREMAINING());
                    listOfLoanRemaining.add(loanRemainingMap);
        
                }  
        }
        return  listOfLoanRemaining;
        }
    
    public Map getPerDiemBasedOnGrade(String grade) throws Exception {
        Map PerDiemMap = new HashMap();
        String tableName = null;
            for(int count=0;count<2;count++){
                if(count==0){
                    tableName = "THC_LOCAL_BT";
                }
                else if(count==1){
                    tableName = "THC_INTERNATIONAL_BT";
                }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("p_table", tableName );
            params.put("p_grade", grade );
            byte[] results =
                this.getReportBytes(REPORT_PASS_BASE_PATH+BUSINESS_TRIP_PERDIEM_REPORT,params,
                                    true); 
            PerDiemCollection perDiemCollection = (PerDiemCollection) this.parseXml(results,  PerDiemCollection.class);
            System.err.println("first============"+perDiemCollection);
            int size=perDiemCollection.getPerDiem().size();
            System.err.println("first============"+size); 
            if(perDiemCollection!=null && size>0){
                for(int i =0;i<size;i++){
                        if(tableName.equalsIgnoreCase("THC_LOCAL_BT")){
                            PerDiemMap.put("Local",perDiemCollection.getPerDiem().get(i).getVALUE());
                            System.err.println("first============"+PerDiemMap);
                        }
                        else{
                            PerDiemMap.put("Inter",perDiemCollection.getPerDiem().get(i).getVALUE());
                            System.err.println("first============"+PerDiemMap);
                        }
                    }  
            }
        }
            System.err.println("final============"+PerDiemMap);
        return  PerDiemMap;
        }
    public  List<Map> getBudgetAmount(String AccountNumber,String budgetdate) throws Exception {
         
          Map<String, Object> params = new HashMap<String, Object>();
          params.put("P_CONCAT", AccountNumber);
          params.put("P_BUDGET_DATE", budgetdate);
          byte[] results =
              this.getReportBytes(REPORT_PASS_BASE_PATH+BUDGET_AMOUNT_REPORT,params,
                                  true); 
          BudgetReportCollection budgetAmount = (BudgetReportCollection) this.parseXml(results,  BudgetReportCollection.class);
          int size=budgetAmount.getListofBudgetValues().size();
          System.err.println("Size is >> "+budgetAmount.getListofBudgetValues().size());
          Map budgetAmountRemainingMap;
          List<Map> listofBudgetAmount=new ArrayList<Map>();
          if(budgetAmount!=null && budgetAmount.getListofBudgetValues().size()>0){
              for(int i =0;i<size;i++){
                      budgetAmountRemainingMap = new HashMap(); //FUNDS_AVAILABLE_AMOUNT
                      budgetAmountRemainingMap.put("FUNDS_AVAILABLE_AMOUNT",budgetAmount.getListofBudgetValues().get(i).getFUNDSAVAILABLEAMOUNT());
                      listofBudgetAmount.add(budgetAmountRemainingMap);
          
                  }  
          }
          return  listofBudgetAmount;
          }
    
    public List<Map> getmobileDatFileData(String p_emp_no) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_emp_no", p_emp_no);   
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+MOBILE_ALLOWANCE,params,
                                 true); 
         MobileDateCollection mobileCollection = (MobileDateCollection) this.parseXml(results,  MobileDateCollection.class);
         int size=mobileCollection.getMobilereport().size();
         System.err.println("Size is >> "+mobileCollection.getMobilereport().size());
         Map mobiledat ;
         List<Map> listofMobileData=new ArrayList<Map>();
         if(mobileCollection!=null && mobileCollection.getMobilereport().size()>0){
             for(int i =0;i<size;i++){
                     mobiledat = new HashMap();
                     System.out.println("mobile assignment"+mobileCollection.getMobilereport().get(i).getAssignment_number());
                     mobiledat.put("PERSON_NUMBER",mobileCollection.getMobilereport().get(i).getPerson_Number());
                     mobiledat.put("ASSIGNMENT_NUMBER",mobileCollection.getMobilereport().get(i).getAssignment_number());
                     mobiledat.put("MULIPLE_ENTRY",mobileCollection.getMobilereport().get(i).getMultiple_entry());
                     mobiledat.put("EFF_START_DATE",mobileCollection.getMobilereport().get(i).getEff_start_date());
                     mobiledat.put("EFF_END_DATE",mobileCollection.getMobilereport().get(i).getEff_end_date());
                     listofMobileData.add(mobiledat);
     
                 }
             
         }
         return  listofMobileData;
     }  
    
    public List<Map> getVacantPositionsData(String p_loc, String person) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_person_number", person);
         params.put("p_loc", p_loc);
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+VACANT_POSITION,params,
                                 true); 
         VacantPositionCollection vacPositionCollection = (VacantPositionCollection) this.parseXml(results,  VacantPositionCollection.class);
         int size=vacPositionCollection.getVacantPositions().size();
         System.err.println("Size is >> "+size);
         Map vacPoisitionMap ;
         List<Map> listofMobileData=new ArrayList<Map>();
         if(vacPositionCollection!=null && size >0){
             for(int i =0;i<size;i++){
                     vacPoisitionMap = new HashMap(); 
                     vacPoisitionMap.put("POSITION",vacPositionCollection.getVacantPositions().get(i).getPOSITION());
                     listofMobileData.add(vacPoisitionMap);  
                 }
             
         }
         return  listofMobileData;
     }  
    
    public List<Map> getPrivilegesToManpowerData(String p_loc, String p_depatment) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_loc", p_loc);
         params.put("p_depatment", p_depatment);
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+PRIVILEGES_TO_MR,params,
                                 true); 
         PrivilegesToManpowerCollection vacPositionCollection = (PrivilegesToManpowerCollection) this.parseXml(results,  PrivilegesToManpowerCollection.class);
         int size=vacPositionCollection.getPrivilegesToManpower().size();
         System.err.println("Size is >> "+size);
         Map vacPoisitionMap ;
         List<Map> listofMobileData=new ArrayList<Map>();
         if(vacPositionCollection!=null && size >0){
             for(int i =0;i<size;i++){
                     vacPoisitionMap = new HashMap(); 
                 if(vacPositionCollection.getPrivilegesToManpower().get(i).getPRIVILEGES_TO_MR()!=null &&
                            "Y".equals(vacPositionCollection.getPrivilegesToManpower().get(i).getPRIVILEGES_TO_MR())){
                  
                     vacPoisitionMap.put("POSITION",vacPositionCollection.getPrivilegesToManpower().get(i).getPOSITION());
                     vacPoisitionMap.put("PRIVILEGES_TO_MR",vacPositionCollection.getPrivilegesToManpower().get(i).getPRIVILEGES_TO_MR());
                     listofMobileData.add(vacPoisitionMap);    
                 } 
            }
             
         }
         return  listofMobileData;
     } 
    
    public List<Map> getInvoiceStatusList(String inv_num) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("inv_num", inv_num);   
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ INVOICE_STATUS ,params,
                                 true); 
         InvoiceStatusCollection invoiceCollection = (InvoiceStatusCollection) this.parseXml(results,  InvoiceStatusCollection.class);
         int size=invoiceCollection.getInvoiceStatusList().size();
         System.err.println("Size is >> "+invoiceCollection.getInvoiceStatusList().size());
         Map invStatus ;
         List<Map> listofInvoiceStatus = new ArrayList<Map>();
         if(invoiceCollection!=null && invoiceCollection.getInvoiceStatusList().size()>0){
             for(int i =0;i<size;i++){
                     invStatus = new HashMap(); 
                     invStatus.put("TICKET_AMOUNT",invoiceCollection.getInvoiceStatusList().get(i).getTICKET_AMOUNT());
                     invStatus.put("BUSINESS_TRIP_AMOUNT",invoiceCollection.getInvoiceStatusList().get(i).getBUSINESS_TRIP_AMOUNT());
                     invStatus.put("PAYMENT_STATUS_FLAG",invoiceCollection.getInvoiceStatusList().get(i).getPAYMENT_STATUS_FLAG());
                     listofInvoiceStatus.add(invStatus);     
        }             
         }
         return  listofInvoiceStatus;
     }  
    
    public List<Map> getBusinessTripWithdrawnList(String p_element_name, String p_emp_no) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_element_name", p_element_name);
         params.put("p_emp_no", p_emp_no);
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH + BUSINESS_TRIP_WITHDRAWN,params,
                                 true); 
         BusinessTripWithdrawnCollection btCollection = (BusinessTripWithdrawnCollection) this.parseXml(results,  BusinessTripWithdrawnCollection.class);
         int size=btCollection.getBusinessTripWithdrawnList().size();
         System.err.println("Size is >> "+btCollection.getBusinessTripWithdrawnList().size());
         Map btWithdrawn ;
         List<Map> btWithdrawnList = new ArrayList<Map>();
         if(btCollection!=null && btCollection.getBusinessTripWithdrawnList().size()>0){
             for(int i =0;i<size;i++){
                     btWithdrawn = new HashMap(); 
                     btWithdrawn.put("ASSIGNMENT_NUMBER",btCollection.getBusinessTripWithdrawnList().get(i).getASSIGNMENT_NUMBER());
                     btWithdrawn.put("PERSON_NUMBER",btCollection.getBusinessTripWithdrawnList().get(i).getPERSON_NUMBER());
                     btWithdrawn.put("MULTIPLEENTRYCOUNT",btCollection.getBusinessTripWithdrawnList().get(i).getMULTIPLEENTRYCOUNT()); 
                     btWithdrawnList.add(btWithdrawn);
                 }             
         }
         return  btWithdrawnList;
     }  
    
    public List<Map> getAllLocations() throws Exception { 
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ LOCATIONS, true); 
         LocationsCollection locCollection = (LocationsCollection) this.parseXml(results,  LocationsCollection.class);
         int size = locCollection.getLocations().size();
         System.err.println("Size is >> "+locCollection.getLocations().size());
         Map locMap ;
         List<Map> allLocations = new ArrayList<Map>();
         if(locCollection!=null && locCollection.getLocations().size()>0){
             for(int i =0;i<size;i++){
                     locMap = new HashMap(); 
                     locMap.put("LOCATION_NAME",locCollection.getLocations().get(i).getLOCATION());
                     locMap.put("LOCATION_ID",locCollection.getLocations().get(i).getLOCATION_ID());
                     allLocations.add(locMap);     
                }             
         }
         System.err.println("AllLocations--->"+allLocations);
         return  allLocations;
     }  
    
    public List<Map> getAdjustLoanBalance(String personNo) throws Exception { 
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_emp_no", personNo); 
         
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ ADJUST_LOAN_BALANCE, params, true); 
         AdjustLoanBalanceCollection collection = (AdjustLoanBalanceCollection) this.parseXml(results,  AdjustLoanBalanceCollection.class);
         int size = collection.getAdjustLoanBalance().size();
         System.err.println("Size is >> "+collection.getAdjustLoanBalance().size());
         Map balanceMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collection!=null && collection.getAdjustLoanBalance().size()>0){
             for(int i =0;i<size;i++){
                     balanceMap = new HashMap(); 
                     balanceMap.put("ASSIGNMENT_NUMBER",collection.getAdjustLoanBalance().get(i).getASSIGNMENT_NUMBER());
                     balanceMap.put("MULTIPLEENTRYCOUNT",collection.getAdjustLoanBalance().get(i).getMULTIPLEENTRYCOUNT());
                     listMap.add(balanceMap);     
                }             
         }
         return  listMap;
     }  
    
    public List<Map> getMobileAllowanceDatfileInfo(String personNo, String effDate) throws Exception {  
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_emp_no", personNo); 
         params.put("p_effective_date", effDate); 
         
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ MOBILE_WITHDRAW, params, true); 
         MobileDatfileCollection collections = (MobileDatfileCollection) this.parseXml(results,  MobileDatfileCollection.class);
         int size = collections.getMobileDatfile().size();
         System.err.println("Size is.. >> "+ size);
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("PERSON_NUMBER",collections.getMobileDatfile().get(i).getPERSON_NUMBER());
                     tempMap.put("ASSIGNMENT_NUMBER",collections.getMobileDatfile().get(i).getASSIGNMENT_NUMBER());
                     tempMap.put("EFFECTIVE_START_DATE",collections.getMobileDatfile().get(i).getEFFECTIVE_START_DATE());
                     tempMap.put("EFFECTIVE_END_DATE",collections.getMobileDatfile().get(i).getEFFECTIVE_END_DATE());
                     tempMap.put("MULTIPLEENTRYCOUNT",collections.getMobileDatfile().get(i).getMULTIPLEENTRYCOUNT());
                     listMap.add(tempMap);     
                }             
         }
         return  listMap;
     }  
    
    public List<Map> getLoanWithdrawDatfileInfo(String personNo, String effDate) throws Exception {  
//         System.err.println("Params : "+personNo+ " :: "+ effDate);
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_emp_no", personNo); 
         params.put("p_effective_date", effDate); 
         
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ LOAN_WITHDRAW , params, true); 
         LoanWithdrawDatCollection collections = (LoanWithdrawDatCollection) this.parseXml(results,  LoanWithdrawDatCollection.class);
         int size = collections.getLoanWithDraw().size();
         System.err.println("Size is.. >> "+ size);
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("PERSON_NUMBER",collections.getLoanWithDraw().get(i).getPERSON_NUMBER());
                     tempMap.put("ASSIGNMENT_NUMBER",collections.getLoanWithDraw().get(i).getASSIGNMENT_NUMBER());
                     tempMap.put("EFFECTIVE_START_DATE",collections.getLoanWithDraw().get(i).getEFFECTIVE_START_DATE());
                     tempMap.put("EFFECTIVE_END_DATE",collections.getLoanWithDraw().get(i).getEFFECTIVE_END_DATE());
                     tempMap.put("MULTIPLEENTRYCOUNT",collections.getLoanWithDraw().get(i).getMULTIPLEENTRYCOUNT());
                     tempMap.put("NO_OF_MONTHS",collections.getLoanWithDraw().get(i).getNO_OF_MONTHS());
                     tempMap.put("DIVISION",collections.getLoanWithDraw().get(i).getDIVISION());
                     tempMap.put("TOTAL_AMOUNT",collections.getLoanWithDraw().get(i).getTOTAL_AMOUNT());
                     tempMap.put("LOAN_CREATION_DATE",collections.getLoanWithDraw().get(i).getLOAN_CREATION_DATE());
                     listMap.add(tempMap);     
                }             
         }
         return  listMap;
     } 
    
    public List<Map> getExitReEntryWithdrawDatInfo(String personNo, String effDate) throws Exception {   
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_emp_no", personNo); 
         params.put("p_effective_date", effDate); 
         
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ EXIT_RE_ENTRY_DAT_WITHDRAW , params, true); 
         ExitReReentryWithdrawCollections collections = (ExitReReentryWithdrawCollections) this.parseXml(results,  ExitReReentryWithdrawCollections.class);
         int size = collections.getExitReReentryDatWithdraw().size();
         System.err.println("Size is.. >> "+ size);
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("PERSON_NUMBER",collections.getExitReReentryDatWithdraw().get(i).getPERSON_NUMBER());
                     tempMap.put("ASSIGNMENT_NUMBER",collections.getExitReReentryDatWithdraw().get(i).getASSIGNMENT_NUMBER());
                     tempMap.put("EFFECTIVE_START_DATE",collections.getExitReReentryDatWithdraw().get(i).getEFFECTIVE_START_DATE());
                     tempMap.put("EFFECTIVE_END_DATE",collections.getExitReReentryDatWithdraw().get(i).getEFFECTIVE_END_DATE());
                     tempMap.put("MULTIPLEENTRYCOUNT",collections.getExitReReentryDatWithdraw().get(i).getMULTIPLEENTRYCOUNT()); 
                     tempMap.put("SCREENENTRYVALUE",collections.getExitReReentryDatWithdraw().get(i).getSCREENENTRYVALUE()); 
                 
                     listMap.add(tempMap);     
                }             
         }
         return  listMap;
     } 
    
    
    public List<Map> getAllPersonDetails() throws Exception {   
        
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH + ALL_PERSONS_REPORT , true); 
         
         AllPersonsCollection collections = (AllPersonsCollection) this.parseXml(results,  AllPersonsCollection.class);
         int size = collections.getAllPersons().size(); 
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("PERSON_NUMBER",collections.getAllPersons().get(i).getPERSON_NUMBER());
                     tempMap.put("USERNAME",collections.getAllPersons().get(i).getUSERNAME());
                     tempMap.put("NAME",collections.getAllPersons().get(i).getNAME()); 
                     listMap.add(tempMap);     
                }             
         }
         return  listMap;
     } 
    
    public List<Map> getTerminatedEmployees(String personNo) throws Exception {   
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_person_number", personNo); 
//         params.put("p_loc_name", location); 
         
         byte[] results =
             this.getReportBytes(REPORT_PASS_BASE_PATH+ TERMINATED_EMPLOYEES , params, true); 
         TerminatedEmpsCollection collections = (TerminatedEmpsCollection) this.parseXml(results,  TerminatedEmpsCollection.class);
         int size = collections.getTerminatedEmps().size();
         System.err.println("Size is.. >> "+ size);
         Map tempMap ;
         List<Map> listMap = new ArrayList<Map>();
         if(collections!=null && size >0){
             for(int i =0;i<size;i++){
                     tempMap = new HashMap(); 
                     tempMap.put("HIRE_DATE",collections.getTerminatedEmps().get(i).getHIRE_DATE());
                     tempMap.put("DEPARTMENT_AR",collections.getTerminatedEmps().get(i).getDEPARTMENT_AR());
                     tempMap.put("ACTION_REASON",collections.getTerminatedEmps().get(i).getACTION_REASON());
                     tempMap.put("MANAGER_NAME_AR",collections.getTerminatedEmps().get(i).getMANAGER_NAME_AR());
                     tempMap.put("ACTUAL_TERMINATION_DATE",collections.getTerminatedEmps().get(i).getACTUAL_TERMINATION_DATE()); 
                     tempMap.put("TOT_YEARS",collections.getTerminatedEmps().get(i).getTOT_YEARS()); 
                     tempMap.put("TOT_MONTHS",collections.getTerminatedEmps().get(i).getTOT_MONTHS()); 
                     tempMap.put("TOT_DAYS",collections.getTerminatedEmps().get(i).getTOT_DAYS()); 
                     tempMap.put("FULL_NAME_AR",collections.getTerminatedEmps().get(i).getFULL_NAME_AR()); 
                     tempMap.put("SYSTEM_PERSON_TYPE",collections.getTerminatedEmps().get(i).getSYSTEM_PERSON_TYPE()); 
                     tempMap.put("ACTION_REASON_EN",collections.getTerminatedEmps().get(i).getACTION_REASON_EN()); 
                     tempMap.put("MANAGER_NAME",collections.getTerminatedEmps().get(i).getMANAGER_NAME()); 
                     tempMap.put("ACTION",collections.getTerminatedEmps().get(i).getACTION()); 
                     tempMap.put("JOB_NAME",collections.getTerminatedEmps().get(i).getJOB_NAME()); 
                     tempMap.put("POSITION_NAME_AR",collections.getTerminatedEmps().get(i).getPOSITION_NAME_AR()); 
                 
                    listMap.add(tempMap);     
                }             
         }
         return  listMap;
     } 
    
    public byte[] getBusinessTripAttachBytes(String tripID) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("BUSINESS_TRIP_ID", tripID);   
         byte[] results =
             this.getReportBytesPDF(REPORT_PASS_BASE_PATH + BUSINESS_TRIP_ATTACHMENT,params,
                                 true);
         return  results;
     }  
    
    public byte[] getExpCertificate(String p_person_number) throws Exception {
        
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_person_number", p_person_number);   
         byte[] results =
             this.getReportBytesPDF(REPORT_PASS_BASE_PATH + EXP_CERTIFICATE ,params, true);
         return  results;
     }
    
    public byte[] getTrainingCertificate(String p_person_number) throws Exception {
         System.err.println("in TRAINING_CERTIFICATE");
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_person_number", p_person_number);   
         byte[] results =
             this.getReportBytesPDF(REPORT_PASS_BASE_PATH + TRAINING_CERTIFICATE ,params, true);
         return  results;
     } 
    
    public byte[] getTrainingCertificateForTamheer(String p_person_number) throws Exception {
         System.err.println("in TAMHEER_TRAINING_CERTIFICATE");
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("p_person_number", p_person_number);   
         byte[] results =
             this.getReportBytesPDF(REPORT_PASS_BASE_PATH + TAMHEER_TRAINING_CERTIFICATE ,params, true);
         return  results;
     }  
    
    public static void main(String[] args) {
       
        BiReportAccess BIRA=new BiReportAccess();
       

//                List<Map> list;
//                try {
//                    list = BIRA.getMngrEmployees("2046");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

        
//            List<Map> list;
//            try {
//                list = BIRA.getPersonAccrualData("2019-03-07","1151");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
      
//        
//            List<Map> list;
//            try {
//                list = BIRA.getExceptionalRewardDatFileData("1134");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        
//        List<Map> list;
//        try {
//            list = BIRA.getPositionId("Projects Associate");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        
//                      
    List<Map> list;
    try {
        list = BIRA.getBankAccountData("1134");
    } catch (Exception e) {
        e.printStackTrace();
    }
    
        
        
        
        
        
//        List<Map> list1;
//        try {
//                    list1 = BIRA.getPositionsData();
//                    for(Map currentDepartment:list1)
//                                {
//                                    System.out.println("list.size(); >>>"+list1.size());
//                                        System.out.println("I'm Inside for each loop");
//                                        
//                                        System.err.println("ID >>> "+currentDepartment.get("ID"));
//                                        System.err.println("POSITION_ID >>> "+currentDepartment.get("POSITION_ID"));
//                                        System.err.println("NAME >>> "+currentDepartment.get("NAME"));
//                                        
//                                        
//                                        System.out.println("################################");  
//                                    
//                                    }
//                    
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
        
//
//        String actionReasoneReport;
//
//
//        try {
//            actionReasoneReport = BIRA.callActionReasoneReport("1029");
//            System.err.println("actionReasoneReport >>> "+actionReasoneReport);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        List<Map> list1;
//        
//        
//        try {
//            List<Map> list = BIRA.getPersonByPostionReport("Senior HR Specialist");
//            
//            for(Map currentEmp:list)
//            {
//                System.out.println("list.size(); >>>"+list.size());
//                    System.out.println("I'm Inside for each loop");
//                    System.out.println("*****************");
//                    System.err.println("PERSON_NUMBER >>> "+currentEmp.get("PERSON_NUMBER"));
//                    System.out.println("*****************");
//                    System.err.println("DISPLAY_NAME >>> "+currentEmp.get("DISPLAY_NAME"));
//                    System.out.println("*****************");
//                    System.err.println("POSITION_CODE >>> "+currentEmp.get("POSITION_CODE"));
//                    System.out.println("*****************");
//                    System.err.println("POSITION_NAME >>> "+currentEmp.get("POSITION_NAME"));
//                    System.out.println("*****************");
//                    System.err.println("EMAIL_ADDRESS >>> "+currentEmp.get("EMAIL_ADDRESS"));
//                    System.out.println("################################");  
//                
//                }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
////        
//        try {
//            List<Map> list = BIRA.getHCMSegments("1029");
//            
//            for(Map currentHCMSegments:list)
//            {
//                System.out.println("list.size(); >>>"+list.size());
//                    System.out.println("I'm Inside for each loop");
//                    System.out.println("*****************");
//                    System.err.println("Division >>> "+currentHCMSegments.get("SEGMENT2"));
//                    System.out.println("*****************");
//                    System.err.println("LOB >>> "+currentHCMSegments.get("SEGMENT3"));
//                    System.out.println("*****************");
//                    System.err.println("Cost Center >>> "+currentHCMSegments.get("SEGMENT4"));
//                    System.out.println("################################");  
//                
//                }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        
////        try {
////            List<Map> list = BIRA.getCodeCombinationId("01","00","00","1010001","5101030201","00","0000","0000");
////            
////            for(Map currentCodeCombinId:list)
////            {
////                System.out.println("list.size(); >>>"+list.size());
////                    System.out.println("I'm Inside for each loop");
////                    System.err.println("CODE_COMBINATION_ID Is >>> "+currentCodeCombinId.get("CODE_COMBINATION_ID"));
////                    System.out.println("################################");  
////                
////                }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        
////        
////        
//        try {
//            List<Map> list = BIRA.getEmployeeChilds("2046");
//            for(Map currentEmployeeChilds:list)
//            {
//                System.out.println("list.size(); >>>"+list.size());
//                    System.out.println("I'm Inside for each loop");
//                    System.err.println("PERSON_NUMBER >>> "+currentEmployeeChilds.get("PERSON_NUMBER"));
//                    System.err.println("HIRE_DATE >>> "+currentEmployeeChilds.get("HIRE_DATE"));
//                    System.err.println("MARITAL_STATUS >>> "+currentEmployeeChilds.get("MARITAL_STATUS"));
//                    System.err.println("NUMBER_OF_CHILDS >>> "+currentEmployeeChilds.get("NUMBER_OF_CHILDS"));
//                    System.err.println("NATIONALITY >>> "+currentEmployeeChilds.get("NATIONALITY"));
//                    
//                    System.out.println("################################");  
//                
//                }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            list1 = BIRA.getDepartmentsData();
//            for(Map currentDepartment:list1)
//                        {
//                            System.out.println("list.size(); >>>"+list1.size());
//                                System.out.println("I'm Inside for each loop");
//                                System.err.println("NAME >>> "+currentDepartment.get("NAME"));
//                                
//                                System.err.println("ORGANIZATION_ID >>> "+currentDepartment.get("ORGANIZATION_ID"));
//                                
//                                System.err.println("SEGMENT4 >>> "+currentDepartment.get("SEGMENT4"));
//                                
//                                System.out.println("################################");  
//                            
//                            }
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
//        List<Map> listOfGrades=null;
//        try {
//            listOfGrades = BIRA.getGradesData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        for(Map currentGrade:listOfGrades)
//                        {
//                            System.out.println("list.size(); >>>"+listOfGrades.size());
//                                System.out.println("I'm Inside for each loop");
//                                System.err.println("GRADE ID IS >>> "+currentGrade.get("GRADE_ID"));
//                                System.err.println("NAME  IS >>> "+currentGrade.get("NAME"));
//                                System.out.println("################################");  
//                            
//                            }
//
//        List<Map> listOfJobs=null;
//
//        try {
//            listOfJobs = BIRA.getJobsData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        for(Map currentJob:listOfJobs)
//                        {
//                            System.out.println("list.size(); >>>"+listOfJobs.size());
//                                System.out.println("I'm Inside for each loop");
//                                System.err.println("JOB ID IS >>> "+currentJob.get("JOB_ID"));
//                                System.err.println("NAME  IS >>> "+currentJob.get("NAME"));
//                                System.out.println("################################");  
//                            
//                            }
//

//        List<Map> datFileData=null;
//        try {
//            datFileData = BIRA.getExitReEntryDatFileData("2046");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        System.err.println(datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());

//        List<Map> data=null;
//
//        try {
//            data = BIRA.getLoanDatFileData("1092");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        System.err.println(data.get(0).get("ASSIGNMENT_NUMBER").toString());
 
} 
        
}
