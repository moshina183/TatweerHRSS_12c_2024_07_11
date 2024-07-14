package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class LocationsReport {
    
    public LocationsReport() {
        super();
    }
    
    @XmlElement(name = "LOCATION_NAME")
    private String LOCATION_NAME; 
    
    @XmlElement(name = "LOCATION_ID")
    private String LOCATION_ID;

    public void setLOCATION(String LOCATION) {
        this.LOCATION_NAME = LOCATION;
    }

    public String getLOCATION() {
        return LOCATION_NAME;
    }

    public void setLOCATION_ID(String LOCATION_ID) {
        this.LOCATION_ID = LOCATION_ID;
    }

    public String getLOCATION_ID() {
        return LOCATION_ID;
    }
}
