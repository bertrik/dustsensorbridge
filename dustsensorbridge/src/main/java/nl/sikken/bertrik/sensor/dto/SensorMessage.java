package nl.sikken.bertrik.sensor.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a message received from the MQTT stream.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SensorMessage {

	@JsonProperty("alive")
	private int alive;
	
    @JsonProperty("pms7003")
    private SensorPms pms;
    
    @JsonProperty("SDS011")
    private SensorSds sds;
    
    @JsonProperty("bme280")
    private SensorBme bme;
    
    private SensorMessage() {
        // Jackson constructor
    }
    
    /**
     * Constructor.
     * 
     * @param pms dust sensor values
     * @param bme meteo sensor value
     */
    public SensorMessage(SensorPms pms, SensorBme bme) {
        this();
        this.pms = pms;
        this.bme = bme;
    }
    
    public int getAlive() {
    	return alive;
    }
    
    public SensorPms getPms() {
        return pms;
    }

    public SensorSds getSds() {
    	return sds;
    }
    
    public SensorBme getBme() {
        return bme;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{alive=%d,pms=%s,sds=%s,bme=%s}", alive, pms, sds, bme);
    }
    
}
