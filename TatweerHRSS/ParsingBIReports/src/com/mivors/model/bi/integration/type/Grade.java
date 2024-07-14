package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class Grade {
    
    @XmlElement(name = "GRADE_ID")
    private String GRADE_ID;
    
    @XmlElement(name="NAME")
    private String NAME;
    
    
   
    
    public Grade() {
        super();
    }

    public void setGRADE_ID(String GRADE_ID) {
        this.GRADE_ID = GRADE_ID;
    }

    public String getGRADE_ID() {
        return GRADE_ID;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getNAME() {
        return NAME;
    }
}
