package com.sbm.selfServices.view.mb;

import com.mivors.model.bi.integration.BiReportAccess;

import com.sbm.selfServices.view.utils.ADFUtils;


import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;


public class BudgetAmountValidation {
    public BudgetAmountValidation() {
        super();
    }
    List<Map> budgetlist = null;
    Number accnumberBudget = null;

    public boolean getRemainingBudgetAmount(String division, String lob,
                                            String costCenter,
                                            String AccountNumber,
                                            String CostFilter,
                                            String creationDate) {
        String budgetYear = creationDate.substring(0, 4);
        System.out.println(budgetYear);
        Number travelCost = null;
        if(ADFUtils.getBoundAttributeValue("TravelCost")!=null){
            travelCost=(Number)ADFUtils.getBoundAttributeValue("TravelCost");
        }
        else{
            travelCost = new Number(0);
        }
        Number otherCost = null;
        if(ADFUtils.getBoundAttributeValue("OtherCost")!=null){
            otherCost=(Number)ADFUtils.getBoundAttributeValue("OtherCost");
        }
        else{
            otherCost = new Number(0);
        }    
        Number perdiem = null;
        if(ADFUtils.getBoundAttributeValue("PerDiemAmount")!=null){
            perdiem=(Number)ADFUtils.getBoundAttributeValue("PerDiemAmount");
        }
        else{
            perdiem = new Number(0);
        }
        Number travelamount = null;
        Number otheramount = null;
        Number balance = null;
        BiReportAccess reportbudget = new BiReportAccess();

        String combinationcode =
            "01" + "-" + division + "-" + lob + "-" + costCenter + "-" +
            AccountNumber;
          System.err.println("code combination"+combinationcode);
        ViewObject vo =
            ADFUtils.findIterator("BudgetValidationROVOIterator").getViewObject();
        vo.setNamedWhereClauseParam("LOCAL_BUSINESS_TRIP_ID",
                                    ADFUtils.getBoundAttributeValue("LocalBusinessTripId"));
        vo.setNamedWhereClauseParam("p_lob", lob);
        vo.setNamedWhereClauseParam("p_div", division);
        vo.setNamedWhereClauseParam("p_cost", costCenter);
        vo.setNamedWhereClauseParam("p_budget_year", budgetYear);
        vo.executeQuery();
        System.out.println("row count:"+vo.getEstimatedRowCount());
        if (vo.getEstimatedRowCount() > 0) {
            if(vo.first().getAttribute("TravelCost")!=null){
                travelamount = (Number)vo.first().getAttribute("TravelCost");
            }
            else{
                travelamount = new Number(0);
            }
            if(vo.first().getAttribute("OtherCost")!=null){
                otheramount = (Number)vo.first().getAttribute("OtherCost");
            }
            else{
                otheramount = new Number(0);
            }
        }
        System.out.println("pending req travel cost:"+travelamount);
        System.out.println("pending req other cost:"+otheramount);
        boolean flag = false;
        if (CostFilter.equals("OtherCost")) {
        Number otherTotal = otherCost.add(perdiem);
            if(otherTotal.intValue()>0){
                try {
                    System.err.println("Inside Report call");
                budgetlist = reportbudget.getBudgetAmount(combinationcode,creationDate);
                    
        } catch (Exception e) {
                    e.printStackTrace();
            return false;
        }
                System.err.println("size of listt"+budgetlist.size());
        if (budgetlist.size() > 0) {
           
                System.err.println("Inside Report call list");
                System.out.println("list:"+budgetlist);
                if(budgetlist.get(0).get("FUNDS_AVAILABLE_AMOUNT")!=null){
                        try {
                            accnumberBudget =
                            new Number(budgetlist.get(0).get("FUNDS_AVAILABLE_AMOUNT"));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                if (accnumberBudget != null) {
                    System.err.println("values===" +
                                       budgetlist.get(0).get("FUNDS_AVAILABLE_AMOUNT"));

                    balance = accnumberBudget.subtract(otheramount);
                    
                    if (balance.intValue() > otherTotal.intValue()) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
            }
            }
            else{
                flag=true;
            }
        }

            if (CostFilter.equals("TravelCost")) {
                if(travelCost.intValue()>0){
                try {
                    budgetlist = reportbudget.getBudgetAmount(combinationcode,creationDate);
                } catch (Exception e) {
                    return false;
                }
                try {
                    accnumberBudget =
                    new Number(budgetlist.get(0).get("FUNDS_AVAILABLE_AMOUNT"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (accnumberBudget != null) {
                    balance = accnumberBudget.subtract(travelamount);
                    if (balance.intValue() > travelCost.intValue()) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }

            }
                else{
                    flag=true;
                }
            }
        return flag;
    }
    
//    public static java.util.Date convertJavaUtilDateToJavaSqlDate(oracle.jbo.domain.Date javaUtilDate)
//      {
//        if(javaUtilDate == null)
//          return null;
//         System.out.println(javaUtilDate.getValue());
//          return javaUtilDate.getValue();
//          
//      }

}
