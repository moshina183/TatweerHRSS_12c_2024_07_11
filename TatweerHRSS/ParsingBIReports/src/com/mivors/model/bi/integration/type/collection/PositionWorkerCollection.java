package com.mivors.model.bi.integration.type.collection;
import com.mivors.model.bi.integration.type.PositionWorker;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class PositionWorkerCollection implements TatweerCollection{
    @XmlElement(name="ROW")
    private List<PositionWorker> positionWorkers;
    
    public PositionWorkerCollection() {
        super();
        positionWorkers=new ArrayList<PositionWorker>();
    }

    public void setPositionWorkers(List<PositionWorker> positionWorkers) {
        this.positionWorkers = positionWorkers;
    }

    public List<PositionWorker> getPositionWorkers() {
        return positionWorkers;
    }
}
