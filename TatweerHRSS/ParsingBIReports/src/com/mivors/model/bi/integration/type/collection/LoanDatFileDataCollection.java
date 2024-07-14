package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.LoanDatFileData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class LoanDatFileDataCollection implements TatweerCollection {
    @XmlElement(name="ROW")
    private List<LoanDatFileData> loanDatFileData;
    
    public LoanDatFileDataCollection() {
        super();
        loanDatFileData=new ArrayList<LoanDatFileData>();
    }

    public void setLoanDatFileData(List<LoanDatFileData> loanDatFileData) {
        this.loanDatFileData = loanDatFileData;
    }

    public List<LoanDatFileData> getLoanDatFileData() {
        return loanDatFileData;
    }
}
