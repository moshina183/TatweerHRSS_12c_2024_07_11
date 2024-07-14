package com.sbm.selfServices.view.utils;

import com.oracle.xmlns.oxp.service.v2.AccessDeniedException_Exception;
import com.oracle.xmlns.oxp.service.v2.ArrayOfParamNameValue;
import com.oracle.xmlns.oxp.service.v2.ArrayOfString;
import com.oracle.xmlns.oxp.service.v2.InvalidParametersException_Exception;
import com.oracle.xmlns.oxp.service.v2.OperationFailedException_Exception;
import com.oracle.xmlns.oxp.service.v2.ParamNameValue;
import com.oracle.xmlns.oxp.service.v2.ParamNameValues;
import com.oracle.xmlns.oxp.service.v2.ReportRequest;
import com.oracle.xmlns.oxp.service.v2.ReportResponse;
import com.oracle.xmlns.oxp.service.v2.ReportService;
import com.oracle.xmlns.oxp.service.v2.ReportService_Service;

import java.io.ByteArrayInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class ReportsUtils {
    private static ReportService_Service reportService_Service;
    static final String KEY_STORE =
        "C:\\Oracle\\Middleware\\jdk160_24\\jre\\lib\\security\\cacerts";

    public void setPortCredentialProviderList(Map requestContext) throws Exception {
//        String username = "HCM.TRACK";
//        String password = "12345678";
        String username = "PaaS.Self Service@tatweer.sa";
        String password = "PAASTatweer@2020";
        requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        //  requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
    }

    public byte[] callRunReport(ParamNameValues paramNameValues,
                                String reportPath) {
//        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//        System.setProperty("javax.net.ssl.keyStore", KEY_STORE);
//        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump","true");

        reportService_Service = new ReportService_Service();
        ReportService reportService = reportService_Service.getReportService();
        Map requestContext =
            ((BindingProvider)reportService).getRequestContext();
        // Add your code to call the desired methods.
        try {
            setPortCredentialProviderList(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setAttributeFormat("xml");
        reportRequest.setAttributeLocale("en-us");

        //Set User Parameter P_DEPT
        reportRequest.setParameterNameValues(paramNameValues);

        reportRequest.setReportAbsolutePath(reportPath);
        reportRequest.setSizeOfDataChunkDownload(-1);
        ReportResponse reportResponse;
        byte[] bytes = null;
        try {
            reportResponse =
                    reportService.runDataModel(reportRequest, "PaaS.Self Service@tatweer.sa",
                                               "PAASTatweer@2020");
           
//                       reportResponse =
//                    reportService.runDataModel(reportRequest, "msaber@sbm.com.sa",
//                                               "SBM_2018");
            if (reportResponse != null) {
                bytes = reportResponse.getReportBytes();
                return bytes;
            }
        } catch (InvalidParametersException_Exception e) {
            e.printStackTrace();
        } catch (AccessDeniedException_Exception e) {
            e.printStackTrace();
        } catch (OperationFailedException_Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }
    
    
    public byte[] callRunReport(String reportPath) {
    //        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
    //        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    //        System.setProperty("javax.net.ssl.keyStore", KEY_STORE);
    //        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump","true");

        reportService_Service = new ReportService_Service();
        ReportService reportService = reportService_Service.getReportService();
        Map requestContext =
            ((BindingProvider)reportService).getRequestContext();
        // Add your code to call the desired methods.
        try {
            setPortCredentialProviderList(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setAttributeFormat("xml");
        reportRequest.setAttributeLocale("en-us");

        reportRequest.setReportAbsolutePath(reportPath);
        reportRequest.setSizeOfDataChunkDownload(-1);
        ReportResponse reportResponse;
        byte[] bytes = null;
        try {
            reportResponse =
                    reportService.runDataModel(reportRequest, "PaaS.Self Service@tatweer.sa",
                                               "PAASTatweer@2020");
           
    //                       reportResponse =
    //                    reportService.runDataModel(reportRequest, "msaber@sbm.com.sa",
    //                                               "SBM_2018");
            if (reportResponse != null) {
                bytes = reportResponse.getReportBytes();
                return bytes;
            }
        } catch (InvalidParametersException_Exception e) {
            e.printStackTrace();
        } catch (AccessDeniedException_Exception e) {
            e.printStackTrace();
        } catch (OperationFailedException_Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public String callActionReasoneReport(String personNumber) {
        System.out.println("person is " + personNumber);
        ArrayOfParamNameValue arrayOfParamNameValue =
            new ArrayOfParamNameValue();
        ParamNameValues paramNameValues = new ParamNameValues();
        ParamNameValue paramNameValue = new ParamNameValue();
        ArrayOfString arrayOfString = new ArrayOfString();
        paramNameValue.setName("P_EMP_NO");
        arrayOfString.getItem().add(personNumber);
        paramNameValue.setValues(arrayOfString);
        arrayOfParamNameValue.getItem().add(paramNameValue);
        paramNameValues.setListOfParamNameValues(arrayOfParamNameValue);
        System.out.println("params are  " + paramNameValues.getListOfParamNameValues().getItem().get(0));
        byte[] bytes =
            callRunReport(paramNameValues, "/~HCM.TRACK/data sets/THC_EMP_ACTION_REASON.xdm");
        System.out.println("lenth is " + bytes.length);
        String reportBytes = new String(bytes);
        //   System.out.println("-----:" + reportBytes);
        DocumentBuilderFactory docBuilderFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc =
docBuilder.parse(new InputSource(new ByteArrayInputStream(reportBytes.getBytes("utf-8"))));
            System.out.println("Root element of the doc is " +
                               doc.getDocumentElement().getNodeName());
            NodeList listOfPersons = doc.getElementsByTagName("G_1");
            Node firstPersonNode = listOfPersons.item(0);
            Element firstPersonElement = (Element)firstPersonNode;
            if (firstPersonElement != null && firstPersonElement.getElementsByTagName("ACTION_REASON_CODE").item(0) != null) {
                return firstPersonElement.getElementsByTagName("ACTION_REASON_CODE").item(0).getTextContent();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<String> getPersonByPostionReport(String postion) {
        System.out.println("postion is " + postion);
        ArrayOfParamNameValue arrayOfParamNameValue =
            new ArrayOfParamNameValue();
        ParamNameValues paramNameValues = new ParamNameValues();
        ParamNameValue paramNameValue = new ParamNameValue();
        ArrayOfString arrayOfString = new ArrayOfString();
        paramNameValue.setName("position_name");
        arrayOfString.getItem().add(postion);
        paramNameValue.setValues(arrayOfString);
        arrayOfParamNameValue.getItem().add(paramNameValue);
        paramNameValues.setListOfParamNameValues(arrayOfParamNameValue);
        System.out.println("params are  " + paramNameValues.getListOfParamNameValues().getItem().get(0));
        byte[] bytes =
            callRunReport(paramNameValues, "/Custom/THC Position.xdm");
        System.out.println("lenth is " + bytes.length);
        String reportBytes = new String(bytes);
        //   System.out.println("-----:" + reportBytes);
        DocumentBuilderFactory docBuilderFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc = null;
        ArrayList<String> person=new ArrayList<String>();  
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc =
    docBuilder.parse(new InputSource(new ByteArrayInputStream(reportBytes.getBytes("utf-8"))));
            System.out.println("Root element of the doc is " +
                               doc.getDocumentElement().getNodeName());
        
            NodeList listOfPersons = doc.getElementsByTagName("G_1");
            System.err.println("length of List >>>> "+listOfPersons.getLength());
            Node firstPersonNode = listOfPersons.item(0);
            Element firstPersonElement = (Element)firstPersonNode;
            if (firstPersonElement != null && firstPersonElement.getElementsByTagName("PERSON_NUMBER").item(0) != null) {
                person.add(firstPersonElement.getElementsByTagName("PERSON_NUMBER").item(0).getTextContent());
            }
            if (firstPersonElement != null && firstPersonElement.getElementsByTagName("DISPLAY_NAME").item(0) != null) {
                person.add(firstPersonElement.getElementsByTagName("DISPLAY_NAME").item(0).getTextContent());
            }
            if (firstPersonElement != null && firstPersonElement.getElementsByTagName("EMAIL_ADDRESS").item(0) != null) {
                person.add(firstPersonElement.getElementsByTagName("EMAIL_ADDRESS").item(0).getTextContent());
            }
            return person;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
//    public ArrayList<HashMap> getDepartmentsReport() {
//        
//        
//        
//       
//        byte[] bytes =
//            callRunReport( "/Custom/Departments.xdm");
//        System.out.println("lenth is " + bytes.length);
//        String reportBytes = new String(bytes);
//        //   System.out.println("-----:" + reportBytes);
//        DocumentBuilderFactory docBuilderFactory =
//            DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder;
//        Document doc = null;
//        Map department = new HashMap();
//    
//        ArrayList<HashMap> departments=new ArrayList<HashMap>(); 
//        
//        try {
//            docBuilder = docBuilderFactory.newDocumentBuilder();
//            doc =
//    docBuilder.parse(new InputSource(new ByteArrayInputStream(reportBytes.getBytes("utf-8"))));
//            System.out.println("Root element of the doc is " +
//                               doc.getDocumentElement().getNodeName());
//            NodeList listOfDepartments = doc.getElementsByTagName("G_1");
//            int length=listOfDepartments.getLength();
//            System.err.println("listOfDepts.getLength() >> "+length);
//            for(int i=0;i<length;i++)
//                
//            {
//                   
//                    Node currentDeptNode = listOfDepartments.item(i);
//                    Element currentDeptElement = (Element)currentDeptNode;
//                    if (currentDeptElement != null && currentDeptElement.getElementsByTagName("ORGANIZATION_ID").item(i) != null) {
//                        department.put("ORGANIZATION_ID",currentDeptElement.getElementsByTagName("ORGANIZATION_ID").item(i).getTextContent());
//                        System.err.println("org Id is > "+currentDeptElement.getElementsByTagName("ORGANIZATION_ID").item(i).getTextContent());
//                    }
//                    if (currentDeptElement != null && currentDeptElement.getElementsByTagName("NAME").item(i) != null) {
//                        department.put("NAME",currentDeptElement.getElementsByTagName("NAME").item(i).getTextContent());
//                        System.err.println("NAME is > "+currentDeptElement.getElementsByTagName("NAME").item(i).getTextContent());
//                    }
//                    
//                    departments.add((HashMap)department);
//                }
//            
//          
//            return departments;
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    
}
