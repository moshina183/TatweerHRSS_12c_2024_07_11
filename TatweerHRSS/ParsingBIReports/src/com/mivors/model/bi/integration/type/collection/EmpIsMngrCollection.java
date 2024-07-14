package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.EmpIsMngr;




import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)


public class EmpIsMngrCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<EmpIsMngr> emp;
    
    public EmpIsMngrCollection() {
        super();
        emp=new ArrayList<EmpIsMngr>();
    }

    public void setEmp(List<EmpIsMngr> emp) {
        this.emp = emp;
    }

    public List<EmpIsMngr> getEmp() {
        return emp;
    }
}
