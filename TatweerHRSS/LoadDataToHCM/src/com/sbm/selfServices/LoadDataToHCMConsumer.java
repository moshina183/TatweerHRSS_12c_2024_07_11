package com.sbm.selfServices;

import com.oracle.xmlns.apps.hcm.common.dataloader.core.dataloaderintegrationservice.HCMDataLoader;
import com.oracle.xmlns.apps.hcm.common.dataloader.core.dataloaderintegrationservice.HCMDataLoader_Service;

import com.oracle.xmlns.apps.hcm.common.dataloader.core.dataloaderintegrationservice.ServiceException;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class LoadDataToHCMConsumer {
    SecurityPoliciesFeature securityFeatures;
    private static String endpointUrl =                                         
    "https://egwo-dev1.fa.em2.oraclecloud.com/hcmCommonDataLoader/HCMDataLoader";
    static final String KEY_STORE = "C:\\Oracle\\Middleware\\jdk160_24\\jre\\lib\\security\\cacerts";
    HCMDataLoader DL=null;
    public LoadDataToHCMConsumer() {
        super();
        securityFeatures =
                new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
//        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump = true;
        
        HCMDataLoader_Service hcmDL=new HCMDataLoader_Service();
        DL=hcmDL.getHCMDataLoaderSoapHttpPort(securityFeatures);
        
        Map requestContext = ((BindingProvider)DL).getRequestContext();
        try {
            setPortCredentialProviderList(requestContext);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoadDataToHCMConsumer loadDataToHCMConsumer = new LoadDataToHCMConsumer();
    }
    
    public static void setPortCredentialProviderList(Map requestContext) throws Exception {
   
        String username = "PaaS.Self Service@tatweer.sa";
        String password = "PAASTatweer@2020";
        requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

    }
    
    public String importAndLoadData(String content){
        
        
 

        String andLoadData=null;

        try {
            andLoadData = DL.importAndLoadData(content, null);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return andLoadData;
    }
}
