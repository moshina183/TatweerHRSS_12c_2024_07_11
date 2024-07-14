package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.ManagerEmployee;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)


public class ManagerEmployeesCollection implements TatweerCollection {

    @XmlElement(name = "ROW")
    private List<ManagerEmployee> mngrEmployees;

    public ManagerEmployeesCollection() {
        super();
        mngrEmployees = new ArrayList<ManagerEmployee>();
    }

    public void setMngrEmployees(List<ManagerEmployee> mngrEmployees) {
        this.mngrEmployees = mngrEmployees;
    }

    public List<ManagerEmployee> getMngrEmployees() {
        return mngrEmployees;
    }
}
