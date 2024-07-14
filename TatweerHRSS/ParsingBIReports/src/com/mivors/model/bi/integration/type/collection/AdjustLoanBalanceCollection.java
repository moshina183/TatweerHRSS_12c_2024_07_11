package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.AdjustLoanBalance;
 
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class AdjustLoanBalanceCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<AdjustLoanBalance> adjustLoanBalance;
    
    public AdjustLoanBalanceCollection() {
        super();
        adjustLoanBalance=new ArrayList<AdjustLoanBalance>();
    }

    public void setAdjustLoanBalance(List<AdjustLoanBalance> adjustLoanBalance) {
        this.adjustLoanBalance = adjustLoanBalance;
    }

    public List<AdjustLoanBalance> getAdjustLoanBalance() {
        return adjustLoanBalance;
    }
}
