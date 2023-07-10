package zeebe.manager;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.ClientStatusException;
import io.grpc.Status;
import zeebe.client.ZeebeClientProvider;

import java.util.Map;

public class ZeebeManager {

	private static final ZeebeClient ZEEBE_CLIENT = ZeebeClientProvider.get();

	public static void cancelAllActiveProcessesByKeyList(Map<String, Long> processKeys) {
		processKeys.forEach((processKey, bpmnProcessId) -> {
			try {
				ZEEBE_CLIENT.newCancelInstanceCommand(bpmnProcessId).send().join();
			} catch (ClientStatusException e) {
				if (e.getStatusCode() != Status.Code.NOT_FOUND) {
					throw e;
				}
			}
		});
	}

}
