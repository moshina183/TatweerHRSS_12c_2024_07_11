package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class InvoiceStatus { 
    
    @XmlElement(name = "TICKET_AMOUNT")
    private String TICKET_AMOUNT;
    
    @XmlElement(name = "BUSINESS_TRIP_AMOUNT")
    private String BUSINESS_TRIP_AMOUNT; 
    
    @XmlElement(name = "PAYMENT_STATUS_FLAG")
    private String PAYMENT_STATUS_FLAG; 
    
    public InvoiceStatus() {
        super();
    }


    public void setTICKET_AMOUNT(String TICKET_AMOUNT) {
        this.TICKET_AMOUNT = TICKET_AMOUNT;
    }

    public String getTICKET_AMOUNT() {
        return TICKET_AMOUNT;
    }

    public void setBUSINESS_TRIP_AMOUNT(String BUSINESS_TRIP_AMOUNT) {
        this.BUSINESS_TRIP_AMOUNT = BUSINESS_TRIP_AMOUNT;
    }

    public String getBUSINESS_TRIP_AMOUNT() {
        return BUSINESS_TRIP_AMOUNT;
    }

    public void setPAYMENT_STATUS_FLAG(String PAYMENT_STATUS_FLAG) {
        this.PAYMENT_STATUS_FLAG = PAYMENT_STATUS_FLAG;
    }

    public String getPAYMENT_STATUS_FLAG() {
        return PAYMENT_STATUS_FLAG;
    }
}
