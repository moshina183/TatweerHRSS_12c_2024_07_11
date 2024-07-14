package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.PersonAccrual;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class PersonAccrualCollection implements TatweerCollection{
    @XmlElement(name="ROW")
    private List<PersonAccrual> personAccrual;
    public PersonAccrualCollection() {
        super();
        personAccrual=new ArrayList<PersonAccrual>();
    }

    public void setPersonAccrual(List<PersonAccrual> personAccrual) {
        this.personAccrual = personAccrual;
    }

    public List<PersonAccrual> getPersonAccrual() {
        return personAccrual;
    }
}
