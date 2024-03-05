package com0.trello;

import com0.trello.model.*;
import com0.trello.repository.*;
import com0.trello.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class BoardServiceTest {

    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBoard() {
        // Mocking data
        String title = "Test Board";
        int workspaceId = 1;

        // Call the createBoard method
        String result = boardService.createBoard(title, workspaceId);

        // Verify that boardRepository.save() is called once and the returned message is correct
        verify(boardRepository, times(1)).save(any(Board.class));
        Assertions.assertEquals("Board data created", result);
    }

    @Test
    public void testFindBoardById() {
        // Mocking data
        int boardId = 1;
        Board board = new Board();
        board.setId(boardId);
        board.setTitle("Test Board");
        board.setWorkspaceID(1);

        // Mocking boardRepository.findById()
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // Call the findBoardById method
        Board foundBoard = boardService.findBoardById(boardId);

        // Verify that boardRepository.findById() is called once and the returned board is correct
        verify(boardRepository, times(1)).findById(boardId);
        Assertions.assertEquals(board, foundBoard);
    }

    @Test
    public void testFindBoardById_BoardNotFound() {
        // Mocking data
        int boardId = 1;

        // Mocking boardRepository.findById()
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // Call the findBoardById method
        Board foundBoard = boardService.findBoardById(boardId);

        // Verify that boardRepository.findById() is called once and the returned board is null
        verify(boardRepository, times(1)).findById(boardId);
        Assertions.assertNull(foundBoard);
    }

//    @Test
//    public void testUpdateBoard() {
//        // Mocking data
//        Board boardToUpdate = new Board();
//        boardToUpdate.setId(1);
//        boardToUpdate.setTitle("Updated Board");
//        boardToUpdate.setWorkspaceID(2);
//
//        Board existingBoard = new Board();
//        existingBoard.setId(1);
//        existingBoard.setTitle("Existing Board");
//        existingBoard.setWorkspaceID(1);
//
//        // Mocking boardRepository.findById()
//        when(boardRepository.findById(boardToUpdate.getId())).thenReturn(Optional.of(existingBoard));
//        // Mocking boardRepository.save()
//        when(boardRepository.save(existingBoard)).thenReturn(existingBoard);
//
//        // Call the updateBoard method
//        String result = boardService.updateBoard(boardToUpdate);
//
//        // Verify that boardRepository.findById() and boardRepository.save() are called once and the returned message is correct
//        verify(boardRepository, times(1)).findById(boardToUpdate.getId());
//        verify(boardRepository, times(1)).save(existingBoard);
//        Assertions.assertEquals("Board Updated Successfully", result);
//        Assertions.assertEquals(boardToUpdate.getTitle(), existingBoard.getTitle());
//        Assertions.assertEquals(boardToUpdate.getWorkspaceID(), existingBoard.getWorkspaceID());
//    }

    @Test
    public void testUpdateBoard_BoardNotFound() {
        // Mocking data
        Board boardToUpdate = new Board();
        boardToUpdate.setId(1);
        boardToUpdate.setTitle("Updated Board");
        boardToUpdate.setWorkspaceID(2);

        // Mocking boardRepository.findById()
        when(boardRepository.findById(boardToUpdate.getId())).thenReturn(Optional.empty());

        // Call the updateBoard method
        String result = boardService.updateBoard(boardToUpdate);

        // Verify that boardRepository.findById() is called once and the returned message is correct
        verify(boardRepository, times(1)).findById(boardToUpdate.getId());
        verify(boardRepository, never()).save( Mockito.any());
        Assertions.assertEquals("No board Assigned with this ID", result);
    }

    @Test
    public void testDeleteBoardById() {
        // Mocking data
        int boardId = 1;

        // Call the deleteBoardById method
        String result = boardService.deleteBoardById(boardId);

        // Verify that boardRepository.deleteById() is called once and the returned message is correct
        verify(boardRepository, times(1)).deleteById(boardId);
        Assertions.assertEquals("Board deleted Successfully", result);
    }
}
