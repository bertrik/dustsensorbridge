package nl.sikken.bertrik.sensor;

import java.util.Locale;

/**
 * Represents static data of a sensor.
 */
public final class SensorInfo {

    private final Double lat;
    private final Double lon;

    /**
     * Constructor.
     * 
     * @param lat the latitude (decimal degrees WGS84)
     * @param lon the longitude (decimal degrees WGS84)
     */
    public SensorInfo(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
    
    @Override
    public String toString() {
    	return String.format(Locale.US, "{%f,%f}", lat, lon);
    }
    
}
