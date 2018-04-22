package nl.sikken.bertrik.sensor.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Triplet of three particulate concentrations.
 */
public final class SensorPms {

    @JsonProperty("pm1_0")
    private Double pm1_0;
    
    @JsonProperty("pm2_5")
    private Double pm2_5;
    
    @JsonProperty("pm10")
    private Double pm10;
    
    private SensorPms() {
        // Jackson constructor
    }
    
    /**
     * Constructor.
     * 
     * @param pm1_0 PM1.0 concentration in ug/m3
     * @param pm2_5 PM2.5 concentration in ug/m3
     * @param pm10 PM10 concentration in ug/m3
     */
    public SensorPms(Double pm1_0, Double pm2_5, Double pm10) {
        this();
        this.pm1_0 = pm1_0;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
    }
    
    public Double getPm1_0() {
        return pm1_0;
    }

    public Double getPm2_5() {
        return pm2_5;
    }

    public Double getPm10() {
        return pm10;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.format(Locale.US, "{pm1_0=%.1f,pm2_5=%.1f,pm10=%.1f}", pm1_0, pm2_5, pm10);
    }
    
}
