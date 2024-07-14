package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.BankAccountData;



import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class BankAccountDataCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<BankAccountData> listOfBankAccounts;
    
    public BankAccountDataCollection() {
        super();
        listOfBankAccounts=new ArrayList<BankAccountData>();
    }

    public void setListOfBankAccounts(List<BankAccountData> listOfBankAccounts) {
        this.listOfBankAccounts = listOfBankAccounts;
    }

    public List<BankAccountData> getListOfBankAccounts() {
        return listOfBankAccounts;
    }
}
