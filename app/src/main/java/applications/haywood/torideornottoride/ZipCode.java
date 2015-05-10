package applications.haywood.torideornottoride;

/**
 * Created by Allan on 5/4/2015.
 */
public class ZipCode {

    static final String ZipCodePattern = "\\d{5}?";

    private int zipCode;
    private float latitude;
    private float longitude;
    private String city;

    ZipCode(int zipCode, String city, float latitude, float longitude) {
        this.zipCode = zipCode;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    ZipCode(int zipCode, String city) {
        this.zipCode = zipCode;
        this.city = city;
        // Valid latitude is from +90 - -90, setting to an invalid value so it is known this is
        // unknown for this location.
        this.latitude = 91;
        // Valid longitude is from +180 - -180, setting to an invalid value so it is known this is
        // unknown for this location.
        this.longitude = 181;
    }

    public int getZipCode() {
        return this.zipCode;
    }

    public String getCity() {
        return this.city;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public float getLongitude() {
        return this.longitude;
    }
}
