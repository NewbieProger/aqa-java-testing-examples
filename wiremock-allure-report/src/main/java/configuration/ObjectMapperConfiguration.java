package configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperConfiguration {

	private static ObjectMapper defaultObjectMapper;

	public static ObjectMapper getDefaultObjectMapper() {
		if (defaultObjectMapper == null) {
			defaultObjectMapper = new ObjectMapper();
			defaultObjectMapper.registerModule(new JavaTimeModule());
		}
		return defaultObjectMapper;
	}
}
