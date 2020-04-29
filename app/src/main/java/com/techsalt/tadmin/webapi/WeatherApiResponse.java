package com.techsalt.tadmin.webapi;

public class WeatherApiResponse {

    private double latitude;
    private double longitude;
    private String timezone;
    private CurrentlyBean currently;
    private double offset;

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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public CurrentlyBean getCurrently() {
        return currently;
    }

    public void setCurrently(CurrentlyBean currently) {
        this.currently = currently;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public static class CurrentlyBean {
        /**
         * time : 1578314828
         * summary : Overcast
         * icon : cloudy
         * precipIntensity : 0
         * precipProbability : 0
         * temperature : 60.98
         * apparentTemperature : 60.98
         * dewPoint : 50.87
         * humidity : 0.69
         * pressure : 1019.4
         * windSpeed : 7.37
         * windGust : 11.05
         * windBearing : 56
         * cloudCover : 0.99
         * uvIndex : 0
         * visibility : 10
         * ozone : 269.7
         */

        private int time;
        private String summary;
        private String icon;
        private double precipIntensity;
        private double precipProbability;
        private double temperature;
        private double apparentTemperature;
        private double dewPoint;
        private double humidity;
        private double pressure;
        private double windSpeed;
        private double windGust;
        private int windBearing;
        private double cloudCover;
        private int uvIndex;
        private int visibility;
        private double ozone;

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public double getPrecipIntensity() {
            return precipIntensity;
        }

        public void setPrecipIntensity(int precipIntensity) {
            this.precipIntensity = precipIntensity;
        }

        public double getPrecipProbability() {
            return precipProbability;
        }

        public void setPrecipProbability(int precipProbability) {
            this.precipProbability = precipProbability;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(double apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }

        public double getDewPoint() {
            return dewPoint;
        }

        public void setDewPoint(double dewPoint) {
            this.dewPoint = dewPoint;
        }

        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }

        public double getWindGust() {
            return windGust;
        }

        public void setWindGust(double windGust) {
            this.windGust = windGust;
        }

        public int getWindBearing() {
            return windBearing;
        }

        public void setWindBearing(int windBearing) {
            this.windBearing = windBearing;
        }

        public double getCloudCover() {
            return cloudCover;
        }

        public void setCloudCover(double cloudCover) {
            this.cloudCover = cloudCover;
        }

        public int getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(int uvIndex) {
            this.uvIndex = uvIndex;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }

        public double getOzone() {
            return ozone;
        }

        public void setOzone(double ozone) {
            this.ozone = ozone;
        }
    }
}
