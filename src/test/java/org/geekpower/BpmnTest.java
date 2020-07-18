package org.geekpower;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BpmnTest {

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void testSerializable() {
        Deployment deployment = repositoryService.createDeployment()
                .name("applyPrcesss.bpmn")
                .addClasspathResource("bpmn/applyPrcesss.bpmn")
                .enableDuplicateFiltering()//
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();

        System.out.println(processDefinition);
    }

}
