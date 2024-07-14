package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.LocationsReport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class LocationsCollection implements TatweerCollection{
    
    @XmlElement(name="ROW")
    private List<LocationsReport> locations;
    
    public LocationsCollection() {
        super();
        locations = new ArrayList<LocationsReport>();
    }

    public void setLocations(List<LocationsReport> locations) {
        this.locations = locations;
    }

    public List<LocationsReport> getLocations() {
        return locations;
    }
}
