package nl.sikken.bertrik.samenmeten;

import java.time.Instant;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.sikken.bertrik.sensor.SensorInfo;
import nl.sikken.bertrik.sensor.SensorMessage;

/**
 * Uploader towards samenmeten server.
 */
public final class SamenMetenUploader {

    private static final Logger LOG = LoggerFactory.getLogger(SamenMetenUploader.class);
    
    private final String url;
    private final String dbName;
    private final String username;
    private final String password;
    
    private InfluxDB influxDB;
    private Instant timeStampFrom;

    public SamenMetenUploader(String url, String dbName, String username, String password) {
        this.url = url;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }
    
    public void start() {
        LOG.info("Starting SamenMeten Uploader");
//        this.influxDB = InfluxDBFactory.connect(url, username, password);
    }
    
    public void stop() {
        LOG.info("Stopping SamenMeten Uploader");
//        influxDB.close();
    }

    public void scheduleMeasurementUpload(SensorInfo info, SensorMessage message, Instant now) {
        LOG.info("scheduleMeasurementUpload({}, {})", message, now);

        BatchPoints batchPoints = BatchPoints.database(dbName)
                .consistency(ConsistencyLevel.QUORUM)
                .build();
        
        // create measurement point for PM10
        Point point = createPoint(info, message, now);
        batchPoints.point(point);
            
//        influxDB.write(batchPoints);
    }
    
    public Point createPoint(SensorInfo info, SensorMessage message, Instant now) {
        // create measurement point
        Point.Builder builder = Point.measurement("m_" + username);
        builder.tag("id", info.getId());
        builder.addField("lat", info.getLat()).addField("lon", info.getLon());
        
        // add timestamp
        if (timeStampFrom == null) {
            // fake a timestamp from 1 second ago
            timeStampFrom = now.minusSeconds(1);
        }
        builder.addField("timestamp_from", timeStampFrom.toString());
        builder.addField("timestamp_to", now.toString());
        timeStampFrom = Instant.from(now);
        
        // add PM
        builder.addField("PM", message.getAmb().getPm10().doubleValue());
        
        Point point = builder.build();
        return point;
    }

}
