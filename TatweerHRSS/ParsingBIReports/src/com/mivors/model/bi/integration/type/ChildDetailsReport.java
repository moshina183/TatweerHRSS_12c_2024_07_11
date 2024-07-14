package com.mivors.model.bi.integration.type;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)


public class ChildDetailsReport {
    @XmlElement(name = "DISPLAY_NAME")
    private String ChildName;
    @XmlElement(name = "NATIONAL_IDENTIFIER_NUMBER")
    private String ChildNationalId;
    public ChildDetailsReport() {
        super();
    }

    public void setChildName(String ChildName) {
        this.ChildName = ChildName;
    }

    public String getChildName() {
        return ChildName;
    }

    public void setChildNationalId(String ChildNationalId) {
        this.ChildNationalId = ChildNationalId;
    }

    public String getChildNationalId() {
        return ChildNationalId;
    }
}
