package weather2.ltcompat;

import com.lovetropics.weather.ClientWeather;
import com.lovetropics.weather.TypeBridge;
import weather2.datatypes.PrecipitationType;

public final class ClientWeatherIntegration {
	private static ClientWeatherIntegration instance = new ClientWeatherIntegration();

	private ClientWeatherIntegration() {
	}

	public static ClientWeatherIntegration get() {
		return instance;
	}

	public static void reset() {
		instance = new ClientWeatherIntegration();
	}

	public float getRainAmount() {
		return ClientWeather.get().getRainAmount();
	}

	public float getVanillaRainAmount() {
		return ClientWeather.get().getVanillaRainAmount();
	}

	public PrecipitationType getPrecipitationType() {
		return PrecipitationType.VALUES[TypeBridge.getPrecipitationTypeOrdinal(ClientWeather.get())];
	}

	public float getWindSpeed() {
		return ClientWeather.get().getWindSpeed();
	}

	public boolean isHeatwave() {
		return ClientWeather.get().isHeatwave();
	}

	public boolean isSandstorm() {
		return ClientWeather.get().isSandstorm();
	}

	public boolean isSnowstorm() {
		return ClientWeather.get().isSnowstorm();
	}

	public boolean hasWeather() {
		return ClientWeather.get().hasWeather();
	}
}