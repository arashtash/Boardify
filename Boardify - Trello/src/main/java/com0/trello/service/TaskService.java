package com0.trello.service;

import com0.trello.model.Board;
import com0.trello.model.Task;
import com0.trello.model.TaskStatus;
import com0.trello.model.User;
import com0.trello.model.Workspace;
import com0.trello.repository.BoardRepository;
import com0.trello.repository.TaskRepository;
import com0.trello.repository.UserRepository;
import com0.trello.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;

    @Autowired
    WorkspaceService workspaceService;

     @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, BoardRepository boardRepository) {
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
    }

    public Task createTask(String name, Integer boardId, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid Due Date");
        }

        if (name == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Empty name");
        }
        Board board = boardRepository.findById( boardId ).orElse(null);
        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with ID: " + boardId);
        }
        Task task = new Task(null, name, date, null, board, TaskStatus.TODO);
        task.setBoard( board );
        return taskRepository.save( task );
    }


    public Task changeTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findTaskById( taskId );
        if (task == null) {
            throw new IllegalArgumentException( "Task with ID " + taskId + " not found." );
        }

        // Perform conditional checks for valid status transitions
        TaskStatus currentStatus = task.getStatus();

        switch (currentStatus) {
            case TODO -> {
                if (newStatus != TaskStatus.DOING) {
                    throw new IllegalArgumentException( "Invalid status transition from " + currentStatus + " to " + newStatus );
                }
            }
            case DOING -> {
                if (newStatus != TaskStatus.DONE) {
                    throw new IllegalArgumentException( "Invalid status transition from " + currentStatus + " to " + newStatus );
                }
            }
            case DONE -> throw new IllegalArgumentException( "Task is already in the DONE status." );
            default -> throw new IllegalArgumentException( "Unknown task status: " + currentStatus );
        }

        task.setStatus( newStatus );
        return taskRepository.save( task );
    }

    public void updateTaskDate(LocalDate date, Long id) {
        Task task = taskRepository.findTaskById( id );
        if (task == null) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Task with ID " + id + " not found." );
        }

        task.setDueDate( date );
        taskRepository.save( task );
    }

    public String addMemberToUsersfromWorkspace(Long taskId, String email) {

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return "task not found";
        }

        long workspaceID = task.getBoard().getWorkspaceID();
        Workspace workspace = workspaceRepository.findById(workspaceID).orElse(null);
        if (workspace == null) {
            return "workspace not found";
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "user not found";
        }

        // Check if the user is a member of the workspace
        if (workspace.getUsers().contains(user)) {
            List<User> taskUsers = task.getUsers();
            if (!taskUsers.contains(user)) {
                taskUsers.add(user);
                taskRepository.save(task);
            }
            return "user added to task";
        } else {
            return "user not found in workspace";
        }
    }
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }


}