package com.techsalt.tadmin.webapi;

import android.os.Parcel;
import android.os.Parcelable;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SiteVisitRes {

    private String status;
    private String msg;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends ExpandableGroup<SiteVisitRes.DataBean.SiteVisitAttendanceBean> {

        private int euid;
        private String fieldOfficerName;
        private String mobile;
        private String empcode;
        private List<DataBean.SiteVisitAttendanceBean> siteVisitAttendance;

        public DataBean(String title, List<SiteVisitRes.DataBean.SiteVisitAttendanceBean> items,String fieldOfficerName,String mobile,String empcode) {
            super(title, items);
            this.fieldOfficerName=fieldOfficerName;
            this.mobile=mobile;
            this.empcode=empcode;
        }

        public int getEuid() {
            return euid;
        }

        public void setEuid(int euid) {
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

        public List<DataBean.SiteVisitAttendanceBean> getSiteVisitAttendance() {
            return siteVisitAttendance;
        }

        public void setSiteVisitAttendance(List<DataBean.SiteVisitAttendanceBean> siteVisitAttendance) {
            this.siteVisitAttendance = siteVisitAttendance;
        }

        public static class SiteVisitAttendanceBean implements Parcelable {

            private String siteName;
            private boolean isVisitFound;
            private boolean isVisitCompleted;
            private boolean isSurveyFound;
            private String msg;
            private int suid;
            private String selfie;
            private String visitStartTime;
            private String visitEndTime;
            private String visitStartAddress;
            private String visitEndAddress;
            private double visitStartLatitude;
            private double visitStartLongitude;
            private double visitEndLatitude;
            private double visitEndLongitude;
            private List<VisitCommunicationBean> visitCommunication;
            private List<SurveyBean> survey;

            protected SiteVisitAttendanceBean(Parcel in) {
                siteName = in.readString();
                isVisitFound = in.readByte() != 0;
                isVisitCompleted = in.readByte() != 0;
                isSurveyFound = in.readByte() != 0;
                msg = in.readString();
                suid = in.readInt();
                selfie = in.readString();
                visitStartTime = in.readString();
                visitEndTime = in.readString();
                visitStartAddress = in.readString();
                visitEndAddress = in.readString();
                visitStartLatitude = in.readDouble();
                visitStartLongitude = in.readDouble();
                visitEndLatitude = in.readDouble();
                visitEndLongitude = in.readDouble();
            }

            public static final Creator<SiteVisitAttendanceBean> CREATOR = new Creator<SiteVisitAttendanceBean>() {
                @Override
                public SiteVisitAttendanceBean createFromParcel(Parcel in) {
                    return new SiteVisitAttendanceBean(in);
                }

                @Override
                public SiteVisitAttendanceBean[] newArray(int size) {
                    return new SiteVisitAttendanceBean[size];
                }
            };

            public String getSiteName() {
                return siteName;
            }

            public void setSiteName(String siteName) {
                this.siteName = siteName;
            }

            public boolean isIsVisitFound() {
                return isVisitFound;
            }

            public void setIsVisitFound(boolean isVisitFound) {
                this.isVisitFound = isVisitFound;
            }

            public boolean isIsVisitCompleted() {
                return isVisitCompleted;
            }

            public void setIsVisitCompleted(boolean isVisitCompleted) {
                this.isVisitCompleted = isVisitCompleted;
            }

            public boolean isIsSurveyFound() {
                return isSurveyFound;
            }

            public void setIsSurveyFound(boolean isSurveyFound) {
                this.isSurveyFound = isSurveyFound;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public int getSuid() {
                return suid;
            }

            public void setSuid(int suid) {
                this.suid = suid;
            }

            public String getSelfie() {
                return selfie;
            }

            public void setSelfie(String selfie) {
                this.selfie = selfie;
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

            public double getVisitStartLatitude() {
                return visitStartLatitude;
            }

            public void setVisitStartLatitude(double visitStartLatitude) {
                this.visitStartLatitude = visitStartLatitude;
            }

            public double getVisitStartLongitude() {
                return visitStartLongitude;
            }

            public void setVisitStartLongitude(double visitStartLongitude) {
                this.visitStartLongitude = visitStartLongitude;
            }

            public double getVisitEndLatitude() {
                return visitEndLatitude;
            }

            public void setVisitEndLatitude(double visitEndLatitude) {
                this.visitEndLatitude = visitEndLatitude;
            }

            public double getVisitEndLongitude() {
                return visitEndLongitude;
            }

            public void setVisitEndLongitude(double visitEndLongitude) {
                this.visitEndLongitude = visitEndLongitude;
            }

            public List<VisitCommunicationBean> getVisitCommunication() {
                return visitCommunication;
            }

            public void setVisitCommunication(List<VisitCommunicationBean> visitCommunication) {
                this.visitCommunication = visitCommunication;
            }

            public List<SurveyBean> getSurvey() {
                return survey;
            }

            public void setSurvey(List<SurveyBean> survey) {
                this.survey = survey;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(siteName);
                dest.writeByte((byte) (isVisitFound ? 1 : 0));
                dest.writeByte((byte) (isVisitCompleted ? 1 : 0));
                dest.writeByte((byte) (isSurveyFound ? 1 : 0));
                dest.writeString(msg);
                dest.writeInt(suid);
                dest.writeString(selfie);
                dest.writeString(visitStartTime);
                dest.writeString(visitEndTime);
                dest.writeString(visitStartAddress);
                dest.writeString(visitEndAddress);
                dest.writeDouble(visitStartLatitude);
                dest.writeDouble(visitStartLongitude);
                dest.writeDouble(visitEndLatitude);
                dest.writeDouble(visitEndLongitude);
            }

            public static class VisitCommunicationBean {

                private boolean isCommunicationFound;
                private String type;
                private String text;
                private String image;

                public boolean isIsCommunicationFound() {
                    return isCommunicationFound;
                }

                public void setIsCommunicationFound(boolean isCommunicationFound) {
                    this.isCommunicationFound = isCommunicationFound;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }
            }

            public static class SurveyBean {

                private String question;
                private String response;

                public String getQuestion() {
                    return question;
                }

                public void setQuestion(String question) {
                    this.question = question;
                }

                public String getResponse() {
                    return response;
                }

                public void setResponse(String response) {
                    this.response = response;
                }
            }
        }
    }
}
