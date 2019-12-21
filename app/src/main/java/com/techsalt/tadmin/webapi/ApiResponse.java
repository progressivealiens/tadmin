package com.techsalt.tadmin.webapi;

import java.util.List;

public class ApiResponse {

    private String status;
    private String msg;
    private String companyName;
    private String companyLogo;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public static class DataBean {

        private String name;
        private String empcode;
        private String type;
        private String date;
        private String checkInTime;
        private String message;
        private String checkOutTime;
        private String checkInAddress;
        private double checkInLatitude;
        private double checkInLongitude;
        private String checkOutAddress;
        private double checkOutLatitude;
        private double checkOutLongitude;
        private String startImageName;
        private String dutyHours;
        private String checkInBatteryLevel;
        private String checkOutBatteryLevel;
        private String categoryName;
        private int routeId;
        private String routeName;
        private int siteId;
        private String siteName;
        private boolean isLiveTracking;
        private String trackingMessage;
        private String loginVia;
        private int employeeId;
        private boolean showHistory;
        private String mobile;
        private double latitude;
        private double longitude;

        private List<RoutePatrolBean> routePatrol;
        private List<CommunicationsListDataBean> communicationsListData;
        private List<QrCodeScanVisitListBean> qrCodeScanVisitList;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public int getRouteId() {
            return routeId;
        }

        public void setRouteId(int routeId) {
            this.routeId = routeId;
        }

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public int getSiteId() {
            return siteId;
        }

        public void setSiteId(int siteId) {
            this.siteId = siteId;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public boolean isLiveTracking() {
            return isLiveTracking;
        }

        public void setLiveTracking(boolean liveTracking) {
            isLiveTracking = liveTracking;
        }

        public String getLoginVia() {
            return loginVia;
        }

        public void setLoginVia(String loginVia) {
            this.loginVia = loginVia;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmpcode() {
            return empcode;
        }

        public void setEmpcode(String empcode) {
            this.empcode = empcode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCheckInTime() {
            return checkInTime;
        }

        public void setCheckInTime(String checkInTime) {
            this.checkInTime = checkInTime;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCheckOutTime() {
            return checkOutTime;
        }

        public void setCheckOutTime(String checkOutTime) {
            this.checkOutTime = checkOutTime;
        }

        public String getCheckInAddress() {
            return checkInAddress;
        }

        public void setCheckInAddress(String checkInAddress) {
            this.checkInAddress = checkInAddress;
        }

        public double getCheckInLatitude() {
            return checkInLatitude;
        }

        public void setCheckInLatitude(double checkInLatitude) {
            this.checkInLatitude = checkInLatitude;
        }

        public double getCheckInLongitude() {
            return checkInLongitude;
        }

        public void setCheckInLongitude(double checkInLongitude) {
            this.checkInLongitude = checkInLongitude;
        }

        public String getCheckOutAddress() {
            return checkOutAddress;
        }

        public void setCheckOutAddress(String checkOutAddress) {
            this.checkOutAddress = checkOutAddress;
        }

        public double getCheckOutLatitude() {
            return checkOutLatitude;
        }

        public void setCheckOutLatitude(double checkOutLatitude) {
            this.checkOutLatitude = checkOutLatitude;
        }

        public double getCheckOutLongitude() {
            return checkOutLongitude;
        }

        public void setCheckOutLongitude(double checkOutLongitude) {
            this.checkOutLongitude = checkOutLongitude;
        }

        public String getStartImageName() {
            return startImageName;
        }

        public void setStartImageName(String startImageName) {
            this.startImageName = startImageName;
        }

        public String getDutyHours() {
            return dutyHours;
        }

        public void setDutyHours(String dutyHours) {
            this.dutyHours = dutyHours;
        }

        public String getCheckInBatteryLevel() {
            return checkInBatteryLevel;
        }

        public void setCheckInBatteryLevel(String checkInBatteryLevel) {
            this.checkInBatteryLevel = checkInBatteryLevel;
        }

        public String getCheckOutBatteryLevel() {
            return checkOutBatteryLevel;
        }

        public void setCheckOutBatteryLevel(String checkOutBatteryLevel) {
            this.checkOutBatteryLevel = checkOutBatteryLevel;
        }

        public boolean isIsLiveTracking() {
            return isLiveTracking;
        }

        public void setIsLiveTracking(boolean isLiveTracking) {
            this.isLiveTracking = isLiveTracking;
        }

        public boolean isShowHistory() {
            return showHistory;
        }

        public void setShowHistory(boolean showHistory) {
            this.showHistory = showHistory;
        }

        public String getTrackingMessage() {
            return trackingMessage;
        }

        public void setTrackingMessage(String trackingMessage) {
            this.trackingMessage = trackingMessage;
        }

        public List<CommunicationsListDataBean> getCommunicationsListData() {
            return communicationsListData;
        }

        public void setCommunicationsListData(List<CommunicationsListDataBean> communicationsListData) {
            this.communicationsListData = communicationsListData;
        }

        public List<QrCodeScanVisitListBean> getQrCodeScanVisitList() {
            return qrCodeScanVisitList;
        }

        public void setQrCodeScanVisitList(List<QrCodeScanVisitListBean> qrCodeScanVisitList) {
            this.qrCodeScanVisitList = qrCodeScanVisitList;
        }

        public List<RoutePatrolBean> getRoutePatrol() {
            return routePatrol;
        }

        public void setRoutePatrol(List<RoutePatrolBean> routePatrol) {
            this.routePatrol = routePatrol;
        }

        public static class CommunicationsListDataBean {
            /**
             * text :
             * image : 71-1576355571.png
             * postAddress : B-3, Tulsi Marg, Block B, Sector 22, Noida, Uttar Pradesh 201307, India
             * date : 2019-12-15 02:02:52
             * postLat : 28.59023
             * postLong : 77.3444948
             */

            private String text;
            private String image;
            private String postAddress;
            private String date;
            private String postLat;
            private String postLong;

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

            public String getPostAddress() {
                return postAddress;
            }

            public void setPostAddress(String postAddress) {
                this.postAddress = postAddress;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getPostLat() {
                return postLat;
            }

            public void setPostLat(String postLat) {
                this.postLat = postLat;
            }

            public String getPostLong() {
                return postLong;
            }

            public void setPostLong(String postLong) {
                this.postLong = postLong;
            }
        }

        public static class QrCodeScanVisitListBean {
            /**
             * vpmuid : 230
             * qrType : checkpoint
             * qrName : Head office
             * scanSelfie : xmua3YC.png
             * timeStamp : 2019-12-15 22:51:38
             */

            private int vpmuid;
            private String qrType;
            private String qrName;
            private String scanSelfie;
            private String timeStamp;

            public int getVpmuid() {
                return vpmuid;
            }

            public void setVpmuid(int vpmuid) {
                this.vpmuid = vpmuid;
            }

            public String getQrType() {
                return qrType;
            }

            public void setQrType(String qrType) {
                this.qrType = qrType;
            }

            public String getQrName() {
                return qrName;
            }

            public void setQrName(String qrName) {
                this.qrName = qrName;
            }

            public String getScanSelfie() {
                return scanSelfie;
            }

            public void setScanSelfie(String scanSelfie) {
                this.scanSelfie = scanSelfie;
            }

            public String getTimeStamp() {
                return timeStamp;
            }

            public void setTimeStamp(String timeStamp) {
                this.timeStamp = timeStamp;
            }
        }

        public static class RoutePatrolBean {

            private String checkPointName;
            private int round;
            private String deviceID;
            private String batteryStatus;
            private String guardScanImage;
            private String created_at;

            public String getCheckPointName() {
                return checkPointName;
            }

            public void setCheckPointName(String checkPointName) {
                this.checkPointName = checkPointName;
            }

            public int getRound() {
                return round;
            }

            public void setRound(int round) {
                this.round = round;
            }

            public String getDeviceID() {
                return deviceID;
            }

            public void setDeviceID(String deviceID) {
                this.deviceID = deviceID;
            }

            public String getBatteryStatus() {
                return batteryStatus;
            }

            public void setBatteryStatus(String batteryStatus) {
                this.batteryStatus = batteryStatus;
            }

            public String getGuardScanImage() {
                return guardScanImage;
            }

            public void setGuardScanImage(String guardScanImage) {
                this.guardScanImage = guardScanImage;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }
        }
    }
}
