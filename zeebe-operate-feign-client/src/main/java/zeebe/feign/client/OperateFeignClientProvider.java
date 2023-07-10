package zeebe.feign.client;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.restassured.response.Response;
import zeebe.exceptions.ZeebeOperateTestException;

import java.util.Objects;

public class OperateFeignClientProvider {

	private static final OperateFeignClient FEIGN_CLIENT;

	static {
		FEIGN_CLIENT = Feign.builder()
				.encoder(new JacksonEncoder())
				.decoder(new RestAssuredResponseDecoder(new JacksonDecoder()))
				.requestInterceptor(new OperateAuthInterceptor())
				.target(OperateFeignClient.class, "http://localhost:8080");
	}

	public static OperateFeignClient get() {
		return FEIGN_CLIENT;
	}

	private static class OperateAuthInterceptor implements RequestInterceptor {

		private String setCookie;

		@Override
		public void apply(RequestTemplate template) {

			if (template.path().equals("/api/login")) {
				return;
			}
			if (Objects.isNull(setCookie)) {
				Response response = OperateFeignClientProvider.get().getAuthCookie();
				setCookie = response.header("Set-Cookie");
				if (Objects.isNull(setCookie)) {
					throw new ZeebeOperateTestException("Didn't find cookie: Set-Cookie header in OperateResponse");
				}
			}
			template.header("Cookie", setCookie);
		}
	}


}
