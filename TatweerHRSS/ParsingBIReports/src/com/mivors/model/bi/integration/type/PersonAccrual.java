package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class PersonAccrual {
    
    @XmlElement(name = "PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    @XmlElement(name = "PER_ACCRUAL_ENTRY_ID")
    private String PER_ACCRUAL_ENTRY_ID;
    
    @XmlElement(name="ACCRUAL_PERIOD")
    private String ACCRUAL_PERIOD;
    
    @XmlElement(name="END_BAL")
    private String END_BAL;
    
    @XmlElement(name = "ASSIGNMENT_NUMBER")
    private String ASSIGNMENT_NUMBER;
    
    public PersonAccrual() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setPER_ACCRUAL_ENTRY_ID(String PER_ACCRUAL_ENTRY_ID) {
        this.PER_ACCRUAL_ENTRY_ID = PER_ACCRUAL_ENTRY_ID;
    }

    public String getPER_ACCRUAL_ENTRY_ID() {
        return PER_ACCRUAL_ENTRY_ID;
    }

    public void setACCRUAL_PERIOD(String ACCRUAL_PERIOD) {
        this.ACCRUAL_PERIOD = ACCRUAL_PERIOD;
    }

    public String getACCRUAL_PERIOD() {
        return ACCRUAL_PERIOD;
    }

    public void setEND_BAL(String END_BAL) {
        this.END_BAL = END_BAL;
    }

    public String getEND_BAL() {
        return END_BAL;
    }

    public void setASSIGNMENT_NUMBER(String ASSIGNMENT_NUMBER) {
        this.ASSIGNMENT_NUMBER = ASSIGNMENT_NUMBER;
    }

    public String getASSIGNMENT_NUMBER() {
        return ASSIGNMENT_NUMBER;
    }
}
