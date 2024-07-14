package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class PositionWorker {
    
    
    @XmlElement(name = "WORKER_TYPE")
    private String WORKER_TYPE;
    
    @XmlElement(name="WORK_TERM_NUMBER")
    private String WORK_TERM_NUMBER;
    
    @XmlElement(name = "PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    @XmlElement(name="ASSIGNMENT_ID")
    private String ASSIGNMENT_ID;
    
    @XmlElement(name = "ASSIGNMENT_NUMBER")
    private String ASSIGNMENT_NUMBER;
    
    @XmlElement(name="ACTION_CODE")
    private String ACTION_CODE;
    
    @XmlElement(name = "ASSIGNMENT_TYPE")
    private String ASSIGNMENT_TYPE;
    
    @XmlElement(name="EFFECTIVE_LATEST_CHANGE")
    private String EFFECTIVE_LATEST_CHANGE;
    
    @XmlElement(name = "GRADE_ID")
    private String GRADE_ID;
    
    @XmlElement(name="JOB_ID")
    private String JOB_ID;
    
    @XmlElement(name = "LOCATION_ID")
    private String LOCATION_ID;

    @XmlElement(name = "ORGANIZATION_ID")
    private String ORGANIZATION_ID;
    
    @XmlElement(name="EFFECTIVE_SEQUENCE")
    private String EFFECTIVE_SEQUENCE;
    
    @XmlElement(name="EFFECTIVE_START_DATE")
    private String EFFECTIVE_START_DATE;
    
    @XmlElement(name = "PERSON_ID")
    private String PERSON_ID;
    
    @XmlElement(name="PERIOD_OF_SERVICE_ID")
    private String PERIOD_OF_SERVICE_ID;
    
    @XmlElement(name = "DATE_START")
    private String DATE_START;
    
    @XmlElement(name="POSITION_ID")
    private String POSITION_ID;
    
    @XmlElement(name = "PRIMARY_WORK_TERMS_FLAG")
    private String PRIMARY_WORK_TERMS_FLAG;
    
    @XmlElement(name="PRIMARY_ASSIGNMENT_FLAG")
    private String PRIMARY_ASSIGNMENT_FLAG;
    
    @XmlElement(name="WORK_TERM_ASSIGNMENT_ID")
    private String WORK_TERM_ASSIGNMENT_ID;
    
    public PositionWorker() {
        super();
    }

    public void setWORKER_TYPE(String WORKER_TYPE) {
        this.WORKER_TYPE = WORKER_TYPE;
    }

    public String getWORKER_TYPE() {
        return WORKER_TYPE;
    }

    public void setWORK_TERM_NUMBER(String WORK_TERM_NUMBER) {
        this.WORK_TERM_NUMBER = WORK_TERM_NUMBER;
    }

    public String getWORK_TERM_NUMBER() {
        return WORK_TERM_NUMBER;
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setASSIGNMENT_ID(String ASSIGNMENT_ID) {
        this.ASSIGNMENT_ID = ASSIGNMENT_ID;
    }

    public String getASSIGNMENT_ID() {
        return ASSIGNMENT_ID;
    }

    public void setASSIGNMENT_NUMBER(String ASSIGNMENT_NUMBER) {
        this.ASSIGNMENT_NUMBER = ASSIGNMENT_NUMBER;
    }

    public String getASSIGNMENT_NUMBER() {
        return ASSIGNMENT_NUMBER;
    }

    public void setACTION_CODE(String ACTION_CODE) {
        this.ACTION_CODE = ACTION_CODE;
    }

    public String getACTION_CODE() {
        return ACTION_CODE;
    }

    public void setASSIGNMENT_TYPE(String ASSIGNMENT_TYPE) {
        this.ASSIGNMENT_TYPE = ASSIGNMENT_TYPE;
    }

    public String getASSIGNMENT_TYPE() {
        return ASSIGNMENT_TYPE;
    }

    public void setEFFECTIVE_LATEST_CHANGE(String EFFECTIVE_LATEST_CHANGE) {
        this.EFFECTIVE_LATEST_CHANGE = EFFECTIVE_LATEST_CHANGE;
    }

    public String getEFFECTIVE_LATEST_CHANGE() {
        return EFFECTIVE_LATEST_CHANGE;
    }

    public void setGRADE_ID(String GRADE_ID) {
        this.GRADE_ID = GRADE_ID;
    }

    public String getGRADE_ID() {
        return GRADE_ID;
    }

    public void setJOB_ID(String JOB_ID) {
        this.JOB_ID = JOB_ID;
    }

    public String getJOB_ID() {
        return JOB_ID;
    }

    public void setLOCATION_ID(String LOCATION_ID) {
        this.LOCATION_ID = LOCATION_ID;
    }

    public String getLOCATION_ID() {
        return LOCATION_ID;
    }

    public void setORGANIZATION_ID(String ORGANIZATION_ID) {
        this.ORGANIZATION_ID = ORGANIZATION_ID;
    }

    public String getORGANIZATION_ID() {
        return ORGANIZATION_ID;
    }

    public void setEFFECTIVE_SEQUENCE(String EFFECTIVE_SEQUENCE) {
        this.EFFECTIVE_SEQUENCE = EFFECTIVE_SEQUENCE;
    }

    public String getEFFECTIVE_SEQUENCE() {
        return EFFECTIVE_SEQUENCE;
    }

    public void setEFFECTIVE_START_DATE(String EFFECTIVE_START_DATE) {
        this.EFFECTIVE_START_DATE = EFFECTIVE_START_DATE;
    }

    public String getEFFECTIVE_START_DATE() {
        return EFFECTIVE_START_DATE;
    }

    public void setPERSON_ID(String PERSON_ID) {
        this.PERSON_ID = PERSON_ID;
    }

    public String getPERSON_ID() {
        return PERSON_ID;
    }

    public void setPERIOD_OF_SERVICE_ID(String PERIOD_OF_SERVICE_ID) {
        this.PERIOD_OF_SERVICE_ID = PERIOD_OF_SERVICE_ID;
    }

    public String getPERIOD_OF_SERVICE_ID() {
        return PERIOD_OF_SERVICE_ID;
    }

    public void setDATE_START(String DATE_START) {
        this.DATE_START = DATE_START;
    }

    public String getDATE_START() {
        return DATE_START;
    }

    public void setPOSITION_ID(String POSITION_ID) {
        this.POSITION_ID = POSITION_ID;
    }

    public String getPOSITION_ID() {
        return POSITION_ID;
    }

    public void setPRIMARY_WORK_TERMS_FLAG(String PRIMARY_WORK_TERMS_FLAG) {
        this.PRIMARY_WORK_TERMS_FLAG = PRIMARY_WORK_TERMS_FLAG;
    }

    public String getPRIMARY_WORK_TERMS_FLAG() {
        return PRIMARY_WORK_TERMS_FLAG;
    }

    public void setPRIMARY_ASSIGNMENT_FLAG(String PRIMARY_ASSIGNMENT_FLAG) {
        this.PRIMARY_ASSIGNMENT_FLAG = PRIMARY_ASSIGNMENT_FLAG;
    }

    public String getPRIMARY_ASSIGNMENT_FLAG() {
        return PRIMARY_ASSIGNMENT_FLAG;
    }

    public void setWORK_TERM_ASSIGNMENT_ID(String WORK_TERM_ASSIGNMENT_ID) {
        this.WORK_TERM_ASSIGNMENT_ID = WORK_TERM_ASSIGNMENT_ID;
    }

    public String getWORK_TERM_ASSIGNMENT_ID() {
        return WORK_TERM_ASSIGNMENT_ID;
    }
}
