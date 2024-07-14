package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.AllPersonsReport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class AllPersonsCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<AllPersonsReport> allPersons;
    
    public AllPersonsCollection() {
        super();
        allPersons=new ArrayList<AllPersonsReport>();
    }

    public void setAllPersons(List<AllPersonsReport> allPersons) {
        this.allPersons = allPersons;
    }

    public List<AllPersonsReport> getAllPersons() {
        return allPersons;
    }
}
