package com.sbm.selfServices.model.views.up;

import com.sbm.selfServices.model.entities.MobileAllowanceRequestImpl;

import oracle.jbo.RowIterator;
import oracle.jbo.RowSet;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sun Dec 23 12:51:44 AST 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class MobileAllowanceRequestUPViewRowImpl extends ViewRowImpl {


    public static final int ENTITY_MOBILEALLOWANCEREQUEST = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        RequestId {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getRequestId();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setRequestId((DBSequence) value);
            }
        }
        ,
        PersoneNumber {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersoneNumber();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersoneNumber((String) value);
            }
        }
        ,
        StepId {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getStepId();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setStepId((Long) value);
            }
        }
        ,
        PersoneName {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersoneName();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersoneName((String) value);
            }
        }
        ,
        RequestStatus {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getRequestStatus();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setRequestStatus((String) value);
            }
        }
        ,
        RejectComments {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getRejectComments();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setRejectComments((String) value);
            }
        }
        ,
        Assignee {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getAssignee();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setAssignee((String) value);
            }
        }
        ,
        RequestReason {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getRequestReason();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setRequestReason((String) value);
            }
        }
        ,
        AssigneeName {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getAssigneeName();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setAssigneeName((String) value);
            }
        }
        ,
        PersonDepartment {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersonDepartment();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersonDepartment((String) value);
            }
        }
        ,
        PersonGrade {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersonGrade();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersonGrade((String) value);
            }
        }
        ,
        PersonJob {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersonJob();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersonJob((String) value);
            }
        }
        ,
        PersonLocation {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersonLocation();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersonLocation((String) value);
            }
        }
        ,
        PersonPosition {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getPersonPosition();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setPersonPosition((String) value);
            }
        }
        ,
        CreationDate {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getCreationDate();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setCreationDate((Date) value);
            }
        }
        ,
        ActionTaken {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getActionTaken();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setActionTaken((String) value);
            }
        }
        ,
        NumberOfMonths {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getNumberOfMonths();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setNumberOfMonths((Number) value);
            }
        }
        ,
        EditRequest {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getEditRequest();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setEditRequest((String) value);
            }
        }
        ,
        Amount {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getAmount();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setAmount((Number) value);
            }
        }
        ,
        CostCenterName {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getCostCenterName();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setCostCenterName((String) value);
            }
        }
        ,
        CostCenterNumber {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getCostCenterNumber();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setCostCenterNumber((String) value);
            }
        }
        ,
        Division {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getDivision();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setDivision((String) value);
            }
        }
        ,
        EffectiveEndDate {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getEffectiveEndDate();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setEffectiveEndDate((Date) value);
            }
        }
        ,
        EffectiveStartDate {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getEffectiveStartDate();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setEffectiveStartDate((Date) value);
            }
        }
        ,
        DepartmentNumber {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getDepartmentNumber();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setDepartmentNumber((String) value);
            }
        }
        ,
        HireType {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getHireType();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setHireType((String) value);
            }
        }
        ,
        ApprovalHistoryVO {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getApprovalHistoryVO();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        RequestStepsROView {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getRequestStepsROView();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        MonthsLOV1 {
            protected Object get(MobileAllowanceRequestUPViewRowImpl obj) {
                return obj.getMonthsLOV1();
            }

            protected void put(MobileAllowanceRequestUPViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        ;
        private static final int firstIndex = 0;


        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static final int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }


        protected abstract Object get(MobileAllowanceRequestUPViewRowImpl object);

        protected abstract void put(MobileAllowanceRequestUPViewRowImpl object, Object value);
    }


    public static final int REQUESTID = AttributesEnum.RequestId.index();
    public static final int PERSONENUMBER = AttributesEnum.PersoneNumber.index();
    public static final int STEPID = AttributesEnum.StepId.index();
    public static final int PERSONENAME = AttributesEnum.PersoneName.index();
    public static final int REQUESTSTATUS = AttributesEnum.RequestStatus.index();
    public static final int REJECTCOMMENTS = AttributesEnum.RejectComments.index();
    public static final int ASSIGNEE = AttributesEnum.Assignee.index();
    public static final int REQUESTREASON = AttributesEnum.RequestReason.index();
    public static final int ASSIGNEENAME = AttributesEnum.AssigneeName.index();
    public static final int PERSONDEPARTMENT = AttributesEnum.PersonDepartment.index();
    public static final int PERSONGRADE = AttributesEnum.PersonGrade.index();
    public static final int PERSONJOB = AttributesEnum.PersonJob.index();
    public static final int PERSONLOCATION = AttributesEnum.PersonLocation.index();
    public static final int PERSONPOSITION = AttributesEnum.PersonPosition.index();
    public static final int CREATIONDATE = AttributesEnum.CreationDate.index();
    public static final int ACTIONTAKEN = AttributesEnum.ActionTaken.index();
    public static final int NUMBEROFMONTHS = AttributesEnum.NumberOfMonths.index();
    public static final int EDITREQUEST = AttributesEnum.EditRequest.index();
    public static final int AMOUNT = AttributesEnum.Amount.index();
    public static final int COSTCENTERNAME = AttributesEnum.CostCenterName.index();
    public static final int COSTCENTERNUMBER = AttributesEnum.CostCenterNumber.index();
    public static final int DIVISION = AttributesEnum.Division.index();
    public static final int EFFECTIVEENDDATE = AttributesEnum.EffectiveEndDate.index();
    public static final int EFFECTIVESTARTDATE = AttributesEnum.EffectiveStartDate.index();
    public static final int DEPARTMENTNUMBER = AttributesEnum.DepartmentNumber.index();
    public static final int HIRETYPE = AttributesEnum.HireType.index();
    public static final int APPROVALHISTORYVO = AttributesEnum.ApprovalHistoryVO.index();
    public static final int REQUESTSTEPSROVIEW = AttributesEnum.RequestStepsROView.index();
    public static final int MONTHSLOV1 = AttributesEnum.MonthsLOV1.index();

    /**
     * This is the default constructor (do not remove).
     */
    public MobileAllowanceRequestUPViewRowImpl() {
    }

    /**
     * Gets MobileAllowanceRequest entity object.
     * @return the MobileAllowanceRequest
     */
    public MobileAllowanceRequestImpl getMobileAllowanceRequest() {
        return (MobileAllowanceRequestImpl)getEntity(0);
    }

    /**
     * Gets the attribute value for REQUEST_ID using the alias name RequestId.
     * @return the REQUEST_ID
     */
    public DBSequence getRequestId() {
        return (DBSequence)getAttributeInternal(REQUESTID);
    }

    /**
     * Sets <code>value</code> as attribute value for REQUEST_ID using the alias name RequestId.
     * @param value value to set the REQUEST_ID
     */
    public void setRequestId(DBSequence value) {
        setAttributeInternal(REQUESTID, value);
    }

    /**
     * Gets the attribute value for PERSONE_NUMBER using the alias name PersoneNumber.
     * @return the PERSONE_NUMBER
     */
    public String getPersoneNumber() {
        return (String) getAttributeInternal(PERSONENUMBER);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSONE_NUMBER using the alias name PersoneNumber.
     * @param value value to set the PERSONE_NUMBER
     */
    public void setPersoneNumber(String value) {
        setAttributeInternal(PERSONENUMBER, value);
    }

    /**
     * Gets the attribute value for STEP_ID using the alias name StepId.
     * @return the STEP_ID
     */
    public Long getStepId() {
        return (Long) getAttributeInternal(STEPID);
    }

    /**
     * Sets <code>value</code> as attribute value for STEP_ID using the alias name StepId.
     * @param value value to set the STEP_ID
     */
    public void setStepId(Long value) {
        setAttributeInternal(STEPID, value);
    }

    /**
     * Gets the attribute value for PERSONE_NAME using the alias name PersoneName.
     * @return the PERSONE_NAME
     */
    public String getPersoneName() {
        return (String) getAttributeInternal(PERSONENAME);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSONE_NAME using the alias name PersoneName.
     * @param value value to set the PERSONE_NAME
     */
    public void setPersoneName(String value) {
        setAttributeInternal(PERSONENAME, value);
    }

    /**
     * Gets the attribute value for REQUEST_STATUS using the alias name RequestStatus.
     * @return the REQUEST_STATUS
     */
    public String getRequestStatus() {
        return (String) getAttributeInternal(REQUESTSTATUS);
    }

    /**
     * Sets <code>value</code> as attribute value for REQUEST_STATUS using the alias name RequestStatus.
     * @param value value to set the REQUEST_STATUS
     */
    public void setRequestStatus(String value) {
        setAttributeInternal(REQUESTSTATUS, value);
    }

    /**
     * Gets the attribute value for REJECT_COMMENTS using the alias name RejectComments.
     * @return the REJECT_COMMENTS
     */
    public String getRejectComments() {
        return (String) getAttributeInternal(REJECTCOMMENTS);
    }

    /**
     * Sets <code>value</code> as attribute value for REJECT_COMMENTS using the alias name RejectComments.
     * @param value value to set the REJECT_COMMENTS
     */
    public void setRejectComments(String value) {
        setAttributeInternal(REJECTCOMMENTS, value);
    }

    /**
     * Gets the attribute value for ASSIGNEE using the alias name Assignee.
     * @return the ASSIGNEE
     */
    public String getAssignee() {
        return (String) getAttributeInternal(ASSIGNEE);
    }

    /**
     * Sets <code>value</code> as attribute value for ASSIGNEE using the alias name Assignee.
     * @param value value to set the ASSIGNEE
     */
    public void setAssignee(String value) {
        setAttributeInternal(ASSIGNEE, value);
    }

    /**
     * Gets the attribute value for REQUEST_REASON using the alias name RequestReason.
     * @return the REQUEST_REASON
     */
    public String getRequestReason() {
        return (String) getAttributeInternal(REQUESTREASON);
    }

    /**
     * Sets <code>value</code> as attribute value for REQUEST_REASON using the alias name RequestReason.
     * @param value value to set the REQUEST_REASON
     */
    public void setRequestReason(String value) {
        setAttributeInternal(REQUESTREASON, value);
    }

    /**
     * Gets the attribute value for ASSIGNEE_NAME using the alias name AssigneeName.
     * @return the ASSIGNEE_NAME
     */
    public String getAssigneeName() {
        return (String) getAttributeInternal(ASSIGNEENAME);
    }

    /**
     * Sets <code>value</code> as attribute value for ASSIGNEE_NAME using the alias name AssigneeName.
     * @param value value to set the ASSIGNEE_NAME
     */
    public void setAssigneeName(String value) {
        setAttributeInternal(ASSIGNEENAME, value);
    }

    /**
     * Gets the attribute value for PERSON_DEPARTMENT using the alias name PersonDepartment.
     * @return the PERSON_DEPARTMENT
     */
    public String getPersonDepartment() {
        return (String) getAttributeInternal(PERSONDEPARTMENT);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSON_DEPARTMENT using the alias name PersonDepartment.
     * @param value value to set the PERSON_DEPARTMENT
     */
    public void setPersonDepartment(String value) {
        setAttributeInternal(PERSONDEPARTMENT, value);
    }

    /**
     * Gets the attribute value for PERSON_GRADE using the alias name PersonGrade.
     * @return the PERSON_GRADE
     */
    public String getPersonGrade() {
        return (String) getAttributeInternal(PERSONGRADE);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSON_GRADE using the alias name PersonGrade.
     * @param value value to set the PERSON_GRADE
     */
    public void setPersonGrade(String value) {
        setAttributeInternal(PERSONGRADE, value);
    }

    /**
     * Gets the attribute value for PERSON_JOB using the alias name PersonJob.
     * @return the PERSON_JOB
     */
    public String getPersonJob() {
        return (String) getAttributeInternal(PERSONJOB);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSON_JOB using the alias name PersonJob.
     * @param value value to set the PERSON_JOB
     */
    public void setPersonJob(String value) {
        setAttributeInternal(PERSONJOB, value);
    }

    /**
     * Gets the attribute value for PERSON_LOCATION using the alias name PersonLocation.
     * @return the PERSON_LOCATION
     */
    public String getPersonLocation() {
        return (String) getAttributeInternal(PERSONLOCATION);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSON_LOCATION using the alias name PersonLocation.
     * @param value value to set the PERSON_LOCATION
     */
    public void setPersonLocation(String value) {
        setAttributeInternal(PERSONLOCATION, value);
    }

    /**
     * Gets the attribute value for PERSON_POSITION using the alias name PersonPosition.
     * @return the PERSON_POSITION
     */
    public String getPersonPosition() {
        return (String) getAttributeInternal(PERSONPOSITION);
    }

    /**
     * Sets <code>value</code> as attribute value for PERSON_POSITION using the alias name PersonPosition.
     * @param value value to set the PERSON_POSITION
     */
    public void setPersonPosition(String value) {
        setAttributeInternal(PERSONPOSITION, value);
    }

    /**
     * Gets the attribute value for CREATION_DATE using the alias name CreationDate.
     * @return the CREATION_DATE
     */
    public Date getCreationDate() {
        return (Date) getAttributeInternal(CREATIONDATE);
    }

    /**
     * Sets <code>value</code> as attribute value for CREATION_DATE using the alias name CreationDate.
     * @param value value to set the CREATION_DATE
     */
    public void setCreationDate(Date value) {
        setAttributeInternal(CREATIONDATE, value);
    }

    /**
     * Gets the attribute value for ACTION_TAKEN using the alias name ActionTaken.
     * @return the ACTION_TAKEN
     */
    public String getActionTaken() {
        return (String) getAttributeInternal(ACTIONTAKEN);
    }

    /**
     * Sets <code>value</code> as attribute value for ACTION_TAKEN using the alias name ActionTaken.
     * @param value value to set the ACTION_TAKEN
     */
    public void setActionTaken(String value) {
        setAttributeInternal(ACTIONTAKEN, value);
    }

    /**
     * Gets the attribute value for NUMBER_OF_MONTHS using the alias name NumberOfMonths.
     * @return the NUMBER_OF_MONTHS
     */
    public Number getNumberOfMonths() {
        return (Number) getAttributeInternal(NUMBEROFMONTHS);
    }

    /**
     * Sets <code>value</code> as attribute value for NUMBER_OF_MONTHS using the alias name NumberOfMonths.
     * @param value value to set the NUMBER_OF_MONTHS
     */
    public void setNumberOfMonths(Number value) {
        setAttributeInternal(NUMBEROFMONTHS, value);
    }

    /**
     * Gets the attribute value for EDIT_REQUEST using the alias name EditRequest.
     * @return the EDIT_REQUEST
     */
    public String getEditRequest() {
        return (String) getAttributeInternal(EDITREQUEST);
    }

    /**
     * Sets <code>value</code> as attribute value for EDIT_REQUEST using the alias name EditRequest.
     * @param value value to set the EDIT_REQUEST
     */
    public void setEditRequest(String value) {
        setAttributeInternal(EDITREQUEST, value);
    }

    /**
     * Gets the attribute value for AMOUNT using the alias name Amount.
     * @return the AMOUNT
     */
    public Number getAmount() {
        return (Number) getAttributeInternal(AMOUNT);
    }

    /**
     * Sets <code>value</code> as attribute value for AMOUNT using the alias name Amount.
     * @param value value to set the AMOUNT
     */
    public void setAmount(Number value) {
        setAttributeInternal(AMOUNT, value);
    }

    /**
     * Gets the attribute value for COST_CENTER_NAME using the alias name CostCenterName.
     * @return the COST_CENTER_NAME
     */
    public String getCostCenterName() {
        return (String) getAttributeInternal(COSTCENTERNAME);
    }

    /**
     * Sets <code>value</code> as attribute value for COST_CENTER_NAME using the alias name CostCenterName.
     * @param value value to set the COST_CENTER_NAME
     */
    public void setCostCenterName(String value) {
        setAttributeInternal(COSTCENTERNAME, value);
    }

    /**
     * Gets the attribute value for COST_CENTER_NUMBER using the alias name CostCenterNumber.
     * @return the COST_CENTER_NUMBER
     */
    public String getCostCenterNumber() {
        return (String) getAttributeInternal(COSTCENTERNUMBER);
    }

    /**
     * Sets <code>value</code> as attribute value for COST_CENTER_NUMBER using the alias name CostCenterNumber.
     * @param value value to set the COST_CENTER_NUMBER
     */
    public void setCostCenterNumber(String value) {
        setAttributeInternal(COSTCENTERNUMBER, value);
    }

    /**
     * Gets the attribute value for DIVISION using the alias name Division.
     * @return the DIVISION
     */
    public String getDivision() {
        return (String) getAttributeInternal(DIVISION);
    }

    /**
     * Sets <code>value</code> as attribute value for DIVISION using the alias name Division.
     * @param value value to set the DIVISION
     */
    public void setDivision(String value) {
        setAttributeInternal(DIVISION, value);
    }

    /**
     * Gets the attribute value for EFFECTIVE_END_DATE using the alias name EffectiveEndDate.
     * @return the EFFECTIVE_END_DATE
     */
    public Date getEffectiveEndDate() {
        return (Date) getAttributeInternal(EFFECTIVEENDDATE);
    }

    /**
     * Sets <code>value</code> as attribute value for EFFECTIVE_END_DATE using the alias name EffectiveEndDate.
     * @param value value to set the EFFECTIVE_END_DATE
     */
    public void setEffectiveEndDate(Date value) {
        setAttributeInternal(EFFECTIVEENDDATE, value);
    }

    /**
     * Gets the attribute value for EFFECTIVE_START_DATE using the alias name EffectiveStartDate.
     * @return the EFFECTIVE_START_DATE
     */
    public Date getEffectiveStartDate() {
        return (Date) getAttributeInternal(EFFECTIVESTARTDATE);
    }

    /**
     * Sets <code>value</code> as attribute value for EFFECTIVE_START_DATE using the alias name EffectiveStartDate.
     * @param value value to set the EFFECTIVE_START_DATE
     */
    public void setEffectiveStartDate(Date value) {
        setAttributeInternal(EFFECTIVESTARTDATE, value);
    }

    /**
     * Gets the attribute value for DEPARTMENT_NUMBER using the alias name DepartmentNumber.
     * @return the DEPARTMENT_NUMBER
     */
    public String getDepartmentNumber() {
        return (String) getAttributeInternal(DEPARTMENTNUMBER);
    }

    /**
     * Sets <code>value</code> as attribute value for DEPARTMENT_NUMBER using the alias name DepartmentNumber.
     * @param value value to set the DEPARTMENT_NUMBER
     */
    public void setDepartmentNumber(String value) {
        setAttributeInternal(DEPARTMENTNUMBER, value);
    }

    /**
     * Gets the attribute value for HIRE_TYPE using the alias name HireType.
     * @return the HIRE_TYPE
     */
    public String getHireType() {
        return (String) getAttributeInternal(HIRETYPE);
    }

    /**
     * Sets <code>value</code> as attribute value for HIRE_TYPE using the alias name HireType.
     * @param value value to set the HIRE_TYPE
     */
    public void setHireType(String value) {
        setAttributeInternal(HIRETYPE, value);
    }

    /**
     * Gets the associated <code>RowIterator</code> using master-detail link ApprovalHistoryVO.
     */
    public RowIterator getApprovalHistoryVO() {
        return (RowIterator)getAttributeInternal(APPROVALHISTORYVO);
    }

    /**
     * Gets the view accessor <code>RowSet</code> RequestStepsROView.
     */
    public RowSet getRequestStepsROView() {
        return (RowSet)getAttributeInternal(REQUESTSTEPSROVIEW);
    }

    /**
     * Gets the view accessor <code>RowSet</code> MonthsLOV1.
     */
    public RowSet getMonthsLOV1() {
        return (RowSet)getAttributeInternal(MONTHSLOV1);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index,
                                           AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value,
                                         AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }
}
