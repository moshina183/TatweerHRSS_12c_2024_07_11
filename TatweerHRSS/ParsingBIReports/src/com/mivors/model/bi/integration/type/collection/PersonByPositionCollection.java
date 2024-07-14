package com.mivors.model.bi.integration.type.collection;


import com.mivors.model.bi.integration.type.PersonByPosition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class PersonByPositionCollection implements TatweerCollection {
    
    
    @XmlElement(name="ROW")
    private List<PersonByPosition> personByPosition;
    
    public PersonByPositionCollection() {
        super();
        personByPosition=new ArrayList<PersonByPosition>();
    }

    public void setPersonByPosition(List<PersonByPosition> personByPosition) {
        this.personByPosition = personByPosition;
    }

    public List<PersonByPosition> getPersonByPosition() {
        return personByPosition;
    }
}
