package com.mivors.model.bi.integration.type.collection;
import com.mivors.model.bi.integration.type.VacantPosition;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class VacantPositionCollection implements TatweerCollection{
    @XmlElement(name="ROW")
    private List<VacantPosition> vacantPositions;
    
    public VacantPositionCollection() {
        super();
        vacantPositions=new ArrayList<VacantPosition>();
    }

    public void setVacantPositions(List<VacantPosition> vacantPositions) {
        this.vacantPositions = vacantPositions;
    }

    public List<VacantPosition> getVacantPositions() {
        return vacantPositions;
    }
}