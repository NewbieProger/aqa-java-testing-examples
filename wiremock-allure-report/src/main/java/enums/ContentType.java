package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ContentType {
	APPLICATION_JSON("application/json"),
	APPLICATION_XML("application/xml"),
	APPLICATION_SOAP_XML("application/soap+xml"),
	TEXT_XML("text/xml"),
	UNDEFINED("undefined");

	final String value;

	public static ContentType getNameByValue(String value) {
		return Arrays.stream(ContentType.values())
				.filter(contentType -> contentType.getValue().equals(value))
				.findFirst()
				.orElse(ContentType.UNDEFINED);
	}

}