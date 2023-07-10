package zeebe.feign.dto;

import lombok.Builder;
import lombok.Value;
import zeebe.enums.OperateProcessFlowState;

@Value
@Builder
public class OperateRequestDto {

	@Builder.Default
	OperateFilter filter = null;
	@Builder.Default
	Long size = 100L;

	@Builder
	@Value
	public static class ProcessInstancesSearchFilter implements OperateFilter {
		Long key;
		Long processVersion;
		String bpmnProcessId;
		Long parentKey;
		String startDate;
		String endDate;
		OperateProcessFlowState state;
		Long processDefinitionKey;
	}

	@Builder
	@Value
	public static class ProcessDefinitionSearchFilter implements OperateFilter {
		Long key;
		String name;
		Long version;
		String bpmnProcessId;
	}

	@Builder
	@Value
	public static class FlowNodeInstancesSearchFilter implements OperateFilter {
		Long key;
		Long processInstanceKey;
		Long processDefinitionKey;
		String startDate;
		String endDate;
		String flowNodeId;
		String flowNodeName;
		Long incidentKey;
		String type;
		OperateProcessFlowState state;
		@Builder.Default
		Boolean incident = null;
	}

	@Builder
	@Value
	public static class VariablesSearchFilter implements OperateFilter {
		Long key;
		Long processInstanceKey;
		Long scopeKey;
		String Setter;
		@Builder.Default
		Boolean truncated = null;
	}

	@Builder
	@Value
	public static class IncidentsSearchFilter implements OperateFilter {
		Long key;
		Long processInstanceKey;
		Long processDefinitionKey;
		String type;
		String message;
		String creationTime;
		OperateProcessFlowState state;
	}
}
