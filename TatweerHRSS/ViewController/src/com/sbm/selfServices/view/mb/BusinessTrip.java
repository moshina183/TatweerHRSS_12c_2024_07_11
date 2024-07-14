
package com.sbm.selfServices.view.mb;


import com.mivors.model.bi.integration.BiReportAccess;

import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserPersonDetails;
import com.oracle.xmlns.apps.hcm.people.roles.userdetailsservicev2.UserWorkRelationshipDetails;

import com.sbm.APConsumer;
import com.sbm.CodeCombinationConsumer;
import com.sbm.selfServices.model.fusion.integration.FusionDataLoader;
import com.sbm.selfServices.model.views.up.BusinessTripRequestViewRowImpl;
import com.sbm.selfServices.model.views.up.DepartmentsVORowImpl;
import com.sbm.selfServices.model.views.up.LoanRequestViewRowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;
import com.sbm.selfServices.view.utils.JSFUtils;
import com.sbm.selfServices.view.utils.PersonInfo;
import com.sbm.selfServices.view.utils.UserServiceUtil;

import com.view.beans.filmStripApp.FilmStripBean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichInputFile;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.component.rich.input.RichSelectItem;
import oracle.adf.view.rich.component.rich.input.RichSelectManyChoice;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.layout.RichPanelLabelAndMessage;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.ClobDomain;
import oracle.jbo.domain.Number;

import oracle.jbo.uicli.binding.JUCtrlListBinding;

import org.apache.commons.io.IOUtils;
import org.apache.myfaces.trinidad.model.UploadedFile;

import org.opensaml.saml2.metadata.Company;


public class BusinessTrip {
    private RichPopup attachmentPopup;
    private RichInputFile inputFile;
    private String attachmentFileName;
    private String actionReason;
    private RichPopup requestPopup;
    private RichSelectOneChoice transAllowanceType;
    private RichInputText transAllowanceValue;
    private RichSelectOneChoice subType;
    private RichSelectOneChoice desType;
    private RichSelectItem carValueInWayOfTravel;
    private RichSelectOneChoice housingAllowanceType;
    private RichInputText housingAllowanceValue;
    private RichSelectOneChoice foodAllowanceType;
    private RichInputText foodAllowanceValue;
    private RichSelectBooleanCheckbox requiredVisa;
    private RichInputText tripComment;
    private RichPanelLabelAndMessage otherCities;
    private RichOutputText attachmentsRowCount;
    private RichSelectBooleanCheckbox moreCitesCheckBox;
    private RichInputDate startdatebinding;
    private RichInputDate enddatebinding;
    private List<Object> selCities;
    private RichSelectManyChoice otherCitiesibinding;
    private RichInputText otherCitiesDb;
    private FusionDataLoader fusionFileLoader;
    private RichInputDate preStartDate;
    private RichInputDate preArrivalTime;
    private String value1 = null;
    private RichSelectBooleanCheckbox declarationBox;

    public BusinessTrip() {
    }

