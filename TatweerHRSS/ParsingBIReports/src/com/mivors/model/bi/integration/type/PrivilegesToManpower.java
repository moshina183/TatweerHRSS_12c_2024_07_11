package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class PrivilegesToManpower {
    
    @XmlElement(name = "POSITION")
    private String POSITION;
    
    @XmlElement(name = "PRIVILEGES_TO_MR")
    private String PRIVILEGES_TO_MR;
    
    public PrivilegesToManpower() {
        super();
    }

    public void setPOSITION(String POSITION) {
        this.POSITION = POSITION;
    }

    public String getPOSITION() {
        return POSITION;
    }

    public void setPRIVILEGES_TO_MR(String PRIVILEGES_TO_MR) {
        this.PRIVILEGES_TO_MR = PRIVILEGES_TO_MR;
    }

    public String getPRIVILEGES_TO_MR() {
        return PRIVILEGES_TO_MR;
    }
}
