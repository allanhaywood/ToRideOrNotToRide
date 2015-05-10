
package applications.haywood.torideornottoride;

public class ZipCodeWeather {
    private Currently currently;
    private Flags flags;
    private Number latitude;
    private Number longitude;
    private Minutely minutely;
    private Number offset;
    private String timezone;

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
