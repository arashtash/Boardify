package com0.trello.service;

import com0.trello.model.Board;
import com0.trello.model.User;
import com0.trello.model.Workspace;
import com0.trello.repository.BoardRepository;
import com0.trello.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;

import java.util.List;

@Service
public class WorkspaceService {

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Autowired
    BoardRepository boardRepository;

    public String createWorkspace(Workspace workspace) {
        workspaceRepository.save( workspace );
        return "Workspace data created";
    }

    public void addMemberToWorkSpace(User user, Long workSpace_Id) {
        Workspace workSpace = workspaceRepository.findById( workSpace_Id ).orElse( null );

        assert workSpace != null;
        List<User> allMembers = workSpace.getUsers();
        allMembers.add( user );
        workSpace.setUsers( allMembers );

        workspaceRepository.save( workSpace );

    }

    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    public List<User> getWorkspaceUsers(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        if (workspace != null) {
            return workspace.getUsers();
        }

        return Collections.emptyList();
    }

    public List<Workspace> getAllWorkspacesWithBoards() {
        List<Workspace> workspaces = workspaceRepository.findAll();
        for (Workspace workspace : workspaces) {
            List<Board> boards = boardRepository.findByWorkspaceID(workspace.getId());
            workspace.setBoards(boards);
        }
        return workspaces;
    }

}
