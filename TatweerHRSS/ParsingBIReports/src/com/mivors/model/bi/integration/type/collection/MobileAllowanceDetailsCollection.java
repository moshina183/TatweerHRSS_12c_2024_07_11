package com.mivors.model.bi.integration.type.collection;


import com.mivors.model.bi.integration.type.MobileAllowanceDetails;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)
public class MobileAllowanceDetailsCollection implements TatweerCollection {
    
    
    @XmlElement(name="ROW")
    private List<MobileAllowanceDetails> mobileAllowanceDetails;
    public MobileAllowanceDetailsCollection() {
        super();
    }


    public void setMobileAllowanceDetails(List<MobileAllowanceDetails> mobileAllowanceDetails) {
        this.mobileAllowanceDetails = mobileAllowanceDetails;
    }

    public List<MobileAllowanceDetails> getMobileAllowanceDetails() {
        return mobileAllowanceDetails;
    }

}
