package com.deviget.minesweeper.entity;

import com.deviget.minesweeper.model.BoardCell;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.MinesweeperGame;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinesweeperGameEntity {

  @Id
  @GeneratedValue
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private UserEntity user;

  @Column(name = "NUM_ROWS")
  private int numRows;

  @Column(name = "NUM_COLUMNS")
  private int numColumns;

  @Column(name = "NUM_BOMBS")
  private int numBombs;

  @Column(name = "NUM_CELLS_OPENED")
  private int numCellsOpened;

  @Column(name = "START_TIME")
  private Instant startTime;

  @Column(name = "END_TIME")
  private Instant endTime;

  @Column(name = "DURATION")
  private Duration duration;

  @Column(name = "IS_GAME_OVER")
  private boolean isGameOver;

  @Column(name = "IS_WON")
  private boolean isWon;

  @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<MinesweeperBoardCellEntity> boardCells;

  public static MinesweeperGame toModel(MinesweeperGameEntity minesweeperGameEntity) {

    MinesweeperGameDetails minesweeperDetails = MinesweeperGameDetails.builder()
        .id(minesweeperGameEntity.getId())
        .rows(minesweeperGameEntity.getNumRows())
        .columns(minesweeperGameEntity.getNumColumns())
        .numBombs(minesweeperGameEntity.getNumBombs())
        .numCellsOpened(minesweeperGameEntity.getNumCellsOpened())
        .isGameOver(minesweeperGameEntity.isGameOver)
        .isWon(minesweeperGameEntity.isWon)
        .startTime(minesweeperGameEntity.getStartTime())
        .endTime(minesweeperGameEntity.getEndTime())
        .durationInSegs(minesweeperGameEntity.getDuration().toSeconds())
        .user(UserEntity.toModel(minesweeperGameEntity.getUser()))
        .build();

    BoardCell[][] board = MinesweeperGameEntity.getMatrixBoardCell(minesweeperGameEntity);

    return MinesweeperGame.builder()
        .details(minesweeperDetails)
        .board(board)
        .build();
  }

  public static List<BoardCell> fromMinesweeperBoardCellEntityListToBoarCellList(List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities){

    List<BoardCell> boardCells = new ArrayList<>();

    minesweeperBoardCellEntities.stream().forEach(cell -> boardCells.add(BoardCell.builder()
        .id(cell.getId())
        .row(cell.getRow())
        .column(cell.getColumn())
        .value(cell.getValue())
        .isFlagged(cell.isFlagged())
        .isOpened(cell.isOpened())
        .isDetonated(cell.isDetonated())
        .build()));

    return boardCells;
  }

  public static BoardCell[][] getMatrixBoardCell(MinesweeperGameEntity game) {

    BoardCell[][] board = null;
    int rows = game.getNumRows();
    int columns = game.getNumColumns();
    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = game.getBoardCells();

    if (rows * columns == minesweeperBoardCellEntities.size()) {
      BoardCell[][] tempBoard = new BoardCell[rows][columns];

      minesweeperBoardCellEntities.stream().forEach(cell -> tempBoard[cell.getRow()][cell.getColumn()] = BoardCell.builder()
          .id(cell.getId())
          .row(cell.getRow())
          .column(cell.getColumn())
          .value(cell.getValue())
          .isFlagged(cell.isFlagged())
          .isOpened(cell.isOpened())
          .isDetonated(cell.isDetonated())
          .build());

      board = tempBoard;
    }

    return board;

  }

  public static List<MinesweeperBoardCellEntity> boardCellListToListMinesweeperBoardCellEntity(MinesweeperGameEntity game, List<BoardCell> boardCells) {

    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();


    boardCells.stream().forEach(boardCell -> minesweeperBoardCellEntities.add(MinesweeperBoardCellEntity.builder()
        .id(boardCell.getId())
        .game(game)
        .row(boardCell.getRow())
        .column(boardCell.getColumn())
        .isFlagged(boardCell.isFlagged())
        .value(boardCell.getValue())
        .isOpened(boardCell.isOpened())
        .isDetonated(boardCell.isDetonated())
        .build()));

    return minesweeperBoardCellEntities;
  }
}