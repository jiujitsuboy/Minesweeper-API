package com.deviget.minesweeper.controller;

import com.deviget.minesweeper.hateoas.MinesweeperDetailsRepresentationModelAssembler;
import com.deviget.minesweeper.hateoas.MinesweeperGameRepresentationModelAssembler;
import com.deviget.minesweeper.model.GameStatus;
import com.deviget.minesweeper.model.MinesweeperGameDetails;
import com.deviget.minesweeper.model.MinesweeperGame;
import com.deviget.minesweeper.model.MinesweeperGameCreateReq;
import com.deviget.minesweeper.model.MinesweeperCellReq;
import com.deviget.minesweeper.service.MinesweeperService;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
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

  @PostMapping("/")
  public MinesweeperGameDetails createNewGame(@RequestBody(required = true) MinesweeperGameCreateReq minesweeperGameCreateReq) {
    return this.minesweeperDetailsRepresentationModelAssembler.toModel(minesweeperService.createNewGame(minesweeperGameCreateReq.getUserId(), minesweeperGameCreateReq.getNumRows(),
        minesweeperGameCreateReq.getNumColumns(), minesweeperGameCreateReq.getNumBombs()));
  }

  @GetMapping("/{userId}/{gameId}")
  public MinesweeperGame getGameForUser(@PathVariable("gameId") UUID gameId, @PathVariable("userId") UUID userId) {
    return this.minesweeperGameRepresentationModelAssembler.toModel(minesweeperService.getGameForUser(gameId, userId));
  }

  @GetMapping("/{userId}")
  public PagedModel<MinesweeperGame> getAllGamesForUser(@PathVariable("userId") UUID userId, Pageable pageable) {
    return this.pagedResourcesAssembler.toModel(minesweeperService.getAllGamesForUser(userId, pageable),this.minesweeperGameRepresentationModelAssembler);
  }

  @PatchMapping("/cell")
  public GameStatus openCell(@RequestBody(required = true) MinesweeperCellReq minesweeperCellReq) {
    return minesweeperService.openCell(minesweeperCellReq.getGameId(), minesweeperCellReq.getUserId(), minesweeperCellReq.getRow(),
        minesweeperCellReq.getColumn());
  }

  @PatchMapping("/cell/flagged")
  public boolean markCell(@RequestBody(required = true) MinesweeperCellReq minesweeperCellReq) {
    return minesweeperService.markCell(minesweeperCellReq.getGameId(), minesweeperCellReq.getUserId(), minesweeperCellReq.getRow(),
        minesweeperCellReq.getColumn(), minesweeperCellReq.isFlaggedCell());
  }

}
