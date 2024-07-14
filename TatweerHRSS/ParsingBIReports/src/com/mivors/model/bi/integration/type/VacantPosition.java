package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class  VacantPosition {
     
    @XmlElement(name = "POSITION")
    private String POSITION; 
    
    public VacantPosition() {
        super();
    }

    public void setPOSITION(String POSITION) {
        this.POSITION = POSITION;
    }

    public String getPOSITION() {
        return POSITION;
    }
}