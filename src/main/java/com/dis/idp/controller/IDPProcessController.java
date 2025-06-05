package com.dis.idp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@RestController
@RequestMapping("/api/process")
public class IDPProcessController {

	private ZeebeClient zeebeClient;

	
	@PostMapping("/start-process")
	public Map<String, Object> startLoanProcess() {
		ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
				.bpmnProcessId("Chech-Service-Invoice").latestVersion().send().join();

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Started process instance");
		response.put("processInstanceKey", processInstance.getProcessInstanceKey());

		return response;
	}

	@Autowired
	public IDPProcessController(ZeebeClient zeebeClient, CamundaOperateService operateService) {
		this.zeebeClient = zeebeClient;
		this.operateService = operateService;
	}

	private final CamundaOperateService operateService;

	@PostMapping("/active-process")
	public ResponseEntity<String> getActiveProcessInstances(
			@RequestBody(required = false) Map<String, Object> variables) {
		String activeInstances = operateService.getActiveInstances(variables);
		return ResponseEntity.ok(activeInstances);
	}

	@PostMapping("/task/{taskId}/complete")
	public ResponseEntity<String> completeTask(@PathVariable String taskId,
			@RequestBody(required = false) Map<String, Object> variables) {
		String result = operateService.completeTask(taskId, variables != null ? variables : Map.of());
		return ResponseEntity.ok(result);
	}
}
