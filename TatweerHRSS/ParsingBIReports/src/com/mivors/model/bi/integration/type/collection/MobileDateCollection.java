package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.ManagerOfDept;
import com.mivors.model.bi.integration.type.MobileDatReport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)
public class MobileDateCollection  implements TatweerCollection {
   
    
    public MobileDateCollection() {
        super();
        mobilereport=new ArrayList<MobileDatReport>();
       
    }
    @XmlElement(name="ROW")
    private List<MobileDatReport> mobilereport;
    public void setMobilereport(List<MobileDatReport> mobilereport) {
        this.mobilereport = mobilereport;
    }

    public List<MobileDatReport> getMobilereport() {
        return mobilereport;
    }
}
