package com.techsalt.tadmin.webapi;

import java.util.List;

public class BarGraphResponse {

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

    public static class DataBean {
        /**
         * date : 2019-12-19
         * checkins : 2
         * sitevisit : 0
         * qrscan : 0
         */

        private String date;
        private int checkins;
        private int sitevisit;
        private int qrscan;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getCheckins() {
            return checkins;
        }

        public void setCheckins(int checkins) {
            this.checkins = checkins;
        }

        public int getSitevisit() {
            return sitevisit;
        }

        public void setSitevisit(int sitevisit) {
            this.sitevisit = sitevisit;
        }

        public int getQrscan() {
            return qrscan;
        }

        public void setQrscan(int qrscan) {
            this.qrscan = qrscan;
        }
    }
}
