package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.TerminatedEmployees;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class TerminatedEmpsCollection  implements TatweerCollection{
    
    @XmlElement(name="ROW")
    private List<TerminatedEmployees> terminatedEmps;
    
    public TerminatedEmpsCollection() {
        super();
        terminatedEmps = new ArrayList<TerminatedEmployees>();
    }

    public void setTerminatedEmps(List<TerminatedEmployees> terminatedEmps) {
        this.terminatedEmps = terminatedEmps;
    }

    public List<TerminatedEmployees> getTerminatedEmps() {
        return terminatedEmps;
    }
}
