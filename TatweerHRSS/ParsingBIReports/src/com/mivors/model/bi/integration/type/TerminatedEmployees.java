package com.mivors.model.bi.integration.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROW")
@XmlAccessorType(XmlAccessType.FIELD)

public class TerminatedEmployees {
    
    @XmlElement(name = "HIRE_DATE")
    private String HIRE_DATE; 
    
    @XmlElement(name = "DEPARTMENT_AR")
    private String DEPARTMENT_AR; 
    
    @XmlElement(name = "ACTION_REASON")
    private String ACTION_REASON; 
    
    @XmlElement(name = "MANAGER_NAME_AR")
    private String MANAGER_NAME_AR; 
    
    @XmlElement(name = "ACTUAL_TERMINATION_DATE")
    private String ACTUAL_TERMINATION_DATE; 
    
    @XmlElement(name = "TOT_YEARS")
    private String TOT_YEARS;
    
    @XmlElement(name = "TOT_MONTHS")
    private String TOT_MONTHS;
    
    @XmlElement(name = "TOT_DAYS")
    private String TOT_DAYS;
    
    @XmlElement(name = "FULL_NAME_AR")
    private String FULL_NAME_AR;
    
    @XmlElement(name = "SYSTEM_PERSON_TYPE")
    private String SYSTEM_PERSON_TYPE;
    
    @XmlElement(name = "ACTION_REASON_EN")
    private String ACTION_REASON_EN;
    
    @XmlElement(name = "MANAGER_NAME")
    private String MANAGER_NAME;
    
    @XmlElement(name = "ACTION")
    private String ACTION;
    
    @XmlElement(name = "JOB_NAME")
    private String JOB_NAME;
    
    @XmlElement(name = "POSITION_NAME_AR")
    private String POSITION_NAME_AR;
    
    public TerminatedEmployees() {
        super();
    }

    public void setHIRE_DATE(String HIRE_DATE) {
        this.HIRE_DATE = HIRE_DATE;
    }

    public String getHIRE_DATE() {
        return HIRE_DATE;
    }

    public void setDEPARTMENT_AR(String DEPARTMENT_AR) {
        this.DEPARTMENT_AR = DEPARTMENT_AR;
    }

    public String getDEPARTMENT_AR() {
        return DEPARTMENT_AR;
    }

    public void setACTION_REASON(String ACTION_REASON) {
        this.ACTION_REASON = ACTION_REASON;
    }

    public String getACTION_REASON() {
        return ACTION_REASON;
    }

    public void setMANAGER_NAME_AR(String MANAGER_NAME_AR) {
        this.MANAGER_NAME_AR = MANAGER_NAME_AR;
    }

    public String getMANAGER_NAME_AR() {
        return MANAGER_NAME_AR;
    }

    public void setACTUAL_TERMINATION_DATE(String ACTUAL_TERMINATION_DATE) {
        this.ACTUAL_TERMINATION_DATE = ACTUAL_TERMINATION_DATE;
    }

    public String getACTUAL_TERMINATION_DATE() {
        return ACTUAL_TERMINATION_DATE;
    }

    public void setTOT_YEARS(String TOT_YEARS) {
        this.TOT_YEARS = TOT_YEARS;
    }

    public String getTOT_YEARS() {
        return TOT_YEARS;
    }

    public void setTOT_MONTHS(String TOT_MONTHS) {
        this.TOT_MONTHS = TOT_MONTHS;
    }

    public String getTOT_MONTHS() {
        return TOT_MONTHS;
    }

    public void setTOT_DAYS(String TOT_DAYS) {
        this.TOT_DAYS = TOT_DAYS;
    }

    public String getTOT_DAYS() {
        return TOT_DAYS;
    }

    public void setFULL_NAME_AR(String FULL_NAME_AR) {
        this.FULL_NAME_AR = FULL_NAME_AR;
    }

    public String getFULL_NAME_AR() {
        return FULL_NAME_AR;
    }

    public void setSYSTEM_PERSON_TYPE(String SYSTEM_PERSON_TYPE) {
        this.SYSTEM_PERSON_TYPE = SYSTEM_PERSON_TYPE;
    }

    public String getSYSTEM_PERSON_TYPE() {
        return SYSTEM_PERSON_TYPE;
    }

    public void setACTION_REASON_EN(String ACTION_REASON_EN) {
        this.ACTION_REASON_EN = ACTION_REASON_EN;
    }

    public String getACTION_REASON_EN() {
        return ACTION_REASON_EN;
    }

    public void setMANAGER_NAME(String MANAGER_NAME) {
        this.MANAGER_NAME = MANAGER_NAME;
    }

    public String getMANAGER_NAME() {
        return MANAGER_NAME;
    }

    public void setACTION(String ACTION) {
        this.ACTION = ACTION;
    }

    public String getACTION() {
        return ACTION;
    }

    public void setJOB_NAME(String JOB_NAME) {
        this.JOB_NAME = JOB_NAME;
    }

    public String getJOB_NAME() {
        return JOB_NAME;
    }

    public void setPOSITION_NAME_AR(String POSITION_NAME_AR) {
        this.POSITION_NAME_AR = POSITION_NAME_AR;
    }

    public String getPOSITION_NAME_AR() {
        return POSITION_NAME_AR;
    }
}
