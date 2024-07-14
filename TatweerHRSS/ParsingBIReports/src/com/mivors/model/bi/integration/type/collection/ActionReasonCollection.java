package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.ActionReason;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class ActionReasonCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<ActionReason> actionReason;
    
    public ActionReasonCollection() {
        super();
        actionReason=new ArrayList<ActionReason>();
    }

    public void setActionReason(List<ActionReason> actionReason) {
        this.actionReason = actionReason;
    }

    public List<ActionReason> getActionReason() {
        return actionReason;
    }
}
