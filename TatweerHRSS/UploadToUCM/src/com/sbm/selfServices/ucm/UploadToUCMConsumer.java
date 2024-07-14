package com.sbm.selfServices.ucm;

import com.oracle.ucm.Field;
import com.oracle.ucm.File;
import com.oracle.ucm.Generic;

import com.oracle.ucm.Service;

import genericsoap.GenericSoapPortType;
import genericsoap.GenericSoapService;

import java.util.Map;

import javax.activation.DataHandler;

import javax.activation.DataSource;

import javax.mail.util.ByteArrayDataSource;

import javax.xml.ws.BindingProvider;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class UploadToUCMConsumer {
    SecurityPoliciesFeature securityFeatures;
    private static String endpointUrl =                                         
    "https://egwo-dev1.fa.em2.oraclecloud.com:443/idcws/GenericSoapPort";
    static final String KEY_STORE = "C:\\Oracle\\Middleware\\jdk160_24\\jre\\lib\\security\\cacerts";
    
   GenericSoapService genericSoapService = new GenericSoapService();
    GenericSoapPortType genericSoapPortType =null;
        
    
    public UploadToUCMConsumer() {
        super();
        securityFeatures =
                new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
//        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump = true;
   
        
       
        genericSoapPortType = genericSoapService.getGenericSoapPort(securityFeatures);
        
        Map requestContext = ((BindingProvider)genericSoapPortType).getRequestContext();
        try {
            setPortCredentialProviderList(requestContext);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

   
    
    public void setPortCredentialProviderList(Map requestContext) throws Exception {
    
        String username = "PaaS.Self Service@tatweer.sa";
        String password = "PAASTatweer@2020";
        requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

    }
    
    public Generic genericSoapOperation( byte[] file, String contentType,
                               String dDocTitle, String dDocAuthor, String dSecurityGroup, String dDocAccount,
                               String dDocName){
        
        
        
        com.oracle.ucm.Generic genericRequest= new com.oracle.ucm.Generic();
        Service service =  new Service();
        Service.Document doc = new Service.Document();
        service.setIdcService("CHECKIN_UNIVERSAL");
        
        Field dDocTitleField=  new Field();
        dDocTitleField.setName("dDocTitle");
        dDocTitleField.setValue(dDocTitle);
        doc.getField().add(dDocTitleField);
        
        Field dDocType=  new Field();
        dDocType.setName("dDocType");
        dDocType.setValue(contentType);
        doc.getField().add(dDocType);
        
        Field dDocAuthorField=  new Field();
        dDocAuthorField.setName("dDocAuthor");
        dDocAuthorField.setValue(dDocAuthor);
        doc.getField().add(dDocAuthorField);
        
        
        Field dSecurityGroupField=  new Field();
        dSecurityGroupField.setName("dSecurityGroup");
        dSecurityGroupField.setValue(dSecurityGroup);
        doc.getField().add(dSecurityGroupField);
        
        Field dDocAccountField=  new Field();
        dDocAccountField.setName("dDocAccount");
        dDocAccountField.setValue(dDocAccount);
        doc.getField().add(dDocAccountField);
        
        Field dDocNameField=  new Field();
        dDocNameField.setName("dDocName");
        dDocNameField.setValue(dDocTitle);
        doc.getField().add(dDocNameField);
        
        Field primaryFileField=  new Field();
        primaryFileField.setName("primaryFile");
        primaryFileField.setValue(dDocName+".zip");
        doc.getField().add(primaryFileField);
        
            
        File content = new File();
        DataSource dataSource =
        new ByteArrayDataSource(file, dDocName);
        DataHandler data =new DataHandler(dataSource);
        
        content.setContents(data);
        System.err.println("Content data is >>>>>>"+data);
        content.setName("primaryFile");
        content.setHref(dDocName+".zip");
        doc.getFile().add(content);
        
        service.setDocument(doc);
        //      service.set
        genericRequest.setService(service);
        
        genericRequest.setWebKey("cs");
      return  genericSoapPortType.genericSoapOperation(genericRequest);
        
    }
}
