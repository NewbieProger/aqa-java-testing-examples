package zeebe.feign.client;

import feign.Headers;
import feign.RequestLine;
import io.restassured.response.Response;
import zeebe.feign.dto.OperateRequestDto;

@Headers("Content-Type: application/json")
public interface OperateFeignClient {

	@RequestLine("POST /api/login?username=demo&password=demo")
	Response getAuthCookie();

	@RequestLine("POST /v1/process-instances/search")
	Response getProcessInstancesSearch(OperateRequestDto operateRequestDto);

	@RequestLine("POST /v1/process-definitions/search")
	Response getProcessDefinitionsSearch(OperateRequestDto operateRequestDto);

	@RequestLine("POST /v1/variables/search")
	Response getVariablesSearch(OperateRequestDto operateRequestDto);

	@RequestLine("POST /v1/incidents/search")
	Response getIncidentsSearch(OperateRequestDto operateRequestDto);

	@RequestLine("POST /v1/flownode-instances/search")
	Response getFlowNodeInstancesSearch(OperateRequestDto operateRequestDto);

}
