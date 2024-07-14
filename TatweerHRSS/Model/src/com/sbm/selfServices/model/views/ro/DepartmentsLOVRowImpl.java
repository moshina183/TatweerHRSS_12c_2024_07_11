package com.sbm.selfServices.model.views.ro;

import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Aug 28 22:50:40 EEST 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class DepartmentsLOVRowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        DeptId {
            public Object get(DepartmentsLOVRowImpl obj) {
                return obj.getDeptId();
            }

            public void put(DepartmentsLOVRowImpl obj, Object value) {
                obj.setDeptId((Number)value);
            }
        }
        ,
        DeptName {
            public Object get(DepartmentsLOVRowImpl obj) {
                return obj.getDeptName();
            }

            public void put(DepartmentsLOVRowImpl obj, Object value) {
                obj.setDeptName((String)value);
            }
        }
        ,
        Segment4 {
            public Object get(DepartmentsLOVRowImpl obj) {
                return obj.getSegment4();
            }

            public void put(DepartmentsLOVRowImpl obj, Object value) {
                obj.setSegment4((String)value);
            }
        }
        ,
        Segment2 {
            public Object get(DepartmentsLOVRowImpl obj) {
                return obj.getSegment2();
            }

            public void put(DepartmentsLOVRowImpl obj, Object value) {
                obj.setSegment2((String)value);
            }
        }
        ,
        Segment3 {
            public Object get(DepartmentsLOVRowImpl obj) {
                return obj.getSegment3();
            }

            public void put(DepartmentsLOVRowImpl obj, Object value) {
                obj.setSegment3((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(DepartmentsLOVRowImpl object);

        public abstract void put(DepartmentsLOVRowImpl object, Object value);

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


    public static final int DEPTID = AttributesEnum.DeptId.index();
    public static final int DEPTNAME = AttributesEnum.DeptName.index();
    public static final int SEGMENT4 = AttributesEnum.Segment4.index();
    public static final int SEGMENT2 = AttributesEnum.Segment2.index();
    public static final int SEGMENT3 = AttributesEnum.Segment3.index();

    /**
     * This is the default constructor (do not remove).
     */
    public DepartmentsLOVRowImpl() {
    }

    /**
     * Gets the attribute value for the calculated attribute DeptId.
     * @return the DeptId
     */
    public Number getDeptId() {
        return (Number) getAttributeInternal(DEPTID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DeptId.
     * @param value value to set the  DeptId
     */
    public void setDeptId(Number value) {
        setAttributeInternal(DEPTID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DeptName.
     * @return the DeptName
     */
    public String getDeptName() {
        return (String) getAttributeInternal(DEPTNAME);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DeptName.
     * @param value value to set the  DeptName
     */
    public void setDeptName(String value) {
        setAttributeInternal(DEPTNAME, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Segment4.
     * @return the Segment4
     */
    public String getSegment4() {
        return (String) getAttributeInternal(SEGMENT4);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Segment4.
     * @param value value to set the  Segment4
     */
    public void setSegment4(String value) {
        setAttributeInternal(SEGMENT4, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Segment2.
     * @return the Segment2
     */
    public String getSegment2() {
        return (String) getAttributeInternal(SEGMENT2);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Segment2.
     * @param value value to set the  Segment2
     */
    public void setSegment2(String value) {
        setAttributeInternal(SEGMENT2, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Segment3.
     * @return the Segment3
     */
    public String getSegment3() {
        return (String) getAttributeInternal(SEGMENT3);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Segment3.
     * @param value value to set the  Segment3
     */
    public void setSegment3(String value) {
        setAttributeInternal(SEGMENT3, value);
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