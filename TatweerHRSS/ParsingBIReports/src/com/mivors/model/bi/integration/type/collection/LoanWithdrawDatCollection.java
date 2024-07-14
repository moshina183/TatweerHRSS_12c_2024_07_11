package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.LoanWithdrawDat;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class LoanWithdrawDatCollection implements TatweerCollection {
    public LoanWithdrawDatCollection() {
        super();
        loanWithDraw = new ArrayList<LoanWithdrawDat>();
    }
    
    @XmlElement(name="ROW")
    private List<LoanWithdrawDat> loanWithDraw;

    public void setLoanWithDraw(List<LoanWithdrawDat> loanWithDraw) {
        this.loanWithDraw = loanWithDraw;
    }

    public List<LoanWithdrawDat> getLoanWithDraw() {
        return loanWithDraw;
    }
}
