package com.view.beans.filmStripApp;

import com.mivors.model.bi.integration.BiReportAccess;

import com.octetstring.vde.util.Base64;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserPersonDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.selfServices.model.views.up.DepartmentsVORowImpl;
import com.sbm.selfServices.model.views.up.LocationsVORowImpl;
import com.sbm.selfServices.view.utils.PersonInfo;
import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStrip.SessionState;
import com.view.uiutils.ADFUtils;
import com.view.uiutils.JSFUtils;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import javax.xml.bind.JAXBElement;

import oracle.adf.controller.v2.lifecycle.Lifecycle;
import oracle.adf.controller.v2.lifecycle.PagePhaseEvent;
import oracle.adf.controller.v2.lifecycle.PagePhaseListener;
import oracle.adf.model.BindingContext;
import oracle.adf.model.OperationBinding;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import oracle.jbo.domain.Number;

import org.json.JSONException;
import org.json.JSONObject;

public class FilmStripPhaseListener implements PagePhaseListener {

    public FilmStripPhaseListener() {
        super();
    }


    public void afterPhase(PagePhaseEvent phaseEvent) {
    }

    public void beforePhase(PagePhaseEvent phaseEvent) {
        if (phaseEvent.getPhaseId() == Lifecycle.PREPARE_RENDER_ID) {
            onPageLoad();
        }
    }

    public PhaseId getPhaseId() {
        return null;
    }

