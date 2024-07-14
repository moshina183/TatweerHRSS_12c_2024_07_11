package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.Department;
import com.mivors.model.bi.integration.type.Position;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class PositionsCollection implements TatweerCollection{
    @XmlElement(name="ROW")
    private List<Position> positions;
    public PositionsCollection() {
        super();
        positions=new ArrayList<Position>();
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {
        return positions;
    }
}
