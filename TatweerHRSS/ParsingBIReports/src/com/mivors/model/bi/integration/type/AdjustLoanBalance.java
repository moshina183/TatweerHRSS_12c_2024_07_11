package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class AdjustLoanBalance {
    
    @XmlElement(name = "PERSON_NUMBER")
    private String PERSON_NUMBER; 
    
    @XmlElement(name = "ASSIGNMENT_NUMBER")
    private String ASSIGNMENT_NUMBER; 
    
    @XmlElement(name = "MULTIPLEENTRYCOUNT")
    private String MULTIPLEENTRYCOUNT; 
    
    
    public AdjustLoanBalance() {
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
}
