package com.sbm.selfServices.view.utils;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.ServiceException;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetailsResult;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetailsService;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetailsService_Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import javax.xml.ws.handler.MessageContext;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;
import weblogic.wsee.jws.jaxws.owsm.SecurityPolicyFeature;


public class UserServiceUtil {
   
   String endpointUrl =
       "https://egwo-dev1.fa.em2.oraclecloud.com/hcmPeopleRolesV2/UserDetailsService?WSDL";
      
    String KEY_STORE = //"D:\\Project\\Tatweer\\certificate_weblogic\\cacerts";
        "C:\\Oracle\\Middleware\\jdk160_24\\jre\\lib\\security\\cacerts";
    private UserDetailsService_Service userDetailsService_Service;

    public void setPortCredentialProviderList(Map requestContext) throws Exception {
        String username = "PaaS.Self Service@tatweer.sa";
        String password = "PAASTatweer@2020";
        requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                           endpointUrl);
    }

    public UserDetails getUserDetailsByPersonNumber(String personNumber) {
        SecurityPoliciesFeature securityFeatures =
            new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
        //                                                   new SecurityPoliciesFeature(new String[] { "oracle/wss11_username_token_with_message_protection_service_policy" });
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                           "true");
//                System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
//                System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        userDetailsService_Service = new UserDetailsService_Service();
        UserDetailsService userDetailsService =
            userDetailsService_Service.getUserDetailsServiceSoapHttpPort(securityFeatures);
        Map requestContext =
            ((BindingProvider)userDetailsService).getRequestContext();

        try {
            setPortCredentialProviderList(requestContext);
        } catch (Exception e) {
            System.out.println("hamada1");
            return null;
        }
        UserDetailsResult byUsername;
        UserDetails details = null;
        try {
            byUsername =
                    userDetailsService.findUserDetailsByPersonNumber(personNumber);
            System.out.println("byUsername is " + byUsername);
            List<UserDetails> list = byUsername.getValue();
            System.out.println("list size is " + list.size());
            details = list.get(0);
            //Person id added by Moshina - 24-03-2024
            System.out.println("Person id: "+details.getPersonId());
        } catch (ServiceException e) {
            System.out.println("hamada 2");
            return null;
        }

        return details;

    }

    public UserDetails getUserDetailsByPersonId(long personId) {
        SecurityPoliciesFeature securityFeatures =
            new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                           "true");
//                System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
//                System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

        userDetailsService_Service = new UserDetailsService_Service();
        UserDetailsService userDetailsService =
            userDetailsService_Service.getUserDetailsServiceSoapHttpPort(securityFeatures);
        Map requestContext =
            ((BindingProvider)userDetailsService).getRequestContext();

        try {
            setPortCredentialProviderList(requestContext);
        } catch (Exception e) {
            System.out.println("hamada1");
            return null;
        }
        UserDetailsResult byUsername;
        UserDetails details = null;
        try {
            byUsername =
                    userDetailsService.findUserDetailsByPersonId(personId);
            System.out.println("byUsername is " + byUsername);
            List<UserDetails> list = byUsername.getValue();
            System.out.println("list size is " + list.size());
            details = list.get(0);
            System.out.println(details.getPersonId());
        } catch (ServiceException e) {
            System.out.println("hamada 2");
            return null;
        }

        return details;

    }


    public UserDetails getUserDetailsByJWT(String jwt) {
        UserDetailsService_Service service_Service;
        UserDetailsService service = null;
        // JWT requires no client policy
//        SecurityPolicyFeature[] securityFeatures =
//        { new SecurityPolicyFeature("") };---Weblogic Oracle Configuration
//        SecurityPoliciesFeature securityFeatures =
//  new SecurityPoliciesFeature(new String[] { "customer/customjwtpolicy" });

                SecurityPoliciesFeature securityFeatures =
            new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy_Copy" });
        // new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                           "true");
//                        System.setProperty("javax.net.ssl.trustStore", KEY_STORE);
//                        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        service_Service = new UserDetailsService_Service();
        service =
                service_Service.getUserDetailsServiceSoapHttpPort(securityFeatures);
        if (service == null) {
            System.out.println("SBM: UsersService is null");
        } else {
            System.out.println("SBM:  UsersService created.");
        }
        // add credentials and keystore details
        //String jwt = JSFUtils.resolveExpressionAsString("#{pageFlowScope.jwt}");
        // add JWT auth map to HTTP header
        //BindingProvider bp = (BindingProvider)service;
        BindingProvider bp = (BindingProvider)service;
        Map<String, List<String>> authMap =
            new HashMap<String, List<String>>();
        List<String> authZlist = new ArrayList<String>();
        authZlist.add(new StringBuilder().append("Bearer ").append(jwt).toString());
        authMap.put("Authorization", authZlist);
        System.out.println("AuthMap:" + authMap.toString());
        bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
                                   authMap);

        UserDetailsResult byUsername;
        UserDetails details = null;
        try {
            System.out.println("byusername before ");
            byUsername = service.findSelfUserDetails();
            System.out.println("byUsername is " + byUsername);
            List<UserDetails> list = byUsername.getValue();
            System.out.println("list size is " + list.size());
            details = list.get(0);
            System.out.println(details.getPersonId());
        } catch (ServiceException e) {
            System.out.println("hamada 2");
            return null;
        }

        return details;
    }

    public static void main(String[] args) {
        UserServiceUtil userServiceUtil = new UserServiceUtil();
        UserDetails byJWT = userServiceUtil.getUserDetailsByJWT("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6IkY3LVhDZFJHdE9RUFU1cWxpU09yeXNNVTlFZyIsImtpZCI6InRydXN0c2VydmljZSJ9.eyJleHAiOjE1NTkwMjYxNjAsInN1YiI6Ik1vaGFtbWVkLkFiZHVsd2FoZWQxQHRhdHdlZXIuc2EiLCJpc3MiOiJ3d3cub3JhY2xlLmNvbSIsInBybiI6Ik1vaGFtbWVkLkFiZHVsd2FoZWQxQHRhdHdlZXIuc2EiLCJpYXQiOjE1NTkwMTE3NjB9.neOWNQxHXwUD6-fXtPaAIBGSCc9b6IQkYRh5kps2sQoH_HTwrm_kAu9KuhooJY5z5lDhF-Wx9LiKtRWT5iKtKDCxD-kBaXbdsICgXINdkEC-ww9dNoYO_UuqblyX7wlHOBpPNcx1Z-aWrCe41wP7NKBVoy7KsIFtezj_wRCZ_GPdIVvdET1T8WQQzBzpP8ZVtc4AN5zvu1qQR1DtP9ISwa0PasU8vywiXQEgbWhqRJNPJ3PM4X6sYAyDDjBO01htSaL5LeDJlMSubo9N8COGHdzrK96N0cPZ6P9ZIiNI-vqtUU2T-ls_utisYqD8utfioWgc5dYlcfgecfHyFPb8HQ&_afrLoop=11944750146828656&_afrWindowMode=0&_afrWindowId=null&_adf.ctrl-state=croigxbji_1&_afrFS=16&_afrMT=screen&_afrMFW=1600&_afrMFH=762&_afrMFDW=1280&_afrMFDH=720&_afrMFC=8&_afrMFCI=0&_afrMFM=0&_afrMFR=115&_afrMFG=0&_afrMFS=0&_afrMFO=0");
        System.err.println("byJWT.getPersonNumber()"  +byJWT.getPersonNumber());
    }
}
