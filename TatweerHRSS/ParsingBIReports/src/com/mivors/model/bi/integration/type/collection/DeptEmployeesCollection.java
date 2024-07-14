package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.DeptEmployee;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class DeptEmployeesCollection implements TatweerCollection {

    @XmlElement(name = "ROW")
    private List<DeptEmployee> deptEmployees;

    public DeptEmployeesCollection() {
        super();
        deptEmployees = new ArrayList<DeptEmployee>();
    }

    public void setDeptEmployees(List<DeptEmployee> deptEmployees) {
        this.deptEmployees = deptEmployees;
    }

    public List<DeptEmployee> getDeptEmployees() {
        return deptEmployees;
    }
}
