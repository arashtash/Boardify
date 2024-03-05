package com0.trello.controller;
import java.util.Map;
import java.util.List;

import com0.trello.model.Board;
import com0.trello.model.Task;
import com0.trello.model.TaskStatus;
import com0.trello.model.User;
import com0.trello.repository.BoardRepository;
import com0.trello.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    BoardRepository boardRepository;


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/createTask")
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> requestBody) {
        String name = (String) requestBody.get("name");
        Integer boardId = (Integer) requestBody.get("boardId");
        LocalDate date = LocalDate.parse((String) requestBody.get("date"));

        Task createdTask = taskService.createTask(name, boardId, date);
        return ResponseEntity.ok(createdTask);
    }

    @PostMapping("/updateDate")
    public ResponseEntity<String> updateDueDate(@RequestParam LocalDate date, @RequestParam Long id) {
        taskService.updateTaskDate( date, id );
        return ResponseEntity.ok( "updateDueDate" );
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<Task> changeTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus newStatus
    ) {
        Task updatedTask = taskService.changeTaskStatus( taskId, newStatus );
        return ResponseEntity.ok( updatedTask );
    }

    @PostMapping("/{taskId}/add-member")
    public ResponseEntity<String> addMemberToTask( @PathVariable Long taskId, @RequestParam String email) {
        String result = taskService.addMemberToUsersfromWorkspace(taskId, email);
        if ("user added to task".equals(result)) {
            return ResponseEntity.ok(result);
        } else if ("user not found in workspace".equals(result)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found in the workspace.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding user to task.");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

}