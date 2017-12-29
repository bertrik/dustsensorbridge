package nl.sikken.bertrik.sensor;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a message received from the MQTT stream.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SensorMessage {

    @JsonProperty("cf1")
    private SensorPmTriplet cf1;
    
    @JsonProperty("amb")
    private SensorPmTriplet amb;
    
    private SensorMessage() {
        // Jackson constructor
    }
    
    /**
     * Constructor.
     * 
     * @param cf1 cf=1 values
     * @param amb amb values
     */
    public SensorMessage(SensorPmTriplet cf1, SensorPmTriplet amb) {
        this();
        this.cf1 = cf1;
        this.amb = amb;
    }
    
    public SensorPmTriplet getCf1() {
        return cf1;
    }

    public SensorPmTriplet getAmb() {
        return amb;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{cf1=%s,amb=%s}", cf1, amb);
    }
    
}
