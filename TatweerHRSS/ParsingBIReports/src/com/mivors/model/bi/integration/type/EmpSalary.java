package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class EmpSalary {
    
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "PERSON_NAME")
    private String PERSON_NAME;
    
    @XmlElement(name="SALARY_AMOUNT")
    private String SALARY_AMOUNT;
    
    
    
    
    
    public EmpSalary() {
        super();
    }

    public void setPERSON_NUMBER(String person_number) {
        this.PERSON_NUMBER = person_number;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setPERSON_NAME(String FULL_NAME) {
        this.PERSON_NAME = FULL_NAME;
    }

    public String getPERSON_NAME() {
        return PERSON_NAME;
    }

    public void setSALARY_AMOUNT(String SALARY_AMOUNT) {
        this.SALARY_AMOUNT = SALARY_AMOUNT;
    }

    public String getSALARY_AMOUNT() {
        return SALARY_AMOUNT;
    }
}
