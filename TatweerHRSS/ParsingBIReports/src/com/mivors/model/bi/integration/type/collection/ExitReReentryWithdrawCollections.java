package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.ExitReReentryDatWithdraw;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class ExitReReentryWithdrawCollections implements TatweerCollection  {
    
    @XmlElement(name="ROW")
    private List<ExitReReentryDatWithdraw> exitReReentryDatWithdraw;
    
    public ExitReReentryWithdrawCollections() {
        super();
    }

    public void setExitReReentryDatWithdraw(List<ExitReReentryDatWithdraw> exitReReentryDatWithdraw) {
        this.exitReReentryDatWithdraw = exitReReentryDatWithdraw;
    }

    public List<ExitReReentryDatWithdraw> getExitReReentryDatWithdraw() {
        return exitReReentryDatWithdraw;
    }
}
