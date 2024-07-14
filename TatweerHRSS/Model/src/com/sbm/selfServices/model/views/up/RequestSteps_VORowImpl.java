package com.sbm.selfServices.model.views.up;

import com.sbm.selfServices.model.entities.RequestSteps_EOImpl;

import oracle.jbo.RowSet;
import oracle.jbo.domain.Number;
import oracle.jbo.domain.RowID;
import oracle.jbo.domain.Timestamp;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon Apr 26 18:53:27 IST 2021
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class RequestSteps_VORowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        BudgetValidation {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getBudgetValidation();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setBudgetValidation((String)value);
            }
        }
        ,
        CreatedBy {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getCreatedBy();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setCreatedBy((String)value);
            }
        }
        ,
        CreatedOn {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getCreatedOn();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setCreatedOn((Timestamp)value);
            }
        }
        ,
        LastUpdatedBy {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getLastUpdatedBy();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setLastUpdatedBy((String)value);
            }
        }
        ,
        LastUpdatedOn {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getLastUpdatedOn();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setLastUpdatedOn((Timestamp)value);
            }
        }
        ,
        NextStepId {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getNextStepId();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setNextStepId((Number)value);
            }
        }
        ,
        RequestTypeId {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getRequestTypeId();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setRequestTypeId((Number)value);
            }
        }
        ,
        RowID {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getRowID();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setRowID((RowID)value);
            }
        }
        ,
        StepId {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getStepId();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setStepId((Number)value);
            }
        }
        ,
        StepName {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getStepName();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setStepName((String)value);
            }
        }
        ,
        StepType {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getStepType();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setStepType((String)value);
            }
        }
        ,
        SpecialEdit {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getSpecialEdit();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setSpecialEdit((String)value);
            }
        }
        ,
        SpecialEditFlag {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getSpecialEditFlag();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setSpecialEditFlag((Boolean)value);
            }
        }
        ,
        StepTypeSwitch {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getStepTypeSwitch();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setStepTypeSwitch((String)value);
            }
        }
        ,
        RequestTypesVO1 {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getRequestTypesVO1();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        ApproverStepType_Stat1 {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getApproverStepType_Stat1();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        ApproverStepsExitClearance_Stat1 {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getApproverStepsExitClearance_Stat1();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        TraineeExitApprovalSteps1 {
            public Object get(RequestSteps_VORowImpl obj) {
                return obj.getTraineeExitApprovalSteps1();
            }

            public void put(RequestSteps_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(RequestSteps_VORowImpl object);

        public abstract void put(RequestSteps_VORowImpl object, Object value);

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
    }


    public static final int BUDGETVALIDATION = AttributesEnum.BudgetValidation.index();
    public static final int CREATEDBY = AttributesEnum.CreatedBy.index();
    public static final int CREATEDON = AttributesEnum.CreatedOn.index();
    public static final int LASTUPDATEDBY = AttributesEnum.LastUpdatedBy.index();
    public static final int LASTUPDATEDON = AttributesEnum.LastUpdatedOn.index();
    public static final int NEXTSTEPID = AttributesEnum.NextStepId.index();
    public static final int REQUESTTYPEID = AttributesEnum.RequestTypeId.index();
    public static final int ROWID = AttributesEnum.RowID.index();
    public static final int STEPID = AttributesEnum.StepId.index();
    public static final int STEPNAME = AttributesEnum.StepName.index();
    public static final int STEPTYPE = AttributesEnum.StepType.index();
    public static final int SPECIALEDIT = AttributesEnum.SpecialEdit.index();
    public static final int SPECIALEDITFLAG = AttributesEnum.SpecialEditFlag.index();
    public static final int STEPTYPESWITCH = AttributesEnum.StepTypeSwitch.index();
    public static final int REQUESTTYPESVO1 = AttributesEnum.RequestTypesVO1.index();
    public static final int APPROVERSTEPTYPE_STAT1 = AttributesEnum.ApproverStepType_Stat1.index();
    public static final int APPROVERSTEPSEXITCLEARANCE_STAT1 = AttributesEnum.ApproverStepsExitClearance_Stat1.index();
    public static final int TRAINEEEXITAPPROVALSTEPS1 = AttributesEnum.TraineeExitApprovalSteps1.index();

    /**
     * This is the default constructor (do not remove).
     */
    public RequestSteps_VORowImpl() {
    }

    /**
     * Gets RequestSteps_EO entity object.
     * @return the RequestSteps_EO
     */
    public RequestSteps_EOImpl getRequestSteps_EO() {
        return (RequestSteps_EOImpl)getEntity(0);
    }

    /**
     * Gets the attribute value for BUDGET_VALIDATION using the alias name BudgetValidation.
     * @return the BUDGET_VALIDATION
     */
    public String getBudgetValidation() {
        return (String) getAttributeInternal(BUDGETVALIDATION);
    }

    /**
     * Sets <code>value</code> as attribute value for BUDGET_VALIDATION using the alias name BudgetValidation.
     * @param value value to set the BUDGET_VALIDATION
     */
    public void setBudgetValidation(String value) {
        setAttributeInternal(BUDGETVALIDATION, value);
    }

    /**
     * Gets the attribute value for CREATED_BY using the alias name CreatedBy.
     * @return the CREATED_BY
     */
    public String getCreatedBy() {
        return (String) getAttributeInternal(CREATEDBY);
    }

    /**
     * Sets <code>value</code> as attribute value for CREATED_BY using the alias name CreatedBy.
     * @param value value to set the CREATED_BY
     */
    public void setCreatedBy(String value) {
        setAttributeInternal(CREATEDBY, value);
    }

    /**
     * Gets the attribute value for CREATED_ON using the alias name CreatedOn.
     * @return the CREATED_ON
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) getAttributeInternal(CREATEDON);
    }

    /**
     * Sets <code>value</code> as attribute value for CREATED_ON using the alias name CreatedOn.
     * @param value value to set the CREATED_ON
     */
    public void setCreatedOn(Timestamp value) {
        setAttributeInternal(CREATEDON, value);
    }

    /**
     * Gets the attribute value for LAST_UPDATED_BY using the alias name LastUpdatedBy.
     * @return the LAST_UPDATED_BY
     */
    public String getLastUpdatedBy() {
        return (String) getAttributeInternal(LASTUPDATEDBY);
    }

    /**
     * Sets <code>value</code> as attribute value for LAST_UPDATED_BY using the alias name LastUpdatedBy.
     * @param value value to set the LAST_UPDATED_BY
     */
    public void setLastUpdatedBy(String value) {
        setAttributeInternal(LASTUPDATEDBY, value);
    }

    /**
     * Gets the attribute value for LAST_UPDATED_ON using the alias name LastUpdatedOn.
     * @return the LAST_UPDATED_ON
     */
    public Timestamp getLastUpdatedOn() {
        return (Timestamp) getAttributeInternal(LASTUPDATEDON);
    }

    /**
     * Sets <code>value</code> as attribute value for LAST_UPDATED_ON using the alias name LastUpdatedOn.
     * @param value value to set the LAST_UPDATED_ON
     */
    public void setLastUpdatedOn(Timestamp value) {
        setAttributeInternal(LASTUPDATEDON, value);
    }

    /**
     * Gets the attribute value for NEXT_STEP_ID using the alias name NextStepId.
     * @return the NEXT_STEP_ID
     */
    public Number getNextStepId() {
        return (Number) getAttributeInternal(NEXTSTEPID);
    }

    /**
     * Sets <code>value</code> as attribute value for NEXT_STEP_ID using the alias name NextStepId.
     * @param value value to set the NEXT_STEP_ID
     */
    public void setNextStepId(Number value) {
        setAttributeInternal(NEXTSTEPID, value);
    }

    /**
     * Gets the attribute value for REQUEST_TYPE_ID using the alias name RequestTypeId.
     * @return the REQUEST_TYPE_ID
     */
    public Number getRequestTypeId() {
        return (Number) getAttributeInternal(REQUESTTYPEID);
    }

    /**
     * Sets <code>value</code> as attribute value for REQUEST_TYPE_ID using the alias name RequestTypeId.
     * @param value value to set the REQUEST_TYPE_ID
     */
    public void setRequestTypeId(Number value) {
        setAttributeInternal(REQUESTTYPEID, value);
    }

    /**
     * Gets the attribute value for ROWID using the alias name RowID.
     * @return the ROWID
     */
    public RowID getRowID() {
        return (RowID) getAttributeInternal(ROWID);
    }

    /**
     * Sets <code>value</code> as attribute value for ROWID using the alias name RowID.
     * @param value value to set the ROWID
     */
    public void setRowID(RowID value) {
        setAttributeInternal(ROWID, value);
    }

    /**
     * Gets the attribute value for STEP_ID using the alias name StepId.
     * @return the STEP_ID
     */
    public Number getStepId() {
        return (Number) getAttributeInternal(STEPID);
    }

    /**
     * Sets <code>value</code> as attribute value for STEP_ID using the alias name StepId.
     * @param value value to set the STEP_ID
     */
    public void setStepId(Number value) {
        setAttributeInternal(STEPID, value);
    }

    /**
     * Gets the attribute value for STEP_NAME using the alias name StepName.
     * @return the STEP_NAME
     */
    public String getStepName() {
        return (String) getAttributeInternal(STEPNAME);
    }

    /**
     * Sets <code>value</code> as attribute value for STEP_NAME using the alias name StepName.
     * @param value value to set the STEP_NAME
     */
    public void setStepName(String value) {
        setAttributeInternal(STEPNAME, value);
    }

    /**
     * Gets the attribute value for STEP_TYPE using the alias name StepType.
     * @return the STEP_TYPE
     */
    public String getStepType() {
        return (String) getAttributeInternal(STEPTYPE);
    }

    /**
     * Sets <code>value</code> as attribute value for STEP_TYPE using the alias name StepType.
     * @param value value to set the STEP_TYPE
     */
    public void setStepType(String value) {
        setAttributeInternal(STEPTYPE, value);
    }

    /**
     * Gets the attribute value for SPECIAL_EDIT using the alias name SpecialEdit.
     * @return the SPECIAL_EDIT
     */
    public String getSpecialEdit() {
        return (String) getAttributeInternal(SPECIALEDIT);
    }

    /**
     * Sets <code>value</code> as attribute value for SPECIAL_EDIT using the alias name SpecialEdit.
     * @param value value to set the SPECIAL_EDIT
     */
    public void setSpecialEdit(String value) {
        setAttributeInternal(SPECIALEDIT, value);
    }

    /**
     * Gets the attribute value for the calculated attribute SpecialEditFlag.
     * @return the SpecialEditFlag
     */
    public Boolean getSpecialEditFlag() {
        Boolean flag = false;
        if (getSpecialEdit() != null &&
            getSpecialEdit().equals("Y")) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
//        return (Boolean) getAttributeInternal(SPECIALEDITFLAG);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute SpecialEditFlag.
     * @param value value to set the  SpecialEditFlag
     */
    public void setSpecialEditFlag(Boolean value) {
        if (value) {
            setSpecialEdit("Y");
        } else {
            setSpecialEdit("N");
        }
        setAttributeInternal(SPECIALEDITFLAG, value);
    }

    /**
     * Gets the attribute value for the calculated attribute StepTypeSwitch.
     * @return the StepTypeSwitch
     */
    public String getStepTypeSwitch() {
        String req_id = this.getRequestTypeId().toString();
        if("19".equals(req_id) || "20".equals(req_id) || "21".equals(req_id) || "22".equals(req_id) || "23".equals(req_id)){
            return "LOV_StepTypeEXIT";
        }else if("24".equals(req_id) || "25".equals(req_id) || "26".equals(req_id) || "27".equals(req_id) || "28".equals(req_id)){
            return "LOV_TraineeEXIT";
        }else{
            return "LOV_StepType";
        }
//        return (String) getAttributeInternal(STEPTYPESWITCH);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute StepTypeSwitch.
     * @param value value to set the  StepTypeSwitch
     */
    public void setStepTypeSwitch(String value) {
        setAttributeInternal(STEPTYPESWITCH, value);
    }

    /**
     * Gets the view accessor <code>RowSet</code> RequestTypesVO1.
     */
    public RowSet getRequestTypesVO1() {
        return (RowSet)getAttributeInternal(REQUESTTYPESVO1);
    }

    /**
     * Gets the view accessor <code>RowSet</code> ApproverStepType_Stat1.
     */
    public RowSet getApproverStepType_Stat1() {
        return (RowSet)getAttributeInternal(APPROVERSTEPTYPE_STAT1);
    }

    /**
     * Gets the view accessor <code>RowSet</code> ApproverStepsExitClearance_Stat1.
     */
    public RowSet getApproverStepsExitClearance_Stat1() {
        return (RowSet)getAttributeInternal(APPROVERSTEPSEXITCLEARANCE_STAT1);
    }

    /**
     * Gets the view accessor <code>RowSet</code> TraineeExitApprovalSteps1.
     */
    public RowSet getTraineeExitApprovalSteps1() {
        return (RowSet)getAttributeInternal(TRAINEEEXITAPPROVALSTEPS1);
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