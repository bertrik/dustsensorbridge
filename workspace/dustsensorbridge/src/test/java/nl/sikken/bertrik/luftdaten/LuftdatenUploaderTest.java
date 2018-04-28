package nl.sikken.bertrik.luftdaten;

import java.io.IOException;
import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import nl.sikken.bertrik.luftdaten.dto.LuftdatenMessage;
import nl.sikken.bertrik.sensor.dto.SensorBme;
import nl.sikken.bertrik.sensor.dto.SensorMessage;
import nl.sikken.bertrik.sensor.dto.SensorPms;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Unit tests of LuftdatenUploader.
 */
public final class LuftdatenUploaderTest {

	/**
	 * Verifies the upload process up until the REST client.
	 * @throws IOException 
	 */
	@Test
	public void testUpload() throws IOException {
		// create mock
		ILuftdatenApi api = Mockito.mock(ILuftdatenApi.class);

		@SuppressWarnings("unchecked")
		Call<String> call = Mockito.mock(Call.class);
		Mockito.when(call.execute()).thenReturn(Response.success("OK"));
		Mockito.when(api.pushSensorData(Mockito.eq("1"), Mockito.any(), Mockito.any())).thenReturn(call);
		LuftdatenUploader uploader = new LuftdatenUploader(api, "0.0");

		SensorMessage message = 
				new SensorMessage(new SensorPms(0.0, 2.5, 10.0), new SensorBme(0.0, 0, 1000.0));
		uploader.uploadMeasurement(Instant.now(), 0, message);
		
		ArgumentCaptor<LuftdatenMessage> captor = ArgumentCaptor.forClass(LuftdatenMessage.class);
		Mockito.verify(api, Mockito.timeout(3000)).pushSensorData(Mockito.eq("1"), Mockito.any(), captor.capture());
		
		LuftdatenMessage actual = captor.getValue();
		Assert.assertEquals("0.0", actual.getSoftwareVersion());
		Assert.assertEquals("P1", actual.getItems().get(0).getName());
		Assert.assertEquals("10.0", actual.getItems().get(0).getValue());
	}

}
