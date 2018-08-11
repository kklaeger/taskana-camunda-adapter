package pro.taskana.camunda.mappings;

import java.time.Instant;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper for the Timestamps of the last creation Taskana tasks or completion of Camunda tasks.
 *
 * @author kkl
 */
@Mapper
public interface TimestampMapper {

    @Select("<script>SELECT MAX(CREATED) FROM TIMESTAMPS_TASKS_CREATED </script>")
    Instant getLatestTimestampOfCreated();

    @Insert("INSERT INTO TIMESTAMPS_TASKS_CREATED (ID, CREATED) VALUES (#{id}, #{created})")
    void insertCreated(@Param("id") String id, @Param("created") Instant created);

    @Delete("<script>DELETE TIMESTAMPS_TASKS_CREATED</script>")
    void clearCreateTable();

    @Select("<script>SELECT MAX(COMPLETED) FROM TIMESTAMPS_TASKS_COMPLETED </script>")
    Instant getLatestTimestampOfCompleted();

    @Insert("INSERT INTO TIMESTAMPS_TASKS_COMPLETED (ID, COMPLETED) VALUES (#{id}, #{completed})")
    void insertCompleted(@Param("id") String id, @Param("completed") Instant completed);

    @Delete("<script>DELETE TIMESTAMPS_TASKS_COMPLETED</script>")
    void clearCompletedTable();

}
