package taskmanager.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import taskmanager.dto.TaskDTO;
import taskmanager.entity.Task;
import taskmanager.entity.User;
import taskmanager.service.TaskService;
import taskmanager.service.UserService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	
	private final TaskService taskService;
	private final UserService userService;
	@Autowired
	ModelMapper modelMapper;
	
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
		this.userService = userService;
    }
    
    
    @PostMapping("")
	public ResponseEntity<TaskDTO> addTask(@Valid @RequestBody TaskDTO taskDTO){
		TaskDTO newTaskDTO = taskService.createTask(taskDTO);
		return new ResponseEntity<TaskDTO>(taskDTO, HttpStatus.CREATED);
	}
    
	@PutMapping("/{taskId}")
	public ResponseEntity<Task> updateTask(@Valid @PathVariable Long taskId, @RequestBody Task task
											){
		
		Task updatedTask = taskService.updateTask(taskId, task);
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
	@GetMapping("")
	public ResponseEntity<List<TaskDTO>> getAllTasks(
	        @RequestParam(required = false) Long userId,
	        @RequestParam(required = false) Boolean completed,
	        @RequestParam(required = false) Long categoryId,
	        @RequestParam(required = false) String sortBy,
	        @RequestParam(required = false) String sortDirection
	) {
	    List<TaskDTO> tasks = taskService.findAllTasks(userId, completed, categoryId, sortBy, sortDirection);

	    return ResponseEntity.ok(tasks);
	}
	
	@DeleteMapping("/{taskId}")
	public ResponseEntity<String> deleteTask(@PathVariable Long taskId){
		taskService.deleteTaskById(taskId);
		return ResponseEntity.ok("Task deleted!");
		
	}
	
	@GetMapping("/{taskId}/users")
	public ResponseEntity<List<User>> getTaskUsers(@PathVariable Long taskId){
		List<User> usersList = taskService.getUsersAssignedToTask(taskId);
		return new ResponseEntity<List<User>>(usersList, HttpStatus.OK);
		
	}
	
	@PostMapping("/{taskId}/{userId}")
	public ResponseEntity<String> assignTask(@PathVariable Long taskId, @PathVariable Long userId){
		 taskService.assignToUser(taskId, userId);
		return new ResponseEntity<String>("Task assigned!" , HttpStatus.OK);
		
	}
	@DeleteMapping("/{taskId}/{userId}")
	public ResponseEntity<String> removeTask (@PathVariable Long taskId, @PathVariable Long userId){
		taskService.unassignTask(taskId, userId);
		return new ResponseEntity<String>( "User removed", HttpStatus.OK);
	}
	
	@GetMapping("/idle")
	public ResponseEntity<List<Task>> getUnassignedTasks(){
		List<Task> tasks = taskService.findIdleTasks();
		return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	
}
