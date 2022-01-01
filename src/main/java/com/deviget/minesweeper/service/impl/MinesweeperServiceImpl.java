package com.deviget.minesweeper.service.impl;

import com.deviget.minesweeper.entity.MinesweeperBoardCellEntity;
import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.entity.UserEntity;
import com.deviget.minesweeper.exception.CellNotFoundException;
import com.deviget.minesweeper.exception.GameIsOverException;
import com.deviget.minesweeper.exception.GameNotFoundException;
import com.deviget.minesweeper.exception.InvalidGameParameter;
import com.deviget.minesweeper.exception.UserNotFoundException;
import com.deviget.minesweeper.model.BoardCell;
import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.MinesweeperGame;
import com.deviget.minesweeper.repository.MinesweeperBoardCellRepository;
import com.deviget.minesweeper.repository.MinesweeperGameRepository;
import com.deviget.minesweeper.repository.UserRepository;
import com.deviget.minesweeper.service.MinesweeperService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MinesweeperServiceImpl implements MinesweeperService {

  private final MinesweeperGameRepository minesweeperRepository;
  private final UserRepository userRepository;
  private final MinesweeperBoardCellRepository minesweeperBoardCellRepository;


  public MinesweeperServiceImpl(MinesweeperGameRepository minesweeperRepository, UserRepository userRepository,
      MinesweeperBoardCellRepository minesweeperBoardCellRepository) {
    this.minesweeperRepository = minesweeperRepository;
    this.userRepository = userRepository;
    this.minesweeperBoardCellRepository = minesweeperBoardCellRepository;
  }

  @Override
  @Transactional
  public MinesweeperGameDetails createNewGame(UUID userId, int rows, int columns, int numBombs) {

    if (rows < 0 || rows > 999) {
      throw new InvalidGameParameter("Number of rows should be between 0 to 999");
    }
    else if (columns < 0 || columns > 999) {
      throw new InvalidGameParameter("Number of columns should be between 0 to 999");
    }
    else if (numBombs < 0 || numBombs > 999) {
      throw new InvalidGameParameter("Number of bombs should be between 0 to 999");
    }
    else if (numBombs > (rows * columns)) {
      throw new InvalidGameParameter("Number of bombs can't be greater than the number of cells on the board");
    }

    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user found"));

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .numRows(rows)
        .numColumns(columns)
        .numBombs(numBombs)
        .isGameOver(false)
        .startTime(Instant.now())
        .duration(Duration.ZERO)
        .boardCells(new ArrayList<>())
        .user(user)
        .build();

    BoardCell[][] minesweeperBoard = createBoard(rows, columns, numBombs);

    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {

        minesweeperGameEntity.getBoardCells().add(MinesweeperBoardCellEntity.builder()
            .game(minesweeperGameEntity)
            .row(row)
            .column(column)
            .isFlagged(minesweeperBoard[row][column].isFlagged())
            .value(minesweeperBoard[row][column].getValue())
            .build());

      }
    }

    minesweeperGameEntity = minesweeperRepository.save(minesweeperGameEntity);

    return MinesweeperGameEntity.toModel(minesweeperGameEntity).getDetails();
  }

  @Override
  public MinesweeperGame getGameForUser(UUID gameId, UUID userId) {
    return MinesweeperGameEntity.toModel(
        minesweeperRepository.findByIdAndUserId(gameId, userId).orElseThrow(() -> new GameNotFoundException("No game found")));
  }

  @Override
  public List<MinesweeperGameDetails> getAllGamesForUser(UUID userId) {
    List<MinesweeperGameEntity> minesweeperGameEntities = minesweeperRepository.findAllByUserId(userId);
    return minesweeperGameEntities.stream().map(entity -> MinesweeperGameEntity.toModel(entity).getDetails()).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public GameStatus openCell(UUID gameId, UUID userId, int row, int column) {
    boolean isWon = false, isGameOver = false;
    List<BoardCell> emptyBoardCells = new ArrayList<>();
    List<MinesweeperBoardCellEntity> boardCellsEntities = new ArrayList<>();
    Instant currentInstant = Instant.now();

    MinesweeperGameEntity minesweeperGameEntity = minesweeperRepository.findByIdAndUserId(gameId, userId)
        .orElseThrow(() -> new GameNotFoundException("No game found"));

    if (minesweeperGameEntity.isGameOver()) {
      throw new GameIsOverException("Game is already finished");
    }

    MinesweeperBoardCellEntity minesweeperBoardCellEntity = minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(gameId, row, column)
        .orElseThrow(() -> new CellNotFoundException("No cell found"));

    minesweeperBoardCellEntity.setOpened(true);
    int cellValue = minesweeperBoardCellEntity.getValue();

    emptyBoardCells.add(BoardCell.builder()
        .id(minesweeperBoardCellEntity.getId())
        .row(row)
        .column(column)
        .value(cellValue)
        .isFlagged(minesweeperBoardCellEntity.isFlagged())
        .isDetonated(cellValue == -1)
        .isOpened(true)
        .build());

    switch (cellValue) {
      case -1:
        isGameOver = true;
        break;
      case 0:
        BoardCell[][] board = MinesweeperGameEntity.getMatrixBoardCell(minesweeperGameEntity);
        board[row][column].setOpened(true);
        getAdjacentEmptyCells(row, column, board, emptyBoardCells);
        boardCellsEntities = MinesweeperGameEntity.getListBoardCell(minesweeperGameEntity, emptyBoardCells);
        break;
    }

    int totalNumCellsOpened = minesweeperGameEntity.getNumCellsOpened() + emptyBoardCells.size();
    int totalNumCellsGame = ((minesweeperGameEntity.getNumRows() * minesweeperGameEntity.getNumColumns())
        - minesweeperGameEntity.getNumBombs());

    if (totalNumCellsOpened == totalNumCellsGame) {
      isWon = true;
      isGameOver = true;
    }

    if (isGameOver) {
      minesweeperGameEntity.setEndTime(currentInstant);
    }
    minesweeperGameEntity.setDuration(Duration.between(minesweeperGameEntity.getStartTime(), currentInstant));
    minesweeperGameEntity.setNumCellsOpened(totalNumCellsOpened);
    minesweeperGameEntity.setGameOver(isGameOver);
    minesweeperGameEntity.setWon(isWon);

    minesweeperBoardCellRepository.saveAll(boardCellsEntities);
    minesweeperRepository.save(minesweeperGameEntity);

    return GameStatus.builder()
        .gameId(gameId)
        .userId(userId)
        .isWon(isWon)
        .isGameOver(isGameOver)
        .cellValue(cellValue)
        .cellsOpened(emptyBoardCells)
        .build();
  }

  @Override
  public boolean markCell(UUID gameId, UUID userId, int row, int column, boolean flagCell) {

    MinesweeperBoardCellEntity minesweeperBoardCellEntity = minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(gameId, row, column)
        .orElseThrow(() -> new CellNotFoundException(""));

    minesweeperBoardCellEntity.setFlagged(flagCell);
    minesweeperBoardCellRepository.save(minesweeperBoardCellEntity);

    return flagCell;
  }

  private BoardCell[][] createBoard(int rows, int columns, int numBombs) {

    int bombsCreated = 0;
    BoardCell[][] board = new BoardCell[rows][columns];
    List<BoardCell> bombCells = new ArrayList<>();

    while (bombsCreated < numBombs) {

      int rowIndex = (int) Math.round(Math.random() * (rows - 1));
      int columnIndex = (int) Math.round(Math.random() * (columns - 1));

      if (board[rowIndex][columnIndex] == null) {
        BoardCell boardCell = BoardCell.builder()
            .row(rowIndex)
            .column(columnIndex)
            .value(-1)
            .isFlagged(false)
            .isDetonated(false)
            .build();
        board[rowIndex][columnIndex] = boardCell;

        bombCells.add(boardCell);

        bombsCreated++;
      }
    }

    for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
      for (int columnIndex = 0; columnIndex < columns; columnIndex++) {

        if (board[rowIndex][columnIndex] == null) {
          board[rowIndex][columnIndex] = BoardCell.builder()
              .row(rowIndex)
              .column(columnIndex)
              .value(0)
              .isFlagged(false)
              .isDetonated(false)
              .build();
        }
      }
    }

    for (BoardCell bombCell : bombCells) {
      calculateBombsCloseToCell(bombCell.getRow(), bombCell.getColumn(), board);
    }

    return board;
  }

  private void calculateBombsCloseToCell(int row, int column, BoardCell[][] board) {
    if (row - 1 > -1 && column - 1 > -1 && board[row - 1][column - 1].getValue() != -1) {
      board[row - 1][column - 1].setValue(board[row - 1][column - 1].getValue() + 1);
    }
    if (row - 1 > -1 && board[row - 1][column].getValue() != -1) {
      board[row - 1][column].setValue(board[row - 1][column].getValue() + 1);
    }
    if (row - 1 > -1 && column + 1 < board[0].length && board[row - 1][column + 1].getValue() != -1) {
      board[row - 1][column + 1].setValue(board[row - 1][column + 1].getValue() + 1);
    }

    if (row + 1 < board.length && column - 1 > -1 && board[row + 1][column - 1].getValue() != -1) {
      board[row + 1][column - 1].setValue(board[row + 1][column - 1].getValue() + 1);
    }
    if (row + 1 < board.length && board[row + 1][column].getValue() != -1) {
      board[row + 1][column].setValue(board[row + 1][column].getValue() + 1);
    }
    if (row + 1 < board.length && column + 1 < board[0].length && board[row + 1][column + 1].getValue() != -1) {
      board[row + 1][column + 1].setValue(board[row + 1][column + 1].getValue() + 1);
    }

    if (column - 1 > -1 && board[row][column - 1].getValue() != -1) {
      board[row][column - 1].setValue(board[row][column - 1].getValue() + 1);
    }
    if (column + 1 < board[0].length && board[row][column + 1].getValue() != -1) {
      board[row][column + 1].setValue(board[row][column + 1].getValue() + 1);
    }
  }

  private void getAdjacentEmptyCells(int row, int column, BoardCell[][] board, List<BoardCell> emptyBoardCells) {

    if (row - 1 > -1 && column - 1 > -1 && !board[row - 1][column - 1].isOpened() && board[row - 1][column - 1].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row - 1, column - 1));
      board[row - 1][column - 1].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row - 1][column - 1].getId())
          .row(row - 1)
          .column(column - 1)
          .value(board[row - 1][column - 1].getValue())
          .isFlagged(board[row - 1][column - 1].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row - 1, column - 1, board, emptyBoardCells);
    }
    if (row - 1 > -1 && !board[row - 1][column].isOpened() && board[row - 1][column].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row - 1, column));
      board[row - 1][column].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row - 1][column].getId())
          .row(row - 1)
          .column(column)
          .value(board[row - 1][column].getValue())
          .isFlagged(board[row - 1][column].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row - 1, column, board, emptyBoardCells);
    }
    if (row - 1 > -1 && column + 1 < board[0].length && !board[row - 1][column + 1].isOpened()
        && board[row - 1][column + 1].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row - 1, column + 1));
      board[row - 1][column + 1].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row - 1][column + 1].getId())
          .row(row - 1)
          .column(column + 1)
          .value(board[row - 1][column + 1].getValue())
          .isFlagged(board[row - 1][column + 1].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row - 1, column + 1, board, emptyBoardCells);
    }

    if (row + 1 < board.length && column - 1 > -1 && !board[row + 1][column - 1].isOpened() && board[row + 1][column - 1].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row + 1, column - 1));
      board[row + 1][column - 1].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row + 1][column - 1].getId())
          .row(row + 1)
          .column(column - 1)
          .value(board[row + 1][column - 1].getValue())
          .isFlagged(board[row + 1][column - 1].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row + 1, column - 1, board, emptyBoardCells);
    }
    if (row + 1 < board.length && !board[row + 1][column].isOpened() && board[row + 1][column].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row + 1, column));
      board[row + 1][column].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row + 1][column].getId())
          .row(row + 1)
          .column(column)
          .value(board[row + 1][column].getValue())
          .isFlagged(board[row + 1][column].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row + 1, column, board, emptyBoardCells);
    }
    if (row + 1 < board.length && column + 1 < board[0].length && !board[row + 1][column + 1].isOpened()
        && board[row + 1][column + 1].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row + 1, column + 1));
      board[row + 1][column + 1].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row + 1][column + 1].getId())
          .row(row + 1)
          .column(column + 1)
          .value(board[row + 1][column + 1].getValue())
          .isFlagged(board[row + 1][column + 1].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row + 1, column + 1, board, emptyBoardCells);
    }

    if (column - 1 > -1 && !board[row][column - 1].isOpened() && board[row][column - 1].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row, column - 1));
      board[row][column - 1].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row][column - 1].getId())
          .row(row)
          .column(column - 1)
          .value(board[row][column - 1].getValue())
          .isFlagged(board[row][column - 1].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row, column - 1, board, emptyBoardCells);
    }
    if (column + 1 < board[0].length && !board[row][column + 1].isOpened() && board[row][column + 1].getValue() == 0) {
      System.out.println(String.format("row: %d, col: %d", row, column + 1));
      board[row][column + 1].setOpened(true);
      emptyBoardCells.add(BoardCell.builder()
          .id(board[row][column + 1].getId())
          .row(row)
          .column(column + 1)
          .value(board[row][column + 1].getValue())
          .isFlagged(board[row][column + 1].isFlagged())
          .isOpened(true)
          .isDetonated(false)
          .build());
      getAdjacentEmptyCells(row, column + 1, board, emptyBoardCells);
    }
  }
}
