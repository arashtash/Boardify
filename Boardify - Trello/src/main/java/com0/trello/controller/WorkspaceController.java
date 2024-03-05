package com0.trello.controller;

import com0.trello.model.Workspace;
import com0.trello.service.UserService;
import com0.trello.service.WorkspaceService;
import com0.trello.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    public String createWorkspace(@RequestBody Workspace workspace) {
        return workspaceService.createWorkspace( workspace );
    }

    @PostMapping("/addMemberToWorkSpace")
    public String addMemberToWorkSpace(@RequestParam Long workspaceId, @RequestParam String email){
        User user = userService.findUserByEmail(email);
        if (user == null) {
        throw new NullPointerException("User with email " + email + " not found.");
        }

        // workspaceService.addMemberToWorkSpace( userService.findUserByEmail( email ), workspaceId );
        workspaceService.addMemberToWorkSpace(user, workspaceId);
        return "Member Added to the Workspace";
    }


    @GetMapping("/getAll")
    public List<Workspace> getAllWorkspaces() {
        return workspaceService.getAllWorkspacesWithBoards();
    }

    @GetMapping("/{workspaceId}/users")
    public List<User> getWorkspaceUsers(@PathVariable Long workspaceId) {
        return workspaceService.getWorkspaceUsers(workspaceId);
    }
}
