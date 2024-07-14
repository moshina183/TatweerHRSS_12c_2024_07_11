package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.Department;

import com.mivors.model.bi.integration.type.Grade;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class GradesCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<Grade> grades;
    
    public GradesCollection() {
        super();
        grades=new ArrayList<Grade>();
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public List<Grade> getGrades() {
        return grades;
    }
}
