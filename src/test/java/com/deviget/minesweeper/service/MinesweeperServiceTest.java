package com.deviget.minesweeper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.deviget.minesweeper.TestConstants;
import com.deviget.minesweeper.entity.MinesweeperBoardCellEntity;
import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.entity.UserEntity;
import com.deviget.minesweeper.exception.CellNotFoundException;
import com.deviget.minesweeper.exception.GameIsOverException;
import com.deviget.minesweeper.exception.GameNotFoundException;
import com.deviget.minesweeper.exception.InvalidGameParameter;
import com.deviget.minesweeper.exception.UserNotFoundException;
import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.repository.MinesweeperBoardCellRepository;
import com.deviget.minesweeper.repository.MinesweeperGameRepository;
import com.deviget.minesweeper.repository.UserRepository;
import com.deviget.minesweeper.service.impl.MinesweeperServiceImpl;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class MinesweeperServiceTest {

  @Mock
  private MinesweeperGameRepository minesweeperRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MinesweeperBoardCellRepository minesweeperBoardCellRepository;
  @InjectMocks
  private MinesweeperServiceImpl classUnderTest;

  @Test
  public void createNewGame10x10With5Bombs() {

    int rows = 10;
    int columns = 10;
    int bombs = 5;

    UserEntity userEntity = TestConstants.getTestUserEntity(UUID.randomUUID(), TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userEntity));
    when(minesweeperRepository.save(any(MinesweeperGameEntity.class))).thenReturn(null);

    MinesweeperGameEntity minesweeperGame = classUnderTest.createNewGame(UUID.randomUUID(), rows, columns, bombs);

    assertNotNull(minesweeperGame);
    assertEquals(minesweeperGame.getNumRows(), rows);
    assertEquals(minesweeperGame.getNumColumns(), columns);
    assertEquals(minesweeperGame.getNumBombs(), bombs);
  }

  @Test
  public void createNewGameInvalidGameParameterNumberOfRows() {

    int rows = 100;
    int columns = 4;
    int bombs = 5;

    assertThrows(InvalidGameParameter.class, () -> classUnderTest.createNewGame(UUID.randomUUID(), rows, columns, bombs));

  }

  @Test
  public void createNewGameInvalidGameParameterNumberOfColumns() {

    int rows = 5;
    int columns = 100;
    int bombs = 5;

    assertThrows(InvalidGameParameter.class, () -> classUnderTest.createNewGame(UUID.randomUUID(), rows, columns, bombs));

  }

  @Test
  public void createNewGameInvalidGameParameterNumberOfBombs() {

    int rows = 10;
    int columns = 10;
    int bombs = 101;

    assertThrows(InvalidGameParameter.class, () -> classUnderTest.createNewGame(UUID.randomUUID(), rows, columns, bombs));

  }

  @Test
  public void createNewGameInvalidGameParameterNumberOfBombsGreaterThanCells() {

    int rows = 5;
    int columns = 4;
    int bombs = 21;

    assertThrows(InvalidGameParameter.class, () -> classUnderTest.createNewGame(UUID.randomUUID(), rows, columns, bombs));

  }

  @Test
  public void createNewGameUserNotFoundException() {

    int rows = 10;
    int columns = 10;
    int bombs = 5;

    when(userRepository.findById(any(UUID.class))).thenThrow(new UserNotFoundException("No such user found"));

    assertThrows(UserNotFoundException.class, () -> classUnderTest.createNewGame(UUID.randomUUID(), rows, columns, bombs));
  }

  @Test
  public void getGameForUser() {

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(MinesweeperGameEntity.builder().build()));
    MinesweeperGameEntity minesweeperGameEntity = classUnderTest.getGameForUser(UUID.randomUUID(), UUID.randomUUID());
    assertNotNull(minesweeperGameEntity);
  }

  @Test
  public void getGameForUserGameNotFoundException() {

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenThrow(GameNotFoundException.class);

    assertThrows(GameNotFoundException.class, () -> classUnderTest.getGameForUser(UUID.randomUUID(), UUID.randomUUID()));
  }

  @Test
  public void getAllGamesForUser() {
    int numPage = 0;
    int size = 2;
    Page page = new PageImpl(List.of(MinesweeperGameEntity.builder().build()));
    when(minesweeperRepository.findAllByUserId(any(UUID.class), any(Pageable.class))).thenReturn(page);
    Page<MinesweeperGameEntity> minesweeperGameEntity = classUnderTest.getAllGamesForUser(UUID.randomUUID(), PageRequest.of(numPage, size));
    assertNotNull(minesweeperGameEntity);
  }

  @Test
  public void openCellValueOne() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    int value = 1;

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(MinesweeperGameEntity.builder().startTime(
            Instant.now()).build()));
    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenReturn(
        Optional.of(MinesweeperBoardCellEntity.builder().value(value).build()));

    when(minesweeperBoardCellRepository.saveAll(any(List.class))).thenReturn(null);
    when(minesweeperRepository.save(any(MinesweeperGameEntity.class))).thenReturn(null);

    GameStatus gameStatus = classUnderTest.openCell(gameId, userId, row, column);

    assertNotNull(gameStatus);
    assertEquals(gameStatus.getGameId(), gameId);
    assertEquals(gameStatus.getUserId(), userId);
    assertEquals(gameStatus.isGameOver(), false);
    assertEquals(gameStatus.isWon(), false);
    assertEquals(gameStatus.getCellsOpened().size(), 1);
  }

  @Test
  public void openCellBomb() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    int value = -1;
    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .startTime(Instant.now())
        .build();

    for (int cell = 0; cell < 2; cell++) {
      minesweeperBoardCellEntities.add(
          MinesweeperBoardCellEntity.builder()
              .isDetonated(false)
              .isOpened(false)
              .isFlagged(false)
              .id(UUID.randomUUID())
              .game(minesweeperGameEntity)
              .row(cell / 2)
              .column(cell % 2)
              .value(value)
              .build()
      );
    }

    minesweeperGameEntity.setBoardCells(minesweeperBoardCellEntities);

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(minesweeperGameEntity));
    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenReturn(
        Optional.of(minesweeperBoardCellEntities.get(0)));

    when(minesweeperBoardCellRepository.saveAll(any(List.class))).thenReturn(null);
    when(minesweeperRepository.save(any(MinesweeperGameEntity.class))).thenReturn(null);

    GameStatus gameStatus = classUnderTest.openCell(gameId, userId, row, column);

    assertNotNull(gameStatus);
    assertEquals(gameStatus.getGameId(), gameId);
    assertEquals(gameStatus.getUserId(), userId);
    assertEquals(gameStatus.isGameOver(), true);
    assertEquals(gameStatus.isWon(), false);
    assertEquals(gameStatus.getCellsOpened().size(), minesweeperBoardCellEntities.size());
  }

  @Test
  public void openCellValuesAllZero() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int numRows = 3;
    int numColumns = 3;
    int row = 1;
    int column = 1;
    int bombs = 0;
    int value = 0;
    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .id(UUID.randomUUID())
        .numCellsOpened(0)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(bombs)
        .startTime(Instant.now())
        .build();

    for (int cell = 0; cell < 9; cell++) {
      minesweeperBoardCellEntities.add(
          MinesweeperBoardCellEntity.builder()
              .isDetonated(false)
              .isOpened(false)
              .isFlagged(false)
              .id(UUID.randomUUID())
              .game(minesweeperGameEntity)
              .row(cell / 3)
              .column(cell % 3)
              .value(value)
              .build()
      );
    }

    minesweeperGameEntity.setBoardCells(minesweeperBoardCellEntities);

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(minesweeperGameEntity));
    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenReturn(
        Optional.of(minesweeperBoardCellEntities.get(4)));

    when(minesweeperBoardCellRepository.saveAll(any(List.class))).thenReturn(null);
    when(minesweeperRepository.save(any(MinesweeperGameEntity.class))).thenReturn(null);

    GameStatus gameStatus = classUnderTest.openCell(gameId, userId, row, column);

    assertNotNull(gameStatus);
    assertEquals(gameStatus.getGameId(), gameId);
    assertEquals(gameStatus.getUserId(), userId);
    assertEquals(gameStatus.isGameOver(), true);
    assertEquals(gameStatus.isWon(), true);
    assertEquals(gameStatus.getCellsOpened().size(), minesweeperBoardCellEntities.size());
  }

  @Test
  public void openCellIsGameOver() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int numRows = 3;
    int numColumns = 3;
    int row = 1;
    int column = 1;
    int bombs = 0;
    int value = 0;
    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .id(UUID.randomUUID())
        .numCellsOpened(0)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(bombs)
        .startTime(Instant.now())
        .isGameOver(true)
        .isWon(true)
        .build();

    for (int cell = 0; cell < 2; cell++) {
      minesweeperBoardCellEntities.add(
          MinesweeperBoardCellEntity.builder()
              .isDetonated(false)
              .isOpened(false)
              .isFlagged(false)
              .id(UUID.randomUUID())
              .game(minesweeperGameEntity)
              .row(cell / 2)
              .column(cell % 2)
              .value(value)
              .build()
      );
    }

    minesweeperGameEntity.setBoardCells(minesweeperBoardCellEntities);

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(minesweeperGameEntity));

    assertThrows(GameIsOverException.class, () -> classUnderTest.openCell(gameId, userId, row, column));

  }

  @Test
  public void openCellGameNotFoundException() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 1;
    int column = 1;

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenThrow(GameNotFoundException.class);

    assertThrows(GameNotFoundException.class, () -> classUnderTest.openCell(gameId, userId, row, column));

  }

  @Test
  public void openCellCellNotFoundException() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 1;
    int column = 1;

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(MinesweeperGameEntity.builder().startTime(
            Instant.now()).build()));
    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenThrow(
        CellNotFoundException.class);

    assertThrows(CellNotFoundException.class, () -> classUnderTest.openCell(gameId, userId, row, column));

  }

  @Test
  public void openCellAlreadyOpenCell() {

    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    int value = 1;

    when(minesweeperRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(
        Optional.of(MinesweeperGameEntity.builder()
            .startTime(Instant.now())
            .build()));
    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenReturn(
        Optional.of(MinesweeperBoardCellEntity.builder()
            .isOpened(true)
            .value(value)
            .build()));

    GameStatus gameStatus = classUnderTest.openCell(gameId, userId, row, column);

    assertNotNull(gameStatus);
    assertEquals(gameStatus.getGameId(), gameId);
    assertEquals(gameStatus.getUserId(), userId);
    assertEquals(gameStatus.isGameOver(), false);
    assertEquals(gameStatus.isWon(), false);
    assertEquals(gameStatus.getCellsOpened().size(), 0);

  }

  @Test
  public void markCell() {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean flaggedCell = true;

    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenReturn(
        Optional.of(MinesweeperBoardCellEntity.builder()
            .isOpened(true)
            .value(0)
            .build()));

    when(minesweeperBoardCellRepository.save(any(MinesweeperBoardCellEntity.class))).thenReturn(null);

    boolean isCellMarked = classUnderTest.markCell(gameId, userId, row, column, flaggedCell);

    assertEquals(isCellMarked, flaggedCell);

  }

  @Test
  public void markCellCellNotFoundException() {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean flaggedCell = true;

    when(minesweeperBoardCellRepository.findByGameIdAndRowAndColumn(any(UUID.class), anyInt(), anyInt())).thenThrow(
        CellNotFoundException.class);

    assertThrows(CellNotFoundException.class,()->classUnderTest.markCell(gameId, userId, row, column, flaggedCell));

  }
}