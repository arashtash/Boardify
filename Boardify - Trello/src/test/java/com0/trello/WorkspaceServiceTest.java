package com0.trello;

import com0.trello.model.Board;
import com0.trello.model.User;
import com0.trello.model.Workspace;
import com0.trello.repository.BoardRepository;
import com0.trello.repository.WorkspaceRepository;
import com0.trello.service.WorkspaceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WorkspaceServiceTest {

    @InjectMocks
    WorkspaceService workspaceService;

    @Mock
    WorkspaceRepository workspaceRepository;

    @Mock
    BoardRepository boardRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWorkspace() {
        // Mocking data
        Workspace workspace = new Workspace();
        workspace.setId(1);
        workspace.setTitle("Test Workspace");
        workspace.setCreated(LocalDateTime.now());
        workspace.setLastUpdated(LocalDateTime.now());
        workspace.setUserID(1);
        workspace.setUsers(new ArrayList<>());

        // Mocking workspaceRepository.save()
        when(workspaceRepository.save(workspace)).thenReturn(workspace);

        // Call the createWorkspace method
        String result = workspaceService.createWorkspace(workspace);

        // Verify that workspaceRepository.save() is called once and the returned message is correct
        verify(workspaceRepository, times(1)).save(workspace);
        Assertions.assertEquals("Workspace data created", result);
    }

    @Test
    public void testAddMemberToWorkspace() {
        // Mocking data
        int workspaceId = 1;
        Workspace workspace = new Workspace();
        workspace.setId(workspaceId);
        workspace.setTitle("Test Workspace");
        workspace.setCreated(LocalDateTime.now());
        workspace.setLastUpdated(LocalDateTime.now());
        workspace.setUserID(1);
        List<User> allMembers = new ArrayList<>();
        workspace.setUsers(allMembers);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        // Mocking workspaceRepository.findById()
        when(workspaceRepository.findById((long)workspaceId)).thenReturn(Optional.of(workspace));
        // Mocking workspaceRepository.save()
        when(workspaceRepository.save(workspace)).thenReturn(workspace);

        // Call the addMemberToWorkSpace method
        workspaceService.addMemberToWorkSpace(user, (long) workspaceId);

        // Verify that workspaceRepository.findById() and workspaceRepository.save() are called once
        verify(workspaceRepository, times(1)).findById((long)workspaceId);
        verify(workspaceRepository, times(1)).save(workspace);
        // Verify that the user is added to the workspace
        Assertions.assertTrue(workspace.getUsers().contains(user));
    }

    @Test
    public void testGetWorkspaceUsers() {
        // Mocking data
        int workspaceId = 1;
        Workspace workspace = new Workspace();
        workspace.setId(workspaceId);
        workspace.setTitle("Test Workspace");
        workspace.setCreated(LocalDateTime.now());
        workspace.setLastUpdated(LocalDateTime.now());
        workspace.setUserID(1);
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        users.add(user1);
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        users.add(user2);
        workspace.setUsers(users);

        // Mocking workspaceRepository.findById()
        when(workspaceRepository.findById((long)workspaceId)).thenReturn(Optional.of(workspace));

        // Call the getWorkspaceUsers method
        List<User> foundUsers = workspaceService.getWorkspaceUsers((long) workspaceId);

        // Verify that workspaceRepository.findById() is called once and the returned users are correct
        verify(workspaceRepository, times(1)).findById((long)workspaceId);
        Assertions.assertEquals(users, foundUsers);
    }

    @Test
    public void testGetWorkspaceUsers_WorkspaceNotFound() {
        // Mocking data
        int workspaceId = 1;

        // Mocking workspaceRepository.findById()
        when(workspaceRepository.findById((long)workspaceId)).thenReturn(Optional.empty());

        // Call the getWorkspaceUsers method
        List<User> foundUsers = workspaceService.getWorkspaceUsers((long) workspaceId);

        // Verify that workspaceRepository.findById() is called once and the returned users are empty
        verify(workspaceRepository, times(1)).findById((long)workspaceId);
        Assertions.assertTrue(foundUsers.isEmpty());
    }



    @Test
    public void testGetAllWorkspacesWithBoards() {
        // Mocking data
        Workspace workspace1 = new Workspace();
        workspace1.setId(1);
        workspace1.setTitle("Workspace 1");
        workspace1.setCreated(LocalDateTime.now());
        workspace1.setLastUpdated(LocalDateTime.now());
        workspace1.setUserID(1);

        Workspace workspace2 = new Workspace();
        workspace2.setId(2);
        workspace2.setTitle("Workspace 2");
        workspace2.setCreated(LocalDateTime.now());
        workspace2.setLastUpdated(LocalDateTime.now());
        workspace2.setUserID(2);

        List<Workspace> workspaces = new ArrayList<>();
        workspaces.add(workspace1);
        workspaces.add(workspace2);

        Board board1 = new Board();
        board1.setId(1);
        board1.setTitle("Board 1");
        board1.setWorkspaceID(workspace1.getId());
        Board board2 = new Board();
        board2.setId(2);
        board2.setTitle("Board 2");
        board2.setWorkspaceID(workspace1.getId());

        // Mocking workspaceRepository.findAll()
        when(workspaceRepository.findAll()).thenReturn(workspaces);

        // Mocking boardRepository.findByWorkspaceID()
        when(boardRepository.findByWorkspaceID(workspace1.getId())).thenReturn(List.of(board1, board2));
        when(boardRepository.findByWorkspaceID(workspace2.getId())).thenReturn(List.of());

        // Call the getAllWorkspacesWithBoards method
        List<Workspace> workspacesWithBoards = workspaceService.getAllWorkspacesWithBoards();

        // Verify that workspaceRepository.findAll() is called once and boardRepository.findByWorkspaceID() is called twice
        verify(workspaceRepository, times(1)).findAll();
        verify(boardRepository, times(2)).findByWorkspaceID( Mockito.anyInt());

        // Verify that the boards are added to the workspaces correctly
        Workspace foundWorkspace1 = workspacesWithBoards.get(0);
        Workspace foundWorkspace2 = workspacesWithBoards.get(1);
        Assertions.assertEquals(2, foundWorkspace1.getBoards().size());
        Assertions.assertEquals(0, foundWorkspace2.getBoards().size());
    }
}
