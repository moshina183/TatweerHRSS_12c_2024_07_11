package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.LoanRemaining;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class LoanRemainingCollection implements TatweerCollection {
    @XmlElement(name="ROW")
    private List<LoanRemaining> LoanRemaining;
    
    public LoanRemainingCollection() {
        super();
        LoanRemaining=new ArrayList<LoanRemaining>();
    }

    public void setLoanRemaining(List<LoanRemaining> LoanRemaining) {
        this.LoanRemaining = LoanRemaining;
    }

    public List<LoanRemaining> getLoanRemaining() {
        return LoanRemaining;
    }
}
