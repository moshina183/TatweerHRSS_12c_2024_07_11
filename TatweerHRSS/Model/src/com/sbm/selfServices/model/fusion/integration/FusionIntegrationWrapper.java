package com.sbm.selfServices.model.fusion.integration;

import com.oracle.ucm.Generic;
import com.oracle.xmlns.apps.hcm.common.dataloader.core.dataloaderintegrationservice.HCMDataLoaderSoapHttpPortClient;

import com.sbm.selfServices.LoadDataToHCMConsumer;
import com.octetstring.vde.util.Base64;

import com.sbm.selfServices.ucm.UploadToUCMConsumer;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;


import java.util.Date;


import java.util.*;

import oracle.stellent.ridc.IdcClient;
import oracle.stellent.ridc.IdcClientException;
import oracle.stellent.ridc.IdcClientManager;
import oracle.stellent.ridc.IdcContext;
import oracle.stellent.ridc.model.DataBinder;
import oracle.stellent.ridc.model.TransferFile;
import oracle.stellent.ridc.protocol.ServiceResponse;


       

public class FusionIntegrationWrapper {
//    String username = "HCM.TRACK";
//    String password = "12345678";
    
        String username = "PaaS.Self Service@tatweer.sa";
        String password = "PAASTatweer@2020";
    private String ucmPath="https://egwo-dev1.fa.em2.oraclecloud.com/cs/idcplg";
    

    LoadDataToHCMConsumer loadDataToHCMConsumer ;
    UploadToUCMConsumer ucmUploader;
    public FusionIntegrationWrapper() {
        super();
        ucmUploader = new UploadToUCMConsumer();
        loadDataToHCMConsumer = new LoadDataToHCMConsumer();
    }
    
    public String loadToHCM(String docId)throws Exception{
        
        return loadDataToHCMConsumer.importAndLoadData(docId);
    }
    
    public Map<String, String> uploadToContent(byte[] zipFilePath)throws Exception{
        String docId ="HCM_"+(new Date()).getTime();
        Map<String, String> mapData = new HashMap<String, String>();
        try{
            IdcClientManager m_clientManager = new IdcClientManager();
            IdcClient idcClient = m_clientManager.createClient(ucmPath);
            IdcContext userContext = new IdcContext(username,password);
            System.out.println("zipFilePath : "+zipFilePath);
            String base64String = Base64.encode(zipFilePath).toString();
            System.out.println("base64String: "+base64String);
            mapData.put("dDocTitle",docId);
            mapData.put("dDocAuthor",userContext.getUser());
            mapData.put("base64String",base64String);
            mapData.put("dSecurityGroup", "FAFusionImportExport");
            mapData.put("dDocAccount", "hcm$/dataloader$/import$");
            mapData.put("contentType", "Document");
        }catch(IdcClientException e){
            e.printStackTrace();
            throw new Exception(e.getMessage(),e);
        }
        return mapData;
    }
    
    
   
    /*Auto - EES Commented by Moshina
     * 
    public String uploadToContent(byte[] zipFilePath)throws Exception{
        
        String docId ="HCM_"+(new Date()).getTime();
        try{
            IdcClientManager m_clientManager = new IdcClientManager();
            IdcClient idcClient =
                m_clientManager.createClient(ucmPath);
            IdcContext userContext = new IdcContext(username,password);
            
            checkin(idcClient, userContext, zipFilePath,
                    // Replace with fully qualified path to source file
                    "Document", // content type
                    docId, // doc title
                    userContext.getUser(), // author
                    "FAFusionImportExport", // security group  FAFusionImportExport
                    "hcm$/dataloader$/import$", // account
                    docId); // dDocName - this is the ContentId
        }catch(IdcClientException e){
            e.printStackTrace();
            throw new Exception(e.getMessage(),e);
        }
        return docId;
    }
    */
    
    /**
     * Method description
     * @param idcClient
     * @param userContext
     * @param sourceFileFQP fully qualified path to source content
     * @param contentType content type
     * @param dDocTitle doc title
     * @param dDocAuthor author
     * @param dSecurityGroup security group
     * @param dDocAccount account
     * @param dDocName dDocName
     *
     * @throws IdcClientException
     */
    private void checkin(IdcClient idcClient, IdcContext userContext, byte[] file, String contentType,
                               String dDocTitle, String dDocAuthor, String dSecurityGroup, String dDocAccount,
                               String dDocName) throws IdcClientException {
        InputStream is = null;
        try {
            String fileName = "HCM_FILE_"+(new Date()).getTime()+".zip";
//            is = new ByteArrayInputStream(file);
//            long fileLength = file.length;
//            TransferFile primaryFile = new TransferFile();
//            primaryFile.setInputStream(is);
//            primaryFile.setContentType(contentType);
//            primaryFile.setFileName(fileName);
//            primaryFile.setContentLength(fileLength);
//            // note!!! when using HTTP protocol (not intradoc/jaxws) - you must explicitly
//            // set the Content Length when supplying an InputStream to the transfer file
//            // e.g. primaryFile.setContentLength(xxx);
//            // otherwise, a 0-byte file results on the server
//            DataBinder request = idcClient.createBinder();
//            request.putLocal("IdcService", "CHECKIN_UNIVERSAL");
//            request.addFile("primaryFile", primaryFile);
//            request.putLocal("dDocTitle", dDocTitle);
//            request.putLocal("dDocAuthor", dDocAuthor);
//            request.putLocal("dDocType", contentType);
//            request.putLocal("dSecurityGroup", dSecurityGroup);
//            // if server is setup to use accounts - an account MUST be specified
//            // even if it is the empty string; supplying null results in Content server
//            // attempting to apply an account named "null" to the content!
//            request.putLocal("dDocAccount", dDocAccount == null ? "" : dDocAccount);
//
//            if (dDocName != null && dDocName.trim().length() > 0) {
//                request.putLocal("dDocName", dDocName);
//            }
//            // execute the request
//            ServiceResponse response = idcClient.sendRequest(userContext, request); // throws IdcClientException
//            // get the binder - get a binder closes the response automatically
//            DataBinder responseBinder = response.getResponseAsBinder(); // throws IdcClientException
           System.err.println("contentType is >>>>>>"+contentType); 
            System.err.println("dDocTitle is >>>>>>"+dDocTitle); 
            System.err.println("dDocAuthor is >>>>>>"+dDocAuthor); 
            System.err.println("dSecurityGroup is >>>>>>"+dSecurityGroup); 
            System.err.println("dDocAccount is >>>>>>"+dDocAccount); 
            System.err.println("dDocName is >>>>>>"+dDocName); 
           Generic results=  ucmUploader.genericSoapOperation(file, contentType, dDocTitle, dDocAuthor, dSecurityGroup, dDocAccount, dDocName);
            
            System.err.println("uploadcontent result is >>>>>>"+results);
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                    ignore.printStackTrace();
                }
            }
        }
    }
    
 
    
}
