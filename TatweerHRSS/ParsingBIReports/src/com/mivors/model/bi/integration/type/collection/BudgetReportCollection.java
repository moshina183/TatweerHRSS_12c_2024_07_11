package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.BudgetReportType;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)
public class BudgetReportCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<BudgetReportType>  BudgetReportType;
    
    public BudgetReportCollection() {
        super();
        BudgetReportType=new ArrayList<BudgetReportType>();
    }

    public void setListofBudgetValues(List<BudgetReportType> listofBudgetValues) {
        this.BudgetReportType = listofBudgetValues;
    }

    public List<BudgetReportType> getListofBudgetValues() {
        return BudgetReportType;
    }
}
