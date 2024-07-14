package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.OvertimeDatFileData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class OvertimeDatFileDataCollection implements TatweerCollection {
    @XmlElement(name="ROW")
    private List<OvertimeDatFileData> overtimeDatFileData;
    
    public OvertimeDatFileDataCollection() {
        super();
        overtimeDatFileData=new ArrayList<OvertimeDatFileData>();
    }

    public void setOvertimeDatFileData(List<OvertimeDatFileData> overtimeDatFileData) {
        this.overtimeDatFileData = overtimeDatFileData;
    }

    public List<OvertimeDatFileData> getOvertimeDatFileData() {
        return overtimeDatFileData;
    }
}
