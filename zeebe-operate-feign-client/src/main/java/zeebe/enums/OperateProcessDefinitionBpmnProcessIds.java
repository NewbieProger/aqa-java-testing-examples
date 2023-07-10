package zeebe.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OperateProcessDefinitionBpmnProcessIds {

	SOME_BPMN_PROCESS_ID("some-bpmn-process-id");

	private final String bpmnProcessId;
}
