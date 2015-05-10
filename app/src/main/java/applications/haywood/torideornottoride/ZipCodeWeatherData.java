
package applications.haywood.torideornottoride;

public class ZipCodeWeatherData {
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
