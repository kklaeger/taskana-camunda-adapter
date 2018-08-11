package pro.taskana.camunda.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class CamundaTaskConverterTest {

    @InjectMocks
    private CamundaTaskConverter cut;

    @Before
    public void setup() throws WorkbasketNotFoundException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertCamundaTaskToTaskanaTask()
        throws DomainNotFoundException, InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, ClassificationAlreadyExistException, InvalidArgumentException {
        // CamundaTask camundaTask = new CamundaTask();
        // camundaTask.setName("taskName");
        //
        // Task taskanaTask = cut.toTaskanaTask(camundaTask);
        //
        // assertNotNull(taskanaTask);
        // assertThat(camundaTask.getName(), equalTo(taskanaTask.getName()));
    }

}
