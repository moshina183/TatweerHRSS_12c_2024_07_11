package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.OvertimeDatWithdraw;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class OvertimeDatWithdrawCollection implements TatweerCollection  {
    
    @XmlElement(name="ROW")
    private List<OvertimeDatWithdraw> overtimeDatWithdraw;
    
    public OvertimeDatWithdrawCollection() {
        super();
    }

    public void setOvertimeDatWithdraw(List<OvertimeDatWithdraw> overtimeDatWithdraw) {
        this.overtimeDatWithdraw = overtimeDatWithdraw;
    }

    public List<OvertimeDatWithdraw> getOvertimeDatWithdraw() {
        return overtimeDatWithdraw;
    }
}
