package com.sbm.selfServices.model.views.up;

import com.sbm.selfServices.model.entities.RequestStepsCEO_EOImpl;

import oracle.jbo.domain.Number;
import oracle.jbo.domain.RowID;
import oracle.jbo.domain.Timestamp;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Jul 04 08:13:42 AST 2024
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class RequestStepsCEO_VORowImpl extends ViewRowImpl {


    public static final int ENTITY_REQUESTSTEPSCEO_EO = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    protected enum AttributesEnum {
        BudgetValidation {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        CreatedBy {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        CreatedOn {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        LastUpdatedBy {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        LastUpdatedOn {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        NextStepId {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        RequestTypeId {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        RowID {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        SpecialEdit {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        StepId {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        StepName {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        StepType {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        SpecialEditFlag {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        StepTypeSwitch {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        RequestTypesVO1 {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        ApproverStepType_Stat1 {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        ApproverStepsExitClearance_Stat1 {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        TraineeExitApprovalSteps1 {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        SpecialEditStatVO1 {
            protected Object get(RequestStepsCEO_VORowImpl obj) {
                return obj.getAttributeInternal(index());
            }

            protected void put(RequestStepsCEO_VORowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        ;
        private static final int firstIndex = 0;


        protected int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        protected static final int firstIndex() {
            return firstIndex;
        }

        protected static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        protected static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }


        protected abstract Object get(RequestStepsCEO_VORowImpl object);

        protected abstract void put(RequestStepsCEO_VORowImpl object, Object value);
    }


    public static final int BUDGETVALIDATION = AttributesEnum.BudgetValidation.index();
    public static final int CREATEDBY = AttributesEnum.CreatedBy.index();
    public static final int CREATEDON = AttributesEnum.CreatedOn.index();
    public static final int LASTUPDATEDBY = AttributesEnum.LastUpdatedBy.index();
    public static final int LASTUPDATEDON = AttributesEnum.LastUpdatedOn.index();
    public static final int NEXTSTEPID = AttributesEnum.NextStepId.index();
    public static final int REQUESTTYPEID = AttributesEnum.RequestTypeId.index();
    public static final int ROWID = AttributesEnum.RowID.index();
    public static final int SPECIALEDIT = AttributesEnum.SpecialEdit.index();
    public static final int STEPID = AttributesEnum.StepId.index();
    public static final int STEPNAME = AttributesEnum.StepName.index();
    public static final int STEPTYPE = AttributesEnum.StepType.index();
    public static final int SPECIALEDITFLAG = AttributesEnum.SpecialEditFlag.index();
    public static final int STEPTYPESWITCH = AttributesEnum.StepTypeSwitch.index();
    public static final int REQUESTTYPESVO1 = AttributesEnum.RequestTypesVO1.index();
    public static final int APPROVERSTEPTYPE_STAT1 = AttributesEnum.ApproverStepType_Stat1.index();
    public static final int APPROVERSTEPSEXITCLEARANCE_STAT1 = AttributesEnum.ApproverStepsExitClearance_Stat1.index();
    public static final int TRAINEEEXITAPPROVALSTEPS1 = AttributesEnum.TraineeExitApprovalSteps1.index();
    public static final int SPECIALEDITSTATVO1 = AttributesEnum.SpecialEditStatVO1.index();

    /**
     * This is the default constructor (do not remove).
     */
    public RequestStepsCEO_VORowImpl() {
    }


}

