package nl.sikken.bertrik.sensor;

/**
 * Represents static data of a sensor.
 */
public final class SensorInfo {

    private final String id;
    private final Double lat;
    private final Double lon;

    /**
     * Constructor.
     * 
     * @param id the unique sensor id
     * @param lat the latitude (decimal degrees WGS84)
     * @param lon the longitude (decimal degrees WGS84)
     */
    public SensorInfo(String id, Double lat, Double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
    
}
