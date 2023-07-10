package zeebe.feign.client;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.Header;
import io.restassured.http.Headers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RestAssuredResponseDecoder implements Decoder {

	private final Decoder fallbackDecoder;

	public RestAssuredResponseDecoder(Decoder fallbackDecoder) {
		this.fallbackDecoder = fallbackDecoder;
	}

	@Override
	public Object decode(Response response, Type type) throws IOException, FeignException {

		if (type == io.restassured.response.Response.class) {
			List<Header> restAssuredHeaderList = response.headers().entrySet().stream()
					.flatMap(feignHeader -> feignHeader.getValue()
							.stream()
							.map(headerValue -> new Header(feignHeader.getKey(), headerValue)))
					.toList();

			return new ResponseBuilder()
					.setBody(response.body().asInputStream().readAllBytes())
					.setHeaders(new Headers(restAssuredHeaderList))
					.setStatusCode(response.status())
					.build();
		}

		return fallbackDecoder.decode(response, type);
	}
}
