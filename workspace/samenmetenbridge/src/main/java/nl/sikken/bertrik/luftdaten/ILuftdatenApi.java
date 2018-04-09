package nl.sikken.bertrik.luftdaten;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.sikken.bertrik.luftdaten.dto.LuftdatenMessage;

/**
 * REST API for luftdaten.info
 */
@Path("/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ILuftdatenApi {

	@Path("/push-sensor-data/")
	@POST
	String pushSensorData(LuftdatenMessage message);
	
}
