package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.PrivilegesToManpower;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class PrivilegesToManpowerCollection implements TatweerCollection{
    
    @XmlElement(name="ROW")
    private List<PrivilegesToManpower> privilegesToManpower;
    
    public PrivilegesToManpowerCollection() {
        super();
        privilegesToManpower=new ArrayList<PrivilegesToManpower>();
    }

    public void setPrivilegesToManpower(List<PrivilegesToManpower> privilegesToManpower) {
        this.privilegesToManpower = privilegesToManpower;
    }

    public List<PrivilegesToManpower> getPrivilegesToManpower() {
        return privilegesToManpower;
    }
}
