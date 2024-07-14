package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class OvertimeDatWithdraw {
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    @XmlElement(name="ASSIGNMENT_NUMBER")
    private String ASSIGNMENT_NUMBER;
    
    @XmlElement(name="MULTIPLEENTRYCOUNT")
    private String MULTIPLEENTRYCOUNT;
    
    @XmlElement(name="EFFECTIVE_START_DATE")
    private String EFFECTIVE_START_DATE;
    
    @XmlElement(name="EFFECTIVE_END_DATE")
    private String EFFECTIVE_END_DATE;
    
    @XmlElement(name="SCREENENTRYVALUE")
    private String SCREENENTRYVALUE;
    
    public OvertimeDatWithdraw() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setASSIGNMENT_NUMBER(String ASSIGNMENT_NUMBER) {
        this.ASSIGNMENT_NUMBER = ASSIGNMENT_NUMBER;
    }

    public String getASSIGNMENT_NUMBER() {
        return ASSIGNMENT_NUMBER;
    }

    public void setMULTIPLEENTRYCOUNT(String MULTIPLEENTRYCOUNT) {
        this.MULTIPLEENTRYCOUNT = MULTIPLEENTRYCOUNT;
    }

    public String getMULTIPLEENTRYCOUNT() {
        return MULTIPLEENTRYCOUNT;
    }

    public void setEFFECTIVE_START_DATE(String EFFECTIVE_START_DATE) {
        this.EFFECTIVE_START_DATE = EFFECTIVE_START_DATE;
    }

    public String getEFFECTIVE_START_DATE() {
        return EFFECTIVE_START_DATE;
    }

    public void setEFFECTIVE_END_DATE(String EFFECTIVE_END_DATE) {
        this.EFFECTIVE_END_DATE = EFFECTIVE_END_DATE;
    }

    public String getEFFECTIVE_END_DATE() {
        return EFFECTIVE_END_DATE;
    }

    public void setSCREENENTRYVALUE(String SCREENENTRYVALUE) {
        this.SCREENENTRYVALUE = SCREENENTRYVALUE;
    }

    public String getSCREENENTRYVALUE() {
        return SCREENENTRYVALUE;
    }
}
