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

  public static MinesweeperGameEntity toEntity(MinesweeperGame minesweeperGame) {

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .id(minesweeperGame.getDetails().getId())
        .numRows(minesweeperGame.getDetails().getRows())
        .numColumns(minesweeperGame.getDetails().getColumns())
        .numBombs(minesweeperGame.getDetails().getNumBombs())
        .numCellsOpened(minesweeperGame.getDetails().getNumCellsOpened())
        .isGameOver(minesweeperGame.getDetails().isGameOver())
        .isWon(minesweeperGame.getDetails().isWon())
        .startTime(minesweeperGame.getDetails().getStartTime())
        .endTime(minesweeperGame.getDetails().getEndTime())
        .duration(Duration.ofSeconds(minesweeperGame.getDetails().getDurationInSegs()))
        .user(UserEntity.toEntity(minesweeperGame.getDetails().getUser()))
        .build();

    minesweeperGameEntity.setBoardCells(MinesweeperGameEntity.getListBoardCell(minesweeperGameEntity, minesweeperGame.getBoard()));

    return minesweeperGameEntity;

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

  public static List<MinesweeperBoardCellEntity> getListBoardCell(MinesweeperGameEntity game, BoardCell[][] boardCells) {

    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    for (int rowIndex = 0; rowIndex < game.getNumRows(); rowIndex++) {
      for (int columnIndex = 0; columnIndex < game.getNumColumns(); columnIndex++) {

        minesweeperBoardCellEntities.add(MinesweeperBoardCellEntity.builder()
            .id(boardCells[rowIndex][columnIndex].getId())
            .game(game)
            .row(rowIndex)
            .column(columnIndex)
            .isFlagged(boardCells[rowIndex][columnIndex].isFlagged())
            .isDetonated(boardCells[rowIndex][columnIndex].isDetonated())
            .isOpened(boardCells[rowIndex][columnIndex].isOpened())
            .value(boardCells[rowIndex][columnIndex].getValue())
            .build());

      }
    }

    return minesweeperBoardCellEntities;
  }


  public static List<MinesweeperBoardCellEntity> getListBoardCell(MinesweeperGameEntity game, List<BoardCell> boardCells) {

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
