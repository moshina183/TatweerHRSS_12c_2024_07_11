package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.ExitReEntryDatFileData;

import com.mivors.model.bi.integration.type.OvertimeDatFileData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)


public class ExitReEntryDatFileDataCollection implements TatweerCollection {
    @XmlElement(name="ROW")
    private List<ExitReEntryDatFileData> exitReEntryDatFileData;
    public ExitReEntryDatFileDataCollection() {
        super();
        exitReEntryDatFileData=new ArrayList<ExitReEntryDatFileData>();
    }

    public void setExitReEntryDatFileData(List<ExitReEntryDatFileData> exitReEntryDatFileData) {
        this.exitReEntryDatFileData = exitReEntryDatFileData;
    }

    public List<ExitReEntryDatFileData> getExitReEntryDatFileData() {
        return exitReEntryDatFileData;
    }
}
