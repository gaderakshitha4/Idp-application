package com.dis.idp;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import io.camunda.zeebe.process.test.filters.RecordStream;

@ZeebeProcessTest
public class IdpApplicationTests  {

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ZeebeTestEngine engine;

    @Autowired
    private RecordStream recordStream;

    @Test
    void shouldDeployAndStartIDPProcess() throws Exception {
        try {
            // Deploy the BPMN process for IDP
            DeploymentEvent deployment = zeebeClient.newDeployResourceCommand()
                    .addResourceFromClasspath("IDP-Application.bpmn")
                    .send()
                    .join();

            assertThat(deployment.getProcesses()).isNotEmpty();
            assertThat(deployment.getProcesses().get(0).getBpmnProcessId()).isEqualTo("idp-invoice");

            System.out.println("IDP Process deployed: " + deployment.getProcesses().get(0).getBpmnProcessId());

            // Start the process instance
            ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("idp-invoice")
                    .latestVersion()
                    .send()
                    .join();

            assertThat(processInstance).isNotNull();
            BpmnAssert.assertThat(processInstance).isStarted();

            System.out.println("Started IDP process instance with key: " + processInstance.getProcessInstanceKey());

            // Wait for idle engine state
            engine.waitForIdleState(Duration.ofSeconds(10));
            System.out.println("IDP process test completed successfully!");

        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
