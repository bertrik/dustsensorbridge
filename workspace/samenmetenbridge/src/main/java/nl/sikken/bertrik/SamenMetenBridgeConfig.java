package nl.sikken.bertrik;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration class.
 */
public final class SamenMetenBridgeConfig implements ISamenMetenBridgeConfig {
    
    /**
     * One enumeration item per configuration item.
     */
    private enum EConfigItem {
        SENSOR_ID("sensor.id", "pms7003", "Unique sensor id"),
        SENSOR_LAT("sensor.lat", "52.02264", "Sensor latitude"),
        SENSOR_LON("sensor.lon", "4.69260", "Sensor longitude"),
        
        MQTT_URL("mqtt.url", "tcp://aliensdetected.com", "URL of the MQTT server"),
        MQTT_TOPIC("mqtt.topic", "bertrik/pms7003/json", "The sensor MQTT topic"),

        INFLUX_URL("influx.url", "http://influx.rivm.nl:8086", "URL of the influx server"),
        INFLUX_USER("influx.user", "bsik", "User name for the influx server"),
        INFLUX_PASS("influx.pass", "", "Password for the influx server")
        ;
        
        private final String key;
        private final String def;
        private final String comment;

        EConfigItem(String key, String def, String comment) {
            this.key = key;
            this.def = def;
            this.comment = comment;
        }
    }
    
    private final Map<EConfigItem, String> props = new HashMap<>();
    
    /**
     * Constructor.
     * 
     * Configures all settings to their default value.
     */
    public SamenMetenBridgeConfig() {
        for (EConfigItem e : EConfigItem.values()) {
            props.put(e, e.def);
        }
    }
    
    /**
     * Load settings from stream.
     * 
     * @param is input stream containing the settings
     * @throws IOException in case of a problem reading the file
     */
    public void load(InputStream is) throws IOException {
        final Properties properties = new Properties();
        properties.load(is);
        for (EConfigItem e : EConfigItem.values()) {
            String value = properties.getProperty(e.key);
            if (value != null) {
                props.put(e, value);
            }
        }
    }
    
    /**
     * Save settings to stream.
     * 
     * @param os the output stream
     * @throws IOException in case of a file problem
     */
    public void save(OutputStream os) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, StandardCharsets.US_ASCII)) {
            for (EConfigItem e : EConfigItem.values()) {
                // comment line
                writer.append("# " + e.comment + "\n");
                writer.append(e.key + "=" + e.def + "\n");
                writer.append("\n");
            }
        }
    }

    @Override
    public String getSensorId() {
        return props.get(EConfigItem.SENSOR_ID);
    }

    @Override
    public Double getSensorLat() {
        return Double.valueOf(props.get(EConfigItem.SENSOR_LAT));
    }

    @Override
    public Double getSensorLon() {
        return Double.valueOf(props.get(EConfigItem.SENSOR_LON));
    }

    @Override
    public String getMqttUrl() {
        return props.get(EConfigItem.MQTT_URL);
    }

    @Override
    public String getMqttTopic() {
        return props.get(EConfigItem.MQTT_TOPIC);
    }

    @Override
    public String getInfluxUrl() {
        return props.get(EConfigItem.INFLUX_URL);
    }

    @Override
    public String getInfluxUsername() {
        return props.get(EConfigItem.INFLUX_USER);
    }

    @Override
    public String getInfluxPassword() {
        return props.get(EConfigItem.INFLUX_PASS);
    }
    
}