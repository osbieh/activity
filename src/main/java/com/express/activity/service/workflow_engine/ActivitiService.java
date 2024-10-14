package com.express.activity.service.workflow_engine;

import com.express.activity.service.dto.DeployedProcessDTO;
import lombok.RequiredArgsConstructor;

import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivitiService {

    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;

    public DeployedProcessDTO deployProcess(String fileName) {
        Deployment deployment = repositoryService.createDeployment()
            .addClasspathResource("processes/" + fileName)
            .name(fileName)
            .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(deployment.getId())
            .singleResult();

        return DeployedProcessDTO.builder()
            .id(processDefinition.getId())
            .name(processDefinition.getName())
            .key(processDefinition.getKey())
            .version(processDefinition.getVersion())
            .category(processDefinition.getCategory())
            .deploymentId(processDefinition.getDeploymentId())
            .resourceName(processDefinition.getResourceName())
            .build();
    }

    public byte[] generateProcessDiagram(String processInstanceId) throws IOException {
        List<HistoricActivityInstance> historicActivities = historyService
            .createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId)
            .orderBy((QueryProperty) () -> "id_")
            .asc()
            .list();

        boolean isProcessInstanceCompleted = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).finished().count() == 1;
        String processDefinitionId = null;
        List<String> activeActivityIds = new ArrayList<>();
        if(isProcessInstanceCompleted) {
            processDefinitionId = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getProcessDefinitionId();
        }
        else {
            processDefinitionId = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getProcessDefinitionId();

            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        }

        // Create a BPMN model for the process definition
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        List<String> highLightedActivities = historicActivities.stream().map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());
        List<String> previousFlows = new ArrayList<>(), highLightedFlows = new ArrayList<>();;
        boolean previousExclusiveGateway = false;

        for(HistoricActivityInstance historicActivity: historicActivities) {
            FlowElement flowElement = bpmnModel.getFlowElement(historicActivity.getActivityId());
            FlowNode flowNode = ((FlowNode) flowElement);

            if(previousExclusiveGateway) {
                List<String> incomingFlows = flowNode.getIncomingFlows().stream().map(BaseElement::getId).collect(Collectors.toList());
                previousFlows.retainAll(incomingFlows);
                highLightedFlows.addAll(previousFlows);
                previousFlows.clear();
                previousExclusiveGateway = false;
            }

            // if exclusive gateway, don't add its outgoing sequences until next loop, to check which path has selected
            if (flowElement instanceof ExclusiveGateway) {
                previousExclusiveGateway = true;
                List<String> outgoingFlows = flowNode.getOutgoingFlows().stream().map(BaseElement::getId).collect(Collectors.toList());
                previousFlows.addAll(outgoingFlows);
            }
            else {
                if(!activeActivityIds.contains(historicActivity.getActivityId())) // if current task, we shouldn't add its outgoing sequence
                    highLightedFlows.addAll(flowNode.getOutgoingFlows().stream().map(BaseElement::getId).collect(Collectors.toList()));
            }
        }

        // Generate the process diagram with historical data
        InputStream diagramStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, highLightedActivities, highLightedFlows);

        return diagramStream.readAllBytes();
    }

    public void deleteProcessInstance(String processInstanceId) {
        // Check if the process instance exists in the runtime
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
        if (processInstance != null) {
            // If the process instance exists in runtime, delete it
            runtimeService.deleteProcessInstance(processInstanceId, "");
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }
        else {
            // Check if the process instance exists in history
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
            if (historicProcessInstance != null) {
                // If the process instance exists in history, delete it
                historyService.deleteHistoricProcessInstance(processInstanceId);
            }
            else {
                throw new RuntimeException("No process instance found for id: " + processInstanceId);
            }
        }
   }
}
