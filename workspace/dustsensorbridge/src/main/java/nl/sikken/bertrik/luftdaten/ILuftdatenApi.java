package nl.sikken.bertrik.luftdaten;

import nl.sikken.bertrik.luftdaten.dto.LuftdatenMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * REST API for luftdaten.info
 */
public interface ILuftdatenApi {

	@POST("/v1/push-sensor-data/")
	Call<String> pushSensorData(
			@Header("X-Pin") String pin, @Header("X-Sensor") String sensor, @Body LuftdatenMessage message);
	
}
