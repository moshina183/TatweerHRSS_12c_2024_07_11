package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class EducationNoOfChildren {
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    @XmlElement(name="DISPLAY_NAME")
    private String DISPLAY_NAME;
    
    
    @XmlElement(name = "MARITAL_STATUS")
    private String MARITAL_STATUS;
    
    
    @XmlElement(name="NUMBER_OF_CHILDS")
    private String NUMBER_OF_CHILDS;
                                        
    
    
    public EducationNoOfChildren() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setMARITAL_STATUS(String MARITAL_STATUS) {
        this.MARITAL_STATUS = MARITAL_STATUS;
    }

    public String getMARITAL_STATUS() {
        return MARITAL_STATUS;
    }

    public void setNUMBER_OF_CHILDS(String NUMBER_OF_CHILDS) {
        this.NUMBER_OF_CHILDS = NUMBER_OF_CHILDS;
    }

    public String getNUMBER_OF_CHILDS() {
        return NUMBER_OF_CHILDS;
    }
}
