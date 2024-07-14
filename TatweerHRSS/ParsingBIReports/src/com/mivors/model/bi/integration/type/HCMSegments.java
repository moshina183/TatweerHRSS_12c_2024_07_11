package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class HCMSegments {
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "SEGMENT2")
    private String SEGMENT2;
    
    @XmlElement(name="SEGMENT3")
    private String SEGMENT3;
    
    
    @XmlElement(name = "SEGMENT4")
    private String SEGMENT4;
    
    
    public HCMSegments() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setSEGMENT2(String SEGMENT2) {
        this.SEGMENT2 = SEGMENT2;
    }

    public String getSEGMENT2() {
        return SEGMENT2;
    }

    public void setSEGMENT3(String SEGMENT3) {
        this.SEGMENT3 = SEGMENT3;
    }

    public String getSEGMENT3() {
        return SEGMENT3;
    }

    public void setSEGMENT4(String SEGMENT4) {
        this.SEGMENT4 = SEGMENT4;
    }

    public String getSEGMENT4() {
        return SEGMENT4;
    }
}
