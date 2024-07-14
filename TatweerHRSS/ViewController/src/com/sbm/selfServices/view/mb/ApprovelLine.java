package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.model.views.up.BusinessTripRequestViewRowImpl;
import com.sbm.selfServices.model.views.up.ManPowerRequestViewRowImpl;
import com.sbm.selfServices.view.utils.ADFUtils;

import com.sbm.selfServices.view.utils.JSFUtils;

import java.io.File;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import oracle.adf.view.rich.component.rich.output.RichOutputText;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Number;
import oracle.jbo.server.ViewRowImpl;

public class ApprovelLine {

    public static String approvalLine(String iterName) {
        String ApprovalPart = "";
        String finalApprovalPart = "";
        String tableHeader =
            "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "<tr class=\"tableHeader\"><td colspan=\"5\" align=\"center\"><h2 style=\"color:white;\">Approval History</h2></td></tr>";

        String type = "<th style=text-align:left>\n" +
            "        &nbsp;Type\n" +
            "      </th>\n";
        String Assignee = "<th style=text-align:left>\n" +
            "        &nbsp;Assignee Name\n" +
            "      </th>\n";
        String status = "<th style=text-align:left>\n" +
            "        &nbsp;Status\n" +
            "      </th>\n";
        String submitDate = "<th style=text-align:left>\n" +
            "        &nbsp;Submitted On\n" +
            "      </th>\n";
        String approvaldate = "<th style=text-align:left>\n" +
            "        &nbsp;Approved On\n" +
            "      </th>\n";

        try {

            ViewObject vo = ADFUtils.findIterator(iterName).getViewObject();
            vo.executeQuery();
            System.err.println("Count===>" + vo.getEstimatedRowCount());
            RowSetIterator rs = vo.createRowSetIterator(null);
            oracle.jbo.domain.Date strDate = null;
            oracle.jbo.domain.Date endDate = null;
            String endDate2 = null;
            String strDate1 = null;
            while (rs.hasNext()) {
               
                Date date1 = null;
                Date date2 = null;
                
                Row r = rs.next();
                DateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    strDate =
                            (oracle.jbo.domain.Date)r.getAttribute("SubmitttedOn");
                    
                    endDate =
                            (oracle.jbo.domain.Date)r.getAttribute("ApprovalOn");
                    date1 = dateFormat.parse(strDate.toString());
                    if (endDate != null) {
                        date2 = dateFormat.parse(endDate.toString());
                    }

                    dateFormat =
                            new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                    strDate1 = dateFormat.format(date1);

                    if (date2 != null) {
                        endDate2 = dateFormat.format(date2);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String value = endDate2 != null ? endDate2.toString() : "";
                String colorcode = "";
                String action = (String)r.getAttribute("ActionTaken");
                if (action.equalsIgnoreCase("PENDING")) {
                    colorcode = "blue";
                } else if (action.equalsIgnoreCase("APPROVED")) {
                    colorcode = "green";
                } else if (action.equalsIgnoreCase("REJECTED")) {
                    colorcode = "red";
                } else {
                    colorcode = "violet";
                }
                String aprType = r.getAttribute("ApprovalType")!=null?r.getAttribute("ApprovalType").toString():"";
                ApprovalPart = ApprovalPart + "<tr>\n" +
                        "<td style=font-weight:bold>" +
                        aprType + "</td>\n" +
                        "<td style=font-weight:bold>" +
                        r.getAttribute("AssigneeName") + "</td>\n" +
                        " <td style=color:" + colorcode + ">" + action +
                        "</td>\n" +
                        "    <td>" + strDate1 + "</td>\n" +
                        "    <td>" + value + "</td>\n" +
                        "  </tr>";
                //         }
            }
            vo.executeQuery();
            String verticalSpace = "<p>&nbsp;</p>";
            finalApprovalPart =
                    verticalSpace + tableHeader + type + Assignee + status +
                    submitDate + approvaldate + ApprovalPart + " </table>" +
                    "<br/></br>";
        } catch (Exception e) {
            e.printStackTrace();

        }
        return finalApprovalPart;
    }
    public static String overTimeWithLine(String iterName) {
        String Overtimepart = "";
        String finalOvertimepart = "";
        String tableHeader =
            "<table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
            "<tr class=\"tableHeader\"><td colspan=\"4\" align=\"center\"><h2 style=\"color:white;\">Over Time Line</h2></td></tr>";

        String oDate = "<th>\n" +
            "        &nbsp;Over Time Date\n" +
            "      </th>\n";
        String oHours = "<th>\n" +
            "        &nbsp;Hours\n" +
            "      </th>\n";
        String oDesc = "<th>\n" +
            "        &nbsp;Description\n" +
            "      </th>\n";
        String verticalSpace = "<p>&nbsp;</p>";
        ViewObject vo = ADFUtils.findIterator(iterName).getViewObject();
        vo.executeQuery();
        RowSetIterator rs = vo.createRowSetIterator(null);
        oracle.jbo.domain.Date strDate = null;
        String strDate1 = null;
        if (vo.getEstimatedRowCount() > 0) {
            while (rs.hasNext()) {

                Row r = rs.next();
                String desc =
                    r.getAttribute("Description") != null ? r.getAttribute("Description").toString() :
                    "";
                strDate =
                        (oracle.jbo.domain.Date)r.getAttribute("OverTimeDate");
                String value = convertDatePattern(strDate.toString(),"yyyy-MM-dd","dd-MMM-yyyy");
                Overtimepart = Overtimepart + "<tr>\n" +
                        "<td>" + value + "</td>\n" +
                        " <td>" + r.getAttribute("Hours").toString() +
                        "</td>\n" +
                        "<td>" + desc + "</td>\n" +
                        "  </tr>";
            }

            finalOvertimepart =
                    verticalSpace + tableHeader + oDate + oHours + oDesc +
                    Overtimepart + " </table>" + "<br/></br>";
            //strDate1
            //r.getAttribute("Hours").toString()
        } else {
            finalOvertimepart = " </table>" + "<br/></br>";
        }
        return finalOvertimepart;
    }

    public static String getMailDetails(BusinessTripRequestViewRowImpl subject) {
        //        ViewRowImpl row=null;
        //
        //        for(int i=0;i<row.getAttributeNames().length;i++) {
        //            System.err.println(row.getAttributeValues());
        //        }
        //        BusinessTripRequestViewRowImpl subject;

        String tripType = subject.getTripType();

        String subType =
            subject.getSubType() != null ? subject.getSubType() : "";
        String expenseid =
            subject.getExpenseType() != null ? subject.getExpenseType() : "";
        String eventType =
            subject.getEventType() != null ? subject.getEventType() : "";
        String eventSubject =
            subject.getEventSubject() != null ? subject.getEventSubject() : "";
        Object eventCost =
            subject.getEventCost() != null ? subject.getEventCost().toString() :
            "";
        String targetCompent =
            subject.getTargetedCompetencies() != null ? subject.getTargetedCompetencies() :
            "";
        String courseName =
            subject.getCourseName() != null ? subject.getCourseName() : "";

        String requiredVisa =
            subject.getRequiredVisa() != null ? subject.getRequiredVisa() : "";
        String oneWayTrip =
            subject.getOneWayTrip() != null ? subject.getOneWayTrip() : "";
        String destinationType =
            subject.getDestinationType() != null ? subject.getDestinationType() :
            "";
        Object exp = null;
        if (subject.getTripType() != null && subject.getCountryId() != null &&
            (subject.getTripType().equalsIgnoreCase("Inter") ||
             subject.getSubType().equalsIgnoreCase("International"))) {
            exp =JSFUtils.resolveExpression("#{bindings.CountryId.selectedValue ne ' ' ? bindings.CountryId.selectedValue.attributeValues[1] : ''}");
        } else {
            exp = "";
        }

        String countryId = exp != null ? exp.toString() : "";
        String otherCities =
            subject.getOtherCities() != null ? subject.getOtherCities() : "";
        String tripreason =
            subject.getTripReason() != null ? subject.getTripReason() : "";
        String tripComment =
            subject.getTripComment() != null ? subject.getTripComment() : "";

        String tripreason1 = "<tr>\n" +
            "      <th>\n" +
            "        &nbsp;Trip Reason\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + tripreason + "</td>\n" +
            "    </tr>\n";
        String TripComment = "   <tr>\n" +
            "      <th>\n" +
            "        &nbsp;Trip Comment\n" +
            "      </th>\n" +
            "      <td width=\"50%\">" + tripComment + "</td>\n" +
            "    </tr>\n";
        String changeToValue = "";
        String generic = "";
        if (requiredVisa != null && !requiredVisa.isEmpty() &&
            requiredVisa.equals("Y")) {
            changeToValue = "Yes";
        } else {
            changeToValue = "No";
        }
        String changeValue = "";
        if (oneWayTrip != null && !requiredVisa.isEmpty() &&
            oneWayTrip.equals("Y")) {
            changeValue = "Yes";
        } else {
            changeValue = "No";
        }
        if (tripType.equalsIgnoreCase("Local") ||
            tripType.equalsIgnoreCase("Expense")) {
            generic = "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;One Way Trip\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + changeValue + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Other Cities\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + otherCities + "</td>\n" +
                    "    </tr>\n";

        } else if (tripType.equalsIgnoreCase("Inter") ||
                   tripType.equalsIgnoreCase("Expense")) {
            generic = "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Required Visa\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + changeToValue + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Destination Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + destinationType + "</td>\n" +
                    "    </tr>\n" +
                    "   <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Country\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + countryId + "</td>\n" +
                    "    </tr>\n" +
                    "   <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Other Cities\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + otherCities + "</td>\n" +
                    "    </tr>\n";
        } else if ((tripType.equalsIgnoreCase("Training") &&
                    subType.equalsIgnoreCase("Local")) ||
                   (subType.equalsIgnoreCase("Local") &&
                    expenseid.equalsIgnoreCase("Local"))) {
            generic = "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;One Way Trip\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + changeValue + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Sub Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subType + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Targeted Competencies\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + targetCompent + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Course Name\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + courseName + "</td>\n" +
                    "    </tr>\n";

        } else if ((tripType.equalsIgnoreCase("Training") &&
                    subType.equalsIgnoreCase("International")) ||
                   (subType.equalsIgnoreCase("International") &&
                    expenseid.equalsIgnoreCase("Training"))) {
            generic = "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Required Visa\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + changeToValue + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Sub Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subType + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Targeted Competencies\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + targetCompent + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Course Name\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + courseName + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Destination Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + destinationType + "</td>\n" +
                    "    </tr>\n" +
                    "   <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Country\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + countryId + "</td>\n" +
                    "    </tr>\n";
        } else if ((tripType.equalsIgnoreCase("Event") &&
                    subType.equalsIgnoreCase("Local")) ||
                   (subType.equalsIgnoreCase("Local") &&
                    expenseid.equalsIgnoreCase("Event"))) {
            generic = "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;One Way Trip\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + changeValue + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Sub Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subType + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Event Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + eventType + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Event Subject\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + eventSubject + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Event Cost\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + eventCost + "</td>\n" +
                    "    </tr>\n";
        } else if ((tripType.equalsIgnoreCase("Event") &&
                    subType.equalsIgnoreCase("International")) ||
                   (subType.equalsIgnoreCase("International") &&
                    expenseid.equalsIgnoreCase("Event"))) {
            generic = "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Required Visa\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + changeToValue + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Sub Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + subType + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Event Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + eventType + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Event Subject\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + eventSubject + "</td>\n" +
                    "    </tr>\n" +
                    "<tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Event Cost\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + eventCost + "</td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Destination Type\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + destinationType + "</td>\n" +
                    "    </tr>\n" +
                    "   <tr>\n" +
                    "      <th>\n" +
                    "        &nbsp;Country\n" +
                    "      </th>\n" +
                    "      <td width=\"50%\">" + countryId + "</td>\n" +
                    "    </tr>\n";


        } else {

        }

        return generic + tripreason1 + TripComment;
    }

    public static String getManPowerEmail(String into, String from, String to,
                                          ManPowerRequestViewRowImpl subject , String mailSubj) {
        try {
            if("".equals(mailSubj)){
                mailSubj = "Manpower Requisition Request " + subject.getRequestStatus();
            }
            String dept =
                subject.getDepartment() != null ? subject.getDepartment() : "";
            String personname =
                subject.getPersonName() != null ? subject.getPersonName().toString() :
                "";
            String posperson =
                subject.getPersonPosition() != null ? subject.getPersonPosition().toString() :
                "";
            String perosonjob =
                subject.getPersonJob() != null ? subject.getPersonJob().toString() :
                "";
            String pgrdate =
                subject.getPersonGrade() != null ? subject.getPersonGrade().toString() :
                "";
            String plocation =
                subject.getPersonLocation() != null ? subject.getPersonLocation().toString() :
                "";
            String pdepartment =
                subject.getPersonDepartment() != null ? subject.getPersonDepartment().toString() :
                "";
            String reqtitle =
                subject.getRequisitionTitle() != null ? subject.getRequisitionTitle() :
                "";
            Object opening =
                subject.getNumberOfOpening() != null ? subject.getNumberOfOpening().toString() :
                "";
            Object date =
                subject.getTargetStartDate() != null ? subject.getTargetStartDate().toString() :
                "";
            Object duration =
                subject.getDuration() != null ? subject.getDuration().toString() :
                "";
            String type =
                subject.getType() != null ? subject.getType().toString() : "";
            String category =
                subject.getPositionCategory() != null ? subject.getPositionCategory().toString() :
                "";
            String durationtype =
                subject.getDurationType() != null ? subject.getDurationType().toString() :
                "";
            String jobtype =
                subject.getJob() != null ? subject.getJob().toString() : "";
            String justification =
                subject.getJustifications() != null ? subject.getJustifications().toString() :
                "";
            String languaee =
                subject.getLanguage() != null ? subject.getLanguage().toString() :
                "";
            String replacement =
                subject.getReplacement() != null ? subject.getReplacement().toString() :
                "";
            String desc =
                subject.getJobDescription() != null ? subject.getJobDescription().toString() :
                "";
            String qualification =
                subject.getQualifications() != null ? subject.getQualifications().toString() :
                "";
            String exper =
                subject.getExperience() != null ? subject.getExperience().toString() :
                "";
            String addcomment =
                subject.getAdditionalComments() != null ? subject.getAdditionalComments().toString() :
                "";
            String nationality =
                subject.getNationality() != null ? subject.getNationality().toString() :
                "";
            String convertedDate = null;
            if (subject.getTargetStartDate() != null) {
                convertedDate = convertDatePattern(date.toString(),"yyyy-MM-dd","dd-MMM-yyyy");
            } else {
                convertedDate = "";
            }

            Number basicsalary = new Number(0);
            if (subject.getBasicSalary() != null) {
                basicsalary = subject.getBasicSalary();
            }
            String basicSalaryLabel = "Basic Salary";
            if (type != null && type.equalsIgnoreCase("Coop Training")) {
                basicSalaryLabel = "Compensation Amount";
            }
            String personalInformation =
                " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Job Informationn</h2></td></tr>" +
                " <tr><th>&nbsp;Requester Name</th>\n" +
                "      <td width=\"50%\">" + personname + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Requester Position\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + posperson + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Requester Job\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + perosonjob + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Requester Grade\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + pgrdate + "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Requester Department\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + pdepartment + "</td>\n" +
                "    </tr><tr>\n" +
                "      <th>\n" +
                "        &nbsp;Requester Location\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + plocation + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;" + "Job" + "\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + jobtype + "</td>\n" +
                "    </tr>\n" +


                "  </table>";
            String verticalSpace = " <p>&nbsp;</p>";
            String identification =
                " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Identification</h2></td></tr>" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Requisition Title\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + reqtitle + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Number Of Opening\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + opening + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +

                "      <th>\n" +
                "        &nbsp;Department\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + dept + "</td>\n" +
                "    </tr>\n" +

                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Target Start Date\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + convertedDate + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +

                "      <th>\n" +
                "        &nbsp;Duration\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + duration + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +

                "      <th>\n" +
                "        &nbsp;Type\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + type + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +

                "      <th>\n" +
                "        &nbsp;" + "Duration Type" +

                "\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + durationtype + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Justifications\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + justification + "</td>\n" +
                "    </tr>\n" +
                "  </table>";
            String Additional_Information =
                " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Additional Information</h2></td></tr>" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Language\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + languaee + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Replacement\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + replacement + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Nationality\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + nationality + "</td>\n" +
                "</tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Position Category\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + category + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;" + basicSalaryLabel + "\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + basicsalary.toString() +
                "</td>\n" +
                "    </tr>\n" +
                "  </table>";

            String addtional =
                " <table cellspacing=\"2\" cellpadding=\"3\" border=\"1\" align=\"center\" width=\"100%\">\n" +
                "<tr class=\"tableHeader\"><td colspan=\"2\" align=\"center\"> <h2 style=\"color:white;\">Job Description And Qualifications</h2></td></tr>" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Job Description\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + desc + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Qualifications\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + qualification + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Experience\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + exper + "</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <th>\n" +
                "        &nbsp;Additional Comments\n" +
                "      </th>\n" +
                "      <td width=\"50%\">" + addcomment + "</td>\n" +
                "    </tr>\n" +
                "</table>";
            String ApprovalPart1 =
                ApprovelLine.approvalLine("ApprovalHistoryVOIterator");
            String thankYouPart =
                "<br/><b>Thanks In Advance " + "</b><br/><br/>";
            String signaturePart =
                "This message sent by " + "<b>" + "https://egwo.fa.em2.oraclecloud.com/homePage/faces/FuseWelcome " +
                "<b/>" + "<br/>";
            String emailcontent =
                into + verticalSpace + identification + verticalSpace +
                personalInformation + verticalSpace + Additional_Information +
                verticalSpace + addtional + verticalSpace + ApprovalPart1 +
                thankYouPart + signaturePart + "</p>";
            OperationBinding sendMail =
                ADFUtils.findOperation("callSendEmailStoredPL");
            sendMail.getParamsMap().put("sender", from);
            sendMail.getParamsMap().put("receiver", to);
            sendMail.getParamsMap().put("subject", mailSubj);
//                                        "Manpower Requisition Request " +
//                                        subject.getRequestStatus());
            sendMail.getParamsMap().put("e_body", emailcontent);
            sendMail.execute();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    public static String convertDatePattern(String date,String fromDate,String todate) {
        DateFormat dateFormat = new SimpleDateFormat(fromDate);
        Date date1 = null;
        try {
            date1 = dateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
       if(date1!=null)
       {
        dateFormat = new SimpleDateFormat(todate);
        date = dateFormat.format(date1); 
       }
        return date;
    }

    public static String[] convertStartDateAndEndDate(String from, String to,
                                                      String dateFromFormat,
                                                      String dateToFormat) {
        DateFormat dateFormat = new SimpleDateFormat(dateFromFormat);
        String[] arrofDates = new String[2];
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(from);
            date2 = dateFormat.parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateFormat = new SimpleDateFormat(dateToFormat);
        if (date1 != null && date2 != null) {
            arrofDates[0] = dateFormat.format(date1);
            arrofDates[1] = dateFormat.format(date2);
        }
        return arrofDates;
    }
}

