package zeebe.client;

import io.camunda.zeebe.client.ZeebeClient;

public class ZeebeClientProvider {

	private static final ZeebeClient ZEEBE_CLIENT;

	static {

		ZEEBE_CLIENT = ZeebeClient.newClientBuilder()
				.gatewayAddress("localhost:26500")
				.usePlaintext()
				.build();
	}

	public static ZeebeClient get() {
		return ZEEBE_CLIENT;
	}

}
