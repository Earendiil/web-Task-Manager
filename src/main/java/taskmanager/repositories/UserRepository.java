package taskmanager.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import taskmanager.entity.User;
@Repository
public interface UserRepository  extends JpaRepository<User, Long>{

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional <User> findByUsername(String username);

	 List<User> findByTasks_TaskId(Long taskId);

}
