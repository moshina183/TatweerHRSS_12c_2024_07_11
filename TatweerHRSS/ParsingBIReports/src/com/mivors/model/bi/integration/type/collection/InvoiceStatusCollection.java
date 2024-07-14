package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.InvoiceStatus;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class InvoiceStatusCollection implements TatweerCollection{ 
    
    @XmlElement(name="ROW")
    private List<InvoiceStatus> invoiceStatusList;
    
    public InvoiceStatusCollection() {
        super();
        invoiceStatusList=new ArrayList<InvoiceStatus>(); 
    }

    public void setInvoiceStatusList(List<InvoiceStatus> invoiceStatusList) {
        this.invoiceStatusList = invoiceStatusList;
    }

    public List<InvoiceStatus> getInvoiceStatusList() {
        return invoiceStatusList;
    }
}
