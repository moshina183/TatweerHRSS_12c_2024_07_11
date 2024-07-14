package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class ActionReason {
    
    
    @XmlElement(name = "PERSON_ID")
    private String PERSON_ID;
    
    @XmlElement(name = "FULL_NAME")
    private String FULL_NAME;
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "ACTION_REASON_CODE")
    private String ACTION_REASON_CODE;
    

    
    
    
    public ActionReason() {
        super();
    }

    public void setPERSON_ID(String PERSON_ID) {
        this.PERSON_ID = PERSON_ID;
    }

    public String getPERSON_ID() {
        return PERSON_ID;
    }

    public void setFULL_NAME(String FULL_NAME) {
        this.FULL_NAME = FULL_NAME;
    }

    public String getFULL_NAME() {
        return FULL_NAME;
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setACTION_REASON_CODE(String ACTION_REASON_CODE) {
        this.ACTION_REASON_CODE = ACTION_REASON_CODE;
    }

    public String getACTION_REASON_CODE() {
        return ACTION_REASON_CODE;
    }
}
