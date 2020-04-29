package com.techsalt.tadmin.webapi;

import java.util.List;

public class AllSiteListResponse {

    private String status;
    private List<SiteListBean> siteList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SiteListBean> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<SiteListBean> siteList) {
        this.siteList = siteList;
    }

    public static class SiteListBean {
        /**
         * suid : 1
         * siteName : site1
         * client : [{"clientId":1,"clientName":"Mr. Suresh","clientEmail":"techsaltsolutions@gmail.com"}]
         */

        private int suid;
        private String siteName;
        private List<ClientBean> client;

        public int getSuid() {
            return suid;
        }

        public void setSuid(int suid) {
            this.suid = suid;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public List<ClientBean> getClient() {
            return client;
        }

        public void setClient(List<ClientBean> client) {
            this.client = client;
        }

        public static class ClientBean {
            /**
             * clientId : 1
             * clientName : Mr. Suresh
             * clientEmail : techsaltsolutions@gmail.com
             */

            private int clientId;
            private String clientName;
            private String clientEmail;

            public int getClientId() {
                return clientId;
            }

            public void setClientId(int clientId) {
                this.clientId = clientId;
            }

            public String getClientName() {
                return clientName;
            }

            public void setClientName(String clientName) {
                this.clientName = clientName;
            }

            public String getClientEmail() {
                return clientEmail;
            }

            public void setClientEmail(String clientEmail) {
                this.clientEmail = clientEmail;
            }
        }
    }
}
