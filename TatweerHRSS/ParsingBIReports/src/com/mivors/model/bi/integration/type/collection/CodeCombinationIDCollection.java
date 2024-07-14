package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.CodeCombinationID;
import com.mivors.model.bi.integration.type.ManagerOfDept;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class CodeCombinationIDCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<CodeCombinationID> codeCombinationId;
    
    public CodeCombinationIDCollection() {
        super();
        codeCombinationId = new ArrayList<CodeCombinationID>();
    }

    public void setCodeCombinationId(List<CodeCombinationID> codeCombinationId) {
        this.codeCombinationId = codeCombinationId;
    }

    public List<CodeCombinationID> getCodeCombinationId() {
        return codeCombinationId;
    }
}
