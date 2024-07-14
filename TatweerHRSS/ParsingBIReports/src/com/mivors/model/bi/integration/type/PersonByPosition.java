package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class PersonByPosition {
    
    
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name="DISPLAY_NAME")
    private String DISPLAY_NAME;
    
    
    @XmlElement(name = "POSITION_CODE")
    private String POSITION_CODE;
    
    
    @XmlElement(name = "POSITION_NAME")
    private String POSITION_NAME;
    
    @XmlElement(name = "EMAIL_ADDRESS")
    private String EMAIL_ADDRESS;
    
    
    
    
    
    
    public PersonByPosition() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setPOSITION_CODE(String POSITION_CODE) {
        this.POSITION_CODE = POSITION_CODE;
    }

    public String getPOSITION_CODE() {
        return POSITION_CODE;
    }

    public void setPOSITION_NAME(String POSITION_NAME) {
        this.POSITION_NAME = POSITION_NAME;
    }

    public String getPOSITION_NAME() {
        return POSITION_NAME;
    }

    public void setEMAIL_ADDRESS(String EMAIL_ADDRESS) {
        this.EMAIL_ADDRESS = EMAIL_ADDRESS;
    }

    public String getEMAIL_ADDRESS() {
        return EMAIL_ADDRESS;
    }
}
