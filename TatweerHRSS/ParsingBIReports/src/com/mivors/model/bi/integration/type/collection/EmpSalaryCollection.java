package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.EmpSalary;
import com.mivors.model.bi.integration.type.HCMSegments;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class EmpSalaryCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<EmpSalary> empSalaries;
    
    public EmpSalaryCollection() {
        super();
        empSalaries=new ArrayList<EmpSalary>();
    }

    public void setEmpSalaries(List<EmpSalary> empSalaries) {
        this.empSalaries = empSalaries;
    }

    public List<EmpSalary> getEmpSalaries() {
        return empSalaries;
    }
}
