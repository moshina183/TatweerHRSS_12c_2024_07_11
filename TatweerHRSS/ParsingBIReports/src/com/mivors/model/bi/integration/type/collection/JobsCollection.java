package com.mivors.model.bi.integration.type.collection;

import com.mivors.model.bi.integration.type.Department;

import com.mivors.model.bi.integration.type.Job;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
@XmlAccessorType(XmlAccessType.FIELD)

public class JobsCollection implements TatweerCollection {
    
    @XmlElement(name="ROW")
    private List<Job> jobs;
    
    public JobsCollection() {
        super();
        jobs=new ArrayList<Job>();
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
