package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class ManagerEmployee {
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "MANAGER_NUMBER")
    private String MANAGER_NUMBER;
     
    @XmlElement(name = "DISPLAY_NAME")
    private String DISPLAY_NAME;
    
    @XmlElement(name = "SALARY_AMOUNT")
    private String SALARY_AMOUNT;
    
    public ManagerEmployee() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setMANAGER_NUMBER(String MANAGER_NUMBER) {
        this.MANAGER_NUMBER = MANAGER_NUMBER;
    }

    public String getMANAGER_NUMBER() {
        return MANAGER_NUMBER;
    }

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setSALARY_AMOUNT(String SALARY_AMOUNT) {
        this.SALARY_AMOUNT = SALARY_AMOUNT;
    }

    public String getSALARY_AMOUNT() {
        return SALARY_AMOUNT;
    }
}
