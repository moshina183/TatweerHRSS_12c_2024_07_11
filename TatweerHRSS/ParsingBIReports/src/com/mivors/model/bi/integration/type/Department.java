package com.mivors.model.bi.integration.type;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class  Department {
   
    @XmlElement(name="NAME")
    private String NAME;
    
    
    @XmlElement(name = "ORGANIZATION_ID")
    private String ORGANIZATION_ID;
     
    @XmlElement(name = "SEGMENT4")
    private String SEGMENT4;
    
    @XmlElement(name = "SEGMENT2")
    private String SEGMENT2;
    
    @XmlElement(name = "SEGMENT3")
    private String SEGMENT3;

    public Department() {
        super();
    }


    public void setORGANIZATION_ID(String ORGANIZATION_ID) {
        this.ORGANIZATION_ID = ORGANIZATION_ID;
    }

    public String getORGANIZATION_ID() {
        return ORGANIZATION_ID;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getNAME() {
        return NAME;
    }

    public void setSEGMENT4(String SEGMENT4) {
        this.SEGMENT4 = SEGMENT4;
    }

    public String getSEGMENT4() {
        return SEGMENT4;
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
}
