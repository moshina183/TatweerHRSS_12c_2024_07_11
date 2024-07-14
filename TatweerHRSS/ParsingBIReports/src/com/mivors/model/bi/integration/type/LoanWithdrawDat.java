package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class LoanWithdrawDat {
         
    @XmlElement(name = "ASSIGNMENT_NUMBER")
    private String ASSIGNMENT_NUMBER; 
    
    @XmlElement(name = "PERSON_NUMBER")
    private String PERSON_NUMBER; 
    
    @XmlElement(name = "MULTIPLEENTRYCOUNT")
    private String MULTIPLEENTRYCOUNT; 
    
    @XmlElement(name = "EFFECTIVE_START_DATE")
    private String EFFECTIVE_START_DATE; 
    
    @XmlElement(name = "EFFECTIVE_END_DATE")
    private String EFFECTIVE_END_DATE; 
    
    @XmlElement(name = "NO_OF_MONTHS")
    private String NO_OF_MONTHS; 
    
    @XmlElement(name = "DIVISION")
    private String DIVISION; 
    
    @XmlElement(name = "TOTAL_AMOUNT")
    private String TOTAL_AMOUNT; 
    
    @XmlElement(name = "LOAN_CREATION_DATE")
    private String LOAN_CREATION_DATE; 
    
   
    
    public LoanWithdrawDat() {
        super();
    }

    public void setASSIGNMENT_NUMBER(String ASSIGNMENT_NUMBER) {
        this.ASSIGNMENT_NUMBER = ASSIGNMENT_NUMBER;
    }

    public String getASSIGNMENT_NUMBER() {
        return ASSIGNMENT_NUMBER;
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
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

    public void setNO_OF_MONTHS(String NO_OF_MONTHS) {
        this.NO_OF_MONTHS = NO_OF_MONTHS;
    }

    public String getNO_OF_MONTHS() {
        return NO_OF_MONTHS;
    }

    public void setDIVISION(String DIVISION) {
        this.DIVISION = DIVISION;
    }

    public String getDIVISION() {
        return DIVISION;
    }

    public void setTOTAL_AMOUNT(String TOTAL_AMOUNT) {
        this.TOTAL_AMOUNT = TOTAL_AMOUNT;
    }

    public String getTOTAL_AMOUNT() {
        return TOTAL_AMOUNT;
    }

    public void setLOAN_CREATION_DATE(String LOAN_CREATION_DATE) {
        this.LOAN_CREATION_DATE = LOAN_CREATION_DATE;
    }

    public String getLOAN_CREATION_DATE() {
        return LOAN_CREATION_DATE;
    }
}
