package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class CodeCombinationID {
    
    @XmlElement(name="CODE_COMBINATION_ID")
    private String CODE_COMBINATION_ID;
    
    public CodeCombinationID() {
        super();
    }

    public void setCODE_COMBINATION_ID(String CODE_COMBINATION_ID) {
        this.CODE_COMBINATION_ID = CODE_COMBINATION_ID;
    }

    public String getCODE_COMBINATION_ID() {
        return CODE_COMBINATION_ID;
    }
}
