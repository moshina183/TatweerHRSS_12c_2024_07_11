package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.view.utils.ADFUtils;

import oracle.binding.OperationBinding;

import oracle.jbo.domain.Number;


public class ApprovalHistory {
    
    
    public static String executeHistoryPackage(oracle.jbo.domain.DBSequence req_id,String req_type,Number prev_step_id,Long steps,String assignee_name,String approval_flow,String final_approve) {
        System.err.println("IN executeHistoryPackage::" + req_id + " - "+req_type);
        System.err.println("prev_step_id::"+prev_step_id);
        System.err.println("steps::"+steps);
        OperationBinding op =
        ADFUtils.findOperation("callApprovalHistory");  
        String returnValue=null;
        op.getParamsMap().put("req_id", req_id);
        op.getParamsMap().put("req_type", req_type);
        op.getParamsMap().put("prev_step_id", prev_step_id);        
        op.getParamsMap().put("step_id", steps);
        op.getParamsMap().put("assignee_name", assignee_name);
        op.getParamsMap().put("approval_flow", approval_flow);
        op.getParamsMap().put("final_approval_flow", final_approve);
        op.getParamsMap().put("approval_type", "Approval Request");
        returnValue = (String)op.execute();
        if(returnValue!=null && returnValue.equalsIgnoreCase("Success")) {
            return  returnValue;
        }
        else {
            return  returnValue;
        }
      
    }
    
    public static String executeHistoryPackageWithdraw(oracle.jbo.domain.DBSequence req_id,String req_type,Number prev_step_id,Long steps,String assignee_name,String approval_flow,String final_approve) {
        System.err.println("IN executeHistoryPackageWithdrawEdit::" + req_id + " - "+req_type);
        System.err.println("prev_step_id::"+prev_step_id);
        System.err.println("steps::"+steps);
        OperationBinding op =
        ADFUtils.findOperation("callApprovalHistory");  
        String returnValue=null;
        op.getParamsMap().put("req_id", req_id);
        op.getParamsMap().put("req_type", req_type);
        op.getParamsMap().put("prev_step_id", prev_step_id);        
        op.getParamsMap().put("step_id", steps);
        op.getParamsMap().put("assignee_name", assignee_name);
        op.getParamsMap().put("approval_flow", approval_flow);
        op.getParamsMap().put("final_approval_flow", final_approve);
        op.getParamsMap().put("approval_type", "Withdraw Request");
        returnValue = (String)op.execute();
        if(returnValue!=null && returnValue.equalsIgnoreCase("Success")) {
            return  returnValue;
        }
        else {
            return  returnValue;
        }
      
    }
  
   
  
}

