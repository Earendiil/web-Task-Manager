package taskmanager.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import taskmanager.entity.Task;
import taskmanager.entity.User;
import taskmanager.exceptions.ResourceNotFoundException;
import taskmanager.repositories.CategoryRepository;
import taskmanager.repositories.TaskRepository;
import taskmanager.repositories.UserRepository;

@Service
public class TaskServiceImpl implements TaskService{
	
	private final CategoryRepository categoryRepository;
	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	User user;
	
	public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
		super();
		this.categoryRepository = categoryRepository;
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Task createTask(Task task) {
	   
	    if (task.getCategory() == null || task.getCategory().getCategoryId() == null) {
	        throw new IllegalArgumentException("Category id is required");
	    }

	    categoryRepository.findById(task.getCategory().getCategoryId())
	        .orElseThrow(() -> new IllegalArgumentException("Category ID not found: " + task.getCategory().getCategoryId()));

		
	    return taskRepository.save(task);
	}


	@Override
	public Task updateTask(Long taskId, Task task) {

		if (task.getCategory() == null || task.getCategory().getCategoryId() == null) {
	        throw new IllegalArgumentException("Category id is required");
	    }
		if (task.getDueDate() != null && task.getDueDate().before(new Date())) {
			throw new IllegalArgumentException("Due date must ne in the future!");
		}
			
	    categoryRepository.findById(task.getCategory().getCategoryId())
	        .orElseThrow(() -> new IllegalArgumentException("Category id not found: " + task.getCategory().getCategoryId()));
	 
	    
	    Task updatedTask = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", "task id", taskId));
		updatedTask.setDescription(task.getDescription());
		updatedTask.setDueDate(task.getDueDate());
		updatedTask.setTitle(task.getTitle());
	    updatedTask.setCategoryId(task.getCategoryId());
		
		taskRepository.save(updatedTask);
	
	return updatedTask;
	}


	@Override
	public List<Task> findAllTasks(Long userId, Boolean completed, Long categoryId, String sortBy, String sortDirection) {
		List<Task> tasks = taskRepository.findAll();
		if (tasks.isEmpty()) {
			throw new RuntimeException("No tasks exist!");
		}
		
		    if (completed != null) {
		        tasks = tasks.stream()
		                     .filter(task -> task.isCompleted() == completed)
		                     .collect(Collectors.toList());
		    }
		    if (categoryId != null) {
		        tasks = tasks.stream()
		                     .filter(task -> task.getCategory() != null && task.getCategory().getCategoryId().equals(categoryId))
		                     .collect(Collectors.toList());
		    }

		    // Apply sorting
		    if (sortBy != null && sortDirection != null) {
		        if ("asc".equalsIgnoreCase(sortDirection)) {
		            tasks = tasks.stream()
		                         .sorted(Comparator.comparing(Task::getDueDate))  // Change to the appropriate field
		                         .collect(Collectors.toList());
		        } else if ("desc".equalsIgnoreCase(sortDirection)) {
		            tasks = tasks.stream()
		                         .sorted(Comparator.comparing(Task::getDueDate).reversed())  // Change to the appropriate field
		                         .collect(Collectors.toList());
		        }
		    }
		return tasks;
	}


	@Override
	public void deleteTaskById(Long taskId) {
		Task updatedTask = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", "task id", taskId));
		taskRepository.delete(updatedTask);
	}

//
//	@Override
//	public List<Task> findIdleTasks() {
//	
//		List<Task> idleTasks = new ArrayList<>();
//		List<Task> tasks = taskRepository.findAll();
//		if (tasks.isEmpty()) {
//			throw new RuntimeException("No tasks exist!");
//		}
//		for (Task task : tasks) {
//			if (task.getUser() == null) {
//				idleTasks.add(task);
//			}
//		}
//		return idleTasks;
//	}

	public List<Task> filterTasks(Boolean completed, Date dueDate, Long categoryId, Long userId) {
        if (completed != null) {
            return taskRepository.findByCompleted(completed);
        } else if (dueDate != null) {
            return taskRepository.findByDueDateBefore(dueDate);
        } else if (categoryId != null) {
            return taskRepository.findByCategory_CategoryId(categoryId);
        } else if (userId != null) {
            return taskRepository.findByUser_UserId(userId);
        }
        return taskRepository.findAll(); // Return all tasks if no filter is applied
    }
	

	@Override
	public List<User> getUsersAssignedToTask(Long taskId) {
		List<User> users = taskRepository.findUsersByTaskId(taskId);
		System.out.println("Users found: " + users);

		if (users == null || users.isEmpty()) 
		    throw new ResourceNotFoundException("No users assigned");
		return 	users;
	}

	@Override
	public void assignToUser(Long taskId, Long userId) {
		Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
		if (task.getAssignedUsers() == null) {
	        task.setAssignedUsers(new ArrayList<>());
	    }
		if (!task.getAssignedUsers().contains(user)) {
			task.getAssignedUsers().add(user);
		}
		if (user.getTasks().isEmpty()) {
			user.setTasks(new ArrayList<>());
		}
		if (!user.getTasks().contains(task)) {
			user.getTasks().add(task);
		}
		userRepository.save(user);
		taskRepository.save(task);
		System.out.println("Added");
	}

	@Override
	public List<Task> findIdleTasks() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Transactional
	@Override
	public void unassignTask(Long taskId, Long userId) {
		Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
		
		task.getAssignedUsers().remove(user);
		user.getTasks().remove(task);
		
		taskRepository.save(task);
	    userRepository.save(user);
		
	}
}
