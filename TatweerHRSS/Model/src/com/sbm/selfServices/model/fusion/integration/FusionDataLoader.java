package com.sbm.selfServices.model.fusion.integration;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public  class FusionDataLoader {
    
//    Loan Request data file path
    public static final int LOAN_REQUEST=1;    
    private String  FUSION_LOAN_REQUEST_FILE_NAME="ElementEntry.dat";    
    private String LOAN_REQUEST_FILE_NAME="com/sbm/selfServices/model/fusion/integration/ElementEntry.dat";
    
//    Overtime data file path 
    public static final int OVERTIME_REQUEST=2;    
//    private String  FUSION_OVERTIME_REQUEST_FILE_NAME="Overtime.dat";    
    private String  FUSION_OVERTIME_REQUEST_FILE_NAME="ElementEntry.dat";
    private String OVERTIME_REQUEST_FILE_NAME="com/sbm/selfServices/model/fusion/integration/Overtime.dat";
    
    public static final int Mobile_REQUEST=3;    
    //    private String  FUSION_MOBILE_REQUEST_FILE_NAME="Mobile.dat";
    private String  FUSION_MOBILE_REQUEST_FILE_NAME="ElementEntry.dat";
    private String MOBILE_REQUEST_FILE_NAME="com/sbm/selfServices/model/fusion/integration/Mobile.dat";
    
    public static final int EXITREENTRY_REQUEST=4;    
    //    private String  FUSION_OVERTIME_REQUEST_FILE_NAME="Overtime.dat";
    private String  FUSION_EXITREENTRY_REQUEST_FILE_NAME="ElementEntry.dat";
    private String EXITREENTRY_REQUEST_FILE_NAME="com/sbm/selfServices/model/fusion/integration/exitReEntry.dat";
    
    public static final int CREATE_POSITION=5;    
    private String  FUSION_CREATE_POSITION_FILE_NAME="Position.dat";    
    private String CREATE_POSITION_FILE_NAME="com/sbm/selfServices/model/fusion/integration/Position.dat";
    
    public static final int UPDATE_WORKER=6;    
    private String  FUSION_UPDATE_WORKER_FILE_NAME="Worker.dat";    
    private String UPDATE_WORKER_FILE_NAME="com/sbm/selfServices/model/fusion/integration/Worker.dat";
    
    public static final int EXCEPTIONAL_REWARD=7;    
    private String  FUSION_EXCEPTIONAL_REWARD_REQUEST_FILE_NAME="ElementEntry.dat";    
    private String EXCEPTIONAL_REWARD_REQUEST_FILE_NAME="com/sbm/selfServices/model/fusion/integration/ExceptionalReward.dat";

    public static final int PERSON_ACCRUAL_DETAILS=8;    
    private String  FUSION_PERSON_ACCRUAL_DETAILS_FILE_NAME="PersonAccrualDetail.dat";    
    private String PERSON_ACCRUAL_DETAILS_FILE_NAME="com/sbm/selfServices/model/fusion/integration/PersonAccrualDetail.dat";
    
    public static final int BUSINESS_TRIP_DEDUCTION = 9;    
    private String  FUSION_BUSINESS_TRIP_DEDUCTION_FILE_NAME="ElementEntry.dat";    
    private String BUSINESS_TRIP_DEDUCTION_FILE_NAME="com/sbm/selfServices/model/fusion/integration/BusinessTripDeduction.dat";
    
    public static final int TICKET_DEDUCTION = 10;    
    private String  FUSION_TICKET_DEDUCTION_FILE_NAME="ElementEntry.dat";    
    private String TICKET_DEDUCTION_FILE_NAME="com/sbm/selfServices/model/fusion/integration/TicketsDeduction.dat";
    
    public static final int ADJUST_LOAN_BALANCE = 11;    
    private String  FUSION_ADJUST_LOAN_BALANCE_FILE_NAME="ElementEntry.dat";    
    private String ADJUST_LOAN_BALANCE_FILE_NAME="com/sbm/selfServices/model/fusion/integration/LoanSettlement.dat";
    
    public static final int MOBILE_WITHDRAW = 12;    
    private String  FUSION_MOBILE_WITHDRAW="ElementEntry.dat";    
    private String MOBILE_WITHDRAW_FILE_NAME="com/sbm/selfServices/model/fusion/integration/MobileWithdraw.dat";
    
    public static final int LOAN_WITHDRAW = 13;    
    private String  FUSION_LOAN_WITHDRAW="ElementEntry.dat";    
    private String LOAN_WITHDRAW_FILE_NAME="com/sbm/selfServices/model/fusion/integration/LoanWithdrawEntry.dat";
    
    public static final int OVERTIME_WITHDRAW = 14;    
    private String  FUSION_OVERTIME_WITHDRAW="ElementEntry.dat";    
    private String OVERTIME_WITHDRAW_FILE_NAME="com/sbm/selfServices/model/fusion/integration/OvertimeWithdraw.dat";
    
    public static final int EXIT_REENTRY_WITHDRAW = 15;    
    private String  FUSION_EXIT_REENTRY_WITHDRAW="ElementEntry.dat";    
    private String EXIT_REENTRY_WITHDRAW_FILE_NAME="com/sbm/selfServices/model/fusion/integration/ExitReEntryWithdraw.dat";
    
    public static final int LOAN_SETTLEMENT_WITHDRAW = 16;    
    private String  FUSION_LOAN_SETTLEMENT_WITHDRAW="ElementEntry.dat";    
    private String LOAN_SETTLEMENT_WITHDRAW_FILE_NAME="com/sbm/selfServices/model/fusion/integration/LoanSettlementWithdraw.dat";
    
    public static final int TRAINING_COST_EARNING = 17;    
    private String  FUSION_TRAINING_COST_EARNING="ElementEntryWithCosting.dat";    
    private String TRAINING_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/TrainingEarnings.dat";
    
    public static final int BUSINESS_TRIP_COST_EARNING = 18;    
    private String  FUSION_BUSINESS_TRIP_EARNING="ElementEntryWithCosting.dat";    
    private String BUSINESS_TRIP_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/BusinessTripEarnings.dat";
    
    public static final int EVENT_COST_EARNING = 19;    
    private String  FUSION_EVENT_COST_EARNING="ElementEntryWithCosting.dat";    
    private String EVENT_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/EventCostEarnings.dat";
    
    public static final int EDUCATION_ALLOWANCE_COST_EARNING = 20;    
    private String  FUSION_EDUCATION_ALLOWANCE_COST_EARNING="ElementEntryWithCosting.dat";    
    private String EDUCATION_ALLOWANCE_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/EducationAllowanceEarnings.dat";
    
    public static final int REPAYMET_TICKET_COST_EARNING = 21;    
    private String  FUSION_REPAYMET_TICKET_COST_EARNING="ElementEntryWithCosting.dat";    
    private String REPAYMET_TICKET_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/RepaymentTicketEarnings.dat";
    
    public static final int SPECIAL_NEED_COST_EARNING = 22;    
    private String  FUSION_SPECIAL_NEED_COST_EARNING="ElementEntryWithCosting.dat";    
    private String  SPECIAL_NEED_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/SpecialNeedEarnings.dat";
    
    public static final int LOAN_COST_EARNING = 23;    
    private String  FUSION_LOAN_COST_EARNING="ElementEntryWithCosting.dat";    
    private String  LOAN_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/LoanEarnings.dat";
    
    public static final int TRAVEL_COST_EARNING = 24;    
    private String  FUSION_TRAVEL_COST_EARNING="ElementEntryWithCosting.dat";    
    private String  TRAVEL_COST_EARNING_FILE_NAME="com/sbm/selfServices/model/fusion/integration/TravelTicketEarnings.dat";

    
    //    Common            
    private String templatesPath= "/Config/DataUploadTemplates/";
    private FusionIntegrationWrapper fusionWrapper= new FusionIntegrationWrapper();
    
    public FusionDataLoader()  {
        super();
        
    }
    
    
    
//    public static void main(String[] args) {
//        FusionDataLoader class1=new FusionDataLoader();
//        
//        String fileName="com/sbm/selfServices/model/fusion/integration/ElementEntry.dat";
//
//        try {
//            class1.getFileContent(fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
        public  Map<String, String> sendFusionRequest(Map<String,String> params,int requestType)throws Exception{
        byte[] results=null;
        if(requestType == this.LOAN_REQUEST){
            results = this.prepareFile(params,this.LOAN_REQUEST_FILE_NAME,FUSION_LOAN_REQUEST_FILE_NAME);
        
        }
        else if (requestType == this.OVERTIME_REQUEST){
            results = this.prepareFile(params,this.OVERTIME_REQUEST_FILE_NAME,FUSION_OVERTIME_REQUEST_FILE_NAME);
        }
        
           else if (requestType == this.Mobile_REQUEST){
               results = this.prepareFile(params,this.MOBILE_REQUEST_FILE_NAME,FUSION_MOBILE_REQUEST_FILE_NAME);
           }
           else if (requestType == this.EXITREENTRY_REQUEST){
               results = this.prepareFile(params,this.EXITREENTRY_REQUEST_FILE_NAME,FUSION_EXITREENTRY_REQUEST_FILE_NAME);
           }
        
           else if (requestType == this.CREATE_POSITION){
               results = this.prepareFile(params,this.CREATE_POSITION_FILE_NAME,FUSION_CREATE_POSITION_FILE_NAME);
           }
           else if (requestType == this.UPDATE_WORKER){
               results = this.prepareFile(params,this.UPDATE_WORKER_FILE_NAME,FUSION_UPDATE_WORKER_FILE_NAME);
           }
        
           else if (requestType == this.EXCEPTIONAL_REWARD){
               results = this.prepareFile(params,this.EXCEPTIONAL_REWARD_REQUEST_FILE_NAME,FUSION_EXCEPTIONAL_REWARD_REQUEST_FILE_NAME);
           }
        
           else if (requestType == this.PERSON_ACCRUAL_DETAILS){
               results = this.prepareFile(params,this.PERSON_ACCRUAL_DETAILS_FILE_NAME,FUSION_PERSON_ACCRUAL_DETAILS_FILE_NAME);
           }
        
           else if (requestType == this.BUSINESS_TRIP_DEDUCTION){
               results = this.prepareFile(params, this.BUSINESS_TRIP_DEDUCTION_FILE_NAME, FUSION_BUSINESS_TRIP_DEDUCTION_FILE_NAME);
           }
           else if (requestType == this.TICKET_DEDUCTION){
               results = this.prepareFile(params, this.TICKET_DEDUCTION_FILE_NAME, FUSION_TICKET_DEDUCTION_FILE_NAME);
           } 
           else if(requestType == this.ADJUST_LOAN_BALANCE){
                results = this.prepareFile(params, this.ADJUST_LOAN_BALANCE_FILE_NAME, FUSION_ADJUST_LOAN_BALANCE_FILE_NAME);
           }
           else if(requestType == this.MOBILE_WITHDRAW){
                results = this.prepareFile(params, this.MOBILE_WITHDRAW_FILE_NAME, FUSION_MOBILE_WITHDRAW);
           }
           else if(requestType == this.LOAN_WITHDRAW){
                results = this.prepareFile(params, this.LOAN_WITHDRAW_FILE_NAME, FUSION_LOAN_WITHDRAW);
           }
           else if(requestType == this.OVERTIME_WITHDRAW){
                results = this.prepareFile(params, this.OVERTIME_WITHDRAW_FILE_NAME, FUSION_OVERTIME_WITHDRAW);
           }
           else if(requestType == this.EXIT_REENTRY_WITHDRAW){
                results = this.prepareFile(params, this.EXIT_REENTRY_WITHDRAW_FILE_NAME, FUSION_EXIT_REENTRY_WITHDRAW);
           }
           else if(requestType == this.LOAN_SETTLEMENT_WITHDRAW){
                results = this.prepareFile(params, this.LOAN_SETTLEMENT_WITHDRAW_FILE_NAME, FUSION_LOAN_SETTLEMENT_WITHDRAW);
           }
           else if(requestType == this.TRAINING_COST_EARNING){
                results = this.prepareFile(params, this.TRAINING_COST_EARNING_FILE_NAME, FUSION_TRAINING_COST_EARNING);
           }
           else if(requestType == this.BUSINESS_TRIP_COST_EARNING){
                results = this.prepareFile(params, this.BUSINESS_TRIP_COST_EARNING_FILE_NAME, FUSION_BUSINESS_TRIP_EARNING);
           }
           else if(requestType == this.EVENT_COST_EARNING){
                results = this.prepareFile(params, this.EVENT_COST_EARNING_FILE_NAME, FUSION_EVENT_COST_EARNING);
           }
           else if(requestType == this.EDUCATION_ALLOWANCE_COST_EARNING){
                results = this.prepareFile(params, this.EDUCATION_ALLOWANCE_COST_EARNING_FILE_NAME, FUSION_EDUCATION_ALLOWANCE_COST_EARNING);
           }
           else if(requestType == this.REPAYMET_TICKET_COST_EARNING){
                results = this.prepareFile(params, this.REPAYMET_TICKET_COST_EARNING_FILE_NAME, FUSION_REPAYMET_TICKET_COST_EARNING);
           }
           else if(requestType == this.SPECIAL_NEED_COST_EARNING){
                results = this.prepareFile(params, this.SPECIAL_NEED_COST_EARNING_FILE_NAME, FUSION_SPECIAL_NEED_COST_EARNING);
           }
           else if(requestType == this.LOAN_COST_EARNING){
                results = this.prepareFile(params, this.LOAN_COST_EARNING_FILE_NAME, FUSION_LOAN_COST_EARNING);
           }
           else if(requestType == this.TRAVEL_COST_EARNING){
                results = this.prepareFile(params, this.TRAVEL_COST_EARNING_FILE_NAME, FUSION_TRAVEL_COST_EARNING);
           }
        
        else{
            throw new Exception("Invalid Request Type");
        }
       Map<String, String> uploadProcessId = null;
       if(results!=null){
          uploadProcessId = uploadFusionRequest(results);
       }
       return uploadProcessId;
    }
    
    /*EES method commented by Moshina
    public  void sendFusionRequest(Map<String,String> params,int requestType)throws Exception{
        byte[] results=null;
        if(requestType == this.LOAN_REQUEST){
            results = this.prepareFile(params,this.LOAN_REQUEST_FILE_NAME,FUSION_LOAN_REQUEST_FILE_NAME);
        
        }
        else if (requestType == this.OVERTIME_REQUEST){
            results = this.prepareFile(params,this.OVERTIME_REQUEST_FILE_NAME,FUSION_OVERTIME_REQUEST_FILE_NAME);
        }
        
           else if (requestType == this.Mobile_REQUEST){
               results = this.prepareFile(params,this.MOBILE_REQUEST_FILE_NAME,FUSION_MOBILE_REQUEST_FILE_NAME);
           }
           else if (requestType == this.EXITREENTRY_REQUEST){
               results = this.prepareFile(params,this.EXITREENTRY_REQUEST_FILE_NAME,FUSION_EXITREENTRY_REQUEST_FILE_NAME);
           }
        
           else if (requestType == this.CREATE_POSITION){
               results = this.prepareFile(params,this.CREATE_POSITION_FILE_NAME,FUSION_CREATE_POSITION_FILE_NAME);
           }
           else if (requestType == this.UPDATE_WORKER){
               results = this.prepareFile(params,this.UPDATE_WORKER_FILE_NAME,FUSION_UPDATE_WORKER_FILE_NAME);
           }
        
           else if (requestType == this.EXCEPTIONAL_REWARD){
               results = this.prepareFile(params,this.EXCEPTIONAL_REWARD_REQUEST_FILE_NAME,FUSION_EXCEPTIONAL_REWARD_REQUEST_FILE_NAME);
           }
        
           else if (requestType == this.PERSON_ACCRUAL_DETAILS){
               results = this.prepareFile(params,this.PERSON_ACCRUAL_DETAILS_FILE_NAME,FUSION_PERSON_ACCRUAL_DETAILS_FILE_NAME);
           }
        
           else if (requestType == this.BUSINESS_TRIP_DEDUCTION){
               results = this.prepareFile(params, this.BUSINESS_TRIP_DEDUCTION_FILE_NAME, FUSION_BUSINESS_TRIP_DEDUCTION_FILE_NAME);
           }
           else if (requestType == this.TICKET_DEDUCTION){
               results = this.prepareFile(params, this.TICKET_DEDUCTION_FILE_NAME, FUSION_TICKET_DEDUCTION_FILE_NAME);
           } 
           else if(requestType == this.ADJUST_LOAN_BALANCE){
                results = this.prepareFile(params, this.ADJUST_LOAN_BALANCE_FILE_NAME, FUSION_ADJUST_LOAN_BALANCE_FILE_NAME);
           }
           else if(requestType == this.MOBILE_WITHDRAW){
                results = this.prepareFile(params, this.MOBILE_WITHDRAW_FILE_NAME, FUSION_MOBILE_WITHDRAW);
           }
           else if(requestType == this.LOAN_WITHDRAW){
                results = this.prepareFile(params, this.LOAN_WITHDRAW_FILE_NAME, FUSION_LOAN_WITHDRAW);
           }
           else if(requestType == this.OVERTIME_WITHDRAW){
                results = this.prepareFile(params, this.OVERTIME_WITHDRAW_FILE_NAME, FUSION_OVERTIME_WITHDRAW);
           }
           else if(requestType == this.EXIT_REENTRY_WITHDRAW){
                results = this.prepareFile(params, this.EXIT_REENTRY_WITHDRAW_FILE_NAME, FUSION_EXIT_REENTRY_WITHDRAW);
           }
           else if(requestType == this.LOAN_SETTLEMENT_WITHDRAW){
                results = this.prepareFile(params, this.LOAN_SETTLEMENT_WITHDRAW_FILE_NAME, FUSION_LOAN_SETTLEMENT_WITHDRAW);
           }
           else if(requestType == this.TRAINING_COST_EARNING){
                results = this.prepareFile(params, this.TRAINING_COST_EARNING_FILE_NAME, FUSION_TRAINING_COST_EARNING);
           }
           else if(requestType == this.BUSINESS_TRIP_COST_EARNING){
                results = this.prepareFile(params, this.BUSINESS_TRIP_COST_EARNING_FILE_NAME, FUSION_BUSINESS_TRIP_EARNING);
           }
           else if(requestType == this.EVENT_COST_EARNING){
                results = this.prepareFile(params, this.EVENT_COST_EARNING_FILE_NAME, FUSION_EVENT_COST_EARNING);
           }
           else if(requestType == this.EDUCATION_ALLOWANCE_COST_EARNING){
                results = this.prepareFile(params, this.EDUCATION_ALLOWANCE_COST_EARNING_FILE_NAME, FUSION_EDUCATION_ALLOWANCE_COST_EARNING);
           }
           else if(requestType == this.REPAYMET_TICKET_COST_EARNING){
                results = this.prepareFile(params, this.REPAYMET_TICKET_COST_EARNING_FILE_NAME, FUSION_REPAYMET_TICKET_COST_EARNING);
           }
           else if(requestType == this.SPECIAL_NEED_COST_EARNING){
                results = this.prepareFile(params, this.SPECIAL_NEED_COST_EARNING_FILE_NAME, FUSION_SPECIAL_NEED_COST_EARNING);
           }
           else if(requestType == this.LOAN_COST_EARNING){
                results = this.prepareFile(params, this.LOAN_COST_EARNING_FILE_NAME, FUSION_LOAN_COST_EARNING);
           }
           else if(requestType == this.TRAVEL_COST_EARNING){
                results = this.prepareFile(params, this.TRAVEL_COST_EARNING_FILE_NAME, FUSION_TRAVEL_COST_EARNING);
           }
        
        else{
            throw new Exception("Invalid Request Type");
        }
        
       
       String uploadProcessId=null;
       if(results!=null){
           /* Commented by Moshina 2024/02/27
           * For delink the element entry creation for all the requests
           
          //uploadProcessId= uploadFusionRequest(results);
       }

         System.err.println("uploadProcessId is >>>>>>"+uploadProcessId);
       }*/
       
    private byte[] prepareFile(Map<String,String> params,String templateFileName,String fusionFileName)throws Exception{
        
        String fileContent =getFileContent(templateFileName);
        fileContent=this.replaceParams(params, fileContent);
        
        byte[] zipFile = getZipStream(fileContent, fusionFileName) ;
       
        return zipFile ;        
    }
    
    private String getFileContent(String fileName)throws IOException{
        
//        BufferedReader br = new BufferedReader(new FileReader(fileName));
        InputStream inStream =this.getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
        
       // BufferedReader bbr =new BufferedReader(inStream);
        try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    System.out.println(line);
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                return sb.toString();
            } finally {
                br.close();
            }
                
    }
    
    private String replaceParams(Map<String,String> params,String fileContent){
        for(String key : params.keySet()){
            String value = params.get(key);
            fileContent = fileContent.replaceAll("PLACEHOLDER_"+key, value);
        }
        System.out.println("fileContent : "+fileContent);
        return fileContent;
    }
    
    private byte[] getZipStream(String fileContent,String fileName)throws Exception{
      
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream  zis = new ZipOutputStream(byteStream);
        ZipEntry e = new ZipEntry(fileName);
        zis.putNextEntry(e);
        byte[] data = fileContent.toString().getBytes();
        zis.write(data, 0, data.length);
        zis.closeEntry();

        zis.close();
       return  byteStream.toByteArray();
        
    }
    
    /*Method added by Moshina - Auto EES creation
     * 
     */
    private Map<String, String> uploadFusionRequest(byte[] data)throws Exception{
       Map<String, String> fileName= fusionWrapper.uploadToContent(data);
       System.out.println("UCM filename: "+fileName);
       return fileName;
    }
    
    /*Commented by Moshina - Auto EES creation
     * 
    private String uploadFusionRequest(byte[] data)throws Exception{
       String fileName= fusionWrapper.uploadToContent(data);
       System.out.println(fileName);
        
      String processId=fusionWrapper.loadToHCM(fileName);
        return processId;
    }*/
    
}
