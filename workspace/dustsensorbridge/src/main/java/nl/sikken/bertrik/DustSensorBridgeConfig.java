package nl.sikken.bertrik;

import nl.sikken.bertrik.config.BaseConfig;

/**
 * Configuration class.
 */
public final class DustSensorBridgeConfig extends BaseConfig implements IDustSensorBridgeConfig {
    
	private enum EConfigItem {
        SENSOR_LAT("sensor.lat", "52.02264", "Sensor latitude"),
        SENSOR_LON("sensor.lon", "4.69260", "Sensor longitude"),
        
        MQTT_URL("mqtt.url", "tcp://aliensdetected.com", "URL of the MQTT server"),
        MQTT_TOPIC("mqtt.topic", "bertrik/dust/#", "The sensor MQTT topic"),

        SAMENMETEN_URL("samenmeten.url", "http://influx.rivm.nl:8086", "samenmeten server URL (empty to disable)"),
        SAMENMETEN_ID("samenmeten.id", "pms7003", "Unique samenmeten sensor id"),
        SAMENMETEN_USER("samenmeten.user", "bsik", "User name for the samenmeten server"),
        SAMENMETEN_PASS("samenmeten.pass", "", "Password for the samenmeten server"),
        
        LUFTDATEN_URL("luftdaten.url", "https://api.luftdaten.info", "luftdaten server URL (empty to disable)"),
        LUFTDATEN_TIMEOUT("luftdaten.timeout", "3000", "luftdaten timeout"),
        LUFTDATEN_ID("luftdaten.id", "", "luftdaten sensor id (empty for automatic)"),
        LUFTDATEN_VERSION("luftdaten.version", "0.1", "luftdaten uploader version");
		
		private final String key, value, comment;
		
		private EConfigItem(String key, String defValue, String comment) {
			this.key = key;
			this.value = defValue;
			this.comment = comment;
		}
	}
    
    /**
     * Constructor.
     */
    public DustSensorBridgeConfig() {
        for (EConfigItem e : EConfigItem.values()) {
            add(e.key, e.value, e.comment);
        }
    }
    
	@Override
    public Double getSensorLat() {
        return Double.valueOf(get(EConfigItem.SENSOR_LAT.key));
    }

    @Override
    public Double getSensorLon() {
        return Double.valueOf(get(EConfigItem.SENSOR_LON.key));
    }

    @Override
    public String getMqttUrl() {
        return get(EConfigItem.MQTT_URL.key);
    }

    @Override
    public String getMqttTopic() {
        return get(EConfigItem.MQTT_TOPIC.key);
    }

    @Override
	public String getSamenMetenId() {
	    return get(EConfigItem.SAMENMETEN_ID.key);
	}

	@Override
    public String getSamenMetenUrl() {
        return get(EConfigItem.SAMENMETEN_URL.key).trim();
    }

    @Override
    public String getSamenMetenUsername() {
        return get(EConfigItem.SAMENMETEN_USER.key);
    }

    @Override
    public String getSamenMetenPassword() {
        return get(EConfigItem.SAMENMETEN_PASS.key);
    }

	@Override
	public String getLuftdatenUrl() {
        return get(EConfigItem.LUFTDATEN_URL.key).trim();
	}

	@Override
	public int getLuftdatenTimeout() {
		return Integer.parseInt(get(EConfigItem.LUFTDATEN_TIMEOUT.key));
	}

	@Override
	public String getLuftdatenIdOverride() {
		return get(EConfigItem.LUFTDATEN_ID.key).trim();
	}

	@Override
	public String getLuftdatenVersion() {
		return get(EConfigItem.LUFTDATEN_VERSION.key);
	}
    
}