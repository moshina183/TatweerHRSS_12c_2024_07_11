package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.MobileDatfile;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class MobileDatfileCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<MobileDatfile> mobileDatfile;
    
    public MobileDatfileCollection() {
        super();
        mobileDatfile=new ArrayList<MobileDatfile>();
    }

    public void setMobileDatfile(List<MobileDatfile> mobileDatfile) {
        this.mobileDatfile = mobileDatfile;
    }

    public List<MobileDatfile> getMobileDatfile() {
        return mobileDatfile;
    }
}
