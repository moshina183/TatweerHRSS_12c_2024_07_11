package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class EmployeeChild {
    
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "MARITAL_STATUS")
    private String MARITAL_STATUS;
    
    @XmlElement(name="HIRE_DATE")
    private String HIRE_DATE;
    
    @XmlElement(name="NUMBER_OF_CHILDS")
    private String NUMBER_OF_CHILDS;
                                        
    @XmlElement(name="NATIONALITY")
    private String NATIONALITY;
    
    
    public EmployeeChild() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setMARITAL_STATUS(String MARITAL_STATUS) {
        this.MARITAL_STATUS = MARITAL_STATUS;
    }

    public String getMARITAL_STATUS() {
        return MARITAL_STATUS;
    }

    public void setHIRE_DATE(String HIRE_DATE) {
        this.HIRE_DATE = HIRE_DATE;
    }

    public String getHIRE_DATE() {
        return HIRE_DATE;
    }

    public void setNUMBER_OF_CHILDS(String NUMBER_OF_CHILDS) {
        this.NUMBER_OF_CHILDS = NUMBER_OF_CHILDS;
    }

    public String getNUMBER_OF_CHILDS() {
        return NUMBER_OF_CHILDS;
    }

    public void setNATIONALITY(String NATIONALITY) {
        this.NATIONALITY = NATIONALITY;
    }

    public String getNATIONALITY() {
        return NATIONALITY;
    }
}
