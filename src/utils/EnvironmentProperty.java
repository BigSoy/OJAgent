package utils;

import java.io.IOException;
import java.util.Properties;

public class EnvironmentProperty {

	private static Properties properties = new Properties();
	static {
		try {
			properties.load(EnvironmentProperty.class.getResourceAsStream("/environment.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key) {
		return properties.getProperty(key);
	}
}
