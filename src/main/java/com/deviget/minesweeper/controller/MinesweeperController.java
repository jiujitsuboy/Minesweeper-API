package com.deviget.minesweeper.controller;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import com.deviget.minesweeper.hateoas.MinesweeperDetailsRepresentationModelAssembler;
import com.deviget.minesweeper.hateoas.MinesweeperGameRepresentationModelAssembler;
import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.MinesweeperGame;
import com.deviget.minesweeper.model.MinesweeperGameCreateReq;
import com.deviget.minesweeper.model.MinesweeperCellReq;
import com.deviget.minesweeper.service.MinesweeperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
@Api(value = "Minesweeper Controller")
public class MinesweeperController {

  private MinesweeperService minesweeperService;
  private MinesweeperGameRepresentationModelAssembler minesweeperGameRepresentationModelAssembler;
  private MinesweeperDetailsRepresentationModelAssembler minesweeperDetailsRepresentationModelAssembler;
  private PagedResourcesAssembler pagedResourcesAssembler;

  public MinesweeperController(MinesweeperService minesweeperService,
      MinesweeperGameRepresentationModelAssembler minesweeperGameRepresentationModelAssembler,
      MinesweeperDetailsRepresentationModelAssembler minesweeperDetailsRepresentationModelAssembler,
      PagedResourcesAssembler pagedResourcesAssembler) {
    this.minesweeperService = minesweeperService;
    this.minesweeperGameRepresentationModelAssembler = minesweeperGameRepresentationModelAssembler;
    this.minesweeperDetailsRepresentationModelAssembler = minesweeperDetailsRepresentationModelAssembler;
    this.pagedResourcesAssembler = pagedResourcesAssembler;
  }

  @ApiOperation(value = "Create new game", nickname = "createNewGame", notes = "Create a new custom game-")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Create new custom game."),
      @ApiResponse(code = 400, message = "Bad parameters for the custom game.") })
  @PostMapping("/")
  public ResponseEntity<MinesweeperGameDetails> createNewGame(@RequestBody(required = true) MinesweeperGameCreateReq minesweeperGameCreateReq) {
    return status(HttpStatus.CREATED).body(this.minesweeperDetailsRepresentationModelAssembler.toModel(minesweeperService.createNewGame(minesweeperGameCreateReq.getUserId(), minesweeperGameCreateReq.getNumRows(),
        minesweeperGameCreateReq.getNumColumns(), minesweeperGameCreateReq.getNumBombs())));
  }

  @ApiOperation(value = "Get game", nickname = "getGameForUser", notes = "Get an specific game from user-")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Get an specific game from user."),
      @ApiResponse(code = 404, message = "Game not found.") })
  @GetMapping("/{userId}/{gameId}")
  public ResponseEntity<MinesweeperGame> getGameForUser(@PathVariable("gameId") UUID gameId, @PathVariable("userId") UUID userId) {
    return ok(this.minesweeperGameRepresentationModelAssembler.toModel(minesweeperService.getGameForUser(gameId, userId)));
  }

  @ApiOperation(value = "Get all games", nickname = "getAllGamesForUser", notes = "Get all games from user-")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Get all games from user."),
       })
  @GetMapping("/{userId}")
  public ResponseEntity<PagedModel<MinesweeperGame>> getAllGamesForUser(@PathVariable("userId") UUID userId, Pageable pageable) {
    return ok(this.pagedResourcesAssembler.toModel(minesweeperService.getAllGamesForUser(userId, pageable),this.minesweeperDetailsRepresentationModelAssembler));
  }

  @ApiOperation(value = "Open Cell", nickname = "openCell", notes = "Open cell from board-")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "Open cell from board and calculate game status."),
      @ApiResponse(code = 400, message = "Game is already over / Cell not found."),
      @ApiResponse(code = 404, message = "Game not found.") })
  @PatchMapping("/cell")
  public ResponseEntity<GameStatus> openCell(@RequestBody(required = true) MinesweeperCellReq minesweeperCellReq) {
    return status(HttpStatus.ACCEPTED).body(minesweeperService.openCell(minesweeperCellReq.getGameId(), minesweeperCellReq.getUserId(), minesweeperCellReq.getRow(),
        minesweeperCellReq.getColumn()));
  }

  @ApiOperation(value = "Mark Cell", nickname = "markCell", notes = "Mark cell from board-")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "Flag/unFlag board cell."),
      @ApiResponse(code = 400, message = "Cell not found.")})
  @PatchMapping("/cell/flagged")
  public ResponseEntity<Boolean> markCell(@RequestBody(required = true) MinesweeperCellReq minesweeperCellReq) {
    return status(HttpStatus.ACCEPTED).body(minesweeperService.markCell(minesweeperCellReq.getGameId(), minesweeperCellReq.getUserId(), minesweeperCellReq.getRow(),
        minesweeperCellReq.getColumn(), minesweeperCellReq.isFlaggedCell()));
  }

}
