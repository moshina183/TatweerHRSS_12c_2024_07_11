package com.mivors.model.bi.integration.type;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class MobileAllowanceDetails {
    
    @XmlElement(name = "PERSON_NUMBER")
    private String PERSON_NUMBER; 
    
    @XmlElement(name = "GRADE_CODE")
    private String GRADE_CODE; 
    
    @XmlElement(name = "AMOUNT")
    private String AMOUNT; 
    
    public MobileAllowanceDetails() {
        super();
    }


    public MobileAllowanceDetails(String PERSON_NUMBER, String GRADE_CODE, String AMOUNT) {
        this.PERSON_NUMBER = PERSON_NUMBER;
        this.GRADE_CODE = GRADE_CODE;
        this.AMOUNT = AMOUNT;
    }


    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setGRADE_CODE(String GRADE_CODE) {
        this.GRADE_CODE = GRADE_CODE;
    }

    public String getGRADE_CODE() {
        return GRADE_CODE;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getAMOUNT() {
        return AMOUNT;
    }


}
