package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class BankAccountData {
    
    
    @XmlElement(name="PERSON_NUMBER")
    private String PERSON_NUMBER;
    
    
    @XmlElement(name = "FULL_NAME")
    private String FULL_NAME;
    
    @XmlElement(name="ID")
    private String ID;
    
    @XmlElement(name="BANK_ACCOUNT_NUMBER")
    private String BANK_ACCOUNT_NUMBER;
    
    
    @XmlElement(name = "IBAN_NUMBER")
    private String IBAN_NUMBER;
    
    @XmlElement(name="BANK_NAME")
    private String BANK_NAME;
    
    @XmlElement(name="Employee_Bank_ID")
    private String Employee_Bank_ID;
    
    public BankAccountData() {
        super();
    }

    public void setPERSON_NUMBER(String PERSON_NUMBER) {
        this.PERSON_NUMBER = PERSON_NUMBER;
    }

    public String getPERSON_NUMBER() {
        return PERSON_NUMBER;
    }

    public void setFULL_NAME(String FULL_NAME) {
        this.FULL_NAME = FULL_NAME;
    }

    public String getFULL_NAME() {
        return FULL_NAME;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setBANK_ACCOUNT_NUMBER(String BANK_ACCOUNT_NUMBER) {
        this.BANK_ACCOUNT_NUMBER = BANK_ACCOUNT_NUMBER;
    }

    public String getBANK_ACCOUNT_NUMBER() {
        return BANK_ACCOUNT_NUMBER;
    }

    public void setIBAN_NUMBER(String IBAN_NUMBER) {
        this.IBAN_NUMBER = IBAN_NUMBER;
    }

    public String getIBAN_NUMBER() {
        return IBAN_NUMBER;
    }

    public void setBANK_NAME(String BANK_NAME) {
        this.BANK_NAME = BANK_NAME;
    }

    public String getBANK_NAME() {
        return BANK_NAME;
    }

    public void setEmployee_Bank_ID(String Employee_Bank_ID) {
        this.Employee_Bank_ID = Employee_Bank_ID;
    }

    public String getEmployee_Bank_ID() {
        return Employee_Bank_ID;
    }
}
