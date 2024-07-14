package com.mivors.model.bi.integration.type.collection;
import com.mivors.model.bi.integration.type.AllPersonsReport;

import com.mivors.model.bi.integration.type.ChildDetailsReport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class ChildDetailsCollection implements TatweerCollection{
    @XmlElement(name="ROW")
    private List<ChildDetailsReport> childDetails;
    public ChildDetailsCollection() {
        super();
        childDetails=new ArrayList<ChildDetailsReport>();
    }

    public void setChildDetails(List<ChildDetailsReport> childDetails) {
        this.childDetails = childDetails;
    }

    public List<ChildDetailsReport> getChildDetails() {
        return childDetails;
    }
}
