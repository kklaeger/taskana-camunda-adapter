package pro.taskana.camunda.converter;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ObjectReference;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketType;
import pro.taskana.camunda.model.CamundaTask;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.TaskImpl;

@Component
public class CamundaTaskConverter {

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkbasketService workbasketService;

    @Autowired
    private ClassificationService classificationService;

    private static final String DEFAULT_WORKBASKET = "DEFAULT_WORKBASKET";
    private static final String DEFAULT_CLASSIFICATION = "DEFAULT_CLASSIFICATION";
    private static final String DEFAULT_DOMAIN = "DOMAIN_A";
    private static final String DEFAULT_CLASSIFICATION_TYPE = "TASK";
    private static final String DEFAULT_COMPANY = "DEFAULT_COMPANY";
    private static final String DEFAULT_SYSTEM = "DEFAULT_SYSTEM";
    private static final String DEFAULT_SYSTEM_INSTANCE = "DEFAULT_SYSTEM_INSTANCE";
    private static final String DEFAULT_TYPE = "DEFAULT_TYPE";
    private static final String DEFAULT_VALUE = "DEFAULT_VALUE";

    public Task toTaskanaTask(CamundaTask camundaTask)
        throws DomainNotFoundException, InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, ClassificationAlreadyExistException, InvalidArgumentException {
        Workbasket workbasket = createWorkbasket(DEFAULT_WORKBASKET, DEFAULT_DOMAIN);
        Classification classification = createClassification(DEFAULT_CLASSIFICATION, DEFAULT_DOMAIN,
            DEFAULT_CLASSIFICATION_TYPE);
        ObjectReference objectReference = createObjectReference(DEFAULT_COMPANY, DEFAULT_SYSTEM,
            DEFAULT_SYSTEM_INSTANCE, DEFAULT_TYPE, DEFAULT_VALUE);
        TaskImpl taskanaTask = (TaskImpl) taskService.newTask(workbasket.getId());
        HashMap<String, String> callbackInfo = new HashMap<>();
        callbackInfo.put("camunda_task_id", camundaTask.getId());
        taskanaTask.setCallbackInfo(callbackInfo);
        taskanaTask.setName(camundaTask.getName());
        taskanaTask.setOwner(camundaTask.getAssigne());
        taskanaTask.setClassificationKey(classification.getKey());
        taskanaTask.setPrimaryObjRef(objectReference);
        return taskanaTask;
    }

    private Workbasket createWorkbasket(String key, String domain)
        throws InvalidWorkbasketException, NotAuthorizedException,
        DomainNotFoundException, WorkbasketAlreadyExistException {
        Workbasket wb;
        try {
            wb = workbasketService.getWorkbasket(key, domain);
        } catch (WorkbasketNotFoundException e) {
            wb = workbasketService.newWorkbasket(key, domain);
            wb.setName(key);
            wb.setType(WorkbasketType.GROUP);
            wb = workbasketService.createWorkbasket(wb);
        }
        return wb;
    }

    private Classification createClassification(String key, String domain, String type)
        throws ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {

        Classification classification;
        try {
            classification = classificationService.getClassification(key, domain);
        } catch (ClassificationNotFoundException e) {
            classification = classificationService.newClassification(key, domain, type);
            classification = classificationService.createClassification(classification);
        }
        return classification;
    }

    private ObjectReference createObjectReference(String company, String system, String systemInstance,
        String type, String value) {
        ObjectReference objRef = new ObjectReference();
        objRef.setCompany(company);
        objRef.setSystem(system);
        objRef.setSystemInstance(systemInstance);
        objRef.setType(type);
        objRef.setValue(value);
        return objRef;
    }

}
