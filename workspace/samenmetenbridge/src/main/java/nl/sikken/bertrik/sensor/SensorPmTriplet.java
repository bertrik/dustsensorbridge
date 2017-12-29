package nl.sikken.bertrik.sensor;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Triplet of three particulate concentrations.
 */
public final class SensorPmTriplet {

    @JsonProperty("pm1_0")
    private Integer pm1_0;
    
    @JsonProperty("pm2_5")
    private Integer pm2_5;
    
    @JsonProperty("pm10")
    private Integer pm10;
    
    private SensorPmTriplet() {
        // Jackson constructor
    }
    
    /**
     * Constructor.
     * 
     * @param pm1_0 PM1.0 concentration in ug/m3
     * @param pm2_5 PM2.5 concentration in ug/m3
     * @param pm10 PM10 concentration in ug/m3
     */
    public SensorPmTriplet(Integer pm1_0, Integer pm2_5, Integer pm10) {
        this();
        this.pm1_0 = pm1_0;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
    }
    
    public Integer getPm1_0() {
        return pm1_0;
    }

    public Integer getPm2_5() {
        return pm2_5;
    }

    public Integer getPm10() {
        return pm10;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.format(Locale.US, "{pm1_0=%d,pm2_5=%d,pm10=%d}", pm1_0, pm2_5, pm10);
    }
    
}
