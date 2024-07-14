package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.EmpSalary;

import com.mivors.model.bi.integration.type.EmployeeChild;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)


public class EmployeeChildsCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<EmployeeChild> empChilds;
    
    public EmployeeChildsCollection() {
        super();
        empChilds=new ArrayList<EmployeeChild>();
    }

    public void setEmpChilds(List<EmployeeChild> empChilds) {
        this.empChilds = empChilds;
    }

    public List<EmployeeChild> getEmpChilds() {
        return empChilds;
    }
}
