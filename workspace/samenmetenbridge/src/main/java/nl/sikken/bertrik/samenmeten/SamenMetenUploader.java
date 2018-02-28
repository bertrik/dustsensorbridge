package nl.sikken.bertrik.samenmeten;

import java.time.Instant;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.sikken.bertrik.sensor.SensorBmeMessage;
import nl.sikken.bertrik.sensor.SensorInfo;
import nl.sikken.bertrik.sensor.SensorMessage;
import nl.sikken.bertrik.sensor.SensorPmTriplet;

/**
 * Uploader towards samenmeten server.
 */
public final class SamenMetenUploader {

    private static final Logger LOG = LoggerFactory.getLogger(SamenMetenUploader.class);
    
    private final String url;
    private final String username;
    private final String password;
    
    private InfluxDB influxDB;
    private Instant timeStampFrom;

    /**
     * Constructor.
     * 
     * @param url the URL of the influx db
     * @param username the user name (same as database name)
     * @param password the password
     */
    public SamenMetenUploader(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Starts this sub-module.
     */
    public void start() {
        LOG.info("Starting SamenMeten Uploader");
        this.influxDB = InfluxDBFactory.connect(url, username, password);
    }
    
    /**
     * Stops this sub-module.
     */
    public void stop() {
        LOG.info("Stopping SamenMeten Uploader");
        influxDB.close();
    }

    /**
     * Uploads a sensor measurement.
     * 
     * @param info the static sensor info
     * @param message the sensor measurement
     * @param now the current time
     */
    public void uploadMeasurement(SensorInfo info, SensorMessage message, Instant now) {
        LOG.info("scheduleMeasurementUpload({}, {})", message, now);

        // calculate timestamp
		Instant timeStampTo = now;
        if (timeStampFrom == null) {
            // fake a timestamp from 1 second ago
            timeStampFrom = now.minusSeconds(10);
        }

        // create common measurement points builder
        Builder builder = createPointBuilder(info, timeStampFrom, timeStampTo);
        
        // add dust fields from PMS7003
        SensorPmTriplet pms = message.getPms();
        builder.addField("PM10", pms.getPm10().doubleValue());
        builder.addField("PM2.5", pms.getPm2_5().doubleValue());
        builder.addField("PM1", pms.getPm1_0().doubleValue());
        builder.addField("PM-meetopstelling", "Plantower PMS7003");
        
        // add meteo fields from BME280
        SensorBmeMessage bme = message.getBme();
        if (bme.hasValidTemp()) {
	        builder.addField("T", bme.getTemp());
	        builder.addField("T-meetopstelling", "BME280");
        }
        if (bme.hasValidRh()) {
	        builder.addField("RH", bme.getRh());
	        builder.addField("RH-meetopstelling", "BME280");
        }
        if (bme.hasValidPressure()) {
	        builder.addField("P", bme.getPressure());
	        builder.addField("P-meetopstelling", "BME280");
        }

		BatchPoints batchPoints = BatchPoints.database(username).build();
		batchPoints.point(builder.build());
        
        // update last sent timestamp
        timeStampFrom = Instant.from(now);
        
        // send it to influxdb
        try {
        	LOG.info("Writing {}", batchPoints);
        	influxDB.write(batchPoints);
        } catch (InfluxDBException e) {
        	LOG.warn("Caught InfluxDBException: {}", e.getMessage().trim());
        }
    }
    
    /**
     * Creates a basic measurement point builder with default properties.
     * 
     * @param info static sensor info (e.g. latitude / longitude)
     * @param timeStampFrom the timestamp from value
     * @param timeStampTo the timestamp to value
     * @return a measurement builder to which fields can be added
     */
    private Builder createPointBuilder(SensorInfo info, Instant timeStampFrom, Instant timeStampTo) {
        // create measurement point
        Point.Builder builder = Point.measurement("m_" + username);
        builder.tag("id", info.getId());
        builder.addField("lat", info.getLat()).addField("lon", info.getLon());
        
        builder.addField("timestamp_from", timeStampFrom.toString());
        builder.addField("timestamp_to", timeStampTo.toString());
        
        return builder;
    }

}
