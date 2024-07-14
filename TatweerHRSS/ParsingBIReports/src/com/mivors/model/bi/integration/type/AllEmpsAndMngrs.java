package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class AllEmpsAndMngrs {
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    @XmlElement(name="EMPLOYEE_NAME")
    private String EMPLOYEE_NAME ;
    
    
    @XmlElement(name = "MANAGER_NUMBER")
    private String MANAGER_NUMBER;
    
    
    @XmlElement(name="MANAGER_NAME")
    private String MANAGER_NAME;
                                        
    
    
    public AllEmpsAndMngrs() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }


    public void setEMPLOYEE_NAME(String EMPLOYEE_NAME) {
        this.EMPLOYEE_NAME = EMPLOYEE_NAME;
    }

    public String getEMPLOYEE_NAME() {
        return EMPLOYEE_NAME;
    }

    public void setMANAGER_NUMBER(String MANAGER_NUMBER) {
        this.MANAGER_NUMBER = MANAGER_NUMBER;
    }

    public String getMANAGER_NUMBER() {
        return MANAGER_NUMBER;
    }

    public void setMANAGER_NAME(String MANAGER_NAME) {
        this.MANAGER_NAME = MANAGER_NAME;
    }

    public String getMANAGER_NAME() {
        return MANAGER_NAME;
    }
}

