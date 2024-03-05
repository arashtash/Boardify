package com0.trello.service;

import com0.trello.model.Board;
import com0.trello.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired
    BoardRepository boardRepository;

    public String createBoard( String title, int workspaceId) {

        Board board = new Board();
        board.setTitle( title );
        board.setWorkspaceID( workspaceId );
        boardRepository.save( board );
        return "Board data created";
    }

    public Board findBoardById(int id) {
        Board board = boardRepository.findById( id ).orElse( null );
        System.out.println( "Successfully fetched board information " + board );
        return board;
    }


    public String updateBoard(Board board) {
        Board board1 = boardRepository.findById( board.getId() ).orElse( null );
        if (board1 == null) {
            return "No board Assigned with this ID";
        }
        board1.setTitle( board.getTitle() );
        board1.setWorkspaceID( board.getWorkspaceID() );
        boardRepository.save( board );

        return "Board Updated Successfully";
    }

    public String deleteBoardById(int id) {
        boardRepository.deleteById( id );
        return "Board deleted Successfully";
    }
}