package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.AllEmpsAndMngrs;



import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class AllEmpsAndMngrsCollection implements TatweerCollection {

    
    @XmlElement(name="ROW")
    private List<AllEmpsAndMngrs> allEmpsAndMngrs;
    
    public AllEmpsAndMngrsCollection() {
        super();
        allEmpsAndMngrs=new ArrayList<AllEmpsAndMngrs>();
    }


    public void setAllEmpsAndMngrs(List<AllEmpsAndMngrs> allEmpsAndMngrs) {
        this.allEmpsAndMngrs = allEmpsAndMngrs;
    }

    public List<AllEmpsAndMngrs> getAllEmpsAndMngrs() {
        return allEmpsAndMngrs;
    }
}


