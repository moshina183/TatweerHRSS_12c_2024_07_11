package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.Department;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)
public class DepartmentCollection implements TatweerCollection{
    
    @XmlElement(name="ROW")
    private List<Department> departments;
    
    public DepartmentCollection() {
        super();
        departments=new ArrayList<Department>();
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
