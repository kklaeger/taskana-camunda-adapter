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

    @Select("<script>"
        + "SELECT MAX(CREATED) "
        + "FROM TIMESTAMPS_TASKS_CREATED "
        + "WHERE CAMUNDA_HOST = #{camundaHost}"
        + "</script>")
    Instant getLatestCreatedTimestamp(@Param("camundaHost") String camundaHost);

    @Insert("INSERT INTO TIMESTAMPS_TASKS_CREATED (ID, CREATED, CAMUNDA_HOST) VALUES (#{id}, #{created}, #{camundaHost})")
    void insertCreatedTimestamp(@Param("id") String id,
        @Param("created") Instant created,
        @Param("camundaHost") String camundaHost);

    @Delete("<script>"
        + "DELETE FROM TIMESTAMPS_TASKS_CREATED "
        + "WHERE CAMUNDA_HOST = #{camundaHost}"
        + "</script>")
    void removeLatestCreatedTimestamp(@Param("camundaHost") String camundaHost);

    @Select("<script>SELECT MAX(COMPLETED) FROM TIMESTAMPS_TASKS_COMPLETED </script>")
    Instant getLatestCompletedTimestamp();

    @Insert("INSERT INTO TIMESTAMPS_TASKS_COMPLETED (ID, COMPLETED) VALUES (#{id}, #{completed})")
    void insertCompletedTimestamp(@Param("id") String id,
        @Param("completed") Instant completed);

    @Delete("<script>DELETE TIMESTAMPS_TASKS_COMPLETED</script>")
    void clearCompletedTable();

}
