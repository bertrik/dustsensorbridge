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

import nl.sikken.bertrik.IUploader;
import nl.sikken.bertrik.ServerInfo;
import nl.sikken.bertrik.sensor.SensorInfo;
import nl.sikken.bertrik.sensor.dto.SensorBme;
import nl.sikken.bertrik.sensor.dto.SensorMessage;
import nl.sikken.bertrik.sensor.dto.SensorPms;

/**
 * Uploader towards samenmeten server.
 */
public final class SamenMetenUploader implements IUploader {

    private static final Logger LOG = LoggerFactory.getLogger(SamenMetenUploader.class);
    
    private final ServerInfo serverInfo;
    private final SensorInfo sensorInfo;
    
    private InfluxDB influxDB;
    private Instant timeStampFrom;

    /**
     * Constructor.
     * 
     * @param serverInfo the upload server info
     * @param sensorInfo the static sensor info
     */
    public SamenMetenUploader(ServerInfo serverInfo, SensorInfo sensorInfo) {
    	this.serverInfo = serverInfo;
    	this.sensorInfo = sensorInfo;
    }
    
	@Override
	public void start() {
        LOG.info("Starting SamenMeten Uploader");
		influxDB = InfluxDBFactory.connect(serverInfo.getUrl(), serverInfo.getUser(), serverInfo.getPass());
        influxDB.enableBatch();
    }
    
	@Override
	public void stop() {
        LOG.info("Stopping SamenMeten Uploader");
        influxDB.close();
    }

	@Override
	public void uploadMeasurement(Instant now, SensorMessage message) {
        LOG.info("uploadMeasurement({}, {})", message, now);

        // calculate timestamp
		Instant timeStampTo = now;
        if (timeStampFrom == null) {
            // fake a timestamp from 1 second ago
            timeStampFrom = now.minusSeconds(10);
        }

        // create common measurement points builder
        Builder builder = createPointBuilder(sensorInfo, timeStampFrom, timeStampTo);
        
        // add dust fields from PMS7003
        SensorPms pms = message.getPms();
        builder.addField("PM10", pms.getPm10());
        builder.addField("PM2.5", pms.getPm2_5());
        builder.addField("PM1", pms.getPm1_0());
        builder.addField("PM-meetopstelling", "Plantower PMS7003");
        
        // add meteo fields from BME280
        SensorBme bme = message.getBme();
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

		BatchPoints batchPoints = BatchPoints.database(serverInfo.getUser()).build();
		batchPoints.point(builder.build());
        
        // update last sent timestamp
        timeStampFrom = Instant.from(now);
        
        // perform the actual upload
        try {
        	LOG.info("Writing {}", batchPoints);
        	influxDB.write(batchPoints);
        	LOG.info("Done");
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
        Point.Builder builder = Point.measurement("m_" + serverInfo.getUser());
        builder.tag("id", serverInfo.getId());
        builder.addField("lat", info.getLat()).addField("lon", info.getLon());
        
        builder.addField("timestamp_from", timeStampFrom.toString());
        builder.addField("timestamp_to", timeStampTo.toString());
        
        return builder;
    }

}
