package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class LoanDatFileData {
    
    @XmlElement(name="person_number")
    private String person_number;
    
    
    @XmlElement(name = "ASSIGNMENT_NUMBER")
    private String assignment_number;
    
    public LoanDatFileData() {
        super();
    }

    public void setPerson_number(String person_number) {
        this.person_number = person_number;
    }

    public String getPerson_number() {
        return person_number;
    }

    public void setAssignment_number(String assignment_number) {
        this.assignment_number = assignment_number;
    }

    public String getAssignment_number() {
        return assignment_number;
    }
}
