package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class EmpIsMngr {
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "MANAGER_FLAG")
    private String MANAGER_FLAG;
    
    public EmpIsMngr() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setMANAGER_FLAG(String MANAGER_FLAG) {
        this.MANAGER_FLAG = MANAGER_FLAG;
    }

    public String getMANAGER_FLAG() {
        return MANAGER_FLAG;
    }
}
