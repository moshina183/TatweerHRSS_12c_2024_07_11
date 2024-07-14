package com.mivors.model.bi.integration.type;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoanRemaining {
    
    @XmlElement(name = "PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    @XmlElement(name = "EMPLOYEENAME")
    private String EMPLOYEENAME;
    
    @XmlElement(name = "LOANREMAINING")
    private String LOANREMAINING;
    
    public LoanRemaining() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setEMPLOYEENAME(String EMPLOYEENAME) {
        this.EMPLOYEENAME = EMPLOYEENAME;
    }

    public String getEMPLOYEENAME() {
        return EMPLOYEENAME;
    }

    public void setLOANREMAINING(String LOANREMAINING) {
        this.LOANREMAINING = LOANREMAINING;
    }

    public String getLOANREMAINING() {
        return LOANREMAINING;
    }
}
