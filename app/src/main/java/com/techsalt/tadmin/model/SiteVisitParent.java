package com.techsalt.tadmin.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SiteVisitParent extends ExpandableGroup<SiteVisitChildren> {

    String euid, fieldOfficerName, mobile, empcode;

    public SiteVisitParent(String title, List<SiteVisitChildren> items, String euid, String fieldOfficerName, String mobile, String empcode) {
        super(title, items);
        this.euid = euid;
        this.fieldOfficerName=fieldOfficerName;
        this.mobile=mobile;
        this.empcode=empcode;

    }

    public String getEuid() {
        return euid;
    }

    public void setEuid(String euid) {
        this.euid = euid;
    }

    public String getFieldOfficerName() {
        return fieldOfficerName;
    }

    public void setFieldOfficerName(String fieldOfficerName) {
        this.fieldOfficerName = fieldOfficerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmpcode() {
        return empcode;
    }

    public void setEmpcode(String empcode) {
        this.empcode = empcode;
    }
}
