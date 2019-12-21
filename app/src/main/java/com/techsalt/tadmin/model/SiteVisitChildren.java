package com.techsalt.tadmin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SiteVisitChildren implements Parcelable {

     String siteName,msg,suid,visitStartTime,visitEndTime,visitStartAddress,visitEndAddress,selfie;
     boolean isVisitFound,isVisitCompleted,isSurveyFound;

    List<SubChildrenCommunication> subChildrenCommunications;
    List<SurveyResponseBean> surveyResponse;

    public SiteVisitChildren(String siteName, String msg, String suid, String visitStartTime, String visitEndTime, String visitStartAddress, String visitEndAddress,String selfie, boolean isVisitFound, boolean isSurveyFound, boolean isVisitCompleted,List<SubChildrenCommunication> subChildrenCommunications,List<SurveyResponseBean> surveyResponse) {
        this.siteName = siteName;
        this.msg = msg;
        this.suid = suid;
        this.visitStartTime = visitStartTime;
        this.visitEndTime = visitEndTime;
        this.visitStartAddress = visitStartAddress;
        this.visitEndAddress = visitEndAddress;
        this.selfie=selfie;
        this.isVisitFound = isVisitFound;
        this.isVisitCompleted = isVisitCompleted;
        this.isSurveyFound=isSurveyFound;
        this.subChildrenCommunications = subChildrenCommunications;
        this.surveyResponse=surveyResponse;
    }

    protected SiteVisitChildren(Parcel in) {
        siteName = in.readString();
        msg = in.readString();
        suid = in.readString();
        visitStartTime = in.readString();
        visitEndTime = in.readString();
        visitStartAddress = in.readString();
        visitEndAddress = in.readString();
        selfie = in.readString();
        isVisitFound = in.readByte() != 0;
        isVisitCompleted = in.readByte() != 0;
        isSurveyFound = in.readByte() != 0;
    }

    public static final Creator<SiteVisitChildren> CREATOR = new Creator<SiteVisitChildren>() {
        @Override
        public SiteVisitChildren createFromParcel(Parcel in) {
            return new SiteVisitChildren(in);
        }

        @Override
        public SiteVisitChildren[] newArray(int size) {
            return new SiteVisitChildren[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(siteName);
        dest.writeString(msg);
        dest.writeString(suid);
        dest.writeString(visitStartTime);
        dest.writeString(visitEndTime);
        dest.writeString(visitStartAddress);
        dest.writeString(visitEndAddress);
        dest.writeString(selfie);
        dest.writeByte((byte) (isVisitFound ? 1 : 0));
        dest.writeByte((byte) (isVisitCompleted ? 1 : 0));
        dest.writeByte((byte) (isSurveyFound ? 1 : 0));
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getVisitStartTime() {
        return visitStartTime;
    }

    public void setVisitStartTime(String visitStartTime) {
        this.visitStartTime = visitStartTime;
    }

    public String getVisitEndTime() {
        return visitEndTime;
    }

    public void setVisitEndTime(String visitEndTime) {
        this.visitEndTime = visitEndTime;
    }

    public String getVisitStartAddress() {
        return visitStartAddress;
    }

    public void setVisitStartAddress(String visitStartAddress) {
        this.visitStartAddress = visitStartAddress;
    }

    public String getVisitEndAddress() {
        return visitEndAddress;
    }

    public void setVisitEndAddress(String visitEndAddress) {
        this.visitEndAddress = visitEndAddress;
    }

    public String getSelfie() {
        return selfie;
    }

    public void setSelfie(String selfie) {
        this.selfie = selfie;
    }

    public boolean isVisitFound() {
        return isVisitFound;
    }

    public void setVisitFound(boolean visitFound) {
        isVisitFound = visitFound;
    }

    public boolean isVisitCompleted() {
        return isVisitCompleted;
    }

    public void setVisitCompleted(boolean visitCompleted) {
        isVisitCompleted = visitCompleted;
    }


    public boolean isSurveyFound() {
        return isSurveyFound;
    }

    public void setSurveyFound(boolean surveyFound) {
        isSurveyFound = surveyFound;
    }

    public List<SubChildrenCommunication> getSubChildrenCommunications() {
        return subChildrenCommunications;
    }

    public void setSubChildrenCommunications(List<SubChildrenCommunication> subChildrenCommunications) {
        this.subChildrenCommunications = subChildrenCommunications;
    }

    public List<SurveyResponseBean> getSurveyResponse() {
        return surveyResponse;
    }

    public void setSurveyResponse(List<SurveyResponseBean> surveyResponse) {
        this.surveyResponse = surveyResponse;
    }
}
