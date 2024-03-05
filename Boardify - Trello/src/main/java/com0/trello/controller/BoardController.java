package com0.trello.controller;

import com0.trello.model.Board;
import com0.trello.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    BoardService boardService;

    @PostMapping("/create")
    public String createBoard(@RequestParam int workspaceId, @RequestParam String boardName
    ){

        return boardService.createBoard(boardName, workspaceId);
    }

    @DeleteMapping("/delete")
    public boolean deleteBoardById(@RequestParam Integer id) {
        try {
            boardService.deleteBoardById(id);
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @PutMapping("/update")
    public String updateBoard(@RequestBody Board board) {
        return boardService.updateBoard(board);
    }

}