
package applications.haywood.torideornottoride;

import java.util.List;

public class ZipCodeWeather {
    private Currently currently;
    private Flags flags;
    private Number latitude;
    private Number longitude;
    private Minutely minutely;
    private Number offset;
    private String timezone;
    private int lastUpdate;
    private int hour;
    private int minute;

    public int getLastUpdate() {
        return this.lastUpdate;
    }

    // TODO: Add verification that the value is valid (not a future epoch time), and not a rediculously old time.
    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getHour() {
        return this.hour;
    }

    // TODO: Add verification that the hour is between 0-23.
    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    // TODO: Add verification that the minute is between 0-59.
    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Currently getCurrently() {
        return this.currently;
    }

    public void setCurrently(Currently currently) {
        this.currently = currently;
    }

    public Flags getFlags() {
        return this.flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public Number getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Number latitude) {
        this.latitude = latitude;
    }

    public Number getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Number longitude) {
        this.longitude = longitude;
    }

    public Minutely getMinutely() {
        return this.minutely;
    }

    public void setMinutely(Minutely minutely) {
        this.minutely = minutely;
    }

    public Number getOffset() {
        return this.offset;
    }

    public void setOffset(Number offset) {
        this.offset = offset;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}

class Data {
    private Number precipIntensity;
    private Number precipProbability;
    private Number time;

    public Number getPrecipIntensity() {
        return this.precipIntensity;
    }

    public void setPrecipIntensity(Number precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public Number getPrecipProbability() {
        return this.precipProbability;
    }

    public void setPrecipProbability(Number precipProbability) {
        this.precipProbability = precipProbability;
    }

    public Number getTime() {
        return this.time;
    }

    public void setTime(Number time) {
        this.time = time;
    }
}

class Currently {
    private Number apparentTemperature;
    private Number cloudCover;
    private Number dewPoint;
    private Number humidity;
    private String icon;
    private Number ozone;
    private Number precipIntensity;
    private Number precipProbability;
    private Number pressure;
    private String summary;
    private Number temperature;
    private Number time;
    private Number visibility;
    private Number windBearing;
    private Number windSpeed;

    public Number getApparentTemperature() {
        return this.apparentTemperature;
    }

    public void setApparentTemperature(Number apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public Number getCloudCover() {
        return this.cloudCover;
    }

    public void setCloudCover(Number cloudCover) {
        this.cloudCover = cloudCover;
    }

    public Number getDewPoint() {
        return this.dewPoint;
    }

    public void setDewPoint(Number dewPoint) {
        this.dewPoint = dewPoint;
    }

    public Number getHumidity() {
        return this.humidity;
    }

    public void setHumidity(Number humidity) {
        this.humidity = humidity;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Number getOzone() {
        return this.ozone;
    }

    public void setOzone(Number ozone) {
        this.ozone = ozone;
    }

    public Number getPrecipIntensity() {
        return this.precipIntensity;
    }

    public void setPrecipIntensity(Number precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public Number getPrecipProbability() {
        return this.precipProbability;
    }

    public void setPrecipProbability(Number precipProbability) {
        this.precipProbability = precipProbability;
    }

    public Number getPressure() {
        return this.pressure;
    }

    public void setPressure(Number pressure) {
        this.pressure = pressure;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Number getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Number temperature) {
        this.temperature = temperature;
    }

    public Number getTime() {
        return this.time;
    }

    public void setTime(Number time) {
        this.time = time;
    }

    public Number getVisibility() {
        return this.visibility;
    }

    public void setVisibility(Number visibility) {
        this.visibility = visibility;
    }

    public Number getWindBearing() {
        return this.windBearing;
    }

    public void setWindBearing(Number windBearing) {
        this.windBearing = windBearing;
    }

    public Number getWindSpeed() {
        return this.windSpeed;
    }

    public void setWindSpeed(Number windSpeed) {
        this.windSpeed = windSpeed;
    }
}

class Flags {
    private List darkskyStations;
    private List isdStations;
    private List lampStations;
    private List madisStations;
    private List sources;
    private String units;

    public List getDarkskyStations() {
        return this.darkskyStations;
    }

    public void setDarkskyStations(List darkskyStations) {
        this.darkskyStations = darkskyStations;
    }

    public List getIsdStations() {
        return this.isdStations;
    }

    public void setIsdStations(List isdStations) {
        this.isdStations = isdStations;
    }

    public List getLampStations() {
        return this.lampStations;
    }

    public void setLampStations(List lampStations) {
        this.lampStations = lampStations;
    }

    public List getMadisStations() {
        return this.madisStations;
    }

    public void setMadisStations(List madisStations) {
        this.madisStations = madisStations;
    }

    public List getSources() {
        return this.sources;
    }

    public void setSources(List sources) {
        this.sources = sources;
    }

    public String getUnits() {
        return this.units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}

class Minutely {
    private List data;

    public List getData() {
        return this.data;
    }

    public void setData(List data) {
        this.data = data;
    }
}



