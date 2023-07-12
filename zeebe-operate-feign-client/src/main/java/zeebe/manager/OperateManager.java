package zeebe.manager;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import zeebe.enums.OperateProcessDefinitionBpmnProcessIds;
import zeebe.enums.OperateProcessFlowState;
import zeebe.exceptions.ZeebeOperateTestException;
import zeebe.feign.client.OperateFeignClient;
import zeebe.feign.client.OperateFeignClientProvider;
import zeebe.feign.dto.OperateRequestDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OperateManager {

	public static final OperateFeignClient OPERATE_FEIGN_CLIENT = OperateFeignClientProvider.get();

	private static final Map<String, Long> PROCESS_DEFINITION_BPMN_ID_AND_KEY_MAP = new HashMap<>();

	private static final String BPMN_PROCESS_ID_AND_KEY = "items.collectEntries{[it.bpmnProcessId, it.key]}";

	private static final String PROCESS_INSTANCE_KEY_PROCESS_DEFINITION_KEY = "items.collectEntries{[it.processInstanceKey, it.processDefinitionKey]}";

	/**
	 * WARNING!<br>
	 * You have to call this method in {@link BeforeClass} to collect a map of definition keys and their bpmnProcess id's
	 */
	public static void collectOperateProcessDefinitionKeysMap() {
		OperateRequestDto operateRequestDto = OperateRequestDto.builder().build();
		Map<String, Long> processDefinitionsSearch = OPERATE_FEIGN_CLIENT
				.getProcessDefinitionsSearch(operateRequestDto)
				.jsonPath()
				.getMap(BPMN_PROCESS_ID_AND_KEY, String.class, Long.class);

		PROCESS_DEFINITION_BPMN_ID_AND_KEY_MAP.putAll(processDefinitionsSearch);
	}

	public static Map<String, Long> getActiveProcessInstances() {
		OperateRequestDto operateRequestDto = OperateRequestDto.builder()
				.filter(
						OperateRequestDto.ProcessInstancesSearchFilter
								.builder()
								.state(OperateProcessFlowState.ACTIVE)
								.build()
				)
				.build();

		return OPERATE_FEIGN_CLIENT.getProcessInstancesSearch(operateRequestDto)
				.jsonPath()
				.getMap(BPMN_PROCESS_ID_AND_KEY, String.class, Long.class);
	}

	public static Map<Long, Long> getActiveIncidents() {
		OperateRequestDto operateRequestDto = OperateRequestDto
				.builder()
				.filter(
						OperateRequestDto.FlowNodeInstancesSearchFilter.builder()
								.state(OperateProcessFlowState.ACTIVE)
								.incident(true)
								.build())
				.build();

		return OPERATE_FEIGN_CLIENT.getFlowNodeInstancesSearch(operateRequestDto)
				.jsonPath()
				.getMap(PROCESS_INSTANCE_KEY_PROCESS_DEFINITION_KEY, Long.class, Long.class);
	}

	public static Map<Long, Long> getProcessOnFlowNodeId(OperateProcessDefinitionBpmnProcessIds bpmnProcessId,
														 String flowNodeId) {
		OperateRequestDto operateRequestDto = OperateRequestDto.builder()
				.filter(
						OperateRequestDto.FlowNodeInstancesSearchFilter.builder()
								.processDefinitionKey(getProcessDefinitionKey(bpmnProcessId))
								.state(OperateProcessFlowState.ACTIVE)
								.flowNodeId(flowNodeId)
								.build())

				.build();

		return OPERATE_FEIGN_CLIENT.getFlowNodeInstancesSearch(operateRequestDto)
				.body()
				.jsonPath()
				.getMap(PROCESS_INSTANCE_KEY_PROCESS_DEFINITION_KEY, Long.class, Long.class);
	}

	public static Map<Long, Long> getIncidentOnFlowNodeId(OperateProcessDefinitionBpmnProcessIds bpmnProcessId,
														  String flowNodeId) {
		OperateRequestDto operateRequestDto =
				OperateRequestDto
						.builder().filter(
								OperateRequestDto.FlowNodeInstancesSearchFilter.builder()
										.processDefinitionKey(getProcessDefinitionKey(bpmnProcessId))
										.state(OperateProcessFlowState.ACTIVE)
										.flowNodeId(flowNodeId)
										.incident(true)
										.build()
						)
						.build();

		return OPERATE_FEIGN_CLIENT.getFlowNodeInstancesSearch(operateRequestDto)
				.jsonPath()
				.getMap(PROCESS_INSTANCE_KEY_PROCESS_DEFINITION_KEY, Long.class, Long.class);
	}


	public static void attachProcessInstanceVariables() {
		Map<String, Long> processesInstanceListByState = getActiveProcessInstances();

		processesInstanceListByState.forEach((bpmnProcessId, processKey) -> {
			OperateRequestDto operateRequestDto = OperateRequestDto.builder()
					.filter(
							OperateRequestDto.VariablesSearchFilter.builder()
									.processInstanceKey(processKey)
									.build())
					.build();

			String processVariables = OPERATE_FEIGN_CLIENT.getVariablesSearch(operateRequestDto)
					.asPrettyString();
			Allure.attachment(String.format("Variables of processInstanceKey: %s", processKey), processVariables);
		});

	}

	private static Long getProcessDefinitionKey(OperateProcessDefinitionBpmnProcessIds bpmnProcessId) {
		if (!PROCESS_DEFINITION_BPMN_ID_AND_KEY_MAP.isEmpty()) {
			return PROCESS_DEFINITION_BPMN_ID_AND_KEY_MAP.get(bpmnProcessId.getBpmnProcessId());
		}

		throw new ZeebeOperateTestException("Map of process definition key's and bpmnId's is empty");
	}

}
