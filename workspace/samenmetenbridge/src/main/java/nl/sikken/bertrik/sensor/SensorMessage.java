package nl.sikken.bertrik.sensor;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a message received from the MQTT stream.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SensorMessage {

    @JsonProperty("pms7003")
    private SensorPmTriplet pms;
    
    @JsonProperty("bme280")
    private SensorBmeMessage bme;
    
    private SensorMessage() {
        // Jackson constructor
    }
    
    /**
     * Constructor.
     * 
     * @param pms dust sensor values
     * @param bme meteo sensor value
     */
    public SensorMessage(SensorPmTriplet pms, SensorBmeMessage bme) {
        this();
        this.pms = pms;
        this.bme = bme;
    }
    
    public SensorPmTriplet getPms() {
        return pms;
    }

    public SensorBmeMessage getBme() {
        return bme;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{pms=%s,bme=%s}", pms, bme);
    }
    
}
