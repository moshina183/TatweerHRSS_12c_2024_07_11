package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class BudgetReportType {
    

    @XmlElement(name="BUDGET_AMOUNT")
    private String FUNDSAVAILABLEAMOUNT;

    public void setFUNDSAVAILABLEAMOUNT(String FUNDSAVAILABLEAMOUNT) {
        this.FUNDSAVAILABLEAMOUNT = FUNDSAVAILABLEAMOUNT;
    }

    public String getFUNDSAVAILABLEAMOUNT() {
        return FUNDSAVAILABLEAMOUNT;
    }
}
