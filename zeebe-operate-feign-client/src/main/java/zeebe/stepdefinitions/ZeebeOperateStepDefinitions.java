package zeebe.stepdefinitions;

import io.qameta.allure.Description;
import zeebe.manager.OperateManager;
import zeebe.manager.ZeebeManager;

import java.util.Map;

public class ZeebeOperateStepDefinitions {

	/**
	 * You can't delete active processes, so you have to cancel it before
	 * I don't recommend trying to delete processes after that because operate has troubles with it
	 */
	@Description("Cancel all active processes in Zeebe")
	public void cancelAllProcesses() {
		Map<String, Long> processeMapByState = OperateManager.getActiveProcessInstances();
		while (!processeMapByState.isEmpty()) {
			ZeebeManager.cancelAllActiveProcessesByKeyList(processeMapByState);
			processeMapByState = OperateManager.getActiveProcessInstances();
		}
	}

}