    public void onPageLoad() {
        if (!AdfFacesContext.getCurrentInstance().isPostback()) {
            PersonInfo personInfo =
                (PersonInfo)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{PersonInfo}");
            String personNumber =
                (String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{sessionScope.personNumber}");
           // String jwt ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6IkY3LVhDZFJHdE9RUFU1cWxpU09yeXNNVTlFZyIsImtpZCI6InRydXN0c2VydmljZSJ9.eyJleHAiOjE1ODI1NDg4ODksInN1YiI6Ik1vaGFtbWVkLkFiZHVsd2FoZWQxQHRhdHdlZXIuc2EiLCJpc3MiOiJ3d3cub3JhY2xlLmNvbSIsInBybiI6Ik1vaGFtbWVkLkFiZHVsd2FoZWQxQHRhdHdlZXIuc2EiLCJpYXQiOjE1ODI1MzQ0ODl9.Yic9fOrwe6CxVNMjsoJF1PyciU7YIUmI2aRaozFzCY-ZfpHbucFD7sYGYN4-pZ1tc01OCdH5yaKsjCOjWfFxOmgfxOJoHwJmwdf9Qg1eAAG5DxrVsQDZTNjuBRJWdguOkhcnh3NPwyARdS1_BbIscI1Yh3WTNa9KaSW5XpefJjeJF1WAwGv5Q7ZiZtIOl-QoHOaq91MQQyYMBINt-lHSB6cYozoomSmZWoYmEVKnnqZdDaUdDnhDqHU4XCf5lqJELDl8b75qXEEt9QfKmZMqe4l7SsdKiCGGiBTJ4mqsbilQBW2KdjTDGzrSagvi1MFSbbMux7uVkpE3tj0E8vYCjw";
            String jwt= (String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{sessionScope.jwt}");
            
            
            String comingFrom =
                (String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{sessionScope.comingFrom}");
            System.out.println("session person number:" + personNumber);
            if (jwt != null) {
                if (personInfo.getTokenEmail() == null) {
                    System.out.println("person info email is null");
                    loadPersonData();
                    personInfo.setTokenEmail(getEmailfromToken(jwt));
                } else {
                    System.out.println("person info email is not null");
                    System.out.println("email from person info:" +
                                       personInfo.getEmail());
                    String emailUsername = getEmailfromToken(jwt);
                    System.out.println("email id from jwt:" + emailUsername);
                    if (personInfo.getTokenEmail() != null &&
                        emailUsername != null &&
                        !personInfo.getTokenEmail().equals(emailUsername)) {
                        System.out.println("person info mail and email from token is not same");
                        loadPersonData();
                        personInfo.setTokenEmail(getEmailfromToken(jwt));
                        System.err.println("personInfo.setTokenEmail==="+personInfo.getTokenEmail());
                    }


                }
                JSFUtils.setExpressionValue("#{sessionScope.redirect}",
                                            "Dashboard");
            } else if (comingFrom != null &&
                       comingFrom.equalsIgnoreCase("SAAS")) {
                JSFUtils.setExpressionValue("#{sessionScope.redirect}",
                                            "Error");
            } else if (comingFrom != null &&
                       comingFrom.equalsIgnoreCase("LOGIN")) {
                if (personInfo.getPerosnNumber() == null) {
                    System.out.println("person info person number is null");
                    loadPersonData();
                } else {
                    System.out.println("person info person number is not null");
                    if (personNumber != null &&
                        personInfo.getPerosnNumber() != null &&
                        !personInfo.getPerosnNumber().equals(personNumber)) {
                        System.out.println("person info person number and entered person number is not same");
                        loadPersonData();
                    }
                }
                JSFUtils.setExpressionValue("#{sessionScope.redirect}",
                                            "Dashboard");
            } else {
                JSFUtils.setExpressionValue("#{sessionScope.redirect}",
                                            "Error");
            }
            SessionState sessionState =
                (SessionState)JSFUtils.getManagedBeanValue("sessionScope.SessionState");
            if (sessionState == null) {
                sessionState = new SessionState();
            }
            sessionState.parseRootMenu();
            JSFUtils.setManagedBeanValue("sessionScope.SessionState",
                                         sessionState);
            String groupNodeId = "tatweer";
            String itemNodeId = sessionState.getHomePage();
            
            if (ADFContext.getCurrent().getSessionScope().get("selectedItemId") ==
                null) {
                if (itemNodeId.equals("Local")) {
                    ADFUtils.setEL("#{sessionScope.tripType}", "Local");
                } else if (itemNodeId.equals("International")) {
                    ADFUtils.setEL("#{sessionScope.tripType}", "Inter");
                } else if (itemNodeId.equals("Training")) {
                    ADFUtils.setEL("#{sessionScope.tripType}", "Training");
                } else if (itemNodeId.equals("Event")) {
                    ADFUtils.setEL("#{sessionScope.tripType}", "Event");
                } else if (itemNodeId.equals("Expense")) {
                    ADFUtils.setEL("#{sessionScope.tripType}", "Expense");
                }
                ADFContext.getCurrent().getSessionScope().put("selectedGroupId",
                                                              groupNodeId);
                ADFContext.getCurrent().getSessionScope().put("selectedItemId",
                                                              itemNodeId);
                ADFContext.getCurrent().getSessionScope().put("disableGoHome",
                                                              "N");
                if (groupNodeId.equalsIgnoreCase(itemNodeId)) {
                    ADFContext.getCurrent().getSessionScope().put("hideStrip",
                                                                  true);
                    ADFContext.getCurrent().getSessionScope().put("hideStripToggle",
                                                                  true);
                } else {
                    ADFContext.getCurrent().getSessionScope().put("hideStrip",
                                                                  false);
                    ADFContext.getCurrent().getSessionScope().put("hideStripToggle",
                                                                  false);
                }
                }


        }
    }


    public String getEmailfromToken(String jwt) {
        String inputEncodedText = jwt;
        String[] xIn = inputEncodedText.split("\\.");
        byte b[] = Base64.decode(xIn[1]);
        String tempPass = new String(b);
        int chkOccurance = tempPass.lastIndexOf("}");
        if (chkOccurance < 1) {
            tempPass += "}";
        }
        JSONObject jo;
        try {
            jo = new JSONObject(tempPass);
            System.out.println(jo);
            String emailUsername = jo.getString("prn");
            System.err.println("mail==="+emailUsername);
            return emailUsername;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadPersonData() {
        PersonInfo personInfo =
            (PersonInfo)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{PersonInfo}");
        String personNumber =
            (String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{sessionScope.personNumber}");
        UserServiceUtil userService = new UserServiceUtil();
        String jwt =
            (String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{sessionScope.jwt}");
        UserDetails userDetails = null;
        if (jwt != null) {
            userDetails = userService.getUserDetailsByJWT(jwt);
        } else {
            userDetails =
                    userService.getUserDetailsByPersonNumber(personNumber);
        }

        if (userDetails == null) {
            return;
        }

        if (userDetails.getPersonNumber() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Person number");
            //   com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Person number");
            return;
        }
        personInfo.setPerosnNumber(userDetails.getPersonNumber());


        if (userDetails.getPersonId() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Person Id");
            //    com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Person Id");
            return;
        }

        personInfo.setEmployeeId(userDetails.getPersonId());

        if (userDetails.getUsername() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Name");
            //   com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Name");
            return;
        } else {

            JAXBElement<String> userNameJAXB = userDetails.getUsername();
            personInfo.setUserName(userNameJAXB.getValue());

        }
        com.sbm.selfServices.view.utils.JSFUtils.setExpressionValue("#{sessionScope.personNumber}",
                                                                    userDetails.getPersonNumber());
        System.err.println("Ana Hena Hena >>>" +
                           userDetails.getPersonNumber());
        JSFUtils.setExpressionValue("#{sessionScope.userName}", userDetails.getPersonNumber());

        BiReportAccess BIRA = new BiReportAccess();
        List<Map> listOfEmpSalary;


        try {
            listOfEmpSalary =
                    BIRA.getEmpSalary(userDetails.getPersonNumber().toString());
            for (Map currentEmpSalary : listOfEmpSalary) {
                System.out.println("list.size(); >>>" +
                                   listOfEmpSalary.size());
                System.out.println("I'm Inside for each loop");
                System.out.println("*****************");
                System.err.println("PERSON_NUMBER >>> " +
                                   currentEmpSalary.get("PERSON_NUMBER"));
                JSFUtils.setExpressionValue("#{sessionScope.userName}", currentEmpSalary.get("PERSON_NAME"));
                System.out.println("*****************");
                System.err.println("PERSON_NAME >>> ! " +
                                   currentEmpSalary.get("PERSON_NAME"));
                System.out.println("*****************");
                System.err.println("token Email"+personInfo.getTokenEmail());
                if (currentEmpSalary.get("SALARY_AMOUNT") != null) {
                    System.err.println("SALARY_AMOUNT >>> " +
                                       currentEmpSalary.get("SALARY_AMOUNT"));
                    System.out.println("################################");
                    personInfo.setSalary(currentEmpSalary.get("SALARY_AMOUNT").toString());
                }else{
                    System.err.println("Inside Salary Empty else block!!!!!");
                    personInfo.setSalary("");
                }

            }
            listOfEmpSalary.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map> listOfEmpIsManager = null;
        try {
            listOfEmpIsManager =
                    BIRA.getEmpIsMngrData(userDetails.getPersonNumber().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listOfEmpIsManager.size() > 0) {

            if (listOfEmpIsManager.get(0).get("MANAGER_FLAG") != null) {


                personInfo.setEmpIsMngrFlag(listOfEmpIsManager.get(0).get("MANAGER_FLAG").toString());
                System.err.println("Manager Flag (MANAGER_FLAG) is :" +
                                   listOfEmpIsManager.get(0).get("MANAGER_FLAG").toString());

            }


        }

        List<UserPersonDetails> details = userDetails.getUserPersonDetails();
        UserPersonDetails personDetails = details.get(0);
        if (personDetails.getDisplayName() != null) {
            JAXBElement<String> aXBElement = personDetails.getDisplayName();
            personInfo.setFullName(aXBElement.getValue());
            personInfo.setPersonName(aXBElement.getValue());
            System.out.println("-----******-----");
            System.out.println("Full Name " + aXBElement.getValue());
            System.out.println("-----******-----");
        }

        if (personDetails.getEmailAddress() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Email");
            //com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Email");
            return;
        }


        JAXBElement<String> bXBElement = personDetails.getEmailAddress();
        personInfo.setEmail(bXBElement.getValue());
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();

        if (relationshipDetails.get(0).getPositionName() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Position");
            // com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Position");
            return;
        }


        String position = relationshipDetails.get(0).getPositionName();
        String positionCode=relationshipDetails.get(0).getPositionCode();
        System.err.println("Position is : " + position);
        System.err.println("Position Code is : " + positionCode);
        //personInfo.setPosition(position);---2023 Approval Hierarchy Enhancement
        personInfo.setPosition(positionCode);

        if (relationshipDetails.get(0).getDepartmentName() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Department");
            //com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Department");
            return;
        }


        String department = relationshipDetails.get(0).getDepartmentName();
        personInfo.setDepartment(department);

        if (relationshipDetails.get(0).getJobName().getValue() != null) {
            String job = relationshipDetails.get(0).getJobName().getValue();
            personInfo.setJob(job);
        }
        if (relationshipDetails.get(0).getLocationName() != null) {
            String location = relationshipDetails.get(0).getLocationName();
            personInfo.setLocation(location);
            System.err.println("USER LOCATION : "+location);
            JSFUtils.setExpressionValue("#{sessionScope.userLocation}", location);
            ViewObject vo = ADFUtils.findIterator("LocationTranslationROVO1Iterator").getViewObject();
            Row r = vo.first();
            JSFUtils.setExpressionValue("#{sessionScope.userLocation}", r.getAttribute("LocationNameEn"));
            personInfo.setLocation((String)r.getAttribute("LocationNameEn"));
            System.err.println("Location Translation:  is::"+(String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{PersonInfo.location}"));
        }
        //       -------------  get user grade :

        if (relationshipDetails.get(0).getGradeCode() == null) {
            FilmStripBean.showPopupMessage("You can't open the application as this user has no Grad Value");
            //com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Grad Value");
            return;
        }

        String gradeCode = relationshipDetails.get(0).getGradeCode();
        personInfo.setGradeCode(gradeCode);


        if (!(position.equals("Chief Executive Officer"))) {
            if (relationshipDetails.get(0).getManagerId() == null) {
                FilmStripBean.showPopupMessage("You can't open the application as this user has no Manager Id");
                //com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Manager Id");
                return;
            }

            Long managerId = relationshipDetails.get(0).getManagerId();
            UserDetails managerDetails =
                userService.getUserDetailsByPersonId(managerId);

            if (managerDetails.getPersonNumber() == null) {
                FilmStripBean.showPopupMessage("You can't open the application as this user has no Manager number");
                //  com.sbm.selfServices.view.utils.JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Manager number");
                return;
            }

            String managerNumber = managerDetails.getPersonNumber();
            System.err.println("managerNumber >>> " + managerNumber);
        } else {
            personInfo.setAssignee("CEO");
            personInfo.setLineManager(userDetails.getPersonNumber());


        }


        //        System.err.println("Manager Number " + managerNumber);
        //        System.err.println("Manager ID " + managerId);
        //           if (position.equalsIgnoreCase("Executive Director, Shared Services Sector (Acting)")) {
        //               personInfo.setAssignee("Head Of Corporate");
        if (position.equalsIgnoreCase("Director of IT & C")) {
            personInfo.setAssignee("Director of IT & C");

        } else if (position.equalsIgnoreCase("Executive Director, Shared Services Sector (Acting)")) {
            personInfo.setAssignee("Executive Director, Shared Services Sector (Acting)");


        } else if (position.equalsIgnoreCase("Chief Financial Officer")) {
            personInfo.setAssignee("CFO");

        } else if (position.equalsIgnoreCase("Chief Executive Officer")) {
            personInfo.setAssignee("CEO");

        } else if (position.equalsIgnoreCase("HR Payroll and benefits Supervisor")) {
            personInfo.setAssignee("HR Payroll and benefits Supervisor");
        } else if (position.equalsIgnoreCase("HR Specialist")) {
            personInfo.setAssignee("HR Specialist");
        } else if (position.equalsIgnoreCase("Financial Planning & Analysis Manager")) {
            personInfo.setAssignee("Financial Planning & Analysis Manager");
        } else if (position.equalsIgnoreCase("HR Manager")) {
            personInfo.setAssignee("HR Manager");
        } else if (position.equalsIgnoreCase("HR and Admin Director")) {
            personInfo.setAssignee("HR and Admin Director");
        } else if (position.equalsIgnoreCase("Director, HR")) {
            personInfo.setAssignee("Director, HR");
        } else {
            //personInfo.setAssignee(position);--2023 Approval Hierarchy Position Code change
            personInfo.setAssignee(positionCode);
            
        }
        if (!(position.equals("Chief Executive Officer"))) {
        if (relationshipDetails.get(0) != null) {
            Long managerId = relationshipDetails.get(0).getManagerId();
            if (managerId != null) {
                UserDetails managerDetails =
                    userService.getUserDetailsByPersonId(managerId);
                String managerNumber = managerDetails.getPersonNumber();
                personInfo.setLineManager(managerNumber);
                personInfo.setLineManagerID(managerId);
            }
        }
        }
        Map perDiem;
        try {
            perDiem = BIRA.getPerDiemBasedOnGrade(personInfo.getGradeCode());
           
            com.sbm.selfServices.view.utils.JSFUtils.setExpressionValue("#{sessionScope.perDiem}",
                                                                        perDiem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
/* Commented by Moshina on 2024/02/28
 * Block the loading, deleting and inserting data related to Departments and Locations table on page load
 * It will enahce the performance of the page
 * */
//        try {
//            List<Map> list = BIRA.getDepartmentsData();
//
//            DCIteratorBinding deptIter =
//                com.sbm.selfServices.view.utils.ADFUtils.findIterator("DepartmentsVO1Iterator");
//            ViewObject deptView = deptIter.getViewObject();
//          //  System.err.println("deptView===="+deptView.getEstimatedRowCount());
////            ViewObject deptView1 =
////                com.sbm.selfServices.view.utils.ADFUtils.getApplicationModuleForDataControl("SBMModuleDataControl").findViewObject("DifferenceDateDepartmentIterator");
//            DCIteratorBinding differncedate =
//                com.sbm.selfServices.view.utils.ADFUtils.findIterator("DifferenceDateDepartmentIterator");
//            ViewObject diffview = differncedate.getViewObject();    
//            diffview.executeQuery();
//            if(diffview.first()!=null){
//          
//            Long value = (Long)diffview.first().getAttribute("Diff");
//            System.err.println("first============"+value);
//                System.err.println("value============"+diffview.getEstimatedRowCount());
//            DepartmentsVORowImpl deptRow;
//            if (value>0) {
//                if (list.size() > 0) {
//                    com.sbm.selfServices.view.utils.ADFUtils.findOperation("deleteDeptTableRows").execute();
//                    System.out.println("All Departments table rows deleted successfully");
//                    for (Map currentDepartment : list) {
//                        deptRow = (DepartmentsVORowImpl)deptView.createRow();
//                        deptRow.setDeptId(new Number(currentDepartment.get("ORGANIZATION_ID").toString()));
//                        deptRow.setDeptName(currentDepartment.get("NAME").toString());
//                        if (currentDepartment.get("SEGMENT4") != null) {
//                            deptRow.setSegment4((currentDepartment.get("SEGMENT4").toString()));
//
//                        } else {
//                            deptRow.setSegment4("");
//                        }
//                        if (currentDepartment.get("SEGMENT3") != null) {
//                            deptRow.setSegment3((currentDepartment.get("SEGMENT3").toString()));
//
//                        } else {
//                            deptRow.setSegment3("");
//                        }
//                        if (currentDepartment.get("SEGMENT2") != null) {
//                            deptRow.setSegment2((currentDepartment.get("SEGMENT2").toString()));
//
//                        } else {
//                            deptRow.setSegment2("");
//                        }
//                     
//                        deptView.insertRow(deptRow);
//
//                        com.sbm.selfServices.view.utils.ADFUtils.findOperation("Commit").execute();
//
//                    }
//
//                    deptView.executeQuery();
//                    deptIter.executeQuery();
////                    validate();
//                }
//            } else {
//                System.err.println("Cannot Insert");
//            }
//            }
//           
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        //To update locations
//        
//        try{
//            
//            List<Map> locationsList = BIRA.getAllLocations();
//
//            ViewObject locationVO = ADFUtils.findIterator("LocationsVOIterator").getViewObject();
//            ViewObject dateDiffVo = ADFUtils.findIterator("DifferenceDateLocationIterator").getViewObject();
//            if(dateDiffVo.first() != null){
//                
//                Long diffvalue = (Long)dateDiffVo.first().getAttribute("Diff");
//                System.err.println("LOCdiffvalue--"+diffvalue);
//                System.err.println("LOCationsList/size--"+locationsList.size());
//                
//                if(diffvalue > 0 && locationsList.size() > 0 ){
//                    
//                    LocationsVORowImpl locsRow;
//                    ADFUtils.findOperation("deleteAllLocations").execute();
//                    
//                    for (Map currentLocation : locationsList){
//                         
//                        String locId = currentLocation.get("LOCATION_ID") !=null ? currentLocation.get("LOCATION_ID").toString() : "0";
//                        String locName = currentLocation.get("LOCATION_NAME") !=null ? currentLocation.get("LOCATION_NAME").toString() : "";
//                        
//                        locsRow = (LocationsVORowImpl)locationVO.createRow();
//                        locsRow.setLocationsId(new Number(locId));
//                        locsRow.setLocationsName(locName);
//                        
//                        locationVO.insertRow(locsRow);
//                        ADFUtils.findOperation("Commit").execute();
//                    }
//                    locationVO.executeQuery();
//                }
//            } 
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        ViewObject weVisRovo = ADFUtils.findIterator("WithdrawEditVisible_ROVO1Iterator").getViewObject();
     
       this.updateWithdrawAndEdit(weVisRovo);
    } 
    
    public static void updateWithdrawAndEdit(ViewObject vo){
        RowSetIterator rs = vo.createRowSetIterator(null);
        while(rs.hasNext()){
            Row r = rs.next();
            String request = r.getAttribute("RequestName")!=null ? r.getAttribute("RequestName").toString() : "";
            request = request.replace("-", "");
            request = request.replace("/", ""); 
            String withdraw = r.getAttribute("WithdrawFlag")!=null ? r.getAttribute("WithdrawFlag").toString() : "N";
            String edit = r.getAttribute("EditFlag")!=null ? r.getAttribute("EditFlag").toString() : "";
            
            ADFContext.getCurrent().getSessionScope().put( request+"_WITHDRAW", null);
            ADFContext.getCurrent().getSessionScope().put( request+"_EDIT", null);
            
            ADFContext.getCurrent().getSessionScope().put( request+"_WITHDRAW", withdraw);
            ADFContext.getCurrent().getSessionScope().put( request+"_EDIT", edit);
        }
    }

}