    public String save_action() {
        
        //Object deptNameAttribute = ADFUtils.evaluateEL("#{bindings.CountryName.items['"+ADFUtils.getBoundAttributeValue("CountryId")+"'].label}");
        if (ADFUtils.getBoundAttributeValue("MeansTravel") == null) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as you have select way of travel");
            return null;

        }
        if (ADFUtils.getBoundAttributeValue("CostCenterNumber") == null) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as cost center number has no value");
            return null;

        }
        Boolean oneWayTrip =
                    (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
        
        if (ADFUtils.getBoundAttributeValue("NewCity") == null &&
            (ADFUtils.getBoundAttributeValue("NewCity1").equals(""))) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as From City is Mandatory");
            return null;

        }
            if(oneWayTrip != null &&
                                    oneWayTrip.equals(false)){
        if (ADFUtils.getBoundAttributeValue("ToCity") == null &&
            (ADFUtils.getBoundAttributeValue("ToCity1").equals(""))) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as To City is Mandatory");
            return null;

        }
        }
        if (ADFUtils.getBoundAttributeValue("StartDate") == null &&
            ADFUtils.getBoundAttributeValue("EndDate") == null) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as Both Start Date and End Date is Mandatory");
            return null;

        }
        String tripType =
            ADFUtils.getBoundAttributeValue("TripType").toString();
        String wayofTravel =
            ADFUtils.getBoundAttributeValue("MeansTravel").toString();
        if (wayofTravel.equalsIgnoreCase(("Train (Company)")) || wayofTravel.equalsIgnoreCase(("Plane (Company)"))) {

            if (ADFUtils.getBoundAttributeValue("PreferredDepartureTime") ==
                null ) {


                JSFUtils.addFacesErrorMessage("Please enter a value for Preferred Departure Time");
                return null;


            }
            if (ADFUtils.getBoundAttributeValue("PreferredArrivalTime") ==
                null ) {


                JSFUtils.addFacesErrorMessage("Please enter a value for Preferred Arrival Time");
                return null;


            }
        }
           
            
        
        if (tripType.equalsIgnoreCase("Expense")) {

            if (ADFUtils.getBoundAttributeValue("ExpenseBusinessTripId") ==
                null) {


                JSFUtils.addFacesErrorMessage("You can't Save the request as you must select Business trip to be Expensed");
                return null;


            }
        }

        Object employeeNumber =
            JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        if (employeeNumber == null) {
            JSFUtils.addFacesErrorMessage("You donot have Person ID, So you can not save the request");
            return null;
        }

        if (ADFUtils.getBoundAttributeValue("CostCenter") == null)

        {
            JSFUtils.addFacesErrorMessage("You should select Cost Center to save the request");
            return null;

        }
        ADFUtils.setBoundAttributeValue("ActionTaken", "SAVED");
        ADFUtils.setBoundAttributeValue("Assignee", employeeNumber);
        ADFUtils.setBoundAttributeValue("RequestStatus", "SAVED");
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request has been saved");
        return "back";
    }

    public boolean checkOverLap(Date startDate1, Date endDate1,
                                Date startDate2, Date endDate2) {

        return startDate1.getTime() <= endDate2.getTime() &&
            startDate2.getTime() <= endDate1.getTime();

    }

    public static java.util.Date convertDomainDateToUtilDate(oracle.jbo.domain.Date domainDate) {
        java.util.Date date = null;
        if (domainDate != null) {
            java.sql.Date sqldate = domainDate.dateValue();
            date = new Date(sqldate.getTime());
        }
        return date;
    }


    public String submit_action() {
        /*
         * IF condition added by Moshina for Declaration form in Training screen on 05-06-2024
         * Implementation The Deduction Form In The System(SD#20258)
         */
        
        String tripType = ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
        if(tripType.equalsIgnoreCase("training")){  
            if(!declarationBox.isSelected()){
                System.out.println("inside declaration action");
                JSFUtils.addFacesErrorMessage("You can't Submit this request as declaration is required");
                return null;
            }else{
                ADFUtils.setBoundAttributeValue("Declaration", "ACCEPTED");
            }
        }
        Object currStatus = ADFUtils.getBoundAttributeValue("RequestStatus"); 
        String act = "SUMBIT_ACT";
        if(currStatus != null && "EDIT".equals(currStatus)){
            act = "UPDATE_ACT";
        }
        String personNumberCheck =
            (String)ADFUtils.getBoundAttributeValue("PersoneId");
        String costCenter =
            (String)ADFUtils.getBoundAttributeValue("CostCenterNumber");
        String division = (String)ADFUtils.getBoundAttributeValue("Division");
        String lob = (String)ADFUtils.getBoundAttributeValue("Lob");
        String personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
        String requestTripType =
            ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
        String TripTypeName=requestTripType +'-'+ personLocation;
        ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        System.err.println("Request Type name:  is::"+TripTypeName);
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        String budgetValidCheck =
            (String)nextStep.getAttribute("BudgetValidation");
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        System.err.println("Submitted: stepType is::"+stepType);
        Number perdiem = null;
        Number totalForInvoice = null;
        perdiem = (Number)ADFUtils.getBoundAttributeValue("PerDiemAmount");
        totalForInvoice =
                (Number)ADFUtils.getBoundAttributeValue("TotalForInvoice");
        String abc =
            ADFUtils.getBoundAttributeValue("SubType") == null ? "NULL" :
            ADFUtils.getBoundAttributeValue("SubType").toString();
        String fromCity =
            ADFUtils.getBoundAttributeValue("NewCity1") == null ? "NULL" :
            ADFUtils.getBoundAttributeValue("NewCity1").toString();
        String ToCity =
            ADFUtils.getBoundAttributeValue("ToCity1") == null ? "NULL" :
            ADFUtils.getBoundAttributeValue("ToCity1").toString();
        Map perdiemMap =
            (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
        String TripType = (String)ADFUtils.getBoundAttributeValue("SubType");
        String SubTypevalue = null;
        if (TripType != null && TripType.equalsIgnoreCase("Local")) {
            SubTypevalue = "Local";
        } else {
            SubTypevalue = "Inter";
        }
        if (ADFUtils.getBoundAttributeValue("MeansTravel") == null) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as you have select way of travel");
            return null;

        }
        if (ADFUtils.getBoundAttributeValue("CostCenterNumber") == null) {

            JSFUtils.addFacesErrorMessage("You can't Submit this request as cost center number has no value");
            return null;

        }
        Boolean oneWayTrip =
                    (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
       
        if (ADFUtils.getBoundAttributeValue("NewCity") == null &&
            (ADFUtils.getBoundAttributeValue("NewCity1").equals(""))) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as From City is Mandatory");
            return null;

        }
            if(oneWayTrip != null &&
                                    oneWayTrip.equals(false)){
        if (ADFUtils.getBoundAttributeValue("ToCity") == null &&
            (ADFUtils.getBoundAttributeValue("ToCity1").equals(""))) {

            JSFUtils.addFacesErrorMessage("You can't Save this request as To City is Mandatory");
            return null;

        }
        }
        String wayofTravel =
                    ADFUtils.getBoundAttributeValue("MeansTravel").toString();
                if (wayofTravel.equalsIgnoreCase(("Train (Company)")) || wayofTravel.equalsIgnoreCase(("Plane (Company)"))) {

                    if (ADFUtils.getBoundAttributeValue("PreferredDepartureTime") ==
                        null ) {
                        JSFUtils.addFacesErrorMessage("Please enter a value for Preferred Departure Time");
                        return null;
                    }
                    if (ADFUtils.getBoundAttributeValue("PreferredArrivalTime") ==
                        null ) {
                        JSFUtils.addFacesErrorMessage("Please enter a value for Preferred Arrival Time");
                        return null;
                    }
                }

        if (ADFUtils.getBoundAttributeValue("BenefitType").toString().equals("P")) {
            if (TripType != null) {
                if (perdiemMap.get((SubTypevalue)) == null) {
                    JSFUtils.addFacesErrorMessage("You Cannot Submit the Request since Grade/PerDiem not assigned to you");
                    return null;
                }
            }
            //                if (perdiemMap.get(ADFUtils.getBoundAttributeValue("TripType"))==null) {
            //                    JSFUtils.addFacesErrorMessage("You Cannot Submit the Request since Grade/PerDiem not assigned to you");
            //                    return null;
            //                }

        }

        if (ADFUtils.getBoundAttributeValue("BenefitType").toString().equals("P")) {
            if (ADFUtils.getBoundAttributeValue("SubType") != null &&
                (ADFUtils.getBoundAttributeValue("SubType").toString().equalsIgnoreCase("International") ||
                 (ADFUtils.getBoundAttributeValue("SubType").toString().equalsIgnoreCase("Local") &&
                  !(Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS") &&
                  !fromCity.equalsIgnoreCase("Riyadh") &&
                  !ToCity.equalsIgnoreCase("Riyadh")))) {
                if (totalForInvoice == null ||
                    totalForInvoice.intValue() == 0) {

                    JSFUtils.addFacesErrorMessage("You can't Submit this request since Total Amount Due is zero");
                    return null;

                }
            }
        }

        if (ADFUtils.getBoundAttributeValue("SubType").equals("Local")) {
            if (ADFUtils.getBoundAttributeValue("StartDate") == null ||
                ADFUtils.getBoundAttributeValue("EndDate") == null) {

                JSFUtils.addFacesErrorMessage("You can't Submit this request as Both Start Date and End Date is Mandatory");
                return null;

            }
        } else {
            if (ADFUtils.getBoundAttributeValue("SubType").equals("International")) {
                if (ADFUtils.getBoundAttributeValue("ActualStartDate") ==
                    null ||
                    ADFUtils.getBoundAttributeValue("ActualEndDate") == null) {

                    JSFUtils.addFacesErrorMessage("You can't Submit this request as Both Actual Start Date and Actual End Date is Mandatory");
                    return null;

                }
            }
        }
        //        Long validate= calcDays((oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("StartDate"), (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("EndDate"));
        //        if(validate!=null){
        //            String validateDays = validate.toString();
        //            String days = ADFUtils.getBoundAttributeValue("Days").toString();
        //            if(!validateDays.equalsIgnoreCase(days)){
        //                JSFUtils.addFacesErrorMessage("Number of days doesn't match the dates selected");
        //                return null;
        //            }
        //        }
        if (ADFUtils.getBoundAttributeValue("SubType").equals("Local")) {
            ADFUtils.setBoundAttributeValue("ActualStartDate", null);
            ADFUtils.setBoundAttributeValue("ActualEndDate", null);
        }
        //        System.err.println("SubType=="+ADFUtils.getBoundAttributeValue("SubType"));
        //        System.err.println("Resolve============>"+JSFUtils.resolveExpression("#{bindings.SubType.inputValue}"));
        //        if(ADFUtils.getBoundAttributeValue("BenefitType").toString().equals("P")) {


        System.err.println("abc->" + abc + "fromCity->" + fromCity +
                           "ToCity->" + ToCity);
        System.err.println("perdiem.intValue()-->" + perdiem.intValue());
        System.err.println("totalForInvoice.intValue()-->" +
                           totalForInvoice.intValue());

        if ((perdiem.intValue() == 0 && totalForInvoice.intValue() == 0 &&
             (fromCity.equalsIgnoreCase("Riyadh") &&
              ToCity.equalsIgnoreCase("Riyadh"))) ||
            !(fromCity.equalsIgnoreCase("Riyadh") &&
              ToCity.equalsIgnoreCase("Riyadh")) ||
            ADFUtils.getBoundAttributeValue("OneWay_TRANS").equals(true) ||
            (ADFUtils.getBoundAttributeValue("NewCity") != null &&
             ADFUtils.getBoundAttributeValue("ToCity") != null)) {
            //            ADFUtils.getBoundAttributeValue("OneWay_TRANS").equals(true)) {


            BudgetAmountValidation budgetAmount = new BudgetAmountValidation();
            boolean value = false;
            System.out.println("budget validation started");
            if (budgetValidCheck.equals("Y")) {
                value =
                        budgetAmount.getRemainingBudgetAmount(division, lob, costCenter,
                                                              "5101030201",
                                                              "OtherCost",
                                                              ADFUtils.getBoundAttributeValue("CreationDate").toString());
                System.out.println("budget validation flag:" + value);
                String mean_trvael =
                    (String)ADFUtils.getBoundAttributeValue("MeansTravel");
                if (value) {
                    if (mean_trvael.equalsIgnoreCase("Car (Employee)") ||
                        mean_trvael.equalsIgnoreCase("Train (Employee)") ||
                        mean_trvael.equalsIgnoreCase("Plane (Employee)")) {
                        value =
                                budgetAmount.getRemainingBudgetAmount(division, lob,
                                                                      costCenter,
                                                                      "5101030202",
                                                                      "TravelCost",
                                                                      ADFUtils.getBoundAttributeValue("CreationDate").toString());
                        if (!value) {
                            JSFUtils.addFacesErrorMessage("Budget not available for the cost center selected and Travel cost Account number 5101030201");
                            return null;
                        }
                    }

                } else {
                    JSFUtils.addFacesErrorMessage("Budget not available for the cost center selected and other cost Account number 5101030201");
                    return null;
                }
            }


            //        String codeCombinationIdBudget1 =
            //            "01" + "-" + division + "-" + lob + "-" + costCenter + "-" +
            //            "5101030201";
            //        String codeCombinationIdBudget2 =
            //            "01" + "-" + division + "-" + lob + "-" + costCenter + "-" +
            //            "5101030202";
            ArrayList<String> combinationIDs = null;
            combinationIDs =
                    getCodeCombinationId(personNumberCheck, costCenter,
                                         division, lob);
            String codeCombinationIdLine1 = combinationIDs.get(0);
            String codeCombinationIdLine2 = combinationIDs.get(1);
            if ((codeCombinationIdLine1 != null &&
                 !codeCombinationIdLine1.equalsIgnoreCase("-1")) &&
                (codeCombinationIdLine2 != null &&
                 !codeCombinationIdLine2.equalsIgnoreCase("-1"))) {
                String benefitType =
                    (String)ADFUtils.getBoundAttributeValue("BenefitType");


                String checkTripType =
                    ADFUtils.getBoundAttributeValue("TripType").toString();
                if (benefitType.equalsIgnoreCase("A") &&
                    !checkTripType.equals("Expense")) {
                    JSFUtils.addFacesErrorMessage("You can't Submit this request as company policy has changed! Please raise a new request!");
                    return null;
                }
                ADFUtils.setBoundAttributeValue("ActionTaken", "Submit");
                if (checkTripType.equals("Expense")) {


                    if (ADFUtils.getBoundAttributeValue("ExpenseBusinessTripId") ==
                        null) {


                        JSFUtils.addFacesErrorMessage("You can't Submit the request as you must select Business trip to be Expensed");
                        return null;


                    }


                    ADFUtils.setBoundAttributeValue("RequestStatus",
                                                    "PENDING");
                    ADFUtils.setBoundAttributeValue("StepId", 2);
                    ADFUtils.setBoundAttributeValue("Assignee",
                                                    "HR Payroll and benefits Supervisor");
                    ADFUtils.findOperation("changeExpenseFlag").execute();

                    if (benefitType.equalsIgnoreCase("A")) {
                        String wayOfTravel =
                            (String)ADFUtils.getBoundAttributeValue("MeansTravel");
                        Number totalAmount = null;

                        if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
                            wayOfTravel.equalsIgnoreCase("Train (Employee)") ||
                            wayOfTravel.equalsIgnoreCase("Plane (Employee)")) {

                            Number travelCost = null;
                            if (ADFUtils.getBoundAttributeValue("TravelCost") !=
                                null) {

                                travelCost =
                                        (Number)ADFUtils.getBoundAttributeValue("TravelCost");

                            } else {
                                travelCost = new Number(0);
                            }


                            Number laundryAmount = null;
                            Number foodAmount = null;
                            Number visaCostAmount = null;
                            Number otherCostAmount = null;
                            Number transportationAmount = null;
                            Number housingAmount = null;

                            if (ADFUtils.getBoundAttributeValue("LaundryAllowanceAmount") !=
                                null) {
                                laundryAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("LaundryAllowanceAmount");
                            } else {
                                laundryAmount = new Number(0);

                            }


                            if (ADFUtils.getBoundAttributeValue("FoodAllowanceAmount") !=
                                null) {
                                foodAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("FoodAllowanceAmount");
                            } else {
                                foodAmount = new Number(0);

                            }

                            if (ADFUtils.getBoundAttributeValue("VisaCost") !=
                                null) {
                                visaCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("VisaCost");
                            } else {
                                visaCostAmount = new Number(0);

                            }

                            if (ADFUtils.getBoundAttributeValue("OtherCost") !=
                                null) {
                                otherCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
                            } else {
                                otherCostAmount = new Number(0);

                            }


                            if (ADFUtils.getBoundAttributeValue("TransAllowanceAmount") !=
                                null) {
                                transportationAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("TransAllowanceAmount");
                            } else {
                                transportationAmount = new Number(0);

                            }


                            if (ADFUtils.getBoundAttributeValue("HousingAllowanceAmount") !=
                                null) {
                                housingAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("HousingAllowanceAmount");
                            } else {
                                housingAmount = new Number(0);

                            }

                            totalAmount =
                                    laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount).add(travelCost);

                            if (ADFUtils.getBoundAttributeValue("HousingAllowanceType").toString().equalsIgnoreCase("Emp")) {
                                totalAmount = totalAmount.add(housingAmount);

                            }

                            if (ADFUtils.getBoundAttributeValue("TransportationAllowanceType").toString().equalsIgnoreCase("Emp")) {
                                totalAmount =
                                        totalAmount.add(transportationAmount);

                            }
                        } else if (wayOfTravel.equalsIgnoreCase("Car (Company)") ||
                                   wayOfTravel.equalsIgnoreCase("Train (Company)") ||
                                   wayOfTravel.equalsIgnoreCase("Plane (Company)")) {


                            //code with one line


                            Number laundryAmount = null;
                            Number foodAmount = null;
                            Number visaCostAmount = null;
                            Number otherCostAmount = null;
                            Number transportationAmount = null;
                            Number housingAmount = null;

                            if (ADFUtils.getBoundAttributeValue("LaundryAllowanceAmount") !=
                                null) {
                                laundryAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("LaundryAllowanceAmount");
                            } else {
                                laundryAmount = new Number(0);

                            }


                            if (ADFUtils.getBoundAttributeValue("FoodAllowanceAmount") !=
                                null) {
                                foodAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("FoodAllowanceAmount");
                            } else {
                                foodAmount = new Number(0);

                            }

                            if (ADFUtils.getBoundAttributeValue("VisaCost") !=
                                null) {
                                visaCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("VisaCost");
                            } else {
                                visaCostAmount = new Number(0);

                            }

                            if (ADFUtils.getBoundAttributeValue("OtherCost") !=
                                null) {
                                otherCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
                            } else {
                                otherCostAmount = new Number(0);

                            }


                            if (ADFUtils.getBoundAttributeValue("TransAllowanceAmount") !=
                                null) {
                                transportationAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("TransAllowanceAmount");
                            } else {
                                transportationAmount = new Number(0);

                            }


                            if (ADFUtils.getBoundAttributeValue("HousingAllowanceAmount") !=
                                null) {
                                housingAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("HousingAllowanceAmount");
                            } else {
                                housingAmount = new Number(0);

                            }

                            totalAmount =
                                    laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount);

                            if (ADFUtils.getBoundAttributeValue("HousingAllowanceType").toString().equalsIgnoreCase("Emp")) {
                                totalAmount = totalAmount.add(housingAmount);

                            }

                            if (ADFUtils.getBoundAttributeValue("TransportationAllowanceType").toString().equalsIgnoreCase("Emp")) {
                                totalAmount =
                                        totalAmount.add(transportationAmount);

                            }

                        }

                        if (totalAmount != null) {

                            ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                            totalAmount);

                        }
                    } else if (benefitType.equalsIgnoreCase("P")) {
                        calcPerDiem();
                    }

                    //            System.err.println("Ana Hena 10/7/2019");

                    BiReportAccess report = new BiReportAccess();
                    //                ReportsUtils report = new ReportsUtils();
                    List<Map> personData = null;
                    try {
                        personData =
                                report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        ADFUtils.setBoundAttributeValue("AssigneeName",
                                                        personData.get(0).get("DISPLAY_NAME").toString());
                    }

                    ADFUtils.findOperation("Commit").execute(); 
                    
                    Row tripRow =
                        ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                          (Number)nextStep.getAttribute("NextStepId"),
                                                          (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                           act, "N");
        System.err.println("Email====>"+personData.get(0).get("EMAIL_ADDRESS"));
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        if (emailNotification != null &&
                            emailNotification.equalsIgnoreCase("Y")) {
                            sendEmailByEmailExpense(personData.get(0).get("EMAIL_ADDRESS").toString(),
                                                    tripRow);
                        }
                    } else {
                        JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");

                    }
                    JSFUtils.addFacesInformationMessage("Request has been Expensed");

                    return "back";


                }

                else if (checkTripType.equals("Training")) {

                    Long rowCount = (Long)attachmentsRowCount.getValue();
                    //            System.err.println("Row Count >> "+rowCount.toString());
                    if (rowCount < 1) {
                        JSFUtils.addFacesErrorMessage("You must add the required attachments before submitting the request");
                        return null;
                    }
                }


                // Add event code here...
                //        String[] mainPositions={"Head Of Corporate Support Division","CFO","Chief Executive Officer","Payroll Manager","HR Manager","Budget Controller","HR Manager"};
                if (ADFUtils.getBoundAttributeValue("Days") == null) {
                    JSFUtils.addFacesErrorMessage("Cannot calculate Number of Days, So you can not submit the request");
                    return null;
                }

                if (ADFUtils.getBoundAttributeValue("CostCenter") == null)

                {
                    JSFUtils.addFacesErrorMessage("You should select Cost Center to submit the request");
                    return null;

                }
                String personNumber =
                    ADFUtils.getBoundAttributeValue("PersoneId").toString();
                System.err.println("personNumber=======>" + personNumber);
                UserServiceUtil userService = new UserServiceUtil();
                UserDetails userDetails = null;

                userDetails =
                        userService.getUserDetailsByPersonNumber(personNumber);
                List<UserWorkRelationshipDetails> relationshipDetails =
                    userDetails.getUserWorkRelationshipDetails();
                
                /*
                 * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
                 * Issue Id #21361 
                 * Commented
                Long lineManagerID = relationshipDetails.get(0).getManagerId();
                UserDetails managerDetails =
                    userService.getUserDetailsByPersonId(lineManagerID);
                String managerNumber = managerDetails.getPersonNumber();
                JAXBElement<String> aXBElement = null;
                if (managerDetails.getUserPersonDetails().get(0).getDisplayName() !=
                    null) {
                    aXBElement =
                            managerDetails.getUserPersonDetails().get(0).getDisplayName();
                }
                String position = relationshipDetails.get(0).getPositionName();
                */
                
                /*
                 * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
                 * Issue Id #21361 
                 * Added
                */
                Long lineManagerID = 0L;
                String managerNumber = null;
                JAXBElement<String> aXBElement = null;
                String position = relationshipDetails.get(0).getPositionName();
                System.out.println("CEO Position: "+position);
                if(!(position.equalsIgnoreCase("THC Group CEO"))){
                    lineManagerID = relationshipDetails.get(0).getManagerId();
                    UserDetails managerDetails = userService.getUserDetailsByPersonId(lineManagerID);
                    managerNumber = managerDetails.getPersonNumber();
                    if (managerDetails.getUserPersonDetails().get(0).getDisplayName() != null) {
                        aXBElement = managerDetails.getUserPersonDetails().get(0).getDisplayName();
                    }
                }
                
                ADFUtils.setBoundAttributeValue("PersonPosition", position);
                String department = relationshipDetails.get(0).getDepartmentName();
                ADFUtils.setBoundAttributeValue("PersonDepartment", department);
                String job = relationshipDetails.get(0).getJobName().getValue();
                ADFUtils.setBoundAttributeValue("PersonJob", job);
                String location = relationshipDetails.get(0).getLocationName();
                ADFUtils.setBoundAttributeValue("PersonLocation", location);

                String gradeCode = relationshipDetails.get(0).getGradeCode();
                System.err.println("gradeCode is >>> " + gradeCode);
                oracle.jbo.domain.Date oracleDate =
                    (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("StartDate");
                java.sql.Date javaSqlDate = oracleDate.dateValue();
                long javaMilliSeconds = javaSqlDate.getTime();
                java.util.Date startDate1 =
                    new java.util.Date(javaMilliSeconds);

                oracle.jbo.domain.Date oracleDate2 =
                    (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("EndDate");
                java.sql.Date javaSqlDate2 = oracleDate2.dateValue();
                long javaMilliSeconds2 = javaSqlDate2.getTime();
                java.util.Date endDate1 =
                    new java.util.Date(javaMilliSeconds2);

                OperationBinding getDatesOper =
                    ADFUtils.findOperation("getBusinessTripDatesToCheckOverlap");
                ArrayList<Row> requestsDates =
                    (ArrayList<Row>)getDatesOper.execute();
                if ((requestsDates.size()) > 0) {
                    for (Row row : requestsDates) {
                        if(row.getAttribute("RequestStatus")!=null && "EDIT".equals(row.getAttribute("RequestStatus"))){
                            
                        }else{
                            System.out.println("@ BusinessTrip is >>"+row.getAttribute("RequestStatus"));
                            System.out.println("@ BusinessTrip is >>"+row.getAttribute("StartDate"));
                            System.out.println("@ BusinessTrip is >>"+row.getAttribute("EndDate"));
                            oracle.jbo.domain.Date oracleDate3 =
                                (oracle.jbo.domain.Date)row.getAttribute("StartDate");
                            oracle.jbo.domain.Date oracleDate4 =
                                (oracle.jbo.domain.Date)row.getAttribute("EndDate");
                            if(oracleDate3!=null && oracleDate4!=null)
                            {
                            java.sql.Date javaSqlDate3 = oracleDate3.dateValue();
                            long javaMilliSeconds3 = javaSqlDate3.getTime();
                            
                            java.util.Date StartDate2 =
                                new java.util.Date(javaMilliSeconds3);
                            java.sql.Date javaSqlDate4 = oracleDate4.dateValue();
                            long javaMilliSeconds4 = javaSqlDate4.getTime();
                            java.util.Date endDate2 =
                                new java.util.Date(javaMilliSeconds4);

                            boolean isOverlaped =
                                (checkOverLap(startDate1, endDate1, StartDate2,
                                              endDate2));

                            if (isOverlaped) {
                                JSFUtils.addFacesErrorMessage("You cannot submit the request as there is an overlap in the dates");
                                return null;
                            }
                            }
                        } 
                    }
                }

                /*
                 * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
                 * Issue Id #21361 
                 * Commented
                if (position.equals("Chief Executive Officer"))
                {
                    BiReportAccess report = new BiReportAccess();
                    //                ReportsUtils report = new ReportsUtils();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String tripType = ADFUtils.getBoundAttributeValue("TripType").toString();
                    ADFUtils.setBoundAttributeValue("Assignee", "HR Payroll and benefits Supervisor");
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        ADFUtils.setBoundAttributeValue("AssigneeName", personData.get(0).get("DISPLAY_NAME").toString());
                    }
                    //                ADFUtils.setBoundAttributeValue("Assignee", "Payroll Manager");

                    if (tripType.equals("Local") || tripType.equals("Inter")) {
                        ADFUtils.setBoundAttributeValue("StepId", 4);
                    }
                    else if (tripType.equals("Event") || tripType.equals("Training")) {
                        ADFUtils.setBoundAttributeValue("StepId", 5);
                    }

                    Row tripRow = ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
                    ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
                    ADFUtils.findOperation("Commit").execute();

                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                          (Number)nextStep.getAttribute("NextStepId"),
                                                          (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          act, "N");
                    JSFUtils.addFacesInformationMessage("Request has been submitted");


                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                        }
                    } else {
                        JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
                    }
                    return "back";

                }
                */
                
                /*
                 * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
                 * Issue Id #21361 
                 * Added
                 * Commented by Moshina for CEO hierarchy imlplementation
                * if (position.equals("THC Group CEO"))
                    {
                        BiReportAccess report = new BiReportAccess();
                        List<Map> personData = null;
                        try {
                            personData = report.getPersonByPostionReport("HC-CPTL");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tripType = ADFUtils.getBoundAttributeValue("TripType").toString();
                        ADFUtils.setBoundAttributeValue("Assignee", "HC-CPTL");
                        if (personData.get(0).get("DISPLAY_NAME") != null) {
                            ADFUtils.setBoundAttributeValue("AssigneeName", personData.get(0).get("DISPLAY_NAME").toString());
                        }
                        System.out.println("CEO Request Type name:  is:"+TripTypeName);
                        OperationBinding oper = ADFUtils.findOperation("getCEONextStepId");
                        Map paramMap = oper.getParamsMap();
                        paramMap.put("requestName", TripTypeName);
                        long stepId = (Long)oper.execute();
                        System.err.println("CEO stepId is "+stepId);
                        ADFUtils.setBoundAttributeValue("StepId", stepId);
                        Row tripRow = ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
                        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
                        ADFUtils.findOperation("Commit").execute();
 
                        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                              ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                              (Number)nextStep.getAttribute("NextStepId"),
                                                              (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                              (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                              act, "N");
                        JSFUtils.addFacesInformationMessage("Request has been submitted");
                        if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                            if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                                sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                            }
                        } else {
                            JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Compensation and Payroll Team Lead doesn't has email");
                        }
                        return "back";
                    }
                    */
                
        if (position.equals("THC Group CEO")){
            nextOpr = ADFUtils.findOperation("getNextStepCEO");
            nextStep = (Row)nextOpr.execute();
            emailNotification = (String)nextStep.getAttribute("EmailNotification");
            budgetValidCheck = (String)nextStep.getAttribute("BudgetValidation");
            stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            System.err.println("Submitted: stepType CEO is::"+stepType);
        
            BiReportAccess report = new BiReportAccess();
            List<Map> personData = null;
            try {
                personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ADFUtils.setBoundAttributeValue("Assignee", nextStep.getAttribute("NextAssignee").toString());
            if (personData.get(0).get("DISPLAY_NAME") != null) {
                ADFUtils.setBoundAttributeValue("AssigneeName", personData.get(0).get("DISPLAY_NAME").toString());
            }
            ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
            Row tripRow = ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
            ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
            ADFUtils.findOperation("Commit").execute();

            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                  ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                  (Number)nextStep.getAttribute("NextStepId"),
                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                  act, "N");
            JSFUtils.addFacesInformationMessage("Request has been submitted");
            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                }
            } else {
                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
            }
            return "back";
        }
                                

                
                // Usual Submit processs
                
                String assigneeName = "";
                String assigneeNo = "";
                String assigneeEmail = "";
                String stringLineManagerID = lineManagerID.toString();
                
                // If step type is POSITION
                if("POSITION".equals(stepType)){
                    System.err.println("Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
                    BiReportAccess report = new BiReportAccess();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
                    }
                    if (personData.get(0).get("PERSON_NUMBER") != null) {
                        assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
                    }
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
                    }
                }
                // If step type is COST CENTER MANAGER
                if("COST_CENTER_MANAGER".equals(stepType) && nextStep.getAttribute("NextAssignee").equals("Cost Center Manager")){
                    System.err.println("Submitted : COST_CENTER_MANAGER ");
                    Object costCenterName = ADFUtils.getBoundAttributeValue("CostCenter");
                    if (costCenter != null){
                        BiReportAccess BIRA = new BiReportAccess();
                        try {
                            List<Map> managerOfDeptList = BIRA.getManagerOfDepartmentData(costCenterName.toString());
                            if (managerOfDeptList.get(0).get("PERSON_NUMBER") != null) {
                                assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                            }
                            if (managerOfDeptList.get(0).get("DISPLAY_NAME") != null) {
                                assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            }
                            if (managerOfDeptList.get(0).get("EMAIL_ADDRESS") != null) {
                                assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                // If step type is DEPARTMENT MANAGER
                if("DEPT_MANAGER".equals(stepType)){
                    System.err.println("Submitted : DEPT_MANAGER :"+department);
                    BiReportAccess BIRA = new BiReportAccess();
                    try {
                        List<Map> managerOfDeptList =
                            BIRA.getManagerOfDepartmentData(department);
                        if (managerOfDeptList.size() > 0) {
                            if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                            }
                            if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                            }
                            if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                            } 
                        } else {
                            JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                            return null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                // If step type is LINE MANAGER
                if("LINE_MANAGER".equals(stepType)){
                    System.err.println("Submitted : LINE_MANAGER ");
                    if (stringLineManagerID == null) {
                        JSFUtils.addFacesErrorMessage("You donot have Line Manager, So you can not submit the request");
                        return null;
                    }
                    if (managerNumber == null) {
                        JSFUtils.addFacesErrorMessage("You donot have Line Manager, So you can not submit the request");
                        return null;
                    }
                    
                    assigneeName = aXBElement.getValue();
                    assigneeNo = managerNumber;
                    assigneeEmail = "";
                } 
                // If step type is USER
                if("USER".equals(stepType)){
                    System.err.println("Submitted : USER ");
                    ArrayList<String> personDetails = new ArrayList<String>();
                    personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
                    assigneeName = personDetails.get(0).toString();
                    assigneeEmail = personDetails.get(1).toString();
                    assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
                } 
                
                System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
                System.err.println("Assigning to (name) ::"+assigneeName);
                System.err.println("Assigning to (no) ::"+assigneeNo);
                
                ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
                ADFUtils.setBoundAttributeValue("StepId",
                                                nextStep.getAttribute("NextStepId"));
                ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
                ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
                
                HashMap subjectHashMap = new HashMap();
                subjectHashMap.put("trip",
                                   ADFUtils.getBoundAttributeValue("TripType"));
                subjectHashMap.put("empName",
                                   ADFUtils.getBoundAttributeValue("PersoneName"));
                subjectHashMap.put("empNumber",
                                   ADFUtils.getBoundAttributeValue("PersoneId"));
                subjectHashMap.put("startDate",
                                   ADFUtils.getBoundAttributeValue("StartDate"));
                subjectHashMap.put("endDate",
                                   ADFUtils.getBoundAttributeValue("EndDate"));
                subjectHashMap.put("days",
                                   ADFUtils.getBoundAttributeValue("Days"));
                Row tripRow =
                    ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
               
                ADFUtils.findOperation("Commit").execute();
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                      ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                      (Number)nextStep.getAttribute("NextStepId"),
                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                      act, "");
                if (emailNotification != null &&
                    emailNotification.equalsIgnoreCase("Y")) {
                    sendEmail(assigneeNo, tripRow);
                }
                JSFUtils.addFacesInformationMessage("Request has been submitted");
                return "back";
            } else {
                JSFUtils.addFacesInformationMessage("you cannot submit the request since there is no distribution combination for the cost center selected!");
                return null;
            }
            //        }
            //        else {
            //            if(ADFUtils.getBoundAttributeValue("BenefitType").toString().equals("P")) {
            //                if (perdiem == null || perdiem.intValue()==0) {
            //
            //                    JSFUtils.addFacesErrorMessage("You Cannot Submit the Request since Grade/PerDiem not assigned to you");
            //                    return null;
            //
            //                }
            //            }
            //
            //            if (totalForInvoice == null||totalForInvoice.intValue()==0) {
            //
            //                JSFUtils.addFacesErrorMessage("You can't Submit this request since Total Amount Due is zero");
            //                return null;
            //
            //            }
            //        }
            //
            ////        }
            //        return "back";
        }
        System.err.println("Else");
        return "back";
    }

    public void sendEmailByEmailExpense(String personEmail, Row subject) {

        sendTripEmail("OFOQ.HR@TATWEER.SA", personEmail,
                      (BusinessTripRequestViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendEmailByEmail(String personEmail, Row subject) {

        sendTripEmail("OFOQ.HR@TATWEER.SA", personEmail,
                      (BusinessTripRequestViewRowImpl)subject, "");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }
    
    public void sendEmailToSir(String personEmail, Row subject) {

        sendTripEmail("OFOQ.HR@TATWEER.SA", personEmail,
                      (BusinessTripRequestViewRowImpl)subject, "SIR");
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendEmailByEmailToSharedServiceDirector(String personEmail,
                                                        Row subject) {

        sendEmailToSharedServiceDirector("OFOQ.HR@TATWEER.SA", personEmail,
                                         (BusinessTripRequestViewRowImpl)subject);
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public void sendEmailToAccountant(String personEmail, Row subject) {

        sendExpenseEmail("OFOQ.HR@TATWEER.SA", personEmail,
                         (BusinessTripRequestViewRowImpl)subject);
        JSFUtils.addFacesInformationMessage("Mail has been sent");

    }

    public String approve_action() throws Exception {
        // Add event code here...
        int updateOrInsert = 0;
        int approvalDone = 0;
        String personNumberCheck =
            (String)ADFUtils.getBoundAttributeValue("PersoneId");
        String costCenter =
            (String)ADFUtils.getBoundAttributeValue("CostCenterNumber");
        String division = (String)ADFUtils.getBoundAttributeValue("Division");
        String lob = (String)ADFUtils.getBoundAttributeValue("Lob");
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        String returnValue = null;
        Row tripRow =
            ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
        String tripType =
            ADFUtils.getBoundAttributeValue("TripType").toString();
        String finalapproval = null;
        String assignee =
            ADFUtils.getBoundAttributeValue("Assignee").toString();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String requestTripType =
            ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
                String TripTypeName=requestTripType +'-'+ personLocation;
                ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
        //        String[] mainPositions={"Head Of Corporate Support Division","CFO","Chief Executive Officer","Payroll Manager","HR Manager","Budget Controller","HR Manager"};
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        System.out.println("next step is " +
                           nextStep.getAttribute("NextStepId"));
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        String fyiEmailAddress = nextStep.getAttribute("FyiEmail")!=null?nextStep.getAttribute("FyiEmail").toString():null;
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        String budgetValidCheck =
            (String)nextStep.getAttribute("BudgetValidation");
        BudgetAmountValidation budgetAmount = new BudgetAmountValidation();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;

        userDetails =
                userService.getUserDetailsByPersonNumber(personNumberCheck);
        List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
        
        /*
         * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
         * Issue Id #21361 
         * Commented
        Long lineManagerID = relationshipDetails.get(0).getManagerId();
        String department =  relationshipDetails.get(0).getDepartmentName();
        String userEmployeeType=relationshipDetails.get(0).getUserPersonType();
        UserDetails managerDetails = userService.getUserDetailsByPersonId(lineManagerID);
        String managerNumber = managerDetails.getPersonNumber();
        JAXBElement<String> aXBElement = null;
        if (managerDetails.getUserPersonDetails().get(0).getDisplayName() != null) {
            aXBElement = managerDetails.getUserPersonDetails().get(0).getDisplayName();
        }
        */
        
        /*
         * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
         * Issue Id #21361 
         * Added
         */
        Long lineManagerID = 0L;
        String managerNumber = null;
        JAXBElement<String> aXBElement = null;
        String position = relationshipDetails.get(0).getPositionName();
        System.out.println("CEO Position: "+position);
        if(!(position.equalsIgnoreCase("THC Group CEO"))){
            lineManagerID = relationshipDetails.get(0).getManagerId();
            UserDetails managerDetails = userService.getUserDetailsByPersonId(lineManagerID);
            managerNumber = managerDetails.getPersonNumber();
            if (managerDetails.getUserPersonDetails().get(0).getDisplayName() != null) {
                aXBElement = managerDetails.getUserPersonDetails().get(0).getDisplayName();
            }
        }else{
            nextOpr = ADFUtils.findOperation("getNextStepCEO");
            nextStep = (Row)nextOpr.execute();
            emailNotification = (String)nextStep.getAttribute("EmailNotification");
            budgetValidCheck = (String)nextStep.getAttribute("BudgetValidation");
            stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            System.err.println("Submitted: stepType CEO is::"+stepType);
        }
        String department = relationshipDetails.get(0).getDepartmentName();
        String userEmployeeType=relationshipDetails.get(0).getUserPersonType();
        
        boolean value = false;
        if (budgetValidCheck.equals("Y")) {
            value = budgetAmount.getRemainingBudgetAmount(division, lob, costCenter,
                                                          "5101030201",
                                                          "OtherCost",
                                                          (String)ADFUtils.getBoundAttributeValue("CreationDate"));
            String mean_trvael = (String)ADFUtils.getBoundAttributeValue("MeansTravel");
            if (value) {
                if (mean_trvael.equalsIgnoreCase("Car (Employee)") ||
                    mean_trvael.equalsIgnoreCase("Train (Employee)") ||
                    mean_trvael.equalsIgnoreCase("Plane (Employee)")) {
                    value =
                            budgetAmount.getRemainingBudgetAmount(division, lob,
                                                                  costCenter,
                                                                  "5101030202",
                                                                  "TravelCost",
                                                                  (String)ADFUtils.getBoundAttributeValue("CreationDate"));
                    if (!value) {
                        JSFUtils.addFacesErrorMessage("Budget not available for the cost center selected and Travel cost Account number 5101030201");
                        return null;
                    }
                }

            } else {
                JSFUtils.addFacesErrorMessage("Budget not available for the cost center selected and other cost Account number 5101030201");
                return null;
            }
        }
        //ADFUtils.setBoundAttributeValue("ActionTaken", "APPROVED");

     //2023 Approval Hierarchy Enhancement change 10-AUG-2023  if (assignee.equals("CEO")){
//            System.err.println("IN CEO step--***");
//            ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
//            ADFUtils.setBoundAttributeValue("Assignee", nextStep.getAttribute("NextAssignee"));
//            ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//            approvalDone = 1;
//            ADFUtils.findOperation("Commit").execute();
//            nextStep = (Row)nextOpr.execute();
             
/*            if (tripType.equals("Local") || tripType.equals("Inter")) {


                BiReportAccess report = new BiReportAccess();

                List<Map> personData = null;
                try {

                    personData =
                            report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ADFUtils.setBoundAttributeValue("StepId", 6);
                //                ADFUtils.setBoundAttributeValue("Assignee",
                //                                                "Executive Director, Shared Services Sector (Acting)");
                //                ADFUtils.setBoundAttributeValue("Assignee",
                //                                                "1441");
                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    ADFUtils.setBoundAttributeValue("Assignee",
                                                    personData.get(0).get("PERSON_NUMBER").toString());
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME").toString());

                } else {

                    ADFUtils.setBoundAttributeValue("AssigneeName", "");

                }
                ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");

                ADFUtils.findOperation("Commit").execute();
                String returnvalue =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                          (Number)nextStep.getAttribute("StepId"),
                                                          (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "N");


                //          Head Of Corporate Support Division

                //                BiReportAccess report = new BiReportAccess();
                //                //                ReportsUtils report = new ReportsUtils();
                //                List<Map> personData = null;
                //                try {
                //                    //                            personData = report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
                //                    personData =
                //                            report.getPersonByPostionReport("Director of IT & C");
                //                } catch (Exception e) {
                //                    e.printStackTrace();
                //                }
                //
                //
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmailToSharedServiceDirector(personData.get(0).get("EMAIL_ADDRESS").toString(),
                                                                tripRow);
                    }
                } else {
                    //                                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Head Of Corporate dosn't has email");
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");
                }
                //                sendEmailByEmailToSharedServiceDirector("Rashid.Maslokhi@tatweer.sa",
                //                                                        tripRow);
                JSFUtils.addFacesInformationMessage("Request has been approved");
                return "back";


            }


            else if (tripType.equals("Event") || tripType.equals("Training")) {


                BiReportAccess report = new BiReportAccess();

                List<Map> personData = null;
                try {

                    personData =
                            report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ADFUtils.setBoundAttributeValue("StepId", 7);
                //                        ADFUtils.setBoundAttributeValue("Assignee","Head Of Corporate");
                //                ADFUtils.setBoundAttributeValue("Assignee",
                //                                                "Director of IT & C");
                //                ADFUtils.setBoundAttributeValue("Assignee",
                //                                                "Executive Director, Shared Services Sector (Acting)");
                //                ADFUtils.setBoundAttributeValue("Assignee",
                //                                                "1441");
                if (personData.get(0).get("PERSON_NUMBER") != null) {
                    ADFUtils.setBoundAttributeValue("Assignee",
                                                    personData.get(0).get("PERSON_NUMBER").toString());
                }
                if (personData.get(0).get("DISPLAY_NAME") != null) {
                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    personData.get(0).get("DISPLAY_NAME").toString());

                } else {

                    ADFUtils.setBoundAttributeValue("AssigneeName", "");

                }

                ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");

                ADFUtils.findOperation("Commit").execute();
                String returnvalue =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                          (Number)nextStep.getAttribute("StepId"),
                                                          (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "N");
                updateOrInsert = 1;


                //                BiReportAccess report = new BiReportAccess();
                //                //                ReportsUtils report = new ReportsUtils();
                //                List<Map> personData = null;
                //                try {
                //                    //                        personData = report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
                //                    personData =
                //                            report.getPersonByPostionReport("Director of IT & C");
                //                } catch (Exception e) {
                //                    e.printStackTrace();
                //                }
                //
                //
                //                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                //                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
                //                                     tripRow);
                //                } else {
                //                    //                                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Head Of Corporate dosn't has email");
                //                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Director of IT & C dosn't has email");
                //                }

                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailByEmailToSharedServiceDirector(personData.get(0).get("EMAIL_ADDRESS").toString(),
                                                                tripRow);
                    }
                } else {
                    //                                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Head Of Corporate dosn't has email");
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");
                }

                //                sendEmailByEmailToSharedServiceDirector("Rashid.Maslokhi@tatweer.sa",
                //                                                        tripRow);
                JSFUtils.addFacesInformationMessage("Request has been approved");
                return "back";


            }
*/
     //2023 Approval Hierarchy Enhancement change 10-AUG-2023  }

        if (tripType.equals("Local") || tripType.equals("Inter")) {

            if (nextStep.getAttribute("NextAssignee").equals("Cost Center Manager")) {
                approvalDone = 1;
                Object costCenterName =
                    ADFUtils.getBoundAttributeValue("CostCenter");
                if (costCenter != null) {
                    String managerOfDeptNum = null;
                    String managerOfDeptName = null;
                    String specialEducationDepartment =
                        (String)ADFUtils.getBoundAttributeValue("PersonDepartment");
//                    if (specialEducationDepartment != null &&
//                        specialEducationDepartment.equalsIgnoreCase("Special Education Program")) {
//                        managerOfDeptNum = "1206";
//                        managerOfDeptName = "Abdulrahman Hasan";
//                    } else {
                        BiReportAccess BIRA = new BiReportAccess();

                        try {
                            System.err.println("Inside Local===>>>");
                            List<Map> managerOfDeptList =
                                BIRA.getManagerOfDepartmentData(costCenterName.toString());
                            if (managerOfDeptList.get(0).get("PERSON_NUMBER") !=
                                null) {
                                managerOfDeptNum =
                                        managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                                System.err.println("Inside ===>>>Cost Center Manager Number" + managerOfDeptNum);
                            }
                            if (managerOfDeptList.get(0).get("DISPLAY_NAME") !=
                                null) {
                                managerOfDeptName =
                                        managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                                System.err.println("Inside ===>>>Cost Center Manager Name" + managerOfDeptName);
                            }
                            if(managerOfDeptNum == null) 
                                {
                                    JSFUtils.addFacesErrorMessage("There is no manager for "+costCenterName.toString()+" so you can't submit the request");
                                    return null;
                                }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    //}

                    System.err.println(" Assignee is : " + assignee);
                    System.err.println(" managerOfDeptNum is : " +
                                       managerOfDeptNum);
                    //                    if (assignee == managerOfDeptNum)
                    //
                    //                    {
                    //                        ADFUtils.setBoundAttributeValue("StepId", 4);
                    //                        ADFUtils.setBoundAttributeValue("Assignee",
                    //                                                        "HR Payroll and benefits Supervisor");
                    //                        //                    ADFUtils.setBoundAttributeValue("Assignee","Payroll Manager");
                    //
                    //                        ADFUtils.setBoundAttributeValue("RequestStatus",
                    //                                                        "PENDING");
                    //
                    //                        ADFUtils.findOperation("Commit").execute();
                    //                        JSFUtils.addFacesInformationMessage("Request has been approved");
                    //
                    //                        BiReportAccess report = new BiReportAccess();
                    //                        //                ReportsUtils report = new ReportsUtils();
                    //                        List<Map> personData = null;
                    //                        try {
                    //                            personData =
                    //                                    report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
                    //                        } catch (Exception e) {
                    //                            e.printStackTrace();
                    //                        }
                    //
                    //
                    //                        if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    //                            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
                    //                                             tripRow);
                    //                        } else {
                    //                            JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
                    //
                    //                        }
                    //
                    //                        return "back";
                    //                    } else {


                    ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
                    ADFUtils.setBoundAttributeValue("Assignee",
                                                    managerOfDeptNum);
                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    managerOfDeptName);

                    ADFUtils.setBoundAttributeValue("RequestStatus",
                                                    "PENDING");
                    //System.err.println("ADFUtils.getBoundAttributeValue(\"StepId\")"+ADFUtils.getBoundAttributeValue("StepId")+"========>"+ADFUtils.getBoundAttributeValue("NextStepId"));
                    System.err.println("Current Step====>" +
                                       ADFUtils.getBoundAttributeValue("StepId"));
                    ADFUtils.findOperation("Commit").execute();
                    String returnvalue =
                        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                              ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                              (Number)nextStep.getAttribute("StepId"),
                                                              (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                              (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                              "APPROVE_ACT",
                                                              "N");
                    updateOrInsert = 1;
                    System.err.println("From Package====>" + returnvalue);
                    if (returnvalue.equalsIgnoreCase("SUCCESS")) {
                        ADFUtils.findOperation("Commit").execute();
                    } else {
                        ADFUtils.findOperation("Rollback").execute();
                    }
                    ViewObject vo =
                        ADFUtils.findIterator("ApprovalHistoryBusinessIterator").getViewObject();
                    vo.executeQuery();
                    System.err.println("CounntOFVO=========>" +
                                       vo.getEstimatedRowCount());
                    JSFUtils.addFacesInformationMessage("Request has been approved");
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmail(managerOfDeptNum, tripRow);
                    }
                    return "back";


                    //                    }
                }

            }
        }

        else if (tripType.equals("Event") || tripType.equals("Training")) {


            if (nextStep.getAttribute("NextAssignee").equals("Cost Center Manager")) {
                approvalDone = 1;
                Object costCenterName =
                    ADFUtils.getBoundAttributeValue("CostCenter");
                if (costCenter != null) {
                    String managerOfDeptNum = null;
                    String managerOfDeptName = null;
                    String specialEducationDepartment =
                        (String)ADFUtils.getBoundAttributeValue("PersonDepartment");
//                    if (specialEducationDepartment != null &&
//                        specialEducationDepartment.equalsIgnoreCase("Special Education Program")) {
//                        managerOfDeptNum = "1206";
//                        managerOfDeptName = "Abdulrahman Hasan";
//                    } else {
                        BiReportAccess BIRA = new BiReportAccess();
                        try {

                            List<Map> managerOfDeptList =
                                BIRA.getManagerOfDepartmentData(costCenterName.toString()); 
                            if (managerOfDeptList.get(0).get("PERSON_NUMBER") !=
                                null) {
                                managerOfDeptNum =
                                        managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                                System.err.println("Inside ===>>>Cost Center Manager Number" + managerOfDeptNum);
                            }
                            if (managerOfDeptList.get(0).get("DISPLAY_NAME") !=
                                null) {
                                managerOfDeptName =
                                        managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                                System.err.println("Inside ===>>>Cost Center Manager Name" + managerOfDeptName);
                            }
                            if(managerOfDeptNum == null) 
                                {
                                    JSFUtils.addFacesErrorMessage("There is no manager for "+costCenterName.toString()+" so you can't submit the request");
                                    return null;
                                }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                   // }

                    System.err.println(" Assignee is : " + assignee);
                    System.err.println(" managerOfDeptNum is : " +
                                       managerOfDeptNum);
                    //                    if (assignee == managerOfDeptNum)
                    //
                    //                    {
                    //                        ADFUtils.setBoundAttributeValue("StepId", 4);
                    //                        ADFUtils.setBoundAttributeValue("Assignee",
                    //                                                        "HR Manager");
                    //                        ADFUtils.setBoundAttributeValue("RequestStatus",
                    //                                                        "PENDING");
                    //
                    //                        ADFUtils.findOperation("Commit").execute();
                    //                        JSFUtils.addFacesInformationMessage("Request has been approved");
                    //
                    //                        BiReportAccess report = new BiReportAccess();
                    //                        //                ReportsUtils report = new ReportsUtils();
                    //                        List<Map> personData = null;
                    //                        try {
                    //                            personData =
                    //                                    report.getPersonByPostionReport("HR Manager");
                    //                        } catch (Exception e) {
                    //                            e.printStackTrace();
                    //                        }
                    //
                    //
                    //                        if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    //                            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
                    //                                             tripRow);
                    //                        } else {
                    //                            JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR Manager dosn't has email");
                    //
                    //                        }
                    //
                    //                        return "back";
                    //                    } else {


                    ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
                    ADFUtils.setBoundAttributeValue("Assignee",
                                                    managerOfDeptNum);
                    ADFUtils.setBoundAttributeValue("AssigneeName",
                                                    managerOfDeptName);
                    ADFUtils.setBoundAttributeValue("RequestStatus",
                                                    "PENDING");

                    ADFUtils.findOperation("Commit").execute();
                    //System.err.println("ADFUtils.getBoundAttributeValue(\"StepId\")"+ADFUtils.getBoundAttributeValue("StepId")+"========>"+ADFUtils.getBoundAttributeValue("NextStepId"));
                    String returnvalue =
                        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                              ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                              (Number)nextStep.getAttribute("StepId"),
                                                              (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                              (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                              "APPROVE_ACT",
                                                              "N");
                    updateOrInsert = 1;
                    System.err.println("returnvalue========" + returnvalue);
                    if (returnvalue.equalsIgnoreCase("SUCCESS")) {
                        ADFUtils.findOperation("Commit").execute();
                    } else {
                        ADFUtils.findOperation("Rollback").execute();
                    }
                    JSFUtils.addFacesInformationMessage("Request has been approved");
                    if (emailNotification != null &&
                        emailNotification.equalsIgnoreCase("Y")) {
                        sendEmail(managerOfDeptNum, tripRow);
                    }
                    return "back";


                    //                    }
                }

            }


        }

//
//        if (nextStep.getAttribute("NextAssignee").equals("HR Payroll and benefits Supervisor")) {
//            approvalDone = 1;
//            System.err.println("Next Director===>");
//            System.err.println("Current Step" +
//                               ADFUtils.getBoundAttributeValue("StepId"));
//            System.err.println("Next Step" +
//                               nextStep.getAttribute("NextStepId"));
//            System.err.println("Next Step" + nextStep.getAttribute("StepId"));
//            BiReportAccess report = new BiReportAccess();
//            //                ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//            try {
//                personData =
//                        report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (personData.get(0).get("DISPLAY_NAME") != null) {
//
//                ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                personData.get(0).get("DISPLAY_NAME").toString());
//                ADFUtils.setBoundAttributeValue("Assignee", 
//                                                personData.get(0).get("PERSON_NUMBER").toString());
//            }
//            System.err.println("Current Step" +
//                               ADFUtils.getBoundAttributeValue("StepId"));
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//            System.err.println("prevs===" + nextStep.getAttribute("StepId") +
//                               "Current===>" +
//                               ADFUtils.getBoundAttributeValue("StepId"));
//            String returnvalue =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
//                                                      ADFUtils.getBoundAttributeValue("TripType").toString(),
//                                                      (Number)nextStep.getAttribute("StepId"),
//                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                      "APPROVE_ACT", "N");
//            updateOrInsert = 1;
//            System.err.println("returnvalue========" + returnvalue);
//            if (returnvalue.equalsIgnoreCase("SUCCESS")) {
//                ADFUtils.findOperation("Commit").execute();
//            } else {
//                ADFUtils.findOperation("Rollback").execute();
//            }
//            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                if (emailNotification != null &&
//                    emailNotification.equalsIgnoreCase("Y")) {
//                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                                     tripRow);
//                }
//            } else {
//                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
//
//            }
//
//        }
//
//        //    Financial Planning & Analysis Manager
//        if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")) {
//            approvalDone = 1;
//            System.err.println("Called From Financial");
//            System.err.println("Current Step" +
//                               ADFUtils.getBoundAttributeValue("StepId"));
//            System.err.println("Next Step" +
//                               nextStep.getAttribute("NextStepId"));
//            System.err.println("Current From Step" +
//                               nextStep.getAttribute("StepId"));
//            BiReportAccess report = new BiReportAccess();
//            //                ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//            try {
//                personData =
//                        report.getPersonByPostionReport("Financial Planning & Analysis Manager");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (personData.get(0).get("DISPLAY_NAME") != null) {
//
//                ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                personData.get(0).get("DISPLAY_NAME").toString());
//                ADFUtils.setBoundAttributeValue("Assignee", 
//                                                personData.get(0).get("PERSON_NUMBER").toString());
//            }
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//         
//            String returnvalue =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
//                                                      ADFUtils.getBoundAttributeValue("TripType").toString(),
//                                                      (Number)nextStep.getAttribute("StepId"),
//                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                      "APPROVE_ACT", "N");
//            updateOrInsert = 1;
//            System.err.println("returnvalue========" + returnvalue);
//            if (returnvalue.equalsIgnoreCase("SUCCESS")) {
//                ADFUtils.findOperation("Commit").execute();
//            } else {
//                ADFUtils.findOperation("Rollback").execute();
//            }
//            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                if (emailNotification != null &&
//                    emailNotification.equalsIgnoreCase("Y")) {
//                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                                     tripRow);
//                }
//            } else {
//                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Financial Planning & Analysis Manager dosn't has email");
//
//            }
//            System.err.println("Called From Financia=====l");
//        }
//
//        if (nextStep.getAttribute("NextAssignee").equals("HR and Admin Director")) {
//            approvalDone = 1;
//            System.err.println("Next Director===>1");
//            BiReportAccess report = new BiReportAccess();
//            //                ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//            try {
//                personData =
//                        report.getPersonByPostionReport("HR and Admin Director");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//            if (personData.get(0).get("DISPLAY_NAME") != null) {
//                ADFUtils.setBoundAttributeValue("Assignee",
//                                                personData.get(0).get("PERSON_NUMBER"));
//                ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                personData.get(0).get("DISPLAY_NAME").toString());
//            }
//           
//            String returnvalue =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
//                                                      ADFUtils.getBoundAttributeValue("TripType").toString(),
//                                                      (Number)nextStep.getAttribute("StepId"),
//                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                      "APPROVE_ACT", "N");
//            updateOrInsert = 1;
//            System.err.println("returnvalue========" + returnvalue);
//            if (returnvalue.equalsIgnoreCase("SUCCESS")) {
//                ADFUtils.findOperation("Commit").execute();
//            } else {
//                ADFUtils.findOperation("Rollback").execute();
//            }
//            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                if (emailNotification != null &&
//                    emailNotification.equalsIgnoreCase("Y")) {
//                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                                     tripRow);
//                }
//            } else {
//                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR and Admin Director dosn't has email");
//
//            }
//
//        }
//
//        //        if (nextStep.getAttribute("NextAssignee").equals("Head Of Corporate")){
//        if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")) {
//            approvalDone = 1;
//            System.err.println("Next Director===>2");
//            BiReportAccess report = new BiReportAccess();
//
//            List<Map> personData = null;
//            try {
//                personData =
//                        report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (personData.get(0).get("DISPLAY_NAME") != null) {
//
//                ADFUtils.setBoundAttributeValue("AssigneeName",
//                                                personData.get(0).get("DISPLAY_NAME").toString());
//            }
//            if (personData.get(0).get("PERSON_NUMBER") != null) {
//                ADFUtils.setBoundAttributeValue("Assignee",
//                                                personData.get(0).get("PERSON_NUMBER").toString());
//            }
//            ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
//            ADFUtils.setBoundAttributeValue("StepId",
//                                            nextStep.getAttribute("NextStepId"));
//
//            //Set Expense flag
//            //            String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
//
//            ADFUtils.findOperation("Commit").execute();
//            JSFUtils.addFacesInformationMessage("Request has been approved");
//            String returnvalue =
//                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
//                                                      ADFUtils.getBoundAttributeValue("TripType").toString(),
//                                                      (Number)nextStep.getAttribute("StepId"),
//                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                      "APPROVE_ACT", "N");
//            updateOrInsert = 1;
//            System.err.println("returnvalue========" + returnvalue);
//            if (returnvalue.equalsIgnoreCase("SUCCESS")) {
//                ADFUtils.findOperation("Commit").execute();
//            } else {
//                ADFUtils.findOperation("Rollback").execute();
//            }
//            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                if (emailNotification != null &&
//                    emailNotification.equalsIgnoreCase("Y")) {
//                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(),
//                                     tripRow);
//                }
//            } else {
//                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");
//
//            }
//
//            return "back";
//        }


        //2023 Approval Hierarchy Enhancement change 10-AUG-2023 if (nextStep.getAttribute("NextAssignee").equals("CEO")) {
           // approvalDone = 1;
            //Check Number of Days
//            OperationBinding daysOpr =
//                ADFUtils.findOperation("ExecuteNumDays");
//            daysOpr.execute();
//            Number days = (Number)ADFUtils.getBoundAttributeValue("SumDays");
//            if (days != null) {
//                Number currentDays =
//                    (Number)ADFUtils.getBoundAttributeValue("Days");
//                int totalDays = days.intValue() + currentDays.intValue();
//                if (totalDays <= 30) {
//                    //skip CEO step
//                    ADFUtils.setBoundAttributeValue("StepId",
//                                                    nextStep.getAttribute("NextStepId"));
//                    nextStep = (Row)nextOpr.execute();
//                }
//            }
       //2023 Approval Hierarchy Enhancement change 10-AUG-2023  }


        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
            approvalDone = 1;
            ArrayList<String> combinationIDs = null;
            combinationIDs =
                    getCodeCombinationId(personNumberCheck, costCenter,
                                         division, lob);
            String codeCombinationIdLine1 = combinationIDs.get(0);
            String codeCombinationIdLine2 = combinationIDs.get(1);
            if ((codeCombinationIdLine1 != null &&
                 !codeCombinationIdLine1.equalsIgnoreCase("-1")) &&
                (codeCombinationIdLine2 != null &&
                 !codeCombinationIdLine2.equalsIgnoreCase("-1"))) {
                String personNumber =
                    (String)JSFUtils.resolveExpression("#{bindings.PersoneId.inputValue}");
                //            UserDetails byPersonNumber =
                //                userService.getUserDetailsByPersonNumber(personNumber);
                String personName =
                    (String)JSFUtils.resolveExpression("#{bindings.PersoneName.inputValue}");

//              if (tripType.equalsIgnoreCase("Expense")) {
                if (true) {
                    System.err.println("Invoicen creation process !!");
                    ADFUtils.findOperation("changeExpenseFlag").execute();
                    if (ADFUtils.getBoundAttributeValue("CostCenterNumber") !=
                        null) {

                        //Division
                        //Lob
                        System.err.println("CostCenterNumber >>> " +
                                           costCenter);
                        System.err.println("divison >>> " + division);
                        System.err.println("lob >>> " + lob);
                    } else {


                        JSFUtils.addFacesErrorMessage("You can't approve this request as cost center number has no value");
                        return null;

                    }

                    if (combinationIDs == null) {
                        JSFUtils.addFacesErrorMessage("You can't approve the request");
                        return null;

                    }
                    Long longCodeCombinationIdLine1 =
                        Long.parseLong(codeCombinationIdLine1);

                    Long longCodeCombinationIdLine2 =
                        Long.parseLong(codeCombinationIdLine2);
                    System.out.println("longCodeCombinationIdLine1:" +
                                       longCodeCombinationIdLine1);
                    System.out.println("longCodeCombinationIdLine2:" +
                                       longCodeCombinationIdLine2);
                    String requestId =
                        ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString();
                    String invoiceNumber =
                        personNumber + "-" + requestId + "-BT";
                    System.err.println("invoiceNumber >>>> " + invoiceNumber);

                    String wayOfTravel =
                        (String)ADFUtils.getBoundAttributeValue("MeansTravel");
                    String creationDate =
                        ADFUtils.getBoundAttributeValue("CreationDate").toString();
                    System.err.println("");
                    if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
                        wayOfTravel.equalsIgnoreCase("Train (Employee)") ||
                        wayOfTravel.equalsIgnoreCase("Plane (Employee)")) {
                        Number perdiemAmount = null;
                        Number travelCost = null;
                        Number totalAmount = null;
                        Number otherCostAmount = null;
                        Number secondLineAmount = null;
                        Number firstLineAmount = null;
                        Number otherCostByEmployee = null;
                        Number TrainingCostByEmployee = null;
                        Number TravelCostByEmployee = null;
                        Number VisaCost = null;
                        Number EventCost = null;
                        Number ExpenseBusinessTripId = null;
                        String ToCity=null;
                        String NewCity=null;
                        Number PerDiemAmount = null;
                        if (ADFUtils.getBoundAttributeValue("VisaCost") !=
                            null) {
                            VisaCost =
                                    (Number)ADFUtils.getBoundAttributeValue("VisaCost");
                        } else {
                            VisaCost = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("EventCost") !=
                            null) {
                            EventCost =
                                    (Number)ADFUtils.getBoundAttributeValue("EventCost");
                        } else {
                            EventCost = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("ExpenseBusinessTripId") !=
                            null) {
                            ExpenseBusinessTripId =
                                    (Number)ADFUtils.getBoundAttributeValue("ExpenseBusinessTripId");
                        } else {
                            ExpenseBusinessTripId = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("ToCity") !=
                            null) {
                            ToCity = (String)ADFUtils.getBoundAttributeValue("ToCity");
                        } else {
                            ToCity = "";
                        }
                        if (ADFUtils.getBoundAttributeValue("NewCity") !=
                            null) {
                            NewCity = (String)ADFUtils.getBoundAttributeValue("NewCity");
                        } else {
                            NewCity = "";
                        }
                        if (ADFUtils.getBoundAttributeValue("PerDiemAmount") !=
                            null) {
                            PerDiemAmount =
                                    (Number)ADFUtils.getBoundAttributeValue("PerDiemAmount");
                        } else {
                            PerDiemAmount = new Number(0);
                        }
                        if (benefitType.equalsIgnoreCase("A")) {
                            if (ADFUtils.getBoundAttributeValue("TravelCostByEmployee") !=
                                null) {
                                travelCost =
                                        (Number)ADFUtils.getBoundAttributeValue("TravelCostByEmployee");
                            } else {
                                travelCost = new Number(0);
                            }

                            if (ADFUtils.getBoundAttributeValue("OtherByEmployee") !=
                                null) {
                                otherCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherByEmployee");
                            } else {
                                otherCostAmount = new Number(0);

                            }


                            totalAmount = otherCostAmount.add(travelCost);
                            secondLineAmount = travelCost;
                            firstLineAmount = otherCostAmount;
                        } else if (benefitType.equalsIgnoreCase("P")) {
                            if (ADFUtils.getBoundAttributeValue("TravelCost") !=
                                null) {
                                travelCost =
                                        (Number)ADFUtils.getBoundAttributeValue("TravelCost");
                            } else {
                                travelCost = new Number(0);
                            }

                            if (ADFUtils.getBoundAttributeValue("OtherCost") !=
                                null) {
                                otherCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
                            } else {
                                otherCostAmount = new Number(0);

                            }
                            if (ADFUtils.getBoundAttributeValue("PerDiemAmount") !=
                                null) {
                                perdiemAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("PerDiemAmount");
                            } else {
                                perdiemAmount = new Number(0);
                            }
                            if (ADFUtils.getBoundAttributeValue("OtherByEmployee") !=
                                null) {
                                otherCostByEmployee =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherByEmployee");
                            } else {
                                otherCostByEmployee = new Number(0);
                            }
                            if (ADFUtils.getBoundAttributeValue("TrainingByEmployee") !=
                                null) {
                                TrainingCostByEmployee =
                                        (Number)ADFUtils.getBoundAttributeValue("TrainingByEmployee");
                            } else {
                                TrainingCostByEmployee = new Number(0);
                            }
                            if (ADFUtils.getBoundAttributeValue("TravelCostByEmployee") !=
                                null) {
                                TravelCostByEmployee =
                                        (Number)ADFUtils.getBoundAttributeValue("TravelCostByEmployee");
                            } else {
                                TravelCostByEmployee = new Number(0);
                            }



                      //   2022   totalAmount =
                                   // otherCostAmount.add(travelCost).add(perdiemAmount);
                          
                                totalAmount =
                                        otherCostAmount.add(TravelCostByEmployee).add(perdiemAmount).add(otherCostByEmployee).add(TrainingCostByEmployee);
                            
                            secondLineAmount = TravelCostByEmployee;
                            firstLineAmount =
                                    otherCostByEmployee.add(perdiemAmount).add(TrainingCostByEmployee);
                        }


                        String description = personNumber + " - " + personName;
                        System.err.println("Outside Loop");
                        if (totalAmount.intValue() > 0) {
                            System.err.println("Inside Loop:totalAmount"+totalAmount);
                            System.err.println("User Employee"+userEmployeeType);
                          if(!userEmployeeType.equals("Employee")){ //2023 Element Entry Enhancement
//                            Map param = new HashMap();
//                            param.put("vendorName",
//                                      "Business Trip"); //select vendor_name  from poz_suppliers_v
//                            param.put("vendorId",
//                                      "300000004500418"); //select vendor_id from poz_suppliers_v
//                            param.put("vendorSiteCode", "Riyadh");
//                            param.put("ledgerId",
//                                      "300000001778002"); //300000001778002 //select * from XCC_BCE_LEDGERS
//                            param.put("orgId",
//                                      "300000001642073"); //300000001642073
//                            param.put("invoiceNumber", invoiceNumber);
//                            param.put("invoiceAmount", totalAmount);
//                            param.put("paymentCurrencyCode", "SAR");
//                            param.put("invoiceTypeLookupCode", "STANDARD");
//                            param.put("termsName", "Immediate");
//                            param.put("paymentMethod", "WIRE");
//                            param.put("Description", description);
//                            param.put("CreationDate", creationDate);
//                            int lineNumber = 1;
//                            List<Map> lineList = null;
//
//                            if (firstLineAmount.intValue() > 0) {
//                                Map line1 = new HashMap();
//                                line1.put("lineNumber", lineNumber);
//                                line1.put("lineType", "ITEM");
//                                line1.put("lineAmount", firstLineAmount); //var
//                                line1.put("lineCurrencyCode", "SAR");
//                                line1.put("codeCombinationId",
//                                          longCodeCombinationIdLine1);
//                                line1.put("personNumber", personNumber);
//                                line1.put("bankName", "");
//                                line1.put("IBAN", "");
//                                line1.put("SwiftCode", "");
//                                line1.put("PaymentType", "None");
//
//                                lineList = new ArrayList<Map>();
//                                lineList.add(line1);
//                                lineNumber++;
//                            }
//                            if (secondLineAmount.intValue() > 0)
//
//                            {
//                                Map line2 = new HashMap();
//                                line2.put("lineNumber", lineNumber);
//                                line2.put("lineType", "ITEM");
//                                line2.put("lineAmount",
//                                          secondLineAmount); //var
//                                line2.put("lineCurrencyCode", "SAR");
//                                line2.put("codeCombinationId",
//                                          longCodeCombinationIdLine2);
//                                line2.put("personNumber", personNumber);
//                                line2.put("bankName", "");
//                                line2.put("IBAN", "");
//                                line2.put("SwiftCode", "");
//                                line2.put("PaymentType", "None");
//                                if (lineList == null) {
//                                    lineList = new ArrayList<Map>();
//                                }
//                                lineList.add(line2);
//                            }
//
//                            List<Map> attacheList = new ArrayList<Map>();
//
//                            ViewObject attachmentVO =
//                                ADFUtils.findIterator("RequestAttachmentsView1Iterator").getViewObject();
//                            if (attachmentVO.getEstimatedRowCount() > 0) {
//                                Row[] rows = attachmentVO.getAllRowsInRange();
//                                Row row;
//                                Map attachmentMap = null;
//                                for (int i = 0; i < rows.length; i++) {
//                                    attachmentMap = new HashMap();
//                                    row = rows[i];
//                                    attachmentMap.put("attachmentType",
//                                                      "FILE");
//                                    attachmentMap.put("category",
//                                                      "To Payables");
//                                    attachmentMap.put("fileTitle",
//                                                      row.getAttribute("FileName"));
//                                    BlobDomain blob =
//                                        (BlobDomain)row.getAttribute("AttachmentFile");
//                                    InputStream inputStream =
//                                        blob.getInputStream();
//                                    byte[] bytes = null;
//                                    try {
//                                        bytes =
//                                                IOUtils.toByteArray(inputStream);
//                                    } catch (IOException e) {
//                                    }
//                                    attachmentMap.put("fileContent",
//                                                      DatatypeConverter.printBase64Binary(bytes));
//                                    attacheList.add(attachmentMap);
//                                }  
//                            }
//                            Map attachmentMap = new HashMap();
//                            attachmentMap.put("attachmentType","FILE");
//                            attachmentMap.put("category","To Payables");
//                            attachmentMap.put("fileTitle", "BusinessTripAttachment.pdf"); 
//                            attachmentMap.put("fileContent",
//                                            DatatypeConverter.printBase64Binary(this.getBusinessTripAttach()));
//                      
//                            attacheList.add(attachmentMap);  
//
//                            System.err.println("Param >>> " +
//                                               param);
//                            System.err.println("lineList >>> " +
//                                               lineList);
//                            System.err.println("attacheList >>> " +
//                                               attacheList);
//                                                      
//                            APConsumer newAPInvoice = new APConsumer();
//                            returnValue =
//                                    newAPInvoice.createInvoice(param, lineList,
//                                                               attacheList);
                               System.err.println("Inside Invoice Mail");
                               byte[] bytes = null;
                             //  bytes=this.getBusinessTripAttach();
                               BlobDomain blob =new BlobDomain(bytes);
                               OperationBinding sendMail =
                               ADFUtils.findOperation("callSendInvoiceEmailStoredPL");
                               sendMail.getParamsMap().put("p_request_type", tripType);
                               sendMail.getParamsMap().put("p_request_number", requestId);
                               sendMail.getParamsMap().put("p_attachment", blob);
                               sendMail.execute();
                               returnValue = "success";
                              invoiceNumber="";
                             
                          }
                            
                            if ((tripType.equals("Training")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                         
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                            System.out.println("myDateString  >>> " + formattedDate);
                            //EES - AccountNumber
                            OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                            Map paramMap = oper.getParamsMap();
                            paramMap.put("elementName", "Training Earnings");
                            Row elementAccountNo = (Row)oper.execute();
                            String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                            System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Training Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             
//                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 if(firstLineAmount.intValue() > 0){
                             params.put("AssignmentNumber",
                             datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                             //params.put("Amount",totalAmount.toString());
                             params.put("Amount",firstLineAmount.toString());
                             params.put("StartDate", formattedDate);
                             params.put("Count",
                                                datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                             params.put("TripType", "Training");
                             params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                             params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                             params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                             params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                             params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                             params.put("FromCity", NewCity);
                             params.put("ToCity", ToCity);
                             params.put("PerDiemAmount",PerDiemAmount.toString());
                             params.put("TravelCost", TravelCostByEmployee.toString());
                             params.put("OtherCost", otherCostByEmployee.toString());
                             params.put("EventCost", EventCost.toString());
                             params.put("TrainingCost", TrainingCostByEmployee.toString());
                             params.put("VISACost", VisaCost.toString());
                             params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                             params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                             params.put("Segment1","01");
                             params.put("Segment2",division);
                             params.put("Segment3",lob);
                             params.put("Segment4",costCenter);
                             params.put("Segment5",accountNumber);
                             params.put("Segment6","00"); 
                             
                                 try {
                                 fusionFileLoader = new FusionDataLoader();
                                 System.err.println("Training  Fusion Data Loader Params  is::"+params);
                                 //EES - code added by Moshina
                                 Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 17);
                                 value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Training Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                 System.out.println("TAT HDR Updated Value - Approved Training Earnings: "+value1);
                                 System.err.println("Training  Fusion Data Loaded Successfully");
                                 //fusionFileLoader.sendFusionRequest(params, 17);
                                 System.err.println("Training  Fusion Data Loaded Successfully");
                                 
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                 }

                             }
                             else {

                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;
                            }
                                //EES - AccountNumber
                                OperationBinding oper1 = ADFUtils.findOperation("getElementAccount");
                                Map paramMap1 = oper1.getParamsMap();
                                paramMap1.put("elementName", "Travel Ticket Earnings");
                                Row elementAccountNo1 = (Row)oper1.execute();
                                String accountNumber1 = elementAccountNo1.getAttribute("AccountNumber")!=null?elementAccountNo1.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber1);
                                try {
                                datFileData =
                                            report.getBusinessTripWithdrawnList("Travel Ticket Earnings",personNumber);
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                HashMap<String, String> params1 = new HashMap<String, String>();
                                if (datFileData.size() > 0) {
                                    if(secondLineAmount.intValue() > 0){
                               params1.put("Segment1","01");
                                params1.put("Segment2",division);
                                params1.put("Segment3",lob);
                                params1.put("Segment4",costCenter);
                                params1.put("Segment5",accountNumber1);
                                params1.put("Segment6","00");
                                params1.put("FromCity", NewCity);
                                params1.put("ToCity", ToCity);
                                params1.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                params1.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                params1.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                params1.put("Amount",secondLineAmount.toString());
                                params1.put("StartDate", formattedDate);
                                params1.put("AssignmentNumber",
                                    datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                params1.put("Count",
                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                    try {
                                        fusionFileLoader = new FusionDataLoader();
                                        System.err.println("Training Travel Ticket Fusion Data Loader Params is::"+params1);
                                        //EES code added by Moshina
                                        System.err.println("Training Travel Ticket Fusion Data Loader Params is::"+params1);
                                        Map<String, String> map = fusionFileLoader.sendFusionRequest(params1, 24);
                                        value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Travel Ticket Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                        System.out.println("TAT HDR Updated Value - Approved Ticket Earnings: "+value1);
                                        //fusionFileLoader.sendFusionRequest(params1, 24);
                                        System.err.println("Training Travel Ticket Fusion Data Loaded Successfully");
                                        
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                    }
                                    }

                                }
                                else {

                                JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                    return null;
                                }
                           
                           
                                returnValue="success";
                                invoiceNumber="";
                            }//2023 Element Entry Enhancement
                            if ((tripType.equals("Local")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                            
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                            System.out.println("myDateString  >>> " + formattedDate);
                              //EES - AccountNumber
                              OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                              Map paramMap = oper.getParamsMap();
                              paramMap.put("elementName", "Business Trip Earnings");
                              Row elementAccountNo = (Row)oper.execute();
                              String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                              System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Business Trip Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                              
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 if(firstLineAmount.intValue() > 0){
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",firstLineAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "Local");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity", NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount", PerDiemAmount.toString());
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00");  
                                
                                 try {
                                     fusionFileLoader = new FusionDataLoader();
                                     System.err.println("Local BT Fusion Data Loader Params is::"+params);
                                     //EES code changes done by Moshina
                                     Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 18);
                                     value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Business Trip Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                     System.out.println("TAT HDR Updated Value - Approved Business Trip Earnings: "+value1);
                                     //fusionFileLoader.sendFusionRequest(params, 18);   
                                     System.err.println("Local BT Fusion Data Loaded Successfully");   
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                 }
                             }
                             else {

                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;

                            }
                              //EES - AccountNumber
                              OperationBinding oper1 = ADFUtils.findOperation("getElementAccount");
                              Map paramMap1 = oper1.getParamsMap();
                              paramMap1.put("elementName", "Travel Ticket Earnings");
                              Row elementAccountNo1 = (Row)oper1.execute();
                              String accountNumber1 = elementAccountNo1.getAttribute("AccountNumber")!=null?elementAccountNo1.getAttribute("AccountNumber").toString():"";
                              System.err.println("EES: AccountNumber is::"+accountNumber1);
                              try {
                              datFileData =
                                          report.getBusinessTripWithdrawnList("Travel Ticket Earnings",personNumber);
                               } catch (Exception e) {
                               e.printStackTrace();
                               }
                              HashMap<String, String> params1 = new HashMap<String, String>();
                              if (datFileData.size() > 0) {
                                  if(secondLineAmount.intValue() > 0){
                                  params1.put("Segment1","01");
                                  params1.put("Segment2",division);
                                  params1.put("Segment3",lob);
                                  params1.put("Segment4",costCenter);
                                  params1.put("Segment5",accountNumber1);
                                  params1.put("Segment6","00");
                                  params1.put("FromCity", NewCity);
                                  params1.put("ToCity", ToCity);
                                  params1.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                  params1.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                  params1.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                  params1.put("Amount",secondLineAmount.toString());
                                  params1.put("StartDate", formattedDate);
                                  params1.put("AssignmentNumber",
                                      datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                  params1.put("Count",
                                      datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                  try {
                                      fusionFileLoader = new FusionDataLoader();
                                      System.err.println("Loacl BT Travel Ticket Fusion Data Loader Params is::"+params1);
                                          //EES code added by Moshina
                                      System.err.println("Training Travel Ticket Fusion Data Loader Params is::"+params1);
                                      Map<String, String> map = fusionFileLoader.sendFusionRequest(params1, 24);
                                      value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Travel Ticket Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                      System.out.println("TAT HDR Updated Value - Approved Ticket Earnings: "+value1);
                                  
                                    //fusionFileLoader.sendFusionRequest(params1, 24);
                                      System.err.println("Loacl BT Travel Ticket Fusion Data Loaded Successfully");
                                  } catch (Exception e) {
                                  e.printStackTrace();
                                  }
                                  }
                              }
                              else {

                              JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                  return null;

                              }
                                returnValue="success";
                                invoiceNumber="";
                          }//2023 Element Entry Enhancement
                            if ((tripType.equals("Inter")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                              
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                            System.out.println("myDateString  >>> " + formattedDate);
                                //EES - AccountNumber
                                OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                Map paramMap = oper.getParamsMap();
                                paramMap.put("elementName", "Business Trip Earnings");
                                Row elementAccountNo = (Row)oper.execute();
                                String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                           BiReportAccess report = new BiReportAccess();
                           List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                                datFileData =
                                        report.getBusinessTripWithdrawnList("Business Trip Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 if(firstLineAmount.intValue() > 0){
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",firstLineAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "International");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity", NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount",PerDiemAmount.toString() );
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00");                                   
                                 try {
                                     fusionFileLoader = new FusionDataLoader();
                                     System.err.println("Inter BT Fusion Data Loader Params is::"+params);
                                     //EES code changes done by Moshina
                                     Map<String, String> map = fusionFileLoader.sendFusionRequest(params, 18);
                                     value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Business Trip Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                     System.out.println("TAT HDR Updated Value - Approved Business Trip Earnings: "+value1);
                                     System.err.println("Local BT Fusion Data Loaded Successfully");
                                     //fusionFileLoader.sendFusionRequest(params, 18);    
                                     System.err.println("Inter BT Fusion Data Loaded Successfully");
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                 }
                                 
                             }
                             else {
                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;
                            }
                                //EES - AccountNumber
                                OperationBinding oper1 = ADFUtils.findOperation("getElementAccount");
                                Map paramMap1 = oper1.getParamsMap();
                                paramMap1.put("elementName", "Travel Ticket Earnings");
                                Row elementAccountNo1 = (Row)oper1.execute();
                                String accountNumber1 = elementAccountNo1.getAttribute("AccountNumber")!=null?elementAccountNo1.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber1);
                                try {
                                datFileData =
                                            report.getBusinessTripWithdrawnList("Travel Ticket Earnings",personNumber);
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                HashMap<String, String> params1 = new HashMap<String, String>();
                                if (datFileData.size() > 0) {
                                    if(secondLineAmount.intValue() > 0){
                                    params1.put("Segment1","01");
                                    params1.put("Segment2",division);
                                    params1.put("Segment3",lob);
                                    params1.put("Segment4",costCenter);
                                    params1.put("Segment5",accountNumber1);
                                    params1.put("Segment6","00");
                                    params1.put("FromCity", NewCity);
                                    params1.put("ToCity", ToCity);
                                    params1.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                    params1.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                    params1.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                    params1.put("Amount",secondLineAmount.toString());
                                    params1.put("StartDate", formattedDate);
                                    params1.put("AssignmentNumber",
                                        datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                    params1.put("Count",
                                        datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                    try {
                                        fusionFileLoader = new FusionDataLoader();
                                        System.err.println("Inter BT Travel Ticket Fusion Data Loader Params is::"+params1);
                                        //EES code added by Moshina
                                        System.err.println("Training Travel Ticket Fusion Data Loader Params is::"+params1);
                                        Map<String, String> map = fusionFileLoader.sendFusionRequest(params1, 24);
                                        value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Travel Ticket Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                        System.out.println("TAT HDR Updated Value - Approved Ticket Earnings: "+value1);
                                        //fusionFileLoader.sendFusionRequest(params1, 24);
                                        System.err.println("Inter BT Travel Ticket Fusion Data Loaded Sucessfully");
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                    }
                                    }
                                    
                                }
                                else {
                                JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                    return null;
                                }
                                returnValue="success";
                                invoiceNumber="";
                            }//2023 Element Entry Enhancement
                           if ((tripType.equals("Event")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                            
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                              System.out.println("myDateString  >>> " + formattedDate);
                                //EES - AccountNumber
                                OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                Map paramMap = oper.getParamsMap();
                                paramMap.put("elementName", "Event Cost Earnings");
                                Row elementAccountNo = (Row)oper.execute();
                                String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Event Cost Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 if(firstLineAmount.intValue() > 0){
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",firstLineAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "Event");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity",NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount", PerDiemAmount.toString());
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00");                                
                                 try {
                                     fusionFileLoader = new FusionDataLoader();
                                     System.err.println("Event Fusion Data Loader params  is::"+params);
                                     //EES - code added by Moshina
                                     Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 19);
                                     value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Event Cost Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                     System.out.println("TAT HDR Updated Value - Approved Event Cost Earnings: "+value1);
                                     //fusionFileLoader.sendFusionRequest(params, 19);   
                                     System.err.println("Event Fusion Data Loaded  Successfully");
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                 }
                             }
                             else {
                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;
                            }
                                //EES - AccountNumber
                                OperationBinding oper1 = ADFUtils.findOperation("getElementAccount");
                                Map paramMap1 = oper1.getParamsMap();
                                paramMap1.put("elementName", "Travel Ticket Earnings");
                                Row elementAccountNo1 = (Row)oper1.execute();
                                String accountNumber1 = elementAccountNo1.getAttribute("AccountNumber")!=null?elementAccountNo1.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber1);
                                try {
                                datFileData =
                                            report.getBusinessTripWithdrawnList("Travel Ticket Earnings",personNumber);
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                                HashMap<String, String> params1 = new HashMap<String, String>();
                                if (datFileData.size() > 0) {
                                    if(secondLineAmount.intValue() > 0){
                                    params1.put("Segment1","01");
                                    params1.put("Segment2",division);
                                    params1.put("Segment3",lob);
                                    params1.put("Segment4",costCenter);
                                    params1.put("Segment5",accountNumber1);
                                    params1.put("Segment6","00");
                                    params1.put("FromCity", NewCity);
                                    params1.put("ToCity", ToCity);
                                    params1.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                    params1.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                    params1.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                    params1.put("Amount",secondLineAmount.toString());
                                    params1.put("StartDate", formattedDate);
                                    params1.put("AssignmentNumber",
                                        datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                    params1.put("Count",
                                        datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                    try {
                                        fusionFileLoader = new FusionDataLoader();
                                        System.err.println("Event Travel Ticket Fusion Data Loader params is::"+params1);
                                        
                                        //EES code added by Moshina
                                        System.err.println("Training Travel Ticket Fusion Data Loader Params is::"+params1);
                                        Map<String, String> map = fusionFileLoader.sendFusionRequest(params1, 24);
                                        value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Travel Ticket Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                        System.out.println("TAT HDR Updated Value - Approved Ticket Earnings: "+value1);
                                        //fusionFileLoader.sendFusionRequest(params1, 24);
                                        System.err.println("Event Travel Ticket Fusion Data Loaded Sucessfully");
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                    }
                                    }
                                }
                                else {
                                JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                    return null;
                                }
                                
                                returnValue="success";
                                invoiceNumber="";
                            }//2023 Element Entry Enhancement
                                             
                        }else if(totalAmount.intValue()==0){ /*Added this block for approving the request with 0 Total Cost ----22/11/2022*/
                            
                            returnValue="success";
                        } /*Added this block for approving the request with 0 Total Cost ----22/11/2022*/


                    }

                    else if (wayOfTravel.equalsIgnoreCase("Car (Company)") ||
                             wayOfTravel.equalsIgnoreCase("Train (Company)") ||
                             wayOfTravel.equalsIgnoreCase("Plane (Company)")) {

                        Number totalAmount = null;
                        Number otherCostAmount = null;
                        Number perdiemAmount = null;
                        Number otherCostByEmployee = null;
                        Number TrainingCostByEmployee = null;
                        Number TravelCostByEmployee = null;
                        Number VisaCost = null;
                        Number EventCost = null;
                        Number ExpenseBusinessTripId = null;
                        String ToCity=null;
                        String NewCity=null;
                        Number PerDiemAmount = null;
                        if (ADFUtils.getBoundAttributeValue("VisaCost") !=
                            null) {
                            VisaCost =
                                    (Number)ADFUtils.getBoundAttributeValue("VisaCost");
                        } else {
                            VisaCost = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("EventCost") !=
                            null) {
                            EventCost =
                                    (Number)ADFUtils.getBoundAttributeValue("EventCost");
                        } else {
                            EventCost = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("TravelCostByEmployee") !=
                            null) {
                            TravelCostByEmployee =
                                    (Number)ADFUtils.getBoundAttributeValue("TravelCostByEmployee");
                        } else {
                            TravelCostByEmployee = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("ExpenseBusinessTripId") !=
                            null) {
                            ExpenseBusinessTripId =
                                    (Number)ADFUtils.getBoundAttributeValue("ExpenseBusinessTripId");
                        } else {
                            ExpenseBusinessTripId = new Number(0);
                        }
                        if (ADFUtils.getBoundAttributeValue("ToCity") !=
                            null) {
                            ToCity = (String)ADFUtils.getBoundAttributeValue("ToCity");
                        } else {
                            ToCity = "";
                        }
                        if (ADFUtils.getBoundAttributeValue("NewCity") !=
                            null) {
                            NewCity = (String)ADFUtils.getBoundAttributeValue("NewCity");
                        } else {
                            NewCity = "";
                        }
                        if (ADFUtils.getBoundAttributeValue("PerDiemAmount") !=
                            null) {
                            PerDiemAmount =
                                    (Number)ADFUtils.getBoundAttributeValue("PerDiemAmount");
                        } else {
                            PerDiemAmount = new Number(0);
                        }      

                        if (benefitType.equalsIgnoreCase("A")) {
                            if (ADFUtils.getBoundAttributeValue("OtherByEmployee") !=
                                null) {
                                otherCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherByEmployee");
                            } else {
                                otherCostAmount = new Number(0);

                            }
                            totalAmount = otherCostAmount;
                        } else if (benefitType.equalsIgnoreCase("P")) {
                            if (ADFUtils.getBoundAttributeValue("OtherCost") !=
                                null) {
                                otherCostAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
                            } else {
                                otherCostAmount = new Number(0);

                            }
                            if (ADFUtils.getBoundAttributeValue("PerDiemAmount") !=
                                null) {
                                perdiemAmount =
                                        (Number)ADFUtils.getBoundAttributeValue("PerDiemAmount");
                            } else {
                                perdiemAmount = new Number(0);
                            }
                            if (ADFUtils.getBoundAttributeValue("OtherByEmployee") !=
                                null) {
                                otherCostByEmployee =
                                        (Number)ADFUtils.getBoundAttributeValue("OtherByEmployee");
                            } else {
                                otherCostByEmployee = new Number(0);
                            }
                            if (ADFUtils.getBoundAttributeValue("TrainingByEmployee") !=
                                null) {
                                TrainingCostByEmployee =
                                        (Number)ADFUtils.getBoundAttributeValue("TrainingByEmployee");
                            } else {
                                TrainingCostByEmployee = new Number(0);
                            }
                           
                   // 2022        totalAmount = otherCostAmount.add(perdiemAmount);
                           
                                totalAmount =
                                        otherCostAmount.add(perdiemAmount).add(otherCostByEmployee).add(TrainingCostByEmployee);
                            
                        }

                        String description = personNumber + " - " + personName;
                        if (totalAmount.intValue() > 0) {
                           if(!userEmployeeType.equals("Employee")){
//                            Map param = new HashMap();
//                            param.put("vendorName",
//                                      "Business Trip"); //select vendor_name  from poz_suppliers_v
//                            param.put("vendorId",
//                                      "300000004500418"); //select vendor_id from poz_suppliers_v
//                            param.put("vendorSiteCode", "Riyadh");
//                            param.put("ledgerId",
//                                      "300000001778002"); //300000001778002 //select * from XCC_BCE_LEDGERS
//                            param.put("orgId",
//                                      "300000001642073"); //300000001642073
//                            param.put("invoiceNumber", invoiceNumber);
//                            param.put("invoiceAmount", totalAmount);
//                            param.put("paymentCurrencyCode", "SAR");
//                            param.put("invoiceTypeLookupCode", "STANDARD");
//                            param.put("termsName", "Immediate");
//                            param.put("paymentMethod", "WIRE");
//                            param.put("Description", description);
//                            param.put("CreationDate", creationDate);
//                            Map line1 = new HashMap();
//                            line1.put("lineNumber", "1");
//                            line1.put("lineType", "ITEM");
//                            line1.put("lineAmount", totalAmount); //var
//                            line1.put("lineCurrencyCode", "SAR");
//                            line1.put("codeCombinationId",
//                                      longCodeCombinationIdLine1);
//                            line1.put("personNumber", personNumber);
//                            line1.put("bankName", "");
//                            line1.put("IBAN", "");
//                            line1.put("SwiftCode", "");
//                            line1.put("PaymentType", "None");
//
//                            List<Map> lineList = new ArrayList<Map>();
//                            lineList.add(line1);
//
//                            List<Map> attacheList = new ArrayList<Map>();
//
//                            ViewObject attachmentVO =
//                                ADFUtils.findIterator("RequestAttachmentsView1Iterator").getViewObject();
//                            if (attachmentVO.getEstimatedRowCount() > 0) {
//                                Row[] rows = attachmentVO.getAllRowsInRange();
//                                Row row;
//                                Map attachmentMap = null;
//                                for (int i = 0; i < rows.length; i++) {
//                                    attachmentMap = new HashMap();
//                                    row = rows[i];
//                                    attachmentMap.put("attachmentType",
//                                                      "FILE");
//                                    attachmentMap.put("category",
//                                                      "To Payables");
//                                    attachmentMap.put("fileTitle",
//                                                      row.getAttribute("FileName"));
//                                    BlobDomain blob =
//                                        (BlobDomain)row.getAttribute("AttachmentFile");
//                                    InputStream inputStream =
//                                        blob.getInputStream();
//                                    byte[] bytes = null;
//                                    try {
//                                        bytes =
//                                                IOUtils.toByteArray(inputStream);
//                                    } catch (IOException e) {
//                                    }
//                                    attachmentMap.put("fileContent",
//                                                      DatatypeConverter.printBase64Binary(bytes));
//                                    attacheList.add(attachmentMap);
//                                }
//                            } 
//                                Map attachmentMap = new HashMap();
//                                attachmentMap.put("attachmentType","FILE");
//                                attachmentMap.put("category","To Payables");
//                                attachmentMap.put("fileTitle", "BusinessTripAttachment.pdf"); 
//                                attachmentMap.put("fileContent",DatatypeConverter.printBase64Binary(this.getBusinessTripAttach()));
//                              
//                               
//                                attacheList.add(attachmentMap);
//                            System.err.println("Param >>> " +
//                                               param);
//                            System.err.println("lineList >>> " +
//                                               lineList);
//                            System.err.println("attacheList >>> " +
//                                               attacheList);
//
//                           
//                            APConsumer newAPInvoice = new APConsumer();
//                            returnValue =
//                                    newAPInvoice.createInvoice(param, lineList,
//                                                               attacheList);
                              // String attachment=DatatypeConverter.printBase64Binary(this.getBusinessTripAttach());
                              System.err.println("Inside Invoice Mail");
                               byte[] bytes = null;
                              // bytes=this.getBusinessTripAttach();
                               BlobDomain blob =new BlobDomain(bytes);
                               OperationBinding sendMail =
                               ADFUtils.findOperation("callSendInvoiceEmailStoredPL");
                               sendMail.getParamsMap().put("p_request_type", tripType);
                               sendMail.getParamsMap().put("p_request_number", requestId);
                               sendMail.getParamsMap().put("p_attachment", blob);
                               sendMail.execute();
                               returnValue = "success";
                               invoiceNumber="";
                           }
                           
                            if ((tripType.equals("Training")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                           
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                            System.out.println("myDateString  >>> " + formattedDate);
                            //EES - AccountNumber
                            OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                            Map paramMap = oper.getParamsMap();
                            paramMap.put("elementName", "Training Earnings");
                            Row elementAccountNo = (Row)oper.execute();
                            String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                            System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Training Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",totalAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "Training");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity", NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount",PerDiemAmount.toString() );
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00");  
                                 try {
                                 fusionFileLoader = new FusionDataLoader();
                                 System.err.println("Training Fusion Data Loader Params is::"+params);
                                 //EES code added by Moshina
                                 Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 17);
                                 value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Training Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                 System.out.println("TAT HDR Updated Value - Approved Training Earnings: "+value1);
                                 System.err.println("Training Fusion Data Loaded Sucessfully");
                                 //fusionFileLoader.sendFusionRequest(params, 17);
                                 //System.err.println("Training Fusion Data Loaded Sucessfully");
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                             }

                             else {

                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;

                            }
                           
                                returnValue="success";
                                invoiceNumber="";
                            }//2023 Element Entry Enhancement
                            if ((tripType.equals("Local")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                            
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                           System.out.println("myDateString  >>> " + formattedDate);
                               //EES - AccountNumber
                               OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                               Map paramMap = oper.getParamsMap();
                               paramMap.put("elementName", "Business Trip Earnings");
                               Row elementAccountNo = (Row)oper.execute();
                               String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                               System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Business Trip Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",totalAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "Local");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity", NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount", PerDiemAmount.toString());
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00"); 
                                 try {
                                     fusionFileLoader = new FusionDataLoader();
                                     System.err.println("Local BT Fusion Data Loader Params is::"+params);
                                     //EES code changes done by Moshina
                                     Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 18);
                                     value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Business Trip Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                     System.out.println("TAT HDR Updated Value - Approved Business Trip Earnings: "+value1);
                                     //fusionFileLoader.sendFusionRequest(params, 18);
                                     System.err.println("Local BT Fusion Data Loaded Sucessfully");
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                             }

                             else {

                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;

                            }
                           
                                returnValue="success";
                                invoiceNumber="";
                           }//2023 Element Entry Enhancement
                            if ((tripType.equals("Inter")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                            
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------

//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                            System.out.println("myDateString  >>> " + formattedDate);
                                //EES - AccountNumber
                                OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                Map paramMap = oper.getParamsMap();
                                paramMap.put("elementName", "Business Trip Earnings");
                                Row elementAccountNo = (Row)oper.execute();
                                String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Business Trip Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",totalAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "International");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity", NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount", PerDiemAmount.toString());
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00"); 
                                 try {
                                     fusionFileLoader = new FusionDataLoader();
                                     System.err.println("Inter BT Fusion Data Loader Params is::"+params);
                                     //EES code changes done by Moshina
                                     Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 18);
                                     value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Business Trip Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                     System.out.println("TAT HDR Updated Value - Approved Business Trip Earnings: "+value1);
                                     //fusionFileLoader.sendFusionRequest(params, 18);
                                     System.err.println("Inter BT Fusion Data Loaded Successfully");
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                             }

                             else {

                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;

                            }
                            
                                returnValue="success";
                                invoiceNumber="";
                            }//2023 Element Entry Enhancement
                            if ((tripType.equals("Event")) && (userEmployeeType.equals("Employee"))) {//2023 Element Entry Enhancement
                            String dateString =creationDate;
                          
                            String formattedDate = dateString.replace('-', '/');
                            //-----------------------------------------
//
//                            SimpleDateFormat oldDateFormat =
//                                            new SimpleDateFormat("dd/MM/yyyy");
//                            Date myDate = null;
//                            try {
//                            myDate = oldDateFormat.parse(formattedDate);
//                            } catch (ParseException e) {
//                            e.printStackTrace();
//                            }
//                            oldDateFormat.applyPattern("yyyy/MM/dd");
//                            formattedDate = oldDateFormat.format(myDate);
                            System.out.println("myDateString  >>> " + formattedDate);
                                //EES - AccountNumber
                                OperationBinding oper = ADFUtils.findOperation("getElementAccount");
                                Map paramMap = oper.getParamsMap();
                                paramMap.put("elementName", "Event Cost Earnings");
                                Row elementAccountNo = (Row)oper.execute();
                                String accountNumber = elementAccountNo.getAttribute("AccountNumber")!=null?elementAccountNo.getAttribute("AccountNumber").toString():"";
                                System.err.println("EES: AccountNumber is::"+accountNumber);
                            //---------------------------------------------
                            BiReportAccess report = new BiReportAccess();
                            List<Map> datFileData = null;
                            //----------------------------------------


                            //            upload element on oracle fusion HCM
                            try {
                            datFileData =
                                        report.getBusinessTripWithdrawnList("Event Cost Earnings",personNumber);
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             HashMap<String, String> params = new HashMap<String, String>();
                             //            params.put("Date", creationDate.toString());
                             if (datFileData.size() > 0) {
                                 params.put("AssignmentNumber",
                                 datFileData.get(0).get("ASSIGNMENT_NUMBER").toString());
                                 params.put("Amount",totalAmount.toString());
                                 params.put("StartDate", formattedDate);
                                 params.put("Count",
                                                    datFileData.get(0).get("MULTIPLEENTRYCOUNT").toString());
                                 params.put("TripType", "Event");
                                 params.put("Start_Date", ADFUtils.getBoundAttributeValue("StartDate").toString());
                                 params.put("EndDate", ADFUtils.getBoundAttributeValue("EndDate").toString());
                                 params.put("NumberofDays", ADFUtils.getBoundAttributeValue("Days").toString());
                                 params.put("BusinessTripID", ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString());
                                 params.put("WayofTravel", ADFUtils.getBoundAttributeValue("MeansTravel").toString());
                                 params.put("FromCity", NewCity);
                                 params.put("ToCity", ToCity);
                                 params.put("PerDiemAmount",PerDiemAmount.toString());
                                 params.put("TravelCost", TravelCostByEmployee.toString());
                                 params.put("OtherCost", otherCostByEmployee.toString());
                                 params.put("EventCost", EventCost.toString());
                                 params.put("TrainingCost", TrainingCostByEmployee.toString());
                                 params.put("VISACost", VisaCost.toString());
                                 params.put("TotalforInvoice", ADFUtils.getBoundAttributeValue("TotalAmount").toString());
                                 params.put("ExpenseBusinessTripID", ExpenseBusinessTripId.toString());
                                 params.put("Segment1","01");
                                 params.put("Segment2",division);
                                 params.put("Segment3",lob);
                                 params.put("Segment4",costCenter);
                                 params.put("Segment5",accountNumber);
                                 params.put("Segment6","00"); 
                                 try {
                                     fusionFileLoader = new FusionDataLoader();
                                     System.err.println("Event Fusion Data Loader Params is::"+params);
                                     //EES - code added by Moshina
                                     Map<String, String> map =  fusionFileLoader.sendFusionRequest(params, 19);
                                     value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Approved", "Event Cost Earnings", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                                     System.out.println("TAT HDR Updated Value - Approved Event Cost Earnings: "+value1);
                                     //fusionFileLoader.sendFusionRequest(params, 19);
                                     System.err.println("Event Fusion Data Loaded Sucessfully");
                                 } catch (Exception e) {
                                 e.printStackTrace();
                                 }
                             }

                             else {

                             JSFUtils.addFacesErrorMessage("You can't create  element as the Assignment number or Multiple Entry Count is null");
                                return null;

                            }
                           
                                returnValue="success";
                                invoiceNumber="";
                            }//2023 Element Entry Enhancement
                          
                        }else if(totalAmount.intValue()==0){ /*Added this block for approving the request with 0 Total Cost ----22/11/2022*/
                            
                            returnValue="success";
                        }  /*Added this block for approving the request with 0 Total Cost ----22/11/2022*/


                    }

                    ADFUtils.setBoundAttributeValue("RequestStatus",
                                                    "APPROVED");
                    ADFUtils.setBoundAttributeValue("Assignee", personNumber);
                    ADFUtils.setBoundAttributeValue("AssigneeName", "");
                    ADFUtils.setBoundAttributeValue("StepId",
                                                    nextStep.getAttribute("NextStepId"));
                    ADFUtils.setBoundAttributeValue("InvoiceNumber", invoiceNumber);
                  

                    if (returnValue != null && returnValue.equalsIgnoreCase("success")) {
                        ADFUtils.findOperation("Commit").execute();
                        String returnvalue =
                            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                                  ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                                  (Number)nextStep.getAttribute("StepId"),
                                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                                  "APPROVE_ACT",
                                                                  "Y");
                        updateOrInsert = 1;
                        System.err.println("returnvalue========" +
                                           returnvalue);
                        if (returnvalue.equalsIgnoreCase("SUCCESS")) {
                            ADFUtils.findOperation("Commit").execute();
                        } else {
                            ADFUtils.findOperation("Rollback").execute();
                        }

                        if (emailNotification != null &&
                            emailNotification.equalsIgnoreCase("Y")) {
                            //Testing
                            //                            sendEmailToAccountant("Amr.Yahia@tatweer.sa,saud.muraibadh@tatweer.sa,Mansour.Badran@tatweer.sa",
                            //                                                  tripRow);
                              sendEmailToPerson(personNumber, tripRow);
                            /*Commented and added code by Moshina
                             * Mail SMTP issue
                             * sendEmailByEmail(fyiEmailAddress, tripRow)
                             * group of mail addresses splited into single email address
                             */
                            //sendEmailByEmail(fyiEmailAddress, tripRow);
                            if(fyiEmailAddress != null || fyiEmailAddress != ""){
                                String[] arrOfEmail = fyiEmailAddress.split(",");
                                for (String mail : arrOfEmail) {
                                    System.out.println("single email address"+mail);
                                    sendEmailByEmail(mail, tripRow);
                                }
                            }
//                              sendFyiMailForInformationExpensed( fyiEmailAddress, tripRow);
                        }
                        JSFUtils.addFacesInformationMessage("Request has been approved");
                        return "back";
                    } else {
                        ADFUtils.findOperation("Rollback").execute();
                        JSFUtils.addFacesInformationMessage("Something went wrong! please contact HR Administrator!");
                        return null;
                    }

                }

                /*
                ADFUtils.setBoundAttributeValue("Assignee", personNumber);
                ADFUtils.setBoundAttributeValue("RequestStatus", "APPROVED");
                ADFUtils.setBoundAttributeValue("StepId",
                                                nextStep.getAttribute("NextStepId"));

                //Set Expense flag
                //            String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");

                ADFUtils.findOperation("Commit").execute();
                String returnvalue =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                          (Number)nextStep.getAttribute("StepId"),
                                                          (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                          (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                          "APPROVE_ACT", "Y");
                updateOrInsert = 1;
                System.err.println("returnvalue========" + returnvalue);
                if (returnvalue.equalsIgnoreCase("SUCCESS")) {
                    ADFUtils.findOperation("Commit").execute();
                } else {
                    ADFUtils.findOperation("Rollback").execute();
                }
                JSFUtils.addFacesInformationMessage("Request has been approved");

                //Send Email to employee
                if (emailNotification != null &&
                    emailNotification.equalsIgnoreCase("Y")) {
                    //sendEmailWithInvoiceAmount(personNumber, tripRow);
                   		sendFyiMailForInformationWithoutExpensed(fyiEmailAddress, tripRow);
                    //                    sendEmailToPayrollMgrFinalApproval(tripRow);
                }


                return "back";
                */
            } else {
                ADFUtils.findOperation("Rollback").execute();
                JSFUtils.addFacesInformationMessage("you cannot approve the request since there is no distribution combination for the cost center selected!");
                return null;
            }
        }
        //For dynamic approval- If next assignee not from above, based on step type it will assign.
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        if(approvalDone != 1){  
             // If step type is POSITION
                if("POSITION".equals(stepType)){
                    System.err.println("Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
                    BiReportAccess report = new BiReportAccess();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
                        System.err.println("Inside ===>>>Position Manager Name" + assigneeName);
                    }
                    if (personData.get(0).get("PERSON_NUMBER") != null) {
                        assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
                        System.err.println("Inside ===>>>Position Manager Number" + assigneeNo);
                    }
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
                    }
                }
            // If step type is DEPARTMENT MANAGER
                if("DEPT_MANAGER".equals(stepType)){
                        System.err.println("Approved, Assigning to : : DEPT_MANAGER :"+ department);
                        BiReportAccess BIRA = new BiReportAccess();
                        try {
                            List<Map> managerOfDeptList =
                            BIRA.getManagerOfDepartmentData(department);
                            if (managerOfDeptList.size() > 0) {
                                if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                    assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                                    System.err.println("Inside ===>>>Department Manager Number" + assigneeNo);
                                }
                                if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                    assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                                    System.err.println("Inside ===>>>Department Manager Name" + assigneeName);
                                }
                                if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                    assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                                }
                            }
                            else
                            {
                                JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                                return null;
                            }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                   }
            // If step type is LINE MANAGER
            if("LINE_MANAGER".equals(stepType)){
                    System.err.println("Approved, Assigning to : : LINE_MANAGER ");           
                    assigneeName = aXBElement.getValue();
                    assigneeNo = managerNumber;
                    assigneeEmail = "LINE";
            } 
            // If step type is USER
            if("USER".equals(stepType)){
                System.err.println("Approved, Assigning to : : USER ");
                ArrayList<String> personDetails = new ArrayList<String>();
                personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
                assigneeName = personDetails.get(0).toString();
                assigneeEmail = personDetails.get(1).toString();
                assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
            } 
            
            //Will be called for all above Step Type
            ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId"));
            updateOrInsert = 1;
            String returnvalue =
                        ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                              ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                              (Number)nextStep.getAttribute("StepId"),
                                                              (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                              (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                              "APPROVE_ACT", "N"); 
            if (returnvalue.equalsIgnoreCase("SUCCESS")) {
                ADFUtils.findOperation("Commit").execute();
            } else {
                ADFUtils.findOperation("Rollback").execute();
            } 
            if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                if("LINE".equals(assigneeEmail)){
                    sendEmail(assigneeNo, tripRow);
                }else{
                    if (assigneeEmail != null) {
                        sendEmailByEmail(assigneeEmail, tripRow);
                    }else{
                        JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
                     }
                } 
            }
        } 
        ADFUtils.setBoundAttributeValue("Assignee",
                                        nextStep.getAttribute("NextAssignee"));
        
        if("LINE_MANAGER".equals(stepType) || "DEPT_MANAGER".equals(stepType)){
            ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
        }
        //         JSFUtils.resolveExpression("#{PersonInfo.nextAssignee}"));
        ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
        System.err.println("Called====.....>>>>>");
        ADFUtils.findOperation("Commit").execute();
        if (updateOrInsert == 0) {
            if (nextStep.getAttribute("NextAssignee").equals("Finished")) {
                finalapproval = "Y";
            } else {
                finalapproval = "N";
            }
            System.err.println("finalapproval=======" + finalapproval);
            System.err.println("Next Director===>1");
            String returnvalue =
                ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                      ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                      (Number)nextStep.getAttribute("StepId"),
                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                      "APPROVE_ACT",
                                                      finalapproval);

            if (returnvalue != null &&
                returnvalue.equalsIgnoreCase("SUCCESS")) {
                ADFUtils.findOperation("Commit").execute();
            } else {
                ADFUtils.findOperation("Rollback").execute();
            }

        }
        JSFUtils.addFacesInformationMessage("Request has been approved");
        return "back";

    }

    public ArrayList<String> getCodeCombinationId(String emp_number,
                                                  String costCenterNumber,
                                                  String divi,
                                                  String lob_value) {


        CodeCombinationConsumer codeCombinationConsumer =
            new CodeCombinationConsumer();
        BiReportAccess BIRA = new BiReportAccess();
        String costCenter = costCenterNumber;
        String codeCombinationId1 = null;
        String codeCombinationId2 = null;
        String division = divi;
        String lob = lob_value;
        try {
            if (costCenterNumber == null || divi == null ||
                lob_value == null) {
                List<Map> hcmSegmentsList = BIRA.getHCMSegments(emp_number);
                if (hcmSegmentsList.size() > 0) {

                    Map currentHCMSegments = hcmSegmentsList.get(0);
                    if (costCenterNumber != null) {
                        costCenter = costCenterNumber;
                    } else {
                        if (currentHCMSegments.get("SEGMENT4") != null) {
                            costCenter =
                                    (String)currentHCMSegments.get("SEGMENT4");
                        } else {
                            JSFUtils.addFacesErrorMessage("This Employee has no costing values( Cost Center ) !!!!");
                            return null;

                        }
                    }
                    if (divi != null) {
                        division = divi;
                    } else {
                        if (currentHCMSegments.get("SEGMENT2") != null) {
                            division =
                                    (String)currentHCMSegments.get("SEGMENT2");
                        } else {
                            JSFUtils.addFacesErrorMessage("This Employee has no costing values( Division ) !!!!");
                            return null;

                        }
                    }
                    if (lob_value != null) {
                        lob = lob_value;
                    } else {
                        if (currentHCMSegments.get("SEGMENT3") != null) {
                            lob = (String)currentHCMSegments.get("SEGMENT3");
                        } else {
                            JSFUtils.addFacesErrorMessage("This Employee has no costing values( Lob ) !!!!");
                            return null;

                        }
                    }

                }

                else {

                    JSFUtils.addFacesErrorMessage("This Employee has no costing values( Division & lob & Cost Center ) !!!!");
                    return null;
                }

            } else {
                costCenter = costCenterNumber;
                division = divi;
                lob = lob_value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("cost center for invoice:" + costCenter);

        List<Map> combinationIdList1 = null;
        List<Map> combinationIdList2 = null;
        ArrayList<String> combinationIDs = new ArrayList<String>();

        try {
            combinationIdList1 =
                    BIRA.getCodeCombinationId("01", division, lob, costCenter,
                                              "5101030201", "00", "0000",
                                              "0000");

            //            BIRA.getCodeCombinationId("01","03","00","2010004","5102010003","00","0000","0000");

            combinationIdList2 =
                    BIRA.getCodeCombinationId("01", division, lob, costCenter,
                                              "5101030202", "00", "0000",
                                              "0000");

            //            BIRA.getCodeCombinationId("01","03","00","2010004","5102010003","00","0000","0000");
            System.out.println("combinationIdList1:" + combinationIdList1);
            System.out.println("combinationIdList2:" + combinationIdList2);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("inside get code combination exception");
        }
        //        System.err.println("CODE_COMBINATION_ID >>>> "+combinationIdList1.get(0).get("CODE_COMBINATION_ID"));
        if (combinationIdList1.get(0).get("CODE_COMBINATION_ID") != null) {
            codeCombinationId1 =
                    combinationIdList1.get(0).get("CODE_COMBINATION_ID").toString();
            System.err.println("CODE_COMBINATION_ID 1 >>>> " +
                               combinationIdList1.get(0).get("CODE_COMBINATION_ID"));
        } else {


            codeCombinationId1 =
                    codeCombinationConsumer.createCodeCombination("THC Ledger SA",
                                                                  "01",
                                                                  division,
                                                                  lob,
                                                                  costCenter,
                                                                  "5101030201",
                                                                  "00", "0000",
                                                                  "0000",
                                                                  "PaaS.Self Service@tatweer.sa",
                                                                  "PAASTatweer@2020",
                                                                  "https://egwo-dev1.fa.em2.oraclecloud.com/fscmService/AccountCombinationService");

            System.out.println("inside else codeCombinationId1:" +
                               codeCombinationId1);
            //            String codeComb1 =
            //                "01-" + division + "-" + lob + "-" + costCenter +
            //                "-5101030201" + "-00" + "-0000" + "-0000";
            //            JSFUtils.addFacesErrorMessage("You can't create AP Invoice as this code combination (" +
            //                                          codeComb1 +
            //                                          ") isn't created before");
            //            return null;
        }


        if (combinationIdList2.get(0).get("CODE_COMBINATION_ID") != null) {
            codeCombinationId2 =
                    combinationIdList2.get(0).get("CODE_COMBINATION_ID").toString();
            System.err.println("CODE_COMBINATION_ID 2 >>>> " +
                               combinationIdList2.get(0).get("CODE_COMBINATION_ID"));
        }


        else {

            System.out.println("inside else codeCombinationId2:" +
                               codeCombinationId2);
            codeCombinationId2 =
                    codeCombinationConsumer.createCodeCombination("THC Ledger SA",
                                                                  "01",
                                                                  division,
                                                                  lob,
                                                                  costCenter,
                                                                  "5101030202",
                                                                  "00", "0000",
                                                                  "0000",
                                                                  "PaaS.Self Service@tatweer.sa",
                                                                  "PAASTatweer@2020",
                                                                  "https://egwo-dev1.fa.em2.oraclecloud.com/fscmService/AccountCombinationService");


            //            String codeComb2 =
            //                "01-" + division + "-" + lob + "-" + costCenter +
            //                "-5101030202" + "-00" + "-0000" + "-0000";
            //            JSFUtils.addFacesErrorMessage("You can't create AP Invoice as this code combination (" +
            //                                          codeComb2 +
            //                                          ") isn't created before");
            //            return null;
        }

        combinationIDs.add(codeCombinationId1);
        combinationIDs.add(codeCombinationId2);
        return combinationIDs;

    }


    public String reject_action() {
        // Add event code here...
        //OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        //nextOpr.getParamsMap().put("currentStep", null);
        //Row nextStep = (Row)nextOpr.execute();
        //System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
        //ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
        String stepId =
            ADFUtils.getBoundAttributeValue("StepId").toString();
        String tripType =
            ADFUtils.getBoundAttributeValue("TripType").toString();
        String financeStep = "";
/*2023 Code Change */       
//        if("Local".equals(tripType) || "Inter".equals(tripType)){
//            financeStep = "5";
//        }else{
//            financeStep = "6";
//        }/*2023 Code Change */  
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String requestTripType =
            ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
                String TripTypeName=requestTripType +'-'+ personLocation;
                ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        int setupapproval = 0;
        //        String budgetFlag = ADFUtils.getBoundAttributeValue("BudgetFlag").toString();
//        if (stepId.equals(financeStep)) {/*2023 Code Change */  
//        System.err.println("REJECTED BY Financial Planning & Analysis Manager");
//            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
//                                                  (String)ADFUtils.getBoundAttributeValue("TripType"),
//                                                  (Number)nextStep.getAttribute("StepId"),
//                                                  new Long(0), null,
//                                                  "REJECT_ACT", "N");
//            ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
//            ADFUtils.setBoundAttributeValue("Assignee", "CEO");
//            ADFUtils.setBoundAttributeValue("AssigneeName", "Osama  Alheizan");
//            ADFUtils.findOperation("Commit").execute();
//            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
//                                                  (String)ADFUtils.getBoundAttributeValue("TripType"),
//                                                  (Number)nextStep.getAttribute("NextStepId"),
//                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
//                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
//                                                  "APPROVE_ACT", "N");
//            setupapproval = 1;
//            JSFUtils.addFacesInformationMessage("Request has been Rejected and sent to the CEO to take the decision");
//            return "back";
//        }/*2023 Code Change */  

      //  else {/*2023 Code Change */  

            String personNumber =
                (String)JSFUtils.resolveExpression("#{bindings.PersoneId.inputValue}");
        /*
         * code added by Moshina for CEO - setup hier on 07/07/2024
         * CEO - SETUP HIER 
         * */
            UserServiceUtil userService = new UserServiceUtil();
            UserDetails userDetails = null;
            userDetails = userService.getUserDetailsByPersonNumber(personNumber);
            List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
            Long lineManagerID = 0L;
            String managerNumber = null;
            JAXBElement<String> aXBElement = null;
            String position = relationshipDetails.get(0).getPositionName();
            System.out.println("CEO - SETUP HIER reject_action position: " + position);
            if (position.equalsIgnoreCase("THC Group CEO")) {
                nextOpr = ADFUtils.findOperation("getNextStepCEO");
                nextStep = (Row)nextOpr.execute();
                emailNotification = (String)nextStep.getAttribute("EmailNotification");
            }
            ADFUtils.setBoundAttributeValue("ActionTaken", "REJECTED");
            ADFUtils.setBoundAttributeValue("RequestStatus", "REJECTED");
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            
            //Get requstor ID by Number
            //        UserServiceUtil userService = new UserServiceUtil();
            //        UserDetails byPersonNumber =
            //            userService.getUserDetailsByPersonNumber(personNumber);
            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            //Send Email to employee
            Row tripRow =
                ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
            if (setupapproval == 0) {
                System.err.println("Called");
                String value =
                    ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                          (Number)nextStep.getAttribute("StepId"),
                                                          new Long(0), null,
                                                          "REJECT_ACT", "");
            }
            if (emailNotification != null &&
                emailNotification.equalsIgnoreCase("Y")) {
                sendRejectionEmail(personNumber, tripRow);
            }
       // }
        ADFUtils.findOperation("Commit").execute();

        JSFUtils.addFacesInformationMessage("Request has been Rejected");
        return "back";
    }

    public void loadPersonData() {
        PersonInfo personInfo =
            (PersonInfo)JSFUtils.resolveExpression("#{PersonInfo}");
        String personNumber =
            (String)JSFUtils.resolveExpression("#{sessionScope.personNumber}");
        UserServiceUtil userService = new UserServiceUtil();
        String jwt =
            (String)JSFUtils.resolveExpression("#{pageFlowScope.jwt}");
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
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Person number");
            return;
        }
        personInfo.setPerosnNumber(userDetails.getPersonNumber());


        if (userDetails.getPersonId() == null) {
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Person Id");
            return;
        }

        personInfo.setEmployeeId(userDetails.getPersonId());

        if (userDetails.getUsername() == null) {
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Name");
            return;
        } else {

            JAXBElement<String> userNameJAXB = userDetails.getUsername();
            personInfo.setUserName(userNameJAXB.getValue());

        }
        JSFUtils.setExpressionValue("#{sessionScope.personNumber}",
                                    userDetails.getPersonNumber());
        System.err.println("Ana Hena Hena >>>" +
                           userDetails.getPersonNumber());

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
                System.out.println("*****************");
                System.err.println("PERSON_NAME >>> " +
                                   currentEmpSalary.get("PERSON_NAME"));
                System.out.println("*****************");
                if (currentEmpSalary.get("SALARY_AMOUNT") != null) {
                    System.err.println("SALARY_AMOUNT >>> " +
                                       currentEmpSalary.get("SALARY_AMOUNT"));
                    System.out.println("################################");
                    personInfo.setSalary(currentEmpSalary.get("SALARY_AMOUNT").toString());
                }

            }
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
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Email");
            return;
        }


        JAXBElement<String> bXBElement = personDetails.getEmailAddress();
        personInfo.setEmail(bXBElement.getValue());
        List<UserWorkRelationshipDetails> relationshipDetails =
            userDetails.getUserWorkRelationshipDetails();

        if (relationshipDetails.get(0).getPositionName() == null) {
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Position");
            return;
        }


        String position = relationshipDetails.get(0).getPositionName();
        String positionCode=relationshipDetails.get(0).getPositionCode();
        System.err.println("Position is : " + position);
        //personInfo.setPosition(position); 2023 Approval Hierarchy Change
        System.err.println("Position Code is : " + positionCode);
        personInfo.setPosition(positionCode);

        if (relationshipDetails.get(0).getDepartmentName() == null) {
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Department");
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
        }
        //       -------------  get user grade :

        if (relationshipDetails.get(0).getGradeCode() == null) {
            JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Grad Value");
            return;
        }

        String gradeCode = relationshipDetails.get(0).getGradeCode();
        personInfo.setGradeCode(gradeCode);


        if (!(position.equals("THC Group CEO"))) {
            if (relationshipDetails.get(0).getManagerId() == null) {
                JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Manager Id");
                return;
            }

            Long managerId = relationshipDetails.get(0).getManagerId();
            UserDetails managerDetails =
                userService.getUserDetailsByPersonId(managerId);

            if (managerDetails.getPersonNumber() == null) {
                JSFUtils.addFacesErrorMessage("You can't open the application as this user has no Manager number");
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

        } else if (position.equalsIgnoreCase("THC Group CEO")) {
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
        } else {
            personInfo.setAssignee(userDetails.getPersonNumber());
        }
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
        Map perDiem;
        try {
            perDiem = BIRA.getPerDiemBasedOnGrade(personInfo.getGradeCode());
            JSFUtils.setExpressionValue("#{sessionScope.perDiem}", perDiem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEmail(String personNumber) {
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);

        JAXBElement<String> aXBElement =
            userDetails.getUserPersonDetails().get(0).getEmailAddress();
        return aXBElement.getValue();
    }

    public void sendEmail(String personNumber, Row subject) {
        System.err.println("Called");
        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendTripEmail("OFOQ.HR@TATWEER.SA", email,
                          (BusinessTripRequestViewRowImpl)subject, "");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendRejectionEmail(String personNumber, Row subject) {
        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendTripRejectionEmail("OFOQ.HR@TATWEER.SA", email,
                                   (BusinessTripRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendEmailWithInvoiceAmount(String personNumber, Row subject) {
        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendTripEmailWithInvoiceAmount("OFOQ.HR@TATWEER.SA", email,
                                           (BusinessTripRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }


    public void sendEmailToPayrollMgr(Row subject) {

        BiReportAccess report = new BiReportAccess();
        //                ReportsUtils report = new ReportsUtils();
        List<Map> personData = null;
        try {
            personData =
                    report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("HR Payroll and benefits Supervisor Name is " +
                           personData.get(0).get("DISPLAY_NAME"));
        System.err.println("HR Payroll and benefits Supervisor Email " +
                           personData.get(0).get("EMAIL_ADDRESS"));

        if (null == (personData.get(0).get("EMAIL_ADDRESS"))) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent to HR Payroll and benefits Supervisor as he has no email");
        } else {
            sendFYIEmailToPayrollManager("OFOQ.HR@TATWEER.SA",
                                         personData.get(0).get("EMAIL_ADDRESS").toString(),
                                         (BusinessTripRequestViewRowImpl)subject,
                                         personData.get(0).get("DISPLAY_NAME").toString());
            JSFUtils.addFacesInformationMessage("FYI Mail has been sent to HR Payroll and benefits Supervisor");
        }
    }


    public void sendEmailToPayrollMgrFinalApproval(Row subject) {
        BiReportAccess report = new BiReportAccess();
        //                ReportsUtils report = new ReportsUtils();
        List<Map> personData = null;
        try {
            personData =
                    report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("HR Payroll and benefits Supervisor Name is " +
                           personData.get(0).get("DISPLAY_NAME"));
        System.err.println("PHR Payroll and benefits Supervisor Email " +
                           personData.get(0).get("EMAIL_ADDRESS"));

        if (null == (personData.get(0).get("EMAIL_ADDRESS"))) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent to HR Payroll and benefits Supervisor as he has no email");
        } else {
            sendFYIEmailToPayrollManagerFinalApproval("OFOQ.HR@TATWEER.SA",
                                                      personData.get(0).get("EMAIL_ADDRESS").toString(),
                                                      (BusinessTripRequestViewRowImpl)subject,
                                                      personData.get(0).get("DISPLAY_NAME").toString());
            JSFUtils.addFacesInformationMessage("FYI Mail has been sent to HR Payroll and benefits Supervisor");
        }
    }

    public void sendTripEmail(String from, String to,
                              BusinessTripRequestViewRowImpl subject, String fyi) {
        
        String status = subject.getRequestStatus();
        String edited = subject.getEditRequest();
        String requesterName = subject.getPersoneName();
        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        
        String assigneeName = "Sir";
        if(fyi!= null && "PERSON".equals(fyi)){
            assigneeName = requesterName;
        }else if(fyi!= null && "SIR".equals(fyi)){
            assigneeName = "Sir";
        } else{
            assigneeName = subject.getAssigneeName()!=null ? subject.getAssigneeName() : "Sir";
        }
        
        String subj = "";
        String hdrMsg = "";
        if("Withdrawn".equals(status)){
            subj = bt + " Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw "+ bt +" Request, and below the details";
            
        }else if("Waiting Withdraw Approval".equals(status)){
            subj =  bt +" Request for Mr./Mrs."+requesterName+" has been withdraw";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has withdraw "+ bt +" Request, and below the details";
            
        }else if("Withdrawn Rejected".equals(status)){
            subj = bt +" Withdraw Request for Mr./Mrs."+requesterName+" has been Rejected";
            hdrMsg= "Kindly find below the details of "+ bt +" request as below";
            
        }else if(edited!=null && edited.equals("Y") && "PENDING".equals(status)){
            subj = bt + " Request for Mr./Mrs."+requesterName+" has been updated";
            hdrMsg= "Kindly be informed you that Mr./Mrs."+requesterName+" has updated "+ bt +" Request, and below the details";
            
        }else{
            subj = bt + " Request for Mr./Mrs."+requesterName+ " is " + status;  
            hdrMsg= "Kindly find below the details of "+ bt +" request as below";
        }
        
        if(fyi!= null && "Y".equals(fyi)){
            if("Withdrawn".equals(status)){
                subj = bt + " Request has been withdraw";
                hdrMsg= "Kindly be informed you that the following "+ bt +" request has been withdraw";
            }
            if("APPROVED".equals(status)){
                subj = bt +" Request has been APPROVED";
                hdrMsg= "Kindly be informed you that the following "+ bt +" request has been Approved";
            }
           
        }

//            JSFUtils.addFacesInformationMessage(subj);
//            JSFUtils.addFacesInformationMessage(hdrMsg);
        
        if (to == null) {
           // to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();
        String fromCity=subject.getNewCity()!=null?subject.getNewCity():"";
        String toCity=subject.getToCity()!=null?subject.getToCity():"";
        String meanTravel=subject.getMeansTravel()!=null?subject.getMeansTravel():"";
        
        
        String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
        String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
        String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
        String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
        String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
        
        String[] arrofDates=null;
        if(strDate!=null && endDate!=null)
        {
         arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
        }
        
        String tripType = subject.getTripType();
        //String toPart = "Dear Sir," + "<br/><br/>";
        String returnMail=ApprovelLine.getMailDetails(subject);
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String benefitType = subject.getBenefitType();
        //        System.err.println("==="+strDate+"===="+endDate+"======>"+apprvalsubDate+"===============>"+apprvalAppDate);

        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"left\" style=\"text-align:left\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
//            subject.getTripType() + " Business Trip " +
//            subject.getRequestStatus() + "  </span></p>\n" +
            "Dear " + assigneeName + "," + "\n" +
            "<br/>" +
            hdrMsg +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

            " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
            "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
            subject.getPersoneId() + "</td></tr>\n" +
            "    <tr><th>&nbsp;Name</th>\n" +
            "      <td width=\"50%\">" + PersoneName + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Position\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +PersonPosition +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Job\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + PersonJob + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Department\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +PersonDepartment +
            "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Location\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +PersonLocation +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
            "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
            bt + " Details</h2></td></tr>" + "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Trip Type\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Way of Travel\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
            "    </tr>\n" +
            returnMail  +
            "    </table>";

        String bodyPart13 =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;From City\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + fromCity + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;To City\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + toCity + "</td>\n" +
            "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" +  arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays().toString() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject", subj);
//                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }

    public void sendTripRejectionEmail(String from, String to,
                                       BusinessTripRequestViewRowImpl subject) {

        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();
        String[] arrofDates=null;
        if(strDate!=null && endDate!=null)
        {
         arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
        }
        String fromCity =
            subject.getNewCity() != null ? subject.getNewCity() : "";
        String toCity = subject.getToCity() != null ? subject.getToCity() : "";
        String meanTravel =
            subject.getMeansTravel() != null ? subject.getMeansTravel() : "";


        String PersoneName =
            subject.getPersoneName() != null ? subject.getPersoneName() : "";
        String PersonPosition =
            subject.getPersonPosition() != null ? subject.getPersonPosition() :
            "";
        String PersonJob =
            subject.getPersonJob() != null ? subject.getPersonJob() : "";
        String PersonDepartment =
            subject.getPersonDepartment() != null ? subject.getPersonDepartment() :
            "";
        String PersonLocation =
            subject.getPersonLocation() != null ? subject.getPersonLocation() :
            "";

        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String benefitType = subject.getBenefitType();
        String mailReturn =ApprovelLine.getMailDetails(subject);
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            subject.getTripType() + " Business Trip " +
            subject.getRequestStatus() + "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
                   "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
                   bt + " Details</h2></td></tr>" + "<tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Trip Type\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
                   "    </tr>\n" +
                   "    <tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Way of Travel\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
                   "    </tr>\n" +
                   mailReturn  +
                   "    </table>";

        String bodyPart13 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;From City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + fromCity + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;To City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + toCity + "</td>\n" +
                    "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }


    public void sendTripEmailWithInvoiceAmount(String from, String to,
                                               BusinessTripRequestViewRowImpl subject) {

        //        Number totalAmount = null;
        //        Number laundryAmount = null;
        //        Number foodAmount = null;
        //        Number visaCostAmount = null;
        //        Number otherCostAmount = null;
        //        Number transportationAmount = null;
        //        Number housingAmount = null;
        //        Number travelCost = null;
        //        String wayOfTravel = "";
        //        if (subject.getMeansTravel() != null) {
        //            wayOfTravel = subject.getMeansTravel();
        //            if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
        //                wayOfTravel.equalsIgnoreCase("Train (Employee)") ||
        //                wayOfTravel.equalsIgnoreCase("Plane (Employee)")) {
        //
        //
        //                if (subject.getTravelCost() != null) {
        //
        //                    travelCost = subject.getTravelCost();
        //
        //                } else {
        //                    travelCost = new Number(0);
        //                }
        //                if (subject.getLaundryAllowanceAmount() != null) {
        //
        //                    laundryAmount = subject.getLaundryAllowanceAmount();
        //
        //                } else {
        //                    laundryAmount = new Number(0);
        //                }
        //                if (subject.getFoodAllowanceAmount() != null) {
        //
        //                    foodAmount = subject.getFoodAllowanceAmount();
        //
        //                } else {
        //                    foodAmount = new Number(0);
        //                }
        //                if (subject.getVisaCost() != null) {
        //
        //                    visaCostAmount = subject.getVisaCost();
        //
        //                } else {
        //                    visaCostAmount = new Number(0);
        //                }
        //                if (subject.getOtherCost() != null) {
        //
        //                    otherCostAmount = subject.getOtherCost();
        //
        //                } else {
        //                    otherCostAmount = new Number(0);
        //                }
        //
        //                if (subject.getTransAllowanceAmount() != null) {
        //
        //                    transportationAmount = subject.getTransAllowanceAmount();
        //
        //                } else {
        //                    transportationAmount = new Number(0);
        //                }
        //
        //                if (subject.getHousingAllowanceAmount() != null) {
        //
        //                    housingAmount = subject.getHousingAllowanceAmount();
        //
        //                } else {
        //                    housingAmount = new Number(0);
        //                }
        //                totalAmount =
        //                        laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount).add(travelCost);
        //
        //                if (subject.getHousingAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(housingAmount);
        //
        //                }
        //
        //                if (subject.getTransportationAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(transportationAmount);
        //
        //                }
        //
        //
        //            } else if (wayOfTravel.equalsIgnoreCase("Car (Company)") ||
        //                       wayOfTravel.equalsIgnoreCase("Train (Company)") ||
        //                       wayOfTravel.equalsIgnoreCase("Plane (Company)")) {
        //
        //
        //                if (subject.getLaundryAllowanceAmount() != null) {
        //
        //                    laundryAmount = subject.getLaundryAllowanceAmount();
        //
        //                } else {
        //                    laundryAmount = new Number(0);
        //                }
        //                if (subject.getFoodAllowanceAmount() != null) {
        //
        //                    foodAmount = subject.getFoodAllowanceAmount();
        //
        //                } else {
        //                    foodAmount = new Number(0);
        //                }
        //                if (subject.getVisaCost() != null) {
        //
        //                    visaCostAmount = subject.getVisaCost();
        //
        //                } else {
        //                    visaCostAmount = new Number(0);
        //                }
        //                if (subject.getOtherCost() != null) {
        //
        //                    otherCostAmount = subject.getOtherCost();
        //
        //                } else {
        //                    otherCostAmount = new Number(0);
        //                }
        //
        //                if (subject.getTransAllowanceAmount() != null) {
        //
        //                    transportationAmount = subject.getTransAllowanceAmount();
        //
        //                } else {
        //                    transportationAmount = new Number(0);
        //                }
        //
        //                if (subject.getHousingAllowanceAmount() != null) {
        //
        //                    housingAmount = subject.getHousingAllowanceAmount();
        //
        //                } else {
        //                    housingAmount = new Number(0);
        //                }
        //                totalAmount =
        //                        laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount);
        //
        //                if (subject.getHousingAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(housingAmount);
        //
        //                }
        //
        //                if (subject.getTransportationAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(transportationAmount);
        //
        //                }
        //
        //
        //            }
        //        }

        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();
        String[] arrofDates=null;
        if(strDate!=null && endDate!=null)
        {
         arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
        }
        String PersoneName =
            subject.getPersoneName() != null ? subject.getPersoneName() : "";
        String PersonPosition =
            subject.getPersonPosition() != null ? subject.getPersonPosition() :
            "";
        String PersonJob =
            subject.getPersonJob() != null ? subject.getPersonJob() : "";
        String PersonDepartment =
            subject.getPersonDepartment() != null ? subject.getPersonDepartment() :
            "";
        String PersonLocation =
            subject.getPersonLocation() != null ? subject.getPersonLocation() :
            "";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            subject.getTripType() + " Business Trip " +
            subject.getRequestStatus() + "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = " <p>&nbsp;</p>";

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";

        String bodyPart13 = " <p>&nbsp;</p>";

        String bodyPart14 =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
            "      <th>\n" +
            "        Total Amount Due\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getTotalForInvoice() +
            "</td>\n" +
            "    </tr>\n" +
            "  </table>";
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";
        String text =
            bodyPart + bodyPart10 + bodyPart11 + bodyPartDuration + bodyPart13 +
            bodyPart14 + verticalSpace + ApprovalPart1 + thankYouPart +
            signaturePart + "</p>";

        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);
        //sendMail.getParamsMap().put("receiver", "alaa.osman200@gmail.com");
        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text);
        sendMail.execute();
    }


    public String showAttachmentPopup() {
        // Add event code here...
        inputFile.resetValue();
        inputFile.setValid(true);
        //ADFUtils.findOperation("CreateInsert15").execute();
        attachmentPopup.show(new RichPopup.PopupHints());
        return null;
    }

    public void setAttachmentPopup(RichPopup attachmentPopup) {
        this.attachmentPopup = attachmentPopup;
    }

    public RichPopup getAttachmentPopup() {
        return attachmentPopup;
    }

    public String saveBtnAction() {
        // Add event code here...
        UploadedFile file = (UploadedFile)inputFile.getValue();
        // Get the file name
        String fileName = file.getFilename();
        // get the mime type
        String contentType = file.getContentType();
        // get blob
        if (fileName.length() > 50) {
            JSFUtils.addFacesErrorMessage("File Name cannot be more than 50 character including extension. Please try with smaller file name!");
            return null;
        } else {
            String uploadedBy =
                (String)JSFUtils.resolveExpression("#{PersonInfo.fullName}");
            BlobDomain blob = createBlobDomain(file);
            System.out.println("file name is " + fileName);
            ADFUtils.findOperation("CreateInsert").execute();
            //        if (getAttachmentFileName() != null &&
            //            getAttachmentFileName().isEmpty()) {
            //            fileName = getAttachmentFileName();
            //        }
            ADFUtils.setBoundAttributeValue("AttachmentFile", blob);
            ADFUtils.setBoundAttributeValue("FileName", fileName);
            ADFUtils.setBoundAttributeValue("FileType", contentType);
            ADFUtils.setBoundAttributeValue("UploadedBy", uploadedBy);
        }
        //        AdfFacesContext.getCurrentInstance().addPartialTarget(attachmentsTable);
        attachmentFileName = null;
        //        attachmentFileDesc = null ;
        attachmentPopup.hide();
        return null;
    }

    public BlobDomain getBlob(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;
        try {
            in = file.getInputStream();
            blobDomain = new BlobDomain();
            out = blobDomain.getBinaryOutputStream();
            IOUtils.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return blobDomain;
    }

    private BlobDomain createBlobDomain(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;

        try {
            in = file.getInputStream();

            blobDomain = new BlobDomain();
            out = blobDomain.getBinaryOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead = 0;

            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }

        return blobDomain;
    }

    public String cancelBtnAction() {
        // Add event code here...
        attachmentPopup.hide();
        return null;
    }

    public void downloadFile(FacesContext facesContext,
                             OutputStream outputStream) {
        BlobDomain blob =
            (BlobDomain)ADFUtils.getBoundAttributeValue("AttachmentFile");
        try {
            IOUtils.copy(blob.getInputStream(), outputStream);
            blob.closeInputStream();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInputFile(RichInputFile inputFile) {
        this.inputFile = inputFile;
    }

    public RichInputFile getInputFile() {
        return inputFile;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void populateActionReason() {
        BiReportAccess report = new BiReportAccess();
        //                ReportsUtils report = new ReportsUtils();
        String actionReasoneReport = null;
        try {
            actionReasoneReport =
                    report.callActionReasoneReport(JSFUtils.resolveExpressionAsString("#{pageFlowScope.reqPersonNumber}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (actionReasoneReport != null) {
            setActionReason(actionReasoneReport);
        }
    }

    public void trans_allowances_vcl(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        if (tripType.equalsIgnoreCase("Expense")) {
            return;
        }

        String type = (String)valueChangeEvent.getNewValue();
        System.out.println("type is " + type);
        if (type.equalsIgnoreCase("Comp")) {
            ADFUtils.setBoundAttributeValue("TransAllowanceAmount", null);
        } else if (type.equalsIgnoreCase("Emp")) {
            String allowanceTripType = null;
            String destinationType =
                (String)ADFUtils.getBoundAttributeValue("DestinationType");
            String localOrInternational =
                (String)ADFUtils.getBoundAttributeValue("SubType");
            if (localOrInternational.equalsIgnoreCase("local"))

            {
                allowanceTripType = "Local";

            }

            else {
                if (destinationType == null) {

                    JSFUtils.addFacesErrorMessage("You Should Select Destination Type");
                    return;
                }
                if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                    allowanceTripType = "Inter";

                } else {
                    allowanceTripType = "Inter2";

                }

            }

            OperationBinding calcOpr =
                ADFUtils.findOperation("calcAllowances");
            Map map = calcOpr.getParamsMap();
            map.put("allowanceType", "TRANS");
            map.put("tripType", allowanceTripType);
            //            tripType
            String assignee =
                (String)ADFUtils.getBoundAttributeValue("Assignee");

            if (assignee != null) {
                if (assignee.equals("HR Payroll and benefits Supervisor")) {
                    String reqEmpNo =
                        (String)ADFUtils.getBoundAttributeValue("PersoneId");
                    //           Payroll Manager
                    BiReportAccess report = new BiReportAccess();
                    //                ReportsUtils report = new ReportsUtils();
                    String actionReasoneReport = null;
                    try {
                        actionReasoneReport =
                                report.callActionReasoneReport(reqEmpNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (actionReasoneReport != null) {
                        map.put("classType", actionReasoneReport);
                        calcOpr.execute();
                    } else {
                        JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                    }
                }
            } else {
                if (getActionReason() != null) {
                    map.put("classType", getActionReason());
                    calcOpr.execute();
                } else {
                    JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                }

            }
        }
    }

    public void hous_allowances_vcl(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        if (benefitType.equalsIgnoreCase("A")) {
            if (tripType.equalsIgnoreCase("Expense")) {
                return;
            }
            String type = (String)valueChangeEvent.getNewValue();
            if (type.equalsIgnoreCase("Comp")) {
                ADFUtils.setBoundAttributeValue("HousingAllowanceAmount",
                                                null);
            } else if (type.equalsIgnoreCase("Emp")) {

                String allowanceTripType = null;
                String destinationType =
                    (String)ADFUtils.getBoundAttributeValue("DestinationType");
                String localOrInternational =
                    (String)ADFUtils.getBoundAttributeValue("SubType");
                if (localOrInternational.equalsIgnoreCase("local"))

                {
                    allowanceTripType = "Local";

                }

                else {

                    if (destinationType == null) {

                        JSFUtils.addFacesErrorMessage("You Should Select Destination Type");
                        return;
                    }

                    if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                        allowanceTripType = "Inter";

                    } else {
                        allowanceTripType = "Inter2";

                    }

                }

                OperationBinding calcOpr =
                    ADFUtils.findOperation("calcAllowances");
                Map map = calcOpr.getParamsMap();
                map.put("allowanceType", "HOUSING");
                map.put("tripType", allowanceTripType);

                String assignee =
                    (String)ADFUtils.getBoundAttributeValue("Assignee");

                if (assignee != null) {
                    if (assignee.equals("HR Payroll and benefits Supervisor")) {
                        String reqEmpNo =
                            (String)ADFUtils.getBoundAttributeValue("PersoneId");
                        //           Payroll Manager
                        BiReportAccess report = new BiReportAccess();
                        //                ReportsUtils report = new ReportsUtils();
                        String actionReasoneReport = null;
                        try {
                            actionReasoneReport =
                                    report.callActionReasoneReport(reqEmpNo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (actionReasoneReport != null) {
                            map.put("classType", actionReasoneReport);
                            calcOpr.execute();
                        } else {
                            JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                        }
                    }
                } else {
                    if (getActionReason() != null) {
                        map.put("classType", getActionReason());
                        calcOpr.execute();
                    } else {
                        JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                    }

                }
            }
        }
    }

    public void food_allowances_vcl(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        if (benefitType.equalsIgnoreCase("A")) {
            if (tripType.equalsIgnoreCase("Expense")) {
                return;
            }
            String type = (String)valueChangeEvent.getNewValue();
            if (type.equalsIgnoreCase("Comp")) {
                ADFUtils.setBoundAttributeValue("FoodAllowanceAmount", null);
            } else if (type.equalsIgnoreCase("Emp")) {

                String allowanceTripType = null;
                String destinationType =
                    (String)ADFUtils.getBoundAttributeValue("DestinationType");
                String localOrInternational =
                    (String)ADFUtils.getBoundAttributeValue("SubType");
                if (localOrInternational.equalsIgnoreCase("local"))

                {
                    allowanceTripType = "Local";

                }

                else {

                    if (destinationType == null) {

                        JSFUtils.addFacesErrorMessage("You Should Select Destination Type");
                        return;
                    }

                    if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                        allowanceTripType = "Inter";

                    } else {
                        allowanceTripType = "Inter2";

                    }

                }

                OperationBinding calcOpr =
                    ADFUtils.findOperation("calcAllowances");
                Map map = calcOpr.getParamsMap();
                map.put("allowanceType", "FOOD");
                map.put("tripType", allowanceTripType);


                String assignee =
                    (String)ADFUtils.getBoundAttributeValue("Assignee");

                if (assignee != null) {
                    if (assignee.equals("HR Payroll and benefits Supervisor")) {
                        String reqEmpNo =
                            (String)ADFUtils.getBoundAttributeValue("PersoneId");
                        //           Payroll Manager
                        BiReportAccess report = new BiReportAccess();
                        //                ReportsUtils report = new ReportsUtils();
                        String actionReasoneReport = null;
                        try {
                            actionReasoneReport =
                                    report.callActionReasoneReport(reqEmpNo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (actionReasoneReport != null) {
                            map.put("classType", actionReasoneReport);
                            calcOpr.execute();
                        } else {
                            JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                        }
                    }
                } else {
                    if (getActionReason() != null) {
                        map.put("classType", getActionReason());
                        calcOpr.execute();
                    } else {
                        JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                    }

                }
            }
        }
    }

    public void setActionReason(String actionReason) {
        this.actionReason = actionReason;
    }

    public String getActionReason() {
        return actionReason;
    }

    public void setRequestPopup(RichPopup requestPopup) {
        this.requestPopup = requestPopup;
    }

    public RichPopup getRequestPopup() {
        return requestPopup;
    }

    public String startRequestAction() {
        // Add event code here...

        ADFUtils.findOperation("ExecuteClevelWithParams").execute();
        DCIteratorBinding csecretaryIterator =
            ADFUtils.findIterator("CsecretaryROViewIterator");
        if (csecretaryIterator.getEstimatedRowCount() > 0) {
            requestPopup.show(new RichPopup.PopupHints());
            return null;
        } else {
            JSFUtils.setExpressionValue("#{pageFlowScope.reqPersonNumber}",
                                        JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
            JSFUtils.setExpressionValue("#{pageFlowScope.reqPersonName}",
                                        JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
            //            JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
            //                                        false);
            //            pageFlowScope.moreCitiesCheckBox

            //            BiReportAccess BIRA = new BiReportAccess();
            //            try {
            //                List<Map> list = BIRA.getDepartmentsData();
            //
            //                DCIteratorBinding deptIter =
            //                    ADFUtils.findIterator("DepartmentsVO1Iterator");
            //                ViewObject deptView = deptIter.getViewObject();
            //                DepartmentsVORowImpl deptRow;
            //                if (list.size() > 0) {
            //                    ADFUtils.findOperation("deleteDeptTableRows").execute();
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
            //                        ADFUtils.findOperation("Commit").execute();
            //
            //                    }
            //
            //                    deptView.executeQuery();
            //                    deptIter.executeQuery();
            //
            //                }
            //
            //            } catch (Exception e) {
            //                e.printStackTrace();
            //            }
            return "addNew";
        }
    }


    /* Modified by Moshina Dt: 2024/02/28
    * Instead of position number, Position code is passing as parameter for the below THCPositionReport.xdo
    */
    public String okPopRequestAction() {
        // Add event code here...

        String reqType = (String)JSFUtils.resolveExpression("#{pageFlowScope.reqType}");
        if (reqType.equals("clevel")) {
        
            String clevelPosition = (String)JSFUtils.resolveExpression("#{bindings.CsecretaryROView.inputValue}");
            System.out.println("clevel positon: "+clevelPosition);
            UserDetails userDetails = null;
            UserServiceUtil userService = new UserServiceUtil();
            userDetails = userService.getUserDetailsByPersonNumber(clevelPosition);
            System.out.println("userDetails.getPersonNumber(): "+userDetails.getPersonNumber());
            System.out.println("userDetails: "+userDetails);
            List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
            int length = userDetails.getUserWorkRelationshipDetails().size();
            System.out.println("length: "+length);
            
            /*
             * CEO issue - wrong perDiem value (Hier for Clevel) added by Moshina on 2024.05.14
             * Issue Id #21361 
             * Added
             */
            String gradeCode = relationshipDetails.get(0).getGradeCode();
            Map perDiem;
            try {
                BiReportAccess BIRA = new BiReportAccess();
                perDiem = BIRA.getPerDiemBasedOnGrade(gradeCode);
                System.out.println("perDiem data of selected clevel person: "+perDiem);
                com.sbm.selfServices.view.utils.JSFUtils.setExpressionValue("#{sessionScope.perDiem}", perDiem);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            String position = relationshipDetails.get(0).getPositionName();
            String positionCode = relationshipDetails.get(0).getPositionCode();
            System.err.println("Position and Position code of CEO: " + position + ", " + positionCode);
            
            BiReportAccess report = new BiReportAccess();
            //            ReportsUtils report = new ReportsUtils();
            List<Map> personData = null;
            try {
                personData = report.getPersonByPostionReport(positionCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("person is " +
                               personData.get(0).get("PERSON_NUMBER"));
            System.out.println("person is " +
                               personData.get(0).get("DISPLAY_NAME"));
            JSFUtils.setExpressionValue("#{pageFlowScope.reqPersonNumber}",
                                        personData.get(0).get("PERSON_NUMBER").toString());
            JSFUtils.setExpressionValue("#{pageFlowScope.reqPersonName}",
                                        personData.get(0).get("DISPLAY_NAME").toString());


            try {
                List<Map> list = report.getDepartmentsData();

                DCIteratorBinding deptIter =
                    ADFUtils.findIterator("DepartmentsVO1Iterator");
                ViewObject deptView = deptIter.getViewObject();
                DepartmentsVORowImpl deptRow;
                if (list.size() > 0) {
                    ADFUtils.findOperation("deleteDeptTableRows").execute();
                    System.out.println("All Departments table rows deleted successfully");
                    for (Map currentDepartment : list) {
                        deptRow = (DepartmentsVORowImpl)deptView.createRow();
                        deptRow.setDeptId(new Number(currentDepartment.get("ORGANIZATION_ID").toString()));
                        deptRow.setDeptName(currentDepartment.get("NAME").toString());
                        if (currentDepartment.get("SEGMENT2") != null) {
                            deptRow.setSegment2((currentDepartment.get("SEGMENT2").toString()));
                            System.out.println("testing deptRow.getSeg2: "+deptRow.getSegment2());
                        } else {
                            deptRow.setSegment2("");
                        }
                        if (currentDepartment.get("SEGMENT3") != null) {
                            deptRow.setSegment3((currentDepartment.get("SEGMENT3").toString()));
                            System.out.println("testing deptRow.getSeg3: "+deptRow.getSegment3());
                        } else {
                            deptRow.setSegment3("");
                        }
                        if (currentDepartment.get("SEGMENT4") != null) {
                            deptRow.setSegment4((currentDepartment.get("SEGMENT4").toString()));
                        } else {
                            deptRow.setSegment4("");
                        }
                        deptView.insertRow(deptRow);
                        //                            System.out.println("list.size(); >>>"+list.size());
                        //                                System.out.println("I'm Inside for each loop");
                        //                                System.err.println("Dept ID >>> "+currentDepartment.get("ORGANIZATION_ID"));
                        //                                System.err.println("Dept Name >>> "+currentDepartment.get("NAME"));
                        //                                System.out.println("################################");
                        ADFUtils.findOperation("Commit").execute();
                        //                                System.out.println("Currunt Dept inserted successfully");
                    }
                    deptView.executeQuery();
                    deptIter.executeQuery();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "addNew";
        } else {
            JSFUtils.setExpressionValue("#{pageFlowScope.reqPersonNumber}", JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}"));
            JSFUtils.setExpressionValue("#{pageFlowScope.reqPersonName}", JSFUtils.resolveExpression("#{PersonInfo.fullName}"));
            
            /*
             * CEO issue - wrong perDiem value (Hier for Clevel) added by Moshina on 2024.05.14
             * Issue Id #21361 
             * Added
             */
            String reqPersonNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
            System.out.println("req person number : "+reqPersonNumber);
            UserDetails userDetails = null;
            UserServiceUtil userService = new UserServiceUtil();
            userDetails = userService.getUserDetailsByPersonNumber(reqPersonNumber);
            System.out.println("userDetails.getPersonNumber(): "+userDetails.getPersonNumber());
            List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
           
            String gradeCode = relationshipDetails.get(0).getGradeCode();
            Map perDiem;
            try {
                BiReportAccess BIRA = new BiReportAccess();
                perDiem = BIRA.getPerDiemBasedOnGrade(gradeCode);
                System.out.println("perDiem data of selected req person person: "+perDiem);
                com.sbm.selfServices.view.utils.JSFUtils.setExpressionValue("#{sessionScope.perDiem}", perDiem);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String position = relationshipDetails.get(0).getPositionName();
            String positionCode = relationshipDetails.get(0).getPositionCode();
            System.err.println("Position and position code of the person: " + position + ", " + positionCode);
            
            BiReportAccess BIRA = new BiReportAccess();
            try {
                List<Map> list = BIRA.getDepartmentsData();

                DCIteratorBinding deptIter = ADFUtils.findIterator("DepartmentsVO1Iterator");
                ViewObject deptView = deptIter.getViewObject();
                DepartmentsVORowImpl deptRow;
                if (list.size() > 0) {
                    ADFUtils.findOperation("deleteDeptTableRows").execute();
                    System.out.println("All Departments table rows deleted successfully");
                    for (Map currentDepartment : list) {
                        deptRow = (DepartmentsVORowImpl)deptView.createRow();
                        deptRow.setDeptId(new Number(currentDepartment.get("ORGANIZATION_ID").toString()));
                        deptRow.setDeptName(currentDepartment.get("NAME").toString());
                        if (currentDepartment.get("SEGMENT2") != null) {
                            deptRow.setSegment2((currentDepartment.get("SEGMENT2").toString()));
                            System.out.println("testing deptRow.getSeg2-second: "+deptRow.getSegment2());
                        } else {
                            deptRow.setSegment2("");
                        }
                        if (currentDepartment.get("SEGMENT3") != null) {
                            deptRow.setSegment3((currentDepartment.get("SEGMENT3").toString()));
                            System.out.println("testing deptRow.getSeg3-second: "+deptRow.getSegment3());
                        } else {
                            deptRow.setSegment3("");
                        }
                        if (currentDepartment.get("SEGMENT4") != null) {
                            deptRow.setSegment4((currentDepartment.get("SEGMENT4").toString()));
                        } else {
                            deptRow.setSegment4("");
                        }
                        deptView.insertRow(deptRow);
                        //                            System.out.println("list.size(); >>>"+list.size());
                        //                                System.out.println("I'm Inside for each loop");
                        //                                System.err.println("Dept ID >>> "+currentDepartment.get("ORGANIZATION_ID"));
                        //                                System.err.println("Dept Name >>> "+currentDepartment.get("NAME"));
                        //                                System.out.println("################################");
                        ADFUtils.findOperation("Commit").execute();
                        //                                System.out.println("Currunt Dept inserted successfully");
                    }

                    deptView.executeQuery();
                    deptIter.executeQuery();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "addNew";

        }
    }

    public void confirmationDL(DialogEvent dialogEvent) throws Exception {
        // Add event code here...
        String moveFlag = "Y";
        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {


            //            String status =
            //                ADFUtils.getBoundAttributeValue("RequestStatus").toString();

            Row tripRow =
                ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();


            String status = (String)tripRow.getAttribute("RequestStatus");
            System.out.println("Request status===>" + status);

            ADFUtils.setBoundAttributeValue("ActionTaken",
                                            "Withdraw Request PENDING");

            String personNumber = tripRow.getAttribute("PersoneId").toString();
            Object costCenter = tripRow.getAttribute("CostCenter");
            tripRow.setAttribute("StepId", "1");
            String personLocation = (String)tripRow.getAttribute("PersonLocation") !=null?(String)tripRow.getAttribute("PersonLocation"):""; //2023-PSC change
            String requestTripType =tripRow.getAttribute("TripType").toString() !=null?tripRow.getAttribute("TripType").toString():"";
                                        
                    String TripTypeName=requestTripType +'-'+ personLocation;
                    ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String emailNotification = (String)nextStep.getAttribute("EmailNotification");
            String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";

            UserServiceUtil userService = new UserServiceUtil();
            UserDetails userDetails = null;

            userDetails = userService.getUserDetailsByPersonNumber(personNumber);
            List<UserWorkRelationshipDetails> relationshipDetails = userDetails.getUserWorkRelationshipDetails();
            
            /*
             * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
             * Issue Id #21361 
             * Commented
            Long lineManagerID = relationshipDetails.get(0).getManagerId();
            String department = relationshipDetails.get(0).getDepartmentName();
            UserDetails managerDetails = userService.getUserDetailsByPersonId(lineManagerID);
            String managerNumber = managerDetails.getPersonNumber();
            JAXBElement<String> aXBElement = null;
            if (managerDetails.getUserPersonDetails().get(0).getDisplayName() != null) {
                aXBElement = managerDetails.getUserPersonDetails().get(0).getDisplayName();
            }
            */
            
            /*
             * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
             * Issue Id #21361 
             * Added
            */
            Long lineManagerID = 0L;
            String managerNumber = null;
            JAXBElement<String> aXBElement = null;
            String department = relationshipDetails.get(0).getDepartmentName();
            String position = relationshipDetails.get(0).getPositionName();
            System.out.println("CEO Position: "+position);
            if(!(position.equalsIgnoreCase("THC Group CEO"))){
                lineManagerID = relationshipDetails.get(0).getManagerId();
                UserDetails managerDetails = userService.getUserDetailsByPersonId(lineManagerID);
                managerNumber = managerDetails.getPersonNumber();
                if (managerDetails.getUserPersonDetails().get(0).getDisplayName() != null) {
                    aXBElement = managerDetails.getUserPersonDetails().get(0).getDisplayName();
                }
            }
            
            position = tripRow.getAttribute("PersonPosition").toString();
            if (status.equalsIgnoreCase("PENDING")) {
            
                tripRow.setAttribute("StepId", totalStep);
                tripRow.setAttribute("RequestStatus", "Withdrawn");
                tripRow.setAttribute("ActionTaken", "Withdrawn");
                tripRow.setAttribute("Assignee", personNumber);
                tripRow.setAttribute("AssigneeName", "");
                
                ADFUtils.findOperation("Commit").execute();
                 
                String value= ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"), 
                                                                            (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                             (Number)nextStep.getAttribute("NextStepId"), 
                                                                             (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                             (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                             "WITHDRAW_ACT", "");
                ADFUtils.findOperation("Execute").execute();
                JSFUtils.addFacesInformationMessage("Request Withdrawn Successfully !");
                
                
//                String tripType =
//                    ADFUtils.getBoundAttributeValue("TripType").toString();
//                tripRow.setAttribute("RequestStatus", "Withdrawn");
//                if (tripType.equalsIgnoreCase("Local") ||
//                    tripType.equalsIgnoreCase("Inter")) {
//                    tripRow.setAttribute("StepId", totalStep);
//                } else if (tripType.equalsIgnoreCase("Event") ||
//                           tripType.equalsIgnoreCase("Training")) {
//                    tripRow.setAttribute("StepId", totalStep);
//                }
//                tripRow.setAttribute("Assignee", personNumber);
//                if (tripType.equalsIgnoreCase("Expense")) {
//                    ADFUtils.findOperation("changeExpenseFlagToNo").execute();
//                }
//                ADFUtils.findOperation("Commit").execute();
//
//                OperationBinding saveOpe = ADFUtils.findOperation("Commit");
//                saveOpe.execute();
                OperationBinding exOpe = ADFUtils.findOperation("Execute");
                exOpe.execute();

            }

            else {

                /*
                 * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
                 * Issue Id #21361 
                 * Commented
                if (position.equals("Chief Executive Officer"))
                {
                    String tripType = ADFUtils.getBoundAttributeValue("TripType").toString();
                    ADFUtils.setBoundAttributeValue("Assignee", "HR Payroll and benefits Supervisor");

                    if (tripType.equals("Local") || tripType.equals("Inter")) {
                        ADFUtils.setBoundAttributeValue("StepId", 4);
                    }
                    else if (tripType.equals("Event") || tripType.equals("Training")) {
                        ADFUtils.setBoundAttributeValue("StepId", 5);
                    }

                    ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
                    ADFUtils.findOperation("Commit").execute();

                    JSFUtils.addFacesInformationMessage("Request has been submitted");
                    BiReportAccess report = new BiReportAccess();
                    //                ReportsUtils report = new ReportsUtils();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 

                    if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                        if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                        } else {
                            JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
                        }
                    }
                }
                */
                
                /*
                 * CEO issue - Hierarchy Method added by Moshina on 2024.05.14
                 * Issue Id #21361 
                 * Added - Commented for CEO HIER SETUP by Moshina on 07/07/2024
                 */
               /* if (position.equals("THC Group CEO"))
                {
                    BiReportAccess report = new BiReportAccess();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport("HC-CPTL");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String tripType = ADFUtils.getBoundAttributeValue("TripType").toString();
                    ADFUtils.setBoundAttributeValue("Assignee", "HC-CPTL");
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        ADFUtils.setBoundAttributeValue("AssigneeName", personData.get(0).get("DISPLAY_NAME").toString());
                    }
                    System.out.println("CEO Request Type name:  is:"+TripTypeName);
                    OperationBinding oper = ADFUtils.findOperation("getCEONextStepId");
                    Map paramMap = oper.getParamsMap();
                    paramMap.put("requestName", TripTypeName);
                    long stepId = (Long)oper.execute();
                    System.err.println("CEO stepId is "+stepId);
                    ADFUtils.setBoundAttributeValue("StepId", stepId);
                    tripRow = ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
                    ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
                    ADFUtils.findOperation("Commit").execute();

                    JSFUtils.addFacesInformationMessage("Request has been submitted");
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                            sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                        }
                    } else {
                        JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Compensation and Payroll Team Lead doesn't has email");
                    }
                }*/
               /*
                * CEO SETUP HIER code added by Moshina on 07/07/2024
                * Added
               */ 
               if (position.equals("THC Group CEO"))
               {
                   nextOpr = ADFUtils.findOperation("getNextStepCEO");
                   nextStep = (Row)nextOpr.execute();
                   emailNotification = (String)nextStep.getAttribute("EmailNotification");
                   String budgetValidCheck = (String)nextStep.getAttribute("BudgetValidation");
                   stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
                   System.err.println("Submitted: stepType CEO is::"+stepType);
                   BiReportAccess report = new BiReportAccess();
                   List<Map> personData = null;
                   try {
                       personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                   } catch (Exception e) {
                       e.printStackTrace();
                   }

                   ADFUtils.setBoundAttributeValue("Assignee", nextStep.getAttribute("NextAssignee").toString());
                   if (personData.get(0).get("DISPLAY_NAME") != null) {
                       ADFUtils.setBoundAttributeValue("AssigneeName", personData.get(0).get("DISPLAY_NAME").toString());
                   }
                   ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
                   tripRow = ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
                   //ADFUtils.setBoundAttributeValue("RequestStatus", "PENDING");
                   //ADFUtils.findOperation("Commit").execute();

                   JSFUtils.addFacesInformationMessage("Request has been submitted");
                   if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                       if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                           sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                       }
                   } else {
                       JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Compensation and Payroll Team Lead doesn't has email");
                   }
               }
                else{ 
                    String assigneeName = "";
                    String assigneeNo = "";
                    String assigneeEmail = "";
                    String stringLineManagerID = lineManagerID.toString(); 
                    // If step type is POSITION
                                   if("POSITION".equals(stepType)){
                                       System.err.println("Submitted : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
                                       BiReportAccess report = new BiReportAccess();
                                       List<Map> personData = null;
                                       try {
                                           personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                       if (personData.get(0).get("DISPLAY_NAME") != null) {
                                           assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
                                       }
                                       if (personData.get(0).get("PERSON_NUMBER") != null) {
                                           assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
                                       }
                                       if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                                           assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
                                       }
                                   }
                                   // If step type is COST CENTER MANAGER
                                   if("COST_CENTER_MANAGER".equals(stepType) && nextStep.getAttribute("NextAssignee").equals("Cost Center Manager")){
                                       System.err.println("Submitted : COST_CENTER_MANAGER "); 
                                       if (costCenter != null){
                                           BiReportAccess BIRA = new BiReportAccess();
                                           try {
                                               List<Map> managerOfDeptList = BIRA.getManagerOfDepartmentData(costCenter.toString());
                                               if (managerOfDeptList.get(0).get("PERSON_NUMBER") != null) {
                                                   assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                                               }
                                               if (managerOfDeptList.get(0).get("DISPLAY_NAME") != null) {
                                                   assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                                               }
                                               if (managerOfDeptList.get(0).get("EMAIL_ADDRESS") != null) {
                                                   assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                                               }
                                           } catch (Exception e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
                                   
                                   // If step type is DEPARTMENT MANAGER
                                   if("DEPT_MANAGER".equals(stepType)){
                                       System.err.println("Submitted : DEPT_MANAGER :"+department);
                                       BiReportAccess BIRA = new BiReportAccess();
                                       try {
                                           List<Map> managerOfDeptList =
                                               BIRA.getManagerOfDepartmentData(department);
                                           if (managerOfDeptList.size() > 0) {
                                               if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                                   assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                                               }
                                               if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                                   assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                                               }
                                               if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                                   assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                                               } 
                                           } else {
                                               JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                                               moveFlag = "N";
                                           }
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                   
                                   // If step type is LINE MANAGER
                                   if("LINE_MANAGER".equals(stepType)){
                                       System.err.println("Submitted : LINE_MANAGER ");
                                       if (stringLineManagerID == null) {
                                           JSFUtils.addFacesErrorMessage("You donot have Line Manager, So you can not submit the request");
                                           moveFlag = "N";
                                       }
                                       if (managerNumber == null) {
                                           JSFUtils.addFacesErrorMessage("You donot have Line Manager, So you can not submit the request");
                                           moveFlag = "N";
                                       }
                                       
                                       assigneeName = aXBElement.getValue();
                                       assigneeNo = managerNumber;
                                       assigneeEmail = "";
                                   } 
                                   // If step type is USER
                                   if("USER".equals(stepType)){
                                       System.err.println("Submitted : USER ");
                                       ArrayList<String> personDetails = new ArrayList<String>();
                                       personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
                                       assigneeName = personDetails.get(0).toString();
                                       assigneeEmail = personDetails.get(1).toString();
                                       assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
                                   } 
                                   System.out.println("next step is " + nextStep.getAttribute("NextStepId"));
                                   System.err.println("Assigning to (name) ::"+assigneeName);
                                   System.err.println("Assigning to (no) ::"+assigneeNo);
                                   
                                   ADFUtils.setBoundAttributeValue("RequestStatus", "Waiting Withdraw Approval");
                                   ADFUtils.setBoundAttributeValue("StepId",
                                                                   nextStep.getAttribute("NextStepId"));
                                   ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
                                   ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
                    
                                    String value= ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"), 
                                                                                (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                                 (Number)nextStep.getAttribute("NextStepId"), 
                                                                                 (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                                 (String)ADFUtils.getBoundAttributeValue("AssigneeName"), 
                                                                                 "SUMBIT_ACT", "");
                    
                                    System.err.println("EmailNotification flag-->"+emailNotification);
                                    if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                                    if(assigneeEmail!=null){
                                        sendEmailByEmail(assigneeEmail, tripRow);
                                    }
                    }
                }


                //                String managerNumber = (String)JSFUtils.resolveExpression("#{PersonInfo.lineManager}");
//                tripRow.setAttribute("RequestStatus", "WithdrawNeedApproval");
//                tripRow.setAttribute("StepId", "2");
//                tripRow.setAttribute("Assignee", managerNumber);
//                ADFUtils.findOperation("Commit").execute();

                OperationBinding saveOpe = ADFUtils.findOperation("Commit");
                saveOpe.execute();
                OperationBinding exOpe = ADFUtils.findOperation("Execute");
                exOpe.execute();  
            }
        }
    }

    public void noTransAllowance(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String assignee = "";
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        if (ADFUtils.getBoundAttributeValue("Assignee") != null) {

            assignee = ADFUtils.getBoundAttributeValue("Assignee").toString();

        }
        String wayOfTravel = (String)valueChangeEvent.getNewValue();
        if (wayOfTravel == null) {
            wayOfTravel = "Plane (Employee)";

        }
        if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
            wayOfTravel.equalsIgnoreCase("Car (Company)"))

        {
            if (!(assignee.equalsIgnoreCase("HR Payroll and benefits Supervisor"))) {
                if (benefitType.equalsIgnoreCase("A")) {
                    transAllowanceType.setVisible(false);
                    transAllowanceValue.setVisible(false);
                    transAllowanceValue.setValue(null);
                    transAllowanceType.setValue("Comp");
                }

            }

        }

        else {

            if (!(assignee.equalsIgnoreCase("HR Payroll and benefits Supervisor"))) {
                if (benefitType.equalsIgnoreCase("A")) {
                    transAllowanceType.setVisible(true);
                    transAllowanceValue.setVisible(true);
                    transAllowanceType.setValue("Comp");
                    transAllowanceValue.setValue(null);
                }

            } else {
                if (benefitType.equalsIgnoreCase("A")) {
                    transAllowanceType.setVisible(true);
                    transAllowanceValue.setVisible(true);
                }

            }

        }
        if(wayOfTravel.equals("Car (Company)") || wayOfTravel.equals("Train (Employee)")
        || wayOfTravel.equals("Plane (Employee)") || wayOfTravel.equals("Car (Employee)"))
        {
                preStartDate.setValue("");
                preArrivalTime.setValue("");
            }
        if (benefitType.equalsIgnoreCase("P")) {
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
            calcPerDiem();
        }

    }

    public void setTransAllowanceType(RichSelectOneChoice transAllowanceType) {
        this.transAllowanceType = transAllowanceType;
    }

    public RichSelectOneChoice getTransAllowanceType() {
        return transAllowanceType;
    }

    public void setTransAllowanceValue(RichInputText transAllowanceValue) {
        this.transAllowanceValue = transAllowanceValue;
    }

    public RichInputText getTransAllowanceValue() {
        return transAllowanceValue;
    }

    public void showHideSubType(ValueChangeEvent valueChangeEvent) {
        // Add event code here...

        String tripType = (String)valueChangeEvent.getNewValue();
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        if (tripType.equalsIgnoreCase("inter"))

        {
            subType.setValue("International");

            subType.setDisabled(true);

        }

        else if (tripType.equalsIgnoreCase("local")) {
            subType.setValue("Local");

            subType.setDisabled(true);

        }

        else if (tripType.equals("Event") || tripType.equals("Training")) {
            subType.setDisabled(false);

        }

        if (tripType.equalsIgnoreCase("Expense") == true) {
            subType.setDisabled(true);
            //            JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
            //                                        true);
            moreCitesCheckBox.setSelected(true);
            moreCitesCheckBox.setVisible(false);


        }


        if (tripType != "Expense") {
            if (benefitType.equalsIgnoreCase("A")) {
                transAllowanceType.setValue("Comp");
                housingAllowanceType.setValue("Comp");
                foodAllowanceType.setValue("Comp");
            }
            transAllowanceValue.setValue(null);
            housingAllowanceValue.setValue(null);
            foodAllowanceValue.setValue(null);
            moreCitesCheckBox.setSelected(false);
            moreCitesCheckBox.setVisible(true);
            //            JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
            //                                        false);
            if (benefitType.equalsIgnoreCase("P")) {
                valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
                try {
                    Map perdiemMap =
                        (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
                    Number days =
                        ADFUtils.getBoundAttributeValue("Days") != null ?
                        new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
                        new Number(0);
                    Number perdiem = new Number(0);
                    if (tripType.equalsIgnoreCase("Local") ||
                        tripType.equalsIgnoreCase("Inter")) {
                        perdiem = new Number(perdiemMap.get(tripType));
                    }
                    if (tripType.equalsIgnoreCase("Event") ||
                        tripType.equalsIgnoreCase("Training")) {
                        String subType =
                            (String)ADFUtils.getBoundAttributeValue("SubType");
                        if (subType != null) {
                            if (subType.equalsIgnoreCase("Local")) {
                                perdiem = new Number(perdiemMap.get("Local"));
                            }
                            if (subType.equalsIgnoreCase("International")) {
                                perdiem = new Number(perdiemMap.get("Inter"));
                            }
                        }
                    }
                    calcInternationalPerDiem();
                    days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
                    perdiem = perdiem.multiply(days);
                    ADFUtils.setBoundAttributeValue("PerDiemAmount", perdiem);
                } catch (SQLException e) {
                    e.printStackTrace();

                }
                calcPerDiem();
            }
        }
    }


    public void setSubType(RichSelectOneChoice subType) {
        this.subType = subType;
    }

    public RichSelectOneChoice getSubType() {
        return subType;
    }

    public void showOrHideDestinationLOV(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String subType = (String)valueChangeEvent.getNewValue();
        if (subType.equalsIgnoreCase("local")) {

            desType.setRendered(false);

        }

        else {
            desType.setRendered(true);

        }


    }

    public void setDesType(RichSelectOneChoice desType) {
        this.desType = desType;
    }

    public RichSelectOneChoice getDesType() {
        return desType;
    }

    public String approveWithdraw() throws Exception {
        // Add event code here...
        int approvalDone = 0;
        ADFUtils.setBoundAttributeValue("ActionTaken", "Withdraw Approved");
        Row tripRow =
            ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String requestTripType =
            ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
                String TripTypeName=requestTripType +'-'+ personLocation;
                ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        System.err.println("EmailNotification flag-->"+emailNotification);
        String stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
        String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
        String personNumberCheck = (String)ADFUtils.getBoundAttributeValue("PersoneId");
        
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails = null;

        userDetails =
                userService.getUserDetailsByPersonNumber(personNumberCheck);
        List<UserWorkRelationshipDetails> relationshipDetails =  userDetails.getUserWorkRelationshipDetails();
        Long lineManagerID = 0L;
        String managerNumber = null;
        JAXBElement<String> aXBElement = null;
        String department = relationshipDetails.get(0).getDepartmentName();
        String position = relationshipDetails.get(0).getPositionName();
        System.out.println("CEO ISSUE position: " + position);
        if (!(position.equalsIgnoreCase("THC Group CEO"))) {
            System.out.println("CEO ISSUE position - not CEO: ");
            lineManagerID = relationshipDetails.get(0).getManagerId();
            UserDetails managerDetails =
                userService.getUserDetailsByPersonId(lineManagerID);
            managerNumber = managerDetails.getPersonNumber();

            if (managerDetails.getUserPersonDetails().get(0).getDisplayName() !=
                null) {
                aXBElement =
                        managerDetails.getUserPersonDetails().get(0).getDisplayName();
            }
        }else{
            nextOpr = ADFUtils.findOperation("getNextStepCEO");
            nextStep = (Row)nextOpr.execute();
            emailNotification = (String)nextStep.getAttribute("EmailNotification");
           // budgetValidCheck = (String)nextStep.getAttribute("BudgetValidation");
            stepType = nextStep.getAttribute("StepType")!=null?nextStep.getAttribute("StepType").toString():"";
            System.err.println("Submitted: stepType CEO is::"+stepType);
            totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
            
        }
        /*CEO - Hier setup commented by Moshina
         * Long lineManagerID = relationshipDetails.get(0).getManagerId();
        String department =
            relationshipDetails.get(0).getDepartmentName();
        UserDetails managerDetails =
            userService.getUserDetailsByPersonId(lineManagerID);
        String managerNumber = managerDetails.getPersonNumber();
        JAXBElement<String> aXBElement = null;
        if (managerDetails.getUserPersonDetails().get(0).getDisplayName() !=
            null) {
            aXBElement =
                    managerDetails.getUserPersonDetails().get(0).getDisplayName();
        }*/


        String tripType = ADFUtils.getBoundAttributeValue("TripType").toString();

        if (tripType.equals("Local") || tripType.equals("Inter")) {

            if (nextStep.getAttribute("NextAssignee").equals("Cost Center Manager")) {
                approvalDone = 1;
                String assigneeName ="";
                Object costCenter =
                    ADFUtils.getBoundAttributeValue("CostCenter");
                BiReportAccess BIRA = new BiReportAccess();
                String managerOfDeptNum = null;
                try {
                    List<Map> managerOfDeptList =
                        BIRA.getManagerOfDepartmentData(costCenter.toString());
                    managerOfDeptNum =
                            managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                     assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();  


                } catch (Exception e) {
                    e.printStackTrace();
                }
                String assignee =
                    ADFUtils.getBoundAttributeValue("Assignee").toString();

                //                if (assignee == managerOfDeptNum)
                //
                //                {
                //                    ADFUtils.setBoundAttributeValue("StepId", 4);
                //                    ADFUtils.setBoundAttributeValue("Assignee",
                //                                                    "HR Payroll and benefits Supervisor");
                //                    ADFUtils.setBoundAttributeValue("RequestStatus",
                //                                                    "WithdrawNeedApproval");
                //
                //                    ADFUtils.findOperation("Commit").execute();
                //                    JSFUtils.addFacesInformationMessage("Withdrawing Request has been approved");
                //
                //                    //                        ReportsUtils report = new ReportsUtils();
                //                    //                        ArrayList<String> personData =
                //                    //                        report.getPersonByPostionReport("Payroll Manager");
                //                    //                        if(personData.get(2)!=null)
                //                    //                        {
                //                    ////                        sendEmailByEmail(personData.get(2), tripRow);
                //                    //                        }
                //
                //                    return "back";
                //                } else {
                ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
                ADFUtils.setBoundAttributeValue("Assignee", managerOfDeptNum);
                ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
                ADFUtils.setBoundAttributeValue("RequestStatus",
                                                "Waiting Withdraw Approval");

                ADFUtils.findOperation("Commit").execute();
                String value =
                               ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                                      (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                      (Number)nextStep.getAttribute("StepId"),
                                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
                JSFUtils.addFacesInformationMessage("Withdrawing Request has been approved");
                if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                    sendEmail(managerOfDeptNum, tripRow);
//                    sendEmailByEmail("ibrahim23may@gmail.com", tripRow);
//                    sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
                }
                
                return "back";

                //                }

            }
        }

        else if (tripType.equals("Event") || tripType.equals("Training")) {

            if (nextStep.getAttribute("NextAssignee").equals("Cost Center Manager")) {
                approvalDone = 1;
                String assigneeName="";
                Object costCenter =
                    ADFUtils.getBoundAttributeValue("CostCenter");
                BiReportAccess BIRA = new BiReportAccess();
                String managerOfDeptNum = null;
                try {
                    List<Map> managerOfDeptList =
                        BIRA.getManagerOfDepartmentData(costCenter.toString());
                    managerOfDeptNum =
                            managerOfDeptList.get(0).get("PERSON_NUMBER").toString();
                    assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();  


                } catch (Exception e) {
                    e.printStackTrace();
                }
                String assignee =
                    ADFUtils.getBoundAttributeValue("Assignee").toString();

                //                if (assignee == managerOfDeptNum)
                //
                //                {
                //                    ADFUtils.setBoundAttributeValue("StepId", 4);
                //                    ADFUtils.setBoundAttributeValue("Assignee", "HR Manager");
                //                    ADFUtils.setBoundAttributeValue("RequestStatus",
                //                                                    "WithdrawNeedApproval");
                //
                //                    ADFUtils.findOperation("Commit").execute();
                //                    JSFUtils.addFacesInformationMessage("Withdrawing Request has been approved");
                //
                //                    //                        ReportsUtils report = new ReportsUtils();
                //                    //                        ArrayList<String> personData =
                //                    //                        report.getPersonByPostionReport("HR Manager");
                //                    //                        if(personData.get(2)!=null)
                //                    //                        {
                //                    ////                        sendEmailByEmail(personData.get(2), tripRow);
                //                    //                        }
                //
                //                    return "back";
                //                } else {
                ADFUtils.setBoundAttributeValue("StepId", nextStep.getAttribute("NextStepId"));
                ADFUtils.setBoundAttributeValue("Assignee", managerOfDeptNum);
                ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
                ADFUtils.setBoundAttributeValue("RequestStatus",
                                                "Waiting Withdraw Approval");

                ADFUtils.findOperation("Commit").execute();
                String value =
                               ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                                      (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                      (Number)nextStep.getAttribute("StepId"),
                                                                      (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                      (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
                JSFUtils.addFacesInformationMessage("Withdrawing Request has been approved");
                if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                sendEmail(managerOfDeptNum, tripRow);
//                    sendEmailByEmail("ibrahim23may@gmail.com", tripRow);
//                    sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
                }
                return "back";

                //                }

            }
        }


//        if (nextStep.getAttribute("NextAssignee").equals("HR Payroll and benefits Supervisor")) {
//            approvalDone = 1;
//            BiReportAccess report = new BiReportAccess();
//            //                ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//            try {
//                personData =
//                        report.getPersonByPostionReport("HR Payroll and benefits Supervisor");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
//                sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
////                    sendEmailByEmail("ibrahim23may@gmail.com", tripRow);
////                    sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
//                } 
//            } else {
//                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
//
//            }
//
//        }
//
//
//        if (nextStep.getAttribute("NextAssignee").equals("Financial Planning & Analysis Manager")) {
//            approvalDone = 1;
//            BiReportAccess report = new BiReportAccess();
//            //                ReportsUtils report = new ReportsUtils();
//            List<Map> personData = null;
//            try {
//                personData =
//                        report.getPersonByPostionReport("Financial Planning & Analysis Manager");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            if (personData.get(0).get("EMAIL_ADDRESS") != null) {
//                if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
//                sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
////                    sendEmailByEmail("ibrahim23may@gmail.com", tripRow);
////                    sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
//                }
//
//            } else {
//                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Financial Planning & Analysis Manager dosn't has email");
//
//            }
//
//        }


        if (nextStep.getAttribute("NextAssignee").equals("Executive Director, Shared Services Sector (Acting)")) {
            approvalDone = 1;
            BiReportAccess report = new BiReportAccess();

            List<Map> personData = null;
            try {
                personData =
                        report.getPersonByPostionReport("Executive Director, Shared Services Sector (Acting)");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (personData.get(0).get("DISPLAY_NAME") != null) {

                ADFUtils.setBoundAttributeValue("AssigneeName",
                                                personData.get(0).get("DISPLAY_NAME").toString());
            }
            if (personData.get(0).get("PERSON_NUMBER") != null) {
                ADFUtils.setBoundAttributeValue("Assignee",
                                                personData.get(0).get("PERSON_NUMBER").toString());
            }
            ADFUtils.setBoundAttributeValue("RequestStatus",
                                            "Waiting Withdraw Approval");
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId"));

            //Set Expense flag
            //            String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");

            ADFUtils.findOperation("Commit").execute();
            
            String value =
                           ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                                  (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                  (Number)nextStep.getAttribute("StepId"),
                                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");


                        if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                            if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                                sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
//                                sendEmailByEmail("ibrahim23may@gmail.com", tripRow);
//                                sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
                            }

                        } else {
                            JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Executive Director, Shared Services Sector (Acting) dosn't has email");
            
                        }

            return "back";

        }


        if (nextStep.getAttribute("NextAssignee").equals("Finished")) {

            String personNumber =
                (String)JSFUtils.resolveExpression("#{bindings.PersoneId.inputValue}");

            ADFUtils.setBoundAttributeValue("Assignee", personNumber);
            ADFUtils.setBoundAttributeValue("RequestStatus", "Withdrawn");
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId")); 
            ADFUtils.setBoundAttributeValue("AssigneeName", "");
            if (tripType.equalsIgnoreCase("Expense")) {
                ADFUtils.findOperation("changeExpenseFlagToNo").execute();
            }
            ADFUtils.findOperation("Commit").execute();
            //Send Email to employee
            //            Row tripRow =
            //                ADFUtils.findIteradtor("BusinessTripRequestViewIterator").getCurrentRow();
            if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                        sendEmailToPerson(personNumber, tripRow);
//                sendEmailByEmail("ibrahim23may@gmail.com", tripRow);
//                sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
            }
            approvalDone = 1;
            String value =
                           ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                                 (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                   (Number)nextStep.getAttribute("StepId"),
                                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","Y");
                        this.callBusinessTripWithdrawnProcess(emailNotification);
            JSFUtils.addFacesInformationMessage("Withdrawing Request has been approved");
            //            String expenseFlag =
            //                ADFUtils.getBoundAttributeValue("ExpenseFlag").toString();
            //            if (expenseFlag.equals("Y")) {
            //                //                    sendEmailToPayrollMgr( tripRow);
            //
            //            }

            return "back";

        }
        
                

        //For dynamic approval- If next assignee not from above, based on step type it will assign.
        String assigneeName = "";
        String assigneeNo = "";
        String assigneeEmail = "";
        
        if(approvalDone != 1){  
             // If step type is POSITION
                if("POSITION".equals(stepType)){
                    System.err.println("Approved, Assigning to : POSITION :"+nextStep.getAttribute("NextAssignee").toString());
                    BiReportAccess report = new BiReportAccess();
                    List<Map> personData = null;
                    try {
                        personData = report.getPersonByPostionReport(nextStep.getAttribute("NextAssignee").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (personData.get(0).get("DISPLAY_NAME") != null) {
                        assigneeName = personData.get(0).get("DISPLAY_NAME").toString();
                    }
                    if (personData.get(0).get("PERSON_NUMBER") != null) {
                        assigneeNo = personData.get(0).get("PERSON_NUMBER").toString();
                    }
                    if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                        assigneeEmail = personData.get(0).get("EMAIL_ADDRESS").toString();
                    }
                }
            // If step type is DEPARTMENT MANAGER
                if("DEPT_MANAGER".equals(stepType)){
                        System.err.println("Approved, Assigning to : : DEPT_MANAGER :"+ department);
                        BiReportAccess BIRA = new BiReportAccess();
                        try {
                            List<Map> managerOfDeptList =
                            BIRA.getManagerOfDepartmentData(department);
                            if (managerOfDeptList.size() > 0) {
                                if(managerOfDeptList.get(0).get("PERSON_NUMBER") != null){
                                    assigneeNo = managerOfDeptList.get(0).get("PERSON_NUMBER").toString();    
                                }
                                if(managerOfDeptList.get(0).get("DISPLAY_NAME") != null){
                                    assigneeName = managerOfDeptList.get(0).get("DISPLAY_NAME").toString();
                                }
                                if(managerOfDeptList.get(0).get("EMAIL_ADDRESS")!=null) {
                                    assigneeEmail = managerOfDeptList.get(0).get("EMAIL_ADDRESS").toString();
                                }
                            }
                            else
                            {
                                JSFUtils.addFacesErrorMessage("There is no manager for Employee Department so you can't submit the request");
                                return null;
                            }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                   }
            // If step type is LINE MANAGER
            if("LINE_MANAGER".equals(stepType)){
                    System.err.println("Approved, Assigning to : : LINE_MANAGER ");           
                    assigneeName = aXBElement.getValue();
                    assigneeNo = managerNumber;
                    assigneeEmail = "LINE";
            } 
            // If step type is USER
            if("USER".equals(stepType)){
                System.err.println("Approved, Assigning to : : USER ");
                ArrayList<String> personDetails = new ArrayList<String>();
                personDetails = getPersonDetails(nextStep.getAttribute("NextAssignee").toString());
                assigneeName = personDetails.get(0).toString();
                assigneeEmail = personDetails.get(1).toString();
                assigneeNo = nextStep.getAttribute("NextAssignee").toString(); 
            } 
            
            //Will be called for all above Step Type
            ADFUtils.setBoundAttributeValue("AssigneeName", assigneeName);
            ADFUtils.setBoundAttributeValue("StepId",
                                            nextStep.getAttribute("NextStepId")); 
            ADFUtils.findOperation("Commit").execute();
            String value =
                           ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                                  (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                                  (Number)nextStep.getAttribute("StepId"),
                                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),"APPROVE_ACT","N");
              
            if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
                if("LINE".equals(assigneeEmail)){
                    sendEmail(assigneeNo, tripRow);
                }else{
                    if (assigneeEmail != null) {
                        sendEmailByEmail(assigneeEmail, tripRow);
                    }else{
                        JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
                     }
                } 
            } 
        } 


        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        ADFUtils.setBoundAttributeValue("Assignee",
                                        nextStep.getAttribute("NextAssignee"));
        
        if("LINE_MANAGER".equals(stepType) || "DEPT_MANAGER".equals(stepType)){
                   ADFUtils.setBoundAttributeValue("Assignee", assigneeNo);
               } 

        ADFUtils.setBoundAttributeValue("RequestStatus",
                                        "Waiting Withdraw Approval");


        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Withdrawing Request has been approved");


        return "back";


    }


    public String rejectWithdraw() {
        
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String requestTripType =
            ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
                String TripTypeName=requestTripType +'-'+ personLocation;
                ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        
        String totalStep = nextStep.getAttribute("TotalStepForRequest")!=null?nextStep.getAttribute("TotalStepForRequest").toString():"";
        String emailNotification = (String)nextStep.getAttribute("EmailNotification");
        
        
        // Add event code here...
        ADFUtils.setBoundAttributeValue("ActionTaken", "Withdrawn Rejected");
        ADFUtils.setBoundAttributeValue("RequestStatus", "Withdrawn Rejected");
        ADFUtils.setBoundAttributeValue("StepId", totalStep);
        String personNumber =
            (String)JSFUtils.resolveExpression("#{bindings.PersoneId.inputValue}");
        ADFUtils.setBoundAttributeValue("Assignee", personNumber);
        ADFUtils.setBoundAttributeValue("AssigneeName", "");
        Row tripRow =
            ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow(); 
        String personNo = ADFUtils.getBoundAttributeValue("PersoneId").toString();
        ADFUtils.findOperation("Commit").execute();
        String value =
                    ApprovalHistory.executeHistoryPackageWithdraw((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                          (String)ADFUtils.getBoundAttributeValue("TripType"),
                                                         (Number)nextStep.getAttribute("StepId"),
                                                          new Long(0),
                                                          null,
                                                          "REJECT_ACT", "");
        if(emailNotification!=null && emailNotification.equalsIgnoreCase("Y")){
            if (personNo == null) {
                JSFUtils.addFacesErrorMessage("You donot have Person Email, So mail can't be sent to the person..");
            }else{
              sendEmailToPerson(personNo, tripRow);
            } 
        }
        JSFUtils.addFacesInformationMessage("Withdrawing Request has been rejected");
        return "back";
    }


    public void sendFYIEmailToPayrollManager(String from, String to,
                                             BusinessTripRequestViewRowImpl subject,
                                             String payrollManagerName) {

        if (to == null) {
           // to = "vf.khayal@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String[] arrofDates = null;
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();
        if (strDate != null && endDate != null) {
            arrofDates =
                    ApprovelLine.convertStartDateAndEndDate(strDate, endDate,
                                                            "yyyy-MM-dd",
                                                            "dd-MMM-yyyy");
        }
        String fromCity =
            subject.getNewCity() != null ? subject.getNewCity() : "";
        String toCity = subject.getToCity() != null ? subject.getToCity() : "";
        String meanTravel =
            subject.getMeansTravel() != null ? subject.getMeansTravel() : "";


        String PersoneName =
            subject.getPersoneName() != null ? subject.getPersoneName() : "";
        String PersonPosition =
            subject.getPersonPosition() != null ? subject.getPersonPosition() :
            "";
        String PersonJob =
            subject.getPersonJob() != null ? subject.getPersonJob() : "";
        String PersonDepartment =
            subject.getPersonDepartment() != null ? subject.getPersonDepartment() :
            "";
        String PersonLocation =
            subject.getPersonLocation() != null ? subject.getPersonLocation() :
            "";

        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        String returnMail=ApprovelLine.getMailDetails(subject);
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String benefitType = subject.getBenefitType();
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear Mr " + payrollManagerName + "," + "\n" +
            "<br/>" +
            "Kindly deduct the amount of the below Business Trip as it was withdrawn " +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                   " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                   "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                   subject.getPersoneId() + "</td></tr>\n" +
                   "    <tr><th>&nbsp;Name</th>\n" +
                   "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                   "    </tr><tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Position\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" +PersonPosition +
                   "</td>\n" +
                   "    </tr><tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Job\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                   "    </tr><tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Department\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" +PersonDepartment +
                   "</td>\n" +
                   "    </tr><tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Location\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" +PersonLocation +
                   "</td>\n" +
                   "    </tr>\n" +
                   "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
                   "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
                   bt + " Details</h2></td></tr>" + "<tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Trip Type\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
                   "    </tr>\n" +
                   "    <tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Way of Travel\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
                   "    </tr>\n" +
                   returnMail  +
                   "    </table>";

        String bodyPart13 =
                   "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                   "<tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;From City\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + fromCity + "</td>\n" +
                   "    </tr>\n" +
                   "    <tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;To City\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + toCity + "</td>\n" +
                   "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";
        System.err.println("e_body=======" + text1);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }


    public void sendEmailToSharedServiceDirector(String from, String to,
                                                 BusinessTripRequestViewRowImpl subject) {

        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String[] arrofDates=null;
        String strDate=subject.getStartDate().toString();
        String endDate=subject.getEndDate().toString();
              if(strDate!=null && endDate!=null)
              {
               arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
              }

        String fromCity=subject.getNewCity()!=null?subject.getNewCity():"";
        String toCity=subject.getToCity()!=null?subject.getToCity():"";
        String meanTravel=subject.getMeansTravel()!=null?subject.getMeansTravel():"";
        
        
        String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
        String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
        String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
        String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
        String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";


        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        String returnMail=ApprovelLine.getMailDetails(subject);
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String benefitType = subject.getBenefitType();
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear Executive Director, Shared Services Sector (Acting) ," +
            "\n" +
            "<br/>" + "Kindly find below a business trip request details  " +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";
        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
                    "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
                    bt + " Details</h2></td></tr>" + "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Trip Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Way of Travel\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
                    "    </tr>\n" +
                    returnMail  +
                    "    </table>";

        String bodyPart13 =
                   "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                   "<tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;From City\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + fromCity + "</td>\n" +
                   "    </tr>\n" +
                   "    <tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;To City\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + toCity + "</td>\n" +
                   "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";
        System.err.println("e_body=======" + text1);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }


    public void sendFYIEmailToPayrollManagerFinalApproval(String from,
                                                          String to,
                                                          BusinessTripRequestViewRowImpl subject,
                                                          String payrollManagerName) {


        //        Number totalAmount = null;
        //        Number laundryAmount = null;
        //        Number foodAmount = null;
        //        Number visaCostAmount = null;
        //        Number otherCostAmount = null;
        //        Number transportationAmount = null;
        //        Number housingAmount = null;
        //        Number travelCost = null;
        //        String wayOfTravel = "";
        //        if (subject.getMeansTravel() != null) {
        //            wayOfTravel = subject.getMeansTravel();
        //            if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
        //                wayOfTravel.equalsIgnoreCase("Train (Employee)") ||
        //                wayOfTravel.equalsIgnoreCase("Plane (Employee)")) {
        //
        //
        //                if (subject.getTravelCost() != null) {
        //
        //                    travelCost = subject.getTravelCost();
        //
        //                } else {
        //                    travelCost = new Number(0);
        //                }
        //                if (subject.getLaundryAllowanceAmount() != null) {
        //
        //                    laundryAmount = subject.getLaundryAllowanceAmount();
        //
        //                } else {
        //                    laundryAmount = new Number(0);
        //                }
        //                if (subject.getFoodAllowanceAmount() != null) {
        //
        //                    foodAmount = subject.getFoodAllowanceAmount();
        //
        //                } else {
        //                    foodAmount = new Number(0);
        //                }
        //                if (subject.getVisaCost() != null) {
        //
        //                    visaCostAmount = subject.getVisaCost();
        //
        //                } else {
        //                    visaCostAmount = new Number(0);
        //                }
        //                if (subject.getOtherCost() != null) {
        //
        //                    otherCostAmount = subject.getOtherCost();
        //
        //                } else {
        //                    otherCostAmount = new Number(0);
        //                }
        //
        //                if (subject.getTransAllowanceAmount() != null) {
        //
        //                    transportationAmount = subject.getTransAllowanceAmount();
        //
        //                } else {
        //                    transportationAmount = new Number(0);
        //                }
        //
        //                if (subject.getHousingAllowanceAmount() != null) {
        //
        //                    housingAmount = subject.getHousingAllowanceAmount();
        //
        //                } else {
        //                    housingAmount = new Number(0);
        //                }
        //                totalAmount =
        //                        laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount).add(travelCost);
        //
        //                if (subject.getHousingAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(housingAmount);
        //
        //                }
        //
        //                if (subject.getTransportationAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(transportationAmount);
        //
        //                }
        //
        //
        //            } else if (wayOfTravel.equalsIgnoreCase("Car (Company)") ||
        //                       wayOfTravel.equalsIgnoreCase("Train (Company)") ||
        //                       wayOfTravel.equalsIgnoreCase("Plane (Company)")) {
        //
        //
        //                if (subject.getLaundryAllowanceAmount() != null) {
        //
        //                    laundryAmount = subject.getLaundryAllowanceAmount();
        //
        //                } else {
        //                    laundryAmount = new Number(0);
        //                }
        //                if (subject.getFoodAllowanceAmount() != null) {
        //
        //                    foodAmount = subject.getFoodAllowanceAmount();
        //
        //                } else {
        //                    foodAmount = new Number(0);
        //                }
        //                if (subject.getVisaCost() != null) {
        //
        //                    visaCostAmount = subject.getVisaCost();
        //
        //                } else {
        //                    visaCostAmount = new Number(0);
        //                }
        //                if (subject.getOtherCost() != null) {
        //
        //                    otherCostAmount = subject.getOtherCost();
        //
        //                } else {
        //                    otherCostAmount = new Number(0);
        //                }
        //
        //                if (subject.getTransAllowanceAmount() != null) {
        //
        //                    transportationAmount = subject.getTransAllowanceAmount();
        //
        //                } else {
        //                    transportationAmount = new Number(0);
        //                }
        //
        //                if (subject.getHousingAllowanceAmount() != null) {
        //
        //                    housingAmount = subject.getHousingAllowanceAmount();
        //
        //                } else {
        //                    housingAmount = new Number(0);
        //                }
        //                totalAmount =
        //                        laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount);
        //
        //                if (subject.getHousingAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(housingAmount);
        //
        //                }
        //
        //                if (subject.getTransportationAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(transportationAmount);
        //
        //                }
        //
        //
        //            }
        //        }


        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();
        String[] arrofDates=null;
               if(strDate!=null && endDate!=null)
               {
                arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
               }


                               String fromCity=subject.getNewCity()!=null?subject.getNewCity():"";
                               String toCity=subject.getToCity()!=null?subject.getToCity():"";
                               String meanTravel=subject.getMeansTravel()!=null?subject.getMeansTravel():"";
                               
                               
                               String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
                               String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                               String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                               String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                               String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";


        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String returnMail=ApprovelLine.getMailDetails(subject);
        String benefitType = subject.getBenefitType();
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "Dear Mr " + payrollManagerName + "," + "\n" +
            "<br/>" +
            "Kindly be informed that the following Business trip request has been approved and ready to be expensed " +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";


        String bodyPart12 =
        "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
        " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
        bt + " Details</h2></td></tr>" + "<tr>\n" +
        "      <th>\n" +
        "        &nbsp;Trip Type\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <th>\n" +
        "        &nbsp;Way of Travel\n" +
        "      </th>\n" +
        "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
        "    </tr>\n" +
        returnMail  +
        "    </table>";
        
        String bodyPart13 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;From City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + fromCity + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;To City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + toCity + "</td>\n" +
                    "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";
        System.err.println("e_body=======" + text1);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }


    public void sendExpenseEmail(String from, String to,
                                 BusinessTripRequestViewRowImpl subject) {


        //        Number totalAmount = null;
        //        Number laundryAmount = null;
        //        Number foodAmount = null;
        //        Number visaCostAmount = null;
        //        Number otherCostAmount = null;
        //        Number transportationAmount = null;
        //        Number housingAmount = null;
        //        Number travelCost = null;
        //        String wayOfTravel = "";
        //        if (subject.getMeansTravel() != null) {
        //            wayOfTravel = subject.getMeansTravel();
        //            if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
        //                wayOfTravel.equalsIgnoreCase("Train (Employee)") ||
        //                wayOfTravel.equalsIgnoreCase("Plane (Employee)")) {
        //
        //
        //                if (subject.getTravelCost() != null) {
        //
        //                    travelCost = subject.getTravelCost();
        //
        //                } else {
        //                    travelCost = new Number(0);
        //                }
        //                if (subject.getLaundryAllowanceAmount() != null) {
        //
        //                    laundryAmount = subject.getLaundryAllowanceAmount();
        //
        //                } else {
        //                    laundryAmount = new Number(0);
        //                }
        //                if (subject.getFoodAllowanceAmount() != null) {
        //
        //                    foodAmount = subject.getFoodAllowanceAmount();
        //
        //                } else {
        //                    foodAmount = new Number(0);
        //                }
        //                if (subject.getVisaCost() != null) {
        //
        //                    visaCostAmount = subject.getVisaCost();
        //
        //                } else {
        //                    visaCostAmount = new Number(0);
        //                }
        //                if (subject.getOtherCost() != null) {
        //
        //                    otherCostAmount = subject.getOtherCost();
        //
        //                } else {
        //                    otherCostAmount = new Number(0);
        //                }
        //
        //                if (subject.getTransAllowanceAmount() != null) {
        //
        //                    transportationAmount = subject.getTransAllowanceAmount();
        //
        //                } else {
        //                    transportationAmount = new Number(0);
        //                }
        //
        //                if (subject.getHousingAllowanceAmount() != null) {
        //
        //                    housingAmount = subject.getHousingAllowanceAmount();
        //
        //                } else {
        //                    housingAmount = new Number(0);
        //                }
        //                totalAmount =
        //                        laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount).add(travelCost);
        //
        //                if (subject.getHousingAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(housingAmount);
        //
        //                }
        //
        //                if (subject.getTransportationAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(transportationAmount);
        //
        //                }
        //
        //
        //            } else if (wayOfTravel.equalsIgnoreCase("Car (Company)") ||
        //                       wayOfTravel.equalsIgnoreCase("Train (Company)") ||
        //                       wayOfTravel.equalsIgnoreCase("Plane (Company)")) {
        //
        //
        //                if (subject.getLaundryAllowanceAmount() != null) {
        //
        //                    laundryAmount = subject.getLaundryAllowanceAmount();
        //
        //                } else {
        //                    laundryAmount = new Number(0);
        //                }
        //                if (subject.getFoodAllowanceAmount() != null) {
        //
        //                    foodAmount = subject.getFoodAllowanceAmount();
        //
        //                } else {
        //                    foodAmount = new Number(0);
        //                }
        //                if (subject.getVisaCost() != null) {
        //
        //                    visaCostAmount = subject.getVisaCost();
        //
        //                } else {
        //                    visaCostAmount = new Number(0);
        //                }
        //                if (subject.getOtherCost() != null) {
        //
        //                    otherCostAmount = subject.getOtherCost();
        //
        //                } else {
        //                    otherCostAmount = new Number(0);
        //                }
        //
        //                if (subject.getTransAllowanceAmount() != null) {
        //
        //                    transportationAmount = subject.getTransAllowanceAmount();
        //
        //                } else {
        //                    transportationAmount = new Number(0);
        //                }
        //
        //                if (subject.getHousingAllowanceAmount() != null) {
        //
        //                    housingAmount = subject.getHousingAllowanceAmount();
        //
        //                } else {
        //                    housingAmount = new Number(0);
        //                }
        //                totalAmount =
        //                        laundryAmount.add(foodAmount).add(visaCostAmount).add(otherCostAmount);
        //
        //                if (subject.getHousingAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(housingAmount);
        //
        //                }
        //
        //                if (subject.getTransportationAllowanceType().toString().equalsIgnoreCase("Emp")) {
        //                    totalAmount = totalAmount.add(transportationAmount);
        //
        //                }
        //
        //
        //            }
        //        }


        if (to == null) {
           // to = "vf.khayal@gmail.com";
           JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        

       
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();

        String[] arrofDates=null;
                if(strDate!=null && endDate!=null)
                {
                 arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
                }

        String fromCity=subject.getNewCity()!=null?subject.getNewCity():"";
                                String toCity=subject.getToCity()!=null?subject.getToCity():"";
                                String meanTravel=subject.getMeansTravel()!=null?subject.getMeansTravel():"";
                                
                                
                                String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
                                String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                                String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                                String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                                String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";
        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String returnMail=ApprovelLine.getMailDetails(subject);
        String benefitType = subject.getBenefitType();
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "\n" +
            "<br/>" +
            "Kindly make an expense of the amount of the below request to this employee " +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
                   "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
                   bt + " Details</h2></td></tr>" + "<tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Trip Type\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
                   "    </tr>\n" +
                   "    <tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;Way of Travel\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
                   "    </tr>\n" +
                   returnMail  +
                   "    </table>";

        String bodyPart13 =
                   "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                   "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                   "<tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;From City\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + fromCity + "</td>\n" +
                   "    </tr>\n" +
                   "    <tr>\n" +
                   "      <th>\n" +
                   "        &nbsp;To City\n" +
                   "      </th>\n" +
                   "      <td width=\"50%\">" + toCity + "</td>\n" +
                   "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";
        System.err.println("e_body=======" + text1);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }


    public long calcDays(oracle.jbo.domain.Date startDate,
                         oracle.jbo.domain.Date endDate) {
        java.sql.Date startSqldate = startDate.dateValue();
        java.util.Date convertedDateStartDate =
            convertedDateStartDate = new java.util.Date(startSqldate.getTime());
        java.sql.Date endSqldate = endDate.dateValue();
        java.util.Date convertedDateEndDate =
            convertedDateEndDate = new java.util.Date(endSqldate.getTime());
        long diffInMillies =
            Math.abs(convertedDateEndDate.getTime() - convertedDateStartDate.getTime());
        long diff =
            TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        //        if(diff == 0){
        //            return 1;
        //        }
        return diff + 1;
    }


    public void recalcAllowances(Number days) {
        // Add event code here...
        //        Number daysNewValue = (Number)valueChangeEvent.getNewValue();
        //        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        String transAllowByWho =
            (String)ADFUtils.getBoundAttributeValue("TransportationAllowanceType");
        String housAllowByWho =
            (String)ADFUtils.getBoundAttributeValue("HousingAllowanceType");
        String foodAllowByWho =
            (String)ADFUtils.getBoundAttributeValue("FoodAllowanceType");
        if (transAllowByWho.equals("Emp")) {

            String allowanceTripType = null;
            String destinationType =
                (String)ADFUtils.getBoundAttributeValue("DestinationType");
            String localOrInternational =
                (String)ADFUtils.getBoundAttributeValue("SubType");
            if (localOrInternational.equalsIgnoreCase("local"))

            {
                allowanceTripType = "Local";

            }

            else {

                if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                    allowanceTripType = "Inter";

                } else {
                    allowanceTripType = "Inter2";

                }

            }

            OperationBinding calcOpr =
                ADFUtils.findOperation("calcAllowances");
            Map map = calcOpr.getParamsMap();
            map.put("allowanceType", "TRANS");
            map.put("tripType", allowanceTripType);
            map.put("days", days);
            //            tripType
            if (getActionReason() != null) {
                map.put("classType", getActionReason());
                calcOpr.execute();
            } else {
                JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
            }


        }
        if (housAllowByWho.equals("Emp")) {

            String allowanceTripType = null;
            String destinationType =
                (String)ADFUtils.getBoundAttributeValue("DestinationType");
            String localOrInternational =
                (String)ADFUtils.getBoundAttributeValue("SubType");
            if (localOrInternational.equalsIgnoreCase("local"))

            {
                allowanceTripType = "Local";

            }

            else {

                if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                    allowanceTripType = "Inter";

                } else {
                    allowanceTripType = "Inter2";

                }

            }

            OperationBinding calcOpr =
                ADFUtils.findOperation("calcAllowances");
            Map map = calcOpr.getParamsMap();
            map.put("allowanceType", "HOUSING");
            map.put("tripType", allowanceTripType);
            map.put("days", days);
            if (getActionReason() != null) {
                map.put("classType", getActionReason());
                calcOpr.execute();
            } else {
                JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
            }


        }


        if (foodAllowByWho.equals("Emp"))

        {
            String allowanceTripType = null;
            String destinationType =
                (String)ADFUtils.getBoundAttributeValue("DestinationType");
            String localOrInternational =
                (String)ADFUtils.getBoundAttributeValue("SubType");
            if (localOrInternational.equalsIgnoreCase("local"))

            {
                allowanceTripType = "Local";

            }

            else {

                if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                    allowanceTripType = "Inter";

                } else {
                    allowanceTripType = "Inter2";

                }

            }

            OperationBinding calcOpr =
                ADFUtils.findOperation("calcAllowances");
            Map map = calcOpr.getParamsMap();
            map.put("allowanceType", "FOOD");
            map.put("tripType", allowanceTripType);
            map.put("days", days);
            if (getActionReason() != null) {
                map.put("classType", getActionReason());
                calcOpr.execute();
            } else {
                JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
            }

        }


    }

    public void calcDaysByStartDate(ValueChangeEvent valueChangeEvent) {
        System.err.println("calcDaysByStartDate");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        Boolean oneWayTrip =
            (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
        String newFromCity =
            (String)ADFUtils.getBoundAttributeValue("NewCity");
        String newtoCity = (String)ADFUtils.getBoundAttributeValue("ToCity");
        String newFromCityLOV =
            (String)ADFUtils.getBoundAttributeValue("NewCity1");
        String newtoCityLOV =
            (String)ADFUtils.getBoundAttributeValue("ToCity1");
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        oracle.jbo.domain.Date newStartDateValue =
            (oracle.jbo.domain.Date)valueChangeEvent.getNewValue();
        oracle.jbo.domain.Date endDate =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("EndDate");
        System.err.println("newStartDateValue======>" + newStartDateValue +
                           "endDate==>" + endDate);
        if (newStartDateValue != null && endDate != null)

        {
            System.err.println("ValueChangeListener");
            if (newStartDateValue.compareTo(endDate) == 0 ||
                newStartDateValue.compareTo(endDate) < 0) {
                Number days = new Number(calcDays(newStartDateValue, endDate));
                ADFUtils.setBoundAttributeValue("Days", days);
                if (benefitType.equalsIgnoreCase("A")) {
                    recalcAllowances(days);
                    calcFoodAllowance();
                } else if (benefitType.equalsIgnoreCase("P")) {
                    Map perdiemMap =
                        (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
                    String subType =
                        (String)ADFUtils.getBoundAttributeValue("SubType");
                    Number perdiem = null;
                    try {
                        if (tripType.equalsIgnoreCase("Local") ||
                            tripType.equalsIgnoreCase("Inter")) {
                            //                        if(newFromCity!=null && newFromCity.equalsIgnoreCase("Riyadh") && newtoCity!=null && newtoCity.equalsIgnoreCase("Riyadh")) {
                            //                            perdiem = new Number(0);
                            //                        }
                            if (oneWayTrip != null &&
                                oneWayTrip.equals(true)) {
                                perdiem = new Number(0);
                            } else {
                                if (newFromCityLOV != null &&
                                    newFromCityLOV.equalsIgnoreCase("Riyadh") &&
                                    newtoCityLOV != null &&
                                    newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                                    System.err.println("Riyadh");
                                    perdiem = new Number(0);
                                } else {
                                    perdiem =
                                            new Number(perdiemMap.get(tripType));
                                }

                            }

                        }
                        if (tripType.equalsIgnoreCase("Event") ||
                            tripType.equalsIgnoreCase("Training")) {
                            if (newFromCityLOV != null &&
                                newFromCityLOV.equalsIgnoreCase("Riyadh") &&
                                newtoCityLOV != null &&
                                newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                                perdiem = new Number(0);
                            }

                            else {
                                if (subType.equalsIgnoreCase("Local")) {
                                    System.err.println("subtye=======" +
                                                       subType);
                                    System.err.println("oneWay=======" +
                                                       oneWayTrip);
                                    if (oneWayTrip != null &&
                                        oneWayTrip.equals(true)) {
                                        perdiem = new Number(0);
                                    } else {
                                        perdiem =
                                                new Number(perdiemMap.get("Local"));
                                    }

                                }

                            }
                            if (subType.equalsIgnoreCase("International")) {
                                if (newFromCity != null &&
                                    newFromCity.equalsIgnoreCase("Riyadh") &&
                                    newtoCity != null &&
                                    newtoCity.equalsIgnoreCase("Riyadh")) {
                                    perdiem = new Number(0);
                                } else {
                                    perdiem =
                                            new Number(perdiemMap.get("Inter"));
                                }

                            }
                        }
                        calcInternationalPerDiem();
                        days =
ADFUtils.getBoundAttributeValue("Days") != null ?
new Number(ADFUtils.getBoundAttributeValue("Days").toString()) : new Number(0);
                        perdiem = perdiem.multiply(days);
                        ADFUtils.setBoundAttributeValue("PerDiemAmount",
                                                        perdiem);
                        calcPerDiem();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void calcDaysByEndDate(ValueChangeEvent valueChangeEvent) {
        System.err.println("calcDaysByStartDate");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        Boolean oneWayTrip =
            (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
        String newFromCity =
            (String)ADFUtils.getBoundAttributeValue("NewCity");
        String newtoCity = (String)ADFUtils.getBoundAttributeValue("ToCity");
        String newFromCityLOV =
            (String)ADFUtils.getBoundAttributeValue("NewCity1");
        String newtoCityLOV =
            (String)ADFUtils.getBoundAttributeValue("ToCity1");
        //NewCity//ToCity
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        oracle.jbo.domain.Date newEndDateValue =
            (oracle.jbo.domain.Date)valueChangeEvent.getNewValue();
        oracle.jbo.domain.Date startDate =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("StartDate");
        if (newEndDateValue != null && startDate != null)

        {
            if (startDate.compareTo(newEndDateValue) == 0 ||
                startDate.compareTo(newEndDateValue) < 0) {
                Number days = new Number(calcDays(startDate, newEndDateValue));
                ADFUtils.setBoundAttributeValue("Days", days);
                if (benefitType.equalsIgnoreCase("A")) {
                    recalcAllowances(days);
                    calcFoodAllowance();
                } else if (benefitType.equalsIgnoreCase("P")) {
                    Map perdiemMap =
                        (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
                    String subType =
                        (String)ADFUtils.getBoundAttributeValue("SubType");
                    Number perdiem = null;
                    try {
                        if (tripType.equalsIgnoreCase("Local") ||
                            tripType.equalsIgnoreCase("Inter")) {
                            //                        if(newFromCity!=null && newFromCity.equalsIgnoreCase("Riyadh") && newtoCity!=null && newtoCity.equalsIgnoreCase("Riyadh")) {
                            //                            perdiem = new Number(0);
                            //                        }

                            if (oneWayTrip != null &&
                                oneWayTrip.equals(true)) {
                                perdiem = new Number(0);
                            } else {
                                if (newFromCityLOV != null &&
                                    newFromCityLOV.equalsIgnoreCase("Riyadh") &&
                                    newtoCityLOV != null &&
                                    newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                                    perdiem = new Number(0);
                                } else {
                                    perdiem =
                                            new Number(perdiemMap.get(tripType));
                                }

                            }

                        }
                        if (tripType.equalsIgnoreCase("Event") ||
                            tripType.equalsIgnoreCase("Training")) {
                            if (newFromCityLOV != null &&
                                newFromCityLOV.equalsIgnoreCase("Riyadh") &&
                                newtoCityLOV != null &&
                                newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                                perdiem = new Number(0);
                            }

                            else {
                                if (subType.equalsIgnoreCase("Local")) {
                                    System.err.println("subtye=======" +
                                                       subType);
                                    System.err.println("oneWay=======" +
                                                       oneWayTrip);
                                    if (oneWayTrip != null &&
                                        oneWayTrip.equals(true)) {
                                        perdiem = new Number(0);
                                    } else {
                                        perdiem =
                                                new Number(perdiemMap.get("Local"));
                                    }

                                }
                            }
                            if (subType.equalsIgnoreCase("International")) {
                                if (newFromCity != null &&
                                    newFromCity.equalsIgnoreCase("Riyadh") &&
                                    newtoCity != null &&
                                    newtoCity.equalsIgnoreCase("Riyadh")) {
                                    perdiem = new Number(0);
                                } else {
                                    perdiem =
                                            new Number(perdiemMap.get("Inter"));
                                }

                            }
                        }

                        calcInternationalPerDiem();
                        days =
ADFUtils.getBoundAttributeValue("Days") != null ?
new Number(ADFUtils.getBoundAttributeValue("Days").toString()) : new Number(0);
                        perdiem = perdiem.multiply(days);
                        ADFUtils.setBoundAttributeValue("PerDiemAmount",
                                                        perdiem);
                        calcPerDiem();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void perdiemInternational() {
        System.err.println("Called==========>");
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        Map perdiemMap =
            (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
        String subType = (String)ADFUtils.getBoundAttributeValue("SubType");
        String newFromCity =
            (String)ADFUtils.getBoundAttributeValue("NewCity");
        String newtoCity = (String)ADFUtils.getBoundAttributeValue("ToCity");
        String newFromCityLOV =
            (String)ADFUtils.getBoundAttributeValue("NewCity1");
        String newtoCityLOV =
            (String)ADFUtils.getBoundAttributeValue("ToCity1");
        Number perdiem = null;
        Number days = null;
        try {
            if (tripType.equalsIgnoreCase("Local") ||
                tripType.equalsIgnoreCase("Inter")) {
                if (newFromCity != null &&
                    newFromCity.equalsIgnoreCase("Riyadh") &&
                    newtoCity != null &&
                    newtoCity.equalsIgnoreCase("Riyadh")) {
                    perdiem = new Number(0);
                } else {
                    perdiem = new Number(perdiemMap.get(tripType));
                }

            }
            if (tripType.equalsIgnoreCase("Event") ||
                tripType.equalsIgnoreCase("Training")) {
                if (subType.equalsIgnoreCase("Local")) {
                    if (newFromCityLOV != null &&
                        newFromCityLOV.equalsIgnoreCase("Riyadh") &&
                        newtoCityLOV != null &&
                        newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                        perdiem = new Number(0);
                    } else {
                        perdiem = new Number(perdiemMap.get("Local"));
                    }

                }
                if (subType.equalsIgnoreCase("International")) {
                    //                    if(newFromCity!=null && newFromCity.equalsIgnoreCase("Riyadh") && newtoCity!=null && newtoCity.equalsIgnoreCase("Riyadh")) {
                    //                     perdiem = new Number(0);
                    //                    }
                    //                    else {
                    perdiem = new Number(perdiemMap.get("Inter"));
                    //                    }
                }
            }
            days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
            perdiem = perdiem.multiply(days);
            ADFUtils.setBoundAttributeValue("PerDiemAmount", perdiem);
            calcPerDiem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calcInternationalPerDiem() {
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        String subType = (String)ADFUtils.getBoundAttributeValue("SubType");
        String destinationType =
            (String)ADFUtils.getBoundAttributeValue("DestinationType");
        oracle.jbo.domain.Date startDate =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("StartDate");
        oracle.jbo.domain.Date endDate =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("EndDate");
        oracle.jbo.domain.Date actualStartDate =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("ActualStartDate");
        oracle.jbo.domain.Date actualEndDate =
            (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("ActualEndDate");
        if (tripType != null && subType != null && actualStartDate != null &&
            actualEndDate != null &&
            (tripType.equalsIgnoreCase("Inter") || (tripType.equalsIgnoreCase("Event") &&
                                                    subType.equalsIgnoreCase("International")) ||
             (tripType.equalsIgnoreCase("Training") &&
              subType.equalsIgnoreCase("International")))) {
            if (destinationType != null &&
                destinationType.equalsIgnoreCase("Two American Continents, and (Japan, South Korea, Singapore, China, Australia, New Zealand,and Malaysia)")) {
                startDate = adjustDate("SUB", actualStartDate, 1);
                endDate = adjustDate("ADD", actualEndDate, 2);
                System.out.println("new start date:" + startDate);
                System.out.println("new end date:" + endDate);
                ADFUtils.setBoundAttributeValue("StartDate", startDate);
                ADFUtils.setBoundAttributeValue("EndDate", endDate);
                Number days = null;
                try {
                    days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                days = new Number(calcDays(startDate, endDate));
                ADFUtils.setBoundAttributeValue("Days", days);
                AdfFacesContext.getCurrentInstance().addPartialTarget(startdatebinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(enddatebinding);
                perdiemInternational();
                JSFUtils.addFacesInformationMessage("For International Trip for the selected Destination type Start date will be 1 day prior & End date will be 2 days after");
            } else if (destinationType != null &&
                       destinationType.equalsIgnoreCase("GCC Countries, Africa ,Europe, other Arab Countries, and Asian States Except (Japan, South Korea, Singapore, China, Australia, New Zealand,and Malaysia)")) {
                startDate = adjustDate("SUB", actualStartDate, 1);
                endDate = adjustDate("ADD", actualEndDate, 1);
                System.out.println("new start date:" + startDate);
                System.out.println("new end date:" + endDate);
                ADFUtils.setBoundAttributeValue("StartDate", startDate);
                ADFUtils.setBoundAttributeValue("EndDate", endDate);
                Number days = null;
                try {
                    days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                days = new Number(calcDays(startDate, endDate));
                ADFUtils.setBoundAttributeValue("Days", days);
                AdfFacesContext.getCurrentInstance().addPartialTarget(startdatebinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(enddatebinding);
                perdiemInternational();
                JSFUtils.addFacesInformationMessage("For International Trip for the selected Destination type Start date will be 1 day prior & End date will be 1 days after");
            }
        }
    }

    static final long MILI_SECONDS_PER_DAY = 86400000;

    public oracle.jbo.domain.Date adjustDate(String action,
                                             oracle.jbo.domain.Date date,
                                             int noOfDays) {
        Timestamp ts = date.timestampValue();
        long nextDatesSecs = 0;
        if (action.equalsIgnoreCase("ADD")) {
            nextDatesSecs = ts.getTime() + (MILI_SECONDS_PER_DAY * noOfDays);
        } else {
            nextDatesSecs = ts.getTime() - (MILI_SECONDS_PER_DAY * noOfDays);
        }
        return new oracle.jbo.domain.Date(new Timestamp(nextDatesSecs));
    }

    public void calcPerDiem() {
        System.err.println("calcPerDiem=========>");
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        String meansTravel =
            (String)ADFUtils.getBoundAttributeValue("MeansTravel");
        Number perdiem = null;
        Number days = null;
        if (ADFUtils.getBoundAttributeValue("Days") != null) {
            days = new Number(0);
            try {
                days =
new Number(ADFUtils.getBoundAttributeValue("Days").toString());

                if (ADFUtils.getBoundAttributeValue("PerDiemAmount") != null) {
                    perdiem =
                            new Number(ADFUtils.getBoundAttributeValue("PerDiemAmount").toString());
                } else {
                    perdiem = new Number(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Number otherCost;
            Number travelCost;
            Number amountDue;
            Number btCost;
            Number eventCost;
            Number trainingCost;
            Number visaCost;
            Number otherCostByEmployee;
            Number otherCostByTatweer;
            Number trainingCostByEmployee;
            Number travelCostByEmployee;
            
            if (ADFUtils.getBoundAttributeValue("OtherCost") != null) {
                otherCost =
                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
            } else {
                otherCost = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("TravelCost") != null) {
                travelCost =
                        (Number)ADFUtils.getBoundAttributeValue("TravelCost");
            } else {
                travelCost = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("EventCost") != null) {
                eventCost =
                        (Number)ADFUtils.getBoundAttributeValue("EventCost");
            } else {
                eventCost = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("TrainingCost") != null) {
                trainingCost =
                        (Number)ADFUtils.getBoundAttributeValue("TrainingCost");
            } else {
                trainingCost = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("VisaCost") != null) {
                visaCost = (Number)ADFUtils.getBoundAttributeValue("VisaCost");
            } else {
                visaCost = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("OtherByEmployee") != null) {
                otherCostByEmployee = (Number)ADFUtils.getBoundAttributeValue("OtherByEmployee");
            } else {
                otherCostByEmployee = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("OtherByTatweer") != null) {
                otherCostByTatweer = (Number)ADFUtils.getBoundAttributeValue("OtherByTatweer");
            } else {
                otherCostByTatweer = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("TrainingByEmployee") != null) {
                trainingCostByEmployee = (Number)ADFUtils.getBoundAttributeValue("TrainingByEmployee");
            } else {
                trainingCostByEmployee = new Number(0);
            }
            if (ADFUtils.getBoundAttributeValue("TravelCostByEmployee") != null) {
                travelCostByEmployee = (Number)ADFUtils.getBoundAttributeValue("TravelCostByEmployee");
            } else {
                travelCostByEmployee = new Number(0);
            }
            try {


                if (tripType != null && meansTravel != null) {
                    if ((tripType.equalsIgnoreCase("Local") ||
                         tripType.equalsIgnoreCase("Inter"))) {
                        if (meansTravel.equalsIgnoreCase("Car (Employee)") ||
                            meansTravel.equalsIgnoreCase("Train (Employee)") ||
                            meansTravel.equalsIgnoreCase("Plane (Employee)")) {
                            amountDue = perdiem.add(travelCostByEmployee).add(otherCostByEmployee);
                        } else {
                            amountDue = perdiem.add(otherCostByEmployee);
                        }
                        btCost =
                        perdiem.add(travelCost).add(visaCost).add(otherCostByEmployee).add(otherCostByTatweer).add(travelCostByEmployee);
                        ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                        amountDue);
                        ADFUtils.setBoundAttributeValue("TotalAmount", btCost);
                    }
                    if (tripType.equalsIgnoreCase("Event")) {
                        if (meansTravel.equalsIgnoreCase("Car (Employee)") ||
                            meansTravel.equalsIgnoreCase("Train (Employee)") ||
                            meansTravel.equalsIgnoreCase("Plane (Employee)")) {
                            amountDue = perdiem.add(otherCostByEmployee).add(travelCostByEmployee);
                        } else {
                            amountDue = perdiem.add(otherCostByEmployee);
                        }
                        btCost =
                        perdiem.add(travelCost).add(visaCost).add(otherCostByEmployee).add(otherCostByTatweer).add(travelCostByEmployee);
                        ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                        amountDue);
                        ADFUtils.setBoundAttributeValue("TotalAmount", btCost);
                    }
                    if (tripType.equalsIgnoreCase("Training")) {
                        if (meansTravel.equalsIgnoreCase("Car (Employee)") ||
                            meansTravel.equalsIgnoreCase("Train (Employee)") ||
                            meansTravel.equalsIgnoreCase("Plane (Employee)")) {
                            amountDue = perdiem.add(travelCostByEmployee).add(otherCostByEmployee).add(trainingCostByEmployee);
                        } else {
                            amountDue = perdiem.add(otherCostByEmployee).add(trainingCostByEmployee);
                        }
                        btCost =
                                perdiem.add(travelCost).add(trainingCost).add(visaCost).add(otherCostByEmployee).add(trainingCostByEmployee).add(otherCostByTatweer).add(travelCostByEmployee);
                        ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                        amountDue);
                        ADFUtils.setBoundAttributeValue("TotalAmount", btCost);
                    }
                    if (tripType.equalsIgnoreCase("Expense")) {
                        if (meansTravel.equalsIgnoreCase("Car (Employee)") ||
                            meansTravel.equalsIgnoreCase("Train (Employee)") ||
                            meansTravel.equalsIgnoreCase("Plane (Employee)")) {
                            amountDue = perdiem.add(otherCost).add(travelCost);
                        } else {
                            amountDue = perdiem.add(otherCost);
                        }
                        btCost =
                                perdiem.add(otherCost).add(travelCost).add(eventCost).add(visaCost).add(trainingCost);
                        ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                        amountDue);
                        ADFUtils.setBoundAttributeValue("TotalAmount", btCost);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setCarValueInWayOfTravel(RichSelectItem carValueInWayOfTravel) {
        this.carValueInWayOfTravel = carValueInWayOfTravel;
    }

    public RichSelectItem getCarValueInWayOfTravel() {
        return carValueInWayOfTravel;
    }


    public void setAllowByCompany(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        // Add event code here...
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        if (benefitType != null && benefitType.equalsIgnoreCase("A")) {
            transAllowanceType.setValue("Comp");
            transAllowanceValue.setValue(null);
            housingAllowanceType.setValue("Comp");
            housingAllowanceValue.setValue(null);
            foodAllowanceType.setValue("Comp");
            foodAllowanceValue.setValue(null);
        }
        calcInternationalPerDiem();
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        Map perdiemMap =
            (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
        String subType = (String)ADFUtils.getBoundAttributeValue("SubType");
        Number perdiem = null;
        try {
            if (tripType.equalsIgnoreCase("Local") ||
                tripType.equalsIgnoreCase("Inter")) {
                perdiem = new Number(perdiemMap.get(tripType));
            }
            if (tripType.equalsIgnoreCase("Event") ||
                tripType.equalsIgnoreCase("Training")) {
                if (subType.equalsIgnoreCase("Local")) {
                    perdiem = new Number(perdiemMap.get("Local"));
                }
                if (subType.equalsIgnoreCase("International")) {
                    perdiem = new Number(perdiemMap.get("Inter"));
                }
            }

            Number days =
                ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
                new Number(0);
            perdiem = perdiem.multiply(days);
            ADFUtils.setBoundAttributeValue("PerDiemAmount", perdiem);
            calcPerDiem();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setHousingAllowanceType(RichSelectOneChoice housingAllowanceType) {
        this.housingAllowanceType = housingAllowanceType;
    }

    public RichSelectOneChoice getHousingAllowanceType() {
        return housingAllowanceType;
    }

    public void setHousingAllowanceValue(RichInputText housingAllowanceValue) {
        this.housingAllowanceValue = housingAllowanceValue;
    }

    public RichInputText getHousingAllowanceValue() {
        return housingAllowanceValue;
    }

    public void setFoodAllowanceType(RichSelectOneChoice foodAllowanceType) {
        this.foodAllowanceType = foodAllowanceType;
    }

    public RichSelectOneChoice getFoodAllowanceType() {
        return foodAllowanceType;
    }

    public void setFoodAllowanceValue(RichInputText foodAllowanceValue) {
        this.foodAllowanceValue = foodAllowanceValue;
    }

    public RichInputText getFoodAllowanceValue() {
        return foodAllowanceValue;
    }

    public void setRequiredVisa(RichSelectBooleanCheckbox requiredVisa) {
        this.requiredVisa = requiredVisa;
    }

    public RichSelectBooleanCheckbox getRequiredVisa() {
        return requiredVisa;
    }

    public void requiredVisaChecked(ValueChangeEvent valueChangeEvent) {
        // Add event code here...

        if (requiredVisa.isSelected())

        {
            JSFUtils.addFacesInformationMessage("You must attach the files required to visa to avoid request rejection as visa is required");


        }

    }

    public void setTripComment(RichInputText tripComment) {
        this.tripComment = tripComment;
    }

    public RichInputText getTripComment() {
        return tripComment;
    }

    public void setTripCommentValue(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        if (valueChangeEvent.getNewValue() != null) {
            String stepCommentNewValue =
                valueChangeEvent.getNewValue().toString();
            tripComment.setValue(stepCommentNewValue);
        }

    }

    public void setOtherCities(RichPanelLabelAndMessage otherCities) {
        this.otherCities = otherCities;
    }

    public RichPanelLabelAndMessage getOtherCities() {
        return otherCities;
    }

    public void showOrHideOtherCities(ValueChangeEvent valueChangeEvent) {
        // Add event code here...

        if (valueChangeEvent.getNewValue().equals(true))

        {
            JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
                                        true);
            //            otherCities.setVisible(true);

        }

        else {
            JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
                                        false);
            //            otherCities.setVisible(false);

        }
    }

    public void calcFoodAllowance() {
        // Add event code here...
        foodAllowanceType.setValue("Emp");

        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        if (tripType.equalsIgnoreCase("Expense")) {
            return;
        }

        String allowanceTripType = null;
        String destinationType =
            (String)ADFUtils.getBoundAttributeValue("DestinationType");
        String localOrInternational =
            (String)ADFUtils.getBoundAttributeValue("SubType");
        if (localOrInternational.equalsIgnoreCase("local"))

        {
            allowanceTripType = "Local";

        }

        else {

            if (destinationType == null) {

                JSFUtils.addFacesErrorMessage("You Should Select Destination Type");
                return;
            }

            if (destinationType.equalsIgnoreCase("GCC countries, Africa, Other Arab Countries, and Asian States except (Japan, South Korea, Singapore and China)")) {
                allowanceTripType = "Inter";

            } else {
                allowanceTripType = "Inter2";

            }

        }

        OperationBinding calcOpr = ADFUtils.findOperation("calcAllowances");
        Map map = calcOpr.getParamsMap();
        map.put("allowanceType", "FOOD");

        map.put("tripType", allowanceTripType);


        String assignee = (String)ADFUtils.getBoundAttributeValue("Assignee");

        if (assignee != null) {
            if (assignee.equals("HR Payroll and benefits Supervisor")) {
                String reqEmpNo =
                    (String)ADFUtils.getBoundAttributeValue("PersoneId");
                //           Payroll Manager
                BiReportAccess report = new BiReportAccess();
                String actionReasoneReport = null;
                try {
                    actionReasoneReport =
                            report.callActionReasoneReport(reqEmpNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (actionReasoneReport != null) {
                    map.put("classType", actionReasoneReport);
                    calcOpr.execute();
                } else {
                    JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
                }
            }
        } else {
            if (getActionReason() != null) {
                map.put("classType", getActionReason());
                calcOpr.execute();
            } else {
                JSFUtils.addFacesErrorMessage("Action Reason Value for this Person Deos not exist");
            }

        }
        //                }

    }

    public void setAttachmentsRowCount(RichOutputText attachmentsRowCount) {
        this.attachmentsRowCount = attachmentsRowCount;
    }

    public RichOutputText getAttachmentsRowCount() {
        return attachmentsRowCount;
    }

    public void setMoreCitesCheckBox(RichSelectBooleanCheckbox moreCitesCheckBox) {
        this.moreCitesCheckBox = moreCitesCheckBox;
    }

    public RichSelectBooleanCheckbox getMoreCitesCheckBox() {
        return moreCitesCheckBox;
    }

    public String makeMoreCitesVisible() {
        JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", null);
        ViewObject vo = ADFUtils.findIterator("BusinessTripRequestViewIterator").getViewObject();
        Row r = vo.getCurrentRow();
        if(r.getAttribute("RequestStatus") != null && ("APPROVED".equals(r.getAttribute("RequestStatus")) || "Withdrawn Rejected".equals(r.getAttribute("RequestStatus")) || "Withdrawn".equals(r.getAttribute("RequestStatus")))){
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", "N");  
        }else{
            String personLocation = (String)r.getAttribute("PersonLocation") !=null?(String)r.getAttribute("PersonLocation"):""; //2023-PSC change
            if(r.getAttribute("RequestStatus") != null && ("SAVED".equals(r.getAttribute("RequestStatus")))){
                 personLocation = (String)JSFUtils.resolveExpression("#{PersonInfo.location}") !=null?(String)JSFUtils.resolveExpression("#{PersonInfo.location}"):"";//2023-PSC change
            }
            String requestTripType =r.getAttribute("TripType").toString() !=null?r.getAttribute("TripType").toString():"";
                                                    
            String TripTypeName=requestTripType +'-'+ personLocation;
            ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
            OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
            Row nextStep = (Row)nextOpr.execute();
            String specialEdit = nextStep.getAttribute("SpecialEdit")!=null? (String)nextStep.getAttribute("SpecialEdit"):"N";
            JSFUtils.setExpressionValue("#{pageFlowScope.SpecialEdit}", specialEdit);  
        } 
        
        JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
                                    true);

        return "edit";
    }

    public String calcAmountDue() {
        // Add event code here...
        String wayOfTravel = null;
        if (ADFUtils.getBoundAttributeValue("MeansTravel") != null) {
            wayOfTravel =
                    ADFUtils.getBoundAttributeValue("MeansTravel").toString();
        }
        if (wayOfTravel.equalsIgnoreCase("Car (Employee)") ||
            wayOfTravel.equalsIgnoreCase("Train (Employee)") ||
            wayOfTravel.equalsIgnoreCase("Plane (Employee)")) {

            Number travelCost = null;
            if (ADFUtils.getBoundAttributeValue("TravelCost") != null) {

                travelCost =
                        (Number)ADFUtils.getBoundAttributeValue("TravelCost");
            }
            Number otherCostAmount = null;

            if (ADFUtils.getBoundAttributeValue("OtherCost") != null) {
                otherCostAmount =
                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
            } else {
                otherCostAmount = new Number(0);

            }
            Number totalForInvoice = otherCostAmount.add(travelCost);

            ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                            totalForInvoice);
            ADFUtils.findOperation("Commit").execute();
        } else {

            Number otherCostAmount = null;

            if (ADFUtils.getBoundAttributeValue("OtherCost") != null) {
                otherCostAmount =
                        (Number)ADFUtils.getBoundAttributeValue("OtherCost");
            } else {
                otherCostAmount = new Number(0);

            }


            ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                            otherCostAmount);
            ADFUtils.findOperation("Commit").execute();

        }

        return null;
    }

    //    public static void main(String[] args) {
    //
    //        BusinessTrip bt=new BusinessTrip();
    //
    ////        bt.getCodeCombinationId("1474","7001001");
    //                    UserServiceUtil userService = new UserServiceUtil();
    //            UserDetails userDetails = null;
    //
    //            userDetails =
    //                    userService.getUserDetailsByPersonNumber("1134");
    //            List<UserWorkRelationshipDetails> relationshipDetails =
    //                userDetails.getUserWorkRelationshipDetails();
    //            System.err.println("1) '"+relationshipDetails.get(0).getPersonTypeId().toString()+"'");
    //            System.err.println("2) "+relationshipDetails.get(0).getSystemPersonType());
    //            System.err.println("3) '"+relationshipDetails.get(0).getUserPersonType()+"'");
    //
    //    }

    public String checkSession() {
        String personNumber =
            (String)JSFUtils.resolveExpression("#{PersonInfo.perosnNumber}");
        String assignee =
            (String)JSFUtils.resolveExpression("#{PersonInfo.assignee}");
        if (personNumber == null || assignee == null) {
            FilmStripBean.showPopupMessage("Session Expired! Please open the application through SAAS!");
        }
        return "success";
    }

    public void visaCostVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (benefitType.equalsIgnoreCase("P")) {
            calcPerDiem();
        }
    }

    public void otherCostVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (benefitType.equalsIgnoreCase("P")) {
            calcPerDiem();
        }
    }

    public void trainingCostVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (benefitType.equalsIgnoreCase("P")) {
            calcPerDiem();
        }
    }

    public void travelCostVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (benefitType.equalsIgnoreCase("P")) {
            calcPerDiem();
        }
    }

    public void eventCostVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (benefitType.equalsIgnoreCase("P")) {
            calcPerDiem();
        }
    }

    public void subTypeVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...

        String subType = (String)valueChangeEvent.getNewValue();
        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
        Boolean oneWayTrip =
            (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
        String newFromCityLOV =
            (String)ADFUtils.getBoundAttributeValue("NewCity1");
        String newtoCityLOV =
            (String)ADFUtils.getBoundAttributeValue("ToCity1");
        String newFromCity =
            (String)ADFUtils.getBoundAttributeValue("NewCity");
        String newtoCity = (String)ADFUtils.getBoundAttributeValue("ToCity");
        String benefitType =
            (String)ADFUtils.getBoundAttributeValue("BenefitType");
        if (benefitType.equalsIgnoreCase("P")) {
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
            try {
                Map perdiemMap =
                    (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
                Number days =
                    ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
                    new Number(0);
                Number perdiem = new Number(0);
                if (tripType.equalsIgnoreCase("Local") ||
                    tripType.equalsIgnoreCase("Inter")) {
                    if (newFromCity != null &&
                        newFromCity.equalsIgnoreCase("Riyadh") &&
                        newtoCity != null &&
                        newtoCity.equalsIgnoreCase("Riyadh")) {
                        perdiem = new Number(0);
                    } else {
                        perdiem = new Number(perdiemMap.get(tripType));
                    }

                }
                if (tripType.equalsIgnoreCase("Event") ||
                    tripType.equalsIgnoreCase("Training")) {
                    if (subType != null) {
                        clearDateForSubType();
                        if (subType.equalsIgnoreCase("Local")) {

                            if (newFromCityLOV != null &&
                                newFromCityLOV.equalsIgnoreCase("Riyadh") &&
                                newtoCityLOV != null &&
                                newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                                perdiem = new Number(0);
                            }
                            if (oneWayTrip != null &&
                                oneWayTrip.equals(true)) {
                                perdiem = new Number(0);
                            } else {
                                perdiem = new Number(perdiemMap.get("Local"));
                            }

                        }
                        if (subType.equalsIgnoreCase("International")) {
                            clearDateForSubType();
                            if (newFromCity != null &&
                                newFromCity.equalsIgnoreCase("Riyadh") &&
                                newtoCity != null &&
                                newtoCity.equalsIgnoreCase("Riyadh")) {
                                perdiem = new Number(0);
                            } else {
                                perdiem = new Number(perdiemMap.get("Inter"));
                            }

                        }
                    }
                }
                calcInternationalPerDiem();
                days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
                perdiem = perdiem.multiply(days);
                ADFUtils.setBoundAttributeValue("PerDiemAmount", perdiem);
            } catch (SQLException e) {
                e.printStackTrace();

            }
            calcPerDiem();
        }
    }

    public void setStartdatebinding(RichInputDate startdatebinding) {
        this.startdatebinding = startdatebinding;
    }

    public RichInputDate getStartdatebinding() {
        return startdatebinding;
    }

    public void setEnddatebinding(RichInputDate enddatebinding) {
        this.enddatebinding = enddatebinding;
    }

    public RichInputDate getEnddatebinding() {
        return enddatebinding;
    }

    public void sendFyiMailForInformationExpensed(String email,
                                                  Row subject) {
//        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendExpenseForEmployeeEmail("OFOQ.HR@TATWEER.SA", email,
                                        (BusinessTripRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }

    }

    public void sendFyiMailForInformationWithoutExpensed(String email,
                                                         Row subject) {
//        String email = getEmail(personNumber);
        System.out.println("Manager Email is " + email);
        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendMailWithoutExpenseForEmployeeEmail("OFOQ.HR@TATWEER.SA", email,
                                                   (BusinessTripRequestViewRowImpl)subject);
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    }

    public void sendExpenseForEmployeeEmail(String from, String to,
                                            BusinessTripRequestViewRowImpl subject) {

        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        

       
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();

        String[] arrofDates=null;
               if(strDate!=null && endDate!=null)
               {
                arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
               }


                               String fromCity=subject.getNewCity()!=null?subject.getNewCity():"";
                               String toCity=subject.getToCity()!=null?subject.getToCity():"";
                               String meanTravel=subject.getMeansTravel()!=null?subject.getMeansTravel():"";
                               
                               
                               String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
                               String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                               String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                               String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                               String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";

        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        String benefitType = subject.getBenefitType();
        String returnMail=ApprovelLine.getMailDetails(subject);
        //String toPart = "Dear Sir," + "<br/><br/>";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "\n" +
            "<br/>" +
            "Kindly be informed that the following Expense request has been approved" +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
                  "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                  " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
                  bt + " Details</h2></td></tr>" + "<tr>\n" +
                  "      <th>\n" +
                  "        &nbsp;Trip Type\n" +
                  "      </th>\n" +
                  "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
                  "    </tr>\n" +
                  "    <tr>\n" +
                  "      <th>\n" +
                  "        &nbsp;Way of Travel\n" +
                  "      </th>\n" +
                  "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
                  "    </tr>\n" +
                  returnMail  +
                  "    </table>";

        String bodyPart13 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;From City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + fromCity + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;To City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + toCity + "</td>\n" +
                    "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }

        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            ApprovalPart1 + thankYouPart + signaturePart + "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";
        System.err.println("e_body=======" + text1);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }

    public void sendMailWithoutExpenseForEmployeeEmail(String from, String to,
                                                       BusinessTripRequestViewRowImpl subject) {

        if (to == null) {
            //to = "vf.khayal@gmail.com";
            JSFUtils.addFacesErrorMessage("Email Address is not updated! please contact HR Administrator!");

        }
        String benefitType = subject.getBenefitType();
       
        String strDate = subject.getStartDate().toString();
        String endDate = subject.getEndDate().toString();

        
        String[] arrofDates=null;
               if(strDate!=null && endDate!=null)
               {
                arrofDates= ApprovelLine.convertStartDateAndEndDate(strDate,endDate,"yyyy-MM-dd","dd-MMM-yyyy");
               }


                               String fromCity=subject.getNewCity()!=null?subject.getNewCity():"";
                               String toCity=subject.getToCity()!=null?subject.getToCity():"";
                               String meanTravel=subject.getMeansTravel()!=null?subject.getMeansTravel():"";
                               
                               
                               String PersoneName=subject.getPersoneName()!=null?subject.getPersoneName():"";
                               String PersonPosition=subject.getPersonPosition()!=null?subject.getPersonPosition():"";
                               String PersonJob=subject.getPersonJob()!=null?subject.getPersonJob():"";
                               String PersonDepartment=subject.getPersonDepartment()!=null?subject.getPersonDepartment():"";
                               String PersonLocation=subject.getPersonLocation()!=null?subject.getPersonLocation():"";

        String bt =
            subject.getTripType().equalsIgnoreCase("Local") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Inter") ? "Business Trip" :
            subject.getTripType().equalsIgnoreCase("Event") ? " Event" :
            subject.getTripType().equalsIgnoreCase("Training") ? " Training" :
            subject.getTripType().equalsIgnoreCase("Expense") ? "Expense" :
            "Business Trip";
        //String toPart = "Dear Sir," + "<br/><br/>";
        String othercities = "";
        if (subject.getOtherCities() != null) {
            othercities = subject.getOtherCities();
        } else {
            othercities = "";
        }
        //String toPart = "Dear Sir," + "<br/><br/>";
        String returnMail=ApprovelLine.getMailDetails(subject);
        String tocity= subject.getToCity()!=null?subject.getToCity():"";
        String bodyPart =
            "<p align=\"center\" style=\"text-align:center\">\n" +
            "    <span style='font-size:16.0pt;line-height:107%;font-family:\"Times New Roman\",serif;'>" +
            "\n" +
            "<br/>" +
            "Kindly be informed that the following Business trip request has been approved " +
            "  </span></p>\n" +
            "  <p>&nbsp;</p>";

        String bodyPart10 =

                    " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Personal Information</h2></td></tr>" +
                    "<tr><th>&nbsp;Person Number </th> <td width=\"50%\">" +
                    subject.getPersoneId() + "</td></tr>\n" +
                    "    <tr><th>&nbsp;Name</th>\n" +
                    "      <td width=\"50%\">" + PersoneName + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Position\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonPosition +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Job\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + PersonJob + "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Department\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonDepartment +
                    "</td>\n" +
                    "    </tr><tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Location\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" +PersonLocation +
                    "</td>\n" +
                    "    </tr>\n" +
                    "  </table>";

        String bodyPart11 = "<p>&nbsp;</p>";

        String bodyPart12 =
                    "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    " <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">" +
                    bt + " Details</h2></td></tr>" + "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Trip Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTripType() + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Way of Travel\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + meanTravel+ "</td>\n" +
                    "    </tr>\n" +
                    returnMail  +
                    "    </table>";

        String bodyPart13 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Destination</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;From City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + fromCity + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;To City\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + toCity + "</td>\n" +
                    "    </tr>\n";
        String moreCitiesEmail = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;More Cities\n" +
            "      </th>\n" +
            "<td width=\"50%\">" + othercities + "</td>\n" +
            "    </tr>";
        String tableEnd = "</table>";
        if (othercities.equalsIgnoreCase("")) {
            bodyPart13 = bodyPart13 + tableEnd;
        } else {
            bodyPart13 = bodyPart13 + moreCitiesEmail + tableEnd;
        }

        String bodyPartDuration =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Duration</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Start Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[0].toString() + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <th>\n" +
            "        &nbsp;End Date\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + arrofDates[1].toString() + "</td>\n" +
            "    </tr>\n" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Duration\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getDays() + "</td>\n" +
            "    </tr>" + "</table>";
        String bodyCostCenter =
            "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "   <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\" ><h2 style=\"color:white;\">Cost Center</h2></td></tr>" +
            "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenter() + "</td>\n" +
            "    </tr><tr>\n" +
            "      <th>\n" +
            "        &nbsp;Cost Center Number\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + subject.getCostCenterNumber() +
            "</td>\n" +
            "    </tr>\n" +
            "    </table>";
        String bodyPart14 = null;

        if (benefitType.equalsIgnoreCase("A")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>" + "<br>";
        } else if (benefitType.equalsIgnoreCase("P")) {
            bodyPart14 =
                    "<p>&nbsp;</p><table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                    "       <tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"><h2 style=\"color:white;\">Cost</h2></td></tr>" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Per-Diem Amount\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getPerDiemAmount() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Invoice Amount Due\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subject.getTotalForInvoice() +
                    "</td>\n" +
                    "    </tr>\n" +
                    "   </table>";
        }
        String verticalSpace = " <p>&nbsp;</p>";
        String ApprovalPart1 =
            ApprovelLine.approvalLine("ApprovalHistoryBusinessIterator");
        String thankYouPart = "<br/><b>Thanks " + "</b><br/><br/>";
        String signaturePart =
            "This message sent by " + "<b>" + " https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
            "<b/>" + "<br/>";

        //        String text =
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyCostCenter;
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";

        String text1 =
            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +
            bodyPartDuration + bodyPart14 + bodyCostCenter + "<br>" +
            verticalSpace + ApprovalPart1 + thankYouPart + signaturePart +
            "</p>";
        //            bodyPart + bodyPart10 + bodyPart11 + bodyPart12 + bodyPart13 +bodyPart14
        //            bodyPart14 + thankYouPart + signaturePart + "</p>";
        System.err.println("e_body=======" + text1);
        OperationBinding sendMail =
            ADFUtils.findOperation("callSendEmailStoredPL");
        sendMail.getParamsMap().put("sender", from);
        sendMail.getParamsMap().put("receiver", to);

        sendMail.getParamsMap().put("subject",
                                    "Busniess Trip Request " + subject.getRequestStatus());
        sendMail.getParamsMap().put("e_body", text1);
        sendMail.execute();
    }

    public void subType() {
        String tripType =
            (String)com.sbm.selfServices.view.utils.JSFUtils.resolveExpression("#{sessionScope.tripType}");
        ViewObject vo =
            ADFUtils.getApplicationModuleForDataControl("SBMModuleDataControl").findViewObject("BusinessTripRequestView");
        Row row = vo.getCurrentRow();
        if (row != null) {
            if (tripType.equalsIgnoreCase("inter")) {
                row.setAttribute("SubType", "International");

            }

            else if (tripType.equalsIgnoreCase("local")) {
                row.setAttribute("SubType", "Local");
            }

            if (tripType.equalsIgnoreCase("Expense") == true) {
                com.sbm.selfServices.view.utils.JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
                                                                            true);

            }
            if (tripType != "Expense") {
                row.setAttribute("TransAllowanceAmount", null);
                row.setAttribute("HousingAllowanceAmount", null);
                row.setAttribute("FoodAllowanceAmount", null);
                com.sbm.selfServices.view.utils.JSFUtils.setExpressionValue("#{pageFlowScope.moreCitiesCheckBox}",
                                                                            false);

            }
        }
    }

    public void onChangeActualStartDate(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (valueChangeEvent.getNewValue() != null) {
            oracle.jbo.domain.Date actualStartDate =
                (oracle.jbo.domain.Date)valueChangeEvent.getNewValue();
            oracle.jbo.domain.Date actualEndDate =
                (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("ActualEndDate");
            if (actualStartDate != null && actualEndDate != null) {
                if (actualStartDate.compareTo(actualEndDate) == 0 ||
                    actualStartDate.compareTo(actualEndDate) < 0) {
                    calcInternationalPerDiem();
                }
            }

        }

    }

    public void onChangeActualEndDate(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (valueChangeEvent.getNewValue() != null) {
            oracle.jbo.domain.Date actualEndDate =
                (oracle.jbo.domain.Date)valueChangeEvent.getNewValue();
            oracle.jbo.domain.Date actualStartDate =
                (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("ActualStartDate");
            if (actualStartDate != null && actualEndDate != null) {
                if (actualStartDate.compareTo(actualEndDate) == 0 ||
                    actualStartDate.compareTo(actualEndDate) < 0) {
                    calcInternationalPerDiem();
                }
            }
        }

    }

    public void onChangeFromCityLOV(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        try {
            String tripType =
                (String)ADFUtils.getBoundAttributeValue("TripType");
            oracle.jbo.domain.Date startDate =
                (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("StartDate");
            oracle.jbo.domain.Date EndDate =
                (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("EndDate");
            String subtype =
                (String)ADFUtils.getBoundAttributeValue("SubType");
            Boolean oneWayTrip =
                (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
            String newtoCityLOV =
                (String)ADFUtils.getBoundAttributeValue("ToCity1");
            Number days =
                ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
                new Number(0);
            Number perdiem = new Number(0);
            Map perdiemMap =
                (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
            String fromCity = (String)valueChangeEvent.getNewValue();
            System.err.println("fromCity=====" + fromCity +
                               "newtoCityLOV=====>" + newtoCityLOV);
            if (fromCity != null && newtoCityLOV != null) {
                if (tripType.equalsIgnoreCase("Local") ||
                    tripType.equalsIgnoreCase("Training") ||
                    tripType.equalsIgnoreCase("Event")) {
                    if (subtype != null && subtype.equalsIgnoreCase("Local")) {
                        if (fromCity.equalsIgnoreCase("Riyadh") &&
                            newtoCityLOV.equalsIgnoreCase("Riyadh")) {
                            perdiem = new Number(0);
                            ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                            new Number(0));
                            ADFUtils.setBoundAttributeValue("TotalAmount",
                                                            new Number(0));
                            System.err.println("Called");
                        }


                    }


                }

            }
            days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
            perdiem = perdiem.multiply(days);
            ADFUtils.setBoundAttributeValue("PerDiemAmount", perdiem);
            perdiemInternational();
        }


        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onChangeToCityLov(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        try {
            System.err.println("Called3333=========>");
            String tripType =
                (String)ADFUtils.getBoundAttributeValue("TripType");
            oracle.jbo.domain.Date startDate =
                (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("StartDate");
            oracle.jbo.domain.Date EndDate =
                (oracle.jbo.domain.Date)ADFUtils.getBoundAttributeValue("EndDate");
            String subtype =
                (String)ADFUtils.getBoundAttributeValue("SubType");
            Boolean oneWayTrip =
                (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
            String newFromCityLOV =
                (String)ADFUtils.getBoundAttributeValue("NewCity1");

            Number days =
                ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
                new Number(0);
            Number perdiem = new Number(0);
            Map perdiemMap =
                (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
            String toCity = (String)valueChangeEvent.getNewValue();
            if (toCity != null && newFromCityLOV != null) {

                System.err.println("Inside22");
                if (tripType.equalsIgnoreCase("Local") ||
                    tripType.equalsIgnoreCase("Training") ||
                    tripType.equalsIgnoreCase("Event")) {
                    if (subtype != null && subtype.equalsIgnoreCase("Local")) {
                        if (toCity.equalsIgnoreCase("Riyadh") &&
                            newFromCityLOV.equalsIgnoreCase("Riyadh")) {
                            perdiem = new Number(0);
                            ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                            new Number(0));
                            ADFUtils.setBoundAttributeValue("TotalAmount",
                                                            new Number(0));
                            System.err.println("Called");
                        }


                    }

                }

            }
            days =
ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
new Number(0);
            perdiem = perdiem.multiply(days);
            ADFUtils.setBoundAttributeValue("PerDiemAmount", perdiem);
            perdiemInternational();
            //            AdfFacesContext.getCurrentInstance().addPartialTarget(arg0);
            //            AdfFacesContext.getCurrentInstance().addPartialTarget(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    public void changeCityPerDiemCalc() {
    //        System.err.println("ChangeValue");
    //
    //        String tripType = (String)ADFUtils.getBoundAttributeValue("TripType");
    //         oracle.jbo.domain.Date  startDate = ( oracle.jbo.domain.Date )ADFUtils.getBoundAttributeValue("StartDate");
    //         oracle.jbo.domain.Date  EndDate = ( oracle.jbo.domain.Date )ADFUtils.getBoundAttributeValue("EndDate");
    //        String subtype = (String)ADFUtils.getBoundAttributeValue("SubType");
    //        Boolean oneWayTrip = (Boolean)ADFUtils.getBoundAttributeValue("OneWay_TRANS");
    //        String newFromCityLOV = (String)ADFUtils.getBoundAttributeValue("NewCity1");
    //        String newtoCityLOV = (String)ADFUtils.getBoundAttributeValue("ToCity1");
    //        Number days =
    //            ADFUtils.getBoundAttributeValue("Days") != null ? new Number(ADFUtils.getBoundAttributeValue("Days").toString()) :
    //            new Number(0);
    //        Number perdiem = new Number(0);
    //        Map perdiemMap =
    //            (Map)JSFUtils.resolveExpression("#{sessionScope.perDiem}");
    //
    //
    //        }
    //        catch(Exception e) {
    //            e.printStackTrace();
    //        }
    //
    //    }

    public void onChangeOneWay(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        if (valueChangeEvent.getNewValue() != null) {
            Boolean checkBox = (Boolean)valueChangeEvent.getNewValue();
            //            System.err.println("checkBox========"+checkBox+"=======>"+ADFUtils.getBoundAttributeValue("NewCity1"));
            if (checkBox.equals(true) &&
                ADFUtils.getBoundAttributeValue("NewCity1") != null) {
                ADFUtils.setBoundAttributeValue("PerDiemAmount",
                                                new Number(0));
                ADFUtils.setBoundAttributeValue("TotalAmount", new Number(0));
                ADFUtils.setBoundAttributeValue("TotalForInvoice",
                                                new Number(0));
                ADFUtils.setBoundAttributeValue("ToCity1", "");
            } else {
                System.err.println("Else");
                if (ADFUtils.getBoundAttributeValue("ToCity1") != null) {
                    perdiemInternational();
                }


            }
        }

    }

    public void clearDateForSubType() {
        //        if(ADFUtils.getBoundAttributeValue("SubType").toString().equalsIgnoreCase("International"))
        //        {
        ADFUtils.setBoundAttributeValue("NewCity", "");
        ADFUtils.setBoundAttributeValue("ToCity", "");
        ADFUtils.setBoundAttributeValue("StartDate", "");
        ADFUtils.setBoundAttributeValue("EndDate", "");
        ADFUtils.setBoundAttributeValue("Days", new Number(0));
        ADFUtils.setBoundAttributeValue("PerDiemAmount", new Number(0));
        ADFUtils.setBoundAttributeValue("TrainingCost", new Number(0));
        ADFUtils.setBoundAttributeValue("TotalAmount", new Number(0));
        ADFUtils.setBoundAttributeValue("TotalForInvoice", new Number(0));
        //        Boolean checkBox = (Boolean)ADFUtils.getBoundAttributeValue("More_Cities_Check_Trans");
        if (ADFUtils.getBoundAttributeValue("SubType").toString().equalsIgnoreCase("Local") &&
            ADFUtils.getBoundAttributeValue("OtherCitiesTrans") != null) {
            ADFUtils.setBoundAttributeValue("More_Cities_Check_Trans",
                                            Boolean.FALSE);
            ADFUtils.setBoundAttributeValue("OneWay_TRANS", Boolean.FALSE);
            otherCitiesibinding.setValue("");
            System.err.println("Local");

            AdfFacesContext.getCurrentInstance().addPartialTarget(otherCitiesibinding);
            AdfFacesContext.getCurrentInstance().addPartialTarget(moreCitesCheckBox);
        } else {
            if (ADFUtils.getBoundAttributeValue("SubType").toString().equalsIgnoreCase("International") &&
                ADFUtils.getBoundAttributeValue("OtherCities") != null) {
                ADFUtils.setBoundAttributeValue("More_Cities_Check_Trans",
                                                Boolean.FALSE);
                otherCitiesDb.setValue("");
                System.err.println("Inernational");
                AdfFacesContext.getCurrentInstance().addPartialTarget(moreCitesCheckBox);
                AdfFacesContext.getCurrentInstance().addPartialTarget(otherCitiesDb);
            }

        }

        //  }
        //        else {
        //            System.err.println("Local");
        //        }
    }


    public void setSelCities(List<Object> selCities) {
        System.out.println("selected cities:" + selCities);
        String otherCitiesTemp = null;
        if (selCities != null) {
            otherCitiesTemp =
                    selCities.toString().substring(1, selCities.toString().length() -
                                                   1);
        }
        System.out.println("other cities:" + otherCitiesTemp);
        ADFUtils.setBoundAttributeValue("OtherCities", otherCitiesTemp);
        this.selCities = selCities;
    }

    public List<Object> getSelCities() {
        String othercities =
            (String)ADFUtils.getBoundAttributeValue("OtherCities");
        System.out.println("othercities:" + othercities);
        Object[] othercitiesArray = null;
        if (othercities != null && !othercities.equalsIgnoreCase("")) {
            othercitiesArray = othercities.split(", ");
            System.out.println("length:" + othercitiesArray.length);
            for (int i = 0; i < othercitiesArray.length; i++) {
                System.out.println("---" + othercitiesArray[i]);
            }
            return Arrays.asList(othercitiesArray);
        }
        return selCities;
    }


    public void setOtherCitiesibinding(RichSelectManyChoice otherCitiesibinding) {
        this.otherCitiesibinding = otherCitiesibinding;
    }

    public RichSelectManyChoice getOtherCitiesibinding() {
        return otherCitiesibinding;
    }

    public void onChangeMoreCities(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Boolean value = (Boolean)valueChangeEvent.getNewValue();
        System.err.println("value==>" + value);
        if (valueChangeEvent.getNewValue() != null &&
            ADFUtils.getBoundAttributeValue("OtherCitiesTrans") != null) {

            System.err.println("Called");
            otherCitiesibinding.setValue("");

            AdfFacesContext.getCurrentInstance().addPartialTarget(otherCitiesibinding);

        } else {
            if (ADFUtils.getBoundAttributeValue("SubType").toString().equalsIgnoreCase("International") &&
                ADFUtils.getBoundAttributeValue("OtherCities") != null) {
                otherCitiesDb.setValue("");

                AdfFacesContext.getCurrentInstance().addPartialTarget(otherCitiesDb);

            }

        }
    }

    public void setOtherCitiesDb(RichInputText otherCitiesDb) {
        this.otherCitiesDb = otherCitiesDb;
    }

    public RichInputText getOtherCitiesDb() {
        return otherCitiesDb;
    }
    //    public String approvalLine() {
    //       String ApprovalPart = "";
    //        try {
    //
    //            ViewObject vo =
    //                ADFUtils.findIterator("ApprovalHistoryBusinessIterator").getViewObject();
    //            vo.executeQuery();
    //            System.err.println("Count===>" + vo.getEstimatedRowCount());
    //            RowSetIterator rs = vo.createRowSetIterator(null);
    //            oracle.jbo.domain.Date strDate = null;
    //            oracle.jbo.domain.Date endDate = null;
    //            String endDate2 = null;
    //            String strDate1 = null;
    //            while (rs.hasNext()) {
    //               System.err.println("While====>"+rs.getRowCount());
    //                Date date1 = null;
    //                Date date2 = null;
    //                Row r = rs.next();
    //                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //                try {
    //                    strDate =
    //                            (oracle.jbo.domain.Date)r.getAttribute("SubmitttedOn");
    //                    endDate =
    //                            (oracle.jbo.domain.Date)r.getAttribute("ApprovalOn");
    //                    date1 = dateFormat.parse(strDate.toString());
    //                    if (endDate != null) {
    //                        date2 = dateFormat.parse(endDate.toString());
    //                    }
    //
    //                    dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    //                    strDate1 = dateFormat.format(date1);
    //
    //                    if (date2 != null) {
    //                        endDate2 = dateFormat.format(date2);
    //                    }
    //                } catch (ParseException e) {
    //                    e.printStackTrace();
    //                }
    //
    //                String value=endDate2!=null?endDate2.toString():"";
    //
    //                ApprovalPart =ApprovalPart+ "<tr>\n" +
    //                        "    <td>" + r.getAttribute("AssigneeName") +
    //                        "</td>\n" +
    //                        "    <td>" + r.getAttribute("ActionTaken") +
    //                        "</td>\n" +
    //                        "    <td>" + strDate1 + "</td>\n" +
    //                              "    <td>" + value + "</td>\n" +
    //                        "  </tr>";
    //                //         }
    //            }
    //            vo.executeQuery();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //
    //        }
    //        return ApprovalPart;
    //    }

    public byte[] getBusinessTripAttach() throws Exception {
        String tripID = ADFUtils.getBoundAttributeValue("LocalBusinessTripId")!=null?ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString():"0";
        byte[] bytes = null;
        BiReportAccess b = new BiReportAccess();
        bytes = b.getBusinessTripAttachBytes(tripID);
        return bytes;
    }

    public void callBusinessTripWithdrawnProcess(String emailNotification) throws Exception {
        Row tripRow =
            ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
        String personNumber = tripRow.getAttribute("PersoneId").toString();
        String requestId = tripRow.getAttribute("LocalBusinessTripId").toString();
        String invoiceNumber = tripRow.getAttribute("InvoiceNumber")!=null ? tripRow.getAttribute("InvoiceNumber").toString() : "0";
        if("0".equals(invoiceNumber)){
            invoiceNumber = personNumber + "-" + requestId + "-BT";
        }
        BiReportAccess b = new BiReportAccess();
        List<Map> invList = new ArrayList<Map>();
        List<Map> btList = new ArrayList<Map>();
        
        invList = b.getInvoiceStatusList(invoiceNumber);
        String paymentStatus = "N";
        String businessTripAmount = "";
        String ticketAmount = "";
        
        if(invList.get(0).get("PAYMENT_STATUS_FLAG")!=null){
            paymentStatus = invList.get(0).get("PAYMENT_STATUS_FLAG").toString();
        } 
        
        //If Payment Status Flag is Y
        if("Y".equals(paymentStatus)){
            System.err.println("<---If Payment Status Flag is Y--->"+paymentStatus);
            String assignmentNo = "";
            String count = "";
            String creationDate = tripRow.getAttribute("CreationDate").toString(); 
         
            creationDate = creationDate.replace('-', '/');
            
            // IMPORTING BUSINESS TRIP DEDUCTION DAT
            btList = b.getBusinessTripWithdrawnList("Business Trip Deduction",personNumber);
            
            if(btList.get(0).get("ASSIGNMENT_NUMBER")!=null){
                assignmentNo = btList.get(0).get("ASSIGNMENT_NUMBER").toString();
            }
            if(btList.get(0).get("MULTIPLEENTRYCOUNT")!=null){
                count = btList.get(0).get("MULTIPLEENTRYCOUNT").toString();
            }
            if(invList.get(0).get("BUSINESS_TRIP_AMOUNT")!=null){
                businessTripAmount = invList.get(0).get("BUSINESS_TRIP_AMOUNT").toString();
            }
            
            HashMap<String, String> btDeductionParams = new HashMap<String, String>();
            btDeductionParams.put("CreationDate", creationDate);
            btDeductionParams.put("AssignmentNo", assignmentNo);
            btDeductionParams.put("Count", count);
            btDeductionParams.put("BusinessTripAmount", businessTripAmount);            
            
            try {
                System.err.println("btDeductionParams--->"+btDeductionParams);
                fusionFileLoader = new FusionDataLoader();
                //EES - Code added by moshina
                Map<String, String> map =  fusionFileLoader.sendFusionRequest(btDeductionParams, 9);
                String value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Withdrawn", "Business Trip Deduction", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Withdrawn Business Trip Deduction: "+value1);
                //fusionFileLoader.sendFusionRequest(btDeductionParams, 9);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // IMPORTING TICKET DEDUCTION DAT
            btList = b.getBusinessTripWithdrawnList("Tickets Deduction",personNumber);
            
            if(btList.get(0).get("ASSIGNMENT_NUMBER")!=null){
                assignmentNo = btList.get(0).get("ASSIGNMENT_NUMBER").toString();
            }
            if(btList.get(0).get("MULTIPLEENTRYCOUNT")!=null){
                count = btList.get(0).get("MULTIPLEENTRYCOUNT").toString();
            }
            if(invList.get(0).get("TICKET_AMOUNT")!=null){
                ticketAmount = invList.get(0).get("TICKET_AMOUNT").toString();
            }
            
            HashMap<String, String> ticketDeductParams = new HashMap<String, String>();
            ticketDeductParams.put("CreationDate", creationDate);
            ticketDeductParams.put("AssignmentNo", assignmentNo);
            ticketDeductParams.put("Count", count);
            ticketDeductParams.put("BusinessTripAmount", ticketAmount);            
            
            try {
                System.err.println("btDeductionParams--->"+ticketDeductParams);
                fusionFileLoader = new FusionDataLoader();
                //EES - Code added by Moshina
                Map<String, String> map =  fusionFileLoader.sendFusionRequest(ticketDeductParams, 10);
                value1 = ElementTatHdrUpdate.executeTatHdrUpdatePackage(ADFUtils.getBoundAttributeValue("LocalBusinessTripId").toString(), personNumber, "Withdrawn", "Tickets Deduction", map.get("dDocTitle"), map.get("dDocAuthor") ,map.get("dSecurityGroup"), map.get("dDocAccount"),map.get("contentType"), map.get("base64String"));
                System.out.println("TAT HDR Updated Value - Withdrawn Tickets Deduction: "+value1);
                //fusionFileLoader.sendFusionRequest(ticketDeductParams, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JSFUtils.addFacesInformationMessage("Element entries created for Business Trip Deduction and Tickets Deduction");
        }
        //If Payment Status Flag is N
        else{
            System.err.println("<---If Payment Status Flag is N--->"+paymentStatus);
            BiReportAccess report = new BiReportAccess();
            List<Map> hrOprSupervisor = null;
            List<Map> costControlSpl = null;
            String emailAdd = "";
            String hrOprSupervisorEmail = "";
            String costControlSplEmail = "";
            String email1078 = "";
            
            try {
                hrOprSupervisor = report.getPersonByPostionReport("HR Operation Supervisor");
                costControlSpl = report.getPersonByPostionReport("Cost Control Specialist"); 
                                
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (hrOprSupervisor.get(0).get("EMAIL_ADDRESS") != null) {
                hrOprSupervisorEmail = hrOprSupervisor.get(0).get("EMAIL_ADDRESS").toString();
            } 
            if (costControlSpl.get(0).get("EMAIL_ADDRESS") != null) {
                    costControlSplEmail = emailAdd + "," + costControlSpl.get(0).get("EMAIL_ADDRESS").toString();
            }
            if(this.getEmail("1078")!=null){
                email1078 =  this.getEmail("1078");
            }
        if (emailNotification != null && emailNotification.equalsIgnoreCase("Y")) {
            if(! "".equals(hrOprSupervisorEmail)){ 
                sendEmailToSir(hrOprSupervisorEmail ,tripRow);
                JSFUtils.addFacesInformationMessage("Mail has been sent to HR Operation Supervisor !!"); 
            }else{
                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as HR Operation Supervisor  dosn't has email");
            } 
            
            if(! "".equals(costControlSplEmail)){ 
                sendEmailToSir(costControlSplEmail ,tripRow);
                JSFUtils.addFacesInformationMessage("Mail has been sent to HR Operation Supervisor !!"); 
            }else{
                JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Cost Control Specialist  dosn't has email");
            } 
            
            if(! "".equals(email1078)){  
                sendEmailToSir(email1078 ,tripRow);
                JSFUtils.addFacesInformationMessage("Mail has been sent !!"); 
            }else{
                JSFUtils.addFacesErrorMessage("Mail hasn't been sent to 1078!");
            }  
        }
//            sendEmailByEmail("ibrahim23may@gmail.com",tripRow);
//            sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
//            sendEmailByEmail("Hamada.eleraki@tatweer.sa",tripRow);
        }
    }
    
    public void approveByPosition(String positionName){
        Row tripRow =
            ADFUtils.findIterator("BusinessTripRequestViewIterator").getCurrentRow();
        String personLocation = (String)ADFUtils.getBoundAttributeValue("PersonLocation") !=null?(String)ADFUtils.getBoundAttributeValue("PersonLocation"):""; //2023-PSC change
        String requestTripType =
            ADFUtils.getBoundAttributeValue("TripType").toString() !=null?ADFUtils.getBoundAttributeValue("TripType").toString():"";
                String TripTypeName=requestTripType +'-'+ personLocation;
                ADFContext.getCurrent().getPageFlowScope().put("TripTypeName", TripTypeName);
        OperationBinding nextOpr = ADFUtils.findOperation("getNextStep");
        Row nextStep = (Row)nextOpr.execute();
        String emailNotification =
            (String)nextStep.getAttribute("EmailNotification");
        BiReportAccess report = new BiReportAccess();
        List<Map> personData = null;
        try {
            personData =
                    report.getPersonByPostionReport(positionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (personData.get(0).get("DISPLAY_NAME") != null) {

            ADFUtils.setBoundAttributeValue("AssigneeName",
                                            personData.get(0).get("DISPLAY_NAME").toString());
            ADFUtils.setBoundAttributeValue("Assignee", 
                                            personData.get(0).get("PERSON_NUMBER").toString());
        }
        ADFUtils.setBoundAttributeValue("StepId",
                                        nextStep.getAttribute("NextStepId"));
        String returnvalue =
            ApprovalHistory.executeHistoryPackage((oracle.jbo.domain.DBSequence)ADFUtils.getBoundAttributeValue("LocalBusinessTripId"),
                                                  ADFUtils.getBoundAttributeValue("TripType").toString(),
                                                  (Number)nextStep.getAttribute("StepId"),
                                                  (Long)ADFUtils.getBoundAttributeValue("StepId"),
                                                  (String)ADFUtils.getBoundAttributeValue("AssigneeName"),
                                                  "APPROVE_ACT", "N"); 
        
        if (returnvalue.equalsIgnoreCase("SUCCESS")) {
            ADFUtils.findOperation("Commit").execute();
        } else {
            ADFUtils.findOperation("Rollback").execute();
        } 
            if (emailNotification != null &&
                emailNotification.equalsIgnoreCase("Y")) {
                if (personData.get(0).get("EMAIL_ADDRESS") != null) {
                    sendEmailByEmail(personData.get(0).get("EMAIL_ADDRESS").toString(), tripRow);
                }else{
                    JSFUtils.addFacesErrorMessage("Mail hasn't been sent as Payroll Manager dosn't has email");
                }
            }
    }
    
    public ArrayList<String> getPersonDetails(String personNumber) {
        ArrayList<String> personList = new ArrayList<String>();
        UserServiceUtil userService = new UserServiceUtil();
        UserDetails userDetails =
            userService.getUserDetailsByPersonNumber(personNumber);

        JAXBElement<String> nameElement =
            userDetails.getUserPersonDetails().get(0).getDisplayName();
        String name = nameElement.getValue();
        
        JAXBElement<String> emailElement =
            userDetails.getUserPersonDetails().get(0).getEmailAddress();
        String email = emailElement.getValue();
        //Get person name by get(0)
        if(name!=null){
            personList.add(name);    
        }else{
            personList.add("");
        }
        //Get person email by get(1)
        if(email!=null){
            personList.add(email);    
        }else{
            personList.add("");
        }
        return personList;
    }
    
    public String editPendingRequest() {
        ViewObject reqVo = ADFUtils.findIterator("BusinessTripRequestViewIterator").getViewObject();
        Row currRow = reqVo.getCurrentRow(); 
        String personNo = currRow.getAttribute("PersoneId").toString();
        
        currRow.setAttribute("StepId", "1"); 
        currRow.setAttribute("EditRequest", "Y");
        currRow.setAttribute("RequestStatus", "EDIT");
        currRow.setAttribute("ActionTaken", "EDIT");
        currRow.setAttribute("Assignee", personNo);
        currRow.setAttribute("AssigneeName", "");
        
        ADFUtils.findOperation("Commit").execute();
        JSFUtils.addFacesInformationMessage("Request enabled for edit and removed from the current approval !");
        return "edit";
    }
    
    public void sendEmailToPerson(String personNumber, Row subject) {
        String email = getEmail(personNumber);

        if (null == email) {
            JSFUtils.addFacesInformationMessage("Mail has not been sent because the employee has no email");
        } else {
            sendTripEmail("OFOQ.HR@TATWEER.SA", email,
                                     (BusinessTripRequestViewRowImpl)subject, "PERSON");
            JSFUtils.addFacesInformationMessage("Mail has been sent");
        }
    } 
    
    public Date getEndDate() {
            
            ViewObject reqVo = ADFUtils.findIterator("BusinessTripRequestViewIterator").getViewObject();
            Row currRow = reqVo.getCurrentRow(); 
            if(currRow.getAttribute("EndDate") != null){
                try { 
                    String endDateS = currRow.getAttribute("EndDate").toString();
                    Date endDate =new SimpleDateFormat("yyyy-MM-dd").parse(endDateS); 
                    Calendar c = Calendar.getInstance(); 
                    c.setTime(endDate); 
                    c.set(Calendar.HOUR, 11);
                    c.set(Calendar.MINUTE, 59);
                    c.set(Calendar.SECOND, 59);
                    c.set(Calendar.AM_PM, Calendar.PM);
                    endDate = c.getTime();
                    return endDate;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }   
            }else{
                return null;
            }
            return null;
        }
    
    public Date getMinValueForArrivalTime() {
            
            ViewObject reqVo = ADFUtils.findIterator("BusinessTripRequestViewIterator").getViewObject();
            Row currRow = reqVo.getCurrentRow(); 
            if(currRow.getAttribute("PreferredDepartureTime") != null){
                try { 
                    String endDateS = currRow.getAttribute("PreferredDepartureTime").toString();
                    Date endDate =new SimpleDateFormat("yyyy-MM-dd").parse(endDateS);  
                    return endDate;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }   
            }else{
                if(currRow.getAttribute("StartDate") != null){
                    try { 
                        String endDateS = currRow.getAttribute("StartDate").toString();
                        Date endDate =new SimpleDateFormat("yyyy-MM-dd").parse(endDateS);  
                        return endDate;
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }   
                }else{
                    return null;
                }
            }
            return null;
        }

    public void onClickStatusCount(ActionEvent actionEvent) {
        String person = ADFContext.getCurrent().getSessionScope().get("PaaSPersonName") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("PaaSPersonName").toString();
        
        String tripType = ADFContext.getCurrent().getSessionScope().get("tripType") == null ? "" : 
                                    ADFContext.getCurrent().getSessionScope().get("tripType").toString();
        
        String statusType = ADFContext.getCurrent().getPageFlowScope().get("statusType") == null ? "" : 
                                    ADFContext.getCurrent().getPageFlowScope().get("statusType").toString();
        
        ViewObject vo = ADFUtils.findIterator("BusinessTripRequestViewIterator").getViewObject();
        vo.applyViewCriteria(null);
        vo.executeQuery();
        
        if ("MyTasks".equals(statusType)) {
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("PersoneName", person);
            vc.addRow(vcRow);
            vcRow.setAttribute("TripType", tripType);
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }else 
        if("PendingTasks".equals(statusType)){
            ViewCriteria vc = vo.createViewCriteria();
            ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
            vcRow.setAttribute("AssigneeName", person);
            vc.addRow(vcRow);
            vcRow.setAttribute("TripType", tripType);
            vc.addRow(vcRow);
            vcRow.setAttribute("RequestStatus", "IN ('PENDING','Waiting Withdraw Approval')");
            vc.addRow(vcRow);
            vo.applyViewCriteria(vc);
            vo.executeQuery();
        }else{
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("BusinessTripRequestViewCriteriaByAssignee"));
            vo.executeQuery();
        }
    }

    public void resetFilter() {
        ViewObject vo = ADFUtils.getApplicationModuleForDataControl("SBMModuleDataControl").findViewObject("BusinessTripRequestView"); 
        vo.applyViewCriteria(null);
        vo.executeQuery();
        vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("BusinessTripRequestViewCriteriaByAssignee"));
        vo.executeQuery();
    }


    public void setPreStartDate(RichInputDate preStartDate) {
        this.preStartDate = preStartDate;
    }

    public RichInputDate getPreStartDate() {
        return preStartDate;
    }

    public void setPreArrivalTime(RichInputDate preArrivalTime) {
        this.preArrivalTime = preArrivalTime;
    }

    public RichInputDate getPreArrivalTime() {
        return preArrivalTime;
    }

    public void setDeclarationBox(RichSelectBooleanCheckbox declarationBox) {
        this.declarationBox = declarationBox;
    }

    public RichSelectBooleanCheckbox getDeclarationBox() {
        return declarationBox;
    }
}
