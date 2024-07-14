package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class MobileDatReport {
    public MobileDatReport() {
        super();
    }
       @XmlElement(name = "PERSON_NUMBER")
        private String person_Number;
        @XmlElement(name = "ASSIGNMENT_NUMBER")
        private String assignment_number;
        @XmlElement(name = "EFFECTIVE_START_DATE")
        private String eff_start_date;
        @XmlElement(name = "EFFECTIVE_END_DATE")
        private String eff_end_date;
       @XmlElement(name = "MULTIPLEENTRYCOUNT")
        private String multiple_entry;
        

        public void setPerson_Number(String person_Number) {
            this.person_Number = person_Number;
        }

        public String getPerson_Number() {
            return person_Number;
        }

        public void setAssignment_number(String assignment_number) {
            this.assignment_number = assignment_number;
        }

        public String getAssignment_number() {
            return assignment_number;
        }

        public void setEff_start_date(String eff_start_date) {
            this.eff_start_date = eff_start_date;
        }

        public String getEff_start_date() {
            return eff_start_date;
        }

        public void setEff_end_date(String eff_end_date) {
            this.eff_end_date = eff_end_date;
        }

        public String getEff_end_date() {
            return eff_end_date;
        }

    public void setMultiple_entry(String multiple_entry) {
        this.multiple_entry = multiple_entry;
    }

    public String getMultiple_entry() {
        return multiple_entry;
    }
}
