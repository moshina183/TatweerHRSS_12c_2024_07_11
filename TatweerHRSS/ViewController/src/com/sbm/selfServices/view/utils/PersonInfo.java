package com.sbm.selfServices.view.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;



public class PersonInfo {
    public PersonInfo() {
        super();
    }
    private String lineManager;
    private Long lineManagerID;
    private String assignee;
    private String assigneeName;
    private String personName;
    private Long employeeId;
    private String fullName;
    private String perosnNumber;
    private String department,job,location,position,email;
    private HashMap<String, Boolean> securityGroups;
    private Set<String> roleAllowedPages;
    private String gradeCode;
    private String salary;
    private String nationality;
    private String maritalStatus;
    private String childsNo;
    private Date hireDate;
    private String empIsMngrFlag;
    private String userName;
    private String tokenEmail;

    public void setPersonName(String username) {
        com.view.uiutils.JSFUtils.setExpressionValue("#{sessionScope.PaaSPersonName}", username);
        this.personName = username;
    }

    public String getPersonName() {
        return personName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setSecurityGroups(HashMap<String, Boolean> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public HashMap<String, Boolean> getSecurityGroups() {
        return securityGroups;
    }

    public void setRoleAllowedPages(Set<String> roleAllowedPages) {
        this.roleAllowedPages = roleAllowedPages;
    }

    public Set<String> getRoleAllowedPages() {
        return roleAllowedPages;
    }

    public void setPerosnNumber(String perosnNumber) {
        this.perosnNumber = perosnNumber;
    }

    public String getPerosnNumber() {
        return perosnNumber;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setLineManager(String lineManager) {
        this.lineManager = lineManager;
    }

    public String getLineManager() {
        return lineManager != null ? lineManager : "";
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setGradeCode(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }


    public void setLineManagerID(Long lineManagerID) {
        this.lineManagerID = lineManagerID;
    }

    public Long getLineManagerID() {
        return lineManagerID;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getSalary() {
        return salary;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setChildsNo(String childsNo) {
        this.childsNo = childsNo;
    }

    public String getChildsNo() {
        return childsNo;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setEmpIsMngrFlag(String empIsMngrFlag) {
        this.empIsMngrFlag = empIsMngrFlag;
    }

    public String getEmpIsMngrFlag() {
        return empIsMngrFlag;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
      
        return userName;
    }

    public void setTokenEmail(String tokenEmail) {
        this.tokenEmail = tokenEmail;
    }

    public String getTokenEmail() {
        return tokenEmail;
    }
}
