package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class PerDiem {
    
    @XmlElement(name = "VALUE")
    private String VALUE;
    
    @XmlElement(name = "GRADE")
    private String GRADE;
    
    public PerDiem() {
        super();
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setGRADE(String GRADE) {
        this.GRADE = GRADE;
    }

    public String getGRADE() {
        return GRADE;
    }
}


