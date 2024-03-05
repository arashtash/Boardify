package com0.trello;

import com0.trello.model.*;
import com0.trello.repository.BoardRepository;
import com0.trello.repository.TaskRepository;
import com0.trello.repository.UserRepository;
import com0.trello.repository.WorkspaceRepository;
import com0.trello.service.TaskService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    TaskService taskService;

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    WorkspaceRepository workspaceRepository;

    @Mock
    BoardRepository boardRepository;


    @BeforeEach
    public void setUp() {
        taskService = new TaskService( taskRepository, boardRepository );
    }

    @Test
    public void testCreateValidTask() {
        // Test data
        String taskName = "Valid Test Task";
        Integer boardId = 2;
        LocalDate dueDate = LocalDate.now().plusDays( 5 );

        // Create a mock board object
        Board board = new Board();
        board.setId( boardId );

        // Mock the behavior of the boardRepository to return the board when findById is called
        when( boardRepository.findById( boardId ) ).thenReturn( Optional.of( board ) );

        // Create a new task to be saved
        Task savedTask = new Task();
        savedTask.setId( 2L );
        savedTask.setName( taskName );
        savedTask.setDueDate( dueDate );
        savedTask.setBoard( board );

        // Mock the behavior of the taskRepository to return the saved task when save is called
        when( taskRepository.save( any( Task.class ) ) ).thenReturn( savedTask );

        // Perform the task creation
        Task result = taskService.createTask( taskName, boardId, dueDate );

        // Verify the task creation result
        verify( boardRepository, times( 1 ) ).findById( boardId );
        verify( taskRepository, times( 1 ) ).save( any( Task.class ) );
        Assertions.assertEquals( savedTask, result );
    }

    @Test
    public void testCreateTask_BoardIsNull() {
        // Test data
        String taskName = "Test Task";
        Integer boardId = 1;
        LocalDate dueDate = LocalDate.now().plusDays( 5 );

        // Mock the behavior of the boardRepository to return null when findById is called
        when( boardRepository.findById( boardId ) ).thenReturn( Optional.empty() );

        // Perform the task creation and catch the exception
        ResponseStatusException exception = Assertions.assertThrows( ResponseStatusException.class, () -> taskService.createTask( taskName, boardId, dueDate ) );

        // Verify the exception
        Assertions.assertEquals( HttpStatus.NOT_FOUND, exception.getStatusCode() );
        Assertions.assertEquals( "Board not found with ID: " + boardId, exception.getReason() );
        verify( boardRepository, times( 1 ) ).findById( boardId );
        verify( taskRepository, times( 0 ) ).save( any( Task.class ) ); // Ensure task is not saved since the board is null
    }

    @Test
    public void testCreateInvalidDueDate() {
        Task task = new Task();
        Board board = new Board();
        board.setId( 490 );
        task.setUsers( new ArrayList<>() );

        ResponseStatusException exception = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> {
                    taskService.createTask( "Test Task", 490, LocalDate.of( 2022, 01, 01 ) );
                }
        );

        Assertions.assertEquals( "Invalid Due Date", exception.getReason() );
    }


    @Test
    public void testCreateTaskWithMissingTitle() {
        Board board = new Board();
        board.setId( 490 );

        ResponseStatusException exception = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> {
                    taskService.createTask( null, 490, LocalDate.now() );
                }
        );
        Assertions.assertEquals( "Empty name", exception.getReason() );
    }

    @Test
    public void testTaskDateBeforeToday() {
        Task task = new Task();
        task.setName( "Test Name" );
        task.setDueDate( LocalDate.now().minusDays( 1 ) );
        task.setUsers( new ArrayList<>() );
    }

    @Test
    public void testChangeTaskStatusValidTransition() {
        // Arrange
        Task task = new Task();
        task.setId( 1L );
        task.setName( "Test Task" );
        task.setStatus( TaskStatus.TODO );

        TaskStatus newStatus = TaskStatus.DOING;

        when( taskRepository.findTaskById( 1L ) ).thenReturn( task );
        when( taskRepository.save( task ) ).thenReturn( task );

        Task updatedTask = taskService.changeTaskStatus( 1L, newStatus );

        Assertions.assertEquals( newStatus, updatedTask.getStatus() );
        verify( taskRepository ).save( task );
    }

    @Test
    public void testChangeTaskStatus_InvalidStatusTransitionFromTODO() {
        // Test data
        Long taskId = 1L;
        TaskStatus currentStatus = TaskStatus.TODO;
        TaskStatus newStatus = TaskStatus.DONE; // Invalid transition from TODO to DONE

        // Create a task with the current status
        Task task = new Task();
        task.setId( taskId );
        task.setStatus( currentStatus );

        // Mock the behavior of the taskRepository to return the task when findTaskById is called
        when( taskRepository.findTaskById( taskId ) ).thenReturn( task );

        // Perform the task status change and catch the exception
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> taskService.changeTaskStatus( taskId, newStatus ) );

        // Verify the exception
        Assertions.assertEquals( "Invalid status transition from " + currentStatus + " to " + newStatus, exception.getMessage() );
        verify( taskRepository, times( 1 ) ).findTaskById( taskId );
        verify( taskRepository, times( 0 ) ).save( any( Task.class ) ); // Ensure task is not saved since the status transition is invalid
    }

    @Test
    public void testChangeTaskStatus_TaskAlreadyInDoneStatus() {
        // Test data
        Long taskId = 1L;
        TaskStatus currentStatus = TaskStatus.DONE;
        TaskStatus newStatus = TaskStatus.DOING; // Trying to change status from DONE to DOING

        // Create a task with the current status
        Task task = new Task();
        task.setId( taskId );
        task.setStatus( currentStatus );

        // Mock the behavior of the taskRepository to return the task when findTaskById is called
        when( taskRepository.findTaskById( taskId ) ).thenReturn( task );

        // Perform the task status change and catch the exception
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> taskService.changeTaskStatus( taskId, newStatus ) );

        // Verify the exception
        Assertions.assertEquals( "Task is already in the DONE status.", exception.getMessage() );
        verify( taskRepository, times( 1 ) ).findTaskById( taskId );
        verify( taskRepository, times( 0 ) ).save( any( Task.class ) ); // Ensure task is not saved since the status transition is invalid
    }

    @Test
    public void testChangeTaskStatus_InvalidStatusTransitionFromDOING() {
        // Test data
        Long taskId = 1L;
        TaskStatus currentStatus = TaskStatus.DOING;
        TaskStatus newStatus = TaskStatus.TODO; // Invalid transition from DOING to TODO

        // Create a task with the current status
        Task task = new Task();
        task.setId( taskId );
        task.setStatus( currentStatus );

        // Mock the behavior of the taskRepository to return the task when findTaskById is called
        when( taskRepository.findTaskById( taskId ) ).thenReturn( task );

        // Perform the task status change and catch the exception
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> taskService.changeTaskStatus( taskId, newStatus ) );

        // Verify the exception
        Assertions.assertEquals( "Invalid status transition from " + currentStatus + " to " + newStatus, exception.getMessage() );
        verify( taskRepository, times( 1 ) ).findTaskById( taskId );
        verify( taskRepository, times( 0 ) ).save( any( Task.class ) ); // Ensure task is not saved since the status transition is invalid
    }


    @Test
    public void testChangeTaskStatusTaskNotFound() {

        Long nonExistentTaskId = 999L;
        TaskStatus newStatus = TaskStatus.DONE;

        when( taskRepository.findTaskById( nonExistentTaskId ) ).thenReturn( null );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskService.changeTaskStatus( nonExistentTaskId, newStatus )
        );
        verify( taskRepository, times( 0 ) ).save( Mockito.any() );
    }

    @Test
    public void testUpdateTaskDate() {

        Long taskId = 1L;
        LocalDate newDueDate = LocalDate.of( 2023, 12, 31 );

        Task task = new Task();
        task.setId( taskId );
        task.setName( "Test Task" );
        task.setDueDate( LocalDate.of( 2023, 8, 15 ) );

        when( taskRepository.findTaskById( taskId ) ).thenReturn( task );
        when( taskRepository.save( task ) ).thenReturn( task );


        taskService.updateTaskDate( newDueDate, taskId );


        Assertions.assertEquals( newDueDate, task.getDueDate() );
        verify( taskRepository ).save( task );
    }

    @Test
    public void testUpdateTaskDateTaskNotFound() {

        Long nonExistentTaskId = 999L;
        LocalDate newDueDate = LocalDate.of( 2023, 12, 31 );

        when( taskRepository.findTaskById( nonExistentTaskId ) ).thenReturn( null );


        ResponseStatusException exception = Assertions.assertThrows( ResponseStatusException.class, () -> taskService.updateTaskDate( newDueDate, nonExistentTaskId ) );

        Assertions.assertEquals( HttpStatus.NOT_FOUND, exception.getStatusCode() );
        Assertions.assertEquals( "Task with ID 999 not found.", exception.getReason() );
        verify( taskRepository, times( 0 ) ).save( Mockito.any() );
    }

    @Test
    public void testGetAllTasks() {
        // Test data
        Task task1 = new Task();
        task1.setId( 1L );
        task1.setName( "Task 1" );
        task1.setStatus( TaskStatus.TODO );

        Task task2 = new Task();
        task2.setId( 2L );
        task2.setName( "Task 2" );
        task2.setStatus( TaskStatus.DOING );

        List<Task> tasks = new ArrayList<>();
        tasks.add( task1 );
        tasks.add( task2 );

        // Mock the behavior of the taskRepository to return the list of tasks when findAll is called
        when( taskRepository.findAll() ).thenReturn( tasks );

        // Call the getAllTasks method
        List<Task> result = taskService.getAllTasks();

        // Verify the result
        Assertions.assertEquals( tasks.size(), result.size() );
        Assertions.assertEquals( tasks.get( 0 ).getId(), result.get( 0 ).getId() );
        Assertions.assertEquals( tasks.get( 0 ).getName(), result.get( 0 ).getName() );
        Assertions.assertEquals( tasks.get( 0 ).getStatus(), result.get( 0 ).getStatus() );
        Assertions.assertEquals( tasks.get( 1 ).getId(), result.get( 1 ).getId() );
        Assertions.assertEquals( tasks.get( 1 ).getName(), result.get( 1 ).getName() );
        Assertions.assertEquals( tasks.get( 1 ).getStatus(), result.get( 1 ).getStatus() );

        // Verify that taskRepository's findAll method was called exactly once
        verify( taskRepository, times( 1 ) ).findAll();
    }

    @Test
    public void testAddMemberToUsersfromWorkspace_TaskNotFound() {
        // Test data
        Long taskId = 1L;
        String email = "user@example.com";

        // Mock the behavior of the taskRepository to return null for non-existent task
        when( taskRepository.findById( taskId ) ).thenReturn( Optional.empty() );

        // Perform the addition of the user to the task
        String result = taskService.addMemberToUsersfromWorkspace( taskId, email );

        // Verify the result
        Assertions.assertEquals( "task not found", result );
        verify( userRepository, times( 0 ) ).findByEmail( email ); // Ensure userRepository method is not called
        verify( taskRepository, times( 1 ) ).findById( taskId );
        verify( taskRepository, times( 0 ) ).save( Mockito.any( Task.class ) ); // Ensure taskRepository save is not called
    }
}
