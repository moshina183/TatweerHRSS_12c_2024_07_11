package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class ManagerOfDept {
    
    @XmlElement(name="NAME")
    private String NAME;
    
    
    @XmlElement(name = "ORGANIZATION_ID")
    private String ORGANIZATION_ID;
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "PERSON_ID")
    private String PERSON_ID;
    
    @XmlElement(name = "FULL_NAME")
    private String FULL_NAME;
    
    @XmlElement(name = "DISPLAY_NAME")
    private String DISPLAY_NAME;
    
    @XmlElement(name = "EMAIL_ADDRESS")
    private String EMAIL_ADDRESS;
    
    
    public ManagerOfDept() {
        super();
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getNAME() {
        return NAME;
    }

    public void setORGANIZATION_ID(String ORGANIZATION_ID) {
        this.ORGANIZATION_ID = ORGANIZATION_ID;
    }

    public String getORGANIZATION_ID() {
        return ORGANIZATION_ID;
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
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

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setEMAIL_ADDRESS(String EMAIL_ADDRESS) {
        this.EMAIL_ADDRESS = EMAIL_ADDRESS;
    }

    public String getEMAIL_ADDRESS() {
        return EMAIL_ADDRESS;
    }
}
