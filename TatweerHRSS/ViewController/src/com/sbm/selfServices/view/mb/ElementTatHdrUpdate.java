package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.view.utils.ADFUtils;
import oracle.binding.OperationBinding;
import oracle.jbo.domain.Number;
import java.util.*;
//EES new java class created by Moshina
public class ElementTatHdrUpdate {
    
    public static String executeTatHdrUpdatePackage(String request_id, String person_no, String request_status, String doc_title,String doc_author,String security_group,
            
                                                                                     String element_name, String doc_account, String doc_type, String base_sixtyfour_file) {
            OperationBinding op = ADFUtils.findOperation("callTatHdrUpdateProcess");  
            String returnValue=null;
            op.getParamsMap().put("request_id", request_id);
            op.getParamsMap().put("person_no", person_no);
            op.getParamsMap().put("request_status", request_status);
            op.getParamsMap().put("element_name", element_name);
            op.getParamsMap().put("doc_title", doc_title);        
            op.getParamsMap().put("doc_author", doc_author);
            op.getParamsMap().put("security_group", security_group);
            op.getParamsMap().put("doc_account", doc_account);
            op.getParamsMap().put("doc_type", doc_type);
            op.getParamsMap().put("base_sixtyfour_file", base_sixtyfour_file);
                    
            returnValue = (String)op.execute();
            if(returnValue!=null && returnValue.equalsIgnoreCase("Success")) {
                return  returnValue;
            }
            else {
                return  returnValue;
            }
        }
    
}
