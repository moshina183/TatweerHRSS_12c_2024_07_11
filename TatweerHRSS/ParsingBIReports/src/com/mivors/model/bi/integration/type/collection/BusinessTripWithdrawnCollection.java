package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.BusinessTripWithdrawn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class BusinessTripWithdrawnCollection implements TatweerCollection{ 
    
    @XmlElement(name="ROW")
    private List<BusinessTripWithdrawn> businessTripWithdrawnList;
    
    public BusinessTripWithdrawnCollection() {
        super();
        businessTripWithdrawnList=new ArrayList<BusinessTripWithdrawn>(); 
    }

    public void setBusinessTripWithdrawnList(List<BusinessTripWithdrawn> businessTripWithdrawnList) {
        this.businessTripWithdrawnList = businessTripWithdrawnList;
    }

    public List<BusinessTripWithdrawn> getBusinessTripWithdrawnList() {
        return businessTripWithdrawnList;
    }
}
