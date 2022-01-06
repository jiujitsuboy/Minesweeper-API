package com.deviget.minesweeper.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deviget.minesweeper.TestConstants;
import com.deviget.minesweeper.configuration.AppConfig;
import com.deviget.minesweeper.entity.MinesweeperBoardCellEntity;
import com.deviget.minesweeper.entity.MinesweeperGameEntity;
import com.deviget.minesweeper.entity.RoleEnum;
import com.deviget.minesweeper.entity.UserEntity;
import com.deviget.minesweeper.exception.CellNotFoundException;
import com.deviget.minesweeper.exception.GameIsOverException;
import com.deviget.minesweeper.exception.GameNotFoundException;
import com.deviget.minesweeper.exception.InvalidGameParameter;
import com.deviget.minesweeper.hateoas.MinesweeperDetailsRepresentationModelAssembler;
import com.deviget.minesweeper.hateoas.MinesweeperGameRepresentationModelAssembler;
import com.deviget.minesweeper.hateoas.UserRepresentationModelAssembler;
import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.model.MinesweeperCellReq;
import com.deviget.minesweeper.model.MinesweeperGameCreateReq;
import com.deviget.minesweeper.security.JwtManager;
import com.deviget.minesweeper.service.MinesweeperService;
import com.deviget.minesweeper.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@Import(AppConfig.class)
@WebMvcTest(MinesweeperController.class)
@ComponentScan(basePackages = "com.deviget.minesweeper.security")
class MinesweeperControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper json;
  @Autowired
  private JwtManager tokenManager;
  @MockBean
  private MinesweeperService minesweeperService;
  @MockBean
  private UserService userService;
  @SpyBean
  private MinesweeperGameRepresentationModelAssembler minesweeperGameRepresentationModelAssembler;
  @SpyBean
  private MinesweeperDetailsRepresentationModelAssembler minesweeperDetailsRepresentationModelAssembler;
  @SpyBean
  private UserRepresentationModelAssembler userAssembler;
  @SpyBean
  private PagedResourcesAssembler pagedResourcesAssembler;

  private String token;

  @BeforeEach
  public void getToken() {
    token = TestConstants.getToken(tokenManager, RoleEnum.USER.name());
  }

  @Test
  public void createNewGame() throws Exception {

    UUID userId = UUID.randomUUID();
    int numRows = 3;
    int numColumns = 3;
    int numBombs = 0;
    int value = 0;

    MinesweeperGameCreateReq minesweeperGameCreateReq = MinesweeperGameCreateReq.builder()
        .userId(userId)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(numBombs)
        .build();

    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    UserEntity userEntity = TestConstants.getTestUserEntity(userId, TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .id(UUID.randomUUID())
        .user(userEntity)
        .numCellsOpened(0)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(numBombs)
        .startTime(Instant.now())
        .duration(Duration.ZERO)
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

    when(minesweeperService.createNewGame(any(UUID.class), anyInt(), anyInt(), anyInt())).thenReturn(minesweeperGameEntity);

    mvc.perform(post("/api/v1/game/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperGameCreateReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user.id", is(userId.toString())))
        .andExpect(jsonPath("$.rows", is(numRows)))
        .andExpect(jsonPath("$.columns", is(numColumns)))
        .andExpect(jsonPath("$.numBombs", is(numBombs)))
        .andExpect(jsonPath("$.numCellsOpened", is(0)));
  }

  @Test
  public void createNewGameInvalidGameParameter() throws Exception {

    UUID userId = UUID.randomUUID();
    int numRows = 3;
    int numColumns = 3;
    int numBombs = 0;
    int value = 0;

    MinesweeperGameCreateReq minesweeperGameCreateReq = MinesweeperGameCreateReq.builder()
        .userId(userId)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(numBombs)
        .build();

    when(minesweeperService.createNewGame(any(UUID.class), anyInt(), anyInt(), anyInt())).thenThrow(InvalidGameParameter.class);

    mvc.perform(post("/api/v1/game/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperGameCreateReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getGameForUser() throws Exception {

    UUID userId = UUID.randomUUID();
    UUID gameId = UUID.randomUUID();
    int numRows = 3;
    int numColumns = 3;
    int numBombs = 0;
    int value = 0;

    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    UserEntity userEntity = TestConstants.getTestUserEntity(userId, TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .id(UUID.randomUUID())
        .user(userEntity)
        .numCellsOpened(0)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(numBombs)
        .startTime(Instant.now())
        .duration(Duration.ZERO)
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

    when(minesweeperService.getGameForUser(any(UUID.class), any(UUID.class))).thenReturn(minesweeperGameEntity);

    mvc.perform(get("/api/v1/game/{userId}/{gameId}", userId, gameId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.details.user.id", is(userId.toString())))
        .andExpect(jsonPath("$.details.rows", is(numRows)))
        .andExpect(jsonPath("$.details.columns", is(numColumns)))
        .andExpect(jsonPath("$.details.numBombs", is(numBombs)))
        .andExpect(jsonPath("$.details.numCellsOpened", is(0)));
  }

  @Test
  public void getGameForUserGameNotFoundException() throws Exception {

    UUID userId = UUID.randomUUID();
    UUID gameId = UUID.randomUUID();

    when(minesweeperService.getGameForUser(any(UUID.class), any(UUID.class))).thenThrow(new GameNotFoundException("Game not found"));

    mvc.perform(get("/api/v1/game/{userId}/{gameId}", userId, gameId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getAllGamesForUser() throws Exception {

    UUID userId = UUID.randomUUID();
    int numRows = 3;
    int numColumns = 3;
    int numBombs = 0;
    int value = 0;

    List<MinesweeperBoardCellEntity> minesweeperBoardCellEntities = new ArrayList<>();

    UserEntity userEntity = TestConstants.getTestUserEntity(userId, TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);

    MinesweeperGameEntity minesweeperGameEntity = MinesweeperGameEntity.builder()
        .id(UUID.randomUUID())
        .user(userEntity)
        .numCellsOpened(0)
        .numRows(numRows)
        .numColumns(numColumns)
        .numBombs(numBombs)
        .startTime(Instant.now())
        .duration(Duration.ZERO)
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
    Page page = new PageImpl(List.of(minesweeperGameEntity));

    when(minesweeperService.getAllGamesForUser(any(UUID.class), any(Pageable.class))).thenReturn(page);

    mvc.perform(get("/api/v1/game/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.minesweeperGameDetailsList[0].user.id", is(userId.toString())))
        .andExpect(jsonPath("$._embedded.minesweeperGameDetailsList[0].rows", is(numRows)))
        .andExpect(jsonPath("$._embedded.minesweeperGameDetailsList[0].columns", is(numColumns)))
        .andExpect(jsonPath("$._embedded.minesweeperGameDetailsList[0].numBombs", is(numBombs)))
        .andExpect(jsonPath("$._embedded.minesweeperGameDetailsList[0].numCellsOpened", is(0)))
        .andExpect(jsonPath("$.page.size", is(1)))
        .andExpect(jsonPath("$.page.totalElements", is(1)))
        .andExpect(jsonPath("$.page.totalPages", is(1)))
        .andExpect(jsonPath("$.page.number", is(0)));
  }

  @Test
  public void openCell() throws Exception {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int value = 5;
    int row = 0;
    int column = 0;
    boolean isCellFlagged = false;
    boolean isGameOver = false;
    boolean isWon = false;

    GameStatus gameStatus = GameStatus.builder()
        .gameId(gameId)
        .userId(userId)
        .cellValue(value)
        .cellsOpened(new ArrayList<>())
        .isGameOver(isGameOver)
        .isWon(isWon)
        .build();

    MinesweeperCellReq minesweeperCellReq = MinesweeperCellReq.builder()
        .userId(userId)
        .gameId(gameId)
        .row(row)
        .column(column)
        .flaggedCell(isCellFlagged)
        .build();

    when(minesweeperService.openCell(any(UUID.class), any(UUID.class), anyInt(), anyInt())).thenReturn(gameStatus);

    mvc.perform(patch("/api/v1/game/cell")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperCellReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.gameId", is(gameId.toString())))
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.cellValue", is(value)))
        .andExpect(jsonPath("$.gameOver", is(isGameOver)))
        .andExpect(jsonPath("$.won", is(isWon)));
  }

  @Test
  public void openCellGameIsOverException() throws Exception {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean isCellFlagged = false;

    MinesweeperCellReq minesweeperCellReq = MinesweeperCellReq.builder()
        .userId(userId)
        .gameId(gameId)
        .row(row)
        .column(column)
        .flaggedCell(isCellFlagged)
        .build();

    when(minesweeperService.openCell(any(UUID.class), any(UUID.class), anyInt(), anyInt())).thenThrow(GameIsOverException.class);

    mvc.perform(patch("/api/v1/game/cell")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperCellReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void openCellCellNotFoundException() throws Exception {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean isCellFlagged = false;

    MinesweeperCellReq minesweeperCellReq = MinesweeperCellReq.builder()
        .userId(userId)
        .gameId(gameId)
        .row(row)
        .column(column)
        .flaggedCell(isCellFlagged)
        .build();

    when(minesweeperService.openCell(any(UUID.class), any(UUID.class), anyInt(), anyInt())).thenThrow(CellNotFoundException.class);

    mvc.perform(patch("/api/v1/game/cell")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperCellReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void markCell() throws Exception {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean isCellFlagged = false;

    MinesweeperCellReq minesweeperCellReq = MinesweeperCellReq.builder()
        .userId(userId)
        .gameId(gameId)
        .row(row)
        .column(column)
        .flaggedCell(isCellFlagged)
        .build();

    when(minesweeperService.markCell(any(UUID.class), any(UUID.class), anyInt(), anyInt(), anyBoolean())).thenReturn(isCellFlagged);

    mvc.perform(patch("/api/v1/game/cell/flagged")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperCellReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$", is(isCellFlagged)));
  }

  @Test
  public void markCellCellNotFoundException() throws Exception {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean isCellFlagged = false;

    MinesweeperCellReq minesweeperCellReq = MinesweeperCellReq.builder()
        .userId(userId)
        .gameId(gameId)
        .row(row)
        .column(column)
        .flaggedCell(isCellFlagged)
        .build();

    when(minesweeperService.markCell(any(UUID.class), any(UUID.class), anyInt(), anyInt(), anyBoolean())).thenThrow(new CellNotFoundException("Cell not found"));

    mvc.perform(patch("/api/v1/game/cell/flagged")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperCellReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void markCellError() throws Exception {
    UUID gameId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    int row = 0;
    int column = 0;
    boolean isCellFlagged = false;

    MinesweeperCellReq minesweeperCellReq = MinesweeperCellReq.builder()
        .userId(userId)
        .gameId(gameId)
        .row(row)
        .column(column)
        .flaggedCell(isCellFlagged)
        .build();

    when(minesweeperService.markCell(any(UUID.class), any(UUID.class), anyInt(), anyInt(), anyBoolean())).thenThrow(Error.class);

    mvc.perform(patch("/api/v1/game/cell/flagged")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(minesweeperCellReq))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isInternalServerError());
  }
}